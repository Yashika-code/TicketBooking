@echo off
echo ========================================
echo Finding Java Installation
echo ========================================
echo.

echo Locating Java...
where java

echo.
echo If you see a path above, Java is installed.
echo.
echo Common Java locations:
dir "C:\Program Files\Java\" 2>nul
dir "C:\Program Files\Eclipse Adoptium\" 2>nul
dir "C:\Program Files\OpenJDK\" 2>nul
dir "C:\Program Files\Temurin\" 2>nul
dir "C:\Program Files\Amazon Corretto\" 2>nul

echo.
pause
