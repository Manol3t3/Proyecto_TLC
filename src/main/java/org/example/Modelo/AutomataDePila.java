package org.example.Modelo;

import java.util.*;

/**
 * Clase que representa un Autómata de Pila (AP)
 */
public class AutomataDePila {

    private Set<String> estados;
    private Set<Character> alfabetoEntrada;
    private Set<Character> alfabetoPila;
    private Map<TransicionKey, List<TransicionValor>> transiciones;
    private String estadoInicial;
    private char simboloInicialPila;
    private Set<String> estadosFinales;
    private boolean aceptarPorPilaVacia;

    // Para el historial de ejecución
    private List<ConfiguracionAP> historial;

    public AutomataDePila() {
        this.estados = new HashSet<>();
        this.alfabetoEntrada = new HashSet<>();
        this.alfabetoPila = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.estadosFinales = new HashSet<>();
        this.historial = new ArrayList<>();
        this.aceptarPorPilaVacia = true; // Por defecto acepta por pila vacía
    }

    /**
     * Clase para la clave de transición (estado, símbolo entrada, símbolo pila)
     * simboloPila = null representa ε (no sacar nada de la pila)
     * simboloEntrada = null representa ε (transición sin consumir entrada)
     */
    public static class TransicionKey {
        public String estado;
        public Character simboloEntrada; // null representa ε
        public Character simboloPila;    // null representa ε

