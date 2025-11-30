package org.example.Modelo;

import java.util.*;

/**
 * Clase que representa un Autómata Finito Determinista (AFD)
 */
public class AFD {
    private Set<String> estados;
    private Set<Character> alfabeto;
    private Map<String, Map<Character, String>> transiciones;
    private String estadoInicial;
    private Set<String> estadosFinales;

    // Para almacenar el historial de ejecución
    private List<String> historialEstados;
    private List<Character> historialSimbolos;

    public AFD() {
        this.estados = new HashSet<>();
        this.alfabeto = new HashSet<>();
        this.transiciones = new HashMap<>();
        this.estadosFinales = new HashSet<>();
        this.historialEstados = new ArrayList<>();
        this.historialSimbolos = new ArrayList<>();
    }

    public void agregarEstado(String estado) {
        estados.add(estado);
        transiciones.putIfAbsent(estado, new HashMap<>());
    }

    public void agregarSimbolo(char simbolo) {
        alfabeto.add(simbolo);
    }

    public void agregarTransicion(String estadoOrigen, char simbolo, String estadoDestino) {
        if (!estados.contains(estadoOrigen) || !estados.contains(estadoDestino)) {
            throw new IllegalArgumentException("Estados no válidos");
        }
        if (!alfabeto.contains(simbolo)) {
            throw new IllegalArgumentException("Símbolo no pertenece al alfabeto");
        }

        transiciones.get(estadoOrigen).put(simbolo, estadoDestino);
    }

    public void setEstadoInicial(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        this.estadoInicial = estado;
    }

    public void agregarEstadoFinal(String estado) {
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException("Estado no existe");
        }
        estadosFinales.add(estado);
    }

    public boolean procesar(String cadena) {
        historialEstados.clear();
        historialSimbolos.clear();

        String estadoActual = estadoInicial;
        historialEstados.add(estadoActual);

        for (char simbolo : cadena.toCharArray()) {
            if (!alfabeto.contains(simbolo)) {
                return false;
            }

            historialSimbolos.add(simbolo);

            Map<Character, String> trans = transiciones.get(estadoActual);
            if (trans == null || !trans.containsKey(simbolo)) {
                return false;
            }

            estadoActual = trans.get(simbolo);
            historialEstados.add(estadoActual);
        }

        return estadosFinales.contains(estadoActual);
    }

    public List<String> obtenerPasoAPaso() {
        List<String> pasos = new ArrayList<>();

        pasos.add("Estado inicial: " + estadoInicial);

        for (int i = 0; i < historialSimbolos.size(); i++) {
            pasos.add(String.format("Leer '%c' → Ir a %s",
                    historialSimbolos.get(i),
                    historialEstados.get(i + 1)));
        }

        String estadoFinal = historialEstados.get(historialEstados.size() - 1);
        pasos.add("Estado final: " + estadoFinal);
        pasos.add(estadosFinales.contains(estadoFinal) ? "✓ ACEPTADA" : "✗ RECHAZADA");

        return pasos;
    }

    // Getters
    public Set<String> getEstados() { return estados; }
    public Set<Character> getAlfabeto() { return alfabeto; }
    public Map<String, Map<Character, String>> getTransiciones() { return transiciones; }
    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstadosFinales() { return estadosFinales; }
    public List<String> getHistorialEstados() { return historialEstados; }
}