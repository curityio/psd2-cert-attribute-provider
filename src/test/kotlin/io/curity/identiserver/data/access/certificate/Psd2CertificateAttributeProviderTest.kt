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

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Assert.assertThat
import org.junit.Test
import se.curity.identityserver.sdk.errors.ErrorCode
import se.curity.identityserver.sdk.http.RedirectStatusCode
import se.curity.identityserver.sdk.service.ExceptionFactory
import se.curity.identityserver.sdk.web.alerts.ErrorMessage
import java.net.URI

class Psd2CertificateAttributeProviderTest {
    companion object {
        val pemString = """-----BEGIN CERTIFICATE-----
MIIFEzCCA/ugAwIBAgIKftbOHZm5yw3zvzANBgkqhkiG9w0BAQsFADBIMQswCQYD
VQQGEwJERTEMMAoGA1UECgwDQkRSMREwDwYDVQQLDAhJVCAtIERldjEYMBYGA1UE
AwwPUFNEMiBUZXN0IFN1YkNBMB4XDTE5MDEyMTEyMDE1NFoXDTE5MDcyMDEyMDE1
NFowggFdMS4wLAYDVQQGEyVGSSAtIHVzYWdlIHN0cmljdGx5IGxpbWl0ZWQgdG8g
RVUvRUVBMUgwRgYDVQQKDD9Ob3JkZWEgIC0gdXNhZ2UgbGltaXRlZCB0byBOb3Jk
ZWEgT3BlbiBCYW5raW5nIERldmVsb3BlciBQb3J0YWwxEjAQBgNVBAsMCU5vcmRl
YSBJVDERMA8GA1UEBwwISGVsc2lua2kxETAPBgNVBAgMCEhlbHNpbmtpMRowGAYD
VQQJDBFTYXRhbWFyYWRhbmthdHUgNTFWMFQGA1UEAwxNTm9yZGVhIE9wZW4gQmFu
a2luZyBEZXZlbG9wZXIgIC0gY2VydGlmaWNhdGUgcHJvdmlkZWQgYnkgQnVuZGVz
ZHJ1Y2tlcmVpIEdtYkgxGTAXBgNVBGETEFBTREZJLUZTQS0xMTIyMzMxGDAWBgNV
BBETD0ZJLTAwMDIwIE5vcmRlYTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoC
ggEBALtcXAStflv0LeZVG49jExx+8jieWm7VaBIFM/ca/Fjp3SuOfUkGWXi9EkkP
UeloxXXc2mKldbsDZJGK9EKyNCkGvSgJ+z8Vuw5u6mIZM64k/AHXea3dC5ufTnVu
2gRwmfb6JDXQk7IkMEif50lpKZdV2FZibssKgui9Bqix2Sin0n2N9RUJIPrJdG/x
YSUPh4x/mK+M9R+18c0Om8tDbLIiMzf9wN8PhsRLNF27OviFaJl2V01e6250SPBv
qbTswdYobcnx6bhTracuOsiOEwmrJ563yi99lF0xXG6Bd0dRFJ19ZB35gpVmTCfl
P0MOYf31FpcadHzEkS1pIiMtvmsCAwEAAaOB5zCB5DB+BggrBgEFBQcBAwRyMHAw
bgYGBACBmCcCMGQwOTARBgcEAIGYJwEDDAZQU1BfQUkwEQYHBACBmCcBBAwGUFNQ
X0lDMBEGBwQAgZgnAQIMBlBTUF9QSQwfRmluYW5jaWFsIFN1cGVydmlzb3J5IEF1
dGhvcml0eQwGRkktRlNBMAsGA1UdDwQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcD
AQYIKwYBBQUHAwIwFQYDVR0RBA4wDIIKbm9yZGVhLmNvbTAfBgNVHSMEGDAWgBRc
zgCARJu0XDNe5JrFOPI4CIgFojANBgkqhkiG9w0BAQsFAAOCAQEAP1baeYRsG5AM
TUHyn2g6cxPchofMlLKwtoJSSqpmqwSg8kEXBWWv5zJD1GgMn1z4GpeeaEpTGVgw
K5coIql2/xQZUfSPSMDRB0Baoo38rZeQCba6Iqv1AOO+PlU4LIULlryTz0DvkCMX
VtdZguTzct+9FV+I0882Rpee0/YSTPFkA+pRTDWB673Emxn29IlXUcq+vAHNd8yN
SC+uERjPCDksRMC5bib0XbtxkLBEoFZhAcs/obv+NX9vro0NRb1HM1W8vczf5vVF
GaixkUK8Z+rSpLwU4cBhGPu574JlzNcHHTVmHc1mep3e2+7yiGVWEbijieTp2jNh
5Q726ENy1g==
-----END CERTIFICATE-----"""
    }

