package org.example.Modelo;

import java.util.*;

/**
 * Representa una Máquina de Turing genérica con:
 * - Conjunto de estados
 * - Alfabeto de entrada
 * - Alfabeto de cinta
 * - Función de transición
 * - Estado inicial
 * - Estados de aceptación
 * - Símbolo blanco en la cinta
 */
public class MaquinaTuring {

    /** Conjunto de estados de la MT */
    private Set<String> estados;

    /** Alfabeto de entrada (símbolos permitidos en la cadena de entrada) */
    private Set<Character> alfabetoEntrada;

    /** Alfabeto de cinta (incluye símbolos de entrada y el blanco) */
    private Set<Character> alfabetoCinta;

    /** Función de transición δ : (estado, símbolo) → (nuevoEstado, símboloEscribir, movimiento) */
    private Map<TransicionKey, TransicionValor> transiciones;

    /** Estado inicial de la MT */
    private String estadoInicial;

    /** Conjunto de estados de aceptación */
    private Set<String> estadosAceptacion;

    /** Símbolo blanco usado en la cinta */
    private char simboloBlanco;

    /** Historial de configuraciones generadas al procesar una cadena */
    private List<ConfiguracionMT> historial;

    /**
     * Constructor: inicializa estructuras vacías y establece '_' como blanco.
     */
    public MaquinaTuring() {
        this.estados = new HashSet<>();
        this.alfabetoEntrada = new HashSet<>();
        this.alfabetoCinta = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.estadosAceptacion = new HashSet<>();
        this.simboloBlanco = '_';
        this.historial = new ArrayList<>();
    }

    //              Clases internas auxiliares

    /**
     * Representa la clave para la función de transición:
     * un par (estadoActual, simboloLeído).
     */
    public static class TransicionKey {
        String estado;
        char simbolo;

