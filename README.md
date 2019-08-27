# PSD2 Certificate Attribute provider #

This plugin parses a certificate, and return the PSD2 roles and organizationIdentifier as attributes

NOTE:
Since version 4.2.0 Curity contains a claims provider for parsing the client certificate, rendering this plugin obsolete.

## Build plugin
Then, build the plugin with:
`./gradlew dist`

This will create a folder `dist`, that contains the full plugin group that can be copied to the Curity plugin folder.
