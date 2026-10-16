package model;

import java.util.Comparator;

public class Paquete implements Comparable<Paquete> {

    //Compara datos por prioridad de forma desciéndete gracias al reversed
    public static final Comparator<Paquete> POR_PRIORIDAD_DESC =
            Comparator.comparingInt(Paquete::getPrioridad).reversed();

    //Compara datos por Peso de forma desciéndete gracias al reversed
    public static final Comparator<Paquete> POR_PESO_DESC =
            Comparator.comparingDouble(Paquete::getPeso).reversed();

    //Compara datos por tiempo estimado de forma ascendente
    public static final Comparator<Paquete> POR_TIEMPO_ASC =
            Comparator.comparingInt(Paquete::getTiempoEstimado);

    //Metodo que nos dice que si hay empate en la prioridad, se decida por orden natural
    public static final Comparator<Paquete> ORDEN_DESPACHO =
            POR_PRIORIDAD_DESC
                    .thenComparing(POR_TIEMPO_ASC)
                    .thenComparing(Comparator.naturalOrder());

    private final String codigo;
    private final String destino;
    private final double peso;
    private final int prioridad;
    private final int tiempoEstimado;

    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {

        //Código no puede ser vacío
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código no puede ser nulo ni vacío.");
        }

        //Destino no nulo ni vacío
        if (destino == null || destino.isBlank()) {
            throw new IllegalArgumentException("El destino no puede ser nulo ni vacío.");
        }

        //peso no puede ser menor a 0
        if (peso <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que cero.");
        }

        //La prioridad debe estar comprendida entre 1 y 5.
        if (prioridad < 1 || prioridad > 5) {
            throw new IllegalArgumentException("La prioridad debe estar entre 1 y 5.");
        }

        //Tiempo estimado mayor que cero
        if (tiempoEstimado <= 0) {
            throw new IllegalArgumentException("El tiempo estimado debe ser mayor que cero.");
        }

        this.codigo = codigo;
        this.destino = destino;
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString() {
        return codigo + " -> " + destino
                + " (peso: " + peso + " kg, prioridad: " + prioridad
                + ", tiempo: " + tiempoEstimado + " min)";
    }
}