        public TransicionKey(String estado, char simbolo) {
            this.estado = estado;
            this.simbolo = simbolo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TransicionKey)) return false;
            TransicionKey that = (TransicionKey) o;
            return simbolo == that.simbolo && Objects.equals(estado, that.estado);
        }

        @Override
        public int hashCode() {
            return Objects.hash(estado, simbolo);
        }

        @Override
        public String toString() {
            return String.format("(%s, %c)", estado, simbolo);
        }
    }

    /**
     * Representa el valor de la función de transición:
     * (estadoDestino, símbolo a escribir, dirección de movimiento).
     */
    public static class TransicionValor {

        public String estadoDestino;
        public char simboloEscribir;
        public Direccion movimiento;

        /** Dirección del movimiento del cabezal */
        public enum Direccion { IZQUIERDA, DERECHA, ESTATICO }

        public TransicionValor(String estadoDestino, char simboloEscribir, Direccion movimiento) {
            this.estadoDestino = estadoDestino;
            this.simboloEscribir = simboloEscribir;
            this.movimiento = movimiento;
        }

        @Override
        public String toString() {
            String dir = movimiento == Direccion.IZQUIERDA ? "L" :
                    movimiento == Direccion.DERECHA ? "R" : "S";
            return String.format("(%s, %c, %s)", estadoDestino, simboloEscribir, dir);
        }
    }

    /**
     * Representa una configuración instantánea de la MT:
     * estado actual, contenido de la cinta y posición del cabezal.
     */
    public static class ConfiguracionMT {

        public String estado;
        public List<Character> cinta;
        public int cabezal;

        public ConfiguracionMT(String estado, List<Character> cinta, int cabezal) {
            this.estado = estado;
            this.cinta = new ArrayList<>(cinta);
            this.cabezal = cabezal;
        }

        /**
         * Devuelve la cinta como texto, marcando la posición del cabezal con [ ].
         */
        public String getCintaString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cinta.size(); i++) {
                if (i == cabezal) {
                    sb.append("[").append(cinta.get(i)).append("]");
                } else {
                    sb.append(cinta.get(i));
                }
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return String.format("Estado: %s, Cinta: %s", estado, getCintaString());
        }
    }

    //              Métodos de configuración

    /** Agrega un estado al conjunto de estados */
    public void agregarEstado(String estado) {
        estados.add(estado);
    }

    /** Agrega un símbolo al alfabeto de entrada (y de cinta automáticamente) */
    public void agregarSimboloEntrada(char simbolo) {
        alfabetoEntrada.add(simbolo);
        alfabetoCinta.add(simbolo);
    }

    /** Agrega un símbolo exclusivamente al alfabeto de cinta */
    public void agregarSimboloCinta(char simbolo) {
        alfabetoCinta.add(simbolo);
    }

    /** Define el estado inicial */
    public void setEstadoInicial(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        this.estadoInicial = estado;
    }

    /** Agrega un estado al conjunto de aceptación */
    public void agregarEstadoAceptacion(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        estadosAceptacion.add(estado);
    }

    /** establece el símbolo blanco y lo agrega al alfabeto de cinta */
    public void setSimboloBlanco(char simbolo) {
        this.simboloBlanco = simbolo;
        alfabetoCinta.add(simbolo);
    }

    /**
     * Agrega una transición δ(estadoOrigen, simboloLeer) = (estadoDestino, simboloEscribir, direccion)
     */
    public void agregarTransicion(String estadoOrigen, char simboloLeer,
                                  String estadoDestino, char simboloEscribir,
                                  TransicionValor.Direccion direccion) {

        if (!estados.contains(estadoOrigen) || !estados.contains(estadoDestino)) {
            throw new IllegalArgumentException("Estados no válidos");
        }

        TransicionKey key = new TransicionKey(estadoOrigen, simboloLeer);
        transiciones.put(key, new TransicionValor(estadoDestino, simboloEscribir, direccion));
    }

    //              Ejecución de la máquina

    /**
     * Procesa una cadena de entrada.
     * Ejecuta la máquina de Turing paso a paso hasta que:
     *  - alcanza un estado de aceptación → retorna true
     *  - no existe transición → retorna false
     *  - excede el límite de pasos → retorna false
     */
    public boolean procesar(String entrada) {
        historial.clear();

        // Inicializar cinta con la entrada
        List<Character> cinta = new ArrayList<>();
        for (char c : entrada.toCharArray()) {
            cinta.add(c);
        }

        // Agregar blancos adicionales
        for (int i = 0; i < 10; i++) cinta.add(simboloBlanco);

        String estadoActual = estadoInicial;
        int cabezal = 0;
        int pasos = 0;
        int maxPasos = 1000;

        // Ciclo principal de ejecución
        while (pasos < maxPasos) {

            // Registrar configuración actual
            historial.add(new ConfiguracionMT(estadoActual, cinta, cabezal));

            // Verificar aceptación
            if (estadosAceptacion.contains(estadoActual)) {
                return true;
            }

            // Extender cinta si cabezal sale del rango
            if (cabezal < 0) {
                cinta.add(0, simboloBlanco);
                cabezal = 0;
            } else if (cabezal >= cinta.size()) {
                cinta.add(simboloBlanco);
            }

            // Obtener transición
            char simboloActual = cinta.get(cabezal);
            TransicionKey key = new TransicionKey(estadoActual, simboloActual);

            if (!transiciones.containsKey(key)) {
                return false; // No hay transición → rechazo
            }

            TransicionValor trans = transiciones.get(key);

            // Aplicar transición
            cinta.set(cabezal, trans.simboloEscribir);
            estadoActual = trans.estadoDestino;

            switch (trans.movimiento) {
                case IZQUIERDA -> cabezal--;
                case DERECHA -> cabezal++;
            }

            pasos++;
        }

        return false; // Excedió pasos permitidos
    }


    //              Métodos auxiliares
    /**
     * Retorna la cadena final contenida en la cinta
     * ignorando símbolos blancos al final.
     */
    public String obtenerResultadoCinta() {
        if (historial.isEmpty()) return "";

        ConfiguracionMT ultima = historial.get(historial.size() - 1);
        StringBuilder sb = new StringBuilder();

        for (char c : ultima.cinta) {
            if (c == simboloBlanco) break;
            sb.append(c);
        }

        return sb.toString();
    }

    // Getters
    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabetoEntrada() { return alfabetoEntrada; }
    public Set<Character> getAlfabetoCinta() { return alfabetoCinta; }
    public Map<TransicionKey, TransicionValor> getTransiciones() { return transiciones; }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosAceptacion() { return estadosAceptacion; }
    public char getSimboloBlanco() { return simboloBlanco; }
    public List<ConfiguracionMT> getHistorial() { return historial; }
}
