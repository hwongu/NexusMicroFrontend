@echo off
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"

cd /d "%PROJECT_ROOT%"

set "ENV_FILE=src\environments\environment.prod.ts"
set "PROD_MANIFEST=public\federation.manifest.prod.json"
set "BASE_HREF=/NexusFrontEnd/"

set "DIST_SHELL=dist\NexusFrontEnd\browser"
set "DIST_AUTH=dist\mfe-auth\browser"
set "DIST_DASHBOARD=dist\mfe-dashboard\browser"
set "DIST_CATEGORIES=dist\mfe-categories\browser"
set "DIST_USERS=dist\mfe-users\browser"
set "DIST_PRODUCTS=dist\mfe-products\browser"
set "DIST_REVENUES=dist\mfe-revenues\browser"

set "DEPLOY_ROOT=dist\frontend"
set "DEPLOY_SHELL=%DEPLOY_ROOT%\NexusFrontEnd"
set "DEPLOY_AUTH=%DEPLOY_ROOT%\mfe-auth"
set "DEPLOY_DASHBOARD=%DEPLOY_ROOT%\mfe-dashboard"
set "DEPLOY_CATEGORIES=%DEPLOY_ROOT%\mfe-categories"
set "DEPLOY_USERS=%DEPLOY_ROOT%\mfe-users"
set "DEPLOY_PRODUCTS=%DEPLOY_ROOT%\mfe-products"
set "DEPLOY_REVENUES=%DEPLOY_ROOT%\mfe-revenues"

if not exist "%ENV_FILE%" (
    echo No se encontro el archivo %ENV_FILE%.
    pause
    exit /b 1
)

if not exist "%PROD_MANIFEST%" (
    echo No se encontro el archivo %PROD_MANIFEST%.
    pause
    exit /b 1
)

for /f "tokens=1,* delims=:" %%A in ('findstr /c:"production:" "%ENV_FILE%"') do set "PRODUCTION_FLAG=%%B"
for /f "tokens=1,* delims=:" %%A in ('findstr /c:"apiBaseUrl:" "%ENV_FILE%"') do set "API_BASE_URL=%%B"
for /f "tokens=1,* delims=:" %%A in ('findstr /c:"apiPrefix:" "%ENV_FILE%"') do set "API_PREFIX=%%B"

set "PRODUCTION_FLAG=%PRODUCTION_FLAG: =%"
set "PRODUCTION_FLAG=%PRODUCTION_FLAG:,=%"

set "API_BASE_URL=%API_BASE_URL: =%"
set "API_BASE_URL=%API_BASE_URL:'=%"
set "API_BASE_URL=%API_BASE_URL:,=%"

set "API_PREFIX=%API_PREFIX: =%"
set "API_PREFIX=%API_PREFIX:'=%"
set "API_PREFIX=%API_PREFIX:,=%"

if not defined API_BASE_URL (
    echo No se pudo obtener apiBaseUrl desde %ENV_FILE%.
    pause
    exit /b 1
)

if not defined API_PREFIX (
    echo No se pudo obtener apiPrefix desde %ENV_FILE%.
    pause
    exit /b 1
)

if /I not "%PRODUCTION_FLAG%"=="true" (
    echo El archivo %ENV_FILE% no tiene production: true.
    pause
    exit /b 1
)

set "FULL_API_URL=%API_BASE_URL%%API_PREFIX%"

echo ==================================================
echo Generando artefactos de produccion para NexusFrontEnd.MFE
echo Environment: %ENV_FILE%
echo production: %PRODUCTION_FLAG%
echo apiBaseUrl: %API_BASE_URL%
echo apiPrefix : %API_PREFIX%
echo apiUrl    : %FULL_API_URL%
echo baseHref  : %BASE_HREF%
echo manifest  : %PROD_MANIFEST%
echo ==================================================
echo.

echo [0/8] Limpiando carpeta dist...
if exist "dist" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "if (Test-Path -LiteralPath '%CD%\dist') { Remove-Item -Recurse -Force -LiteralPath '%CD%\dist' }"
    if errorlevel 1 goto :build_error
)

echo [1/8] Compilando shell...
call npx ng build angular-standalone-project --configuration production --base-href %BASE_HREF%
if errorlevel 1 goto :build_error

echo [2/8] Compilando mfe-auth...
call npx ng build mfe-auth --configuration production
if errorlevel 1 goto :build_error

echo [3/8] Compilando mfe-dashboard...
call npx ng build mfe-dashboard --configuration production
if errorlevel 1 goto :build_error

echo [4/8] Compilando mfe-categories...
call npx ng build mfe-categories --configuration production
if errorlevel 1 goto :build_error

echo [5/8] Compilando mfe-users...
call npx ng build mfe-users --configuration production
if errorlevel 1 goto :build_error

echo [6/8] Compilando mfe-products...
call npx ng build mfe-products --configuration production
if errorlevel 1 goto :build_error

echo [7/8] Compilando mfe-revenues...
call npx ng build mfe-revenues --configuration production
if errorlevel 1 goto :build_error

