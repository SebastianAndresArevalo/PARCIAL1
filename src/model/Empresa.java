package model;

import tools.ListaSimple;

import java.util.*;


public class Empresa {

    private final List<Paquete> pendientes = new ArrayList<>();
    private final Map<String, Paquete> paquetesPorCodigo = new HashMap<>();
    private final Map<String, Repartidor> repartidoresPorId = new LinkedHashMap<>();
    private final Map<String, List<Paquete>> paquetesPorMunicipio = new LinkedHashMap<>();
    private final Map<String, Repartidor> asignaciones = new LinkedHashMap<>();
    private final Queue<Paquete> colaLlegada = new ArrayDeque<>();
    private final Queue<Paquete> colaPrioridad = new PriorityQueue<>(Paquete.ORDEN_DESPACHO);
    private final ListaSimple<Paquete> historial = new ListaSimple<>();
    private final Set<String> municipios = new LinkedHashSet<>();
    private final SortedSet<String> municipiosOrdenados = new TreeSet<>();


    //Busca un paquete por su "llave", ya que se trata de un HashMap
    public boolean existePaquete(String codigo) {
        return paquetesPorCodigo.containsKey(codigo);
    }

    //Busca un repartidor por su "llave", ya que se trata de un LinkedHashMap, este sale en el orden registrado
    public boolean existeRepartidor(String identificacion) {
        return repartidoresPorId.containsKey(identificacion);
    }


    //Registro de paquetes
    public void registrarPaquete(Paquete paquete) {

        //Verifica que el paquete no sea null y que no exista previamente
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
        municipiosOrdenados.add(paquete.getDestino());
        municipios.add(paquete.getDestino());
        paquetesPorMunicipio.computeIfAbsent(paquete.getDestino(), k -> new ArrayList<>()).add(paquete);
    }

    //Entrega los datos del paquete que tiene el codigo requerido
    public Paquete consultarPaquete(String codigo) {
        return paquetesPorCodigo.get(codigo);
    }

    //Permite visualizar los municipios existentes sin permitir ninguna modificación
    public Set<String> obtenerMunicipios() {
        return Collections.unmodifiableSet(municipios);
    }

    //Permite visualizar los paquetes dependiendo del municipio, si hay datos existentes, no deja modificarlos
    public List<Paquete> consultarPaquetesPorMunicipio(String municipio) {
        List<Paquete> paquetes = paquetesPorMunicipio.get(municipio);
        if (paquetes == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(paquetes);
    }


    public Map<String, List<Paquete>> obtenerPaquetesPorMunicipio() {
        return Collections.unmodifiableMap(paquetesPorMunicipio);
    }

    public int cantidadEnEspera() {
        return colaLlegada.size();
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

        Repartidor repartidor = asignaciones.get(codigoPaquete);
        if (repartidor != null) {
            repartidor.setDisponible(true);
        }
    }

    //Verifica una entrega ya realizada
    public Paquete obtenerEntrega(int posicion) {
        return historial.obtener(posicion);
    }

    //Da el total de entregas dependiendo de cuantos datos allá en el historial
    public int cantidadEntregas() {
        return historial.tamano();
    }

    //Muestra el historial de entregas
    public Iterable<Paquete> historialEntregas() {
        return historial;
    }


    public List<Paquete> obtenerPendientes() {
        return Collections.unmodifiableList(pendientes);
    }

    public Collection<Repartidor> obtenerRepartidores() {
        return Collections.unmodifiableCollection(repartidoresPorId.values());
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

    public SortedSet<String> obtenerMunicipiosOrdenados() {
        return Collections.unmodifiableSortedSet(municipiosOrdenados);
    }

    // Un repartidor no disponible no puede recibir una nueva asignación
    public void asignarPaquete(String codigoPaquete, String identificacionRepartidor) {
        Paquete paquete = consultarPaquete(codigoPaquete);
        if (paquete == null) {
            throw new IllegalArgumentException("No existe el paquete " + codigoPaquete + ".");
        }
        if (!pendientes.contains(paquete)) {
            throw new IllegalStateException("El paquete " + codigoPaquete + " ya fue entregado.");
        }
        if (asignaciones.containsKey(codigoPaquete)) {
            throw new IllegalStateException("El paquete " + codigoPaquete + " ya está asignado al repartidor "
                    + asignaciones.get(codigoPaquete).getIdentificacion() + ".");
        }

        Repartidor repartidor = consultarRepartidor(identificacionRepartidor);
        if (repartidor == null) {
            throw new IllegalArgumentException("No existe el repartidor " + identificacionRepartidor + ".");
        }
        if (!repartidor.isDisponible()) {
            throw new IllegalStateException("El repartidor " + identificacionRepartidor + " no está disponible.");
        }
        if (!repartidor.getZona().equalsIgnoreCase(paquete.getDestino())) {
            throw new IllegalArgumentException(
                    "La zona del repartidor (" + repartidor.getZona()
                            + ") no corresponde al destino del paquete (" + paquete.getDestino() + ").");
        }

        asignaciones.put(codigoPaquete, repartidor);
        repartidor.setDisponible(false);
    }

    public Repartidor consultarRepartidorAsignado(String codigoPaquete) {
        return asignaciones.get(codigoPaquete);
    }

    public Map<String, Repartidor> obtenerAsignaciones() {
        return Collections.unmodifiableMap(asignaciones);
    }

    // Búsqueda binaria

    public List<Paquete> obtenerPaquetesOrdenadosPorCodigo() {
        List<Paquete> ordenados = new ArrayList<>(pendientes);
        Collections.sort(ordenados);
        return ordenados;
    }

    public Paquete buscarPaqueteBinaria(List<Paquete> ordenados, String codigo) {
        int posicion = busquedaBinariaRec(ordenados, codigo, 0, ordenados.size() - 1);
        return posicion >= 0 ? ordenados.get(posicion) : null;
    }

    private int busquedaBinariaRec(List<Paquete> ordenados, String codigo, int inicio, int fin) {

        if (inicio > fin) {
            return -1;
        }

        int medio = inicio + (fin - inicio) / 2;
        int comparacion = codigo.compareTo(ordenados.get(medio).getCodigo());

        if (comparacion == 0) {
            return medio;
        }

        if (comparacion < 0) {
            return busquedaBinariaRec(ordenados, codigo, inicio, medio - 1);
        }
        return busquedaBinariaRec(ordenados, codigo, medio + 1, fin);
    }


    //Metodo Divide y vencerás
    public Paquete paqueteMayorPeso() {
        if (pendientes.isEmpty()) {
            return null;
        }
        return mayorPesoDivideYVencerasRec(pendientes, 0, pendientes.size() - 1);
    }

    private Paquete mayorPesoDivideYVencerasRec(List<Paquete> paquetes, int inicio, int fin) {

        if (inicio == fin) {
            return paquetes.get(inicio);
        }

        int medio = inicio + (fin - inicio) / 2;

        Paquete mayorIzquierdo = mayorPesoDivideYVencerasRec(paquetes, inicio, medio);

        Paquete mayorDerecho = mayorPesoDivideYVencerasRec(paquetes, medio + 1, fin);

        return mayorIzquierdo.getPeso() >= mayorDerecho.getPeso() ? mayorIzquierdo : mayorDerecho;
    }
}