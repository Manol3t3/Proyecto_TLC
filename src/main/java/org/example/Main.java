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

/**
 * Clase principal del programa "Simulador de Modelos de Computación - UADY".
 *
 * <p>Esta aplicación integra distintos módulos visuales para simular:
 * AFD – Autómatas Finitos Deterministas</li>
 * GR – Gramáticas Regulares</li>
 * GLC – Gramáticas Libres de Contexto</li>
 * AP – Autómatas de Pila</li>
 * MT – Máquinas de Turing</li>
 * Cada módulo se muestra dentro de una pestaña dentro de un {@link TabPane},
 * permitiendo cambiar entre modelos de computación de forma organizada.
 *
 * <p>La clase extiende {@link Application} para iniciar la interfaz gráfica con JavaFX.</p>
 */
public class Main extends Application {

    /**
     * Método principal estándar en aplicaciones Java.
     * Lanza la aplicación JavaFX.
     *
     * @param args argumentos de ejecución (no utilizados).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Método de entrada de JavaFX. Construye la ventana principal y configura
     * todas las pestañas con los módulos disponibles.
     *
     * @param primaryStage la ventana principal de la aplicación.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simulador de Modelos de Computación - UADY");

        // Crear contenedor de pestañas para organizar los módulos
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        //   Creación de pestañas
        /**
         * Cada pestaña contiene un panel especializado para un modelo de
         * computación. Estos paneles están ubicados en el paquete Ui.
         */

        // AFD - Autómata Finito Determinista
        Tab afdTab = new Tab("AFD - Autómata Finito Determinista");
        afdTab.setContent(new AFDPanel());

        // GR - Gramática Regular
        Tab grTab = new Tab("GR - Gramática Regular");
        grTab.setContent(new GRPanel());

        // GLC - Gramática Libre de Contexto
        Tab glcTab = new Tab("GLC - Gramática Libre de Contexto");
        glcTab.setContent(new GLCPanel());

        // AP - Autómata de Pila
        Tab apTab = new Tab("AP - Autómata de Pila");
        apTab.setContent(new APPanel());

        // MT - Máquina de Turing
        Tab mtTab = new Tab("MT - Máquina de Turing (Extra)");
        mtTab.setContent(new MTPanel());

        // Agregar pestañas al TabPane
        tabPane.getTabs().addAll(afdTab, grTab, glcTab, apTab, mtTab);

        // Layout general usando BorderPane
        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        // Crear escena principal
        Scene scene = new Scene(root, 1200, 700);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}