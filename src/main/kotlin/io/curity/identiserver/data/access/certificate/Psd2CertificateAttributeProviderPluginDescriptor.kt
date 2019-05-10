package io.curity.identiserver.data.access.certificate

import se.curity.identityserver.sdk.plugin.descriptor.DataAccessProviderPluginDescriptor

class Psd2CertificateAttributeProviderPluginDescriptor :
        DataAccessProviderPluginDescriptor<Psd2CertificateAttributeProviderConfiguration> {

    override fun getPluginImplementationType(): String {
        return Psd2CertificateAttributeProvider::class.java.typeName
    }

    override fun getConfigurationType(): Class<Psd2CertificateAttributeProviderConfiguration> {
        return Psd2CertificateAttributeProviderConfiguration::class.java
    }
}