    @Test
    fun getAttributes() {
        val conf = StubConf()
        val dap = Psd2CertificateAttributeProvider(conf)

        val treeView = dap.getAttributes(mutableMapOf("subject" to "teddie", "x5c" to pemString))
        val roles: List<*> = treeView.getRow(0)["roles"] as List<*>
        val orgId = treeView.getRow(0)["organizationIdentifier"] as String

        assertThat(roles, hasItems("PSP_AI" as Any, "PSP_IC", "PSP_PI"))
        assertThat(orgId, CoreMatchers.`is`("PSDFI-FSA-112233"))
    }
}

class StubConf : Psd2CertificateAttributeProviderConfiguration {
    override fun getCertificateSubjectAttributeName(): String = "x5c"

    override fun getExceptionFactory(): ExceptionFactory {
        fun assertionError(): java.lang.RuntimeException {
            throw RuntimeException("Should not be called")
        }
        return object : ExceptionFactory {
            override fun unauthorizedException(p0: ErrorCode?, p1: String?, p2: String?, p3: String?) = throw assertionError()

            override fun notFoundException(p0: ErrorCode?, p1: String?) = throw assertionError()

            override fun contentTypeNotAcceptable(p0: MutableCollection<String>?) = throw assertionError()

            override fun requestValidationException(p0: MutableSet<ErrorMessage>?) = throw assertionError()

            override fun methodNotAllowed() = throw assertionError()

            override fun methodNotAllowed(p0: String?) = throw assertionError()

            override fun externalServiceException(p0: String?) = throw assertionError()

            override fun forbiddenException(p0: ErrorCode?) = throw assertionError()

            override fun forbiddenException(p0: ErrorCode?, p1: String?) = throw assertionError()

            override fun templateNotFoundException(p0: String?) = throw assertionError()

            override fun unauthorizedException(p0: ErrorCode?) = throw assertionError()

            override fun unauthorizedException(p0: ErrorCode?, p1: String?) = throw assertionError()

            override fun unauthorizedException(p0: ErrorCode?, p1: String?, p2: String?) = throw assertionError()

            override fun badRequestException(p0: ErrorCode?) = throw assertionError()

            override fun badRequestException(p0: ErrorCode?, p1: String?) = throw assertionError()

            override fun redirectException(p0: String?) = throw assertionError()

            override fun redirectException(p0: String?, p1: RedirectStatusCode?) = throw assertionError()

            override fun redirectException(p0: String?, p1: RedirectStatusCode?, p2: MutableMap<String, MutableCollection<String>>?, p3: Boolean) = throw assertionError()

            override fun redirectException(p0: URI?, p1: RedirectStatusCode?, p2: MutableMap<String, MutableCollection<String>>?, p3: Boolean) = throw assertionError()

            override fun configurationException(p0: String?) = throw assertionError()

            override fun internalServerException(p0: ErrorCode?) = throw assertionError()

            override fun internalServerException(p0: ErrorCode?, p1: String?) = throw assertionError()

            override fun serviceUnavailable() = throw assertionError()

            override fun notFoundException() = throw assertionError()
        }
    }

}
