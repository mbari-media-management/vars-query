@ECHO OFF

:: Build script used internally at MBARI
SET JPACKAGE_HOME="%USERPROVILE%/Desktop/jdk-14
CALL "%USERPROVILE%/workspace/m3-deployspace/vars-query/env-config.bat"
gradle jpackage --info
