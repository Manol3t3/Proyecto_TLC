package org.example.Ui;

import org.example.Modelo.AFD;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.*;

/**
 * Panel de interfaz para el modo AFD
 */
public class AFDPanel extends BorderPane {

    private AFD afd;
    private TextArea outputArea;
    private Pane canvasPane;

    // Controles de entrada
    private TextField estadosField;
    private TextField alfabetoField;
    private TextField estadoInicialField;
    private TextField estadosFinalesField;
    private TextArea transicionesArea;
    private TextField cadenaField;

    public AFDPanel() {
        this.afd = new AFD();
        initUI();
    }

    private void initUI() {
        setPadding(new Insets(15));

        // Panel izquierdo: Definición del AFD
        VBox leftPanel = createDefinitionPanel();

        // Panel central: Visualización
        VBox centerPanel = createVisualizationPanel();

        // Panel derecho: Resultados
        VBox rightPanel = createResultsPanel();

        // Configurar layout principal
        setLeft(leftPanel);
        setCenter(centerPanel);
        setRight(rightPanel);
    }

    private VBox createDefinitionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Definición del AFD");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Estados
        Label estadosLabel = new Label("Estados (separados por comas):");
        estadosField = new TextField();
        estadosField.setPromptText("q0,q1,q2");

        // Alfabeto
        Label alfabetoLabel = new Label("Alfabeto (sin separadores):");
        alfabetoField = new TextField();
        alfabetoField.setPromptText("ab");

        // Estado inicial
        Label estadoInicialLabel = new Label("Estado inicial:");
        estadoInicialField = new TextField();
        estadoInicialField.setPromptText("q0");

        // Estados finales
        Label estadosFinalesLabel = new Label("Estados finales (separados por comas):");
        estadosFinalesField = new TextField();
        estadosFinalesField.setPromptText("q2");

        // Transiciones
        Label transicionesLabel = new Label("Transiciones (formato: q0,a,q1):");
        transicionesArea = new TextArea();
        transicionesArea.setPromptText("q0,a,q1\nq1,b,q2\nq2,a,q0");
        transicionesArea.setPrefRowCount(5);

        // Botón para construir AFD
        Button construirBtn = new Button("Construir AFD");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        construirBtn.setOnAction(e -> construirAFD());

        // Botones de archivo
        HBox fileButtons = new HBox(5);
        Button cargarBtn = new Button("Cargar");
        Button guardarBtn = new Button("Guardar");
        cargarBtn.setOnAction(e -> cargarDesdeArchivo());
        guardarBtn.setOnAction(e -> guardarEnArchivo());
        fileButtons.getChildren().addAll(cargarBtn, guardarBtn);

        panel.getChildren().addAll(
                titleLabel,
                new Separator(),
                estadosLabel, estadosField,
                alfabetoLabel, alfabetoField,
                estadoInicialLabel, estadoInicialField,
                estadosFinalesLabel, estadosFinalesField,
                transicionesLabel, transicionesArea,
                construirBtn,
                fileButtons
        );

