@echo off
setlocal enabledelayedexpansion

REM Scala Lexer Build Script

set SCALA_HOME=D:\scala
set SBT_HOME=D:\.sbt_build
set SBT_USER_HOME=D:\.sbt_build
set JAVA_HOME=

REM Create output directories
if not exist "target\classes" mkdir target\classes
if not exist "target\scala-3.3.1" mkdir target\scala-3.3.1

echo.
echo ================================
echo Scala Lexer Build Script
echo ================================
echo.
echo This project requires SBT to compile.
echo Due to compatibility issues, please compile using:
echo.
echo   cd D:\APCS\Lexer
echo   sbt compile
echo.
echo Or use this direct SBT path:
echo   D:\sbt\bin\sbt.bat compile
echo.
echo Scala files are located in:
echo   src\main\scala\lexer\
echo.
echo Available files:
echo   - CharacterStream.scala
echo   - Diagnostic.scala
echo   - Lexer.scala
echo   - Position.scala
echo   - Span.scala
echo   - Token.scala
echo   - TokenType.scala
echo.
echo ================================
echo.

endlocal
