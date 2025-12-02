package org.example.Modelo;

import java.util.*;

/**
 * Implementación genérica de un Autómata de Pila (AP).
 *
 * Criterios de aceptación configurables:
 *  - Consumir toda la entrada
 *  - Estar en estado final
 *  - Pila vacía
 *
 * Por defecto se acepta **por pila vacía**, pero puede alterarse mediante
 * setAceptarPorPilaVacia(boolean).
 */
public class AutomataDePila {

    /* ------------- ATRIBUTOS ---------------- */

    /** Conjunto de estados del AP */
    private final Set<String> estados = new HashSet<>();

    /** Alfabeto de entrada */
    private final Set<Character> alfabetoEntrada = new HashSet<>();

    /** Alfabeto de símbolos de pila */
    private final Set<Character> alfabetoPila = new HashSet<>();

    /**
     * Tabla de transiciones:
     * (estado, entrada, cimaPila) → lista de transiciones posibles.
     * Permite no determinismo.
     */
    private final Map<TransicionKey, List<TransicionValor>> transiciones = new HashMap<>();

    /** Conjunto de estados finales */
    private final Set<String> estadosFinales = new HashSet<>();

    /** Estado inicial del AP */
    private String estadoInicial;

    /** Símbolo inicial de la pila (opcional) */
    private char simboloInicialPila = '\0';

    /** Si true, aceptar también por pila vacía */
    private boolean aceptarPorPilaVacia = true;

    /** Historial de configuraciones visitadas */
    private final List<ConfiguracionAP> historial = new ArrayList<>();

    /** Lista de transiciones aplicadas para depuración */
    private final List<String> transicionesAplicadas = new ArrayList<>();

    /** Cadena que se procesa actualmente */
    private String cadenaOriginal;


    /* ----------- CLASES INTERNAS --------------- */

    /**
     * Clave de transición (estado, entrada, cima de pila).
     * null representa ε.
     */
    public static class TransicionKey {
        public final String estado;
        public final Character simboloEntrada; // null = ε
        public final Character simboloPila;    // null = ε

        public TransicionKey(String e, Character in, Character p) {
            this.estado = e;
            this.simboloEntrada = in;
            this.simboloPila = p;
        }

        @Override public boolean equals(Object o) {
            if (!(o instanceof TransicionKey)) return false;
            TransicionKey k = (TransicionKey) o;
            return Objects.equals(estado, k.estado) &&
                    Objects.equals(simboloEntrada, k.simboloEntrada) &&
                    Objects.equals(simboloPila, k.simboloPila);
        }

        @Override public int hashCode() {
            return Objects.hash(estado, simboloEntrada, simboloPila);
        }

        @Override public String toString() {
            String in = simboloEntrada == null ? "ε" : simboloEntrada.toString();
            String p  = simboloPila    == null ? "ε" : simboloPila.toString();
            return "(" + estado + ", " + in + ", " + p + ")";
        }
    }

    /**
     * Valor de una transición: estado destino + cadena a apilar.
     * cadenaApilar = "" representa ε.
     */
    public static class TransicionValor {
        public final String estadoDestino;
        public final String cadenaApilar;

        public TransicionValor(String ed, String ap) {
            this.estadoDestino = ed;
            this.cadenaApilar  = ap;
        }

        @Override public String toString() {
            return "(" + estadoDestino + ", " +
                    (cadenaApilar.isEmpty() ? "ε" : cadenaApilar) + ")";
        }
    }

    /**
     * Representa una configuración del AP:
     * (estado actual, entrada restante, contenido de pila).
     */
    public static class ConfiguracionAP {
        public final String estado;
        public final String cadenaRestante;
        public final Stack<Character> pila;

        public ConfiguracionAP(String e, String cr, Stack<Character> p) {
            this.estado = e;
            this.cadenaRestante = cr;
            this.pila = new Stack<>();
            this.pila.addAll(p);
        }