        public TransicionKey(String estado, Character simboloEntrada, Character simboloPila) {
            this.estado = estado;
            this.simboloEntrada = simboloEntrada;
            this.simboloPila = simboloPila;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TransicionKey)) return false;
            TransicionKey that = (TransicionKey) o;
            return Objects.equals(estado, that.estado) &&
                    Objects.equals(simboloEntrada, that.simboloEntrada) &&
                    Objects.equals(simboloPila, that.simboloPila);
        }

        @Override
        public int hashCode() {
            return Objects.hash(estado, simboloEntrada, simboloPila);
        }

        @Override
        public String toString() {
            String entrada = simboloEntrada == null ? "ε" : simboloEntrada.toString();
            String pila = simboloPila == null ? "ε" : simboloPila.toString();
            return String.format("(%s, %s, %s)", estado, entrada, pila);
        }
    }

    /**
     * Clase para el valor de transición (estado destino, cadena a apilar)
     * cadenaApilar vacía representa ε (no apilar nada)
     */
    public static class TransicionValor {
        public String estadoDestino;
        public String cadenaApilar; // puede ser vacía para no apilar nada

        public TransicionValor(String estadoDestino, String cadenaApilar) {
            this.estadoDestino = estadoDestino;
            this.cadenaApilar = cadenaApilar;
        }

        @Override
        public String toString() {
            return String.format("(%s, %s)", estadoDestino,
                    cadenaApilar.isEmpty() ? "ε" : cadenaApilar);
        }
    }

    /**
     * Clase para representar una configuración del AP
     */
    public static class ConfiguracionAP {
        public String estado;
        public String cadenaRestante;
        public Stack<Character> pila;

        public ConfiguracionAP(String estado, String cadenaRestante, Stack<Character> pila) {
            this.estado = estado;
            this.cadenaRestante = cadenaRestante;
            this.pila = new Stack<>();
            this.pila.addAll(pila);
        }

        public String getPilaString() {
            if (pila.isEmpty()) return "ε";
            StringBuilder sb = new StringBuilder();
            // Mostrar desde el tope hacia abajo (tope a la izquierda)
            for (int i = pila.size() - 1; i >= 0; i--) {
                sb.append(pila.get(i));
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            String cadena = cadenaRestante.isEmpty() ? "ε" : cadenaRestante;
            return String.format("(%s, %s, %s)", estado, cadena, getPilaString());
        }
    }

    // Métodos de configuración
    public void agregarEstado(String estado) {
        estados.add(estado);
    }

    public void agregarSimboloEntrada(char simbolo) {
        alfabetoEntrada.add(simbolo);
    }

    public void agregarSimboloPila(char simbolo) {
        alfabetoPila.add(simbolo);
    }

    public void setEstadoInicial(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe: " + estado);
        }
        this.estadoInicial = estado;
    }

    public void setSimboloInicialPila(char simbolo) {
        this.simboloInicialPila = simbolo;
        alfabetoPila.add(simbolo);
    }

    public void agregarEstadoFinal(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe: " + estado);
        }
        estadosFinales.add(estado);
    }

    public void setAceptarPorPilaVacia(boolean aceptarPorPilaVacia) {
        this.aceptarPorPilaVacia = aceptarPorPilaVacia;
    }

    /**
     * Agrega una transición
     * @param estadoOrigen Estado actual
     * @param simboloEntrada Símbolo a leer (null para ε)
     * @param simboloPila Símbolo en el tope de la pila (null para ε)
     * @param estadoDestino Estado siguiente
     * @param cadenaApilar Cadena a apilar (vacía para no apilar)
     */
    public void agregarTransicion(String estadoOrigen, Character simboloEntrada,
                                  Character simboloPila, String estadoDestino, String cadenaApilar) {
        if (!estados.contains(estadoOrigen) || !estados.contains(estadoDestino)) {
            throw new IllegalArgumentException("Estados no válidos");
        }

        TransicionKey key = new TransicionKey(estadoOrigen, simboloEntrada, simboloPila);
        transiciones.putIfAbsent(key, new ArrayList<>());
        transiciones.get(key).add(new TransicionValor(estadoDestino, cadenaApilar));
    }

    /**
     * Procesa una cadena y determina si es aceptada según el documento
     */
    public boolean procesar(String cadena) {
        historial.clear();

        Stack<Character> pila = new Stack<>();
        pila.push(simboloInicialPila);

        return procesarRecursivo(estadoInicial, cadena, 0, pila);
    }

    private boolean procesarRecursivo(String estadoActual, String cadena,
                                      int pos, Stack<Character> pila) {
        // Guardar configuración actual
        String restante = pos < cadena.length() ? cadena.substring(pos) : "";
        historial.add(new ConfiguracionAP(estadoActual, restante, pila));

        // CONDICIÓN DE ACEPTACIÓN según el documento:
        // 1. Se ha consumido toda la cadena
        // 2. Y (está en estado final O la pila está vacía)
        if (pos == cadena.length()) {
            if (estadosFinales.contains(estadoActual)) {
                return true; // Acepta por estado final
            }
            if (aceptarPorPilaVacia && pila.isEmpty()) {
                return true; // Acepta por pila vacía
            }
        }

        // Obtener el tope de la pila (null si está vacía)
        Character topePila = pila.isEmpty() ? null : pila.peek();

        // Probar transiciones que consumen un símbolo de entrada
        if (pos < cadena.length()) {
            char simboloActual = cadena.charAt(pos);
            if (intentarTransiciones(estadoActual, simboloActual, topePila, cadena, pos + 1, pila)) {
                return true;
            }
        }

        // Probar transiciones epsilon (sin consumir entrada)
        if (intentarTransiciones(estadoActual, null, topePila, cadena, pos, pila)) {
            return true;
        }

        return false;
    }

    private boolean intentarTransiciones(String estadoActual, Character simboloEntrada,
                                         Character topePila, String cadena, int nuevoPos,
                                         Stack<Character> pilaOriginal) {
        // Probar transición normal (con el tope actual)
        TransicionKey keyNormal = new TransicionKey(estadoActual, simboloEntrada, topePila);
        if (aplicarTransicion(keyNormal, cadena, nuevoPos, pilaOriginal)) {
            return true;
        }

        // También probar transiciones con ε en la pila (cuando la pila no está vacía)
        if (topePila != null) {
            TransicionKey keyEpsilonPila = new TransicionKey(estadoActual, simboloEntrada, null);
            if (aplicarTransicion(keyEpsilonPila, cadena, nuevoPos, pilaOriginal)) {
                return true;
            }
        }

        return false;
    }

    private boolean aplicarTransicion(TransicionKey key, String cadena, int nuevoPos,
                                      Stack<Character> pilaOriginal) {
        if (!transiciones.containsKey(key)) {
            return false;
        }

        // Probar todas las transiciones posibles para esta clave
        for (TransicionValor trans : transiciones.get(key)) {
            // Crear una copia de la pila para esta rama de ejecución
            Stack<Character> nuevaPila = new Stack<>();
            nuevaPila.addAll(pilaOriginal);

            // Manejo de la pila según la transición
            if (key.simboloPila != null) {
                // Si la transición especifica un símbolo de pila, debemos sacarlo
                if (nuevaPila.isEmpty()) {
                    continue; // No se puede sacar de una pila vacía
                }
                char sacado = nuevaPila.pop();
                if (sacado != key.simboloPila) {
                    continue; // El tope no coincide con el esperado
                }
            }
            // Si key.simboloPila es null (ε), no sacamos nada

            // Apilar la nueva cadena (si no es vacía)
            if (!trans.cadenaApilar.isEmpty()) {
                // Apilar en orden inverso (el último carácter queda en el tope)
                for (int i = trans.cadenaApilar.length() - 1; i >= 0; i--) {
                    nuevaPila.push(trans.cadenaApilar.charAt(i));
                }
            }

            // Llamada recursiva
            if (procesarRecursivo(trans.estadoDestino, cadena, nuevoPos, nuevaPila)) {
                return true;
            }
        }

        return false;
    }

    // Getters
    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabetoEntrada() { return alfabetoEntrada; }
    public Set<Character> getAlfabetoPila() { return alfabetoPila; }
    public Map<TransicionKey, List<TransicionValor>> getTransiciones() { return transiciones; }
    public String getEstadoInicial() { return estadoInicial; }
    public char getSimboloInicialPila() { return simboloInicialPila; }
    public Set<String> getEstadosFinales() { return estadosFinales; }
    public List<ConfiguracionAP> getHistorial() { return historial; }
    public boolean isAceptarPorPilaVacia() { return aceptarPorPilaVacia; }
}