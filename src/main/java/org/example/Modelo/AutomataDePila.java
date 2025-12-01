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
        Character simboloEntrada; // null representa epsilon de entrada
        Character simboloPila;    // null representa epsilon de pila

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

    // Clase interna para el dibujo en APPanel, usando los campos necesarios
    public static class Transicion {
        public String estadoOrigen;
        public Character simboloEntrada;
        public Character simboloPila;
        public String estadoDestino;
        public String cadenaApilar;

        public Transicion(String origen, Character sEntrada, Character sPila, String destino, String apilar) {
            this.estadoOrigen = origen;
            this.simboloEntrada = sEntrada;
            this.simboloPila = sPila;
            this.estadoDestino = destino;
            this.cadenaApilar = apilar;
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
            // Se asume que el estado ya fue agregado o se hace una excepción menos estricta
        }
        this.estadoInicial = estado;
    }

    public void setSimboloInicialPila(char simbolo) {
        this.simboloInicialPila = simbolo;
        alfabetoPila.add(simbolo);
    }

    public void agregarEstadoFinal(String estado) {
        if (!estados.contains(estado)) {
            // Se asume que el estado ya fue agregado o se hace una excepción menos estricta
        }
        estadosFinales.add(estado);
    }

    /**
     * Agrega una transición
     * @param estadoOrigen Estado actual
     * @param simboloEntrada Símbolo a leer (null para epsilon de entrada)
     * @param simboloPila Símbolo en el tope de la pila (null para epsilon de pila)
     * @param estadoDestino Estado siguiente
     * @param cadenaApilar Cadena a apilar (vacía para no apilar)
     */
    public void agregarTransicion(String estadoOrigen, Character simboloEntrada,
                                  Character simboloPila, String estadoDestino, String cadenaApilar) {
        if (!estados.contains(estadoOrigen) || !estados.contains(estadoDestino)) {
            // Esto solo debería ser un aviso, ya que la UI puede agregarlos después
            // throw new IllegalArgumentException("Estados no válidos");
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

        // Si la pila está vacía, el tope es null. Si no, es el peek.
        Character topePila = pila.isEmpty() ? null : pila.peek();

        // --- BÚSQUEDA DE TRANSICIONES ---

        // 1. Transiciones que consumen SÍMBOLO DE ENTRADA (a, X) y (a, ε)
        if (pos < cadena.length()) {
            char simbolo = cadena.charAt(pos);
            int nuevaPos = pos + 1;

            // a) (q, a, X): consume entrada y pila
            if (topePila != null && buscarTransiciones(estado, simbolo, topePila, cadena, nuevaPos, pila)) {
                return true;
            }

            // b) (q, a, ε): consume entrada, no consume pila
            if (buscarTransiciones(estado, simbolo, null, cadena, nuevaPos, pila)) {
                return true;
            }
        }

        // 2. Transiciones EPSILON (ε, X) y (ε, ε)
        int nuevaPosEpsilon = pos; // No consume entrada

        // c) (q, ε, X): no consume entrada, consume pila
        if (topePila != null && buscarTransiciones(estado, null, topePila, cadena, nuevaPosEpsilon, pila)) {
            return true;
        }

        // d) (q, ε, ε): no consume entrada, no consume pila
        if (buscarTransiciones(estado, null, null, cadena, nuevaPosEpsilon, pila)) {
            return true;
        }


        return false;
    }

    /**
     * Busca y ejecuta transiciones para una clave dada.
     * @param estadoActual Estado de la configuración actual.
     * @param simboloEntrada Símbolo de entrada a buscar (null para epsilon).
     * @param simboloPila Símbolo de pila a buscar (null para epsilon).
     * @param cadena Cadena de entrada.
     * @param nuevaPos Posición en la cadena después de la transición.
     * @param pilaActual Pila de la configuración actual.
     * @return true si alguna rama recursiva lleva a la aceptación.
     */
    private boolean buscarTransiciones(String estadoActual, Character simboloEntrada, Character simboloPila,
                                       String cadena, int nuevaPos, Stack<Character> pilaActual) {

        TransicionKey key = new TransicionKey(estadoActual, simboloEntrada, simboloPila);

        if (transiciones.containsKey(key)) {
            for (TransicionValor trans : transiciones.get(key)) {

                // Evitar ciclos infinitos de transiciones (q, ε, ε) si no hay cambio de estado o pila
                if (simboloEntrada == null && simboloPila == null &&
                        trans.estadoDestino.equals(estadoActual) && trans.cadenaApilar.isEmpty()) {
                    // Si es (q, ε, ε) a sí mismo y no hace nada a la pila, es un ciclo infinito
                    continue;
                }

                Stack<Character> nuevaPila = (Stack<Character>) pilaActual.clone();

                // Si la transición consume un símbolo de pila (simboloPila ≠ null), lo saca
                if (simboloPila != null) {
                    // Se verifica que la pila no esté vacía antes de hacer pop (aunque topePila != null ya lo implica)
                    if (!nuevaPila.isEmpty()) {
                        nuevaPila.pop();
                    } else {
                        // Esto no debería suceder si la lógica principal es correcta, pero es un buen guardrail.
                        continue;
                    }
                }

                // Apilar la cadena nueva (en orden inverso)
                for (int i = trans.cadenaApilar.length() - 1; i >= 0; i--) {
                    nuevaPila.push(trans.cadenaApilar.charAt(i));
                }

                // Llamada recursiva
                if (procesarRec(trans.estadoDestino, cadena, nuevaPos, nuevaPila)) {
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

    // Obtener la lista de transiciones plana para el dibujo
    public List<Transicion> getTransiciones() {
        List<Transicion> list = new ArrayList<>();
        for (Map.Entry<TransicionKey, List<TransicionValor>> entry : transiciones.entrySet()) {
            TransicionKey key = entry.getKey();
            for (TransicionValor val : entry.getValue()) {
                list.add(new Transicion(
                        key.estado,
                        key.simboloEntrada,
                        key.simboloPila,
                        val.estadoDestino,
                        val.cadenaApilar
                ));
            }
        }
        return list;
    }

    public String getEstadoInicial() { return estadoInicial; }
    public char getSimboloInicialPila() { return simboloInicialPila; }
    public Set<String> getEstadosFinales() { return estadosFinales; }
    public List<ConfiguracionAP> getHistorial() { return historial; }
}