#!/bin/bash
set -e

KEYSTORE_FILE="../debug.keystore"
if [ ! -f "$KEYSTORE_FILE" ]; then
    keytool -genkey -v -keystore "$KEYSTORE_FILE" -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
    echo "Keystore generated at $KEYSTORE_FILE"
else
    echo "Keystore already exists at $KEYSTORE_FILE"
fi

echo ""
echo "Add the following to your local.properties or gradle.properties (DO NOT COMMIT THEM):"
echo "KEYSTORE_PATH=debug.keystore"
echo "KEYSTORE_PASSWORD=android"
echo "KEY_ALIAS=androiddebugkey"
echo "KEY_PASSWORD=android"
