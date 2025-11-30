package org.example.Ui;

import org.example.Modelo.AFD;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
                if (!estado.isEmpty()) afd.agregarEstado(estado.trim());
            }

            // Agregar alfabeto
            String alfabeto = alfabetoField.getText().trim();
            for (char c : alfabeto.toCharArray()) {
                afd.agregarSimbolo(c);
            }

            // Establecer estado inicial
            String estadoInicial = estadoInicialField.getText().trim();
            if (!estadoInicial.isEmpty()) afd.setEstadoInicial(estadoInicial);

            // Agregar estados finales
            String[] finales = estadosFinalesField.getText().trim().split(",");
            for (String estado : finales) {
                if (!estado.isEmpty()) afd.agregarEstadoFinal(estado.trim());
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

    /**
     * Dibuja las transiciones como arcos, maneja bucles y transiciones de ida y vuelta.
     * Agrupa las etiquetas de los símbolos por cada arco (origen-destino).
     * CORREGIDO: Ajustes de curvatura para transiciones recíprocas y unidireccionales.
     */
    private void visualizarAFD() {
        canvasPane.getChildren().clear();

        Set<String> estados = afd.getEstados();
        if (estados.isEmpty()) return;

        // Posicionar estados en círculo (Mantiene la distribución anterior)
        double centerX = canvasPane.getPrefWidth() / 2;
        double centerY = canvasPane.getPrefHeight() / 2;
        double graphRadius = Math.min(centerX, centerY) - 80;
        final double STATE_RADIUS = 30;

        Map<String, Point> posiciones = new HashMap<>();
        List<String> listaEstados = new ArrayList<>(estados);
        double angle = 0;
        double angleStep = 2 * Math.PI / listaEstados.size();

        for (String estado : listaEstados) {
            double x = centerX + graphRadius * Math.cos(angle);
            double y = centerY + graphRadius * Math.sin(angle);
            posiciones.put(estado, new Point(x, y));
            angle += angleStep;
        }

        // DIBUJAR TRANSICIONES (ARCOS O BUCLES)
        Map<String, Map<Character, String>> transicionesAFD = afd.getTransiciones();

        // Agrupar transiciones por origen y destino, concatenando los símbolos.
        Map<String, List<Character>> groupedSymbols = new HashMap<>();
        for (String origen : transicionesAFD.keySet()) {
            for (Map.Entry<Character, String> entry : transicionesAFD.get(origen).entrySet()) {
                String destino = entry.getValue();
                char simbolo = entry.getKey();
                String key = origen + "-" + destino;
                groupedSymbols.computeIfAbsent(key, k -> new ArrayList<>()).add(simbolo);
            }
        }

        for (Map.Entry<String, List<Character>> entry : groupedSymbols.entrySet()) {
            String[] estadosPair = entry.getKey().split("-");
            String origen = estadosPair[0];
            String destino = estadosPair[1];

            // Concatenar símbolos para la etiqueta
            String labelText = entry.getValue().stream()
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.joining(","));

            Point p1 = posiciones.get(origen);
            Point p2 = posiciones.get(destino);

            if (p1 == null || p2 == null) {
                mostrarError("Estado no encontrado para transición: " + origen + " o " + destino);
                continue;
            }

            if (origen.equals(destino)) {
                drawSelfLoop(canvasPane, p1, labelText, STATE_RADIUS);
            } else {

                boolean hasReverse = groupedSymbols.containsKey(destino + "-" + origen);
                double bendFactor = 0.0;

                // Lógica de curvatura:
                if (hasReverse) {
                    // *** CORRECCIÓN CLAVE 1: Aumentar la curvatura ligeramente para separar arcos ***
                    final double RECIPROCAL_CURVE_MAGNITUDE = 0.7;

                    if (origen.compareTo(destino) < 0) {
                        bendFactor = RECIPROCAL_CURVE_MAGNITUDE;
                    } else {
                        bendFactor = -RECIPROCAL_CURVE_MAGNITUDE;
                    }
                } else {
                    // Se mantiene en 0.0 para que las transiciones unidireccionales sean rectas.
                    bendFactor = 0.0;
                }

                Point controlPoint = calculateControlPoint(p1, p2, bendFactor, 0);

                Point startPoint = getPointOnCircle(p1, controlPoint, STATE_RADIUS);
                Point endPoint = getPointOnCircle(p2, controlPoint, STATE_RADIUS);

                // DIBUJAR EL ARCO
                Path path = new Path();
                path.getElements().add(new MoveTo(startPoint.x, startPoint.y));
                path.getElements().add(new QuadCurveTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y));
                path.setStroke(Color.GRAY);
                path.setStrokeWidth(2);
                canvasPane.getChildren().add(path);

                // AÑADIR PUNTA DE FLECHA
                addArrowHead(canvasPane, endPoint, controlPoint, 8.0);

                // ETIQUETA DE TRANSICIÓN
                Point labelPos = calculateMidpointOnQuadCurve(startPoint, controlPoint, endPoint);
                Text label = new Text(labelPos.x, labelPos.y, labelText);
                label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                label.setFill(Color.RED);

                // *** CORRECCIÓN CLAVE 2: Aumentar el desplazamiento vertical para evitar superposición con la flecha opuesta ***
                double labelYOffset = hasReverse ? -28 : -5;

                label.setTranslateX(-(label.getLayoutBounds().getWidth() / 2));
                label.setTranslateY(-(label.getLayoutBounds().getHeight() / 2) + labelYOffset);

                canvasPane.getChildren().add(label);
            }
        }

        // DIBUJAR ESTADOS (CÍRCULOS)
        for (String estado : listaEstados) {
            Point p = posiciones.get(estado);

            Circle circle = new Circle(p.x, p.y, STATE_RADIUS);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);
            circle.setStrokeWidth(2);
            canvasPane.getChildren().add(circle);

            // Doble círculo para estados finales
            if (afd.getEstadosFinales().contains(estado)) {
                Circle innerCircle = new Circle(p.x, p.y, STATE_RADIUS - 5);
                innerCircle.setFill(Color.TRANSPARENT);
                innerCircle.setStroke(Color.DARKBLUE);
                innerCircle.setStrokeWidth(2);
                canvasPane.getChildren().add(innerCircle);
            }

            // Flecha para estado inicial (entrada)
            if (estado.equals(afd.getEstadoInicial())) {

                // Flecha de estado inicial entrando desde la izquierda (posición tradicional)
                double arrowLength = 50;
                final double ANGLE_RAD = Math.toRadians(180);

                Point targetPoint = new Point(
                        p.x + STATE_RADIUS * Math.cos(ANGLE_RAD),
                        p.y + STATE_RADIUS * Math.sin(ANGLE_RAD)
                );

                Point startPoint = new Point(
                        p.x + (STATE_RADIUS + arrowLength) * Math.cos(ANGLE_RAD),
                        p.y + (STATE_RADIUS + arrowLength) * Math.sin(ANGLE_RAD)
                );

                Line arrowLine = new Line(startPoint.x, startPoint.y, targetPoint.x, targetPoint.y);
                arrowLine.setStroke(Color.GREEN);
                arrowLine.setStrokeWidth(3);
                canvasPane.getChildren().add(arrowLine);

                addArrowHead(canvasPane, targetPoint, startPoint, 8.0, Color.GREEN);
            }

            // Etiqueta del estado (ej. "q0")
            Text text = new Text(p.x - (estado.length() * 4), p.y + 5, estado);
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
        if (afd.getEstadoInicial() == null || afd.getEstadoInicial().isEmpty()) {
            mostrarError("Defina el estado inicial antes de procesar una cadena.");
            return;
        }

        boolean aceptada = afd.procesar(cadena);
        List<String> pasos = afd.obtenerPasoAPaso();

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append(aceptada ? "Resultado: ACEPTADA\n" : "Resultado: RECHAZADA\n");
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

    // =========================================================
    // MÉTODOS Y CLASES AUXILIARES
    // =========================================================

    /**
     * Clase auxiliar para las coordenadas de un punto.
     */
    private static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        double distance(Point other) {
            return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
        }
    }

    /**
     * Clase auxiliar para almacenar información de la transición.
     */
    private static class TransitionInfo {
        String origen;
        String destino;
        char simbolo;

        TransitionInfo(String origen, String destino, char simbolo) {
            this.origen = origen;
            this.destino = destino;
            this.simbolo = simbolo;
        }
    }


    /**
     * Calcula el punto en la circunferencia de un círculo que está en la dirección
     * de un punto externo (o un punto de control para una curva).
     */
    private Point getPointOnCircle(Point circleCenter, Point targetPoint, double radius) {
        double dx = targetPoint.x - circleCenter.x;
        double dy = targetPoint.y - circleCenter.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) return circleCenter;

        double ratio = radius / dist;
        double x = circleCenter.x + (dx * ratio);
        double y = circleCenter.y + (dy * ratio);

        return new Point(x, y);
    }

    /**
     * Calcula un punto de control para curvar una transición entre dos estados.
     */
    private Point calculateControlPoint(Point p1, Point p2, double bendFactor, double offsetMultiplier) {
        double midX = (p1.x + p2.x) / 2;
        double midY = (p1.y + p2.y) / 2;

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;

        // Vector perpendicular (normal) a la línea p1-p2
        double normalDx = -dy;
        double normalDy = dx;

        // Normalizar el vector
        double length = Math.sqrt(normalDx * normalDx + normalDy * normalDy);
        if (length == 0) return new Point(midX, midY);

        normalDx /= length;
        normalDy /= length;

        // Calcular punto de control desplazado
        double offsetDistance = length * bendFactor;

        return new Point(midX + normalDx * offsetDistance, midY + normalDy * offsetDistance);
    }

    /**
     * Calcula el punto medio de una curva cuadrática de Bézier.
     */
    private Point calculateMidpointOnQuadCurve(Point p0, Point p1, Point p2) {
        double t = 0.5;
        double oneMinusT = 1 - t;

        double x = oneMinusT * oneMinusT * p0.x + 2 * oneMinusT * t * p1.x + t * t * p2.x;
        double y = oneMinusT * oneMinusT * p0.y + 2 * oneMinusT * t * p1.y + t * t * p2.y;

        return new Point(x, y);
    }

    /**
     * Dibuja un bucle (self-loop) mejorado.
     */
    private void drawSelfLoop(Pane pane, Point center, String labelText, double stateRadius) {
        double controlOffset = stateRadius * 2.5;

        // Puntos de entrada y salida del bucle en el borde superior del círculo
        double startAngle = Math.toRadians(250);
        Point startLoop = new Point(center.x + stateRadius * Math.cos(startAngle),
                center.y + stateRadius * Math.sin(startAngle));

        double endAngle = Math.toRadians(290);

        Point endLoop = new Point(center.x + stateRadius * Math.cos(endAngle),
                center.y + stateRadius * Math.sin(endAngle));


        // Punto de control para el arco (más arriba)
        Point controlLoop = new Point(center.x, center.y - controlOffset);

        Path loopPath = new Path();
        loopPath.getElements().add(new MoveTo(startLoop.x, startLoop.y));
        loopPath.getElements().add(new QuadCurveTo(controlLoop.x, controlLoop.y, endLoop.x, endLoop.y));
        loopPath.setStroke(Color.GRAY);
        loopPath.setStrokeWidth(2);
        pane.getChildren().add(loopPath);

        // Añadir punta de flecha al final del bucle
        addArrowHead(pane, endLoop, controlLoop, 8.0);

        // Etiqueta del bucle: un poco debajo del punto de control
        Point labelPos = calculateMidpointOnQuadCurve(startLoop, controlLoop, endLoop);
        Text label = new Text(labelPos.x, labelPos.y - 10, labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        label.setFill(Color.RED); // Etiqueta roja
        // Centrar la etiqueta horizontalmente
        label.setTranslateX(-(label.getLayoutBounds().getWidth() / 2));
        pane.getChildren().add(label);
    }


    /**
     * Añade una punta de flecha al final de una línea o curva con un color específico.
     */
    private void addArrowHead(Pane pane, Point tip, Point tail, double size, Color color) {
        double dx = tip.x - tail.x;
        double dy = tip.y - tail.y;
        double angle = Math.atan2(dy, dx);

        Point p1 = new Point(
                tip.x - size * Math.cos(angle - Math.PI / 6),
                tip.y - size * Math.sin(angle - Math.PI / 6)
        );
        Point p2 = new Point(
                tip.x - size * Math.cos(angle + Math.PI / 6),
                tip.y - size * Math.sin(angle + Math.PI / 6)
        );

        Line arrow1 = new Line(tip.x, tip.y, p1.x, p1.y);
        Line arrow2 = new Line(tip.x, tip.y, p2.x, p2.y);

        arrow1.setStroke(color);
        arrow2.setStroke(color);
        arrow1.setStrokeWidth(2);
        arrow2.setStrokeWidth(2);

        pane.getChildren().addAll(arrow1, arrow2);
    }

    // Sobrecarga para el color por defecto (gris)
    private void addArrowHead(Pane pane, Point tip, Point tail, double size) {
        addArrowHead(pane, tip, tail, size, Color.GRAY);
    }
}