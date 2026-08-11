@echo off
setlocal
set SCRIPT_DIR=%~dp0
set MAVEN_BIN=%SCRIPT_DIR%mvn_dist\apache-maven-3.9.9\bin\mvn.cmd
if not exist "%MAVEN_BIN%" (
  echo Unable to find Maven at %MAVEN_BIN%
  exit /b 1
)
call "%MAVEN_BIN%" %*
exit /b %ERRORLEVEL%
