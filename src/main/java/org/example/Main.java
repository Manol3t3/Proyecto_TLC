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

    public static void main(String[] args) {
        launch(args);
    }

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


        // Crear escena
        Scene scene = new Scene(root, 1200, 700);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}