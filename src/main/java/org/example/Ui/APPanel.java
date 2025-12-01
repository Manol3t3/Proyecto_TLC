package org.example.Ui;

import org.example.Modelo.AutomataDePila;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.*;

/**
 * Panel de interfaz para Autómatas de Pila - Manteniendo estructura original
 */
public class APPanel extends BorderPane {

    private AutomataDePila ap;
    private TextArea outputArea;
    private Pane pilaPane;
    private Pane automataPane;

    private TextField estadosField;
    private TextField alfabetoEntradaField;
    private TextField alfabetoPilaField;
    private TextField estadoInicialField;
    private TextField simboloInicialPilaField;
    private TextField estadosFinalesField;
    private TextArea transicionesArea;
    private TextField cadenaField;

    public APPanel() {
        this.ap = new AutomataDePila();
        initUI();
    }

    private void initUI() {
        setPadding(new Insets(15));

        VBox leftPanel = createDefinitionPanel();
        VBox centerPanel = createVisualizationPanel();
        VBox rightPanel = createResultsPanel();

        setLeft(leftPanel);
        setCenter(centerPanel);
        setRight(rightPanel);
    }

    private VBox createDefinitionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(320);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Definición del Autómata de Pila");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label estadosLabel = new Label("Estados (separados por comas):");
        estadosField = new TextField();
        estadosField.setPromptText("q0,q1,q2");

        Label alfabetoEntradaLabel = new Label("Alfabeto de entrada:");
        alfabetoEntradaField = new TextField();
        alfabetoEntradaField.setPromptText("ab");

        Label alfabetoPilaLabel = new Label("Alfabeto de pila:");
        alfabetoPilaField = new TextField();
        alfabetoPilaField.setPromptText("XZ");

        Label estadoInicialLabel = new Label("Estado inicial:");
        estadoInicialField = new TextField();
        estadoInicialField.setPromptText("q0");

        Label simboloInicialPilaLabel = new Label("Símbolo inicial de pila:");
        simboloInicialPilaField = new TextField();
        simboloInicialPilaField.setPromptText("Z");

        Label estadosFinalesLabel = new Label("Estados finales:");
        estadosFinalesField = new TextField();
        estadosFinalesField.setPromptText("q2");

        Label transicionesLabel = new Label("Transiciones:");
        Label formatoLabel = new Label("Formato: q0,a,Z,q1,XZ");
        formatoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label formatoLabel2 = new Label("(usar 'e' para epsilon en entrada/pila)");
        formatoLabel2.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        transicionesArea = new TextArea();
        transicionesArea.setPrefRowCount(6);

