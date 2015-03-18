@echo off

set "jver=0"
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k%%l%%m"
if %jver% LSS 19000 GOTO NOJAVA

java.exe -jar mazeSolverGui.jar
GOTO EXIT


:NOJAVA
echo ERROR: Can't find Java 8 enviroment!
echo You have installed:
java -version
pause

:EXIT

