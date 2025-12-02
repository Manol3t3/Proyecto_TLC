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
 * Panel de interfaz para Máquina de Turing (MT).
 * Permite al usuario definir la MT, procesar una cadena y visualizar la ejecución
 * paso a paso en una cinta gráfica.
 */
public class MTPanel extends BorderPane {

    // Objeto principal que almacena la lógica de la Máquina de Turing.
    private MaquinaTuring mt;

    // Área de texto para mostrar el historial de ejecución y mensajes de resultado.
    private TextArea outputArea;

    // Panel de JavaFX para dibujar la cinta de la MT y el cabezal.
    private Pane cintaPane;

    // Slider para navegar entre los pasos de la ejecución histórica.
    private Slider pasoSlider;

    // Etiqueta que muestra el número de paso actual.
    private Label pasoLabel;

    // Campos de entrada para la definición de la MT.
    private TextField estadosField;
    private TextField alfabetoEntradaField;
    private TextField alfabetoCintaField;
    private TextField estadoInicialField;
    private TextField simboloBlancoField;
    private TextField estadosAceptacionField;
    private TextArea transicionesArea;

    // Campo de entrada para la cadena a procesar.
    private TextField cadenaField;

    /**
     * Constructor. Inicializa el objeto MaquinaTuring y construye la interfaz de usuario.
     */
    public MTPanel() {
        this.mt = new MaquinaTuring();
        initUI();
    }

    /**
     * Inicializa la interfaz de usuario (UI), dividiéndola en tres paneles.
     */
    private void initUI() {
        setPadding(new Insets(15));

        VBox leftPanel = createDefinitionPanel();
        VBox centerPanel = createVisualizationPanel();
        VBox rightPanel = createResultsPanel();

        setLeft(leftPanel);
        setCenter(centerPanel);
        setRight(rightPanel);
    }

    /**
     * Crea el panel de la izquierda para la definición formal de la Máquina de Turing.
     * @return VBox conteniendo los campos de definición y los botones de acción.
     */
    private VBox createDefinitionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(320);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Definición de Máquina de Turing");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // --- Componentes para la definición (7-tupla) ---
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

        // Definición de transiciones
        Label transicionesLabel = new Label("Transiciones:");
        Label formatoLabel = new Label("Formato: q0,0,q1,X,R");
        formatoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label formatoLabel2 = new Label("(R=Derecha, L=Izquierda, S=Estático)");
        formatoLabel2.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        transicionesArea = new TextArea();
        transicionesArea.setPromptText("q0,0,q1,X,R\nq1,1,q1,1,R\nq1,_,qa,_,S");
        transicionesArea.setPrefRowCount(6);

        // Ejemplo didáctico de una MT.
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

        // Botones de Cargar/Guardar para persistencia.
        HBox fileButtons = new HBox(5);
        Button cargarBtn = new Button("Cargar");
        Button guardarBtn = new Button("Guardar");
        cargarBtn.setOnAction(e -> cargarDesdeArchivo());
        guardarBtn.setOnAction(e -> guardarEnArchivo());
        fileButtons.getChildren().addAll(cargarBtn, guardarBtn);

        // Añadir todos los componentes al panel.
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

