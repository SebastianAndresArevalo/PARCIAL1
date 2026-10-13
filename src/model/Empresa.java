package model;

import tools.ListaSimple;

import java.util.*;


public class Empresa {

    private final List<Paquete> pendientes = new ArrayList<>();
    private final Map<String, Paquete> paquetesPorCodigo = new HashMap<>();
    private final Map<String, Repartidor> repartidoresPorId = new HashMap<>();
    private final Queue<Paquete> colaLlegada = new ArrayDeque<>();
    private final Queue<Paquete> colaPrioridad = new PriorityQueue<>(ORDEN_DESPACHO);
    private final ListaSimple<Paquete> historial = new ListaSimple<>();

    // Cuando se utilice despacho prioritario deberá considerarse primero la prioridad, posteriormente el tiempo estimado y finalmente el código.
    private static final Comparator<Paquete> ORDEN_DESPACHO = Comparator
            .comparingInt(Paquete::getPrioridad).reversed()
            .thenComparingInt(Paquete::getTiempoEstimado)
            .thenComparing(Paquete::getCodigo);


    //Validación de códigos e identificaciones únicas;
    public boolean existePaquete(String codigo) {
        return paquetesPorCodigo.containsKey(codigo);
    }

    public boolean existeRepartidor(String identificacion) {
        return repartidoresPorId.containsKey(identificacion);
    }


    //Registro y consulta de Paquetes
    public void registrarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("El paquete no puede ser nulo.");
        }
        if (paquetesPorCodigo.containsKey(paquete.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe un paquete con el código " + paquete.getCodigo() + ".");
        }

        pendientes.add(paquete);
        colaLlegada.offer(paquete);
        colaPrioridad.offer(paquete);
        paquetesPorCodigo.put(paquete.getCodigo(), paquete);
    }

    public Paquete consultarPaquete(String codigo) {
        return paquetesPorCodigo.get(codigo);
    }

    // Cuando se utilice atención por orden de llegada deberá respetarse el orden original de registro.
    public Paquete atenderPorOrdenLlegada() {
        Paquete paquete = colaLlegada.poll();
        if (paquete != null) {
            colaPrioridad.remove(paquete);
        }
        return paquete;
    }

    public Paquete despacharPorPrioridad() {
        Paquete paquete = colaPrioridad.poll();
        if (paquete != null) {
            colaLlegada.remove(paquete);
        }
        return paquete;
    }

    //El historial deberá conservar el orden real de las entregas.
    public void registrarEntrega(String codigoPaquete) {
        Paquete paquete = consultarPaquete(codigoPaquete);
        if (paquete == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigoPaquete + ".");
        }
        if (!pendientes.remove(paquete)) {
            throw new IllegalStateException("El paquete " + codigoPaquete + " ya fue entregado.");
        }

        colaLlegada.remove(paquete);
        colaPrioridad.remove(paquete);
        historial.agregar(paquete);
    }

    public Paquete obtenerEntrega(int posicion) {
        return historial.obtener(posicion);
    }

    public int cantidadEntregas() {
        return historial.tamano();
    }

    public double pesoTotalPorDestino(String destino) {
        return pesoTotalPorDestinoRec(pendientes, destino, 0);
    }

    //Caso recursividad 1
    private double pesoTotalPorDestinoRec(List<Paquete> paquetes, String destino, int indice) {

        if (indice == paquetes.size()) {
            return 0;
        }

        Paquete actual = paquetes.get(indice);
        double pesoActual = actual.getDestino().equalsIgnoreCase(destino) ? actual.getPeso() : 0;
        return pesoActual + pesoTotalPorDestinoRec(paquetes, destino, indice + 1);
    }

    //Caso recursividad 2
    public int contarPrioridadMinima(int prioridadMinima) {
        return contarPrioridadMinimaRec(pendientes, prioridadMinima, 0);
    }

    private int contarPrioridadMinimaRec(List<Paquete> paquetes, int prioridadMinima, int indice) {

        if (indice == paquetes.size()) {
            return 0;
        }

        int cuentaActual = paquetes.get(indice).getPrioridad() >= prioridadMinima ? 1 : 0;
        return cuentaActual + contarPrioridadMinimaRec(paquetes, prioridadMinima, indice + 1);
    }

    //Consulta y registro de repartidores
    public void registrarRepartidor(Repartidor repartidor) {
        if (repartidor == null) {
            throw new IllegalArgumentException("El repartidor no puede ser nulo.");
        }
        if (repartidoresPorId.containsKey(repartidor.getIdentificacion())) {
            throw new IllegalArgumentException(
                    "Ya existe un repartidor con la identificación " + repartidor.getIdentificacion() + ".");
        }

        repartidoresPorId.put(repartidor.getIdentificacion(), repartidor);
    }

    public Repartidor consultarRepartidor(String identificacion) {
        return repartidoresPorId.get(identificacion);
    }



    // Un repartidor no disponible no puede recibir una nueva asignación
    public void asignarPaquete(String codigoPaquete, String identificacionRepartidor) {
        Paquete paquete = consultarPaquete(codigoPaquete);
        if (paquete == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigoPaquete + ".");
        }

        Repartidor repartidor = consultarRepartidor(identificacionRepartidor);
        if (repartidor == null) {
            throw new IllegalArgumentException(
                    "No existe el repartidor " + identificacionRepartidor + ".");
        }

        if (!repartidor.isDisponible()) {
            throw new IllegalStateException(
                    "El repartidor " + identificacionRepartidor + " no está disponible.");
        }

        // La zona del repartidor debe corresponder al destino del paquete asignado.
        if (!repartidor.getZona().equalsIgnoreCase(paquete.getDestino())) {
            throw new IllegalArgumentException(
                    "La zona del repartidor (" + repartidor.getZona()
                            + ") no corresponde al destino del paquete (" + paquete.getDestino() + ").");
        }

        repartidor.setDisponible(false);

        repartidor.setDisponible(false);
    }

    //Metodo Divide y Venceras
    public Paquete paqueteMayorPeso() {
        if (pendientes.isEmpty()) {
            return null;
        }
        return mayorPesoDivideYVencerasRec(pendientes, 0, pendientes.size() - 1);
    }

    // Trabaja sobre el rango [inicio, fin], ambos incluidos.
    private Paquete mayorPesoDivideYVencerasRec(List<Paquete> paquetes, int inicio, int fin) {
        // CASO BASE: un solo paquete, que es el mayor de su rango.
        if (inicio == fin) {
            return paquetes.get(inicio);
        }

        // DIVISIÓN: se parte el rango en dos mitades.
        int medio = inicio + (fin - inicio) / 2;

        // SUBPROBLEMA IZQUIERDO: mayor de la primera mitad.
        Paquete mayorIzquierdo = mayorPesoDivideYVencerasRec(paquetes, inicio, medio);

        // SUBPROBLEMA DERECHO: mayor de la segunda mitad.
        Paquete mayorDerecho = mayorPesoDivideYVencerasRec(paquetes, medio + 1, fin);

        // COMBINACIÓN: el mayor del rango completo es el mayor de los dos resultados parciales.
        // Si empatan, se conserva el que se registró primero (el de la izquierda).
        return mayorIzquierdo.getPeso() >= mayorDerecho.getPeso() ? mayorIzquierdo : mayorDerecho;
    }
}