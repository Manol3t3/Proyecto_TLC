package org.example.Ui;

import org.example.Modelo.AFD;
import org.example.Modelo.GramaticaRegular;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.List;

/**
 * Panel de interfaz para Gramáticas Regulares (GR).
 * Esta clase extiende BorderPane de JavaFX para organizar los componentes
 * de definición de la gramática, pruebas y resultados.
 */
public class GRPanel extends BorderPane {

    // Objeto principal que almacena la lógica de la Gramática Regular.
    private GramaticaRegular gramatica;

    // Área de texto para mostrar resultados de la construcción, verificación y conversión.
    private TextArea outputArea;

    // Campos de entrada para la definición de la GR.
    private TextField simboloInicialField;
    private TextField noTerminalesField;
    private TextField terminalesField;
    private TextArea produccionesArea;

    // Campo de entrada para la cadena a verificar.
    private TextField cadenaField;

    /**
     * Constructor. Inicializa el objeto GramaticaRegular y construye la interfaz de usuario.
     */
    public GRPanel() {
        this.gramatica = new GramaticaRegular();
        initUI();
    }

    /**
     * Inicializa la interfaz de usuario (UI) organizando los subpaneles.
     */
    private void initUI() {
        // Establece el relleno general del panel principal.
        setPadding(new Insets(15));

        // Crea los tres paneles principales de la interfaz.
        VBox leftPanel = createDefinitionPanel();
        VBox centerPanel = createTestPanel();
        VBox rightPanel = createResultsPanel();

        // Asigna los paneles a las regiones del BorderPane.
        setLeft(leftPanel);
        setCenter(centerPanel);
        setRight(rightPanel);
    }

    /**
     * Crea el panel de la izquierda para la definición de los componentes de la Gramática Regular.
     * @return VBox con los campos de entrada y botones de acción.
     */
    private VBox createDefinitionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(320);
        // Estilo visual del panel de definición.
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Definición de Gramática Regular");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // --- Componentes para la definición de la GR ---

        // Símbolo inicial
        Label simboloInicialLabel = new Label("Símbolo inicial:");
        simboloInicialField = new TextField();
        simboloInicialField.setPromptText("S");

        // No terminales
        Label noTerminalesLabel = new Label("No terminales (separados por comas):");
        noTerminalesField = new TextField();
        noTerminalesField.setPromptText("S,A,B");

        // Terminales
        Label terminalesLabel = new Label("Terminales (sin separadores):");
        terminalesField = new TextField();
        terminalesField.setPromptText("ab");

        // Producciones
        Label produccionesLabel = new Label("Producciones:");
        Label formatoLabel = new Label("Formato: A→aB o A→a o A→ε");
        formatoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        produccionesArea = new TextArea();
        produccionesArea.setPromptText("S→aA\nS→b\nA→aS\nA→ε");
        produccionesArea.setPrefRowCount(8);

