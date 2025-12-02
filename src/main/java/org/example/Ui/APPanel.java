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

public class APPanel extends BorderPane {

    private AutomataDePila ap = new AutomataDePila();

    private TextArea outputArea;
    private ScrollPane pilaScroll;
    private Pane pilaPane;
    private TableView<TransicionRow> transicionTable;

    private TextField estadosField, alfabetoEntradaField, alfabetoPilaField,
            estadoInicialField, estadosFinalesField, cadenaField;
    private TextArea transicionesArea;

    public APPanel() { initUI(); }

    private void initUI() {
        setPadding(new Insets(15));
        setLeft(createDefinitionPanel());
        setCenter(createVisualizationPanel());
        setRight(createResultsPanel());
    }

    private VBox createDefinitionPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 5;");

        Label title = new Label("Definición del Autómata de Pila");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        estadosField = createTField("q0,q1");
        alfabetoEntradaField = createTField("()");
        alfabetoPilaField = createTField("X");
        estadoInicialField = createTField("q0");
        estadosFinalesField = createTField("q1");

        transicionesArea = new TextArea();
        transicionesArea.setPrefRowCount(6);

        Button construirBtn = new Button("Construir Autómata");
        construirBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        construirBtn.setMaxWidth(Double.MAX_VALUE);
        construirBtn.setOnAction(e -> construirAutomata());

        HBox ejemplos = new HBox(5);
        Button parBtn = new Button("Paréntesis balanceados");
        parBtn.setOnAction(ev -> cargarEjemploCorregido());
        ejemplos.getChildren().addAll(parBtn);

