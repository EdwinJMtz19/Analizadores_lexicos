package analizador_lexico;
public class Token {
    private String token;
    private Tipos tipo;
    private String valor;
      
    public String gettoken() {
        return token;
    }
    public void settoken(String tok) {
        token=tok;
    }
    
    public Tipos getTipo() {
        return tipo;
    }

    public void setTipo(Tipos tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

   

    enum Tipos {
       
        IDENTIFICADOR("^([a-z][0-9])$"),
        METODO("^(velocidad|cuerpo|base|garra)$"),
        ACCION("^(iniciar|finalizar|cerrargarra|abrirgarra)$"),
        SIMBOLO("[=.]"),
        NUMERO("^([0-9]|([1-9][0-9])|([1-2][0-9][0-9])|(3[0-5][0-9])|360)$");
    public final String patron;
        Tipos(String s) {
            this.patron = s;
        }
    }

}