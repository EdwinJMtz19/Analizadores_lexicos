/* Especificación para JFlex - Analizador Léxico de Robot */
package analizador_lexico;

import java.util.ArrayList;

%%

%class RobotLexer
%unicode
%line
%column
%public
%standalone

%{
    // Lista para almacenar los tokens encontrados
    public ArrayList<TokenInfo> tokens = new ArrayList<>();
    
    // Método para agregar un token a la lista
    private void addToken(String tipo, String lexema) {
        tokens.add(new TokenInfo(tipo, lexema, yyline + 1, yycolumn + 1));
        System.out.println("Token encontrado: " + tipo + " - '" + lexema + "'");
    }
%}

// Macros para expresiones regulares
Espacio = [ \t\r\n\f]
Digito = [0-9]
Numero = {Digito}+
Letra = [a-zA-Z]
Identificador = {Letra}({Letra}|{Digito})*
Comentario = "//".*

// Palabras reservadas para la creación y control del robot
Robot = "Robot"
Crear = "crear"
Iniciar = "iniciar"
Detener = "detener"

// Componentes del robot
Base = "base"
Cuerpo = "cuerpo"
Garra = "garra"

// Acciones del robot
Girar = "girar"
Subir = "subir"
Bajar = "bajar"
Abrir = "abrir"
Cerrar = "cerrar"

// Parámetros
Velocidad = "velocidad"
Tiempo = "tiempo"
Grados = "grados"

// Símbolos
ParentesisAbre = "("
ParentesisCierra = ")"
PuntoYComa = ";"
Coma = ","
Igual = "="
Punto = "."

%%

// Reglas léxicas

{Robot}             { addToken("ROBOT", yytext()); }
{Crear}             { addToken("CREAR", yytext()); }
{Iniciar}           { addToken("INICIAR", yytext()); }
{Detener}           { addToken("DETENER", yytext()); }

{Base}              { addToken("BASE", yytext()); }
{Cuerpo}            { addToken("CUERPO", yytext()); }
{Garra}             { addToken("GARRA", yytext()); }

{Girar}             { addToken("GIRAR", yytext()); }
{Subir}             { addToken("SUBIR", yytext()); }
{Bajar}             { addToken("BAJAR", yytext()); }
{Abrir}             { addToken("ABRIR", yytext()); }
{Cerrar}            { addToken("CERRAR", yytext()); }

{Velocidad}         { addToken("VELOCIDAD", yytext()); }
{Tiempo}            { addToken("TIEMPO", yytext()); }
{Grados}            { addToken("GRADOS", yytext()); }

{Numero}            { addToken("NUMERO", yytext()); }
{Identificador}     { addToken("IDENTIFICADOR", yytext()); }

{ParentesisAbre}    { addToken("PARENTESIS_ABRE", yytext()); }
{ParentesisCierra}  { addToken("PARENTESIS_CIERRA", yytext()); }
{PuntoYComa}        { addToken("PUNTO_Y_COMA", yytext()); }
{Coma}              { addToken("COMA", yytext()); }
{Igual}             { addToken("IGUAL", yytext()); }
{Punto}             { addToken("PUNTO", yytext()); }

{Espacio}           { /* Ignorar espacios en blanco */ }

// Error: cualquier otro carácter
.                   { addToken("ERROR", yytext()); }
