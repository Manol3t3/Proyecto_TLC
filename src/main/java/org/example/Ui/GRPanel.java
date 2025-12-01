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
 * Panel de interfaz para Gramáticas Regulares
 */
public class GRPanel extends BorderPane {

    private GramaticaRegular gramatica;
    private TextArea outputArea;

    private TextField simboloInicialField;
    private TextField noTerminalesField;
    private TextField terminalesField;
    private TextArea produccionesArea;
    private TextField cadenaField;

    public GRPanel() {
        this.gramatica = new GramaticaRegular();
        initUI();
    }

    private void initUI() {
        setPadding(new Insets(15));

        VBox leftPanel = createDefinitionPanel();
        VBox centerPanel = createTestPanel();
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

        Label titleLabel = new Label("Definición de Gramática Regular");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

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

        Label ejemploLabel = new Label("Ejemplo válido:\nS→aS | b\nse escribe como:\nS→aS\nS→b");
        ejemploLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888; " +
                "-fx-background-color: #ffffcc; -fx-padding: 5;");

        Button construirBtn = new Button("Construir Gramática");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        construirBtn.setOnAction(e -> construirGramatica());

        HBox fileButtons = new HBox(5);
        Button cargarBtn = new Button("Cargar");
        Button guardarBtn = new Button("Guardar");
        cargarBtn.setOnAction(e -> cargarDesdeArchivo());
        guardarBtn.setOnAction(e -> guardarEnArchivo());
        fileButtons.getChildren().addAll(cargarBtn, guardarBtn);

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

    private VBox createTestPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Verificación de Cadenas");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Mostrar gramática actual
        TextArea gramaticaView = new TextArea();
        gramaticaView.setEditable(false);
        gramaticaView.setPrefRowCount(10);
        gramaticaView.setPromptText("La gramática construida aparecerá aquí");
        gramaticaView.setStyle("-fx-font-family: 'Courier New';");

        // Panel de prueba
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

        Button verificarBtn = new Button("Verificar Cadena");
        verificarBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        verificarBtn.setOnAction(e -> {
            verificarCadena();
            gramaticaView.setText(gramatica.getProduccionesTexto());
        });

        Button derivarBtn = new Button("Mostrar Derivaciones");
        derivarBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        derivarBtn.setOnAction(e -> {
            mostrarDerivaciones();
            gramaticaView.setText(gramatica.getProduccionesTexto());
        });

        Button convertirBtn = new Button("Convertir a AFD");
        convertirBtn.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        convertirBtn.setOnAction(e -> {
            try {
                convertirAAFD();
            } catch (Exception ex) {
                // Muestra un diálogo de error al usuario
                mostrarError("Error al convertir o al mostrar el AFD: " + ex.getMessage());

                // Opcional: Imprime el stack trace completo en la consola para depuración
                ex.printStackTrace();
            }
        });

        buttonBox.getChildren().addAll(verificarBtn, derivarBtn, convertirBtn);
        testBox.getChildren().addAll(testLabel, cadenaField, buttonBox);

        panel.getChildren().addAll(titleLabel, gramaticaView, testBox);
        return panel;
    }

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

        Button limpiarBtn = new Button("Limpiar");
        limpiarBtn.setMaxWidth(Double.MAX_VALUE);
        limpiarBtn.setOnAction(e -> outputArea.clear());

        panel.getChildren().addAll(titleLabel, new Separator(), outputArea, limpiarBtn);
        return panel;
    }

    private void construirGramatica() {
        try {
            gramatica = new GramaticaRegular();

            // Símbolo inicial
            String simboloInicial = simboloInicialField.getText().trim();
            gramatica.setSimboloInicial(simboloInicial);

            // No terminales
            String[] noTerminales = noTerminalesField.getText().trim().split(",");
            for (String nt : noTerminales) {
                gramatica.agregarNoTerminal(nt.trim());
            }

            // Terminales
            String terminales = terminalesField.getText().trim();
            for (char t : terminales.toCharArray()) {
                gramatica.agregarTerminal(t);
            }

            // Producciones (formato: A→aB o A→a o A→ε)
            String[] producciones = produccionesArea.getText().trim().split("\n");
            for (String prod : producciones) {
                if (prod.trim().isEmpty()) continue;

                String[] partes = prod.split("→");
                if (partes.length != 2) continue;

                String izq = partes[0].trim();
                String der = partes[1].trim();

                if (der.equals("ε") || der.isEmpty()) {
                    gramatica.agregarProduccionEpsilon(izq);
                } else if (der.length() == 1) {
                    // A → a
                    gramatica.agregarProduccion(izq, der.charAt(0), null);
                } else if (der.length() == 2) {
                    // A → aB
                    gramatica.agregarProduccion(izq, der.charAt(0), String.valueOf(der.charAt(1)));
                }
            }

            outputArea.setText("✓ Gramática construida exitosamente!\n\n" +
                    gramatica.getProduccionesTexto() + "\n" +
                    "No terminales: " + gramatica.getNoTerminales() + "\n" +
                    "Terminales: " + gramatica.getTerminales() + "\n" +
                    "Símbolo inicial: " + gramatica.getSimboloInicial());

        } catch (Exception e) {
            mostrarError("Error al construir gramática: " + e.getMessage());
        }
    }

    private void verificarCadena() {
        String cadena = cadenaField.getText().trim();

        if (gramatica.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

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

    private void mostrarDerivaciones() {
        String cadena = cadenaField.getText().trim();

        if (gramatica.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        List<String> derivaciones = gramatica.generarDerivaciones(cadena);

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("DERIVACIONES\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        for (int i = 0; i < derivaciones.size(); i++) {
            resultado.append("Paso ").append(i).append(": ");
            resultado.append(derivaciones.get(i)).append("\n");
        }

        outputArea.setText(resultado.toString());
    }

    private void convertirAAFD() {
        if (gramatica.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        AFD afd = gramatica.convertirAFD();

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("CONVERSIÓN A AFD\n");
        resultado.append("═══════════════════════════════\n\n");
        resultado.append("AFD Equivalente:\n\n");
        resultado.append("Estados: ").append(afd.getEstados()).append("\n");
        resultado.append("Alfabeto: ").append(afd.getAlfabeto()).append("\n");
        resultado.append("Estado inicial: ").append(afd.getEstadoInicial()).append("\n");
        resultado.append("Estados finales: ").append(afd.getEstadosFinales()).append("\n\n");
        resultado.append("Transiciones:\n");

        afd.getTransiciones().forEach((estado, trans) -> {
            trans.forEach((simbolo, destino) -> {
                resultado.append(String.format("  δ(%s, %c) = %s\n", estado, simbolo, destino));
            });
        });

        outputArea.setText(resultado.toString());
    }

    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar Gramática");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                simboloInicialField.setText(reader.readLine());
                noTerminalesField.setText(reader.readLine());
                terminalesField.setText(reader.readLine());

                StringBuilder producciones = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    producciones.append(line).append("\n");
                }
                produccionesArea.setText(producciones.toString());

                outputArea.setText("✓ Archivo cargado exitosamente");
            } catch (IOException e) {
                mostrarError("Error al cargar archivo: " + e.getMessage());
            }
        }
    }

    private void guardarEnArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Gramática");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de texto", "*.txt")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(simboloInicialField.getText());
                writer.println(noTerminalesField.getText());
                writer.println(terminalesField.getText());
                writer.print(produccionesArea.getText());

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