        HBox fileBar = new HBox(5);
        Button cargarBtn = new Button("Cargar");
        Button guardarBtn = new Button("Guardar");
        cargarBtn.setOnAction(ev -> cargarDesdeArchivo());
        guardarBtn.setOnAction(ev -> guardarEnArchivo());
        fileBar.getChildren().addAll(cargarBtn, guardarBtn);

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
        return new VBox(new ScrollPane(panel));
    }

    private VBox createVisualizationPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPrefWidth(500);

        pilaScroll = new ScrollPane();
        pilaScroll.setPrefSize(400, 250);
        pilaScroll.setStyle("-fx-border-color: #ccc; -fx-background-color: #ffffff;");
        pilaScroll.setFitToWidth(true);
        pilaScroll.setFitToHeight(false);

        pilaPane = new Pane();
        pilaPane.setPrefSize(400, 50);
        pilaScroll.setContent(pilaPane);

        cadenaField = createTField("()()");
        Button procesarBtn = new Button("Procesar");
        procesarBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        procesarBtn.setOnAction(e -> procesarCadena());

        transicionTable = new TableView<>();
        TableColumn<TransicionRow, String> c1 = new TableColumn<>("Estado");
        TableColumn<TransicionRow, String> c2 = new TableColumn<>("Entrada");
        TableColumn<TransicionRow, String> c3 = new TableColumn<>("Pila");
        TableColumn<TransicionRow, String> c4 = new TableColumn<>("→");
        TableColumn<TransicionRow, String> c5 = new TableColumn<>("Destino");
        TableColumn<TransicionRow, String> c6 = new TableColumn<>("Apilar");
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

    private VBox createResultsPanel() {
        VBox v = new VBox(10);
        v.setPadding(new Insets(10));
        v.setPrefWidth(350);
        outputArea = new TextArea();
        outputArea.setPrefRowCount(15);
        Button limpiar = new Button("Limpiar");
        limpiar.setOnAction(e -> { outputArea.clear(); pilaPane.getChildren().clear(); });
        v.getChildren().addAll(new Label("Historial y resultados"), outputArea, limpiar);
        return v;
    }

    /* --------------------- EJEMPLO CORREGIDO --------------------- */
    private void cargarEjemploCorregido() {
        estadosField.setText("q0");
        alfabetoEntradaField.setText("()");
        alfabetoPilaField.setText("X");
        estadoInicialField.setText("q0");
        estadosFinalesField.setText("q0");
        transicionesArea.setText(
                "q0,(,e,q0,X\n" +
                        "q0,),X,q0,e\n"
        );
        cadenaField.setText("()()");  // ← acepta
        construirAutomata();
    }

    /* --------------------- CONSTRUIR --------------------- */
    private void construirAutomata() {
        try {
            ap = new AutomataDePila();

            for (String e : estadosField.getText().split(",")) ap.agregarEstado(e.trim());
            for (char c : alfabetoEntradaField.getText().toCharArray()) ap.agregarSimboloEntrada(c);
            for (char c : alfabetoPilaField.getText().toCharArray()) ap.agregarSimboloPila(c);
            ap.setEstadoInicial(estadoInicialField.getText().trim());

            // ← SIEMPRE pila vacía inicial
            ap.setSimboloInicialPila('\0');

            for (String f : estadosFinalesField.getText().split(",")) ap.agregarEstadoFinal(f.trim());
            ap.setAceptarPorPilaVacia(true);   // aceptación por pila vacía activada

            transicionTable.getItems().clear();
            String[] lineas = transicionesArea.getText().split("\n");
            for (String l : lineas) {
                String[] p = l.split(",");
                if (p.length != 5) continue;
                Character entrada = p[1].trim().equals("e") ? null : p[1].charAt(0);
                Character pila    = p[2].trim().equals("e") ? null : p[2].charAt(0);
                String apilar     = p[4].trim().equals("e") ? "" : p[4].trim();
                ap.agregarTransicion(p[0].trim(), entrada, pila, p[3].trim(), apilar);

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
    private void procesarCadena() {
        String cadena = cadenaField.getText();
        if (cadena.isEmpty()) return;

        boolean aceptada = ap.procesar(cadena);
        outputArea.clear();

        if (aceptada) {
            outputArea.appendText("✅ CADENA ACEPTADA\n");
            String motivo = ap.getMotivoAceptacion(ap.getHistorial().get(ap.getHistorial().size() - 1).estado,
                    cadena.length(),
                    ap.getHistorial().get(ap.getHistorial().size() - 1).pila);
            outputArea.appendText("Motivo: " + motivo + "\n\n");
        } else {
            outputArea.appendText("❌ CADENA RECHAZADA\n");
            String motivo = ap.getMotivoRechazo(ap.getHistorial().get(ap.getHistorial().size() - 1).estado,
                    cadena.length(),
                    ap.getHistorial().get(ap.getHistorial().size() - 1).pila);
            outputArea.appendText("Motivo: " + motivo + "\n\n");
        }

        List<AutomataDePila.ConfiguracionAP> hist = ap.getHistorial();
        List<String> trApl = ap.getTransicionesAplicadas();
        for (int i = 0; i < hist.size(); i++) {
            outputArea.appendText("Paso " + i + ": " + hist.get(i) + "\n");
            if (i < trApl.size() && !trApl.get(i).isEmpty())
                outputArea.appendText("      Transición: " + trApl.get(i) + "\n");
        }
        if (!hist.isEmpty()) mostrarPila(hist.get(hist.size() - 1).pila);
    }

    /* --------------------- VISUAL --------------------- */
    private void mostrarPilaInicial() {
        pilaPane.getChildren().clear();
        Text t = new Text(150, 100, "Pila inicial: vacía");
        t.setFont(Font.font(18)); t.setFill(Color.GRAY);
        pilaPane.getChildren().add(t);
        pilaPane.setPrefHeight(250);
        pilaScroll.setVvalue(0);
    }

    private void mostrarPila(Stack<Character> pila) {
        pilaPane.getChildren().clear();

        final double ANCHO_RECT  = 100;
        final double ALTO_RECT   = 30;
        final double MARGEN_SUP  = 20;
        final double SEP_VERT    = 5;

        // ← Mensaje visual si está en estado final pero pila NO vacía
        String estadoActual = ap.getHistorial().get(ap.getHistorial().size() - 1).estado;
        if (ap.getEstadosFinales().contains(estadoActual) && !pila.isEmpty()) {
            Text warning = new Text(10, 15, "⚠ Estado final pero pila NO vacía → RECHAZADO");
            warning.setFill(Color.RED);
            warning.setFont(Font.font(14));
            pilaPane.getChildren().add(warning);
        }

        if (pila.isEmpty()) {
            Text t = new Text(150, 100, "PILA VACÍA");
            t.setFont(Font.font(20)); t.setFill(Color.GREEN);
            pilaPane.getChildren().add(t);
            pilaPane.setPrefHeight(250);
            pilaScroll.setVvalue(0);
            return;
        }

        List<Character> elems = new ArrayList<>(pila);
        Collections.reverse(elems);
        double alturaTotal = MARGEN_SUP + 20 + elems.size() * (ALTO_RECT + SEP_VERT);
        pilaPane.setPrefHeight(alturaTotal);
        pilaScroll.setVvalue(0);

        for (int i = 0; i < elems.size(); i++) {
            double y = MARGEN_SUP + 20 + i * (ALTO_RECT + SEP_VERT);
            Rectangle r = new Rectangle(150, y, ANCHO_RECT, ALTO_RECT);
            r.setFill(i == 0 ? Color.LIGHTCORAL : Color.LIGHTGREEN);
            r.setStroke(Color.GRAY);
            Text txt = new Text(150 + ANCHO_RECT / 2 - 5, y + ALTO_RECT / 2 + 5,
                    elems.get(i).toString());
            txt.setFont(Font.font(14));
            pilaPane.getChildren().addAll(r, txt);
        }
    }

    /* --------------------- FICHEROS --------------------- */
    private void cargarDesdeArchivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Cargar autómata");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File f = fc.showOpenDialog(null);
        if (f == null) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            estadosField.clear(); alfabetoEntradaField.clear(); alfabetoPilaField.clear();
            estadoInicialField.clear(); estadosFinalesField.clear();
            transicionesArea.clear();
            String l;
            while ((l = br.readLine()) != null) {
                if (l.startsWith("ESTADOS:")) estadosField.setText(l.substring(8).trim());
                else if (l.startsWith("ENTRADA:")) alfabetoEntradaField.setText(l.substring(8).trim());
                else if (l.startsWith("PILA:")) alfabetoPilaField.setText(l.substring(5).trim());
                else if (l.startsWith("INICIAL:")) estadoInicialField.setText(l.substring(8).trim());
                else if (l.startsWith("FINALES:")) estadosFinalesField.setText(l.substring(8).trim());
                else if (l.startsWith("TRANSICION:")) transicionesArea.appendText(l.substring(11).trim() + "\n");
            }
            outputArea.appendText("\n✓ Autómata cargado desde: " + f.getName());
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar:\n" + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void guardarEnArchivo() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar autómata");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto", "*.txt"));
        File f = fc.showSaveDialog(null);
        if (f == null) return;
        try (PrintWriter w = new PrintWriter(f)) {
            w.println("ESTADOS: " + estadosField.getText());
            w.println("ENTRADA: " + alfabetoEntradaField.getText());
            w.println("PILA: " + alfabetoPilaField.getText());
            w.println("INICIAL: " + estadoInicialField.getText());
            w.println("FINALES: " + estadosFinalesField.getText());
            w.println("=== TRANSICIONES ===");
            w.print(transicionesArea.getText());
            outputArea.appendText("\n✓ Autómata guardado en: " + f.getName());
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar:\n" + ex.getMessage());
            alert.showAndWait();
        }
    }

    /* --------------------- UTILS --------------------- */
    private TextField createTField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        return tf;
    }
    private VBox createLabeled(String label, Control c) {
        Label l = new Label(label);
        VBox v = new VBox(5, l, c);
        v.setPadding(new Insets(5));
        Tooltip.install(c, new Tooltip("Introduce los valores que desees – pila siempre vacía inicial"));
        return v;
    }

    /* --------------------- TABLE ROW --------------------- */
    public static class TransicionRow {
        public final javafx.beans.property.SimpleStringProperty
                estado, entrada, pila, flecha, destino, apilar;
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

