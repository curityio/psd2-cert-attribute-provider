package io.curity.identiserver.data.access.certificate

import se.curity.identityserver.sdk.config.Configuration
import se.curity.identityserver.sdk.config.annotation.DefaultString
import se.curity.identityserver.sdk.config.annotation.Description
import se.curity.identityserver.sdk.service.ExceptionFactory


interface Psd2CertificateAttributeProviderConfiguration : Configuration {
    override fun id(): String = "Psd2CertificateAttributeProvider"

    @Description("The certificate is sent through the subject attributes. Specify in which attribute")
    @DefaultString("x5c")
    fun getCertificateSubjectAttributeName(): String

    fun getExceptionFactory(): ExceptionFactory
}