        return panel;
    }

    private VBox createVisualizationPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Visualización del Autómata");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        canvasPane = new Pane();
        canvasPane.setPrefSize(500, 400);
        canvasPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: white;");

        // Panel de prueba de cadenas
        VBox testPanel = new VBox(10);
        testPanel.setAlignment(Pos.CENTER);
        testPanel.setPadding(new Insets(10));
        testPanel.setStyle("-fx-background-color: #e8f5e9; -fx-background-radius: 5;");

        Label testLabel = new Label("Probar Cadena:");
        testLabel.setStyle("-fx-font-weight: bold;");

        cadenaField = new TextField();
        cadenaField.setPromptText("Ingrese la cadena a probar");
        cadenaField.setPrefWidth(300);

        Button procesarBtn = new Button("Procesar Cadena");
        procesarBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        procesarBtn.setOnAction(e -> procesarCadena());

        testPanel.getChildren().addAll(testLabel, cadenaField, procesarBtn);

        panel.getChildren().addAll(titleLabel, canvasPane, testPanel);
        return panel;
    }

    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Resultados");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(20);
        outputArea.setWrapText(true);

        Button limpiarBtn = new Button("Limpiar Resultados");
        limpiarBtn.setMaxWidth(Double.MAX_VALUE);
        limpiarBtn.setOnAction(e -> outputArea.clear());

        panel.getChildren().addAll(titleLabel, new Separator(), outputArea, limpiarBtn);
        return panel;
    }

    private void construirAFD() {
        try {
            afd = new AFD();

            // Agregar estados
            String[] estados = estadosField.getText().trim().split(",");
            for (String estado : estados) {
                afd.agregarEstado(estado.trim());
            }

            // Agregar alfabeto
            String alfabeto = alfabetoField.getText().trim();
            for (char c : alfabeto.toCharArray()) {
                afd.agregarSimbolo(c);
            }

            // Establecer estado inicial
            afd.setEstadoInicial(estadoInicialField.getText().trim());

            // Agregar estados finales
            String[] finales = estadosFinalesField.getText().trim().split(",");
            for (String estado : finales) {
                afd.agregarEstadoFinal(estado.trim());
            }

            // Agregar transiciones
            String[] transiciones = transicionesArea.getText().trim().split("\n");
            for (String trans : transiciones) {
                String[] partes = trans.trim().split(",");
                if (partes.length == 3) {
                    afd.agregarTransicion(
                            partes[0].trim(),
                            partes[1].trim().charAt(0),
                            partes[2].trim()
                    );
                }
            }

            visualizarAFD();
            outputArea.setText("✓ AFD construido exitosamente!\n\n" +
                    "Estados: " + afd.getEstados() + "\n" +
                    "Alfabeto: " + afd.getAlfabeto() + "\n" +
                    "Estado inicial: " + afd.getEstadoInicial() + "\n" +
                    "Estados finales: " + afd.getEstadosFinales());

        } catch (Exception e) {
            mostrarError("Error al construir AFD: " + e.getMessage());
        }
    }

    private void visualizarAFD() {
        canvasPane.getChildren().clear();

        Set<String> estados = afd.getEstados();
        if (estados.isEmpty()) return;

        // Posicionar estados en círculo
        double centerX = canvasPane.getPrefWidth() / 2;
        double centerY = canvasPane.getPrefHeight() / 2;
        double radius = Math.min(centerX, centerY) - 60;

        Map<String, Point> posiciones = new HashMap<>();
        List<String> listaEstados = new ArrayList<>(estados);
        double angle = 0;
        double angleStep = 2 * Math.PI / listaEstados.size();

        for (String estado : listaEstados) {
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            posiciones.put(estado, new Point(x, y));
            angle += angleStep;
        }

        // Dibujar transiciones (líneas)
        Map<String, Map<Character, String>> trans = afd.getTransiciones();
        for (String origen : trans.keySet()) {
            for (Map.Entry<Character, String> entry : trans.get(origen).entrySet()) {
                String destino = entry.getValue();
                char simbolo = entry.getKey();

                Point p1 = posiciones.get(origen);
                Point p2 = posiciones.get(destino);

                if (p1 != null && p2 != null) {
                    Line line = new Line(p1.x, p1.y, p2.x, p2.y);
                    line.setStroke(Color.GRAY);
                    line.setStrokeWidth(2);
                    canvasPane.getChildren().add(line);

                    // Etiqueta de transición
                    Text label = new Text((p1.x + p2.x) / 2, (p1.y + p2.y) / 2, String.valueOf(simbolo));
                    label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    canvasPane.getChildren().add(label);
                }
            }
        }

        // Dibujar estados (círculos)
        for (String estado : listaEstados) {
            Point p = posiciones.get(estado);

            Circle circle = new Circle(p.x, p.y, 30);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);
            circle.setStrokeWidth(2);

            // Doble círculo para estados finales
            if (afd.getEstadosFinales().contains(estado)) {
                Circle innerCircle = new Circle(p.x, p.y, 25);
                innerCircle.setFill(Color.TRANSPARENT);
                innerCircle.setStroke(Color.DARKBLUE);
                innerCircle.setStrokeWidth(2);
                canvasPane.getChildren().add(innerCircle);
            }

            // Flecha para estado inicial
            if (estado.equals(afd.getEstadoInicial())) {
                Line arrow = new Line(p.x - 50, p.y, p.x - 30, p.y);
                arrow.setStroke(Color.GREEN);
                arrow.setStrokeWidth(3);
                canvasPane.getChildren().add(arrow);
            }

            canvasPane.getChildren().add(circle);

            Text text = new Text(p.x - 10, p.y + 5, estado);
            text.setStyle("-fx-font-weight: bold;");
            canvasPane.getChildren().add(text);
        }
    }

    private void procesarCadena() {
        String cadena = cadenaField.getText().trim();

        if (afd.getEstados().isEmpty()) {
            mostrarError("Primero debe construir el AFD");
            return;
        }

        boolean aceptada = afd.procesar(cadena);
        List<String> pasos = afd.obtenerPasoAPaso();

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");
        resultado.append("Paso a paso:\n");

        for (String paso : pasos) {
            resultado.append(paso).append("\n");
        }

        outputArea.setText(resultado.toString());
    }

    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar AFD");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                estadosField.setText(reader.readLine());
                alfabetoField.setText(reader.readLine());
                estadoInicialField.setText(reader.readLine());
                estadosFinalesField.setText(reader.readLine());

                StringBuilder transiciones = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    transiciones.append(line).append("\n");
                }
                transicionesArea.setText(transiciones.toString());

                outputArea.setText("✓ Archivo cargado exitosamente");
            } catch (IOException e) {
                mostrarError("Error al cargar archivo: " + e.getMessage());
            }
        }
    }

    private void guardarEnArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar AFD");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(estadosField.getText());
                writer.println(alfabetoField.getText());
                writer.println(estadoInicialField.getText());
                writer.println(estadosFinalesField.getText());
                writer.print(transicionesArea.getText());

                outputArea.setText("✓ Archivo guardado exitosamente");
            } catch (IOException e) {
                mostrarError("Error al guardar archivo: " + e.getMessage());
            }
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Clase auxiliar para posiciones
    private static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}