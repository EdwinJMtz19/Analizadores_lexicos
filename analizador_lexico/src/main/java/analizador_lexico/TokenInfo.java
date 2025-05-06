/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizador_lexico;

/**
 *
 * @author Edwin
 */


/**
 * Clase para almacenar información de los tokens identificados
 */
public class TokenInfo {
    public final String tipo;
    public final String lexema;
    public final int linea;
    public final int columna;
    
    public TokenInfo(String tipo, String lexema, int linea, int columna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
        this.columna = columna;
    }
    
    @Override
    public String toString() {
        return "Token: " + tipo + ", Lexema: '" + lexema + "', Línea: " + linea + ", Columna: " + columna;
    }
}
