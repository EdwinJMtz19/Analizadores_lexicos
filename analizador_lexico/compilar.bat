@echo off
echo Verificando directorios...
set PROJECT_DIR=C:\Users\Edwin\Documents\NetBeansProjects\analizador_lexico
set SRC_DIR=%PROJECT_DIR%\src\main\java
set LIB_DIR=%PROJECT_DIR%\lib
set TARGET_DIR=%PROJECT_DIR%\target\classes

if not exist %LIB_DIR% mkdir %LIB_DIR%
if not exist %TARGET_DIR% mkdir %TARGET_DIR%

echo Verificando JFlex...
if not exist %LIB_DIR%\jflex-full-1.8.2.jar (
  echo ERROR: No se encuentra JFlex en %LIB_DIR%\jflex-full-1.8.2.jar
  echo Por favor, descarga JFlex desde https://jflex.de/download.html
  echo y coloca el archivo jar en la carpeta %LIB_DIR%\
  pause
  exit /b 1
)

echo Generando analizador léxico con JFlex...
java -jar %LIB_DIR%\jflex-full-1.8.2.jar -d %SRC_DIR%\analizador_lexico %SRC_DIR%\analizador_lexico\Robot.flex

echo Compilando archivos Java...
javac -d %TARGET_DIR% -cp %LIB_DIR%\jflex-full-1.8.2.jar %SRC_DIR%\analizador_lexico\*.java

if %errorlevel% equ 0 (
  echo Compilación completada con éxito.
) else (
  echo Error en la compilación.
)

pause