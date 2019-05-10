# PSD2 Certificate Attribute provider #

This plugin parses a certificate, and return the PSD2 roles and organizationIdentifier as attributes

## Build plugin
First, collect credentials to the Curity Nexus, to be able to fetch the SDK. Create a file called `gradle.properties` in the root of the repository, and put the credentials there.

Then, build the plugin by:
`./gradlew dist`
