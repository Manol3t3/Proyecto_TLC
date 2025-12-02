package org.example.Modelo;

import java.util.*;

/**
 * Representa una Gramática Libre de Contexto (GLC) con soporte para:
 * - Verificación de pertenencia de cadenas mediante derivación izquierda o derecha.
 * - Generación del árbol sintáctico asociado a una derivación.
 * - Visualización estructurada del árbol derivado.
 */
public class GLC {

    private Set<String> noTerminales;       // Conjunto de símbolos no terminales.
    private Set<Character> terminales;      // Conjunto de símbolos terminales.
    private String simboloInicial;          // Símbolo inicial de la gramática.
    private Map<String, List<String>> producciones; // Producciones de cada no terminal.

    public GLC() {
        this.noTerminales = new HashSet<>();
        this.terminales = new HashSet<>();
        this.producciones = new LinkedHashMap<>(); // Mantiene el orden de inserción.
    }

    // Establece el símbolo inicial y lo agrega automáticamente como No Terminal.
    public void setSimboloInicial(String simbolo) {
        this.simboloInicial = simbolo;
        this.noTerminales.add(simbolo);
    }

    // Registra un nuevo No Terminal y crea su lista de producciones.
    public void agregarNoTerminal(String nt) {
        noTerminales.add(nt);
        producciones.putIfAbsent(nt, new ArrayList<>());
    }

    // Agrega un símbolo terminal al alfabeto.
    public void agregarTerminal(char t) {
        terminales.add(t);
    }

    // Añade una producción A → α al No Terminal correspondiente.
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

    // Determina si la cadena puede derivarse desde el símbolo inicial usando derivación izquierda.
    public boolean pertenece(String cadena) {
        if (simboloInicial == null) return false;

        if (cadena.isEmpty() && producciones.getOrDefault(simboloInicial, Collections.emptyList()).contains("")) {
            return true;
        }

        List<String> derivaciones = derivarIzquierda(cadena);
        return !derivaciones.isEmpty() && derivaciones.get(derivaciones.size() - 1).equals(cadena);
    }

    // Devuelve la lista de pasos aplicados en una derivación izquierda.
    public List<String> derivarIzquierda(String objetivo) {
        List<String> derivaciones = new ArrayList<>();
        derivaciones.add(simboloInicial);

        Set<String> visitados = new HashSet<>();

        if (derivarIzquierdaRec(simboloInicial, objetivo, derivaciones, 0, visitados)) {
            return derivaciones;
        }

        return Arrays.asList("No se pudo derivar la cadena");
    }

    // Búsqueda recursiva con poda, límites de profundidad y detección de ciclos.
    private boolean derivarIzquierdaRec(String actual, String objetivo, List<String> derivaciones, int profundidad, Set<String> visitados) {
        // 🚨 Límite de longitud: Si la cadena de derivación se hace mucho más larga que el objetivo, aborta.
        if (actual.length() > objetivo.length() * 2 + 5 && contieneNoTerminal(actual)) {
            return false;
        }

        if (profundidad > 500) return false;

        // Detección de buclos: Rompe ciclos exactos de cadena.
        if (visitados.contains(actual)) {
            return false;
        }

        if (actual.equals(objetivo)) {
            return true;
        }

        if (!contieneNoTerminal(actual)) {
            return false;
        }

        // Busca el NO TERMINAL más a la izquierda
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

        // Marcar el estado actual como visitado
        visitados.add(actual);

        for (String prod : prods) {
            String nuevaCadena = actual.substring(0, pos) + prod + actual.substring(pos + 1);

            // Optimización: Salta si la nueva cadena excede el límite.
            if (nuevaCadena.length() > objetivo.length() * 2 + 5 && contieneNoTerminal(nuevaCadena)) {
                continue;
            }

            derivaciones.add(nuevaCadena);

            if (derivarIzquierdaRec(nuevaCadena, objetivo, derivaciones, profundidad + 1, visitados)) {
                return true;
            }

            // Backtracking
            derivaciones.remove(derivaciones.size() - 1);
        }

        // Desmarcar el estado actual
        visitados.remove(actual);

        return false;
    }

    // Misma estructura que la derivación izquierda, pero expandiendo el No Terminal más a la derecha.
    public List<String> derivarDerecha(String objetivo) {
        List<String> derivaciones = new ArrayList<>();
        derivaciones.add(simboloInicial);

        Set<String> visitados = new HashSet<>();

        if (derivarDerechaRec(simboloInicial, objetivo, derivaciones, 0, visitados)) {
            return derivaciones;
        }

        return Arrays.asList("No se pudo derivar la cadena");
    }

