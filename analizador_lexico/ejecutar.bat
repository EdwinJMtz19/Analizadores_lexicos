@echo off
echo Ejecutando el analizador léxico de robot...

set PROJECT_DIR=C:\Users\Edwin\Documents\NetBeansProjects\analizador_lexico
set TARGET_DIR=%PROJECT_DIR%\target\classes
set LIB_DIR=%PROJECT_DIR%\lib

java -cp "%TARGET_DIR%;%LIB_DIR%\jflex-full-1.8.2.jar" analizador_lexico.Main

echo.
echo Ejecución finalizada.
pause