package org.example;

import org.example.Ui.AFDPanel;
import org.example.Ui.GLCPanel;
import org.example.Ui.GRPanel;
import org.example.Ui.MTPanel;
import org.example.Ui.APPanel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.Ui.*;

/**
 * Aplicación principal del Simulador de Autómatas
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulador de Modelos de Computación - UADY");

        // Crear TabPane para los diferentes modos
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Crear pestañas para cada modo
        Tab afdTab = new Tab("AFD - Autómata Finito Determinista");
        afdTab.setContent(new AFDPanel());

        Tab grTab = new Tab("GR - Gramática Regular");
        grTab.setContent(new GRPanel());

        Tab glcTab = new Tab("GLC - Gramática Libre de Contexto");
        glcTab.setContent(new GLCPanel());

        Tab apTab = new Tab("AP - Autómata de Pila");
        apTab.setContent(new APPanel());

        Tab mtTab = new Tab("MT - Máquina de Turing (Extra)");
        mtTab.setContent(new MTPanel());

        // Agregar pestañas
        tabPane.getTabs().addAll(afdTab, grTab, glcTab, apTab, mtTab);

        // Layout principal
        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        // Barra de menú
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        // Crear escena
        Scene scene = new Scene(root, 1200, 700);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();

        // Menú Archivo
        Menu archivoMenu = new Menu("Archivo");
        MenuItem salirItem = new MenuItem("Salir");
        salirItem.setOnAction(e -> stage.close());
        archivoMenu.getItems().add(salirItem);

        // Menú Ayuda
        Menu ayudaMenu = new Menu("Ayuda");
        MenuItem acercaDeItem = new MenuItem("Acerca de");
        acercaDeItem.setOnAction(e -> mostrarAcercaDe());
        MenuItem manualItem = new MenuItem("Manual de Usuario");
        manualItem.setOnAction(e -> mostrarManual());
        ayudaMenu.getItems().addAll(acercaDeItem, manualItem);

        menuBar.getMenus().addAll(archivoMenu, ayudaMenu);
        return menuBar;
    }

    private void mostrarAcercaDe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Simulador de Modelos de Computación");
        alert.setContentText("Universidad Autónoma de Yucatán\n" +
                "Facultad de Matemáticas\n" +
                "Ingeniería de Software - 3er Semestre\n\n" +
                "Proyecto Final - Teoría de la Computación\n" +
                "Versión 1.0");
        alert.showAndWait();
    }

    private void mostrarManual() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Manual de Usuario");
        alert.setHeaderText("Instrucciones de Uso");
        alert.setContentText(
                "AFD: Define estados, alfabeto y transiciones.\n\n" +
                        "GR: Crea gramáticas regulares y verifica cadenas.\n\n" +
                        "GLC: Define gramáticas libres de contexto.\n\n" +
                        "AP: Simula autómatas de pila.\n\n" +
                        "MT: Simula máquinas de Turing."
        );
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}