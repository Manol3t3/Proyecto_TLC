package org.example.Ui;

import org.example.Modelo.MaquinaTuring;
import org.example.Modelo.MaquinaTuring.TransicionValor.Direccion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.List;

/**
 * Panel de interfaz para Máquina de Turing
 */
public class MTPanel extends BorderPane {

    private MaquinaTuring mt;
    private TextArea outputArea;
    private Pane cintaPane;
    private Slider pasoSlider;
    private Label pasoLabel;

    private TextField estadosField;
    private TextField alfabetoEntradaField;
    private TextField alfabetoCintaField;
    private TextField estadoInicialField;
    private TextField simboloBlancoField;
    private TextField estadosAceptacionField;
    private TextArea transicionesArea;
    private TextField cadenaField;

    public MTPanel() {
        this.mt = new MaquinaTuring();
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

        Label titleLabel = new Label("Definición de Máquina de Turing");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label estadosLabel = new Label("Estados:");
        estadosField = new TextField();
        estadosField.setPromptText("q0,q1,q2,qa");

        Label alfabetoEntradaLabel = new Label("Alfabeto de entrada:");
        alfabetoEntradaField = new TextField();
        alfabetoEntradaField.setPromptText("01");

        Label alfabetoCintaLabel = new Label("Alfabeto de cinta:");
        alfabetoCintaField = new TextField();
        alfabetoCintaField.setPromptText("01X_");

        Label estadoInicialLabel = new Label("Estado inicial:");
        estadoInicialField = new TextField();
        estadoInicialField.setPromptText("q0");

        Label simboloBlancoLabel = new Label("Símbolo blanco:");
        simboloBlancoField = new TextField();
        simboloBlancoField.setPromptText("_");

        Label estadosAceptacionLabel = new Label("Estados de aceptación:");
        estadosAceptacionField = new TextField();
        estadosAceptacionField.setPromptText("qa");

        Label transicionesLabel = new Label("Transiciones:");
        Label formatoLabel = new Label("Formato: q0,0,q1,X,R");
        formatoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label formatoLabel2 = new Label("(R=Derecha, L=Izquierda, S=Estático)");
        formatoLabel2.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        transicionesArea = new TextArea();
        transicionesArea.setPromptText("q0,0,q1,X,R\nq1,1,q1,1,R\nq1,_,qa,_,S");
        transicionesArea.setPrefRowCount(6);

        Label ejemploLabel = new Label(
                "Ejemplo Palíndromo:\n" +
                        "q0,a,q1,X,R\n" +
                        "q1,a,q1,a,R\n" +
                        "q1,b,q2,b,L\n" +
                        "q2,a,q3,X,L\n" +
                        "q3,X,qa,X,S"
        );
        ejemploLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888; " +
                "-fx-background-color: #ffffcc; -fx-padding: 5;");

