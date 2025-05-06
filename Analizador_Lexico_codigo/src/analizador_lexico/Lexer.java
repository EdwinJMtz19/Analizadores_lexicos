package analizador_lexico;

import analizador_lexico.Token.Tipos;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    
    private static ArrayList<Token> lex(String input) {
        final ArrayList<Token> tokens = new ArrayList<Token>();
        System.out.println("\nCADENA: "+input);
        
        
        
        String patronAccionE = ("([a-z][0-9]).(iniciar|finalizar|cerrargarra|abrirgarra)=[0-9]+"); 
        String patronAccion = ("([a-z][0-9]).[A-Za-z]+"); 
        String patronMetodoE = ("([a-z][0-9]).(cuerpo|base|garra)"); 
        String patronMetodo = ("([a-z][0-9]).[A-Za-z]+=[0-9]+"); 
        
        
         Pattern patronA = Pattern.compile(patronAccion);
         Pattern patronM = Pattern.compile(patronMetodo);
         Pattern patronAE = Pattern.compile(patronAccionE);
         Pattern patronME = Pattern.compile(patronMetodoE);
         
         
         Matcher match1=patronA.matcher(input);
         Matcher match2=patronM.matcher(input);
         Matcher match3=patronAE.matcher(input);
         Matcher match4=patronME.matcher(input);
         
        if(match3.matches()||match4.matches()) {
           System.out.println("Error de sintaxis\n");
           System.out.println("TOKEN           VALOR");
           String []partes=input.split("(?=[.=])|(?<=[.=])");
            for (String parte : partes) {
                Agrega(tokens,parte);
            }
        }else if(match2.matches()||match1.matches()){
            System.out.println("TOKEN           VALOR");
           String []partes=input.split("(?=[.=])|(?<=[.=])");
            for (String parte : partes) {
                Agrega(tokens,parte);
            }
        
        }
        else{
            System.out.println("La expresión no tiene formato válido: " + input);
            }
      
        return tokens;
    }
    
    public static void Agrega(ArrayList<Token>usar,String dato){
        boolean matched=false;
    for (Tipos tokenTipo : Tipos.values()) {
                Pattern patron = Pattern.compile(tokenTipo.patron);
                Matcher m = patron.matcher(dato);
               
                if(m.matches()) {
                    Token tk = new Token();
                    tk.setTipo(tokenTipo);
                    tk.setValor(dato);
                    usar.add(tk);
                    matched = true;
                }
            }
     if(!matched) {
                 System.out.println("TOKEN NO RECONOCIDO: "+dato);
                }
    }
    
    
    
    
    
    public static void main(String[] args) {
        
        String [] prueba={
        "r1.velocidad=9","r1.iniciar","r1.cuerpo=320","r1.cuerpos=1","r1.correr","r1.iniciarr=1"     
        };
        for(String input:prueba){
        ArrayList<Token> tokens = lex(input);
        for (Token token : tokens) {
            System.out.println(token.getTipo()+"       "+ token.getValor());
        }
        }
    }


}
