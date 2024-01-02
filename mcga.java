import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class mcga extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JButton[] botones;
    private JLabel pangramaLabel;
    private String[] pangramas;
    private int indicePangramaActual = -1;
    private String pangramaActual;
    private int correctas = 0;
    private int incorrectas = 0;
    private String teclasDificiles = "";
    private Map<Character, Integer> teclasDificilesMap = new HashMap<>();
    private boolean bloquearEntrada = false;
    private boolean borrarPangrama = true;

    public mcga() {
        // Configuración de la ventana principal
        setTitle("Tutor de Mecanografía");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // JTextArea para mostrar el texto ingresado
        textArea = new JTextArea(5, 20);
        textArea.setEditable(true);
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    avanzarPangrama();
                } else if (bloquearEntrada) {
                    e.consume();
                } else if (borrarPangrama) {
                    textArea.setText("");
                    borrarPangrama = false;
                } else {
                    char caracterIngresado = e.getKeyChar();
                    char caracterPangrama = pangramaActual.charAt(textArea.getText().length());
                    if (caracterIngresado != caracterPangrama) {
                        incorrectas++;
                        char caracter = e.getKeyChar();
                        if (!teclasDificiles.contains(String.valueOf(caracter))) {
                            teclasDificiles += caracter + " ";
                            if (teclasDificilesMap.containsKey(caracter)) {
                                int count = teclasDificilesMap.get(caracter);
                                teclasDificilesMap.put(caracter, count + 1);
                            } else {
                                teclasDificilesMap.put(caracter, 1);
                            }
                        }
                    }
                }
            }
        });
        add(new JScrollPane(textArea), BorderLayout.NORTH);

        // Panel para los botones del teclado virtual
        JPanel panelTeclado = new JPanel(new GridLayout(4, 7));
        botones = new JButton[26];

        // Caracteres para los botones (A-Z)
        char caracter = 'A';
        for (int i = 0; i < 26; i++) {
            botones[i] = new JButton(String.valueOf(caracter));
            botones[i].addActionListener(this);
            panelTeclado.add(botones[i]);
            caracter++;
        }

        // Label para mostrar el pangrama actual
        pangramaLabel = new JLabel("");
        add(pangramaLabel, BorderLayout.CENTER);

        // Cargar los pangramas desde el archivo
        cargarPangramasDesdeArchivo("pangramas.txt");

        // Iniciar el juego con el primer pangrama
        avanzarPangrama();

        // Agregar el panel del teclado virtual a la ventana
        add(panelTeclado, BorderLayout.SOUTH);

        // Asegurarse de que la ventana tenga el enfoque al inicio
        requestFocusInWindow();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new mcga());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton botonPresionado = (JButton) e.getSource();
        String letra = botonPresionado.getText();
        textArea.append(letra);
        checkPangrama();
    }

    private void cargarPangramasDesdeArchivo(String nombreArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            ArrayList<String> pangramasList = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                pangramasList.add(linea);
            }
            pangramas = pangramasList.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "No se pudo cargar el archivo de pangramas.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void avanzarPangrama() {
        if (indicePangramaActual < pangramas.length - 1) {
            indicePangramaActual++;
            pangramaActual = pangramas[indicePangramaActual];
            pangramaLabel.setText(pangramaActual);
            textArea.setText("");
            bloquearEntrada = false;
            borrarPangrama = true;
            correctas++; // Aumentar el contador de pulsaciones correctas
        } else {
            mostrarInformeDificultades();
            System.exit(0);
        }
    }

    private void checkPangrama() {
        String textoIngresado = textArea.getText().replaceAll("\\s+", "");
        if (pangramaActual.equals(textoIngresado)) {
            bloquearEntrada = true;
            avanzarPangrama();
        }
    }

    private void mostrarInformeDificultades() {
        StringBuilder informe = new StringBuilder("Pulsaciones Correctas: " + correctas +
                "\nPulsaciones Incorrectas: " + incorrectas +
                "\nTeclas Difíciles: " + teclasDificiles);

        informe.append("\n\nFrecuencia de Teclas Difíciles:\n");
        for (char tecla : teclasDificilesMap.keySet()) {
            informe.append(tecla).append(": ").append(teclasDificilesMap.get(tecla)).append(" veces\n");
        }

        JOptionPane.showMessageDialog(this, informe.toString(),
                "Informe de Dificultades", JOptionPane.INFORMATION_MESSAGE);
    }
}

