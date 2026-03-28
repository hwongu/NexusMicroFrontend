@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"

cd /d "%PROJECT_ROOT%"

echo Cerrando ventanas activas del ecosistema local...
call :close_window "Nexus Shell (4200)"
call :close_window "Nexus Auth (4201)"
call :close_window "Nexus Dashboard (4202)"
call :close_window "Nexus Categories (4203)"
call :close_window "Nexus Users (4204)"
call :close_window "Nexus Products (4205)"
call :close_window "Nexus Revenues (4206)"

echo.
echo Limpiando cache de Angular...
call npx ng cache clean

echo.
echo Eliminando carpetas generadas de %PROJECT_ROOT% ...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Remove-Item -Recurse -Force 'dist','.angular','coverage','node_modules' -ErrorAction SilentlyContinue"

echo.
echo Eliminando archivos temporales y paquetes...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-ChildItem -Path . -Recurse -Force -File | Where-Object { $_.Name -match '\.zip$|\.tar\.gz$|\.tgz$|\.rar$|\.7z$|\.log$' } | Remove-Item -Force -ErrorAction SilentlyContinue"

echo.
echo Limpieza completada.
pause
exit /b 0

:close_window
set "WINDOW_TITLE=%~1"
echo Intentando cerrar %WINDOW_TITLE% ...
taskkill /FI "WINDOWTITLE eq %WINDOW_TITLE%" /T /F >nul 2>&1
if errorlevel 1 (
    echo   No se encontro una ventana activa con ese titulo.
) else (
    echo   Cerrada correctamente.
)
exit /b 0