        // Etiqueta de ejemplo para aclarar el formato de entrada de producciones.
        Label ejemploLabel = new Label("Ejemplo válido:\nS→aS | b\nse escribe como:\nS→aS\nS→b");
        ejemploLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888; " +
                "-fx-background-color: #ffffcc; -fx-padding: 5;");

        // Botón principal para procesar la entrada y construir la Gramática.
        Button construirBtn = new Button("Construir Gramática");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        // Asigna el manejador de eventos para construir la GR.
        construirBtn.setOnAction(e -> construirGramatica());

        // Botones de Cargar/Guardar para persistencia de la definición de la GR.
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
                simboloInicialLabel, simboloInicialField,
                noTerminalesLabel, noTerminalesField,
                terminalesLabel, terminalesField,
                produccionesLabel, formatoLabel,
                produccionesArea,
                ejemploLabel,
                construirBtn,
                fileButtons
        );

        return panel;
    }

    /**
     * Crea el panel central para la verificación de cadenas y acciones de conversión.
     * @return VBox con el área de visualización de la GR y el panel de prueba.
     */
    private VBox createTestPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Verificación de Cadenas");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Área para mostrar la Gramática actual construida.
        TextArea gramaticaView = new TextArea();
        gramaticaView.setEditable(false);
        gramaticaView.setPrefRowCount(10);
        gramaticaView.setPromptText("La gramática construida aparecerá aquí");
        gramaticaView.setStyle("-fx-font-family: 'Courier New';");

        // Panel de prueba de cadena
        VBox testBox = new VBox(10);
        testBox.setAlignment(Pos.CENTER);
        testBox.setPadding(new Insets(15));
        testBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 5;");

        Label testLabel = new Label("Cadena a verificar:");
        testLabel.setStyle("-fx-font-weight: bold;");

        cadenaField = new TextField();
        cadenaField.setPromptText("Ingrese la cadena");
        cadenaField.setPrefWidth(300);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // Botón para verificar pertenencia de cadena.
        Button verificarBtn = new Button("Verificar Cadena");
        verificarBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        verificarBtn.setOnAction(e -> {
            verificarCadena();
            // Actualiza la vista de la gramática (puede ser redundante si no cambia).
            gramaticaView.setText(gramatica.getProduccionesTexto());
        });

        // Botón para mostrar los pasos de derivación de una cadena.
        Button derivarBtn = new Button("Mostrar Derivaciones");
        derivarBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        derivarBtn.setOnAction(e -> {
            mostrarDerivaciones();
            gramaticaView.setText(gramatica.getProduccionesTexto());
        });

        // Botón para convertir la GR a su Autómata Finito Determinista (AFD) equivalente.
        Button convertirBtn = new Button("Convertir a AFD");
        convertirBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        convertirBtn.setOnAction(e -> {
            try {
                convertirAAFD();
            } catch (Exception ex) {
                // Manejo de excepciones durante la conversión (e.g., errores de la lógica AFD).
                mostrarError("Error al convertir o al mostrar el AFD: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        buttonBox.getChildren().addAll(verificarBtn, derivarBtn, convertirBtn);
        testBox.getChildren().addAll(testLabel, cadenaField, buttonBox);

        panel.getChildren().addAll(titleLabel, gramaticaView, testBox);
        return panel;
    }

    /**
     * Crea el panel de la derecha para mostrar los resultados y mensajes de salida.
     * @return VBox con el área de resultados.
     */
    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(320);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Resultados");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(25);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: 'Courier New';");

        // Botón para limpiar el área de resultados.
        Button limpiarBtn = new Button("Limpiar");
        limpiarBtn.setMaxWidth(Double.MAX_VALUE);
        limpiarBtn.setOnAction(e -> outputArea.clear());

        panel.getChildren().addAll(titleLabel, new Separator(), outputArea, limpiarBtn);
        return panel;
    }

    /**
     * Lee los datos de los campos de la UI y construye el objeto GramaticaRegular.
     * Muestra el resultado de la construcción en el área de salida.
     */
    private void construirGramatica() {
        try {
            // Reinicia la gramática para evitar mezclar definiciones.
            gramatica = new GramaticaRegular();

            // 1. Símbolo inicial
            String simboloInicial = simboloInicialField.getText().trim();
            gramatica.setSimboloInicial(simboloInicial);

            // 2. No terminales (separados por comas)
            String[] noTerminales = noTerminalesField.getText().trim().split(",");
            for (String nt : noTerminales) {
                if (!nt.trim().isEmpty()) {
                    gramatica.agregarNoTerminal(nt.trim());
                }
            }

            // 3. Terminales (caracteres consecutivos)
            String terminales = terminalesField.getText().trim();
            for (char t : terminales.toCharArray()) {
                gramatica.agregarTerminal(t);
            }

            // 4. Producciones (una por línea: A→aB o A→a o A→ε)
            String[] producciones = produccionesArea.getText().trim().split("\n");
            for (String prod : producciones) {
                if (prod.trim().isEmpty()) continue;

                String[] partes = prod.split("→");
                if (partes.length != 2) continue; // Formato inválido

                String izq = partes[0].trim(); // Lado izquierdo (No Terminal)
                String der = partes[1].trim(); // Lado derecho (Producción)

                // Verifica los tres posibles formatos de GR.
                if (der.equals("ε") || der.isEmpty()) {
                    // Producción 1: A → ε
                    gramatica.agregarProduccionEpsilon(izq);
                } else if (der.length() == 1) {
                    // Producción 2: A → a (Terminal)
                    gramatica.agregarProduccion(izq, der.charAt(0), null);
                } else if (der.length() == 2) {
                    // Producción 3: A → aB (Terminal + No Terminal)
                    gramatica.agregarProduccion(izq, der.charAt(0), String.valueOf(der.charAt(1)));
                }
                // Las producciones que no cumplen los formatos de GR se ignoran.
            }

            // Muestra el resumen de la gramática construida.
            outputArea.setText("✓ Gramática construida exitosamente!\n\n" +
                    gramatica.getProduccionesTexto() + "\n" +
                    "No terminales: " + gramatica.getNoTerminales() + "\n" +
                    "Terminales: " + gramatica.getTerminales() + "\n" +
                    "Símbolo inicial: " + gramatica.getSimboloInicial());

        } catch (Exception e) {
            mostrarError("Error al construir gramática: " + e.getMessage());
        }
    }

    /**
     * Verifica si la cadena ingresada pertenece al lenguaje generado por la GR.
     */
    private void verificarCadena() {
        String cadena = cadenaField.getText().trim();

        if (gramatica.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        // Llama al método de pertenencia de la clase modelo.
        boolean pertenece = gramatica.pertenece(cadena);

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");
        resultado.append("Gramática:\n").append(gramatica.getProduccionesTexto()).append("\n");

        if (pertenece) {
            resultado.append("✓ La cadena PERTENECE al lenguaje");
        } else {
            resultado.append("✗ La cadena NO PERTENECE al lenguaje");
        }

        outputArea.setText(resultado.toString());
    }

    /**
     * Muestra las derivaciones (si la lógica del modelo lo permite) de la cadena ingresada.
     */
    private void mostrarDerivaciones() {
        String cadena = cadenaField.getText().trim();

        if (gramatica.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        // Llama al método para generar la lista de derivaciones.
        List<String> derivaciones = gramatica.generarDerivaciones(cadena);

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("DERIVACIONES\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        if (derivaciones.isEmpty()) {
            resultado.append("No se pudo generar la derivación completa o la cadena no pertenece.\n");
        } else {
            for (int i = 0; i < derivaciones.size(); i++) {
                resultado.append("Paso ").append(i).append(": ");
                resultado.append(derivaciones.get(i)).append("\n");
            }
        }

        outputArea.setText(resultado.toString());
    }

    /**
     * Convierte la Gramática Regular actual a su Autómata Finito Determinista (AFD) equivalente
     * y muestra sus componentes en el área de resultados.
     */
    private void convertirAAFD() {
        if (gramatica.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        // Llama al método de conversión del modelo.
        AFD afd = gramatica.convertirAFD();

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("CONVERSIÓN A AFD\n");
        resultado.append("═══════════════════════════════\n\n");
        resultado.append("AFD Equivalente:\n\n");

        // Muestra los 5-tuple del AFD:
        resultado.append("Estados (Q): ").append(afd.getEstados()).append("\n");
        resultado.append("Alfabeto (Σ): ").append(afd.getAlfabeto()).append("\n");
        resultado.append("Estado inicial (q0): ").append(afd.getEstadoInicial()).append("\n");
        resultado.append("Estados finales (F): ").append(afd.getEstadosFinales()).append("\n\n");
        resultado.append("Transiciones (δ):\n");

        // Itera sobre el mapa de transiciones para mostrarlas en formato δ(q, a) = q'.
        afd.getTransiciones().forEach((estado, trans) -> {
            trans.forEach((simbolo, destino) -> {
                resultado.append(String.format("  δ(%s, %c) = %s\n", estado, simbolo, destino));
            });
        });

        outputArea.setText(resultado.toString());
    }

    /**
     * Muestra un diálogo de selección de archivo y carga los parámetros de la GR
     * desde un archivo de texto.
     */
    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar Gramática");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // Se asume un formato de archivo específico:
                // Línea 1: Símbolo inicial
                // Línea 2: No terminales (separados por coma)
                // Línea 3: Terminales (concatenados)
                // Resto de líneas: Producciones
                simboloInicialField.setText(reader.readLine());
                noTerminalesField.setText(reader.readLine());
                terminalesField.setText(reader.readLine());

                StringBuilder producciones = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    producciones.append(line).append("\n");
                }
                produccionesArea.setText(producciones.toString());

                outputArea.setText("Archivo cargado exitosamente.\nPresione 'Construir Gramática' para aplicarlos.");
            } catch (IOException e) {
                mostrarError("Error al cargar archivo: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra un diálogo para guardar y escribe los parámetros de la GR
     * en un archivo de texto con un formato predefinido.
     */
    private void guardarEnArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Gramática");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Guarda los campos en el orden esperado por el método cargarDesdeArchivo().
                writer.println(simboloInicialField.getText());
                writer.println(noTerminalesField.getText());
                writer.println(terminalesField.getText());
                writer.print(produccionesArea.getText()); // Las producciones van al final.

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