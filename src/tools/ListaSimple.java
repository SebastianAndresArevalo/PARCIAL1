package tools;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

// Declaración, el comando <T> le permite ser usada en otras clases
// Iterable permite recorrer con un for-each
public class ListaSimple<T> implements Coleccion<T>, Iterable<T> {

    //Creación de variable que guarda el valor
    //Siguiente empieza en null para que un nodo nuevo nazca sin enlazar
    private static class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    //Punteros para una lista y su tamaño
    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamano;

    public ListaSimple() {
        primero = null;
        ultimo = null;
        tamano = 0;
    }

    //Agrega los datos, lista vacía = primer nodo, cuando tiene datos los agrega a la última posicion
    public void agregar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (estaVacia()) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        tamano++;
    }


    //Si cumple las condiciones del if, el nodo recorre pasos hasta llegar al valor indicado
    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException("Posición inválida: " + posicion);
        }
        Nodo<T> actual = primero;
        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    //Utiliza dos punteros, haciendo que el anterior al elemento, salte al valor siguiente del mencionado, ignorándolo o mejor dicho, eliminándolo
    public boolean eliminar(T elemento) {
        Nodo<T> anterior = null;
        Nodo<T> actual = primero;

        while (actual != null) {
            if (Objects.equals(actual.valor, elemento)) {
                if (anterior == null) {
                    // Se elimina el primero (cubre también la lista de un solo elemento).
                    primero = actual.siguiente;
                } else {
                    // Intermedio o último: el anterior salta al siguiente.
                    anterior.siguiente = actual.siguiente;
                }
                if (actual == ultimo) {
                    // Se eliminó el último: último pasa a ser el anterior (null si quedó vacía).
                    ultimo = anterior;
                }
                tamano--;
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    //Devuelve el tamaño de la lista
    public int tamano() {
        return tamano;
    }

    //Verifica que la lista tenga datos
    public boolean estaVacia() {
        return tamano == 0;
    }

    //Esta parte es la que se encarga de avanzar de uno en uno sin dejar reiniciar el recorrido

    //Crea un iterador nuevo que empieza desde el primero
    @Override
    public Iterator<T> iterator() {
        return new IteradorListaSimple();
    }

    //Avanza el nodo de uno en uno buscando si quedan más elementos
    private class IteradorListaSimple implements Iterator<T> {

        private Nodo<T> actual = primero;

        @Override
        public boolean hasNext() {
            return actual != null;
        }

        @Override
        public T next() {
            if (actual == null) {
                throw new NoSuchElementException("No hay más elementos.");
            }
            T valor = actual.valor;
            actual = actual.siguiente;
            return valor;
        }
    }
}