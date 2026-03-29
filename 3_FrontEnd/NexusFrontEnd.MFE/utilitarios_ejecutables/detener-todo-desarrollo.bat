@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"

cd /d "%PROJECT_ROOT%"

echo Deteniendo ecosistema NexusFrontEnd.MFE...
echo.

call :killWindow "Nexus Shell (4200)"
call :killWindow "Nexus Auth (4201)"
call :killWindow "Nexus Dashboard (4202)"
call :killWindow "Nexus Categories (4203)"
call :killWindow "Nexus Users (4204)"
call :killWindow "Nexus Products (4205)"
call :killWindow "Nexus Revenues (4206)"

echo.
echo Proceso de cierre finalizado.
timeout /t 2 >nul
endlocal
exit /b 0

:killWindow
set "WINDOW_TITLE=%~1"
echo Cerrando %WINDOW_TITLE% ...
taskkill /FI "WINDOWTITLE eq %WINDOW_TITLE%" /T /F >nul 2>&1
if errorlevel 1 (
  echo   No se encontro una ventana activa con ese titulo.
) else (
  echo   Cerrada correctamente.
)
exit /b 0
