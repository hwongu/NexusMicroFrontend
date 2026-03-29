@echo off
setlocal EnableExtensions

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"

cd /d "%PROJECT_ROOT%"

set "NF_CACHE=node_modules\.cache\native-federation"
set "WAIT_SECONDS=3"

echo Iniciando ecosistema NexusFrontEnd.MFE...
echo.
echo Shell          -^> http://localhost:4200
echo MFE Auth       -^> http://localhost:4201
echo MFE Dashboard  -^> http://localhost:4202
echo MFE Categories -^> http://localhost:4203
echo MFE Users      -^> http://localhost:4204
echo MFE Products   -^> http://localhost:4205
echo MFE Revenues   -^> http://localhost:4206
echo.

if exist "%SCRIPT_DIR%detener-todo-desarrollo.bat" (
    echo Cerrando instancias previas si existen...
    call "%SCRIPT_DIR%detener-todo-desarrollo.bat" >nul 2>&1
    timeout /t 2 >nul
    echo.
)

if exist "%NF_CACHE%" (
    echo Limpiando cache de native federation...
    powershell -NoProfile -ExecutionPolicy Bypass -Command "if (Test-Path -LiteralPath '%CD%\%NF_CACHE%') { Remove-Item -Recurse -Force -LiteralPath '%CD%\%NF_CACHE%' }"
    echo.
)

echo Abriendo ventanas de desarrollo de forma escalonada...
echo Abriendo Nexus Auth (4201)...
start "Nexus Auth (4201)" /D "%PROJECT_ROOT%" cmd /k "title Nexus Auth (4201) && npm run start:auth"
echo Esperando %WAIT_SECONDS% segundos para evitar choques de cache...
timeout /t %WAIT_SECONDS% >nul

echo Abriendo Nexus Dashboard (4202)...
start "Nexus Dashboard (4202)" /D "%PROJECT_ROOT%" cmd /k "title Nexus Dashboard (4202) && npm run start:dashboard"
echo Esperando %WAIT_SECONDS% segundos para evitar choques de cache...
timeout /t %WAIT_SECONDS% >nul

echo Abriendo Nexus Categories (4203)...
start "Nexus Categories (4203)" /D "%PROJECT_ROOT%" cmd /k "title Nexus Categories (4203) && npm run start:categories"
echo Esperando %WAIT_SECONDS% segundos para evitar choques de cache...
timeout /t %WAIT_SECONDS% >nul

echo Abriendo Nexus Users (4204)...
start "Nexus Users (4204)" /D "%PROJECT_ROOT%" cmd /k "title Nexus Users (4204) && npm run start:users"
echo Esperando %WAIT_SECONDS% segundos para evitar choques de cache...
timeout /t %WAIT_SECONDS% >nul

echo Abriendo Nexus Products (4205)...
start "Nexus Products (4205)" /D "%PROJECT_ROOT%" cmd /k "title Nexus Products (4205) && npm run start:products"
echo Esperando %WAIT_SECONDS% segundos para evitar choques de cache...
timeout /t %WAIT_SECONDS% >nul

echo Abriendo Nexus Revenues (4206)...
start "Nexus Revenues (4206)" /D "%PROJECT_ROOT%" cmd /k "title Nexus Revenues (4206) && npm run start:revenues"
echo Esperando %WAIT_SECONDS% segundos para evitar choques de cache...
timeout /t %WAIT_SECONDS% >nul

echo Abriendo Nexus Shell (4200)...
start "Nexus Shell (4200)" /D "%PROJECT_ROOT%" cmd /k "title Nexus Shell (4200) && npm run start:shell"

echo.
echo Todas las ventanas fueron lanzadas.
echo Si algun remote no inicia, revisa su ventana correspondiente.
echo El host mostrara un fallback si un remote no esta disponible.
echo.
echo Sugerencia:
echo - inicia primero los remotes
echo - deja el shell al final para reducir bloqueos de cache de native federation
echo - espera a que las ventanas terminen de compilar antes de abrir la app en el navegador

endlocal
exit /b 0
