package tools;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ListaSimple<T> implements Coleccion<T>, Iterable<T> {

    private static class Nodo<T> {
        private final T valor;
        private Nodo<T> siguiente;

        private Nodo(T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamano;

    public ListaSimple() {
        primero = null;
        ultimo = null;
        tamano = 0;
    }

    /**
     * Agrega un elemento al final de la lista. Costo: O(1), gracias a la referencia al último nodo.
     */

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

    /**
     * Devuelve el elemento en la posición indicada (la primera posición es 0). Costo: O(n).
     */
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
                    // Se eliminó el último: ultimo pasa a ser el anterior (null si quedó vacía).
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

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new IteradorListaSimple();
    }

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