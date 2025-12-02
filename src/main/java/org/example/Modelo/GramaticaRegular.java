package org.example.Modelo;

import java.util.*;

/**
 * Representa una Gramática Regular lineal por la derecha.
 * Formato general de producciones: A → aB | b | ε.
 */
public class GramaticaRegular {

    /** Conjunto de símbolos no terminales. */
    private Set<String> noTerminales;

    /** Conjunto de símbolos terminales. */
    private Set<Character> terminales;

    /** Símbolo inicial de la gramática. */
    private String simboloInicial;

    /** Tabla de producciones: NoTerminal → Lista de producciones. */
    private Map<String, List<Produccion>> producciones;

    public GramaticaRegular() {
        this.noTerminales = new HashSet<>();
        this.terminales = new HashSet<>();
        this.producciones = new HashMap<>();
    }

    /**
     * Representa una producción de la forma:
     *  - terminal + noTerminal
     *  - terminal
     *  - ε
     */
    public static class Produccion {
        private Character terminal;
        private String noTerminal;

        public Produccion(Character terminal, String noTerminal) {
            this.terminal = terminal;
            this.noTerminal = noTerminal;
        }

        public Character getTerminal() { return terminal; }
        public String getNoTerminal() { return noTerminal; }

        /** Indica si la producción es ε. */
        public boolean esEpsilon() {
            return terminal == null && noTerminal == null;
        }

        @Override
        public String toString() {
            if (esEpsilon()) return "ε";
            if (noTerminal == null) return terminal.toString();
            return terminal + noTerminal;
        }
    }

    /**
     * Define el símbolo inicial de la gramática.
     * @param simbolo no terminal inicial
     */
    public void setSimboloInicial(String simbolo) {
        this.simboloInicial = simbolo;
        this.noTerminales.add(simbolo);
    }

    /**
     * Agrega un no terminal al conjunto.
     */
    public void agregarNoTerminal(String nt) {
        noTerminales.add(nt);
        producciones.putIfAbsent(nt, new ArrayList<>());
    }

    /**
     * Agrega un símbolo terminal al alfabeto.
     */
    public void agregarTerminal(char t) {
        terminales.add(t);
    }

    /**
     * Agrega una producción del tipo A → aB o A → a.
     * @param noTerminal no terminal origen
     * @param terminal símbolo terminal
     * @param siguienteNT siguiente no terminal (o null si es final)
     */
    public void agregarProduccion(String noTerminal, Character terminal, String siguienteNT) {
        if (!noTerminales.contains(noTerminal)) {
            throw new IllegalArgumentException("No terminal " + noTerminal + " no existe");
        }
        producciones.get(noTerminal).add(new Produccion(terminal, siguienteNT));
    }

    /**
     * Agrega una producción ε al no terminal dado.
     */
    public void agregarProduccionEpsilon(String noTerminal) {
        if (!noTerminales.contains(noTerminal)) {
            throw new IllegalArgumentException("No terminal " + noTerminal + " no existe");
        }
        producciones.get(noTerminal).add(new Produccion(null, null));
    }

    /**
     * Determina si una cadena pertenece al lenguaje generado por la gramática.
     */
    public boolean pertenece(String cadena) {
        return perteneceRec(simboloInicial, cadena, 0);
    }

    /**
     * Función recursiva para verificar pertenencia.
     */
    private boolean perteneceRec(String noTerminal, String cadena, int pos) {
        List<Produccion> prods = producciones.get(noTerminal);
        if (prods == null) return false;

        for (Produccion prod : prods) {

            // Caso ε
            if (prod.esEpsilon()) {
                if (pos == cadena.length()) return true;
                continue;
            }

            // Comparación con terminal
            if (pos < cadena.length() &&
                    prod.getTerminal() != null &&
                    cadena.charAt(pos) == prod.getTerminal()) {

                // Producción terminal sin no terminal
                if (prod.getNoTerminal() == null) {
                    if (pos == cadena.length() - 1) return true;
                }
                // Producción terminal seguida de NT
                else {
                    if (perteneceRec(prod.getNoTerminal(), cadena, pos + 1))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Genera una posible derivación paso a paso de la cadena.
     * @return lista de derivaciones o mensaje de error
     */
    public List<String> generarDerivaciones(String cadena) {
        List<String> derivaciones = new ArrayList<>();
        derivaciones.add(simboloInicial);

        if (generarDerivacionesRec(simboloInicial, cadena, 0, derivaciones)) {
            return derivaciones;
        }

        return Arrays.asList("No se puede derivar la cadena");
    }

    /**
     * Función recursiva para construir la derivación.
     */
    private boolean generarDerivacionesRec(String noTerminal, String cadena, int pos, List<String> derivaciones) {
        List<Produccion> prods = producciones.get(noTerminal);
        if (prods == null) return false;

        for (Produccion prod : prods) {

            // Producción ε
            if (prod.esEpsilon()) {
                if (pos == cadena.length()) {
                    derivaciones.add("ε");
                    return true;
                }
                continue;
            }

            // Coincidencia de terminal
            if (pos < cadena.length() &&
                    prod.getTerminal() != null &&
                    cadena.charAt(pos) == prod.getTerminal()) {

                String derivacion = cadena.substring(0, pos + 1);

                if (prod.getNoTerminal() != null)
                    derivacion += prod.getNoTerminal();

                derivaciones.add(derivacion);

                // Terminal sin NT
                if (prod.getNoTerminal() == null) {
                    if (pos == cadena.length() - 1) return true;
                }
                // Terminal + NT
                else {
                    if (generarDerivacionesRec(prod.getNoTerminal(), cadena, pos + 1, derivaciones))
                        return true;
                }

                derivaciones.remove(derivaciones.size() - 1);
            }
        }

        return false;
    }

    /**
     * Convierte la gramática en un Autómata Finito Determinista equivalente.
     */
    public AFD convertirAFD() {
        AFD afd = new AFD();

        // Agregar estados (no terminales)
        for (String nt : noTerminales) {
            afd.agregarEstado(nt);
        }

        // Estado final especial
        String estadoFinal = "qf";
        afd.agregarEstado(estadoFinal);
        afd.agregarEstadoFinal(estadoFinal);

        // Agregar alfabeto
        for (char t : terminales) {
            afd.agregarSimbolo(t);
        }

        afd.setEstadoInicial(simboloInicial);

        // Convertir producciones
        for (String nt : producciones.keySet()) {
            for (Produccion prod : producciones.get(nt)) {
                if (prod.esEpsilon()) {
                    afd.agregarEstadoFinal(nt);
                } else if (prod.getNoTerminal() == null) {
                    afd.agregarTransicion(nt, prod.getTerminal(), estadoFinal);
                } else {
                    afd.agregarTransicion(nt, prod.getTerminal(), prod.getNoTerminal());
                }
            }
        }

        return afd;
    }

    //  Getters


    public Set<String> getNoTerminales() { return noTerminales; }
    public Set<Character> getTerminales() { return terminales; }
    public String getSimboloInicial() { return simboloInicial; }
    public Map<String, List<Produccion>> getProducciones() { return producciones; }

    /**
     * Devuelve las producciones en formato legible.
     */
    public String getProduccionesTexto() {
        StringBuilder sb = new StringBuilder();
        for (String nt : producciones.keySet()) {
            sb.append(nt).append(" → ");
            List<Produccion> prods = producciones.get(nt);
            for (int i = 0; i < prods.size(); i++) {
                sb.append(prods.get(i));
                if (i < prods.size() - 1) sb.append(" | ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
