#!/usr/bin/env bash

# build script used internally at MBARI

export JPACKAGE_HOME="/Library/Java/JavaVirtualMachines/jdk-14.jdk/Contents/Home"
source "$HOME/workspace/m3-deployspace/vars-query/env-config.sh"
gradle jpackage --info


#/Users/brian/Applications/jdk-14.jdk/Contents/Home/bin/jpackage --type dmg --dest /Users/brian/workspace/vars-query/build/jpackage --name "VARS Query" --module-path /Users/brian/workspace/vars-query/build/jlinkbase/jlinkjars --module org.mbari.vars.query/org.mbari.m3.vars.query.App --app-version 0.1.1 --runtime-image /Users/brian/workspace/vars-query/build/image --java-options -Xms1g --java-options -DANNOSAURUS_JDBC_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver --java-options -DANNOSAURUS_JDBC_PASSWORD=guest --java-options -DANNOSAURUS_JDBC_URL="jdbc:sqlserver://perseus.shore.mbari.org:1433;databaseName=M3_ANNOTATIONS" --java-options -DANNOSAURUS_JDBC_USER=everyone --java-options -DCONCEPT_SERVICE_TIMEOUT=5seconds --java-options -DCONCEPT_SERVICE_URL="http:/m3.shore.mbari.org/kb/v1" --java-options -DSHARKTOPODA_PORT=8800 --java-options -DVARS_QUERY_ANNOTATION_START_DATE=1982-01-01T00:00:00Z --java-options -DVARS_QUERY_FRAME_TITLE="VARS Query"