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

        double w = canvasPane.getWidth() > 0 ? canvasPane.getWidth() : canvasPane.getPrefWidth();
        double h = canvasPane.getHeight() > 0 ? canvasPane.getHeight() : canvasPane.getPrefHeight();
        double centerX = w / 2;
        double centerY = h / 2;
        double graphRadius = Math.min(centerX, centerY) - 80;
        final double STATE_RADIUS = 30;

        Map<String, Point> pos = new HashMap<>();
        List<String> list = new ArrayList<>(estados);
        double angleStep = 2 * Math.PI / list.size();

        // Colocar estados en círculo
        for (int i = 0; i < list.size(); i++) {
            double angle = i * angleStep;
            double x = centerX + graphRadius * Math.cos(angle);
            double y = centerY + graphRadius * Math.sin(angle);
            pos.put(list.get(i), new Point(x, y));
        }

        // Agrupar transiciones
        Map<String, Map<Character, List<String>>> tmap = afd.getTransicionesMultiples();
        Map<String, List<Character>> group = new HashMap<>();

        for (String origen : tmap.keySet()) {
            for (Character simbolo : tmap.get(origen).keySet()) {
                for (String destino : tmap.get(origen).get(simbolo)) {
                    String key = origen + "-" + destino;
                    group.computeIfAbsent(key, k -> new ArrayList<>()).add(simbolo);
                }
            }
        }

        //  DIBUJAR TODAS LAS TRANSICIONES
        for (String key : group.keySet()) {

            String[] parts = key.split("-");
            String origen = parts[0];
            String destino = parts[1];

            List<Character> simbolos = group.get(key);

            Point p1 = pos.get(origen);
            Point p2 = pos.get(destino);

            String etiqueta = simbolos.stream()
                    .distinct()
                    .sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            //   DIBUJAR BUCLE
            if (origen.equals(destino)) {
                drawSelfLoop(canvasPane, p1, etiqueta, STATE_RADIUS, 0);
                continue;
            }

            // DETECTAR SI ES RECÍPROCA
            String reverseKey = destino + "-" + origen;
            boolean reciproca = group.containsKey(reverseKey);

            // Detectar si existe transición recíproca
            double bend = 0.0;

            if (reciproca) {

                // Vector base p1 -> p2
                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;

                // Vector perpendicular normalizado
                double nx = -dy;
                double ny = dx;

                double len = Math.sqrt(nx * nx + ny * ny);
                if (len != 0) {
                    nx /= len;
                    ny /= len;
                }

                // Signo según lado geométrico real
                double cross = dx * ny - dy * nx;

                // Curvatura reducida
                bend = (cross >= 0) ? 0.35 : -0.35;
            }


            // Calcular curva
            Point control = calculateControlPoint(p1, p2, bend, 0);
            Point start = getPointOnCircle(p1, control, STATE_RADIUS);
            Point end = getPointOnCircle(p2, control, STATE_RADIUS);

            Path path = new Path();
            path.getElements().add(new MoveTo(start.x, start.y));
            path.getElements().add(new QuadCurveTo(control.x, control.y, end.x, end.y));
            path.setStroke(Color.GRAY);
            path.setStrokeWidth(2);
            canvasPane.getChildren().add(path);

            addArrowHead(canvasPane, end, control, 8.0);

            // Etiqueta en el punto medio
            Point mid = calculateMidpointOnQuadCurve(start, control, end);
            double offset = reciproca ? 18 : 10;

            Text t = new Text(mid.x, mid.y - offset, etiqueta);
            t.setFill(Color.RED);
            t.setStyle("-fx-font-weight: bold;");
            t.setTranslateX(-t.getLayoutBounds().getWidth() / 2);
            canvasPane.getChildren().add(t);

        }

        //     DIBUJAR ESTADOS
        for (String estado : list) {
            Point p = pos.get(estado);

            Circle c = new Circle(p.x, p.y, STATE_RADIUS);
            c.setFill(Color.LIGHTBLUE);
            c.setStroke(Color.DARKBLUE);
            c.setStrokeWidth(2);
            canvasPane.getChildren().add(c);

            if (afd.getEstadosFinales().contains(estado)) {
                Circle inner = new Circle(p.x, p.y, STATE_RADIUS - 5);
                inner.setFill(Color.TRANSPARENT);
                inner.setStroke(Color.DARKBLUE);
                inner.setStrokeWidth(2);
                canvasPane.getChildren().add(inner);
            }

            // Flecha del estado inicial
            if (estado.equals(afd.getEstadoInicial())) {
                Point src = new Point(p.x - 60, p.y);
                Line arrow = new Line(src.x, src.y, p.x - STATE_RADIUS, p.y);
                arrow.setStroke(Color.GREEN);
                arrow.setStrokeWidth(3);
                canvasPane.getChildren().add(arrow);

                addArrowHead(canvasPane, new Point(p.x - STATE_RADIUS, p.y), src, 8, Color.GREEN);
            }

            Text name = new Text(p.x - (estado.length() * 4), p.y + 5, estado);
            name.setStyle("-fx-font-weight: bold;");
            canvasPane.getChildren().add(name);
        }
    }

    /**
     * Dibuja una etiqueta de transición avanzada en el panel.
     * Esta etiqueta incluye un texto (símbolos) con un fondo blanco
     *
     * @param pane El contenedor (Pane) donde se dibujará la etiqueta.
     * @param mid El punto medio (coordenadas) donde se centrará la etiqueta.
     * @param textValue El texto a mostrar (los símbolos de la transición, ej: "a,b").
     * @param reciproca Indica si la transición es parte de un par recíproco, lo que ajusta la posición vertical para evitar superposiciones.
     */
    private void drawAdvancedLabel(Pane pane, Point mid, String textValue, boolean reciproca) {

        // 1. Cálculo del Offset (Desplazamiento)
        // Ajusta la distancia vertical de la etiqueta desde el centro de la curva.
        // Si es recíproca, se desplaza más (7px) para separarse de la etiqueta de la transición inversa.
        double offset = reciproca ? 7 : 4;

        // 2. Creación y Estilización del Texto
        Text text = new Text(textValue);
        text.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        text.setFill(Color.RED);

        // 3. Cálculo de Dimensiones del Fondo
        double padding = 3; // Espacio entre el texto y el borde del recuadro
        // Se calcula el ancho y alto que necesita el fondo para contener el texto más el padding.
        double width = text.getLayoutBounds().getWidth() + padding * 2;
        double height = text.getLayoutBounds().getHeight() + padding * 2;

        // 4. Creación del Fondo (Rectangle)
        javafx.scene.shape.Rectangle background =
                new javafx.scene.shape.Rectangle(width, height);
        background.setFill(Color.WHITE); // Fondo blanco
        background.setStroke(Color.DARKGRAY);
        background.setStrokeWidth(0.4);
        background.setArcWidth(8); // Esquinas redondeadas (Horizontal)
        background.setArcHeight(8); // Esquinas redondeadas (Vertical)

        // 5. Agrupación y Posicionamiento
        // Agrupa el fondo y el texto para tratarlos como una sola unidad.
        javafx.scene.Group group = new javafx.scene.Group(background, text);

        // Centra el grupo horizontalmente en 'mid.x'
        group.setLayoutX(mid.x - width / 2);
        // Centra el grupo verticalmente en 'mid.y' y aplica el 'offset' hacia arriba.
        group.setLayoutY(mid.y - offset - height / 2);

        // 6. Añadir al Panel
        pane.getChildren().add(group);
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
    private void drawSelfLoop(Pane pane, Point center, String labelText,
                              double stateRadius, double angleOffset) {
        double controlDist = stateRadius * 2.2;
        double baseAngle = Math.toRadians(270) + angleOffset;
        double startAngle = baseAngle - Math.toRadians(30);
        double endAngle   = baseAngle + Math.toRadians(30);

        Point start = new Point(center.x + stateRadius * Math.cos(startAngle),
                center.y + stateRadius * Math.sin(startAngle));
        Point end   = new Point(center.x + stateRadius * Math.cos(endAngle),
                center.y + stateRadius * Math.sin(endAngle));
        Point control = new Point(center.x + controlDist * Math.cos(baseAngle),
                center.y + controlDist * Math.sin(baseAngle));

        Path loop = new Path();
        loop.getElements().add(new MoveTo(start.x, start.y));
        loop.getElements().add(new QuadCurveTo(control.x, control.y, end.x, end.y));
        loop.setStroke(Color.GRAY);
        loop.setStrokeWidth(2);
        pane.getChildren().add(loop);

        addArrowHead(pane, end, control, 8.0);

        Point labelPos = calculateMidpointOnQuadCurve(start, control, end);
        Text label = new Text(labelPos.x, labelPos.y - 10, labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        label.setFill(Color.RED);
        label.setTranslateX(-label.getLayoutBounds().getWidth() / 2);
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