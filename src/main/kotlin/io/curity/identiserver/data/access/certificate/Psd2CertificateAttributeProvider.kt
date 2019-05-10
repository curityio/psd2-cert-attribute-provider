package io.curity.identiserver.data.access.certificate

import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.DERUTF8String
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.qualified.QCStatement
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.curity.identityserver.sdk.attribute.AttributeTableView
import se.curity.identityserver.sdk.datasource.AttributeDataAccessProvider
import se.curity.identityserver.sdk.errors.ErrorCode.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class Psd2CertificateAttributeProvider(private val configuration: Psd2CertificateAttributeProviderConfiguration)
    : AttributeDataAccessProvider {

    companion object {
        private val certificateFactory: CertificateFactory = CertificateFactory.getInstance("X.509")
        private val logger: Logger = LoggerFactory.getLogger(Psd2CertificateAttributeProvider::class.java)
    }

    private val exceptionFactory = configuration.getExceptionFactory()

    override fun getAttributes(subject: String): AttributeTableView =
            throw exceptionFactory.internalServerException(INVALID_INPUT,
                    "The plugin requires the use of the full subjectAttributes.")


    override fun getAttributes(subjectMap: MutableMap<*, *>): AttributeTableView {

        val pemString = subjectMap[configuration.getCertificateSubjectAttributeName()]
        if (pemString == null || pemString !is String) {
            logger.debug("Missing certificate in attribute " +
                    "${configuration.getCertificateSubjectAttributeName()}. Subject attributes: $subjectMap")
            throw exceptionFactory.internalServerException(INVALID_INPUT, "Missing certificate")
        }

        val certificate = certificateFactory.generateCertificate(pemString.byteInputStream()) as X509Certificate

        val roleList = getRoleNameList(certificate)
        val orgIdentifier = getOrganizationIdentifier(certificate)

        return AttributeTableView.of(listOf(mapOf("roles" to roleList, "organizationIdentifier" to orgIdentifier)))
    }

    private fun getOrganizationIdentifier(certificate: X509Certificate): String {
        val name = X500Name(certificate.subjectDN.name)
        for (type in name.attributeTypes) {
            if (type.id == "2.5.4.97") {
                val rdn = name.getRDNs(type).first()
                return rdn.first.value.toASN1Primitive().toString()
            }
        }
        throw exceptionFactory.unauthorizedException(ACCESS_DENIED,
                "Invalid Certificate")
    }

    private fun getRoleNameList(certificate: X509Certificate): List<String> {
        val roleNameList = ArrayList<String>()

        val qcStatement = certificate.getExtensionValue("1.3.6.1.5.5.7.1.3")
        if (qcStatement != null) {
            val content = ASN1InputStream(qcStatement).use {
                val derOctetString = it.readObject() as DEROctetString
                derOctetString.octets
            }

            val qcSequence = ASN1InputStream(content).use {
                it.readObject() as ASN1Sequence
            }

            // Sequence of QCStatement
            for (ii in 0 until qcSequence.size()) {
                val statement = QCStatement.getInstance(qcSequence.getObjectAt(ii))

                // Is PSD2 QCStatement
                if ("0.4.0.19495.2" == statement.statementId.id) {
                    try {
                        val psd2QcType = statement.statementInfo as ASN1Sequence
                        val roles = psd2QcType.getObjectAt(0) as ASN1Sequence
                        for (j in 0 until roles.size()) {
                            val rolePair = roles.getObjectAt(j) as ASN1Sequence
                            if (rolePair.size() != 2) {
                                logger.info("Unexpected size of psd2 role pair: ${rolePair.size()}.")
                                continue
                            }
                            val roleName = rolePair.getObjectAt(1) as DERUTF8String
                            roleNameList.add(roleName.string)
                        }
                    } catch (e: ClassCastException) {
                        logger.info("Could not parse QCStatement. Found unexpected type ${e.message}")
                        throw exceptionFactory.internalServerException(GENERIC_ERROR, "Could not parse QCStatement")
                    }
                }
            }
        }

        return roleNameList
    }
}