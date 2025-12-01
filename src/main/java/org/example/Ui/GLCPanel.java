package org.example.Ui;

import org.example.Modelo.GLC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.*;
import java.util.List;

/**
 * Panel de interfaz para Gramáticas Libres de Contexto
 */
public class GLCPanel extends BorderPane {

    private GLC glc;
    private TextArea outputArea;

    private TextField simboloInicialField;
    private TextField noTerminalesField;
    private TextField terminalesField;
    private TextArea produccionesArea;
    private TextField cadenaField;

    public GLCPanel() {
        // Asegúrate de que GLC use LinkedHashMap para el orden
        this.glc = new GLC();
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

        Label titleLabel = new Label("Definición de GLC");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label simboloInicialLabel = new Label("Símbolo inicial:");
        simboloInicialField = new TextField();
        simboloInicialField.setPromptText("S");

        Label noTerminalesLabel = new Label("No terminales (separados por comas):");
        noTerminalesField = new TextField();
        noTerminalesField.setPromptText("S,A,B");

        Label terminalesLabel = new Label("Terminales (sin separadores):");
        terminalesField = new TextField();
        terminalesField.setPromptText("ab");

        Label produccionesLabel = new Label("Producciones:");
        Label formatoLabel = new Label("Formato: A→aAbB o S→aSb o A→ε");
        formatoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        produccionesArea = new TextArea();
        produccionesArea.setPromptText("S→aSb\nS→ε");
        produccionesArea.setPrefRowCount(8);

        Label ejemploLabel = new Label(
                "Ejemplos:\n" +
                        "S→aA\n" +
                        "A→bA\n" +
                        "A→b"
        );
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

        Label titleLabel = new Label("Análisis de Cadenas");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextArea gramaticaView = new TextArea();
        gramaticaView.setEditable(false);
        gramaticaView.setPrefRowCount(10);
        gramaticaView.setPromptText("La gramática construida aparecerá aquí");
        gramaticaView.setStyle("-fx-font-family: 'Courier New';");

        VBox testBox = new VBox(10);
        testBox.setAlignment(Pos.CENTER);
        testBox.setPadding(new Insets(15));
        testBox.setStyle("-fx-background-color: #fff3e0; -fx-background-radius: 5;");

        Label testLabel = new Label("Cadena a analizar:");
        testLabel.setStyle("-fx-font-weight: bold;");

        cadenaField = new TextField();
        cadenaField.setPromptText("Ingrese la cadena");
        cadenaField.setPrefWidth(300);

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);

        // Nuevo Botón de Verificación
        Button verificarBtn = new Button("Verificar Cadena");
        verificarBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        verificarBtn.setPrefWidth(150);
        verificarBtn.setOnAction(e -> {
            verificarCadena();
            gramaticaView.setText(glc.getProduccionesTexto());
        });

        Button derivarIzqBtn = new Button("Derivar Izquierda");
        derivarIzqBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        derivarIzqBtn.setPrefWidth(150);
        derivarIzqBtn.setOnAction(e -> {
            derivarIzquierda();
            gramaticaView.setText(glc.getProduccionesTexto());
        });

        Button derivarDerBtn = new Button("Derivar Derecha");
        derivarDerBtn.setStyle("-fx-background-color: #03A9F4; -fx-text-fill: white;");
        derivarDerBtn.setPrefWidth(150);
        derivarDerBtn.setOnAction(e -> {
            derivarDerecha();
            gramaticaView.setText(glc.getProduccionesTexto());
        });

        Button arbolBtn = new Button("Árbol Sintáctico");
        arbolBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        arbolBtn.setPrefWidth(150);
        arbolBtn.setOnAction(e -> {
            generarArbolSintactico();
            gramaticaView.setText(glc.getProduccionesTexto());
        });

        // Distribución de botones corregida
        buttonGrid.add(verificarBtn, 0, 0);
        buttonGrid.add(derivarIzqBtn, 1, 0);
        buttonGrid.add(derivarDerBtn, 0, 1);
        buttonGrid.add(arbolBtn, 1, 1);

        testBox.getChildren().addAll(testLabel, cadenaField, buttonGrid);

