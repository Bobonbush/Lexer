@echo off
REM SBT Launcher for Lexer Project

setlocal
set SBT_HOME=D:\.sbt_temp
set SBT_USER_HOME=D:\.sbt_temp
if not exist "%SBT_HOME%" mkdir "%SBT_HOME%"

D:\sbt\bin\sbt.bat %*
endlocal