        // Envuelve el panel en un ScrollPane para manejar contenido largo.
        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        VBox container = new VBox(scrollPane);
        return container;
    }

    /**
     * Crea el panel central para la visualización gráfica de la cinta y los controles de paso.
     * @return VBox con el área de visualización, el slider de control y el panel de prueba.
     */
    private VBox createVisualizationPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Visualización de la Cinta");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Panel donde se dibujarán las celdas de la cinta y el cabezal.
        cintaPane = new Pane();
        cintaPane.setPrefSize(600, 200);
        cintaPane.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2; " +
                "-fx-background-color: white;");

        // --- Controles de paso (Slider) ---
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
        pasoSlider.setSnapToTicks(true); // Asegura que el slider se detenga en valores enteros.
        pasoSlider.setPrefWidth(500);

        // Listener para actualizar la visualización cuando el slider cambia de valor.
        pasoSlider.valueProperty().addListener((obs, old, newVal) -> {
            int paso = newVal.intValue();
            pasoLabel.setText("Paso: " + paso);
            visualizarPaso(paso);
        });

        HBox sliderButtons = new HBox(10);
        sliderButtons.setAlignment(Pos.CENTER);

        // Botones para moverse rápidamente entre pasos.
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

        // --- Panel de prueba de cadena ---
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

    /**
     * Crea el panel de la derecha para mostrar el historial de ejecución en texto.
     * @return VBox con el área de resultados.
     */
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
            cintaPane.getChildren().clear(); // Limpia la visualización gráfica.
        });

        panel.getChildren().addAll(titleLabel, new Separator(), outputArea, limpiarBtn);
        return panel;
    }

    /**
     * Lee los datos de los campos de la UI y construye el objeto MaquinaTuring.
     */
    private void construirMaquina() {
        try {
            mt = new MaquinaTuring();

            // 1. Estados
            String[] estados = estadosField.getText().trim().split(",");
            for (String estado : estados) {
                if (!estado.trim().isEmpty()) mt.agregarEstado(estado.trim());
            }

            // 2. Alfabeto de entrada
            String alfabetoEntrada = alfabetoEntradaField.getText().trim();
            for (char c : alfabetoEntrada.toCharArray()) {
                mt.agregarSimboloEntrada(c);
            }

            // 3. Alfabeto de cinta
            String alfabetoCinta = alfabetoCintaField.getText().trim();
            for (char c : alfabetoCinta.toCharArray()) {
                mt.agregarSimboloCinta(c);
            }

            // 4. Estado inicial
            mt.setEstadoInicial(estadoInicialField.getText().trim());

            // 5. Símbolo blanco
            // Se asume que el símbolo blanco es siempre un solo carácter.
            mt.setSimboloBlanco(simboloBlancoField.getText().trim().charAt(0));

            // 6. Estados de aceptación
            String[] aceptacion = estadosAceptacionField.getText().trim().split(",");
            for (String estado : aceptacion) {
                if (!estado.trim().isEmpty()) mt.agregarEstadoAceptacion(estado.trim());
            }

            // 7. Transiciones (formato: q0,0,q1,X,R)
            String[] transiciones = transicionesArea.getText().trim().split("\n");
            for (String trans : transiciones) {
                if (trans.trim().isEmpty()) continue;

                String[] partes = trans.trim().split(",");
                if (partes.length != 5) continue; // Una transición requiere 5 partes.

                String estadoOrigen = partes[0].trim();
                char simboloLeer = partes[1].trim().charAt(0);
                String estadoDestino = partes[2].trim();
                char simboloEscribir = partes[3].trim().charAt(0);
                String direccionStr = partes[4].trim().toUpperCase();

                // Mapeo de la dirección de movimiento
                Direccion direccion;
                if (direccionStr.equals("R")) direccion = Direccion.DERECHA;
                else if (direccionStr.equals("L")) direccion = Direccion.IZQUIERDA;
                else direccion = Direccion.ESTATICO; // Asume "S" o cualquier otra cosa como estático.

                mt.agregarTransicion(estadoOrigen, simboloLeer, estadoDestino,
                        simboloEscribir, direccion);
            }

            // Muestra el resumen de la MT construida.
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

    /**
     * Inicia el procesamiento de la cadena en la MT, registra el historial de ejecución
     * y actualiza la UI para permitir la visualización paso a paso.
     */
    private void procesarCadena() {
        String cadena = cadenaField.getText().trim();

        if (mt.getEstados().isEmpty()) {
            mostrarError("Primero debe construir la máquina");
            return;
        }

        // Ejecuta la lógica principal de la MT.
        boolean aceptada = mt.procesar(cadena);
        List<MaquinaTuring.ConfiguracionMT> historial = mt.getHistorial();

        // Configurar el slider para navegar en el historial (pasos 0 a N-1).
        pasoSlider.setMax(Math.max(0, historial.size() - 1));
        pasoSlider.setValue(0); // Posiciona el slider en el primer paso.

        // --- Mostrar historial de texto ---
        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("EJECUCIÓN DE MÁQUINA DE TURING\n");
        resultado.append("Cadena entrada: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        resultado.append("Configuraciones:\n\n");

        // Detalla cada configuración (estado y contenido de la cinta).
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

        // Visualizar el primer paso después de procesar.
        if (!historial.isEmpty()) {
            visualizarPaso(0);
        }
    }

    /**
     * Dibuja la cinta de la MT para la configuración correspondiente a un paso específico.
     * @param paso El índice del paso en el historial de ejecución.
     */
    private void visualizarPaso(int paso) {
        cintaPane.getChildren().clear(); // Limpia la visualización anterior.

        List<MaquinaTuring.ConfiguracionMT> historial = mt.getHistorial();
        if (historial.isEmpty() || paso < 0 || paso >= historial.size()) {
            return;
        }

        MaquinaTuring.ConfiguracionMT config = historial.get(paso);

        // Parámetros de dibujo
        double cellWidth = 50;
        double cellHeight = 50;
        double startX = 50;
        double startY = 80;

        // Mostrar estado actual
        Text estadoText = new Text(startX, 40, "Estado: " + config.estado);
        estadoText.setFont(Font.font("Arial", 16));
        estadoText.setStyle("-fx-font-weight: bold;");
        cintaPane.getChildren().add(estadoText);

        // Dibujar cinta (muestra un número limitado de celdas).
        // Se dibuja un máximo de 12 celdas o el tamaño actual de la cinta.
        int maxCeldas = Math.min(12, config.cinta.size());

        // El cabezal se indica con color de fondo (LIGHTGREEN) y una flecha (RED).
        for (int i = 0; i < maxCeldas; i++) {
            double x = startX + (i * cellWidth);

            Rectangle rect = new Rectangle(x, startY, cellWidth, cellHeight);
            rect.setFill(i == config.cabezal ? Color.LIGHTGREEN : Color.WHITE); // Celta marcada por el cabezal.
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(2);

            Text text = new Text(x + cellWidth/2 - 8, startY + cellHeight/2 + 5,
                    String.valueOf(config.cinta.get(i)));
            text.setFont(Font.font("Arial", 18));
            text.setStyle("-fx-font-weight: bold;");

            cintaPane.getChildren().addAll(rect, text);

            // Dibujar la flecha del cabezal sobre la celda actual.
            if (i == config.cabezal) {
                // Triángulo (flecha)
                Polygon arrow = new Polygon();
                arrow.getPoints().addAll(
                        x + cellWidth/2, startY - 15.0,
                        x + cellWidth/2 - 10, startY - 30.0,
                        x + cellWidth/2 + 10, startY - 30.0
                );
                arrow.setFill(Color.RED);
                cintaPane.getChildren().add(arrow);

                // Etiqueta "Cabezal"
                Text cabezalText = new Text(x + cellWidth/2 - 35, startY - 35, "Cabezal");
                cabezalText.setFill(Color.RED);
                cabezalText.setStyle("-fx-font-weight: bold;");
                cintaPane.getChildren().add(cabezalText);
            }
        }
        // Sugerencia visual para el concepto de la Máquina de Turing.
        //
    }

    /**
     * Muestra un diálogo de selección de archivo y carga los parámetros de la MT
     * desde un archivo de texto.
     * El formato del archivo debe coincidir con el orden de los campos de la UI.
     */
    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar MT");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Lectura secuencial de los 6 campos de texto.
                estadosField.setText(reader.readLine());
                alfabetoEntradaField.setText(reader.readLine());
                alfabetoCintaField.setText(reader.readLine());
                estadoInicialField.setText(reader.readLine());
                simboloBlancoField.setText(reader.readLine());
                estadosAceptacionField.setText(reader.readLine());

                // Lectura del resto de las líneas para las transiciones.
                StringBuilder transiciones = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    transiciones.append(line).append("\n");
                }
                transicionesArea.setText(transiciones.toString());

                outputArea.setText("✓ Archivo cargado exitosamente.\nPresione 'Construir Máquina' para aplicarlos.");
            } catch (IOException e) {
                mostrarError("Error al cargar archivo: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra un diálogo para guardar y escribe los parámetros de la MT
     * en un archivo de texto.
     */
    private void guardarEnArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar MT");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Escritura secuencial de los 6 campos de texto.
                writer.println(estadosField.getText());
                writer.println(alfabetoEntradaField.getText());
                writer.println(alfabetoCintaField.getText());
                writer.println(estadoInicialField.getText());
                writer.println(simboloBlancoField.getText());
                writer.println(estadosAceptacionField.getText());
                // Escritura de las transiciones al final.
                writer.print(transicionesArea.getText());

                outputArea.setText("✓ Archivo guardado exitosamente");
            } catch (IOException e) {
                mostrarError("Error al guardar archivo: " + e.getMessage());
            }
        }
    }

    /**
     * Método utilitario para mostrar un diálogo de alerta de error al usuario.
     * @param mensaje El mensaje de error a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}