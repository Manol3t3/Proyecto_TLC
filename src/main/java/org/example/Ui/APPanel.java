package org.example.Ui;

import org.example.Modelo.AutomataDePila;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.List;

/**
 * Panel de interfaz para Autómatas de Pila
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
        Label formatoLabel2 = new Label("(usar 'e' para epsilon)");
        formatoLabel2.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        transicionesArea = new TextArea();
        transicionesArea.setPromptText("q0,a,Z,q0,XZ\nq0,e,Z,q1,e");
        transicionesArea.setPrefRowCount(6);

        Label ejemploLabel = new Label(
                "Ejemplo a^n b^n:\n" +
                        "q0,a,Z,q0,XZ\n" +
                        "q0,a,X,q0,XX\n" +
                        "q0,b,X,q1,e\n" +
                        "q1,b,X,q1,e\n" +
                        "q1,e,Z,q2,e"
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
                construirBtn,
                fileButtons
        );

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        VBox container = new VBox(scrollPane);
        return container;
    }

    private VBox createVisualizationPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);

        Label automataLabel = new Label("AUTÓMATA DE PILA");
        automataLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        automataPane = new Pane();
        automataPane.setPrefSize(420, 300);
        automataPane.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2;");

        Label pilaLabel = new Label("PILA");
        pilaLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        pilaPane = new Pane();
        pilaPane.setPrefSize(300, 200);
        pilaPane.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2;");

        VBox testBox = new VBox(8);
        testBox.setAlignment(Pos.CENTER);

        Label testLabel = new Label("Cadena:");
        testLabel.setStyle("-fx-font-weight: bold;");

        cadenaField = new TextField();
        cadenaField.setPrefWidth(250);

        Button procesarBtn = new Button("Procesar");
        procesarBtn.setOnAction(e -> procesarCadena());

        testBox.getChildren().addAll(testLabel, cadenaField, procesarBtn);

        panel.getChildren().addAll(automataLabel, automataPane, pilaLabel, pilaPane, testBox);
        return panel;
    }

    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Historial de Ejecución");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(30);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");

        Button limpiarBtn = new Button("Limpiar");
        limpiarBtn.setMaxWidth(Double.MAX_VALUE);
        limpiarBtn.setOnAction(e -> {
            outputArea.clear();
            pilaPane.getChildren().clear();
            automataPane.getChildren().clear();
        });

        panel.getChildren().addAll(titleLabel, new Separator(), outputArea, limpiarBtn);
        return panel;
    }

    private void construirAutomata() {
        try {
            ap = new AutomataDePila();

            // Estados
            String[] estados = estadosField.getText().trim().split(",");
            for (String estado : estados) {
                if (!estado.trim().isEmpty())
                    ap.agregarEstado(estado.trim());
            }

            // Alfabeto de entrada
            String alfabetoEntrada = alfabetoEntradaField.getText().trim();
            for (char c : alfabetoEntrada.toCharArray()) {
                if (c != ' ') ap.agregarSimboloEntrada(c);
            }

            // Alfabeto de pila
            String alfabetoPila = alfabetoPilaField.getText().trim();
            for (char c : alfabetoPila.toCharArray()) {
                if (c != ' ') ap.agregarSimboloPila(c);
            }

            // Estado inicial
            String estInit = estadoInicialField.getText().trim();
            if (!estInit.isEmpty()) ap.setEstadoInicial(estInit);

            // Símbolo inicial de pila
            String sInicial = simboloInicialPilaField.getText().trim();
            if (!sInicial.isEmpty()) ap.setSimboloInicialPila(sInicial.charAt(0));

            // Estados finales
            String[] finales = estadosFinalesField.getText().trim().split(",");
            for (String estado : finales) {
                if (!estado.trim().isEmpty())
                    ap.agregarEstadoFinal(estado.trim());
            }

            // Transiciones (formato: q0,a,Z,q1,XZ)
            String[] transiciones = transicionesArea.getText().trim().split("\n");
            for (String trans : transiciones) {
                if (trans.trim().isEmpty()) continue;

                String[] partes = trans.trim().split(",");
                if (partes.length != 5) continue;

                String estadoOrigen = partes[0].trim();
                String simboloEntrada = partes[1].trim();
                char simboloPila = partes[2].trim().charAt(0);
                String estadoDestino = partes[3].trim();
                String cadenaApilar = partes[4].trim();

                // 'e' representa epsilon
                Character simbolo = simboloEntrada.equals("e") ? null : simboloEntrada.charAt(0);
                String apilar = cadenaApilar.equals("e") ? "" : cadenaApilar;

                ap.agregarTransicion(estadoOrigen, simbolo, simboloPila, estadoDestino, apilar);
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append("✓ Autómata de Pila construido!\n\n");
            resultado.append("Estados: ").append(ap.getEstados()).append("\n");
            resultado.append("Alfabeto entrada: ").append(ap.getAlfabetoEntrada()).append("\n");
            resultado.append("Alfabeto pila: ").append(ap.getAlfabetoPila()).append("\n");
            resultado.append("Estado inicial: ").append(ap.getEstadoInicial()).append("\n");
            resultado.append("Símbolo inicial pila: ").append(ap.getSimboloInicialPila()).append("\n");
            resultado.append("Estados finales: ").append(ap.getEstadosFinales()).append("\n\n");
            resultado.append("Transiciones definidas: ").append(ap.getTransiciones().size());

            outputArea.setText(resultado.toString());

            // Dibuja el autómata usando la versión corregida
            dibujarAutomata();

        } catch (Exception e) {
            mostrarError("Error al construir autómata: " + e.getMessage());
        }
    }

    private void procesarCadena() {
        String cadena = cadenaField.getText().trim();

        if (ap.getEstados().isEmpty()) {
            mostrarError("Primero debe construir el autómata");
            return;
        }

        boolean aceptada = ap.procesar(cadena);
        List<AutomataDePila.ConfiguracionAP> historial = ap.getHistorial();

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("EJECUCIÓN DEL AUTÓMATA DE PILA\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        resultado.append("Configuraciones:\n");
        resultado.append(String.format("%-8s %-15s %-20s\n", "Paso", "Estado", "Pila"));
        resultado.append("─".repeat(50)).append("\n");

        for (int i = 0; i < historial.size(); i++) {
            AutomataDePila.ConfiguracionAP config = historial.get(i);
            resultado.append(String.format("%-8d %-15s %-20s\n",
                    i, config.estado, config.getPilaString()));
        }

        resultado.append("\n");
        if (aceptada) {
            resultado.append("✓ CADENA ACEPTADA\n");
        } else {
            resultado.append("✗ CADENA RECHAZADA\n");
        }

        outputArea.setText(resultado.toString());

        // Visualizar la pila final
        if (!historial.isEmpty()) {
            visualizarPila(historial.get(historial.size() - 1));
        }
    }

    private void dibujarAutomata() {
        automataPane.getChildren().clear();

        List<String> estados = new java.util.ArrayList<>(ap.getEstados());
        if (estados.isEmpty()) return;

        double cx = automataPane.getPrefWidth() / 2.0;
        double cy = automataPane.getPrefHeight() / 2.0;
        double radio = Math.min(cx, cy) - 60;
        if (radio < 40) radio = 80;

        java.util.Map<String, Circle> nodos = new java.util.HashMap<>();

        // dibujar nodos
        for (int i = 0; i < estados.size(); i++) {

            double ang = 2 * Math.PI * i / estados.size();
            double x = cx + radio * Math.cos(ang);
            double y = cy + radio * Math.sin(ang);

            Circle c = new Circle(x, y, 25);
            c.setFill(Color.LIGHTBLUE);
            c.setStroke(Color.DARKBLUE);
            c.setStrokeWidth(2);

            if (ap.getEstadosFinales().contains(estados.get(i))) {
                c.setStroke(Color.GREEN);
                c.setStrokeWidth(4);
            }

            if (ap.getEstadoInicial() != null && ap.getEstadoInicial().equals(estados.get(i))) {
                c.setFill(Color.LIGHTGREEN);
            }

            Text t = new Text(x - 10, y + 5, estados.get(i));
            t.setFont(Font.font(12));

            nodos.put(estados.get(i), c);
            automataPane.getChildren().addAll(c, t);
        }

        // --- CORRECCIÓN: iterar la lista de transiciones en vez de usar un forEach con dos parámetros ---
        try {
            // suponiendo que getTransiciones() devuelve List<AutomataDePila.Transicion>
            List<AutomataDePila.Transicion> transList = ap.getTransiciones();

            for (AutomataDePila.Transicion tr : transList) {
                // intentamos acceder a los campos esperados (si tus nombres son distintos, ajusta aquí)
                String origen = tr.estadoOrigen;        // campo público
                String destino = tr.estadoDestino;     // campo público
                Character simbolo = tr.simboloEntrada; // campo público (null -> ε)
                char pila = tr.simboloPila;            // campo público
                String apilar = tr.cadenaApilar;       // campo público

                Circle c1 = nodos.get(origen);
                Circle c2 = nodos.get(destino);
                if (c1 == null || c2 == null) continue;

                // línea simple entre centros
                Line line = new Line(c1.getCenterX(), c1.getCenterY(), c2.getCenterX(), c2.getCenterY());
                line.setStroke(Color.BLACK);

                double mx = (c1.getCenterX() + c2.getCenterX()) / 2;
                double my = (c1.getCenterY() + c2.getCenterY()) / 2;

                // --- INICIO DE CORRECCIÓN PARA DIBUJAR FLECHA ---
                // Vector desde origen a destino
                double dx = c2.getCenterX() - c1.getCenterX();
                double dy = c2.getCenterY() - c1.getCenterY();
                double angle = Math.atan2(dy, dx);

                // Coordenadas del punto de la línea que está en el borde del círculo de destino
                double endX = c2.getCenterX() - 25 * Math.cos(angle);
                double endY = c2.getCenterY() - 25 * Math.sin(angle);
                line.setEndX(endX);
                line.setEndY(endY);

                // Triángulo para la punta de flecha
                double arrowSize = 8.0;
                Polygon arrowhead = new Polygon(
                        endX, endY,
                        endX - arrowSize * Math.cos(angle - Math.PI / 6), endY - arrowSize * Math.sin(angle - Math.PI / 6),
                        endX - arrowSize * Math.cos(angle + Math.PI / 6), endY - arrowSize * Math.sin(angle + Math.PI / 6)
                );
                arrowhead.setFill(Color.BLACK);
                // --- FIN DE CORRECCIÓN PARA DIBUJAR FLECHA ---


                String etiqueta = (simbolo == null ? "ε" : simbolo) + "," + pila + "→" + (apilar == null || apilar.isEmpty() ? "ε" : apilar);
                Text txt = new Text(mx, my, etiqueta);
                txt.setFont(Font.font(11));

                // Se agrega la punta de flecha
                automataPane.getChildren().addAll(line, arrowhead, txt);
            }
        } catch (NoSuchMethodError | NoClassDefFoundError | ClassCastException ex) {
            // Si por alguna razón la estructura de transiciones es diferente,
            // evitamos que la UI se caiga; informa en el historial.
            outputArea.appendText("\n⚠️ No se pudieron dibujar transiciones: estructura inesperada en getTransiciones().");
        } catch (Exception ex) {
            outputArea.appendText("\n⚠️ Error al dibujar transiciones: " + ex.getMessage());
        }
    }

    private void visualizarPila(AutomataDePila.ConfiguracionAP config) {
        pilaPane.getChildren().clear();

        double startX = 20;
        double startY = 150;
        double cellHeight = 30;
        double cellWidth = 80;

        java.util.List<Character> pilaList = new java.util.ArrayList<>(config.pila);
        java.util.Collections.reverse(pilaList);

        if (pilaList.isEmpty()) {
            Text emptyText = new Text(startX + 20, startY + 50, "Pila vacía");
            emptyText.setFill(Color.GRAY);
            pilaPane.getChildren().add(emptyText);
            return;
        }

        for (int i = 0; i < pilaList.size(); i++) {
            double y = startY + i * (cellHeight + 5);

            Rectangle rect = new Rectangle(startX, y, cellWidth, cellHeight);
            rect.setFill(i == 0 ? Color.LIGHTGREEN : Color.LIGHTBLUE);
            rect.setStroke(Color.DARKBLUE);

            Text text = new Text(startX + cellWidth / 2 - 5, y + cellHeight / 2 + 5, String.valueOf(pilaList.get(i)));
            text.setFont(Font.font(14));
            pilaPane.getChildren().addAll(rect, text);

            if (i == 0) {
                Text topeLabel = new Text(startX + cellWidth + 8, y + cellHeight / 2 + 5, "← TOPE");
                topeLabel.setFill(Color.GREEN);
                pilaPane.getChildren().add(topeLabel);
            }
        }
    }

    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar AP");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                estadosField.setText(reader.readLine());
                alfabetoEntradaField.setText(reader.readLine());
                alfabetoPilaField.setText(reader.readLine());
                estadoInicialField.setText(reader.readLine());
                simboloInicialPilaField.setText(reader.readLine());
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
        fileChooser.setTitle("Guardar AP");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(estadosField.getText());
                writer.println(alfabetoEntradaField.getText());
                writer.println(alfabetoPilaField.getText());
                writer.println(estadoInicialField.getText());
                writer.println(simboloInicialPilaField.getText());
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
}