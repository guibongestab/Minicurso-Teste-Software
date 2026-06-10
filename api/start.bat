@echo off
SET ROOT=%~dp0

echo Iniciando backend (Spring Boot) em http://localhost:8080...
start "Backend" cmd /k "cd /d %ROOT%backend && mvnw.cmd spring-boot:run"

echo Iniciando frontend em http://localhost:3000...
start "Frontend" cmd /k "cd /d %ROOT%frontend && node server.js"

echo.
echo Tudo rodando!
echo   Frontend  -^> http://localhost:3000
echo   Backend   -^> http://localhost:8080
echo   H2 Console -^> http://localhost:8080/h2-console
echo.
echo Feche as janelas de terminal para encerrar.
