package org.example.Ui;

import javafx.scene.shape.Rectangle;
import org.example.Modelo.AutomataDePila;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;

/**
 * Panel de interfaz para el Autómata de Pila (AP).
 * Extiende BorderPane para organizar la definición (izquierda),
 * visualización (centro) y resultados (derecha) de la ejecución de un AP.
 */
public class APPanel extends BorderPane {

    // Objeto que maneja la lógica central del Autómata de Pila.
    private AutomataDePila ap = new AutomataDePila();

    // Área para mostrar el historial de ejecución y resultados textuales.
    private TextArea outputArea;

    // Contenedor desplazable para la visualización de la pila.
    private ScrollPane pilaScroll;

    // Panel donde se dibujan las celdas de la pila.
    private Pane pilaPane;

    // Tabla para mostrar las transiciones definidas de forma estructurada.
    private TableView<TransicionRow> transicionTable;

    // Campos de entrada para la definición del AP (componentes formales).
    private TextField estadosField, alfabetoEntradaField, alfabetoPilaField,
            estadoInicialField, estadosFinalesField, cadenaField;

    // Área de texto para introducir las transiciones.
    private TextArea transicionesArea;

    /**
     * Constructor. Llama a la inicialización de la interfaz de usuario.
     */
    public APPanel() { initUI(); }

    /**
     * Inicializa la interfaz de usuario (UI), dividiéndola en tres secciones.
     */
    private void initUI() {
        setPadding(new Insets(15));
        setLeft(createDefinitionPanel());
        setCenter(createVisualizationPanel());
        setRight(createResultsPanel());
    }

    /**
     * Crea el panel de la izquierda para la definición del AP (la 7-tupla).
     * @return VBox con todos los campos de definición.
     */
    private VBox createDefinitionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5;");

        Label title = new Label("Definición del Autómata de Pila");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Inicialización de campos de texto con prompts de ejemplo.
        estadosField = createTField("q0,q1");
        alfabetoEntradaField = createTField("()");
        alfabetoPilaField = createTField("X");
        estadoInicialField = createTField("q0");
        estadosFinalesField = createTField("q1");

        transicionesArea = new TextArea();
        transicionesArea.setPrefRowCount(6);

        // Botón principal para construir el modelo AP.
        Button construirBtn = new Button("Construir Autómata");
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setOnAction(e -> construirAutomata());

        // Botones de ejemplo rápido.
        HBox ejemplos = new HBox(5);
        Button parBtn = new Button("Paréntesis balanceados");
        parBtn.setOnAction(ev -> cargarEjemploCorregido());
        ejemplos.getChildren().addAll(parBtn);

        // Botones de persistencia (Cargar/Guardar).
        HBox fileBar = new HBox(5);
        Button cargarBtn = new Button("Cargar");
        Button guardarBtn = new Button("Guardar");
        cargarBtn.setOnAction(ev -> cargarDesdeArchivo());
        guardarBtn.setOnAction(ev -> guardarEnArchivo());
        fileBar.getChildren().addAll(cargarBtn, guardarBtn);

