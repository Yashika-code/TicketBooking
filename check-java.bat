@echo off
echo ========================================
echo Java Installation Checker
echo ========================================
echo.

echo Checking Java installation...
echo.

echo 1. Checking java command:
java -version
if %errorlevel% neq 0 (
    echo [ERROR] Java is NOT installed or not in PATH
    echo.
    echo Please install Java from: https://adoptium.net/
    echo.
    goto :end
) else (
    echo [OK] Java is installed
)

echo.
echo 2. Checking JAVA_HOME:
if "%JAVA_HOME%"=="" (
    echo [ERROR] JAVA_HOME is NOT set
    echo.
    echo You need to set JAVA_HOME environment variable.
    echo See JAVA_SETUP.md for instructions.
    echo.
) else (
    echo [OK] JAVA_HOME is set to: %JAVA_HOME%
)

echo.
echo 3. Checking JAVA_HOME path exists:
if exist "%JAVA_HOME%\bin\java.exe" (
    echo [OK] JAVA_HOME path is valid
) else (
    echo [ERROR] JAVA_HOME path is invalid or Java is not there
    echo Current JAVA_HOME: %JAVA_HOME%
)

echo.
echo ========================================
echo Summary:
echo ========================================
if "%JAVA_HOME%"=="" (
    echo Status: NOT READY - Need to set JAVA_HOME
    echo.
    echo Quick Fix:
    echo 1. Find where Java is installed
    echo 2. Set JAVA_HOME environment variable
    echo 3. See JAVA_SETUP.md for detailed steps
) else (
    if exist "%JAVA_HOME%\bin\java.exe" (
        echo Status: READY - You can run the backend!
        echo.
        echo Next step: Run start-backend.bat
    ) else (
        echo Status: NOT READY - JAVA_HOME path is wrong
    )
)

:end
echo.
pause
