package org.example.Modelo;

import java.util.*;

/**
 * Clase que representa un Autómata Finito Determinista (AFD).
 * Esta clase permite construir el autómata y simular el procesamiento de una cadena de entrada.
 */
public class AFD {
    // Q: Conjunto de estados
    private Set<String> estados;
    // Σ: Alfabeto de entrada
    private Set<Character> alfabeto;
    // δ: Función de transición. Mapea Estado -> (Símbolo -> EstadoDestino)
    // Es la estructura principal para la ejecución determinista.
    private Map<String, Map<Character, String>> transiciones;

    // Para dibujo: Mapea origen -> símbolo -> lista de posibles destinos.
    // Se usa para almacenar todas las definiciones ingresadas, lo que es útil para la visualización gráfica.
    private Map<String, Map<Character, List<String>>> transicionesMultiples;

    // q0: Estado inicial
    private String estadoInicial;
    // F: Conjunto de estados finales
    private Set<String> estadosFinales;

    // --- Atributos de Historial y Resultado de Ejecución ---

    // Almacena la ruta de estados recorrida (incluyendo el estado inicial)
    private List<String> historialEstados;
    // Almacena los símbolos leídos en orden
    private List<Character> historialSimbolos;
    // Contiene el resultado final y el motivo ("✓ ACEPTADA", "✗ RECHAZADA", etc.)
    private String resultadoEjecucion;

    /**
     * Constructor. Inicializa todas las estructuras de datos (Sets, Maps y Lists)
     * para asegurar un estado inicial limpio del autómata.
     */
    public AFD() {
        this.estados = new HashSet<>();
        this.alfabeto = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.transicionesMultiples = new HashMap<>();
        this.estadosFinales = new HashSet<>();
        this.historialEstados = new ArrayList<>();
        this.historialSimbolos = new ArrayList<>();
        this.resultadoEjecucion = "No ejecutado";
    }

    /**
     * Agrega un nuevo estado al conjunto Q y lo inicializa en los mapas de transición.
     * @param estado El identificador (nombre) del estado a agregar.
     */
    public void agregarEstado(String estado) {
        estados.add(estado);
        // Inicializa el mapa interno de transiciones para el nuevo estado
        transiciones.putIfAbsent(estado, new HashMap<>());
        transicionesMultiples.putIfAbsent(estado, new HashMap<>());
    }

    /**
     * Agrega un símbolo al alfabeto Σ del autómata.
     * @param simbolo El carácter que representa un símbolo de entrada.
     */
    public void agregarSimbolo(char simbolo) {
        alfabeto.add(simbolo);
    }

    /**
     * Define una transición δ(estadoOrigen, simbolo) = estadoDestino.
     * <p>
     * Para el AFD real, solo se guarda la ÚLTIMA transición para un par (origen, símbolo).
     * Para fines de dibujo/visualización, guarda TODAS las definiciones.
     *
     * @param estadoOrigen El estado desde el que parte la transición.
     * @param simbolo El símbolo que dispara la transición.
     * @param estadoDestino El estado al que se llega.
     * @throws IllegalArgumentException si los estados no han sido agregados o el símbolo no está en el alfabeto.
     */
    public void agregarTransicion(String estadoOrigen, char simbolo, String estadoDestino) {
        if (!estados.contains(estadoOrigen) || !estados.contains(estadoDestino))
            throw new IllegalArgumentException("Estados no válidos: El origen o destino no existen en Q.");

        if (!alfabeto.contains(simbolo))
            throw new IllegalArgumentException("Símbolo no pertenece al alfabeto Σ.");

        // AFD real, la función de transición es total, por lo que reemplaza cualquier transición anterior
        transiciones.get(estadoOrigen).put(simbolo, estadoDestino);

        // Para dibujar, se añade la definición a la lista (útil para AFNs o visualización de múltiples definiciones)
        transicionesMultiples.get(estadoOrigen)
                .computeIfAbsent(simbolo, k -> new ArrayList<>())
                .add(estadoDestino);
    }