        Button construirBtn = new Button("Construir Máquina");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        construirBtn.setOnAction(e -> construirMaquina());

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
                alfabetoCintaLabel, alfabetoCintaField,
                estadoInicialLabel, estadoInicialField,
                simboloBlancoLabel, simboloBlancoField,
                estadosAceptacionLabel, estadosAceptacionField,
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
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Visualización de la Cinta");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        cintaPane = new Pane();
        cintaPane.setPrefSize(600, 200);
        cintaPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; " +
                "-fx-background-color: white;");

        // Control deslizante para ver pasos
        VBox controlBox = new VBox(10);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(10));
        controlBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        pasoLabel = new Label("Paso: 0");
        pasoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        pasoSlider = new Slider(0, 0, 0);
        pasoSlider.setShowTickLabels(true);
        pasoSlider.setShowTickMarks(true);
        pasoSlider.setMajorTickUnit(1);
        pasoSlider.setMinorTickCount(0);
        pasoSlider.setBlockIncrement(1);
        pasoSlider.setSnapToTicks(true);
        pasoSlider.setPrefWidth(500);
        pasoSlider.valueProperty().addListener((obs, old, newVal) -> {
            int paso = newVal.intValue();
            pasoLabel.setText("Paso: " + paso);
            visualizarPaso(paso);
        });

        HBox sliderButtons = new HBox(10);
        sliderButtons.setAlignment(Pos.CENTER);

        Button primerPasoBtn = new Button("⏮ Inicio");
        primerPasoBtn.setOnAction(e -> pasoSlider.setValue(0));

        Button anteriorBtn = new Button("◀ Anterior");
        anteriorBtn.setOnAction(e -> pasoSlider.setValue(pasoSlider.getValue() - 1));

        Button siguienteBtn = new Button("Siguiente ▶");
        siguienteBtn.setOnAction(e -> pasoSlider.setValue(pasoSlider.getValue() + 1));

        Button ultimoPasoBtn = new Button("Final ⏭");
        ultimoPasoBtn.setOnAction(e -> pasoSlider.setValue(pasoSlider.getMax()));

        sliderButtons.getChildren().addAll(primerPasoBtn, anteriorBtn, siguienteBtn, ultimoPasoBtn);
        controlBox.getChildren().addAll(pasoLabel, pasoSlider, sliderButtons);

        // Panel de prueba
        VBox testBox = new VBox(10);
        testBox.setAlignment(Pos.CENTER);
        testBox.setPadding(new Insets(15));
        testBox.setStyle("-fx-background-color: #e8eaf6; -fx-background-radius: 5;");

        Label testLabel = new Label("Cadena de entrada:");
        testLabel.setStyle("-fx-font-weight: bold;");

        cadenaField = new TextField();
        cadenaField.setPromptText("Ingrese la cadena");
        cadenaField.setPrefWidth(300);

        Button procesarBtn = new Button("Procesar Cadena");
        procesarBtn.setStyle("-fx-background-color: #3F51B5; -fx-text-fill: white;");
        procesarBtn.setOnAction(e -> procesarCadena());

        testBox.getChildren().addAll(testLabel, cadenaField, procesarBtn);

        panel.getChildren().addAll(titleLabel, cintaPane, controlBox, testBox);
        return panel;
    }

    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Ejecución Paso a Paso");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(30);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10px;");

        Button limpiarBtn = new Button("Limpiar");
        limpiarBtn.setMaxWidth(Double.MAX_VALUE);
        limpiarBtn.setOnAction(e -> {
            outputArea.clear();
            cintaPane.getChildren().clear();
        });

        panel.getChildren().addAll(titleLabel, new Separator(), outputArea, limpiarBtn);
        return panel;
    }

    private void construirMaquina() {
        try {
            mt = new MaquinaTuring();

            // Estados
            String[] estados = estadosField.getText().trim().split(",");
            for (String estado : estados) {
                mt.agregarEstado(estado.trim());
            }

            // Alfabeto de entrada
            String alfabetoEntrada = alfabetoEntradaField.getText().trim();
            for (char c : alfabetoEntrada.toCharArray()) {
                mt.agregarSimboloEntrada(c);
            }

            // Alfabeto de cinta
            String alfabetoCinta = alfabetoCintaField.getText().trim();
            for (char c : alfabetoCinta.toCharArray()) {
                mt.agregarSimboloCinta(c);
            }

            // Estado inicial
            mt.setEstadoInicial(estadoInicialField.getText().trim());

            // Símbolo blanco
            mt.setSimboloBlanco(simboloBlancoField.getText().trim().charAt(0));

            // Estados de aceptación
            String[] aceptacion = estadosAceptacionField.getText().trim().split(",");
            for (String estado : aceptacion) {
                mt.agregarEstadoAceptacion(estado.trim());
            }

            // Transiciones (formato: q0,0,q1,X,R)
            String[] transiciones = transicionesArea.getText().trim().split("\n");
            for (String trans : transiciones) {
                if (trans.trim().isEmpty()) continue;

                String[] partes = trans.trim().split(",");
                if (partes.length != 5) continue;

                String estadoOrigen = partes[0].trim();
                char simboloLeer = partes[1].trim().charAt(0);
                String estadoDestino = partes[2].trim();
                char simboloEscribir = partes[3].trim().charAt(0);
                String direccionStr = partes[4].trim().toUpperCase();

                Direccion direccion;
                if (direccionStr.equals("R")) direccion = Direccion.DERECHA;
                else if (direccionStr.equals("L")) direccion = Direccion.IZQUIERDA;
                else direccion = Direccion.ESTATICO;

                mt.agregarTransicion(estadoOrigen, simboloLeer, estadoDestino,
                        simboloEscribir, direccion);
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append("✓ Máquina de Turing construida!\n\n");
            resultado.append("Estados: ").append(mt.getEstados()).append("\n");
            resultado.append("Alfabeto entrada: ").append(mt.getAlfabetoEntrada()).append("\n");
            resultado.append("Alfabeto cinta: ").append(mt.getAlfabetoCinta()).append("\n");
            resultado.append("Estado inicial: ").append(mt.getEstadoInicial()).append("\n");
            resultado.append("Símbolo blanco: ").append(mt.getSimboloBlanco()).append("\n");
            resultado.append("Estados aceptación: ").append(mt.getEstadosAceptacion()).append("\n\n");
            resultado.append("Transiciones definidas: ").append(mt.getTransiciones().size());

            outputArea.setText(resultado.toString());

        } catch (Exception e) {
            mostrarError("Error al construir máquina: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarCadena() {
        String cadena = cadenaField.getText().trim();

        if (mt.getEstados().isEmpty()) {
            mostrarError("Primero debe construir la máquina");
            return;
        }

        boolean aceptada = mt.procesar(cadena);
        List<MaquinaTuring.ConfiguracionMT> historial = mt.getHistorial();

        // Configurar slider
        pasoSlider.setMax(Math.max(0, historial.size() - 1));
        pasoSlider.setValue(0);

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("EJECUCIÓN DE MÁQUINA DE TURING\n");
        resultado.append("Cadena entrada: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        resultado.append("Configuraciones:\n\n");

        for (int i = 0; i < historial.size(); i++) {
            MaquinaTuring.ConfiguracionMT config = historial.get(i);
            resultado.append(String.format("Paso %d:\n", i));
            resultado.append("  Estado: ").append(config.estado).append("\n");
            resultado.append("  Cinta:  ").append(config.getCintaString()).append("\n\n");
        }

        resultado.append("Resultado:\n");
        if (aceptada) {
            resultado.append("✓ CADENA ACEPTADA\n");
            resultado.append("Cinta final: ").append(mt.obtenerResultadoCinta());
        } else {
            resultado.append("✗ CADENA RECHAZADA");
        }

        outputArea.setText(resultado.toString());

        // Visualizar primer paso
        if (!historial.isEmpty()) {
            visualizarPaso(0);
        }
    }

    private void visualizarPaso(int paso) {
        cintaPane.getChildren().clear();

        List<MaquinaTuring.ConfiguracionMT> historial = mt.getHistorial();
        if (historial.isEmpty() || paso < 0 || paso >= historial.size()) {
            return;
        }

        MaquinaTuring.ConfiguracionMT config = historial.get(paso);

        double cellWidth = 50;
        double cellHeight = 50;
        double startX = 50;
        double startY = 80;

        // Mostrar estado actual
        Text estadoText = new Text(startX, 40, "Estado: " + config.estado);
        estadoText.setFont(Font.font("Arial", 16));
        estadoText.setStyle("-fx-font-weight: bold;");
        cintaPane.getChildren().add(estadoText);

        // Dibujar cinta
        int maxCeldas = Math.min(12, config.cinta.size());
        for (int i = 0; i < maxCeldas; i++) {
            double x = startX + (i * cellWidth);

            Rectangle rect = new Rectangle(x, startY, cellWidth, cellHeight);
            rect.setFill(i == config.cabezal ? Color.LIGHTGREEN : Color.WHITE);
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(2);

            Text text = new Text(x + cellWidth/2 - 8, startY + cellHeight/2 + 5,
                    String.valueOf(config.cinta.get(i)));
            text.setFont(Font.font("Arial", 18));
            text.setStyle("-fx-font-weight: bold;");

            cintaPane.getChildren().addAll(rect, text);

            // Flecha para el cabezal
            if (i == config.cabezal) {
                Polygon arrow = new Polygon();
                arrow.getPoints().addAll(
                        x + cellWidth/2, startY - 15.0,
                        x + cellWidth/2 - 10, startY - 30.0,
                        x + cellWidth/2 + 10, startY - 30.0
                );
                arrow.setFill(Color.RED);
                cintaPane.getChildren().add(arrow);

                Text cabezalText = new Text(x + cellWidth/2 - 35, startY - 35, "Cabezal");
                cabezalText.setFill(Color.RED);
                cabezalText.setStyle("-fx-font-weight: bold;");
                cintaPane.getChildren().add(cabezalText);
            }
        }
    }

    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar MT");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                estadosField.setText(reader.readLine());
                alfabetoEntradaField.setText(reader.readLine());
                alfabetoCintaField.setText(reader.readLine());
                estadoInicialField.setText(reader.readLine());
                simboloBlancoField.setText(reader.readLine());
                estadosAceptacionField.setText(reader.readLine());

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
        fileChooser.setTitle("Guardar MT");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(estadosField.getText());
                writer.println(alfabetoEntradaField.getText());
                writer.println(alfabetoCintaField.getText());
                writer.println(estadoInicialField.getText());
                writer.println(simboloBlancoField.getText());
                writer.println(estadosAceptacionField.getText());
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