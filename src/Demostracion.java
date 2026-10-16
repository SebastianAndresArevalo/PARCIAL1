import model.Empresa;
import model.Paquete;
import model.Repartidor;
import tools.ListaSimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Demostración del sistema de QuindíoExpress: cada sección ejecuta una de las operaciones del enunciado.
 */
public class Demostracion {

    public static void main(String[] args) {
        Empresa empresa = new Empresa();

        titulo("1. Carga de datos iniciales");
        cargarRepartidores(empresa);
        cargarPaquetes(empresa);
        System.out.println("Paquetes pendientes: " + empresa.obtenerPendientes().size());
        System.out.println("Repartidores: " + empresa.obtenerRepartidores().size());
        for (Repartidor r : empresa.obtenerRepartidores()) {
            System.out.println("  " + r);
        }

        demostrarConsultasYValidaciones(empresa);
        demostrarMunicipios(empresa);
        demostrarOrdenamientos(empresa);
        demostrarRecursividad(empresa);
        demostrarBusquedaBinaria(empresa);
        demostrarDivideYVenceras(empresa);
        demostrarAtencion(empresa);
        demostrarAsignacion(empresa);
        demostrarEntregasEHistorial(empresa);
        demostrarListaPropia();
    }

    // ------------------------------------------------------------------ secciones

    private static void demostrarConsultasYValidaciones(Empresa empresa) {
        titulo("2. Consulta por código y validaciones");
        System.out.println("¿Existe PQ731? " + empresa.existePaquete("PQ731"));
        System.out.println("Consulta PQ731: " + empresa.consultarPaquete("PQ731"));
        System.out.println("¿Existe PQ000? " + empresa.existePaquete("PQ000"));
        System.out.println("Consulta PQ000: " + empresa.consultarPaquete("PQ000"));

        intentar("Registrar un código repetido (PQ731)",
                () -> empresa.registrarPaquete(new Paquete("PQ731", "Armenia", 1.0, 1, 10)));
        intentar("Registrar un repartidor repetido (R001)",
                () -> empresa.registrarRepartidor(new Repartidor("R001", "Otro", "Armenia", true)));
        intentar("Paquete con peso 0",
                () -> new Paquete("PQ001", "Armenia", 0, 3, 10));
        intentar("Paquete con prioridad 6",
                () -> new Paquete("PQ002", "Armenia", 1.0, 6, 10));
        intentar("Paquete con tiempo estimado 0",
                () -> new Paquete("PQ003", "Armenia", 1.0, 3, 0));
        intentar("Paquete con destino vacío",
                () -> new Paquete("PQ004", "  ", 1.0, 3, 10));
        System.out.println("Pendientes tras los intentos fallidos (sin cambios): "
                + empresa.obtenerPendientes().size());
    }

    private static void demostrarMunicipios(Empresa empresa) {
        titulo("3. Municipios (Set, TreeSet y Map)");
        System.out.println("Municipios sin repetidos (HashSet): " + empresa.obtenerMunicipios());
        System.out.println("Municipios ordenados (TreeSet):     " + empresa.obtenerMunicipiosOrdenados());
        System.out.println("Paquetes agrupados por municipio (Map):");
        for (Map.Entry<String, List<Paquete>> entrada : empresa.obtenerPaquetesPorMunicipio().entrySet()) {
            System.out.println("  " + entrada.getKey() + " -> " + codigos(entrada.getValue()));
        }
        System.out.println("Consulta de un municipio sin paquetes (Pereira): "
                + empresa.consultarPaquetesPorMunicipio("Pereira"));
    }

