#!/usr/bin/env bash

# build script used internally at MBARI

export JPACKAGE_HOME="/Library/Java/JavaVirtualMachines/adoptopenjdk-14.jdk/Contents/Home"
source "$HOME/workspace/m3-deployspace/vars-query/env-config-public.sh"
gradlew clean jpackage --info
