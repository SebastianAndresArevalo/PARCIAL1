import model.Empresa;
import model.Paquete;
import model.Repartidor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        Empresa empresa = new Empresa();
        cargarRepartidores(empresa);
        cargarPaquetes(empresa);

        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            try {
                opcion = leerEntero("Opción: ");
                ejecutar(opcion, empresa);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número.");
            } catch (RuntimeException e) {
                System.out.println("No se pudo: " + e.getMessage());
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== QUINDÍOEXPRESS =====");
        System.out.println(" 1. Registrar paquete");
        System.out.println(" 2. Consultar paquete");
        System.out.println(" 3. Listar paquetes pendientes");
        System.out.println(" 4. Listar paquetes ordenados");
        System.out.println(" 5. Registrar repartidor");
        System.out.println(" 6. Consultar repartidor");
        System.out.println(" 7. Listar repartidores");
        System.out.println(" 8. Atender siguiente (orden de llegada)");
        System.out.println(" 9. Despachar siguiente (por prioridad)");
        System.out.println("10. Asignar paquete a repartidor");
        System.out.println("11. Registrar entrega");
        System.out.println("12. Ver historial de entregas");
        System.out.println("13. Municipios y sus paquetes");
        System.out.println("14. Peso total de un municipio (recursividad)");
        System.out.println("15. Paquetes con prioridad mínima (recursividad)");
        System.out.println("16. Paquete de mayor peso (divide y vencerás)");
        System.out.println("17. Buscar paquete por código (búsqueda binaria)");
        System.out.println(" 0. Salir");
    }

    private static void ejecutar(int opcion, Empresa empresa) {
        switch (opcion) {
            case 1:
                Paquete nuevo = new Paquete(leer("Código: ").toUpperCase(), leer("Destino: "),
                        leerDecimal("Peso (kg): "), leerEntero("Prioridad (1-5): "), leerEntero("Tiempo (min): "));
                empresa.registrarPaquete(nuevo);
                System.out.println("Registrado: " + nuevo);
                break;
            case 2:
                Paquete consultado = empresa.consultarPaquete(leer("Código: ").toUpperCase());
                System.out.println(consultado == null ? "No existe ese paquete." : consultado);
                break;
            case 3:
                System.out.println("Pendientes: " + empresa.obtenerPendientes().size());
                for (Paquete pendiente : empresa.obtenerPendientes()) {
                    System.out.println("  " + pendiente);
                }
                break;
            case 4:
                listarOrdenados(empresa);
                break;
            case 5:
                empresa.registrarRepartidor(new Repartidor(leer("Identificación: ").toUpperCase(), leer("Nombre: "),
                        leer("Zona: "), leer("¿Disponible? (s/n): ").equalsIgnoreCase("s")));
                System.out.println("Repartidor registrado.");
                break;
            case 6:
                Repartidor repartidor = empresa.consultarRepartidor(leer("Identificación: ").toUpperCase());
                System.out.println(repartidor == null ? "No existe ese repartidor." : repartidor);
                break;
            case 7:
                for (Repartidor r : empresa.obtenerRepartidores()) {
                    System.out.println("  " + r);
                }
                break;
            case 8:
                Paquete atendido = empresa.atenderPorOrdenLlegada();
                System.out.println(atendido == null ? "No hay paquetes en espera." : "Atendido: " + atendido);
                break;
            case 9:
                Paquete despachado = empresa.despacharPorPrioridad();
                System.out.println(despachado == null ? "No hay paquetes en espera." : "Despachado: " + despachado);
                break;
            case 10:
                empresa.asignarPaquete(leer("Código del paquete: ").toUpperCase(), leer("Id del repartidor: ").toUpperCase());
                System.out.println("Paquete asignado.");
                break;
            case 11:
                empresa.registrarEntrega(leer("Código entregado: ").toUpperCase());
                System.out.println("Entrega registrada.");
                break;
            case 12:
                System.out.println("Entregas: " + empresa.cantidadEntregas());
                for (Paquete entregado : empresa.historialEntregas()) {
                    System.out.println("  " + entregado);
                }
                break;
            case 13:
                verMunicipios(empresa);
                break;
            case 14:
                // Math.round solo redondea el resultado a 2 decimales para imprimirlo.
                System.out.println("Peso total: "
                        + Math.round(empresa.pesoTotalPorDestino(leer("Municipio: ")) * 100) / 100.0 + " kg");
                break;
            case 15:
                System.out.println("Cantidad: " + empresa.contarPrioridadMinima(leerEntero("Prioridad mínima: ")));
                break;
            case 16:
                Paquete mayor = empresa.paqueteMayorPeso();
                System.out.println(mayor == null ? "No hay paquetes pendientes." : mayor);
                break;
            case 17:
                Paquete buscado = empresa.buscarPaqueteBinaria(empresa.obtenerPaquetesOrdenadosPorCodigo(),
                        leer("Código: ").toUpperCase());
                System.out.println(buscado == null ? "No se encontró." : buscado);
                break;
            case 0:
                System.out.println("Hasta luego.");
                break;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void listarOrdenados(Empresa empresa) {
        int criterio = leerEntero("1.Código 2.Prioridad 3.Peso 4.Tiempo 5.Despacho: ");
        List<Paquete> copia = new ArrayList<>(empresa.obtenerPendientes());
        if (criterio == 1) {
            Collections.sort(copia);
        } else if (criterio == 2) {
            Collections.sort(copia, Paquete.POR_PRIORIDAD_DESC);
        } else if (criterio == 3) {
            Collections.sort(copia, Paquete.POR_PESO_DESC);
        } else if (criterio == 4) {
            Collections.sort(copia, Paquete.POR_TIEMPO_ASC);
        } else if (criterio == 5) {
            Collections.sort(copia, Paquete.ORDEN_DESPACHO);
        } else {
            System.out.println("Criterio no válido.");
            return;
        }
        for (Paquete paquete : copia) {
            System.out.println("  " + paquete);
        }
    }

    private static void verMunicipios(Empresa empresa) {
        for (String municipio : empresa.obtenerMunicipiosOrdenados()) {
            String codigos = "";
            for (Paquete paquete : empresa.consultarPaquetesPorMunicipio(municipio)) {
                codigos += paquete.getCodigo() + " ";
            }
            System.out.println(municipio + ": " + codigos);
        }
    }

    private static String leer(String mensaje) {
        System.out.print(mensaje);
        if (!entrada.hasNextLine()) {
            System.exit(0);
        }
        return entrada.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        return Integer.parseInt(leer(mensaje));
    }

    private static double leerDecimal(String mensaje) {
        return Double.parseDouble(leer(mensaje).replace(',', '.'));
    }

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
}