    private boolean derivarDerechaRec(String actual, String objetivo, List<String> derivaciones, int profundidad, Set<String> visitados) {
        //  Límite de longitud
        if (actual.length() > objetivo.length() * 2 + 5 && contieneNoTerminal(actual)) {
            return false;
        }

        if (profundidad > 500) return false;

        // Detección de bucles
        if (visitados.contains(actual)) {
            return false;
        }

        if (actual.equals(objetivo)) {
            return true;
        }

        if (!contieneNoTerminal(actual)) {
            return false;
        }

        // Busca el NO TERMINAL más a la derecha
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

        // Marcar el estado actual como visitado
        visitados.add(actual);

        for (String prod : prods) {
            String nuevaCadena = actual.substring(0, pos) + prod + actual.substring(pos + 1);

            // Optimización: Salta si la nueva cadena excede el límite.
            if (nuevaCadena.length() > objetivo.length() * 2 + 5 && contieneNoTerminal(nuevaCadena)) {
                continue;
            }

            derivaciones.add(nuevaCadena);

            if (derivarDerechaRec(nuevaCadena, objetivo, derivaciones, profundidad + 1, visitados)) {
                return true;
            }

            // Backtracking
            derivaciones.remove(derivaciones.size() - 1);
        }

        // Desmarcar el estado actual
        visitados.remove(actual);

        return false;
    }

    // --- Lógica del Árbol Sintáctico ---

    public NodoArbol generarArbolSintactico(String cadena) {
        if (simboloInicial == null) return null;
        NodoArbol raiz = new NodoArbol(simboloInicial);
        // El árbol solo necesita el objetivo final (la cadena) y la profundidad.
        if (construirArbolRec(raiz, cadena, 0)) {
            return raiz;
        }
        return null;
    }

    private boolean construirArbolRec(NodoArbol nodo, String objetivo, int profundidad) {
        // Límite de profundidad estricto para evitar congelamiento en árbol
        if (profundidad > 100) return false;

        String simbolo = nodo.getSimbolo();

        if (terminales.contains(simbolo.charAt(0))) {
            return objetivo.startsWith(simbolo);
        }

        if (simbolo.equals("ε")) {
            return true;
        }

        if (noTerminales.contains(simbolo)) {
            List<String> prods = producciones.get(simbolo);
            if (prods == null) return false;

            for (String prod : prods) {
                nodo.getHijos().clear();

                if (prod.equals("ε")) {
                    if (objetivo.isEmpty()) {
                        nodo.agregarHijo(new NodoArbol("ε"));
                        return true;
                    }
                    continue;
                }

                List<NodoArbol> nuevosHijos = new ArrayList<>();
                for (char c : prod.toCharArray()) {
                    nuevosHijos.add(new NodoArbol(String.valueOf(c)));
                }

                // Llamada a la función de coincidencia con las correcciones de partición
                if (intentarCoincidencia(nuevosHijos, objetivo, profundidad + 1)) {
                    nodo.getHijos().addAll(nuevosHijos);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean intentarCoincidencia(List<NodoArbol> nodos, String objetivo, int profundidad) {
        if (nodos.isEmpty()) {
            return objetivo.isEmpty();
        }

        NodoArbol cabeza = nodos.get(0);
        List<NodoArbol> cola = nodos.subList(1, nodos.size());

        if (terminales.contains(cabeza.getSimbolo().charAt(0))) {
            if (objetivo.startsWith(cabeza.getSimbolo())) {
                String nuevoObjetivo = objetivo.substring(cabeza.getSimbolo().length());
                return intentarCoincidencia(cola, nuevoObjetivo, profundidad);
            }
            return false;
        } else { // Es No Terminal

            // Corrección de Partición (Split): Limita las pruebas de división
            for (int i = 0; i <= objetivo.length(); i++) {
                String subObjetivo = objetivo.substring(0, i);
                String restoObjetivo = objetivo.substring(i);

                // Evitar ciclos de recursividad vacía: Si la subcadena es vacía (i=0)
                // y el resto de la cadena no es vacía, saltamos para evitar que un NT
                // se expanda a epsilon o a otro NT recursivo sin consumir nada.
                if (i == 0 && !restoObjetivo.isEmpty() && cabeza.getSimbolo().equals(simboloInicial)) {
                    // Esto es una heurística para detener la recursión por la izquierda en el árbol.
                    continue;
                }

                if (construirArbolRec(cabeza, subObjetivo, profundidad)) {
                    if (intentarCoincidencia(cola, restoObjetivo, profundidad)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private boolean contieneNoTerminal(String cadena) {
        for (char c : cadena.toCharArray()) {
            if (noTerminales.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    // --- Métodos de Visualización y Acceso (Getters) ---

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

    public String getProduccionesTexto() {
        StringBuilder sb = new StringBuilder();
        for (String nt : producciones.keySet()) {
            sb.append(nt).append(" → ");
            List<String> prods = producciones.get(nt);
            for (int i = 0; i < prods.size(); i++) {
                sb.append(prods.get(i).equals("") ? "ε" : prods.get(i));
                if (i < prods.size() - 1) sb.append(" | ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Set<String> getNoTerminales() { return noTerminales; }
    public Set<Character> getTerminales() { return terminales; }
    public String getSimboloInicial() { return simboloInicial; }
    public Map<String, List<String>> getProducciones() { return producciones; }
}