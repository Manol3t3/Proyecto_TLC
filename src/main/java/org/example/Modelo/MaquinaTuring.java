package org.example.Modelo;

import java.util.*;

/**
 * Clase que representa una Máquina de Turing
 */
public class MaquinaTuring {

    private Set<String> estados;
    private Set<Character> alfabetoEntrada;
    private Set<Character> alfabetoCinta;
    private Map<TransicionKey, TransicionValor> transiciones;
    private String estadoInicial;
    private Set<String> estadosAceptacion;
    private char simboloBlanco;

    private List<ConfiguracionMT> historial;

    public MaquinaTuring() {
        this.estados = new HashSet<>();
        this.alfabetoEntrada = new HashSet<>();
        this.alfabetoCinta = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.estadosAceptacion = new HashSet<>();
        this.simboloBlanco = '_';
        this.historial = new ArrayList<>();
    }

    /**
     * Clase para la clave de transición
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
     * Clase para el valor de transición
     */
    public static class TransicionValor {
        String estadoDestino;
        char simboloEscribir;
        Direccion movimiento;

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
     * Configuración de la MT
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

    // Métodos de configuración
    public void agregarEstado(String estado) {
        estados.add(estado);
    }

    public void agregarSimboloEntrada(char simbolo) {
        alfabetoEntrada.add(simbolo);
        alfabetoCinta.add(simbolo);
    }

    public void agregarSimboloCinta(char simbolo) {
        alfabetoCinta.add(simbolo);
    }

    public void setEstadoInicial(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        this.estadoInicial = estado;
    }

    public void agregarEstadoAceptacion(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        estadosAceptacion.add(estado);
    }

    public void setSimboloBlanco(char simbolo) {
        this.simboloBlanco = simbolo;
        alfabetoCinta.add(simbolo);
    }

    /**
     * Agrega una transición
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

    /**
     * Procesa una cadena
     */
    public boolean procesar(String entrada) {
        historial.clear();

        // Inicializar cinta
        List<Character> cinta = new ArrayList<>();
        for (char c : entrada.toCharArray()) {
            cinta.add(c);
        }
        // Agregar blancos al final
        for (int i = 0; i < 10; i++) {
            cinta.add(simboloBlanco);
        }

        String estadoActual = estadoInicial;
        int cabezal = 0;
        int maxPasos = 1000; // Prevenir bucles infinitos
        int pasos = 0;

        while (pasos < maxPasos) {
            // Guardar configuración
            historial.add(new ConfiguracionMT(estadoActual, cinta, cabezal));

            // Verificar estado de aceptación
            if (estadosAceptacion.contains(estadoActual)) {
                return true;
            }

            // Expandir cinta si es necesario
            if (cabezal < 0) {
                cinta.add(0, simboloBlanco);
                cabezal = 0;
            } else if (cabezal >= cinta.size()) {
                cinta.add(simboloBlanco);
            }

            // Buscar transición
            char simboloActual = cinta.get(cabezal);
            TransicionKey key = new TransicionKey(estadoActual, simboloActual);

            if (!transiciones.containsKey(key)) {
                // No hay transición, rechazar
                return false;
            }

            TransicionValor trans = transiciones.get(key);

            // Aplicar transición
            cinta.set(cabezal, trans.simboloEscribir);
            estadoActual = trans.estadoDestino;

            // Mover cabezal
            if (trans.movimiento == TransicionValor.Direccion.IZQUIERDA) {
                cabezal--;
            } else if (trans.movimiento == TransicionValor.Direccion.DERECHA) {
                cabezal++;
            }

            pasos++;
        }

        return false; // Excedió el límite de pasos
    }

    /**
     * Obtiene la cadena resultante en la cinta (sin blancos finales)
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