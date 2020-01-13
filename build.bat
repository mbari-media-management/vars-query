@ECHO OFF

:: Build script used internally at MBARI
SET JPACKAGE_HOME="C:\Users\brian\Desktop\jdk-14"
CALL "Z:/workspace/m3-deployspace/vars-query/env-config.bat"
gradle jpackage --info