        Label ejemploLabel = new Label(
                "Ejemplo a^n b^n:\n" +
                        "q0,a,e,q0,X  (Push X con a)\n" +
                        "q0,b,X,q1,e  (Pop X con b)\n" +
                        "q1,b,X,q1,e  (Sigue pop)\n" +
                        "q1,e,Z,q2,e  (Aceptar pila vacía)"
        );
        ejemploLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888; " +
                "-fx-background-color: #ffffcc; -fx-padding: 5;");

        Button construirBtn = new Button("Construir Autómata");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        construirBtn.setOnAction(e -> construirAutomata());

        HBox fileButtons = new HBox(5);
        Button cargarBtn = new Button("Cargar");
        Button guardarBtn = new Button("Guardar");
        cargarBtn.setOnAction(e -> cargarDesdeArchivo());
        guardarBtn.setOnAction(e -> guardarEnArchivo());
        fileButtons.getChildren().addAll(cargarBtn, guardarBtn);

        // Botones de ejemplos rápidos
        HBox ejemploButtons = new HBox(5);
        Button ejemploAnBn = new Button("Ejemplo a^n b^n");
        ejemploAnBn.setOnAction(e -> cargarEjemploAnBn());

        Button ejemploParentesis = new Button("Ejemplo Paréntesis");
        ejemploParentesis.setOnAction(e -> cargarEjemploParentesis());

        ejemploButtons.getChildren().addAll(ejemploAnBn, ejemploParentesis);

        panel.getChildren().addAll(
                titleLabel,
                new Separator(),
                estadosLabel, estadosField,
                alfabetoEntradaLabel, alfabetoEntradaField,
                alfabetoPilaLabel, alfabetoPilaField,
                estadoInicialLabel, estadoInicialField,
                simboloInicialPilaLabel, simboloInicialPilaField,
                estadosFinalesLabel, estadosFinalesField,
                transicionesLabel, formatoLabel, formatoLabel2,
                transicionesArea,
                ejemploLabel,
                ejemploButtons,
                construirBtn,
                fileButtons
        );

        return new VBox(new ScrollPane(panel));
    }

    private VBox createVisualizationPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(450);

        // Información del estado actual
        VBox estadoInfoBox = new VBox(5);
        estadoInfoBox.setPadding(new Insets(10));
        estadoInfoBox.setStyle("-fx-background-color: #e8f5e8; -fx-border-radius: 5;");

        Label estadoInfoLabel = new Label("Información del Estado Actual");
        estadoInfoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label estadoActualLabel = new Label("Estado: -");
        estadoActualLabel.setId("estadoActualLabel");

        Label cadenaRestanteLabel = new Label("Cadena restante: -");
        cadenaRestanteLabel.setId("cadenaRestanteLabel");

        estadoInfoBox.getChildren().addAll(estadoInfoLabel, estadoActualLabel, cadenaRestanteLabel);

        // Visualización de la pila
        Label pilaTitle = new Label("Visualización de la Pila");
        pilaTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        pilaPane = new Pane();
        pilaPane.setPrefSize(400, 250);
        pilaPane.setStyle("-fx-border-color: #ccc; -fx-background-color: #f9f9f9;");

        // Panel de prueba
        VBox testBox = new VBox(10);
        testBox.setPadding(new Insets(10));
        testBox.setStyle("-fx-background-color: #e1f5fe; -fx-border-radius: 5;");

        Label testLabel = new Label("Probar cadena:");
        cadenaField = new TextField();
        cadenaField.setPromptText("Ejemplo: aabb");

        HBox botonesBox = new HBox(10);
        Button procesarBtn = new Button("Procesar Cadena");
        procesarBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white");
        procesarBtn.setOnAction(e -> procesarCadena());

        Button pasoBtn = new Button("Ver Pasos");
        pasoBtn.setOnAction(e -> mostrarPasos());

        botonesBox.getChildren().addAll(procesarBtn, pasoBtn);

        testBox.getChildren().addAll(testLabel, cadenaField, botonesBox);

        panel.getChildren().addAll(estadoInfoBox, pilaTitle, pilaPane, testBox);
        return panel;
    }

    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f0f0f0;");

        outputArea = new TextArea();
        outputArea.setPrefRowCount(15);

        Button limpiarBtn = new Button("Limpiar");
        limpiarBtn.setOnAction(e -> {
            outputArea.clear();
            pilaPane.getChildren().clear();
            actualizarEstadoActual("-", "-");
        });

        panel.getChildren().addAll(new Label("Historial y Resultados"), outputArea, limpiarBtn);
        return panel;
    }

    private void construirAutomata() {
        try {
            // Crear nuevo autómata
            ap = new AutomataDePila();

            // 1. Estados
            String estadosText = estadosField.getText().trim();
            if (!estadosText.isEmpty()) {
                String[] estados = estadosText.split(",");
                for (String e : estados) {
                    ap.agregarEstado(e.trim());
                }
            }

            // 2. Alfabeto entrada
            String alfEntradaText = alfabetoEntradaField.getText().trim();
            for (char c : alfEntradaText.toCharArray()) {
                if (c != ' ') ap.agregarSimboloEntrada(c);
            }

            // 3. Alfabeto pila
            String alfPilaText = alfabetoPilaField.getText().trim();
            for (char c : alfPilaText.toCharArray()) {
                if (c != ' ') ap.agregarSimboloPila(c);
            }

            // 4. Estado inicial
            String estadoInicialText = estadoInicialField.getText().trim();
            if (!estadoInicialText.isEmpty()) {
                ap.setEstadoInicial(estadoInicialText);
            }

            // 5. Símbolo inicial pila
            String simboloPilaText = simboloInicialPilaField.getText().trim();
            if (!simboloPilaText.isEmpty()) {
                ap.setSimboloInicialPila(simboloPilaText.charAt(0));
            }

            // 6. Estados finales
            String estadosFinalesText = estadosFinalesField.getText().trim();
            if (!estadosFinalesText.isEmpty()) {
                String[] finales = estadosFinalesText.split(",");
                for (String f : finales) {
                    ap.agregarEstadoFinal(f.trim());
                }
            }

            // 7. Configurar aceptación por pila vacía
            ap.setAceptarPorPilaVacia(true);

            // 8. Transiciones
            String transicionesText = transicionesArea.getText().trim();
            if (!transicionesText.isEmpty()) {
                String[] lineas = transicionesText.split("\n");
                for (String linea : lineas) {
                    linea = linea.trim();
                    if (linea.isEmpty()) continue;

                    String[] partes = linea.split(",");
                    if (partes.length == 5) {
                        String qOrigen = partes[0].trim();
                        String entradaStr = partes[1].trim();
                        String pilaStr = partes[2].trim();
                        String qDestino = partes[3].trim();
                        String apilarStr = partes[4].trim();

                        Character entrada = entradaStr.equals("e") ? null : entradaStr.charAt(0);
                        Character pila = pilaStr.equals("e") ? null : pilaStr.charAt(0);
                        String apilar = apilarStr.equals("e") ? "" : apilarStr;

                        ap.agregarTransicion(qOrigen, entrada, pila, qDestino, apilar);
                    }
                }
            }

            // Mostrar confirmación
            outputArea.setText("✓ Autómata construido exitosamente\n");
            outputArea.appendText("Estados: " + String.join(", ", ap.getEstados()) + "\n");
            outputArea.appendText("Alfabeto entrada: " + ap.getAlfabetoEntrada() + "\n");
            outputArea.appendText("Alfabeto pila: " + ap.getAlfabetoPila() + "\n");
            outputArea.appendText("Estado inicial: " + ap.getEstadoInicial() + "\n");
            outputArea.appendText("Símbolo inicial pila: " + ap.getSimboloInicialPila() + "\n");
            outputArea.appendText("Estados finales: " + String.join(", ", ap.getEstadosFinales()) + "\n");

            // Mostrar pila inicial
            mostrarPilaInicial();
            actualizarEstadoActual(ap.getEstadoInicial(), "Cadena completa");

        } catch (Exception ex) {
            mostrarError("Error al construir autómata:\n" + ex.getMessage());
        }
    }

    private void mostrarPilaInicial() {
        pilaPane.getChildren().clear();

        // Dibujar símbolo inicial de pila
        char simboloInicial = ap.getSimboloInicialPila();

        Rectangle rect = new Rectangle(150, 40, 100, 40);
        rect.setFill(Color.LIGHTBLUE);
        rect.setStroke(Color.DARKBLUE);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        Text simboloText = new Text(185, 65, String.valueOf(simboloInicial));
        simboloText.setFont(Font.font(16));
        simboloText.setStyle("-fx-font-weight: bold;");

        Text etiquetaText = new Text(160, 90, "Símbolo inicial (Z)");
        etiquetaText.setFont(Font.font(12));

        pilaPane.getChildren().addAll(rect, simboloText, etiquetaText);
    }

    private void procesarCadena() {
        String cadena = cadenaField.getText();
        if (cadena.isEmpty()) {
            mostrarError("Ingrese una cadena para procesar");
            return;
        }

        try {
            boolean aceptada = ap.procesar(cadena);

            outputArea.clear();
            if (aceptada) {
                outputArea.appendText("✅ CADENA ACEPTADA\n\n");
            } else {
                outputArea.appendText("❌ CADENA RECHAZADA\n\n");
            }

            outputArea.appendText("Cadena: " + cadena + "\n");
            outputArea.appendText("Longitud: " + cadena.length() + "\n\n");

            outputArea.appendText("--- HISTORIAL DE CONFIGURACIONES ---\n");
            List<AutomataDePila.ConfiguracionAP> historial = ap.getHistorial();

            // Mostrar todas las configuraciones
            for (int i = 0; i < historial.size(); i++) {
                AutomataDePila.ConfiguracionAP config = historial.get(i);
                outputArea.appendText("Paso " + i + ": " + config + "\n");
            }

            // Mostrar última configuración
            if (!historial.isEmpty()) {
                AutomataDePila.ConfiguracionAP ultimaConfig = historial.get(historial.size() - 1);
                mostrarPila(ultimaConfig.pila);
                actualizarEstadoActual(ultimaConfig.estado,
                        ultimaConfig.cadenaRestante.isEmpty() ? "ε" : ultimaConfig.cadenaRestante);
            }

        } catch (Exception ex) {
            mostrarError("Error al procesar cadena:\n" + ex.getMessage());
        }
    }

    private void mostrarPasos() {
        String cadena = cadenaField.getText();
        if (cadena.isEmpty()) {
            mostrarError("Ingrese una cadena primero");
            return;
        }

        outputArea.clear();
        outputArea.appendText("=== ANÁLISIS PASO A PASO ===\n\n");

        // Obtener historial si no está procesado
        if (ap.getHistorial().isEmpty()) {
            ap.procesar(cadena);
        }

        List<AutomataDePila.ConfiguracionAP> historial = ap.getHistorial();

        for (int i = 0; i < historial.size(); i++) {
            AutomataDePila.ConfiguracionAP config = historial.get(i);
            outputArea.appendText("┌─ Paso " + i + " ──────────────────┐\n");
            outputArea.appendText("│ Estado: " + config.estado + "\n");
            outputArea.appendText("│ Cadena restante: " +
                    (config.cadenaRestante.isEmpty() ? "ε" : config.cadenaRestante) + "\n");
            outputArea.appendText("│ Pila: " + config.getPilaString() + "\n");
            outputArea.appendText("└──────────────────────────────┘\n");

            if (i < historial.size() - 1) {
                outputArea.appendText("                ↓\n");
            }
        }

        // Mostrar última pila
        if (!historial.isEmpty()) {
            mostrarPila(historial.get(historial.size() - 1).pila);
        }
    }

    private void mostrarPila(Stack<Character> pila) {
        pilaPane.getChildren().clear();

        if (pila.isEmpty()) {
            // Mostrar pila vacía
            Text vacioText = new Text(150, 100, "PILA VACÍA");
            vacioText.setFont(Font.font(20));
            vacioText.setStyle("-fx-font-weight: bold; -fx-fill: #4CAF50;");
            pilaPane.getChildren().add(vacioText);
            return;
        }

        // Mostrar elementos de la pila (tope arriba)
        double x = 150;
        double y = 200;
        double alturaRect = 35;

        // Convertir pila a lista para iterar desde el fondo
        List<Character> elementos = new ArrayList<>();
        for (Character c : pila) {
            elementos.add(c);
        }
        Collections.reverse(elementos); // Para mostrar tope arriba

        for (int i = 0; i < elementos.size(); i++) {
            char simbolo = elementos.get(i);
            boolean esTope = (i == 0); // El primer elemento después de reverse es el tope

            double yPos = y - (i * alturaRect);

            Rectangle rect = new Rectangle(x, yPos, 100, alturaRect);
            rect.setFill(esTope ? Color.LIGHTCORAL : Color.LIGHTGREEN);
            rect.setStroke(Color.DARKGRAY);
            rect.setArcWidth(5);
            rect.setArcHeight(5);

            Text simboloText = new Text(x + 45, yPos + 23, String.valueOf(simbolo));
            simboloText.setFont(Font.font(14));
            simboloText.setStyle("-fx-font-weight: bold;");

            pilaPane.getChildren().addAll(rect, simboloText);

            if (esTope) {
                Text topeText = new Text(x + 30, yPos - 5, "TOPE");
                topeText.setFont(Font.font(10));
                topeText.setStyle("-fx-font-weight: bold;");
                pilaPane.getChildren().add(topeText);
            }
        }

        // Mostrar contador
        Text contadorText = new Text(x, 220, "Elementos en pila: " + pila.size());
        contadorText.setFont(Font.font(12));
        pilaPane.getChildren().add(contadorText);
    }

    private void actualizarEstadoActual(String estado, String cadenaRestante) {
        Label estadoLabel = (Label) lookup("#estadoActualLabel");
        if (estadoLabel != null) {
            estadoLabel.setText("Estado: " + estado);
        }

        Label cadenaLabel = (Label) lookup("#cadenaRestanteLabel");
        if (cadenaLabel != null) {
            cadenaLabel.setText("Cadena restante: " + cadenaRestante);
        }
    }

    private void cargarEjemploAnBn() {
        estadosField.setText("q0,q1,q2");
        alfabetoEntradaField.setText("ab");
        alfabetoPilaField.setText("XZ");
        estadoInicialField.setText("q0");
        simboloInicialPilaField.setText("Z");
        estadosFinalesField.setText("q2");
        transicionesArea.setText(
                "q0,a,e,q0,X\n" +
                        "q0,b,X,q1,e\n" +
                        "q1,b,X,q1,e\n" +
                        "q1,e,Z,q2,e"
        );
        cadenaField.setText("aabb");

        // Construir automáticamente
        construirAutomata();
    }

    private void cargarEjemploParentesis() {
        estadosField.setText("s");
        alfabetoEntradaField.setText("()");
        alfabetoPilaField.setText("XZ");
        estadoInicialField.setText("s");
        simboloInicialPilaField.setText("Z");
        estadosFinalesField.setText("s");
        transicionesArea.setText(
                "s,(,e,s,X\n" +
                        "s,),X,s,e\n" +
                        "s,e,Z,s,e"
        );
        cadenaField.setText("(())");

        // Construir automáticamente
        construirAutomata();
    }

    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar autómata desde archivo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Limpiar campos
                estadosField.clear();
                alfabetoEntradaField.clear();
                alfabetoPilaField.clear();
                estadoInicialField.clear();
                simboloInicialPilaField.clear();
                estadosFinalesField.clear();
                transicionesArea.clear();

                // Leer línea por línea
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("ESTADOS:")) {
                        estadosField.setText(line.substring(8).trim());
                    } else if (line.startsWith("ENTRADA:")) {
                        alfabetoEntradaField.setText(line.substring(8).trim());
                    } else if (line.startsWith("PILA:")) {
                        alfabetoPilaField.setText(line.substring(5).trim());
                    } else if (line.startsWith("INICIAL:")) {
                        estadoInicialField.setText(line.substring(8).trim());
                    } else if (line.startsWith("SIMBOLO_PILA:")) {
                        simboloInicialPilaField.setText(line.substring(13).trim());
                    } else if (line.startsWith("FINALES:")) {
                        estadosFinalesField.setText(line.substring(8).trim());
                    } else if (line.startsWith("TRANSICION:")) {
                        transicionesArea.appendText(line.substring(11).trim() + "\n");
                    }
                }

                outputArea.appendText("\n✓ Autómata cargado desde: " + file.getName());

            } catch (IOException ex) {
                mostrarError("Error al cargar archivo:\n" + ex.getMessage());
            }
        }
    }

    private void guardarEnArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar autómata en archivo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("ESTADOS: " + estadosField.getText());
                writer.println("ENTRADA: " + alfabetoEntradaField.getText());
                writer.println("PILA: " + alfabetoPilaField.getText());
                writer.println("INICIAL: " + estadoInicialField.getText());
                writer.println("SIMBOLO_PILA: " + simboloInicialPilaField.getText());
                writer.println("FINALES: " + estadosFinalesField.getText());
                writer.println("=== TRANSICIONES ===");
                writer.print(transicionesArea.getText());

                outputArea.appendText("\n✓ Autómata guardado en: " + file.getName());

            } catch (IOException ex) {
                mostrarError("Error al guardar archivo:\n" + ex.getMessage());
            }
        }
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}