/*
 *  Copyright 2019 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
