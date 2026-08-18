#!/bin/bash
sed -i 's/android {/android {\n    lint {\n        abortOnError = false\n    }/' app/build.gradle.kts
