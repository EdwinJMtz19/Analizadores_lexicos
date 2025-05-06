/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package analizador_lexico;

/**
 *
 * @author Edwin
 */


import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase principal para el sistema de control de robot
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Inicializando Sistema de Análisis Léxico para Robot...");
        
        try {
            // Intentar usar el look and feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo establecer el look and feel: " + e.getMessage());
        }
        
        // Lanzar la interfaz gráfica
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LexerGUI gui = new LexerGUI();
                gui.setVisible(true);
                System.out.println("Interfaz gráfica iniciada.");
            }
        });
    }
}