    private static void demostrarOrdenamientos(Empresa empresa) {
        titulo("4. Orden natural (Comparable) y criterios alternativos (Comparator)");
        List<Paquete> copia = new ArrayList<>(empresa.obtenerPendientes());

        System.out.println("Orden de registro (primeros 6):  " + codigos(copia.subList(0, 6)));

        Collections.sort(copia);
        System.out.println("Orden natural, por código (6):   " + codigos(copia.subList(0, 6)));

        copia.sort(Paquete.POR_PRIORIDAD_DESC);
        System.out.println("Por prioridad, mayor a menor:    " + detalle(copia.subList(0, 6), "prioridad"));

        copia.sort(Paquete.POR_PESO_DESC);
        System.out.println("Por peso, mayor a menor:         " + detalle(copia.subList(0, 6), "peso"));

        copia.sort(Paquete.POR_TIEMPO_ASC);
        System.out.println("Por tiempo estimado, menor a mayor: " + detalle(copia.subList(0, 6), "tiempo"));

        Collections.sort(copia);
        System.out.println("El orden natural sigue siendo por código: " + codigos(copia.subList(0, 6)));
    }

    private static void demostrarRecursividad(Empresa empresa) {
        titulo("5. Recursividad");
        System.out.printf("Peso total de Armenia:   %.1f kg%n", empresa.pesoTotalPorDestino("Armenia"));
        System.out.printf("Peso total de Salento:   %.1f kg%n", empresa.pesoTotalPorDestino("Salento"));
        System.out.printf("Peso total de Pereira:   %.1f kg (sin paquetes)%n", empresa.pesoTotalPorDestino("Pereira"));
        System.out.println("Paquetes con prioridad >= 4: " + empresa.contarPrioridadMinima(4));
        System.out.println("Paquetes con prioridad >= 1: " + empresa.contarPrioridadMinima(1));
        System.out.println("Paquetes con prioridad >= 5: " + empresa.contarPrioridadMinima(5));

        // Ejemplos del enunciado, con conjuntos reducidos.
        Empresa ejemplo1 = new Empresa();
        ejemplo1.registrarPaquete(new Paquete("PQ101", "Armenia", 4.5, 3, 10));
        ejemplo1.registrarPaquete(new Paquete("PQ203", "Salento", 3.0, 3, 10));
        ejemplo1.registrarPaquete(new Paquete("PQ415", "Armenia", 7.2, 3, 10));
        ejemplo1.registrarPaquete(new Paquete("PQ730", "Armenia", 2.3, 3, 10));
        System.out.printf("Ejemplo del enunciado, peso de Armenia: %.1f kg (esperado 14.0)%n",
                ejemplo1.pesoTotalPorDestino("Armenia"));

        Empresa ejemplo2 = new Empresa();
        int[] prioridades = {5, 2, 4, 1, 5, 3, 4};
        for (int i = 0; i < prioridades.length; i++) {
            ejemplo2.registrarPaquete(new Paquete("PQ" + (100 + i), "Armenia", 1.0, prioridades[i], 10));
        }
        System.out.println("Ejemplo del enunciado, prioridad >= 4: "
                + ejemplo2.contarPrioridadMinima(4) + " (esperado 4)");
    }

    private static void demostrarBusquedaBinaria(Empresa empresa) {
        titulo("6. Búsqueda binaria (sobre paquetes ordenados por código)");
        List<Paquete> ordenados = empresa.obtenerPaquetesOrdenadosPorCodigo();
        System.out.println("Primeros 6 de la lista ordenada: " + codigos(ordenados.subList(0, 6)));
        System.out.println("Busca PQ731 (existe):          " + empresa.buscarPaqueteBinaria(ordenados, "PQ731"));
        System.out.println("Busca PQ061 (primero):         " + empresa.buscarPaqueteBinaria(ordenados, "PQ061"));
        System.out.println("Busca PQ977 (último):          " + empresa.buscarPaqueteBinaria(ordenados, "PQ977"));
        System.out.println("Busca PQ500 (no existe):       " + empresa.buscarPaqueteBinaria(ordenados, "PQ500"));
    }

    private static void demostrarDivideYVenceras(Empresa empresa) {
        titulo("7. Divide y vencerás: paquete de mayor peso");
        System.out.println("Mayor peso: " + empresa.paqueteMayorPeso());
        System.out.println("Sin paquetes: " + new Empresa().paqueteMayorPeso());
    }

