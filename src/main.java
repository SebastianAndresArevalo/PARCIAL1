import model.Empresa;
import model.Paquete;
import model.Repartidor;

public class main {

    public static void main(String[] args) {
        Empresa empresa = new Empresa();

        cargarRepartidores(empresa);
        cargarPaquetes(empresa);

        System.out.println("Datos iniciales cargados.");
        Paquete ejemplo = empresa.consultarPaquete("PQ731");
        System.out.println("Consulta PQ731 -> destino: " + ejemplo.getDestino()
                + ", peso: " + ejemplo.getPeso()
                + ", prioridad: " + ejemplo.getPrioridad()
                + ", tiempo estimado: " + ejemplo.getTiempoEstimado());
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