        /** Representación de la pila con el tope a la izquierda */
        public String getPilaString() {
            if (pila.isEmpty()) return "ε";
            StringBuilder sb = new StringBuilder();
            for (int i = pila.size() - 1; i >= 0; i--)
                sb.append(pila.get(i));
            return sb.toString();
        }

        @Override public String toString() {
            String cr = cadenaRestante.isEmpty() ? "ε" : cadenaRestante;
            return "(" + estado + ", " + cr + ", " + getPilaString() + ")";
        }
    }


    /* ----------- MÉTODOS BÁSICOS ----------- */

    public void agregarEstado(String e)               { estados.add(e); }
    public void agregarSimboloEntrada(char c)         { alfabetoEntrada.add(c); }
    public void agregarSimboloPila(char c)            { alfabetoPila.add(c); }
    public void setAceptarPorPilaVacia(boolean b)     { aceptarPorPilaVacia = b; }

    /** Define el estado inicial */
    public void setEstadoInicial(String e) {
        if (!estados.contains(e))
            throw new IllegalArgumentException("Estado no existe: " + e);
        estadoInicial = e;
    }

    /** Define el símbolo inicial de la pila */
    public void setSimboloInicialPila(char c) {
        simboloInicialPila = c;
    }

    /** Agrega un estado final */
    public void agregarEstadoFinal(String e) {
        if (!estados.contains(e))
            throw new IllegalArgumentException("Estado no existe: " + e);
        estadosFinales.add(e);
    }

    /**
     * Agrega una transición del AP.
     * entrada = null → ε
     * pila = null → ε
     */
    public void agregarTransicion(String origen, Character entrada, Character pila,
                                  String destino, String apilar) {

        if (!estados.contains(origen) || !estados.contains(destino))
            throw new IllegalArgumentException("Estados inválidos en transición");

        if (entrada != null && !alfabetoEntrada.contains(entrada))
            throw new IllegalArgumentException("Símbolo de entrada no válido: " + entrada);

        if (pila != null && !alfabetoPila.contains(pila))
            throw new IllegalArgumentException("Símbolo de pila no válido: " + pila);

        TransicionKey key = new TransicionKey(origen, entrada, pila);
        transiciones.putIfAbsent(key, new ArrayList<>());
        transiciones.get(key).add(new TransicionValor(destino, apilar));
    }


    /* -------- PROCESAMIENTO ---------- */

    /**
     * Procesa una cadena usando backtracking.
     * Inicia desde estadoInicial y pila vacía.
     */
    public boolean procesar(String cadena) {
        historial.clear();
        transicionesAplicadas.clear();
        cadenaOriginal = cadena;
        Stack<Character> pila = new Stack<>();
        return procesarRecursivo(estadoInicial, 0, pila, "");
    }

    /**
     * Método recursivo central del AP.
     * Maneja:
     *   - consumo de símbolos
     *   - ε-transiciones
     *   - no determinismo
     */
    private boolean procesarRecursivo(String estadoActual, int pos,
                                      Stack<Character> pila, String transPrev) {

        // Guardar configuración actual para visualización
        String restante = pos < cadenaOriginal.length()
                ? cadenaOriginal.substring(pos) : "";
        historial.add(new ConfiguracionAP(estadoActual, restante, pila));
        transicionesAplicadas.add(transPrev);

        // Verificar condiciones de aceptación
        if (pos == cadenaOriginal.length()) {
            boolean enEstadoFinal = estadosFinales.contains(estadoActual);
            boolean pilaVacia = pila.isEmpty();

            if (enEstadoFinal && (!aceptarPorPilaVacia || pilaVacia)) return true;
            if (aceptarPorPilaVacia && pilaVacia) return true;
        }

        // Transiciones que consumen símbolo
        if (pos < cadenaOriginal.length()) {
            char sim = cadenaOriginal.charAt(pos);
            if (intentarTransiciones(estadoActual, sim, topePila(pila),
                    pos + 1, pila))
                return true;
        }

        // Transiciones ε
        if (intentarTransiciones(estadoActual, null, topePila(pila),
                pos, pila))
            return true;

        return false; // backtracking completo
    }

