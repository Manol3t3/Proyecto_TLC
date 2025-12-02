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
 * Panel de interfaz para el modo AFD.
 * Organiza la UI en tres secciones: Definición (izquierda), Visualización (centro) y Resultados (derecha).
 */
public class AFDPanel extends BorderPane {

    // Objeto del modelo lógico del Autómata Finito Determinista.
    private AFD afd;
    // Área para mostrar el historial de procesamiento y resultados.
    private TextArea outputArea;
    // Panel de dibujo para la visualización gráfica del AFD.
    private Pane canvasPane;

    // Controles de entrada para definir la 5-tupla (Q, Σ, δ, q0, F)
    private TextField estadosField;
    private TextField alfabetoField;
    private TextField estadoInicialField;
    private TextField estadosFinalesField;
    // Área para introducir las transiciones δ
    private TextArea transicionesArea;
    // Campo para la cadena de prueba
    private TextField cadenaField;

    /**
     * Constructor. Inicializa el modelo y la UI.
     */
    public AFDPanel() {
        this.afd = new AFD();
        initUI();
    }

    /**
     * Configura el layout principal del BorderPane.
     */
    private void initUI() {
        setPadding(new Insets(15));

        // Panel izquierdo: Definición del AFD
        VBox leftPanel = createDefinitionPanel();

        // Panel central: Visualización y Controles de prueba
        VBox centerPanel = createVisualizationPanel();

        // Panel derecho: Resultados
        VBox rightPanel = createResultsPanel();

        // Configurar layout principal
        setLeft(leftPanel);
        setCenter(centerPanel);
        setRight(rightPanel);
    }

    /**
     * Crea el panel de la izquierda para la entrada de la definición del AFD.
     * @return VBox con todos los campos de entrada y botones de archivo.
     */
    private VBox createDefinitionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Definición del AFD");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Estados (Q)
        Label estadosLabel = new Label("Estados (separados por comas):");
        estadosField = new TextField();
        estadosField.setPromptText("q0,q1,q2");

        // Alfabeto (Σ)
        Label alfabetoLabel = new Label("Alfabeto (sin separadores):");
        alfabetoField = new TextField();
        alfabetoField.setPromptText("ab");

        // Estado inicial (q0)
        Label estadoInicialLabel = new Label("Estado inicial:");
        estadoInicialField = new TextField();
        estadoInicialField.setPromptText("q0");

        // Estados finales (F)
        Label estadosFinalesLabel = new Label("Estados finales (separados por comas):");
        estadosFinalesField = new TextField();
        estadosFinalesField.setPromptText("q2");

        // Transiciones (δ)
        Label transicionesLabel = new Label("Transiciones (formato: q0,a,q1):");
        transicionesArea = new TextArea();
        transicionesArea.setPromptText("q0,a,q1\nq1,b,q2\nq2,a,q0");
        transicionesArea.setPrefRowCount(5);

        // Botón principal para construir el modelo.
        Button construirBtn = new Button("Construir AFD");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        construirBtn.setOnAction(e -> construirAFD());

        // Botones de archivo (Cargar/Guardar)
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

    /**
     * Crea el panel central para la visualización gráfica y los controles de prueba.
     * @return VBox con el panel de dibujo y la caja de prueba de cadenas.
     */
    private VBox createVisualizationPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Visualización del Autómata");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Contenedor principal para el grafo del AFD.
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

    /**
     * Crea el panel de la derecha para mostrar los resultados de la simulación.
     * @return VBox con el área de texto de salida y el botón de limpieza.
     */
    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Resultado de la Simulación");
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