        panel.getChildren().addAll(titleLabel, gramaticaView, testBox);
        return panel;
    }

    private VBox createResultsPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");

        Label titleLabel = new Label("Resultados");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(30);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11px;");

        Button limpiarBtn = new Button("Limpiar");
        limpiarBtn.setMaxWidth(Double.MAX_VALUE);
        limpiarBtn.setOnAction(e -> outputArea.clear());

        panel.getChildren().addAll(titleLabel, new Separator(), outputArea, limpiarBtn);
        return panel;
    }

    private void construirGramatica() {
        try {
            // Se debe reconstruir la GLC aquí para usar el LinkedHashMap
            glc = new GLC();

            String simboloInicial = simboloInicialField.getText().trim();
            glc.setSimboloInicial(simboloInicial);

            // Se agregan No Terminales primero para que el LinkedHashMap mantenga el orden de aparición
            String[] noTerminales = noTerminalesField.getText().trim().split(",");
            for (String nt : noTerminales) {
                glc.agregarNoTerminal(nt.trim());
            }

            String terminales = terminalesField.getText().trim();
            for (char t : terminales.toCharArray()) {
                glc.agregarTerminal(t);
            }

            String[] producciones = produccionesArea.getText().trim().split("\n");
            for (String prod : producciones) {
                if (prod.trim().isEmpty()) continue;

                String[] partes = prod.split("→");
                if (partes.length != 2) continue;

                String izq = partes[0].trim();
                String der = partes[1].trim();

                // Asegura que el NT izquierdo ya exista antes de agregar la producción
                if (!glc.getNoTerminales().contains(izq)) {
                    glc.agregarNoTerminal(izq);
                }
                glc.agregarProduccion(izq, der);
            }

            outputArea.setText("✓ Gramática construida exitosamente!\n\n" +
                    glc.getProduccionesTexto() + "\n" +
                    "No terminales: " + glc.getNoTerminales() + "\n" +
                    "Terminales: " + glc.getTerminales() + "\n" +
                    "Símbolo inicial: " + glc.getSimboloInicial());

        } catch (Exception e) {
            mostrarError("Error al construir gramática: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Nuevo método
    private void verificarCadena() {
        String cadena = cadenaField.getText().trim();

        if (glc.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        // Se usa la función de pertenencia basada en la derivación
        boolean pertenece = glc.pertenece(cadena);

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("VERIFICACIÓN DE CADENA\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");
        resultado.append("Gramática:\n").append(glc.getProduccionesTexto()).append("\n");

        if (pertenece) {
            resultado.append("✓ La cadena **PERTENECE** al lenguaje (se pudo derivar)");
        } else {
            resultado.append("✗ La cadena **NO PERTENECE** al lenguaje (falló la derivación)");
        }

        outputArea.setText(resultado.toString());
    }

    private void derivarIzquierda() {
        String cadena = cadenaField.getText().trim();

        if (glc.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        List<String> derivaciones = glc.derivarIzquierda(cadena);

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("DERIVACIÓN POR LA IZQUIERDA\n");
        resultado.append("Cadena objetivo: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        resultado.append("Gramática:\n").append(glc.getProduccionesTexto()).append("\n");

        resultado.append("Pasos de derivación:\n");
        // Mostrar la derivación si fue exitosa
        if (!derivaciones.isEmpty() && !derivaciones.get(0).equals("No se pudo derivar la cadena")) {
            for (int i = 0; i < derivaciones.size(); i++) {
                resultado.append(String.format("%2d. %s\n", i, derivaciones.get(i)));
            }
        } else {
            resultado.append("✗ No se pudo encontrar una derivación por la izquierda.");
        }

        outputArea.setText(resultado.toString());
    }

    private void derivarDerecha() {
        String cadena = cadenaField.getText().trim();

        if (glc.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        List<String> derivaciones = glc.derivarDerecha(cadena);

        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("DERIVACIÓN POR LA DERECHA\n");
        resultado.append("Cadena objetivo: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        resultado.append("Gramática:\n").append(glc.getProduccionesTexto()).append("\n");

        resultado.append("Pasos de derivación:\n");
        // Mostrar la derivación si fue exitosa
        if (!derivaciones.isEmpty() && !derivaciones.get(0).equals("No se pudo derivar la cadena")) {
            for (int i = 0; i < derivaciones.size(); i++) {
                resultado.append(String.format("%2d. %s\n", i, derivaciones.get(i)));
            }
        } else {
            resultado.append("✗ No se pudo encontrar una derivación por la derecha.");
        }

        outputArea.setText(resultado.toString());
    }

    private void generarArbolSintactico() {
        String cadena = cadenaField.getText().trim();

        if (glc.getNoTerminales().isEmpty()) {
            mostrarError("Primero debe construir la gramática");
            return;
        }

        // Se añade un try-catch para manejar errores potenciales de la recursión compleja
        GLC.NodoArbol arbol = null;
        try {
            arbol = glc.generarArbolSintactico(cadena);
        } catch (StackOverflowError e) {
            mostrarError("Error: Desbordamiento de pila. La cadena podría ser muy larga o la gramática ambigua/recursiva.");
            e.printStackTrace();
            return;
        }


        StringBuilder resultado = new StringBuilder();
        resultado.append("═══════════════════════════════\n");
        resultado.append("ÁRBOL SINTÁCTICO\n");
        resultado.append("Cadena: \"").append(cadena).append("\"\n");
        resultado.append("═══════════════════════════════\n\n");

        if (arbol != null) {
            resultado.append("Árbol de derivación:\n\n");
            resultado.append(glc.visualizarArbol(arbol));
        } else {
            resultado.append("✗ No se pudo generar el árbol sintáctico\n");
            resultado.append("La cadena podría no pertenecer al lenguaje o la lógica de parsing falló.");
        }

        outputArea.setText(resultado.toString());
    }

    // Métodos cargarDesdeArchivo, guardarEnArchivo, mostrarError (sin cambios)
    private void cargarDesdeArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Cargar GLC");
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
        fileChooser.setTitle("Guardar GLC");
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