    /**
     * Establece el estado inicial q0 del autómata.
     * @param estado El identificador del estado inicial.
     * @throws IllegalArgumentException si el estado no existe en Q.
     */
    public void setEstadoInicial(String estado) {
        if (!estados.contains(estado))
            throw new IllegalArgumentException("El estado inicial especificado no existe en Q.");
        this.estadoInicial = estado;
    }

    /**
     * Agrega un estado al conjunto F de estados finales (aceptación).
     * @param estado El identificador del estado final.
     * @throws IllegalArgumentException si el estado no existe en Q.
     */
    public void agregarEstadoFinal(String estado) {
        if (!estados.contains(estado))
            throw new IllegalArgumentException("El estado final especificado no existe en Q.");
        estadosFinales.add(estado);
    }

    /**
     * Procesa una cadena de entrada de forma determinista.
     * @param cadena La cadena de símbolos a evaluar.
     * @return {@code true} si la cadena es aceptada (termina en un estado final), {@code false} en caso contrario.
     */
    public boolean procesar(String cadena) {
        // Limpiar el historial de ejecuciones previas
        historialEstados.clear();
        historialSimbolos.clear();

        String estadoActual = estadoInicial;
        historialEstados.add(estadoActual); // Registrar el estado inicial

        // Iterar sobre cada símbolo de la cadena
        for (char simbolo : cadena.toCharArray()) {
            if (!alfabeto.contains(simbolo)) {
                resultadoEjecucion = "✗ RECHAZADA (símbolo '" + simbolo + "' no está en el alfabeto Σ)";
                return false;
            }

            historialSimbolos.add(simbolo);

            Map<Character, String> trans = transiciones.get(estadoActual);

            // Verificar si existe una transición definida (AFD debe ser completo o manejar pozos implícitos)
            if (trans == null || !trans.containsKey(simbolo)) {
                resultadoEjecucion = "✗ RECHAZADA (no hay transición definida desde " + estadoActual + " con '" + simbolo + "')";
                return false;
            }

            // Moverse al siguiente estado (determinista)
            estadoActual = trans.get(simbolo);
            historialEstados.add(estadoActual); // Registrar el nuevo estado
        }

        // --- Verificación final ---
        if (estadosFinales.contains(estadoActual)) {
            resultadoEjecucion = "✓ ACEPTADA";
            return true;
        } else {
            resultadoEjecucion = "✗ RECHAZADA (termina en estado no final: " + estadoActual + ")";
            return false;
        }
    }

    /**
     * Obtiene una representación textual paso a paso de la última ejecución de {@link #procesar(String)}.
     * @return Una lista de cadenas que detallan el recorrido estado por estado.
     */
    public List<String> obtenerPasoAPaso() {
        List<String> pasos = new ArrayList<>();
        pasos.add("Estado inicial: " + estadoInicial);

        // Recorrer los símbolos leídos y los estados visitados
        for (int i = 0; i < historialSimbolos.size(); i++) {
            pasos.add("Leer '" + historialSimbolos.get(i) + "' → Moverse a estado: " + historialEstados.get(i + 1));
        }

        // Agregar el resumen final
        if (!historialEstados.isEmpty()) {
            pasos.add("Estado final alcanzado: " + historialEstados.get(historialEstados.size()-1));
        }
        pasos.add("Resultado: " + resultadoEjecucion);

        return pasos;
    }

    //Getters

    /** @return El conjunto Q de estados. */
    public Set<String> getEstados() { return estados; }

    /** @return El alfabeto Σ. */
    public Set<Character> getAlfabeto() { return alfabeto; }

    /** * Obtiene el mapa de transiciones completo.
     * @return El mapa que representa la función de transición δ.
     */
    public Map<String, Map<Character, String>> getTransiciones() { return transiciones; }

    /** * Obtiene la estructura de transiciones para fines de dibujo/visualización.
     * Es la estructura que almacena TODAS las definiciones ingresadas, incluso si el AFD sobrescribe la transición.
     * @return El mapa de transiciones múltiples.
     */
    public Map<String, Map<Character, List<String>>> getTransicionesMultiples() {
        return transicionesMultiples;
    }

    /** @return El estado inicial q0. */
    public String getEstadoInicial() { return estadoInicial; }

    /** @return El conjunto F de estados finales. */
    public Set<String> getEstadosFinales() { return estadosFinales; }
}