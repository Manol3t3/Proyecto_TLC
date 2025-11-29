package org.example.Modelo;

import java.util.*;

/**
 * Clase que representa una Gramática Regular
 * Formato: A → aB | b (lineal derecha)
 */
public class GramaticaRegular {

    private Set<String> noTerminales;
    private Set<Character> terminales;
    private String simboloInicial;
    private Map<String, List<Produccion>> producciones;

    public GramaticaRegular() {
        this.noTerminales = new HashSet<>();
        this.terminales = new HashSet<>();
        this.producciones = new HashMap<>();
    }

    /**
     * Clase interna para representar una producción
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

    public void setSimboloInicial(String simbolo) {
        this.simboloInicial = simbolo;
        this.noTerminales.add(simbolo);
    }

    public void agregarNoTerminal(String nt) {
        noTerminales.add(nt);
        producciones.putIfAbsent(nt, new ArrayList<>());
    }

    public void agregarTerminal(char t) {
        terminales.add(t);
    }

    public void agregarProduccion(String noTerminal, Character terminal, String siguienteNT) {
        if (!noTerminales.contains(noTerminal)) {
            throw new IllegalArgumentException("No terminal " + noTerminal + " no existe");
        }

        producciones.get(noTerminal).add(new Produccion(terminal, siguienteNT));
    }

    public void agregarProduccionEpsilon(String noTerminal) {
        if (!noTerminales.contains(noTerminal)) {
            throw new IllegalArgumentException("No terminal " + noTerminal + " no existe");
        }

        producciones.get(noTerminal).add(new Produccion(null, null));
    }

    public boolean pertenece(String cadena) {
        return perteneceRec(simboloInicial, cadena, 0);
    }

    private boolean perteneceRec(String noTerminal, String cadena, int pos) {
        List<Produccion> prods = producciones.get(noTerminal);
        if (prods == null) return false;

        for (Produccion prod : prods) {
            if (prod.esEpsilon()) {
                if (pos == cadena.length()) return true;
                continue;
            }

            if (pos < cadena.length() && prod.getTerminal() != null &&
                    cadena.charAt(pos) == prod.getTerminal()) {

                if (prod.getNoTerminal() == null) {
                    if (pos == cadena.length() - 1) return true;
                } else {
                    if (perteneceRec(prod.getNoTerminal(), cadena, pos + 1)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public List<String> generarDerivaciones(String cadena) {
        List<String> derivaciones = new ArrayList<>();
        derivaciones.add(simboloInicial);

        if (generarDerivacionesRec(simboloInicial, cadena, 0, derivaciones)) {
            return derivaciones;
        }

        return Arrays.asList("No se puede derivar la cadena");
    }

    private boolean generarDerivacionesRec(String noTerminal, String cadena, int pos, List<String> derivaciones) {
        List<Produccion> prods = producciones.get(noTerminal);
        if (prods == null) return false;

        for (Produccion prod : prods) {
            if (prod.esEpsilon()) {
                if (pos == cadena.length()) {
                    derivaciones.add("ε");
                    return true;
                }
                continue;
            }

            if (pos < cadena.length() && prod.getTerminal() != null &&
                    cadena.charAt(pos) == prod.getTerminal()) {

                String derivacion = cadena.substring(0, pos + 1);
                if (prod.getNoTerminal() != null) {
                    derivacion += prod.getNoTerminal();
                }

                derivaciones.add(derivacion);

                if (prod.getNoTerminal() == null) {
                    if (pos == cadena.length() - 1) return true;
                } else {
                    if (generarDerivacionesRec(prod.getNoTerminal(), cadena, pos + 1, derivaciones)) {
                        return true;
                    }
                }

                derivaciones.remove(derivaciones.size() - 1);
            }
        }

        return false;
    }

    public AFD convertirAFD() {
        AFD afd = new AFD();

        for (String nt : noTerminales) {
            afd.agregarEstado(nt);
        }
        String estadoFinal = "qf";
        afd.agregarEstado(estadoFinal);
        afd.agregarEstadoFinal(estadoFinal);

        for (char t : terminales) {
            afd.agregarSimbolo(t);
        }

        afd.setEstadoInicial(simboloInicial);

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

    // Getters
    public Set<String> getNoTerminales() { return noTerminales; }
    public Set<Character> getTerminales() { return terminales; }
    public String getSimboloInicial() { return simboloInicial; }
    public Map<String, List<Produccion>> getProducciones() { return producciones; }

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