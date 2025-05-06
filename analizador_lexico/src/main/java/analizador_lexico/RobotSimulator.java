package analizador_lexico;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

/**
 * Clase que implementa la simulación gráfica del brazo robótico
 */
public class RobotSimulator extends JFrame {
    // Estados y propiedades del robot
    private int baseAngle = 0;        // Ángulo de giro de la base (0-360 grados)
    private int cuerpoAltura = 50;    // Altura del cuerpo (0-100)
    private boolean garraAbierta = false;  // Estado de la garra
    
    // Dimensiones y coordenadas para el dibujo
    private final int BASE_WIDTH = 100;
    private final int BASE_HEIGHT = 30;
    private final int BODY_WIDTH = 30;
    private final int MAX_BODY_HEIGHT = 100;
    private final int GARRA_SIZE = 30;
    
    // Panel para la visualización del robot
    private RobotPanel robotPanel;
    
    // Lista de comandos a ejecutar y timer para animación
    private Queue<RobotCommand> commandQueue = new LinkedList<>();
    private Timer animationTimer;
    private boolean animationRunning = false;
    
    /**
     * Constructor de la simulación
     */
    public RobotSimulator() {
        setTitle("Simulador de Brazo Robótico");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Panel para visualizar el robot
        robotPanel = new RobotPanel();
        add(robotPanel, BorderLayout.CENTER);
        
        // Panel de controles
        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Iniciar Simulación");
        JButton resetButton = new JButton("Reiniciar");
        controlPanel.add(startButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Label para mostrar estado actual
        JPanel statusPanel = new JPanel(new BorderLayout());
        JLabel statusLabel = new JLabel("Estado: Esperando comandos");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.NORTH);
        
        // Configurar timer para animación
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!commandQueue.isEmpty()) {
                    RobotCommand command = commandQueue.peek();
                    boolean finished = command.execute();
                    
                    if (finished) {
                        commandQueue.poll(); // Remover el comando completado
                        statusLabel.setText("Estado: " + (commandQueue.isEmpty() ? 
                                         "Simulación completada" : 
                                         "Ejecutando " + commandQueue.peek().getDescription()));
                    }
                    
                    robotPanel.repaint();
                } else {
                    animationTimer.stop();
                    animationRunning = false;
                    statusLabel.setText("Estado: Simulación completada");
                }
            }
        });
        
        // Acción del botón iniciar
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!animationRunning && !commandQueue.isEmpty()) {
                    animationRunning = true;
                    statusLabel.setText("Estado: Ejecutando " + commandQueue.peek().getDescription());
                    animationTimer.start();
                }
            }
        });
        
        // Acción del botón reiniciar
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationTimer.stop();
                animationRunning = false;
                resetRobot();
                commandQueue.clear();
                statusLabel.setText("Estado: Esperando comandos");
                robotPanel.repaint();
            }
        });
        
        // Centrar ventana
        setLocationRelativeTo(null);
    }
    
    /**
     * Reinicia el robot a su posición inicial
     */
    private void resetRobot() {
        baseAngle = 0;
        cuerpoAltura = 50;
        garraAbierta = false;
    }
    
    /**
     * Añade comandos desde los tokens analizados
     */
    public void addCommandsFromTokens(ArrayList<TokenInfo> tokens) {
        // Resetear el robot y la cola de comandos
        resetRobot();
        commandQueue.clear();
        
        // Variables para seguimiento del estado de análisis
        String currentComponent = "";
        String currentAction = "";
        int degrees = 0;
        int velocity = 1;
        int time = 1;
        
        // Analizar los tokens y crear comandos
        for (int i = 0; i < tokens.size(); i++) {
            TokenInfo token = tokens.get(i);
            String type = token.tipo;
            String lexeme = token.lexema;
            
            if (type.equals("BASE") || type.equals("CUERPO") || type.equals("GARRA")) {
                currentComponent = lexeme;
            } 
            else if (type.equals("GIRAR") || type.equals("SUBIR") || type.equals("BAJAR") || 
                     type.equals("ABRIR") || type.equals("CERRAR")) {
                currentAction = lexeme;
                
                // Buscar parámetros para las acciones
                degrees = 0;
                velocity = 1;
                time = 1;
                
                // Buscar parámetros entre paréntesis
                if (i + 1 < tokens.size() && tokens.get(i+1).tipo.equals("PARENTESIS_ABRE")) {
                    int j = i + 2;
                    while (j < tokens.size() && !tokens.get(j).tipo.equals("PARENTESIS_CIERRA")) {
                        if (tokens.get(j).tipo.equals("NUMERO")) {
                            degrees = Integer.parseInt(tokens.get(j).lexema);
                        } else if (tokens.get(j).tipo.equals("VELOCIDAD") && j + 2 < tokens.size() && 
                                  tokens.get(j+1).tipo.equals("IGUAL") && tokens.get(j+2).tipo.equals("NUMERO")) {
                            velocity = Integer.parseInt(tokens.get(j+2).lexema);
                            j += 2;
                        } else if (tokens.get(j).tipo.equals("TIEMPO") && j + 2 < tokens.size() && 
                                  tokens.get(j+1).tipo.equals("IGUAL") && tokens.get(j+2).tipo.equals("NUMERO")) {
                            time = Integer.parseInt(tokens.get(j+2).lexema);
                            j += 2;
                        }
                        j++;
                    }
                }
                
                // Crear comando según componente y acción
                if (currentComponent.equals("base") && currentAction.equals("girar")) {
                    commandQueue.add(new GirarBaseCommand(degrees, velocity, time));
                } else if (currentComponent.equals("cuerpo") && currentAction.equals("subir")) {
                    commandQueue.add(new SubirCuerpoCommand(degrees, velocity, time));
                } else if (currentComponent.equals("cuerpo") && currentAction.equals("bajar")) {
                    commandQueue.add(new BajarCuerpoCommand(degrees, velocity, time));
                } else if (currentComponent.equals("garra") && currentAction.equals("abrir")) {
                    commandQueue.add(new AbrirGarraCommand(time));
                } else if (currentComponent.equals("garra") && currentAction.equals("cerrar")) {
                    commandQueue.add(new CerrarGarraCommand(time));
                }
            }
        }
    }
    
    // Panel personalizado para dibujar el robot
    private class RobotPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Configurar calidad de renderizado
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Obtener dimensiones del panel
            int width = getWidth();
            int height = getHeight();
            
            // Posición central para la base
            int centerX = width / 2;
            int centerY = height - 50;
            
            // Dibujar base
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRoundRect(centerX - BASE_WIDTH/2, centerY - BASE_HEIGHT/2, 
                            BASE_WIDTH, BASE_HEIGHT, 15, 15);
            
            // Calcular posición para el cuerpo según ángulo de la base
            double radians = Math.toRadians(baseAngle);
            int bodyBottomX = centerX + (int)(Math.sin(radians) * BASE_WIDTH/4);
            int bodyBottomY = centerY - BASE_HEIGHT/2 - 5;
            
            // Dibujar cuerpo
            g2d.setColor(Color.BLUE);
            int bodyHeight = cuerpoAltura;
            g2d.fillRect(bodyBottomX - BODY_WIDTH/2, bodyBottomY - bodyHeight, 
                        BODY_WIDTH, bodyHeight);
            
            // Posición de la garra
            int garraX = bodyBottomX;
            int garraY = bodyBottomY - bodyHeight;
            
            // Dibujar garra
            g2d.setColor(Color.RED);
            if (garraAbierta) {
                // Garra abierta
                g2d.fillArc(garraX - GARRA_SIZE/2, garraY - GARRA_SIZE/2, 
                          GARRA_SIZE, GARRA_SIZE, 45, 90);
                g2d.fillArc(garraX - GARRA_SIZE/2, garraY - GARRA_SIZE/2, 
                          GARRA_SIZE, GARRA_SIZE, 225, 90);
            } else {
                // Garra cerrada
               g2d.fillOval(garraX - GARRA_SIZE / 3, garraY - GARRA_SIZE / 3,
             (int)(GARRA_SIZE / 1.5f), (int)(GARRA_SIZE / 1.5f));

            }
            
            // Dibujar información de estado
            g2d.setColor(Color.BLACK);
            g2d.drawString("Base: " + baseAngle + "°", 10, 20);
            g2d.drawString("Cuerpo: " + cuerpoAltura + "%", 10, 40);
            g2d.drawString("Garra: " + (garraAbierta ? "Abierta" : "Cerrada"), 10, 60);
        }
    }
    
    // Interfaz para comandos de robot
    private interface RobotCommand {
        boolean execute();
        String getDescription();
    }
    
    // Comando para girar la base
    private class GirarBaseCommand implements RobotCommand {
        private final int targetAngle;
        private final int startAngle;
        private final int velocity;
        private final int totalSteps;
        private int currentStep = 0;
        
        public GirarBaseCommand(int degrees, int velocity, int time) {
            this.startAngle = baseAngle;
            this.targetAngle = baseAngle + degrees;
            this.velocity = velocity;
            // Calcular pasos totales según velocidad y tiempo
            this.totalSteps = Math.max(10, velocity * time * 10);
        }
        
        @Override
        public boolean execute() {
            currentStep++;
            double progress = (double) currentStep / totalSteps;
            if (progress >= 1.0) {
                baseAngle = targetAngle;
                return true; // Comando completado
            } else {
                baseAngle = startAngle + (int)((targetAngle - startAngle) * progress);
                return false; // Comando aún en ejecución
            }
        }
        
        @Override
        public String getDescription() {
            return "Girando base a " + targetAngle + "°";
        }
    }
    
    // Comando para subir el cuerpo
    private class SubirCuerpoCommand implements RobotCommand {
        private final int startHeight;
        private final int targetHeight;
        private final int velocity;
        private final int totalSteps;
        private int currentStep = 0;
        
        public SubirCuerpoCommand(int amount, int velocity, int time) {
            this.startHeight = cuerpoAltura;
            this.targetHeight = Math.min(100, cuerpoAltura + amount);
            this.velocity = velocity;
            this.totalSteps = Math.max(10, velocity * time * 10);
        }
        
        @Override
        public boolean execute() {
            currentStep++;
            double progress = (double) currentStep / totalSteps;
            if (progress >= 1.0) {
                cuerpoAltura = targetHeight;
                return true;
            } else {
                cuerpoAltura = startHeight + (int)((targetHeight - startHeight) * progress);
                return false;
            }
        }
        
        @Override
        public String getDescription() {
            return "Subiendo cuerpo a " + targetHeight + "%";
        }
    }
    
    // Comando para bajar el cuerpo
    private class BajarCuerpoCommand implements RobotCommand {
        private final int startHeight;
        private final int targetHeight;
        private final int velocity;
        private final int totalSteps;
        private int currentStep = 0;
        
        public BajarCuerpoCommand(int amount, int velocity, int time) {
            this.startHeight = cuerpoAltura;
            this.targetHeight = Math.max(0, cuerpoAltura - amount);
            this.velocity = velocity;
            this.totalSteps = Math.max(10, velocity * time * 10);
        }
        
        @Override
        public boolean execute() {
            currentStep++;
            double progress = (double) currentStep / totalSteps;
            if (progress >= 1.0) {
                cuerpoAltura = targetHeight;
                return true;
            } else {
                cuerpoAltura = startHeight + (int)((targetHeight - startHeight) * progress);
                return false;
            }
        }
        
        @Override
        public String getDescription() {
            return "Bajando cuerpo a " + targetHeight + "%";
        }
    }
    
    // Comando para abrir la garra
    private class AbrirGarraCommand implements RobotCommand {
        private final int totalSteps;
        private int currentStep = 0;
        
        public AbrirGarraCommand(int time) {
            this.totalSteps = time * 20;
        }
        
        @Override
        public boolean execute() {
            currentStep++;
            if (currentStep >= totalSteps) {
                garraAbierta = true;
                return true;
            }
            if (currentStep >= totalSteps / 2) {
                garraAbierta = true;
            }
            return false;
        }
        
        @Override
        public String getDescription() {
            return "Abriendo garra";
        }
    }
    
    // Comando para cerrar la garra
    private class CerrarGarraCommand implements RobotCommand {
        private final int totalSteps;
        private int currentStep = 0;
        
        public CerrarGarraCommand(int time) {
            this.totalSteps = time * 20;
        }
        
        @Override
        public boolean execute() {
            currentStep++;
            if (currentStep >= totalSteps) {
                garraAbierta = false;
                return true;
            }
            if (currentStep >= totalSteps / 2) {
                garraAbierta = false;
            }
            return false;
        }
        
        @Override
        public String getDescription() {
            return "Cerrando garra";
        }
    }
}