    /* ---------- AUXILIARES ---------- */

    private Character topePila(Stack<Character> p) {
        return p.isEmpty() ? null : p.peek();
    }

    /**
     * Intenta aplicar transiciones para:
     *   - (entrada, topePila)
     *   - (entrada, ε)
     */
    private boolean intentarTransiciones(String estadoActual, Character entrada,
                                         Character topePila, int nuevoPos,
                                         Stack<Character> pilaOriginal) {

        TransicionKey keyNormal = new TransicionKey(estadoActual, entrada, topePila);
        if (aplicarTransicion(keyNormal, nuevoPos, pilaOriginal)) return true;

        if (topePila != null) {
            TransicionKey keyEpsilonPila = new TransicionKey(estadoActual, entrada, null);
            if (aplicarTransicion(keyEpsilonPila, nuevoPos, pilaOriginal)) return true;
        }
        return false;
    }

    /**
     * Aplica una transición específica y continúa la recursión.
     */
    private boolean aplicarTransicion(TransicionKey key, int nuevoPos,
                                      Stack<Character> pilaOriginal) {

        if (!transiciones.containsKey(key)) return false;

        for (TransicionValor val : transiciones.get(key)) {

            // Clonar pila
            Stack<Character> nuevaPila = new Stack<>();
            nuevaPila.addAll(pilaOriginal);

            // Pop si es necesario
            if (key.simboloPila != null) {
                if (nuevaPila.isEmpty()) continue;
                char sacado = nuevaPila.pop();
                if (sacado != key.simboloPila) continue;
            }

            // Push si es necesario
            if (!val.cadenaApilar.isEmpty()) {
                for (int i = val.cadenaApilar.length() - 1; i >= 0; i--) {
                    nuevaPila.push(val.cadenaApilar.charAt(i));
                }
            }

            String desc = key + " → " + val;
            if (procesarRecursivo(val.estadoDestino, nuevoPos, nuevaPila, desc))
                return true;
        }
        return false;
    }


    /* ------- MOTIVOS DE ACEPTAR / RECHAZAR -------- */

    public String getMotivoAceptacion(String estadoActual, int pos, Stack<Character> pila) {
        if (pos == cadenaOriginal.length()) {
            boolean fin = estadosFinales.contains(estadoActual);
            boolean vac = pila.isEmpty();
            if (fin && (!aceptarPorPilaVacia || vac)) return "Aceptada por estado final";
            if (aceptarPorPilaVacia && vac)            return "Aceptada por pila vacía";
        }
        return "Aceptada";
    }

    public String getMotivoRechazo(String estadoActual, int pos, Stack<Character> pila) {
        if (pos != cadenaOriginal.length()) return "No se consumió toda la entrada";
        if (!estadosFinales.contains(estadoActual) && !(aceptarPorPilaVacia && pila.isEmpty()))
            return "No se llegó a estado final ni pila vacía";
        if (estadosFinales.contains(estadoActual) && aceptarPorPilaVacia && !pila.isEmpty())
            return "Estado final pero pila NO vacía";
        return "Rechazada";
    }


    // Getters

    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabetoEntrada() { return alfabetoEntrada; }
    public Set<Character> getAlfabetoPila() { return alfabetoPila; }
    public Map<TransicionKey, List<TransicionValor>> getTransiciones() { return transiciones; }
    public String getEstadoInicial() { return estadoInicial; }
    public char getSimboloInicialPila() { return simboloInicialPila; }
    public Set<String> getEstadosFinales() { return estadosFinales; }
    public boolean isAceptarPorPilaVacia() { return aceptarPorPilaVacia; }
    public List<ConfiguracionAP> getHistorial() { return historial; }
    public List<String> getTransicionesAplicadas() { return transicionesAplicadas; }
}
