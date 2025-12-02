package org.example.Modelo;

import java.util.*;

/**
 * Autómata de Pila genérico.
 * Acepta ÚNICAMENTE si:
 *   - se consumió TODA la entrada
 *   - se está en un estado final
 *   - la pila está VACÍA
 * Si el usuario quiere solo “estado final” o solo “pila vacía”, puede
 * desactivar el otro criterio con setAceptarPorPilaVacia(boolean).
 */
public class AutomataDePila {

    /* ================= ATRIBUTOS ================= */
    private final Set<String> estados = new HashSet<>();
    private final Set<Character> alfabetoEntrada = new HashSet<>();
    private final Set<Character> alfabetoPila = new HashSet<>();
    private final Map<TransicionKey, List<TransicionValor>> transiciones = new HashMap<>();
    private final Set<String> estadosFinales = new HashSet<>();
    private String estadoInicial;
    private char simboloInicialPila = '\0';   // '\0'  ->  pila vacía inicial
    private boolean aceptarPorPilaVacia = true; // se puede desactivar

    /* Historial para depurar / mostrar */
    private final List<ConfiguracionAP> historial = new ArrayList<>();
    private final List<String> transicionesAplicadas = new ArrayList<>();
    private String cadenaOriginal;           // cadena que se está procesando

    /* ================= CLASES INTERNAS ================= */
    public static class TransicionKey {
        public final String estado;
        public final Character simboloEntrada; // null == ε
        public final Character simboloPila;    // null == ε

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

    public static class TransicionValor {
        public final String estadoDestino;
        public final String cadenaApilar;   // "" == ε

        public TransicionValor(String ed, String ap) {
            this.estadoDestino = ed;
            this.cadenaApilar  = ap;
        }
        @Override public String toString() {
            return "(" + estadoDestino + ", " + (cadenaApilar.isEmpty() ? "ε" : cadenaApilar) + ")";
        }
    }

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
        public String getPilaString() {
            if (pila.isEmpty()) return "ε";
            StringBuilder sb = new StringBuilder();
            for (int i = pila.size() - 1; i >= 0; i--) sb.append(pila.get(i));
            return sb.toString();
        }
        @Override public String toString() {
            String cr = cadenaRestante.isEmpty() ? "ε" : cadenaRestante;
            return "(" + estado + ", " + cr + ", " + getPilaString() + ")";
        }
    }

    /* ================= MÉTODOS BÁSICOS ================= */
    public void agregarEstado(String e)           { estados.add(e); }
    public void agregarSimboloEntrada(char c)     { alfabetoEntrada.add(c); }
    public void agregarSimboloPila(char c)        { alfabetoPila.add(c); }
    public void setEstadoInicial(String e) {
        if (!estados.contains(e)) throw new IllegalArgumentException("Estado no existe: " + e);
        estadoInicial = e;
    }
    public void setSimboloInicialPila(char c)     { simboloInicialPila = c; }
    public void agregarEstadoFinal(String e) {
        if (!estados.contains(e)) throw new IllegalArgumentException("Estado no existe: " + e);
        estadosFinales.add(e);
    }
    public void setAceptarPorPilaVacia(boolean b) { aceptarPorPilaVacia = b; }

    public void agregarTransicion(String origen, Character entrada, Character pila,
                                  String destino, String apilar) {
        if (!estados.contains(origen) || !estados.contains(destino))
            throw new IllegalArgumentException("Estados inválidos en transición");
        if (entrada != null && !alfabetoEntrada.contains(entrada))
            throw new IllegalArgumentException("Símbolo entrada no válido: " + entrada);
        if (pila != null && !alfabetoPila.contains(pila))
            throw new IllegalArgumentException("Símbolo pila no válido: " + pila);

        TransicionKey key = new TransicionKey(origen, entrada, pila);
        transiciones.putIfAbsent(key, new ArrayList<>());
        transiciones.get(key).add(new TransicionValor(destino, apilar));
    }

