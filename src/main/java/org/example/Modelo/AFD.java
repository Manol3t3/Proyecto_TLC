package org.example.Modelo;

import java.util.*;

/**
 * Clase que representa un Autómata Finito Determinista (AFD).
 * Modela la 5-tupla (Q, Σ, δ, q0, F).
 */
public class AFD {
    // Q: Conjunto de estados
    private Set<String> estados;
    // Σ: Alfabeto de entrada
    private Set<Character> alfabeto;
    // δ: Función de transición. Mapea Estado -> (Símbolo -> EstadoDestino)
    private Map<String, Map<Character, String>> transiciones;
    // q0: Estado inicial
    private String estadoInicial;
    // F: Conjunto de estados finales
    private Set<String> estadosFinales;

    // Para almacenar el historial de ejecución (ruta de estados)
    private List<String> historialEstados;
    // Para almacenar los símbolos leídos en el orden de ejecución
    private List<Character> historialSimbolos;

    // Campo para almacenar la razón específica del rechazo o aceptación.
    private String resultadoEjecucion;

    /**
     * Constructor. Inicializa todas las estructuras de datos.
     */
    public AFD() {
        this.estados = new HashSet<>();
        this.alfabeto = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.estadosFinales = new HashSet<>();
        this.historialEstados = new ArrayList<>();
        this.historialSimbolos = new ArrayList<>();
        this.resultadoEjecucion = "No ejecutado";
    }

    /**
     * Agrega un nuevo estado al conjunto Q.
     */
    public void agregarEstado(String estado) {
        estados.add(estado);
        transiciones.putIfAbsent(estado, new HashMap<>());
    }

    /**
     * Agrega un símbolo al alfabeto Σ.
     */
    public void agregarSimbolo(char simbolo) {
        alfabeto.add(simbolo);
    }

    /**
     * Define una transición δ(estadoOrigen, simbolo) = estadoDestino.
     */
    public void agregarTransicion(String estadoOrigen, char simbolo, String estadoDestino) {
        if (!estados.contains(estadoOrigen) || !estados.contains(estadoDestino)) {
            throw new IllegalArgumentException("Estados no válidos");
        }
        if (!alfabeto.contains(simbolo)) {
            throw new IllegalArgumentException("Símbolo no pertenece al alfabeto");
        }
        transiciones.get(estadoOrigen).put(simbolo, estadoDestino);
    }

    /**
     * Establece el estado inicial q0.
     */
    public void setEstadoInicial(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        this.estadoInicial = estado;
    }

    /**
     * Agrega un estado al conjunto de estados finales F.
     */
    public void agregarEstadoFinal(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        estadosFinales.add(estado);
    }

    /**
     * Procesa una cadena de entrada.
     * Almacena la ruta de estados y la razón final (aceptada o rechazada) en 'resultadoEjecucion'.
     * @param cadena La cadena a procesar.
     * @return true si la cadena es aceptada, false en caso contrario.
     */
    public boolean procesar(String cadena) {
        // Limpia los historiales y resetea el resultado.
        historialEstados.clear();
        historialSimbolos.clear();
        resultadoEjecucion = "No ejecutado";

        String estadoActual = estadoInicial;
        historialEstados.add(estadoActual); // Guarda el estado inicial

        for (char simbolo : cadena.toCharArray()) {
            if (!alfabeto.contains(simbolo)) {
                // FALLO 1: Símbolo no es parte del alfabeto.
                resultadoEjecucion = String.format("✗ RECHAZADA (Símbolo '%c' no está en el alfabeto)", simbolo);
                return false;
            }

            historialSimbolos.add(simbolo);

            Map<Character, String> trans = transiciones.get(estadoActual);

            if (trans == null || !trans.containsKey(simbolo)) {
                // FALLO 2: Transición indefinida (estancamiento).
                resultadoEjecucion = String.format("✗ RECHAZADA (Transición indefinida en %s con '%c')", estadoActual, simbolo);
                return false;
            }

            // Realiza la transición al nuevo estado.
            estadoActual = trans.get(simbolo);
            historialEstados.add(estadoActual); // Guarda el estado alcanzado.
        }

        // Si se llegó al final de la cadena, verifica el estado final.
        if (estadosFinales.contains(estadoActual)) {
            resultadoEjecucion = "✓ ACEPTADA";
            return true;
        } else {
            // FALLO 3: Cadena consumida, pero no terminó en un estado final.
            resultadoEjecucion = String.format("✗ RECHAZADA (Estado final %s no es un estado de aceptación)", estadoActual);
            return false;
        }
    }

    /**
     * Obtiene el historial de ejecución paso a paso, incluyendo la razón de rechazo o aceptación.
     * @return Lista de strings que describen la ruta y el resultado final.
     */
    public List<String> obtenerPasoAPaso() {
        List<String> pasos = new ArrayList<>();

        if (historialEstados.isEmpty()) {
            pasos.add("El AFD no ha sido ejecutado o no tiene estado inicial definido.");
            return pasos;
        }

        pasos.add("Estado inicial: " + estadoInicial);

        // Muestra la transición para cada símbolo leído.
        for (int i = 0; i < historialSimbolos.size(); i++) {
            // Verifica que haya un estado de destino en el historial (se detiene si la ejecución falló antes)
            if (historialEstados.size() > i + 1) {
                pasos.add(String.format("Leer '%c' → Ir a %s",
                        historialSimbolos.get(i),
                        historialEstados.get(i + 1)));
            } else {
                // Si la ejecución se detuvo (por un fallo intermedio), solo mostramos hasta el último símbolo leído.
                pasos.add(String.format("Leer '%c' → Falla la ejecución", historialSimbolos.get(i)));
                break;
            }
        }

        // El último estado en el historial es el estado en el que se detuvo el procesamiento.
        String estadoFinal = historialEstados.get(historialEstados.size() - 1);
        pasos.add("Estado final alcanzado: " + estadoFinal);

        // Reporta el resultado específico (ACEPTADA o razón de RECHAZO).
        pasos.add(resultadoEjecucion);

        return pasos;
    }

    // --- Getters ---

    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public Map<String, Map<Character, String>> getTransiciones() { return transiciones; }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosFinales() { return estadosFinales; }
    public List<String> getHistorialEstados() { return historialEstados; }
}