    private static void demostrarAtencion(Empresa empresa) {
        titulo("8. Atención por orden de llegada y despacho por prioridad");

        // Ejemplos del enunciado, con empresas pequeñas e independientes.
        Empresa llegada = new Empresa();
        for (String codigo : new String[]{"PQ731", "PQ105", "PQ942", "PQ318"}) {
            llegada.registrarPaquete(new Paquete(codigo, "Armenia", 1.0, 3, 10));
        }
        System.out.print("Ejemplo orden de llegada (esperado PQ731, PQ105, PQ942, PQ318): ");
        List<String> atendidos = new ArrayList<>();
        Paquete siguiente;
        while ((siguiente = llegada.atenderPorOrdenLlegada()) != null) {
            atendidos.add(siguiente.getCodigo());
        }
        System.out.println(String.join(", ", atendidos));

        Empresa prioridad = new Empresa();
        prioridad.registrarPaquete(new Paquete("PQ420", "Armenia", 1.0, 3, 30));
        prioridad.registrarPaquete(new Paquete("PQ731", "Armenia", 1.0, 5, 45));
        prioridad.registrarPaquete(new Paquete("PQ105", "Armenia", 1.0, 5, 25));
        prioridad.registrarPaquete(new Paquete("PQ942", "Armenia", 1.0, 4, 20));
        System.out.print("Ejemplo despacho prioritario (esperado PQ105, PQ731, PQ942, PQ420): ");
        atendidos.clear();
        while ((siguiente = prioridad.despacharPorPrioridad()) != null) {
            atendidos.add(siguiente.getCodigo());
        }
        System.out.println(String.join(", ", atendidos));

        // Con los datos reales.
        System.out.println("\nCon los 30 paquetes reales. En espera: " + empresa.cantidadEnEspera());
        System.out.print("Orden de llegada, 3 atendidos: ");
        System.out.println(codigos(List.of(
                empresa.atenderPorOrdenLlegada(),
                empresa.atenderPorOrdenLlegada(),
                empresa.atenderPorOrdenLlegada())));
        System.out.println("En espera: " + empresa.cantidadEnEspera());

        System.out.println("Despacho por prioridad, 4 atendidos (prioridad 5; desempate por menor tiempo):");
        for (int i = 0; i < 4; i++) {
            System.out.println("  " + empresa.despacharPorPrioridad());
        }
        System.out.println("En espera: " + empresa.cantidadEnEspera());
        System.out.println("Los atendidos siguen pendientes hasta su entrega: "
                + empresa.obtenerPendientes().size() + " pendientes");
    }