    /* ================= PROCESAR ================= */
    public boolean procesar(String cadena) {
        historial.clear();
        transicionesAplicadas.clear();
        cadenaOriginal = cadena;
        Stack<Character> pila = new Stack<>();   // ← siempre vacía
        return procesarRecursivo(estadoInicial, 0, pila, "");
    }

    /**
     * Back-tracking estándar.
     * @param estadoActual estado actual
     * @param pos          índice dentro de cadenaOriginal
     * @param pila         copia de la pila (se clona en cada rama)
     * @param transPrev    texto descriptivo de la transición que llegó aquí
     */
    private boolean procesarRecursivo(String estadoActual, int pos,
                                      Stack<Character> pila, String transPrev) {

        /* 1.  guardar configuración para mostrar */
        String restante = (pos < cadenaOriginal.length())
                ? cadenaOriginal.substring(pos) : "";
        historial.add(new ConfiguracionAP(estadoActual, restante, pila));
        transicionesAplicadas.add(transPrev);

        /* 2.  condición de aceptación (solo al final de la entrada) */
        if (pos == cadenaOriginal.length()) {
            boolean enEstadoFinal = estadosFinales.contains(estadoActual);
            boolean pilaVacia     = pila.isEmpty();
            if (enEstadoFinal && (!aceptarPorPilaVacia || pilaVacia)) return true;
            if (aceptarPorPilaVacia && pilaVacia) return true;
            // si no se cumple ninguna → seguimos intentando ε-transiciones
        }

        /* 3.  transiciones que consumen un símbolo */
        if (pos < cadenaOriginal.length()) {
            char sim = cadenaOriginal.charAt(pos);
            if (intentarTransiciones(estadoActual, sim, topePila(pila), pos + 1, pila))
                return true;
        }

        /* 4.  ε-transiciones (sin consumir) */
        if (intentarTransiciones(estadoActual, null, topePila(pila), pos, pila))
            return true;

        return false;   // back-tracking
    }

    /* ---------- auxiliares ---------- */
    private Character topePila(Stack<Character> p) {
        return p.isEmpty() ? null : p.peek();
    }

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

    private boolean aplicarTransicion(TransicionKey key, int nuevoPos,
                                      Stack<Character> pilaOriginal) {
        if (!transiciones.containsKey(key)) return false;

        for (TransicionValor val : transiciones.get(key)) {
            Stack<Character> nuevaPila = new Stack<>();
            nuevaPila.addAll(pilaOriginal);

            /* pop */
            if (key.simboloPila != null) {
                if (nuevaPila.isEmpty()) continue;
                char sacado = nuevaPila.pop();
                if (sacado != key.simboloPila) continue;
            }
            /* push */
            if (!val.cadenaApilar.isEmpty()) {
                for (int i = val.cadenaApilar.length() - 1; i >= 0; i--) {
                    nuevaPila.push(val.cadenaApilar.charAt(i));
                }
            }

            String transDescr = key + " → " + val;
            if (procesarRecursivo(val.estadoDestino, nuevoPos, nuevaPila, transDescr))
                return true;
        }
        return false;
    }

    /* ---------- mensajes claros ---------- */
    public String getMotivoAceptacion(String estadoActual, int pos, Stack<Character> pila) {
        if (pos == cadenaOriginal.length()) {
            boolean fin = estadosFinales.contains(estadoActual);
            boolean vac = pila.isEmpty();
            if (fin && (!aceptarPorPilaVacia || vac)) return "Aceptada por estado final";
            if (aceptarPorPilaVacia && vac) return "Aceptada por pila vacía";
        }
        return "Aceptada";
    }

    public String getMotivoRechazo(String estadoActual, int pos, Stack<Character> pila) {
        if (pos != cadenaOriginal.length())
            return "No se consumió toda la entrada";
        if (!estadosFinales.contains(estadoActual) && !(aceptarPorPilaVacia && pila.isEmpty()))
            return "No se llegó a estado final ni pila vacía";
        if (estadosFinales.contains(estadoActual) && aceptarPorPilaVacia && !pila.isEmpty())
            return "Estado final pero pila NO vacía";
        return "Rechazada";
    }

    /* ---------- getters ---------- */
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