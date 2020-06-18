@ECHO OFF

:: Build script used internally at MBARI
REM SET JPACKAGE_HOME="C:\Users\brian\Desktop\jdk-14"
REM CALL "Z:/workspace/m3-deployspace/vars-query/env-config.bat"
CALL "%1"
gradlew jpackage --info
