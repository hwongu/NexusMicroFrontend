@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"

cd /d "%PROJECT_ROOT%"

echo Instalando dependencias...
call npm install
if errorlevel 1 (
    echo Error al ejecutar npm install
    pause
    exit /b 1
)

call :run_build "Shell" "build:shell"
call :run_build "Auth" "build:auth"
call :run_build "Dashboard" "build:dashboard"
call :run_build "Categories" "build:categories"
call :run_build "Users" "build:users"
call :run_build "Products" "build:products"
call :run_build "Revenues" "build:revenues"

echo.
echo Proceso completado correctamente para shell y todos los micro frontends.
pause
exit /b 0

:run_build
set "TARGET_LABEL=%~1"
set "TARGET_SCRIPT=%~2"
echo.
echo Compilando %TARGET_LABEL%...
call npm run %TARGET_SCRIPT%
if errorlevel 1 (
    echo Error al ejecutar npm run %TARGET_SCRIPT%
    pause
    exit /b 1
)
exit /b 0