    /**
     * Parsea los datos de la UI y construye el objeto AFD del modelo lógico.
     * Llama a la función de visualización tras el éxito.
     */
    private void construirAFD() {
        try {
            afd = new AFD();

            // Agregar estados (Q)
            String[] estados = estadosField.getText().trim().split(",");
            for (String estado : estados) {
                if (!estado.isEmpty()) afd.agregarEstado(estado.trim());
            }

            // Agregar alfabeto (Σ)
            String alfabeto = alfabetoField.getText().trim();
            for (char c : alfabeto.toCharArray()) {
                afd.agregarSimbolo(c);
            }

            // Establecer estado inicial (q0)
            String estadoInicial = estadoInicialField.getText().trim();
            if (!estadoInicial.isEmpty()) afd.setEstadoInicial(estadoInicial);

            // Agregar estados finales (F)
            String[] finales = estadosFinalesField.getText().trim().split(",");
            for (String estado : finales) {
                if (!estado.isEmpty()) afd.agregarEstadoFinal(estado.trim());
            }

            // Agregar transiciones (δ)
            String[] transiciones = transicionesArea.getText().trim().split("\n");
            for (String trans : transiciones) {
                String[] partes = trans.trim().split(",");
                if (partes.length == 3) {
                    // Formato: q_origen, simbolo, q_destino
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
     * Dibuja el diagrama de estados (grafo) del AFD en el canvasPane.
     */
    private void visualizarAFD() {
        canvasPane.getChildren().clear();

        Set<String> estados = afd.getEstados();
        if (estados.isEmpty()) return;

        // --- 1. CÁLCULO DE POSICIONES ---
        double centerX = canvasPane.getPrefWidth() / 2;
        double centerY = canvasPane.getPrefHeight() / 2;
        double graphRadius = Math.min(centerX, centerY) - 80;
        final double STATE_RADIUS = 30;
        final double RECIPROCAL_CURVE_MAGNITUDE = 0.5; // Factor de curvatura

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

        // --- 2. DIBUJAR TRANSICIONES (ARCOS O BUCLES) ---
        Map<String, Map<Character, String>> transicionesAFD = afd.getTransiciones();

        // 2a. Agrupar transiciones por origen y destino para concatenar los símbolos.
        Map<String, List<Character>> groupedSymbols = new HashMap<>();
        for (String origen : transicionesAFD.keySet()) {
            for (Map.Entry<Character, String> entry : transicionesAFD.get(origen).entrySet()) {
                String destino = entry.getValue();
                char simbolo = entry.getKey();
                // Key: "origen-destino"
                String key = origen + "-" + destino;
                groupedSymbols.computeIfAbsent(key, k -> new ArrayList<>()).add(simbolo);
            }
        }

        // 2b. Determinar qué arcos son recíprocos y asignarles una curvatura fija.
        // Key: "qA-qB" o "qB-qA" (solo se almacena el par ordenado lexicográficamente menor)
        Map<String, Boolean> reciprocalPairs = new HashMap<>();
        for (String key : groupedSymbols.keySet()) {
            String[] estadosPair = key.split("-");
            String origen = estadosPair[0];
            String destino = estadosPair[1];

            // Ignorar bucles y casos donde ya se verificó el par.
            if (origen.equals(destino)) continue;

            String reverseKey = destino + "-" + origen;

            // Verificar si el arco opuesto existe
            if (groupedSymbols.containsKey(reverseKey)) {
                // Crear una clave canónica (lexicográficamente menor primero) para el par
                String canonicalKey = origen.compareTo(destino) < 0 ? key : reverseKey;
                reciprocalPairs.put(canonicalKey, true);
            }
        }

        // 2c. Dibujar las transiciones.
        for (Map.Entry<String, List<Character>> entry : groupedSymbols.entrySet()) {
            String key = entry.getKey();
            String[] estadosPair = key.split("-");
            String origen = estadosPair[0];
            String destino = estadosPair[1];

            // Formatear símbolos: "a,b,c"
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
                // Dibujar bucles (self-loops)
                drawSelfLoop(canvasPane, p1, labelText, STATE_RADIUS);
            } else {
                // Dibujar arcos (transiciones entre estados distintos)
                double bendFactor = 0.0;
                boolean isReciprocal = groupedSymbols.containsKey(destino + "-" + origen);

                if (isReciprocal) {
                    // Si es recíproca, asignamos una curvatura:
                    // La que va de la clave lexicográficamente menor (qA) a la mayor (qB) es positiva (+).
                    // La que va de la clave lexicográficamente mayor (qB) a la menor (qA) es negativa (-).
                    if (origen.compareTo(destino) < 0) {
                        bendFactor = RECIPROCAL_CURVE_MAGNITUDE;
                    } else {
                        bendFactor = -RECIPROCAL_CURVE_MAGNITUDE;
                    }
                } else {
                    // Transición unidireccional (línea recta)
                    bendFactor = 0.0;
                }

                // Cálculo del punto de control de la curva de Bézier.
                Point controlPoint = calculateControlPoint(p1, p2, bendFactor, 0);

                // Puntos de inicio y fin ajustados al borde del círculo.
                Point startPoint = getPointOnCircle(p1, controlPoint, STATE_RADIUS);
                Point endPoint = getPointOnCircle(p2, controlPoint, STATE_RADIUS);

                // DIBUJAR EL ARCO (QuadCurveTo)
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

                // Ajuste vertical: La etiqueta debe colocarse *sobre* el arco, no sobre el punto de control.
                // Si la curvatura es positiva, la etiqueta va arriba/izquierda. Si es negativa, abajo/derecha.
                // Usamos un ligero desplazamiento perpendicular al arco.
                double labelYOffset = (bendFactor == 0.0) ? -5 : (bendFactor > 0) ? -15 : 15;

                // Calcular la dirección perpendicular de la etiqueta para que siga la curva
                double dx = controlPoint.x - labelPos.x;
                double dy = controlPoint.y - labelPos.y;
                double angleOfLabel = Math.atan2(dy, dx); // Ángulo entre punto medio y punto de control

                // Aplicar el desplazamiento
                label.setX(labelPos.x + labelYOffset * Math.cos(angleOfLabel + Math.PI / 2));
                label.setY(labelPos.y + labelYOffset * Math.sin(angleOfLabel + Math.PI / 2));

                label.setTranslateX(-(label.getLayoutBounds().getWidth() / 2));
                label.setTranslateY(-(label.getLayoutBounds().getHeight() / 2));

                canvasPane.getChildren().add(label);
            }
        }

        // --- 3. DIBUJAR ESTADOS (CÍRCULOS) Y ETIQUETAS ---
        for (String estado : listaEstados) {
            Point p = posiciones.get(estado);

            // Círculo principal del estado.
            Circle circle = new Circle(p.x, p.y, STATE_RADIUS);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);
            circle.setStrokeWidth(2);
            canvasPane.getChildren().add(circle);

            // Doble círculo para estados finales (F).
            if (afd.getEstadosFinales().contains(estado)) {
                Circle innerCircle = new Circle(p.x, p.y, STATE_RADIUS - 5);
                innerCircle.setFill(Color.TRANSPARENT);
                innerCircle.setStroke(Color.DARKBLUE);
                innerCircle.setStrokeWidth(2);
                canvasPane.getChildren().add(innerCircle);
            }

            // Flecha para estado inicial (q0).
            if (estado.equals(afd.getEstadoInicial())) {

                // Dibuja una flecha entrando desde la izquierda (ángulo 180 grados).
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

            // Etiqueta del estado (ej. "q0").
            Text text = new Text(p.x - (estado.length() * 4), p.y + 5, estado);
            text.setStyle("-fx-font-weight: bold;");
            canvasPane.getChildren().add(text);
        }
    }

    /**
     * Procesa la cadena de entrada utilizando el modelo AFD y muestra el resultado y los pasos.
     * Se ha corregido para mostrar el mensaje de ACEPTADA/RECHAZADA detallado que viene del modelo.
     */
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

        // Ejecuta el procesamiento, el resultado detallado queda almacenado en el AFD.
        afd.procesar(cadena);
        List<String> pasos = afd.obtenerPasoAPaso();

        // Formatea el resultado para el outputArea.
        StringBuilder resultado = new StringBuilder();

        // Agrega el resumen de la cadena y el separador.
        resultado.append("═══════════════════════════════\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");
        resultado.append("Paso a paso:\n");

        // Agrega cada paso, incluyendo el mensaje final de ACEPTADA/RECHAZADA detallado.
        for (String paso : pasos) {
            resultado.append(paso).append("\n");
        }

        outputArea.setText(resultado.toString());
    }

    /**
     * Carga la definición del AFD desde un archivo de texto.
     */
    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar AFD");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Lectura secuencial de los campos, asumiendo un orden fijo.
                estadosField.setText(reader.readLine());
                alfabetoField.setText(reader.readLine());
                estadoInicialField.setText(reader.readLine());
                estadosFinalesField.setText(reader.readLine());

                // Lectura de las transiciones (el resto del archivo).
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

    /**
     * Guarda la definición actual del AFD en un archivo de texto.
     */
    private void guardarEnArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar AFD");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Escritura secuencial de los campos, manteniendo el orden de carga.
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

    /**
     * Muestra un diálogo de alerta de error.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // MÉTODOS Y CLASES AUXILIARES PARA EL DIBUJO DEL GRAFO
    /**
     * Clase auxiliar para las coordenadas de un punto (x, y).
     */
    private static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Calcula el punto en el borde de un círculo (estado) hacia un punto objetivo (flecha).
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
     * Calcula un punto de control P1 para una curva cuadrática de Bézier (P0, P1, P2),
     * utilizado para curvar las transiciones, especialmente las recíprocas.
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
     * Calcula el punto en el medio (t=0.5) de una curva cuadrática de Bézier.
     * Esto se usa para posicionar la etiqueta de la transición.
     */
    private Point calculateMidpointOnQuadCurve(Point p0, Point p1, Point p2) {
        double t = 0.5;
        double oneMinusT = 1 - t;

        // Fórmula de la curva de Bézier cuadrática: B(t) = (1-t)^2 P0 + 2(1-t)t P1 + t^2 P2
        double x = oneMinusT * oneMinusT * p0.x + 2 * oneMinusT * t * p1.x + t * t * p2.x;
        double y = oneMinusT * oneMinusT * p0.y + 2 * oneMinusT * t * p1.y + t * t * p2.y;

        return new Point(x, y);
    }

    /**
     * Dibuja un bucle (transición de un estado a sí mismo).
     */
    private void drawSelfLoop(Pane pane, Point center, String labelText, double stateRadius) {
        double controlOffset = stateRadius * 2.5;

        // Puntos de entrada y salida del bucle en el borde superior del círculo.
        double startAngle = Math.toRadians(250);
        Point startLoop = new Point(center.x + stateRadius * Math.cos(startAngle),
                center.y + stateRadius * Math.sin(startAngle));

        double endAngle = Math.toRadians(290);

        Point endLoop = new Point(center.x + stateRadius * Math.cos(endAngle),
                center.y + stateRadius * Math.sin(endAngle));


        // Punto de control para el arco (arriba del estado).
        Point controlLoop = new Point(center.x, center.y - controlOffset);

        // Dibuja el arco del bucle.
        Path loopPath = new Path();
        loopPath.getElements().add(new MoveTo(startLoop.x, startLoop.y));
        loopPath.getElements().add(new QuadCurveTo(controlLoop.x, controlLoop.y, endLoop.x, endLoop.y));
        loopPath.setStroke(Color.GRAY);
        loopPath.setStrokeWidth(2);
        pane.getChildren().add(loopPath);

        // Añade la punta de flecha.
        addArrowHead(pane, endLoop, controlLoop, 8.0);

        // Etiqueta del bucle: se posiciona en el punto medio de la curva con un desplazamiento.
        Point labelPos = calculateMidpointOnQuadCurve(startLoop, controlLoop, endLoop);
        Text label = new Text(labelPos.x, labelPos.y - 10, labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        label.setFill(Color.RED);
        label.setTranslateX(-(label.getLayoutBounds().getWidth() / 2)); // Centrar
        pane.getChildren().add(label);
    }


    /**
     * Añade una punta de flecha al final de una línea o curva con un color específico.
     */
    private void addArrowHead(Pane pane, Point tip, Point tail, double size, Color color) {
        double dx = tip.x - tail.x;
        double dy = tip.y - tail.y;
        double angle = Math.atan2(dy, dx); // Ángulo de la línea

        // Cálculo de los dos puntos traseros de la flecha (con ángulo ± π/6)
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

    /**
     * Sobrecarga para el color por defecto (gris) de las transiciones.
     */
    private void addArrowHead(Pane pane, Point tip, Point tail, double size) {
        addArrowHead(pane, tip, tail, size, Color.GRAY);
    }
}