package analizador_lexico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Interfaz gráfica para el analizador léxico de robot con simulador integrado
 */
public class LexerGUI extends JFrame {
    private JTextArea inputTextArea;
    private JTable outputTable;
    private DefaultTableModel tableModel;
    private JButton analyzeButton;
    private JButton simulateButton;
    private JButton clearButton;
    private JButton saveButton;
    private JButton loadButton;
    
    // Referencia al simulador
    private RobotSimulator simulator;
    // Lista de tokens analizados más reciente
    private ArrayList<TokenInfo> lastAnalyzedTokens;
    
    public LexerGUI() {
        setTitle("Analizador Léxico para Robot");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Crear el panel principal con división
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        
        // Panel superior para entrada
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Código de Control del Robot"));
        
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputTextArea.setText("// Código para el robot\n" +
                              "Robot r1 = crear();\n" +
                              "r1.iniciar();\n\n" +
                              "// Mover base\n" +
                              "r1.base.girar(45, velocidad=2, tiempo=3);\n\n" +
                              "// Mover cuerpo\n" +
                              "r1.cuerpo.subir(30);\n\n" +
                              "// Usar la garra\n" +
                              "r1.garra.abrir();\n" +
                              "r1.garra.cerrar();\n\n" +
                              "r1.detener();");
        
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        topPanel.add(inputScrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel();
        analyzeButton = new JButton("Analizar");
        simulateButton = new JButton("Simular Robot");
        simulateButton.setEnabled(false); // Deshabilitado hasta que se haga análisis
        clearButton = new JButton("Limpiar");
        saveButton = new JButton("Guardar");
        loadButton = new JButton("Cargar");
        
        buttonPanel.add(analyzeButton);
        buttonPanel.add(simulateButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Panel inferior para salida (tabla)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Tokens Identificados"));
        
        // Crear tabla para mostrar los tokens
        String[] columnNames = {"Tipo", "Lexema", "Línea", "Columna"};
        tableModel = new DefaultTableModel(columnNames, 0);
        outputTable = new JTable(tableModel);
        outputTable.setFillsViewportHeight(true);
        
        JScrollPane tableScrollPane = new JScrollPane(outputTable);
        bottomPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Agregar paneles al split
        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        
        // Agregar split al frame
        add(splitPane);
        
        // Configurar acciones de los botones
        setupActions();
        
        // Centrar la ventana
        setLocationRelativeTo(null);
    }
    
    private void setupActions() {
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeCode();
            }
        });
        
        simulateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulateRobot();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputTextArea.setText("");
                clearTable();
                simulateButton.setEnabled(false);
            }
        });
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });
        
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFromFile();
            }
        });
    }
    
    private void clearTable() {
        // Limpiar la tabla de tokens
        tableModel.setRowCount(0);
    }
    
    private void analyzeCode() {
        String code = inputTextArea.getText();
        clearTable();
        
        try {
            // Crear un reader con el código ingresado
            StringReader reader = new StringReader(code);
            
            // Crear el lexer
            RobotLexer lexer = new RobotLexer(reader);
            
            // Analizar el código
            lexer.yylex();
            
            // Guardar los tokens para la simulación
            lastAnalyzedTokens = lexer.tokens;
            
            // Mostrar los tokens en la tabla
            for (TokenInfo token : lexer.tokens) {
                Object[] row = {token.tipo, token.lexema, token.linea, token.columna};
                tableModel.addRow(row);
            }
            
            // Habilitar el botón de simulación
            simulateButton.setEnabled(true);
            
            JOptionPane.showMessageDialog(this, 
                "Análisis léxico completado con éxito.\n" + 
                lexer.tokens.size() + " tokens encontrados.",
                "Análisis Completo", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error en el análisis léxico: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            
            // Deshabilitar simulación si hay error
            simulateButton.setEnabled(false);
        }
    }
    
    private void simulateRobot() {
        if (lastAnalyzedTokens == null || lastAnalyzedTokens.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay tokens para simular. Por favor, analice el código primero.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Crear simulador si no existe
        if (simulator == null) {
            simulator = new RobotSimulator();
        }
        
        // Configurar comandos en el simulador
        simulator.addCommandsFromTokens(lastAnalyzedTokens);
        
        // Mostrar el simulador
        simulator.setVisible(true);
    }
    
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Código de Robot");
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(inputTextArea.getText());
                JOptionPane.showMessageDialog(this, "Archivo guardado con éxito");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar Código de Robot");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                inputTextArea.setText(content.toString());
                JOptionPane.showMessageDialog(this, "Archivo cargado con éxito");
                
                // Limpiar tabla y deshabilitar simulación
                clearTable();
                simulateButton.setEnabled(false);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al cargar archivo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}