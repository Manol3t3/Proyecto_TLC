package org.example.Modelo;

import java.util.*;

/**
 * Clase que representa una Gramática Libre de Contexto (GLC)
 */
public class GLC {

    private Set<String> noTerminales;
    private Set<Character> terminales;
    private String simboloInicial;
    private Map<String, List<String>> producciones;

    public GLC() {
        this.noTerminales = new HashSet<>();
        this.terminales = new HashSet<>();
        this.producciones = new HashMap<>();
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

    public void agregarProduccion(String noTerminal, String produccion) {
        if (!noTerminales.contains(noTerminal)) {
            throw new IllegalArgumentException("No terminal no existe: " + noTerminal);
        }
        producciones.get(noTerminal).add(produccion);
    }

    /**
     * Clase interna para representar un nodo del árbol sintáctico
     */
    public static class NodoArbol {
        private String simbolo;
        private List<NodoArbol> hijos;

        public NodoArbol(String simbolo) {
            this.simbolo = simbolo;
            this.hijos = new ArrayList<>();
        }

        public void agregarHijo(NodoArbol hijo) {
            hijos.add(hijo);
        }

        public String getSimbolo() { return simbolo; }
        public List<NodoArbol> getHijos() { return hijos; }

        @Override
        public String toString() {
            return simbolo;
        }
    }

    public List<String> derivarIzquierda(String objetivo) {
        List<String> derivaciones = new ArrayList<>();
        derivaciones.add(simboloInicial);

        if (derivarIzquierdaRec(simboloInicial, objetivo, derivaciones, 0)) {
            return derivaciones;
        }

        return Arrays.asList("No se pudo derivar la cadena");
    }

    private boolean derivarIzquierdaRec(String actual, String objetivo, List<String> derivaciones, int profundidad) {
        if (profundidad > 100) return false;

        if (actual.equals(objetivo)) {
            return true;
        }

        if (!contieneNoTerminal(actual)) {
            return false;
        }

        int pos = -1;
        String noTerminal = null;
        for (int i = 0; i < actual.length(); i++) {
            String simbolo = String.valueOf(actual.charAt(i));
            if (noTerminales.contains(simbolo)) {
                pos = i;
                noTerminal = simbolo;
                break;
            }
        }

        if (noTerminal == null) return false;

        List<String> prods = producciones.get(noTerminal);
        if (prods == null) return false;

        for (String prod : prods) {
            String nuevaCadena = actual.substring(0, pos) + prod + actual.substring(pos + 1);
            derivaciones.add(nuevaCadena);

            if (derivarIzquierdaRec(nuevaCadena, objetivo, derivaciones, profundidad + 1)) {
                return true;
            }

            derivaciones.remove(derivaciones.size() - 1);
        }

        return false;
    }

    public List<String> derivarDerecha(String objetivo) {
        List<String> derivaciones = new ArrayList<>();
        derivaciones.add(simboloInicial);

        if (derivarDerechaRec(simboloInicial, objetivo, derivaciones, 0)) {
            return derivaciones;
        }

        return Arrays.asList("No se pudo derivar la cadena");
    }

    private boolean derivarDerechaRec(String actual, String objetivo, List<String> derivaciones, int profundidad) {
        if (profundidad > 100) return false;

        if (actual.equals(objetivo)) {
            return true;
        }

        if (!contieneNoTerminal(actual)) {
            return false;
        }

        int pos = -1;
        String noTerminal = null;
        for (int i = actual.length() - 1; i >= 0; i--) {
            String simbolo = String.valueOf(actual.charAt(i));
            if (noTerminales.contains(simbolo)) {
                pos = i;
                noTerminal = simbolo;
                break;
            }
        }

        if (noTerminal == null) return false;

        List<String> prods = producciones.get(noTerminal);
        if (prods == null) return false;

        for (String prod : prods) {
            String nuevaCadena = actual.substring(0, pos) + prod + actual.substring(pos + 1);
            derivaciones.add(nuevaCadena);

            if (derivarDerechaRec(nuevaCadena, objetivo, derivaciones, profundidad + 1)) {
                return true;
            }

            derivaciones.remove(derivaciones.size() - 1);
        }

        return false;
    }

    public NodoArbol generarArbolSintactico(String cadena) {
        NodoArbol raiz = new NodoArbol(simboloInicial);
        if (construirArbolRec(raiz, cadena, 0)) {
            return raiz;
        }
        return null;
    }

    private boolean construirArbolRec(NodoArbol nodo, String objetivo, int profundidad) {
        if (profundidad > 50) return false;

        String simbolo = nodo.getSimbolo();

        if (terminales.contains(simbolo.charAt(0))) {
            return true;
        }

        if (simbolo.equals("ε")) {
            return true;
        }

        if (noTerminales.contains(simbolo)) {
            List<String> prods = producciones.get(simbolo);
            if (prods == null) return false;

            for (String prod : prods) {
                nodo.getHijos().clear();

                for (char c : prod.toCharArray()) {
                    NodoArbol hijo = new NodoArbol(String.valueOf(c));
                    nodo.agregarHijo(hijo);
                }

                String derivado = obtenerCadenaDerivada(nodo);
                if (derivado.replace("ε", "").equals(objetivo)) {
                    return true;
                }

                boolean exito = true;
                for (NodoArbol hijo : nodo.getHijos()) {
                    if (!construirArbolRec(hijo, objetivo, profundidad + 1)) {
                        exito = false;
                        break;
                    }
                }

                if (exito) return true;
            }
        }

        return false;
    }

    private String obtenerCadenaDerivada(NodoArbol nodo) {
        if (nodo.getHijos().isEmpty()) {
            return nodo.getSimbolo();
        }

        StringBuilder sb = new StringBuilder();
        for (NodoArbol hijo : nodo.getHijos()) {
            sb.append(obtenerCadenaDerivada(hijo));
        }
        return sb.toString();
    }

    private boolean contieneNoTerminal(String cadena) {
        for (char c : cadena.toCharArray()) {
            if (noTerminales.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    public String visualizarArbol(NodoArbol raiz) {
        if (raiz == null) return "No se pudo generar el árbol";

        StringBuilder sb = new StringBuilder();
        visualizarArbolRec(raiz, "", true, sb);
        return sb.toString();
    }

    private void visualizarArbolRec(NodoArbol nodo, String prefijo, boolean esUltimo, StringBuilder sb) {
        sb.append(prefijo);
        sb.append(esUltimo ? "└── " : "├── ");
        sb.append(nodo.getSimbolo()).append("\n");

        List<NodoArbol> hijos = nodo.getHijos();
        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimo = (i == hijos.size() - 1);
            visualizarArbolRec(hijos.get(i), prefijo + (esUltimo ? "    " : "│   "), ultimo, sb);
        }
    }

    // Getters
    public Set<String> getNoTerminales() { return noTerminales; }
    public Set<Character> getTerminales() { return terminales; }
    public String getSimboloInicial() { return simboloInicial; }
    public Map<String, List<String>> getProducciones() { return producciones; }

    public String getProduccionesTexto() {
        StringBuilder sb = new StringBuilder();
        for (String nt : producciones.keySet()) {
            sb.append(nt).append(" → ");
            List<String> prods = producciones.get(nt);
            for (int i = 0; i < prods.size(); i++) {
                sb.append(prods.get(i));
                if (i < prods.size() - 1) sb.append(" | ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}