        // Agregación de todos los componentes al panel.
        panel.getChildren().addAll(
                title,
                new Separator(),
                createLabeled("Estados", estadosField),
                createLabeled("Alfabeto entrada", alfabetoEntradaField),
                createLabeled("Alfabeto pila", alfabetoPilaField),
                createLabeled("Estado inicial", estadoInicialField),
                createLabeled("Estados finales", estadosFinalesField),
                createLabeled("Transiciones", transicionesArea),
                ejemplos,
                construirBtn,
                fileBar
        );
        // Envuelve el panel en un ScrollPane.
        return new VBox(new ScrollPane(panel));
    }

    /**
     * Crea el panel central para la visualización de la pila y la prueba de cadenas.
     * @return VBox con la visualización gráfica de la pila, la tabla de transiciones y los controles de prueba.
     */
    private VBox createVisualizationPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(500);

        // Configuración del ScrollPane y el Pane para dibujar la pila.
        pilaScroll = new ScrollPane();
        pilaScroll.setPrefSize(400, 250);
        pilaScroll.setStyle("-fx-border-color: #ccc; -fx-background-color: #ffffff;");
        pilaScroll.setFitToWidth(true);
        pilaScroll.setFitToHeight(false);

        pilaPane = new Pane();
        pilaPane.setPrefSize(400, 50);
        pilaScroll.setContent(pilaPane);

        // Controles de prueba de cadena.
        cadenaField = createTField("()()");
        Button procesarBtn = new Button("Procesar");
        procesarBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        procesarBtn.setOnAction(e -> procesarCadena());

        // Creación y configuración de la tabla de transiciones.
        transicionTable = new TableView<>();
        TableColumn<TransicionRow, String> c1 = new TableColumn<>("Estado");
        TableColumn<TransicionRow, String> c2 = new TableColumn<>("Entrada");
        TableColumn<TransicionRow, String> c3 = new TableColumn<>("Pila");
        TableColumn<TransicionRow, String> c4 = new TableColumn<>("→");
        TableColumn<TransicionRow, String> c5 = new TableColumn<>("Destino");
        TableColumn<TransicionRow, String> c6 = new TableColumn<>("Apilar");

        // Asignación de las propiedades del modelo (TransicionRow) a las columnas.
        c1.setCellValueFactory(d -> d.getValue().estado);
        c2.setCellValueFactory(d -> d.getValue().entrada);
        c3.setCellValueFactory(d -> d.getValue().pila);
        c4.setCellValueFactory(d -> d.getValue().flecha);
        c5.setCellValueFactory(d -> d.getValue().destino);
        c6.setCellValueFactory(d -> d.getValue().apilar);

        transicionTable.getColumns().addAll(c1, c2, c3, c4, c5, c6);
        transicionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        panel.getChildren().addAll(new Label("Visualización"), pilaScroll,
                new Label("Tabla de transiciones"), transicionTable,
                new Label("Probar cadena"), cadenaField, procesarBtn);
        return panel;
    }

    /**
     * Crea el panel de la derecha para mostrar el historial de ejecución y los resultados.
     * @return VBox con el área de salida.
     */
    private VBox createResultsPanel() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(10));
        v.setPrefWidth(350);
        outputArea = new TextArea();
        outputArea.setPrefRowCount(15);
        Button limpiar = new Button("Limpiar");
        // Limpia el área de texto y el dibujo de la pila.
        limpiar.setOnAction(e -> { outputArea.clear(); pilaPane.getChildren().clear(); });
        v.getChildren().addAll(new Label("Historial y resultados"), outputArea, limpiar);
        return v;
    }

    /* --------------------- EJEMPLO CORREGIDO --------------------- */
    /**
     * Carga el ejemplo predefinido para paréntesis balanceados.
     */
    private void cargarEjemploCorregido() {
        // Define los componentes del AP para el lenguaje L = {w | w tiene paréntesis balanceados}.
        estadosField.setText("q0");
        alfabetoEntradaField.setText("()");
        alfabetoPilaField.setText("X");
        estadoInicialField.setText("q0");
        estadosFinalesField.setText("q0");
        transicionesArea.setText(
                "q0,(,e,q0,X\n" + // Si lee '(', no lee nada de pila (e), va a q0, apila 'X'.
                        "q0,),X,q0,e\n"  // Si lee ')', lee 'X', va a q0, desapila (e).
        );
        cadenaField.setText("()()");
        construirAutomata();
    }

    /* --------------------- CONSTRUIR --------------------- */
    /**
     * Parsea los datos de los campos de texto y construye el objeto AutomataDePila.
     * También actualiza la tabla de transiciones en la UI.
     */
    private void construirAutomata() {
        try {
            ap = new AutomataDePila();

            // 1. Componentes formales (Estados, Alfabetos, Iniciales)
            for (String e : estadosField.getText().split(",")) ap.agregarEstado(e.trim());
            for (char c : alfabetoEntradaField.getText().toCharArray()) ap.agregarSimboloEntrada(c);
            for (char c : alfabetoPilaField.getText().toCharArray()) ap.agregarSimboloPila(c);
            ap.setEstadoInicial(estadoInicialField.getText().trim());

            // Nota: Se asume que el símbolo de pila inicial es nulo ('\0') para un AP que inicia con pila vacía.
            ap.setSimboloInicialPila('\0');

            // 2. Estados finales y aceptación
            for (String f : estadosFinalesField.getText().split(",")) ap.agregarEstadoFinal(f.trim());
            // Se fuerza la aceptación por pila vacía, además de por estado final.
            ap.setAceptarPorPilaVacia(true);

            // 3. Transiciones
            transicionTable.getItems().clear();
            String[] lineas = transicionesArea.getText().split("\n");
            for (String l : lineas) {
                String[] p = l.split(",");
                if (p.length != 5) continue; // Formato esperado: q_origen, entrada, pop, q_destino, push

                // Convierte "e" (epsilon) a null para la entrada y el pop.
                Character entrada = p[1].trim().equals("e") ? null : p[1].charAt(0);
                Character pila    = p[2].trim().equals("e") ? null : p[2].charAt(0);

                // Convierte "e" a cadena vacía para el push (apilar).
                String apilar     = p[4].trim().equals("e") ? "" : p[4].trim();

                // Agrega la transición al modelo lógico.
                ap.agregarTransicion(p[0].trim(), entrada, pila, p[3].trim(), apilar);

                // Agrega la fila a la tabla de la UI, usando "ε" para representar null/cadena vacía.
                transicionTable.getItems().add(new TransicionRow(
                        p[0].trim(),
                        entrada == null ? "ε" : entrada.toString(),
                        pila    == null ? "ε" : pila.toString(),
                        "→",
                        p[3].trim(),
                        apilar.isEmpty() ? "ε" : apilar
                ));
            }

            outputArea.setText("✓ Autómata construido (pila vacía inicial)\n");
            mostrarPilaInicial();

        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al construir:\n" + ex.getMessage());
            alert.showAndWait();
        }
    }

    /* --------------------- PROCESAR --------------------- */
    /**
     * Ejecuta el procesamiento de la cadena en el AP y muestra el historial y el resultado.
     */
    private void procesarCadena() {
        String cadena = cadenaField.getText();
        if (cadena.isEmpty()) return;

        boolean aceptada = ap.procesar(cadena);
        outputArea.clear();

        // Muestra el resultado final (Aceptada/Rechazada) y el motivo.
        if (aceptada) {
            outputArea.appendText("CADENA ACEPTADA\n");
            // Se asume que el modelo proporciona métodos para obtener el motivo de la aceptación/rechazo.
            String motivo = ap.getMotivoAceptacion(ap.getHistorial().get(ap.getHistorial().size() - 1).estado,
                    cadena.length(),
                    ap.getHistorial().get(ap.getHistorial().size() - 1).pila);
            outputArea.appendText("Motivo: " + motivo + "\n\n");
        } else {
            outputArea.appendText(" CADENA RECHAZADA\n");
            String motivo = ap.getMotivoRechazo(ap.getHistorial().get(ap.getHistorial().size() - 1).estado,
                    cadena.length(),
                    ap.getHistorial().get(ap.getHistorial().size() - 1).pila);
            outputArea.appendText("Motivo: " + motivo + "\n\n");
        }

        // Muestra el historial de configuraciones.
        List<AutomataDePila.ConfiguracionAP> hist = ap.getHistorial();
        List<String> trApl = ap.getTransicionesAplicadas();
        for (int i = 0; i < hist.size(); i++) {
            outputArea.appendText("Paso " + i + ": " + hist.get(i) + "\n");
            if (i < trApl.size() && !trApl.get(i).isEmpty())
                outputArea.appendText("      Transición: " + trApl.get(i) + "\n");
        }

        // Muestra la pila final gráficamente.
        if (!hist.isEmpty()) mostrarPila(hist.get(hist.size() - 1).pila);
    }

    /* --------------------- VISUAL --------------------- */
    /**
     * Muestra un mensaje indicando que la pila está inicialmente vacía.
     */
    private void mostrarPilaInicial() {
        pilaPane.getChildren().clear();
        Text t = new Text(150, 100, "Pila inicial: vacía");
        t.setFont(Font.font(18)); t.setFill(Color.GRAY);
        pilaPane.getChildren().add(t);
        pilaPane.setPrefHeight(250);
        pilaScroll.setVvalue(0);
    }

    /**
     * Dibuja el estado actual de la pila en el 'pilaPane'.
     * @param pila El stack de caracteres a dibujar.
     */
    private void mostrarPila(Stack<Character> pila) {
        pilaPane.getChildren().clear();

        final double ANCHO_RECT  = 100;
        final double ALTO_RECT   = 30;
        final double MARGEN_SUP  = 20;
        final double SEP_VERT    = 5;

        // Recupera el último estado para verificar la condición de aceptación por estado final.
        String estadoActual = ap.getHistorial().get(ap.getHistorial().size() - 1).estado;
        if (ap.getEstadosFinales().contains(estadoActual) && !pila.isEmpty()) {
            // Advertencia visual si se cumple aceptación por estado pero no por pila vacía.
            Text warning = new Text(10, 15, "⚠ Estado final pero pila NO vacía → RECHAZADO");
            warning.setFill(Color.RED);
            warning.setFont(Font.font(14));
            pilaPane.getChildren().add(warning);
        }

        // Caso especial: Pila vacía.
        if (pila.isEmpty()) {
            Text t = new Text(150, 100, "PILA VACÍA");
            t.setFont(Font.font(20)); t.setFill(Color.GREEN);
            pilaPane.getChildren().add(t);
            pilaPane.setPrefHeight(250);
            pilaScroll.setVvalue(0);
            return;
        }

        // Preparación para dibujar: Invertir el stack para dibujar el tope arriba.
        List<Character> elems = new ArrayList<>(pila);
        Collections.reverse(elems);

        // Ajusta la altura del panel para que el scroll funcione correctamente.
        double alturaTotal = MARGEN_SUP + 20 + elems.size() * (ALTO_RECT + SEP_VERT);
        pilaPane.setPrefHeight(alturaTotal);
        pilaScroll.setVvalue(0); // Muestra siempre la parte superior de la pila.

        // Dibujo de cada elemento (rectángulo y texto).
        for (int i = 0; i < elems.size(); i++) {
            double y = MARGEN_SUP + 20 + i * (ALTO_RECT + SEP_VERT);
            Rectangle r = new Rectangle(150, y, ANCHO_RECT, ALTO_RECT);
            // El tope de la pila (primer elemento en la lista invertida, i=0) tiene color diferente.
            r.setFill(i == 0 ? Color.LIGHTCORAL : Color.LIGHTGREEN);
            r.setStroke(Color.GRAY);
            Text txt = new Text(150 + ANCHO_RECT / 2 - 5, y + ALTO_RECT / 2 + 5,
                    elems.get(i).toString());
            txt.setFont(Font.font(14));
            pilaPane.getChildren().addAll(r, txt);
        }
        //
    }

    /* --------------------- FICHEROS --------------------- */
    /**
     * Carga la definición del AP desde un archivo de texto, basándose en prefijos (EJ: ESTADOS:, ENTRADA:).
     */
    private void cargarDesdeArchivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Cargar autómata");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File f = fc.showOpenDialog(null);
        if (f == null) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            // Limpia los campos antes de cargar nuevos datos.
            estadosField.clear(); alfabetoEntradaField.clear(); alfabetoPilaField.clear();
            estadoInicialField.clear(); estadosFinalesField.clear();
            transicionesArea.clear();
            String l;
            // Lee línea por línea y parsea según el prefijo.
            while ((l = br.readLine()) != null) {
                if (l.startsWith("ESTADOS:")) estadosField.setText(l.substring(8).trim());
                else if (l.startsWith("ENTRADA:")) alfabetoEntradaField.setText(l.substring(8).trim());
                else if (l.startsWith("PILA:")) alfabetoPilaField.setText(l.substring(5).trim());
                else if (l.startsWith("INICIAL:")) estadoInicialField.setText(l.substring(8).trim());
                else if (l.startsWith("FINALES:")) estadosFinalesField.setText(l.substring(8).trim());
                    // Las transiciones se concatenan en el TextArea.
                else if (l.startsWith("TRANSICION:")) transicionesArea.appendText(l.substring(11).trim() + "\n");
            }
            outputArea.appendText("\n Autómata cargado desde: " + f.getName());
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar:\n" + ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Guarda la definición actual del AP en un archivo de texto, incluyendo prefijos.
     */
    private void guardarEnArchivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar autómata");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File f = fc.showSaveDialog(null);
        if (f == null) return;
        try (PrintWriter w = new PrintWriter(f)) {
            // Escribe los campos de texto con sus prefijos correspondientes.
            w.println("ESTADOS: " + estadosField.getText());
            w.println("ENTRADA: " + alfabetoEntradaField.getText());
            w.println("PILA: " + alfabetoPilaField.getText());
            w.println("INICIAL: " + estadoInicialField.getText());
            w.println("FINALES: " + estadosFinalesField.getText());
            w.println("=== TRANSICIONES ===");
            // Escribe el contenido del TextArea de transiciones.
            w.print(transicionesArea.getText());
            outputArea.appendText("\n Autómata guardado en: " + f.getName());
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar:\n" + ex.getMessage());
            alert.showAndWait();
        }
    }

    /* --------------------- UTILS --------------------- */
    /**
     * Método utilitario para crear un TextField simple.
     * @param prompt El texto de sugerencia.
     * @return El TextField creado.
     */
    private TextField createTField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        return tf;
    }

    /**
     * Método utilitario para crear un par de etiqueta y control.
     * @param label El texto de la etiqueta.
     * @param c El control de JavaFX (TextField, TextArea, etc.).
     * @return VBox conteniendo la etiqueta y el control.
     */
    private VBox createLabeled(String label, Control c) {
        Label l = new Label(label);
        VBox v = new VBox(5, l, c);
        v.setPadding(new Insets(5));
        // Tooltip (información sobre herramientas) para guiar al usuario.
        Tooltip.install(c, new Tooltip("Introduce los valores que desees – pila siempre vacía inicial"));
        return v;
    }

    /* ------- TABLE ROW ------  */
    /**
     * Clase interna para representar una fila de la tabla de transiciones,
     * utilizando propiedades de JavaFX para la vinculación de datos.
     */
    public static class TransicionRow {
        public final javafx.beans.property.SimpleStringProperty
                estado, entrada, pila, flecha, destino, apilar;

        /**
         * Constructor para la fila de transición.
         */
        public TransicionRow(String e, String in, String p,
                             String f, String d, String a) {
            estado  = new javafx.beans.property.SimpleStringProperty(e);
            entrada = new javafx.beans.property.SimpleStringProperty(in);
            pila    = new javafx.beans.property.SimpleStringProperty(p);
            flecha  = new javafx.beans.property.SimpleStringProperty(f);
            destino = new javafx.beans.property.SimpleStringProperty(d);
            apilar  = new javafx.beans.property.SimpleStringProperty(a);
        }
    }
}