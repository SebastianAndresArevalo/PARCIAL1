# QuindíoExpress — Gestión de operaciones de un centro de distribución

Parcial 1 · Estructuras de Datos · 2026-2

Aplicación de consola en Java que organiza la información de una jornada de trabajo de **QuindíoExpress**, una empresa que recibe, clasifica y distribuye paquetes hacia municipios del Quindío. El sistema registra paquetes y repartidores, permite consultarlos de forma eficiente, los atiende por orden de llegada o por prioridad, los asigna a repartidores y conserva un historial de entregas en una **lista simplemente enlazada propia**.

El proyecto sigue la idea central del enunciado: **ninguna estructura de datos sirve para todo**. Cada necesidad (orden, búsqueda rápida, unicidad, prioridad, historial) usa la estructura cuyo costo temporal y espacial mejor se ajusta a la operación dominante.

---

## Tabla de contenido

1. [Estructura del repositorio](#1-estructura-del-repositorio)
2. [Requisitos y ejecución](#2-requisitos-y-ejecución)
3. [Arquitectura general](#3-arquitectura-general)
4. [Descripción de las clases](#4-descripción-de-las-clases)
5. [Estructuras de datos usadas y por qué](#5-estructuras-de-datos-usadas-y-por-qué)
6. [Algoritmos implementados](#6-algoritmos-implementados)
7. [Reglas de negocio y dónde se cumplen](#7-reglas-de-negocio-y-dónde-se-cumplen)
8. [Complejidad de las operaciones](#8-complejidad-de-las-operaciones)
9. [Datos de prueba](#9-datos-de-prueba)
10. [Qué demuestra cada sección del Main](#10-qué-demuestra-cada-sección-del-main)
11. [Decisiones de diseño y limitaciones conocidas](#11-decisiones-de-diseño-y-limitaciones-conocidas)
12. [Documentación complementaria](#12-documentación-complementaria)
13. [Uso de inteligencia artificial](#13-uso-de-inteligencia-artificial)
14. [Equipo y aportes](#14-equipo-y-aportes)

---

## 1. Estructura del repositorio

```
PARCIAL1/
├── README.md                  ← este archivo
├── .gitignore
└── src/
    ├── Main.java              ← demostración ejecutable de todas las operaciones
    ├── model/
    │   ├── Paquete.java       ← entidad Paquete (Comparable + Comparators)
    │   ├── Repartidor.java    ← entidad Repartidor
    │   └── Empresa.java       ← lógica del sistema: colecciones y algoritmos
    └── tools/
        ├── Coleccion.java     ← interfaz genérica (contrato de la colección propia)
        └── ListaSimple.java   ← lista simplemente enlazada propia, genérica e Iterable
```

| Paquete | Responsabilidad |
|---|---|
| *(raíz)* `Main` | Carga los datos iniciales y ejecuta 11 secciones de demostración. |
| `model` | Clases del dominio y la lógica del sistema (`Paquete`, `Repartidor`, `Empresa`). |
| `tools` | Estructura de datos propia, reutilizable y sin dependencia del dominio (`Coleccion<T>`, `ListaSimple<T>`). |

La separación es intencional: `tools` no conoce a `Paquete`; la lista propia sirve para cualquier tipo de dato, como exige el enunciado.

---

## 2. Requisitos y ejecución

### Requisitos

- **JDK 17 o superior.** El código usa `String.isBlank()`, `List.of(...)` y *switch expressions*. Fue desarrollado y ejecutado con OpenJDK 25 en IntelliJ IDEA.
- No usa librerías externas, archivos ni bases de datos: todos los datos se definen directamente en Java.

### Ejecución desde IntelliJ IDEA

1. Abrir la carpeta del proyecto (`File → Open`).
2. Verificar que `src` esté marcada como *Sources Root*.
3. Ejecutar la clase `Main` (`Shift + F10`).

### Ejecución desde la terminal

Desde la raíz del repositorio:

**Linux / macOS**

```bash
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -d out @sources.txt
java -Dfile.encoding=UTF-8 -cp out Main
```

**Windows (PowerShell)**

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -Dfile.encoding=UTF-8 -cp out Main
```

> `Main` está en el paquete por defecto, por eso se invoca como `Main` y no con un nombre calificado. El indicador `-Dfile.encoding=UTF-8` evita que las tildes (Calarcá, Ramírez) se vean mal en consola.

La ejecución termina con código `0` y no pide datos al usuario: es una demostración automática.

---

## 3. Arquitectura general

```
                         ┌───────────────┐
                         │     Main      │  demostración
                         └───────┬───────┘
                                 │ usa
                         ┌───────▼───────┐
                         │    Empresa    │  lógica + colecciones de Java
                         └─┬──────┬────┬─┘
                           │      │    │
              ┌────────────▼┐  ┌──▼────────┐  ┌▼──────────────────┐
              │   Paquete   │  │Repartidor │  │ ListaSimple<T>    │
              │ Comparable  │  │           │  │ (historial)       │
              │ Comparators │  │           │  │ implements        │
              └─────────────┘  └───────────┘  │ Coleccion<T>,     │
                                              │ Iterable<T>       │
                                              └───────────────────┘
```

`Empresa` es la clase central: mantiene todas las estructuras de datos sincronizadas y ofrece las operaciones del sistema. Todas las validaciones de datos de un paquete o repartidor viven en sus constructores, de modo que **un objeto inválido nunca llega a existir**.

---

## 4. Descripción de las clases

### `model.Paquete`

Atributos inmutables (`final`): `codigo`, `destino`, `peso` (kg), `prioridad` (1–5) y `tiempoEstimado` (minutos).

- **Validaciones en el constructor** (lanzan `IllegalArgumentException`): código y destino no nulos ni vacíos, peso > 0, prioridad entre 1 y 5, tiempo estimado > 0.
- **Orden natural (`Comparable<Paquete>`)**: `compareTo` compara por `codigo` ascendente.
- **Criterios alternativos (`Comparator<Paquete>`)** como constantes estáticas reutilizables:

| Constante | Criterio |
|---|---|
| `POR_PRIORIDAD_DESC` | Prioridad, de mayor a menor. |
| `POR_PESO_DESC` | Peso, de mayor a menor. |
| `POR_TIEMPO_ASC` | Tiempo estimado, de menor a mayor. |
| `ORDEN_DESPACHO` | Regla de despacho: mayor prioridad → menor tiempo estimado → código alfabético. Encadena los anteriores con `thenComparing`. |

Los comparadores son independientes del orden natural: ordenar una copia por peso no cambia que el orden natural del objeto siga siendo el código. Además, `ORDEN_DESPACHO` se reutiliza como comparador de la `PriorityQueue`.

### `model.Repartidor`

Atributos: `identificacion`, `nombre`, `zona` (un municipio) y `disponible` (único campo mutable, con `setDisponible`). El constructor valida que identificación, nombre y zona no sean nulos ni vacíos. Incluye `toString()` para imprimir el repartidor completo.

### `model.Empresa`

Orquesta todo el sistema. Sus operaciones públicas se agrupan así:

| Grupo | Métodos |
|---|---|
| Validación de unicidad | `existePaquete`, `existeRepartidor` |
| Paquetes | `registrarPaquete`, `consultarPaquete`, `obtenerPendientes`, `cantidadEnEspera` |
| Municipios | `obtenerMunicipios`, `obtenerMunicipiosOrdenados`, `consultarPaquetesPorMunicipio`, `obtenerPaquetesPorMunicipio` |
| Atención | `atenderPorOrdenLlegada`, `despacharPorPrioridad` |
| Entregas | `registrarEntrega`, `obtenerEntrega`, `cantidadEntregas`, `historialEntregas` |
| Repartidores | `registrarRepartidor`, `consultarRepartidor`, `obtenerRepartidores` |
| Asignación | `asignarPaquete`, `consultarRepartidorAsignado`, `obtenerAsignaciones` |
| Recursividad | `pesoTotalPorDestino`, `contarPrioridadMinima` |
| Búsqueda binaria | `obtenerPaquetesOrdenadosPorCodigo`, `buscarPaqueteBinaria` |
| Divide y vencerás | `paqueteMayorPeso` |

Las colecciones devueltas se envuelven con `Collections.unmodifiable...` para que el código externo no pueda alterar el estado interno de la empresa.

### `tools.Coleccion<T>`

Interfaz genérica que define el contrato común de las estructuras internas:

```java
void agregar(T elemento);
boolean eliminar(T elemento);
T obtener(int posicion);
int tamano();
boolean estaVacia();
```

La interfaz dice **qué** operaciones existen; `ListaSimple` decide **cómo** se implementan.

### `tools.ListaSimple<T>`

Lista simplemente enlazada propia que implementa `Coleccion<T>` e `Iterable<T>`.

- **Nodo interno** (`private static class Nodo<T>`): guarda el `valor` y la referencia al `siguiente`. Es privado: el código externo nunca ve los nodos.
- **Referencias `primero` y `ultimo`**: `agregar` es O(1) porque no recorre la lista.
- **`eliminar(T)`** cubre los casos: lista vacía, único elemento, primero, intermedio, último e inexistente (devuelve `false`). Al eliminar el último actualiza `ultimo`.
- **`obtener(int)`** lanza `IndexOutOfBoundsException` si la posición es inválida.
- **Recorrido**: `iterator()` devuelve una clase interna `IteradorListaSimple` (`hasNext`, `next`, con `NoSuchElementException` al agotarse). Gracias a `Iterable`, el historial se recorre con `for-each` sin conocer cómo están conectados los nodos.
- **No usa ninguna colección de Java** (ni `ArrayList`, `LinkedList`, `Vector`, `Stack`, `Queue` o `Deque`) para almacenar elementos.

---

## 5. Estructuras de datos usadas y por qué

| Necesidad | Campo en `Empresa` | Estructura | Operación dominante | Costo |
|---|---|---|---|---|
| Paquetes pendientes en orden de registro | `pendientes` | `ArrayList<Paquete>` | `add` al final y recorrido | O(1) amortizado / O(n) |
| Consultar por código y validar unicidad | `paquetesPorCodigo` | `HashMap<String, Paquete>` | `get` / `containsKey` | O(1) promedio |
| Municipios sin repetir | `municipios` | `LinkedHashSet<String>` | `add` | O(1) promedio |
| Municipios ordenados sin repetir | `municipiosOrdenados` | `TreeSet<String>` (como `SortedSet`) | `add` e iteración ordenada | O(log m) / O(m) |
| Paquetes por municipio | `paquetesPorMunicipio` | `LinkedHashMap<String, List<Paquete>>` | `computeIfAbsent(...).add` | O(1) promedio |
| Atención por orden de llegada | `colaLlegada` | `ArrayDeque<Paquete>` como `Queue` | `offer` / `poll` | O(1) |
| Despacho por prioridad | `colaPrioridad` | `PriorityQueue<Paquete>` con `ORDEN_DESPACHO` | `offer` / `poll` | O(log n) |
| Historial de entregas | `historial` | `ListaSimple<Paquete>` **propia** | `agregar` al final y recorrer | O(1) / O(n) |
| Repartidores por identificación | `repartidoresPorId` | `LinkedHashMap<String, Repartidor>` | `get` / `containsKey` | O(1) promedio |
| Asignaciones vigentes | `asignaciones` | `LinkedHashMap<String, Repartidor>` | `put` / `get` | O(1) promedio |

**Por qué varias estructuras para los mismos paquetes.** Cada una responde a una pregunta distinta con un costo distinto. Buscar por código en un `ArrayList` obligaría a recorrer hasta O(n) elementos; el `HashMap` va directo. La contrapartida es memoria extra (con 500.000 paquetes, del orden de 20 MB adicionales frente a unos 2 MB del arreglo de referencias), que se acepta porque la búsqueda es la operación más frecuente. El análisis completo de A.1 y A.2 está en el documento de diseño (ver [sección 12](#12-documentación-complementaria)).

---

## 6. Algoritmos implementados

### 6.1 Recursividad (sin ciclos `for`, `while` ni `do-while`)

Ambos métodos recorren la lista de pendientes con un índice que avanza hasta el caso base.

**`pesoTotalPorDestino(String destino)`** — suma el peso de los paquetes de un municipio.

- **Caso base:** `indice == paquetes.size()` → retorna `0`.
- **Caso recursivo:** `pesoActual + pesoTotalPorDestinoRec(paquetes, destino, indice + 1)`, donde `pesoActual` es el peso del paquete si su destino coincide, o `0` si no.
- **Avance hacia el caso base:** `indice + 1` en cada llamada.
- **Ejemplo del enunciado:** Armenia con 4,5 + 7,2 + 2,3 = **14,0 kg**.

**`contarPrioridadMinima(int prioridadMinima)`** — cuenta los paquetes con prioridad mayor o igual a un valor.

- **Caso base:** `indice == paquetes.size()` → retorna `0`.
- **Caso recursivo:** `(1 si prioridad >= mínima, 0 si no) + contarPrioridadMinimaRec(..., indice + 1)`.
- **Ejemplo del enunciado:** prioridades `5, 2, 4, 1, 5, 3, 4` con mínimo 4 → **4 paquetes**.

### 6.2 Búsqueda binaria recursiva

`buscarPaqueteBinaria(List<Paquete> ordenados, String codigo)` localiza un paquete en una lista **ordenada por código**.

- **Precondición:** la lista debe estar ordenada ascendentemente por código (el método no lo verifica). Por eso `obtenerPaquetesOrdenadosPorCodigo()` crea una copia ordenada con `Collections.sort` y sobre ella se busca.
- **Casos base:** `inicio > fin` (no está, retorna `-1`) o el código del elemento medio coincide (retorna su posición).
- **Caso recursivo:** si el código buscado es menor que el del medio, se busca en `[inicio, medio-1]`; si es mayor, en `[medio+1, fin]`. Cada llamada descarta la mitad del rango.
- **Cálculo del medio:** `inicio + (fin - inicio) / 2`, que evita el desbordamiento de enteros.

### 6.3 Divide y vencerás: paquete de mayor peso

`paqueteMayorPeso()` retorna el paquete más pesado (o `null` si no hay pendientes). Las etapas están marcadas con comentarios en el código:

| Etapa | Qué ocurre |
|---|---|
| **Caso base** | Un solo paquete (`inicio == fin`): él mismo es el mayor de su rango. |
| **División** | Se parte `[inicio, fin]` en dos mitades usando `medio`. |
| **Subproblema izquierdo** | Mayor del rango `[inicio, medio]`. |
| **Subproblema derecho** | Mayor del rango `[medio+1, fin]`. |
| **Combinación** | Se queda con el de mayor peso entre los dos resultados parciales; en empate gana el de la izquierda (el registrado primero). |
| **Resultado final** | La combinación del rango completo `[0, n-1]`. |

Con los pesos del enunciado `4.5, 12.0, 3.2, 18.7, 7.4, 5.1`, el resultado es **18,7 kg**.

### 6.4 Atención y despacho

- **Por orden de llegada:** `atenderPorOrdenLlegada()` hace `poll()` sobre la cola FIFO (`colaLlegada`).
- **Por prioridad:** `despacharPorPrioridad()` hace `poll()` sobre la `PriorityQueue` configurada con `ORDEN_DESPACHO`. Con el ejemplo del enunciado (PQ420, PQ731, PQ105, PQ942) el orden obtenido es **PQ105, PQ731, PQ942, PQ420**.

Atender un paquete lo retira de ambas colas (para que no se atienda dos veces por modalidades distintas), pero **sigue en `pendientes` hasta que se registre su entrega**.

### 6.5 Asignación a repartidores

`asignarPaquete(codigoPaquete, identificacionRepartidor)` valida, en este orden:

1. Que el paquete exista.
2. Que siga pendiente (no entregado).
3. Que no esté ya asignado.
4. Que el repartidor exista.
5. Que el repartidor esté **disponible**.
6. Que la **zona del repartidor coincida con el destino** del paquete.

Si todo es válido, registra la asignación y marca al repartidor como no disponible. `registrarEntrega` lo vuelve a dejar disponible.

---

## 7. Reglas de negocio y dónde se cumplen

| Regla del enunciado | Dónde se garantiza |
|---|---|
| No hay dos paquetes con el mismo código | `Empresa.registrarPaquete` (`containsKey` sobre `paquetesPorCodigo`) |
| No hay dos repartidores con la misma identificación | `Empresa.registrarRepartidor` (`containsKey` sobre `repartidoresPorId`) |
| Peso mayor que cero | Constructor de `Paquete` |
| Prioridad entre 1 y 5 | Constructor de `Paquete` |
| Tiempo estimado mayor que cero | Constructor de `Paquete` |
| Destino no nulo ni vacío | Constructor de `Paquete` |
| Repartidor no disponible no recibe asignaciones | `Empresa.asignarPaquete` |
| Zona del repartidor = destino del paquete | `Empresa.asignarPaquete` |
| Atención por llegada respeta el orden original | `colaLlegada` (FIFO) |
| Despacho: prioridad → tiempo → código | `Paquete.ORDEN_DESPACHO` + `PriorityQueue` |
| El historial conserva el orden real de entregas | `ListaSimple.agregar` (siempre al final) en `registrarEntrega` |
| El historial usa la lista propia | Campo `historial` de tipo `ListaSimple<Paquete>` |
| La colección propia es genérica | `ListaSimple<T>` y `Coleccion<T>` |
| La colección propia es `Iterable`/`Iterator` | `ListaSimple.iterator()` + clase interna `IteradorListaSimple` |
| Recursividad solicitada sin ciclos | `pesoTotalPorDestinoRec`, `contarPrioridadMinimaRec`, `busquedaBinariaRec` |
| Divide y vencerás con etapas visibles | `mayorPesoDivideYVencerasRec` |

---

## 8. Complejidad de las operaciones

`n` = paquetes, `m` = municipios, `k` = paquetes de un municipio.

| Operación | Tiempo | Espacio extra |
|---|---|---|
| `registrarPaquete` | O(log n) (dominado por la `PriorityQueue`) | O(1) por paquete en cada estructura |
| `consultarPaquete` / `existePaquete` | O(1) promedio | O(1) |
| `consultarPaquetesPorMunicipio` | O(1) para llegar a la lista, O(k) para recorrerla | O(1) |
| `obtenerMunicipiosOrdenados` | O(1) (el `TreeSet` ya está ordenado); recorrerlo O(m) | O(1) |
| `atenderPorOrdenLlegada` | O(n) (el `poll` es O(1) pero retirar de la cola de prioridad es `remove(Object)`) | O(1) |
| `despacharPorPrioridad` | O(n) (el `poll` es O(log n) pero retirar de la cola de llegada es `remove(Object)`) | O(1) |
| `registrarEntrega` | O(n) (`remove`/`contains` sobre lista y colas); `agregar` al historial O(1) | O(1) |
| `asignarPaquete` | O(n) (`pendientes.contains`) | O(1) |
| `ListaSimple.agregar` | O(1) | O(1) |
| `ListaSimple.obtener` / `eliminar` | O(n) | O(1) |
| `ListaSimple.tamano` / `estaVacia` | O(1) | O(1) |
| Recorrido con `Iterator` | O(n) en total, O(1) por `next` | O(1) |
| `pesoTotalPorDestino` / `contarPrioridadMinima` | O(n) | O(n) por la pila de llamadas |
| `obtenerPaquetesOrdenadosPorCodigo` | O(n log n) | O(n) por la copia |
| `buscarPaqueteBinaria` | O(log n) | O(log n) por la pila de llamadas |
| `paqueteMayorPeso` | O(n) | O(log n) por la pila de llamadas |

---

## 9. Datos de prueba

Definidos directamente en `Main` (sin archivos ni bases de datos), cumpliendo los mínimos del enunciado:

| Requisito | Cumplimiento |
|---|---|
| Al menos 30 paquetes | 30 paquetes |
| Al menos 5 destinos diferentes | 6 destinos: Armenia, Salento, Calarcá, Montenegro, Quimbaya, Circasia |
| Al menos 6 repartidores | 6 repartidores (`R001`–`R006`), uno de ellos no disponible (`R005`, Quimbaya) |
| Diferentes prioridades y pesos | Prioridades de 1 a 5; pesos de 0,5 a 18,7 kg |
| Códigos no consecutivos | `PQ731`, `PQ105`, `PQ942`, `PQ318`, `PQ061`, `PQ977`… |
| Varios paquetes al mismo municipio | Armenia concentra 8; Salento y Calarcá, 5 cada uno |

Además, `Main` crea pequeñas instancias de `Empresa` para reproducir **los ejemplos exactos del enunciado** (peso de Armenia = 14,0 kg; prioridad ≥ 4 = 4 paquetes; orden de llegada y orden de despacho).

---

## 10. Qué demuestra cada sección del Main

| # | Sección | Qué se demuestra |
|---|---|---|
| 1 | Carga de datos iniciales | 30 paquetes y 6 repartidores cargados. |
| 2 | Consulta por código y validaciones | Existencia, consulta, y rechazo de código repetido, repartidor repetido, peso 0, prioridad 6, tiempo 0 y destino vacío. El estado no cambia tras los fallos. |
| 3 | Municipios | Sin repetidos (`LinkedHashSet`), ordenados (`TreeSet`) y paquetes agrupados por municipio (`Map`). |
| 4 | Comparable y Comparator | Orden de registro, natural por código, por prioridad, por peso y por tiempo; el orden natural no se altera. |
| 5 | Recursividad | Peso total por destino y conteo por prioridad mínima, con los ejemplos del enunciado. |
| 6 | Búsqueda binaria | Primer elemento, último, uno intermedio y uno inexistente. |
| 7 | Divide y vencerás | Paquete de mayor peso (PQ584, 18,7 kg) y caso sin paquetes. |
| 8 | Atención y despacho | Ejemplos del enunciado y comportamiento con los 30 paquetes reales. |
| 9 | Asignación | Asignaciones válidas y rechazos por repartidor ocupado, no disponible, zona distinta, paquete ya asignado, paquete o repartidor inexistente. |
| 10 | Entregas e historial | Historial `PQ105 -> PQ731 -> PQ977` recorrido con `for-each`, liberación del repartidor y rechazo de entregas inválidas. |
| 11 | Lista propia: casos borde | Vacía, un elemento, eliminar primero/intermedio/último/inexistente, agregar tras eliminar el último y posición inválida. |

Fragmento de la salida esperada:

```
=== 5. Recursividad ===
Peso total de Armenia:   55,1 kg
Peso total de Salento:   25,4 kg
Paquetes con prioridad >= 4: 13
Ejemplo del enunciado, peso de Armenia: 14,0 kg (esperado 14.0)
Ejemplo del enunciado, prioridad >= 4: 4 (esperado 4)

=== 10. Entregas e historial (lista simplemente enlazada propia) ===
Historial recorrido con for-each (Iterable/Iterator): PQ105 -> PQ731 -> PQ977
```

> Los decimales pueden mostrarse con coma o con punto según la configuración regional del equipo.

---

## 11. Decisiones de diseño y limitaciones conocidas

**Decisiones**

- **Una estructura por necesidad.** Los mismos paquetes se guardan en varias estructuras sincronizadas porque cada una optimiza una operación distinta. El costo es memoria extra y la obligación de mantenerlas coherentes en `registrarPaquete`, `atender...`, `despachar...` y `registrarEntrega`.
- **"Atendido" no es "entregado".** Atender o despachar un paquete lo saca de las colas de atención, pero sigue pendiente hasta `registrarEntrega`. Solo entonces pasa al historial y libera al repartidor.
- **Validar en el constructor.** `Paquete` y `Repartidor` rechazan datos inválidos al crearse, por lo que `Empresa` trabaja siempre con objetos correctos.
- **Colecciones de solo lectura hacia afuera.** Los getters devuelven vistas no modificables.
- **Lista propia independiente del dominio.** `ListaSimple<T>` vive en `tools` y no conoce a `Paquete`.

**Limitaciones conocidas** (se documentan para ser transparentes con el alcance)

- **Retirar de las colas es O(n).** `remove(Object)` sobre `ArrayList`, `ArrayDeque` y `PriorityQueue` recorre la estructura. Con 30 paquetes es irrelevante; con 500.000 convendría la *eliminación diferida* (marcar como atendido y descartar al hacer `poll`).
- **`Paquete` no redefine `equals`/`hashCode`.** `remove` y `contains` comparan por referencia; funciona porque siempre se usa la misma instancia registrada.
- **Mayúsculas en los destinos.** Los `Set` y `Map` distinguen mayúsculas ("Armenia" ≠ "armenia"), mientras que `pesoTotalPorDestino` y `asignarPaquete` usan `equalsIgnoreCase`. Los datos del programa usan siempre la misma escritura.
- **Orden alfabético.** El `TreeSet` usa el orden natural de `String` (por código Unicode), que distingue mayúsculas y tildes. Con los municipios usados el resultado es el alfabético esperado.
- **Búsqueda binaria sin verificación.** El método asume la precondición de lista ordenada; no la comprueba.
- **Profundidad de recursión.** Las recursiones lineales consumen O(n) de pila; con cientos de miles de paquetes podrían producir `StackOverflowError`. La búsqueda binaria y el divide y vencerás solo usan O(log n).
- **Asignaciones tras la entrega.** Al entregar, el repartidor se libera pero la entrada permanece en `asignaciones` como registro.

---

## 12. Documentación complementaria

El documento PDF de entrega acompaña a este repositorio y contiene:

| Parte | Contenido |
|---|---|
| **A. Análisis y diseño** | A.1 selección de colecciones (tabla con estructura, operación dominante, complejidad, alternativa descartada y justificación) y A.2 comparación tiempo frente a memoria (`ArrayList` vs `HashMap`, y otras alternativas). |
| **C. Análisis algorítmico y pruebas** | Pruebas de escritorio, diagramas de pila de llamadas, análisis de complejidad temporal y espacial y respuestas argumentadas. |
| **D. Uso de IA** | Bitácora de consultas y caso de respuesta cuestionable. |

---

## 13. Uso de inteligencia artificial

El enunciado permite usar herramientas de IA siempre que su uso se registre y se justifique. La **bitácora completa** (herramienta, qué se preguntó, por qué, cómo se verificó y qué se modificó) y el **caso de respuesta cuestionable** están en la Parte D del documento PDF. Todo el código entregado puede ser explicado, ejecutado y modificado por cualquier integrante del equipo.

---

## 14. Equipo y aportes

> **Completar antes de entregar.** El enunciado exige que se vean aportes de todos los integrantes en el historial de Git.

| Integrante | Aportes principales |
|---|---|---|
| *[Kevin Rivera]*      | *[p. ej. `Paquete`, `Repartidor`]* |
| *[Sebastian Arevalo]* | *[p. ej. `ListaSimple`, `Coleccion`, main]* |

**Curso:** Estructuras de Datos · **Programa:** Ingeniería de Sistemas y Computación · **Periodo:** 2026-2