    private static void demostrarAsignacion(Empresa empresa) {
        titulo("9. Asignación de paquetes a repartidores");
        empresa.asignarPaquete("PQ731", "R001");
        System.out.println("PQ731 (Armenia) asignado a: " + empresa.consultarRepartidorAsignado("PQ731"));

        intentar("Asignar PQ472 (Armenia) a R001, que ya está ocupado",
                () -> empresa.asignarPaquete("PQ472", "R001"));
        intentar("Asignar PQ512 (Quimbaya) a R005, que no está disponible",
                () -> empresa.asignarPaquete("PQ512", "R005"));
        intentar("Asignar PQ105 (Salento) a R003, de la zona Calarcá",
                () -> empresa.asignarPaquete("PQ105", "R003"));
        intentar("Asignar PQ731 otra vez, ya asignado",
                () -> empresa.asignarPaquete("PQ731", "R002"));
        intentar("Asignar un paquete inexistente",
                () -> empresa.asignarPaquete("PQ000", "R001"));
        intentar("Asignar a un repartidor inexistente",
                () -> empresa.asignarPaquete("PQ105", "R999"));

        empresa.asignarPaquete("PQ105", "R002");
        empresa.asignarPaquete("PQ977", "R004");
        System.out.println("Asignaciones vigentes:");
        for (Map.Entry<String, Repartidor> e : empresa.obtenerAsignaciones().entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue());
        }
    }

    private static void demostrarEntregasEHistorial(Empresa empresa) {
        titulo("10. Entregas e historial (lista simplemente enlazada propia)");
        System.out.println("Pendientes antes de entregar: " + empresa.obtenerPendientes().size());

        empresa.registrarEntrega("PQ105");
        empresa.registrarEntrega("PQ731");
        empresa.registrarEntrega("PQ977");

        System.out.println("Pendientes después de entregar: " + empresa.obtenerPendientes().size());
        System.out.println("Entregas registradas: " + empresa.cantidadEntregas());

        StringBuilder recorrido = new StringBuilder();
        for (Paquete p : empresa.historialEntregas()) {
            if (recorrido.length() > 0) {
                recorrido.append(" -> ");
            }
            recorrido.append(p.getCodigo());
        }
        System.out.println("Historial recorrido con for-each (Iterable/Iterator): " + recorrido);
        System.out.println("Entrega en la posición 1: " + empresa.obtenerEntrega(1));

        System.out.println("R001 vuelve a estar disponible: "
                + empresa.consultarRepartidor("R001").isDisponible());
        intentar("Entregar PQ105 por segunda vez", () -> empresa.registrarEntrega("PQ105"));
        intentar("Entregar un paquete inexistente", () -> empresa.registrarEntrega("PQ000"));
        intentar("Asignar un paquete ya entregado", () -> empresa.asignarPaquete("PQ105", "R002"));
        intentar("Pedir la entrega en la posición 10", () -> empresa.obtenerEntrega(10));
    }

    private static void demostrarListaPropia() {
        titulo("11. Lista simplemente enlazada propia: casos borde");
        ListaSimple<String> lista = new ListaSimple<>();
        System.out.println("Lista vacía -> tamaño " + lista.tamano() + ", vacía: " + lista.estaVacia());
        System.out.println("Eliminar de lista vacía: " + lista.eliminar("A"));

        lista.agregar("A");
        System.out.println("Un elemento: " + texto(lista) + ", tamaño " + lista.tamano());
        System.out.println("Eliminar el único elemento: " + lista.eliminar("A")
                + " -> " + texto(lista) + ", vacía: " + lista.estaVacia());

        for (String s : new String[]{"A", "B", "C", "D", "E"}) {
            lista.agregar(s);
        }
        System.out.println("Lista: " + texto(lista) + " (tamaño " + lista.tamano() + ")");
        System.out.println("Elemento en la posición 2: " + lista.obtener(2));
        System.out.println("Eliminar el primero (A): " + lista.eliminar("A") + " -> " + texto(lista));
        System.out.println("Eliminar uno intermedio (C): " + lista.eliminar("C") + " -> " + texto(lista));
        System.out.println("Eliminar el último (E): " + lista.eliminar("E") + " -> " + texto(lista));
        System.out.println("Eliminar uno inexistente (Z): " + lista.eliminar("Z") + " -> " + texto(lista));
        lista.agregar("F");
        System.out.println("Agregar después de eliminar el último: " + texto(lista));
        intentar("Obtener la posición 5", () -> lista.obtener(5));
        System.out.println();
    }

    // ------------------------------------------------------------------ datos

    private static void cargarRepartidores(Empresa empresa) {
        empresa.registrarRepartidor(new Repartidor("R001", "Carlos Ramírez", "Armenia", true));
        empresa.registrarRepartidor(new Repartidor("R002", "Laura Gómez", "Salento", true));
        empresa.registrarRepartidor(new Repartidor("R003", "Andrés Torres", "Calarcá", true));
        empresa.registrarRepartidor(new Repartidor("R004", "María Rojas", "Montenegro", true));
        empresa.registrarRepartidor(new Repartidor("R005", "Juan Herrera", "Quimbaya", false));
        empresa.registrarRepartidor(new Repartidor("R006", "Sofía Castaño", "Circasia", true));
    }

    private static void cargarPaquetes(Empresa empresa) {
        empresa.registrarPaquete(new Paquete("PQ731", "Armenia", 4.5, 5, 45));
        empresa.registrarPaquete(new Paquete("PQ105", "Salento", 3.0, 5, 25));
        empresa.registrarPaquete(new Paquete("PQ942", "Armenia", 7.2, 4, 20));
        empresa.registrarPaquete(new Paquete("PQ318", "Calarcá", 4.1, 4, 20));
        empresa.registrarPaquete(new Paquete("PQ567", "Salento", 6.8, 3, 55));
        empresa.registrarPaquete(new Paquete("PQ829", "Armenia", 2.3, 2, 35));
        empresa.registrarPaquete(new Paquete("PQ423", "Montenegro", 7.7, 3, 38));
        empresa.registrarPaquete(new Paquete("PQ512", "Quimbaya", 5.0, 1, 65));
        empresa.registrarPaquete(new Paquete("PQ377", "Circasia", 6.0, 2, 27));
        empresa.registrarPaquete(new Paquete("PQ584", "Calarcá", 18.7, 5, 30));
        empresa.registrarPaquete(new Paquete("PQ214", "Armenia", 12.0, 3, 30));
        empresa.registrarPaquete(new Paquete("PQ389", "Salento", 1.2, 1, 60));
        empresa.registrarPaquete(new Paquete("PQ977", "Montenegro", 3.9, 5, 28));
        empresa.registrarPaquete(new Paquete("PQ869", "Quimbaya", 9.1, 4, 52));
        empresa.registrarPaquete(new Paquete("PQ925", "Circasia", 4.8, 3, 18));
        empresa.registrarPaquete(new Paquete("PQ692", "Calarcá", 2.7, 3, 25));
        empresa.registrarPaquete(new Paquete("PQ655", "Armenia", 0.8, 1, 15));
        empresa.registrarPaquete(new Paquete("PQ746", "Salento", 8.9, 4, 45));
        empresa.registrarPaquete(new Paquete("PQ061", "Montenegro", 13.2, 4, 48));
        empresa.registrarPaquete(new Paquete("PQ294", "Quimbaya", 2.0, 3, 42));
        empresa.registrarPaquete(new Paquete("PQ183", "Circasia", 14.5, 4, 36));
        empresa.registrarPaquete(new Paquete("PQ840", "Calarcá", 10.4, 1, 35));
        empresa.registrarPaquete(new Paquete("PQ472", "Armenia", 9.6, 5, 25));
        empresa.registrarPaquete(new Paquete("PQ231", "Salento", 5.5, 2, 70));
        empresa.registrarPaquete(new Paquete("PQ738", "Montenegro", 1.5, 2, 22));
        empresa.registrarPaquete(new Paquete("PQ640", "Quimbaya", 11.6, 5, 33));
        empresa.registrarPaquete(new Paquete("PQ701", "Circasia", 0.5, 1, 12));
        empresa.registrarPaquete(new Paquete("PQ156", "Calarcá", 6.3, 2, 40));
        empresa.registrarPaquete(new Paquete("PQ903", "Armenia", 3.4, 4, 40));
        empresa.registrarPaquete(new Paquete("PQ127", "Armenia", 15.3, 2, 50));
    }

    // ------------------------------------------------------------------ utilidades de impresión

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    /** Ejecuta una acción que debe fallar y muestra el mensaje de la excepción. */
    private static void intentar(String descripcion, Runnable accion) {
        try {
            accion.run();
            System.out.println("  [INESPERADO] " + descripcion + ": no lanzó excepción");
        } catch (RuntimeException e) {
            System.out.println("  " + descripcion + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static String codigos(List<Paquete> paquetes) {
        List<String> codigos = new ArrayList<>();
        for (Paquete p : paquetes) {
            codigos.add(p.getCodigo());
        }
        return String.join(", ", codigos);
    }

    private static String detalle(List<Paquete> paquetes, String criterio) {
        List<String> partes = new ArrayList<>();
        for (Paquete p : paquetes) {
            String valor = switch (criterio) {
                case "prioridad" -> String.valueOf(p.getPrioridad());
                case "peso" -> String.valueOf(p.getPeso());
                default -> String.valueOf(p.getTiempoEstimado());
            };
            partes.add(p.getCodigo() + "(" + valor + ")");
        }
        return String.join(", ", partes);
    }

    private static String texto(ListaSimple<String> lista) {
        if (lista.estaVacia()) {
            return "[]";
        }
        List<String> elementos = new ArrayList<>();
        for (String s : lista) {
            elementos.add(s);
        }
        return "[" + String.join(", ", elementos) + "]";
    }
}