echo [8/8] Preparando carpeta frontend para Docker/Nginx dentro de dist...
if exist "%DEPLOY_ROOT%" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "if (Test-Path -LiteralPath '%CD%\%DEPLOY_ROOT%') { Remove-Item -Recurse -Force -LiteralPath '%CD%\%DEPLOY_ROOT%' }"
    if errorlevel 1 goto :deploy_error
)

mkdir "%DEPLOY_SHELL%" || goto :deploy_error
mkdir "%DEPLOY_AUTH%" || goto :deploy_error
mkdir "%DEPLOY_DASHBOARD%" || goto :deploy_error
mkdir "%DEPLOY_CATEGORIES%" || goto :deploy_error
mkdir "%DEPLOY_USERS%" || goto :deploy_error
mkdir "%DEPLOY_PRODUCTS%" || goto :deploy_error
mkdir "%DEPLOY_REVENUES%" || goto :deploy_error

echo.
echo Copiando artefactos compilados...
xcopy "%DIST_SHELL%\*" "%DEPLOY_SHELL%\" /E /I /Y >nul || goto :deploy_error
xcopy "%DIST_AUTH%\*" "%DEPLOY_AUTH%\" /E /I /Y >nul || goto :deploy_error
xcopy "%DIST_DASHBOARD%\*" "%DEPLOY_DASHBOARD%\" /E /I /Y >nul || goto :deploy_error
xcopy "%DIST_CATEGORIES%\*" "%DEPLOY_CATEGORIES%\" /E /I /Y >nul || goto :deploy_error
xcopy "%DIST_USERS%\*" "%DEPLOY_USERS%\" /E /I /Y >nul || goto :deploy_error
xcopy "%DIST_PRODUCTS%\*" "%DEPLOY_PRODUCTS%\" /E /I /Y >nul || goto :deploy_error
xcopy "%DIST_REVENUES%\*" "%DEPLOY_REVENUES%\" /E /I /Y >nul || goto :deploy_error

echo.
echo Aplicando manifest de produccion al shell...
copy /Y "%PROD_MANIFEST%" "%DEPLOY_SHELL%\federation.manifest.json" >nul || goto :deploy_error

echo.
echo Verificando estructura final para Docker/Nginx...
if not exist "%DEPLOY_SHELL%\index.html" (
    echo Falta %DEPLOY_SHELL%\index.html
    goto :deploy_error
)

if not exist "%DEPLOY_SHELL%\federation.manifest.json" (
    echo Falta %DEPLOY_SHELL%\federation.manifest.json
    goto :deploy_error
)

if not exist "%DEPLOY_AUTH%\remoteEntry.json" (
    echo Falta %DEPLOY_AUTH%\remoteEntry.json
    goto :deploy_error
)

if not exist "%DEPLOY_DASHBOARD%\remoteEntry.json" (
    echo Falta %DEPLOY_DASHBOARD%\remoteEntry.json
    goto :deploy_error
)

if not exist "%DEPLOY_CATEGORIES%\remoteEntry.json" (
    echo Falta %DEPLOY_CATEGORIES%\remoteEntry.json
    goto :deploy_error
)

if not exist "%DEPLOY_USERS%\remoteEntry.json" (
    echo Falta %DEPLOY_USERS%\remoteEntry.json
    goto :deploy_error
)

if not exist "%DEPLOY_PRODUCTS%\remoteEntry.json" (
    echo Falta %DEPLOY_PRODUCTS%\remoteEntry.json
    goto :deploy_error
)

if not exist "%DEPLOY_REVENUES%\remoteEntry.json" (
    echo Falta %DEPLOY_REVENUES%\remoteEntry.json
    goto :deploy_error
)

echo.
echo Despliegue de produccion preparado correctamente.
echo.
echo Carpeta final para Docker:
echo - %DEPLOY_SHELL%
echo - %DEPLOY_AUTH%
echo - %DEPLOY_DASHBOARD%
echo - %DEPLOY_CATEGORIES%
echo - %DEPLOY_USERS%
echo - %DEPLOY_PRODUCTS%
echo - %DEPLOY_REVENUES%
echo.
echo Archivos clave verificados:
echo - %DEPLOY_SHELL%\index.html
echo - %DEPLOY_SHELL%\federation.manifest.json
echo - %DEPLOY_AUTH%\remoteEntry.json
echo - %DEPLOY_DASHBOARD%\remoteEntry.json
echo - %DEPLOY_CATEGORIES%\remoteEntry.json
echo - %DEPLOY_USERS%\remoteEntry.json
echo - %DEPLOY_PRODUCTS%\remoteEntry.json
echo - %DEPLOY_REVENUES%\remoteEntry.json
echo.
echo Backend configurado:
echo - %FULL_API_URL%
echo.
echo Manifest aplicado al shell:
echo - %DEPLOY_SHELL%\federation.manifest.json
echo.
echo Carpeta que debes copiar:
echo - %DEPLOY_ROOT%
echo.
echo Luego puedes levantar tu infraestructura con Docker Compose.
pause
exit /b 0

:build_error
echo.
echo Error al generar los builds de produccion.
pause
exit /b 1

:deploy_error
echo.
echo Error al preparar la carpeta frontend para despliegue.
pause
exit /b 1
