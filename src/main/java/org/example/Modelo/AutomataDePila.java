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

    // Para el historial de ejecución
    private List<ConfiguracionAP> historial;

    public AutomataDePila() {
        this.estados = new HashSet<>();
        this.alfabetoEntrada = new HashSet<>();
        this.alfabetoPila = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.estadosFinales = new HashSet<>();
        this.historial = new ArrayList<>();
    }

    /**
     * Clase para la clave de transición (estado, símbolo entrada, símbolo pila)
     */
    public static class TransicionKey {
        String estado;
        Character simboloEntrada; // null representa epsilon
        Character simboloPila;

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
            return String.format("(%s, %s, %s)", estado, entrada, simboloPila);
        }
    }

    /**
     * Clase para el valor de transición (estado destino, cadena a apilar)
     */
    public static class TransicionValor {
        String estadoDestino;
        String cadenaApilar; // puede ser vacía para no apilar nada

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
        String cadenaRestante;
        public Stack<Character> pila;

        public ConfiguracionAP(String estado, String cadenaRestante, Stack<Character> pila) {
            this.estado = estado;
            this.cadenaRestante = cadenaRestante;
            this.pila = new Stack<>();
            this.pila.addAll(pila);
        }

        public String getPilaString() {
            if (pila.isEmpty()) return "vacía";
            StringBuilder sb = new StringBuilder();
            List<Character> temp = new ArrayList<>(pila);
            Collections.reverse(temp);
            for (char c : temp) {
                sb.append(c);
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return String.format("(%s, \"%s\", [%s])", estado, cadenaRestante, getPilaString());
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
            throw new IllegalArgumentException("Estado no existe");
        }
        this.estadoInicial = estado;
    }

    public void setSimboloInicialPila(char simbolo) {
        this.simboloInicialPila = simbolo;
        alfabetoPila.add(simbolo);
    }

    public void agregarEstadoFinal(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        estadosFinales.add(estado);
    }

    /**
     * Agrega una transición
     * @param estadoOrigen Estado actual
     * @param simboloEntrada Símbolo a leer (null para epsilon)
     * @param simboloPila Símbolo en el tope de la pila
     * @param estadoDestino Estado siguiente
     * @param cadenaApilar Cadena a apilar (vacía para no apilar)
     */
    public void agregarTransicion(String estadoOrigen, Character simboloEntrada,
                                  char simboloPila, String estadoDestino, String cadenaApilar) {
        if (!estados.contains(estadoOrigen) || !estados.contains(estadoDestino)) {
            throw new IllegalArgumentException("Estados no válidos");
        }

        TransicionKey key = new TransicionKey(estadoOrigen, simboloEntrada, simboloPila);
        transiciones.putIfAbsent(key, new ArrayList<>());
        transiciones.get(key).add(new TransicionValor(estadoDestino, cadenaApilar));
    }

    /**
     * Procesa una cadena y determina si es aceptada
     */
    public boolean procesar(String cadena) {
        historial.clear();

        Stack<Character> pila = new Stack<>();
        pila.push(simboloInicialPila);

        return procesarRec(estadoInicial, cadena, 0, pila);
    }

    private boolean procesarRec(String estado, String cadena, int pos, Stack<Character> pila) {
        // Guardar configuración actual
        String restante = pos < cadena.length() ? cadena.substring(pos) : "";
        historial.add(new ConfiguracionAP(estado, restante, pila));

        // Condición de aceptación: cadena consumida y estado final
        if (pos == cadena.length() && estadosFinales.contains(estado)) {
            return true;
        }

        // Si la pila está vacía, no podemos continuar
        if (pila.isEmpty()) {
            return false;
        }

        char topePila = pila.peek();

        // Intentar transición con símbolo de entrada
        if (pos < cadena.length()) {
            char simbolo = cadena.charAt(pos);
            TransicionKey key = new TransicionKey(estado, simbolo, topePila);

            if (transiciones.containsKey(key)) {
                for (TransicionValor trans : transiciones.get(key)) {
                    Stack<Character> nuevaPila = (Stack<Character>) pila.clone();
                    nuevaPila.pop(); // Sacar el símbolo consumido

                    // Apilar la cadena nueva (en orden inverso)
                    for (int i = trans.cadenaApilar.length() - 1; i >= 0; i--) {
                        nuevaPila.push(trans.cadenaApilar.charAt(i));
                    }

                    if (procesarRec(trans.estadoDestino, cadena, pos + 1, nuevaPila)) {
                        return true;
                    }
                }
            }
        }

        // Intentar transición epsilon
        TransicionKey keyEpsilon = new TransicionKey(estado, null, topePila);
        if (transiciones.containsKey(keyEpsilon)) {
            for (TransicionValor trans : transiciones.get(keyEpsilon)) {
                Stack<Character> nuevaPila = (Stack<Character>) pila.clone();
                nuevaPila.pop();

                for (int i = trans.cadenaApilar.length() - 1; i >= 0; i--) {
                    nuevaPila.push(trans.cadenaApilar.charAt(i));
                }

                if (procesarRec(trans.estadoDestino, cadena, pos, nuevaPila)) {
                    return true;
                }
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
}