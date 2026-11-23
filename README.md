# Patrones de diseño — Amazon Locker

Programación II · Grado en Ingeniería Informática

Cada semana se aplica un patrón de diseño a la temática de los
[Amazon Locker](https://en.wikipedia.org/wiki/Amazon_Locker): taquillas de
autoservicio donde el cliente recoge su pedido tecleando un código. Cada patrón
tiene su paquete de código con tests unitarios.

| Tema | Patrón | Paquete |
|------|--------|---------|
| 4 | [Composite](#composite) | `amazonlocker.composite` |
| 5 | [Iterator](#iterator)   | `amazonlocker.iterator` |
| 7 | [Builder](#builder)     | `amazonlocker.builder` |
| 8 | [Observer](#observer)   | `amazonlocker.observer` |

```
DirectorLocker + LockerBuilder  ──construye──▶  árbol Composite (Red > Taquilla > Columna > Casillero)
                                                        │
                                  Iterator ◀──recorre───┤
                                                        │
                                  Observer ◀──avisa─────┘
```

Los diagramas de clases están en `etc/` (`.drawio`, editables con
[draw.io](https://app.diagrams.net)) y sus imágenes en `etc/images/` (`.png` y `.svg`).

---

## Composite

### Problema

Una taquilla está formada por **columnas** y cada columna por **casilleros**
(pequeño, mediano, grande). Las taquillas se agrupan en **redes** (ciudad,
región...), y una red puede contener otras redes. El centro de reparto necesita
hacer las mismas preguntas a cualquier nivel: ¿cuántos casilleros libres hay?,
¿hay alguno GRANDE libre?, ¿qué ocupación tiene esta zona?

Sin el patrón, el cliente tendría que distinguir con `instanceof` si trata con
una red, una taquilla, una columna o un casillero. Con *Composite*, casilleros y
agrupaciones comparten la interfaz `ElementoLocker` y los compuestos delegan
recursivamente en sus hijos.

### Diagrama de clases

![Diagrama de clases del patrón Composite](etc/images/composite.png)

### Roles

| Rol | Clase |
|---|---|
| **Component** | `ElementoLocker` (abstracta) |
| **Leaf** | `Casillero` |
| **Composite** | `GrupoLocker` (abstracta), con los compuestos concretos `Columna`, `Taquilla` y `RedLockers` |
| **Client** | `CentroReparto` |

### Decisiones

- **Variante transparente:** `add`, `remove` y `getHijo` se declaran en el
  Component (`ElementoLocker`), así el cliente no necesita hacer *downcasting*.
  Para no perder seguridad, en la hoja lanzan `UnsupportedOperationException`;
  `esCompuesto()` permite consultarlo sin `instanceof`.
- **Referencia al padre:** `getPadre()` y `getRuta()`
  (`Red León > T1 > T1-A > T1-A2`). La gestiona solo `GrupoLocker` al hacer
  `add`/`remove` y evita que un elemento esté en dos grupos a la vez.
- **Restricción de tipos:** cada compuesto indica en `admite()` qué hijos
  acepta (`Columna` → `Casillero`, `Taquilla` → `Columna`, `RedLockers` →
  `Taquilla` o `RedLockers`). También se impiden ciclos.

### Diagrama de secuencia: asignar un paquete

El cliente pide un casillero GRANDE a la red y la petición baja por el árbol
hasta encontrar una hoja libre.

```mermaid
sequenceDiagram
    participant C as CentroReparto
    participant R as RedLockers León
    participant T as Taquilla Campus
    participant A as Columna A
    participant A1 as Casillero A1 (PEQUENO)
    participant A3 as Casillero A3 (GRANDE)

    C->>R: buscarCasilleroLibre(GRANDE)
    R->>T: buscarCasilleroLibre(GRANDE)
    T->>A: buscarCasilleroLibre(GRANDE)
    A->>A1: buscarCasilleroLibre(GRANDE)
    A1-->>A: null
    A->>A3: buscarCasilleroLibre(GRANDE)
    A3-->>A: A3
    A-->>T: A3
    T-->>R: A3
    R-->>C: A3
    C->>A3: depositar("PKG-001")
```

---

## Iterator

### Problema

Hay muchas tareas que recorren el árbol del locker: buscar el casillero que
tiene un código de recogida, listar los casilleros ocupados de una zona, sumar
los libres de cada compuesto, o recorrer una columna al revés. Si el cliente
accede directamente a la lista de hijos queda acoplado a la estructura interna y
tiene que escribir bucles recursivos distintos según la profundidad.

Con *Iterator* cada colección crea su iterador y se recorre siempre con la
misma interfaz (`firstElement`, `nextElement`, `hasMoreElements`,
`currentElement`).

### Diagrama de clases

![Diagrama de clases del patrón Iterator](etc/images/iterator.png)

### Roles

| Rol | Clase |
|---|---|
| **Iterator** | `Iterator<T>` (interfaz) |
| **ConcreteIterator** | `ListIterator<T>`, `ReverseIterator<T>`, `NullIterator<T>` y `CasilleroIterator` |
| **Aggregate** | `Aggregate<T>` (interfaz) |
| **ConcreteAggregate** | `VectorAggregate<T>` (adapta `java.util.Vector`) |
| **Client** | `CentroReparto` y `GrupoLocker` |

### Decisiones

- **Iteradores externos:** el cliente controla el avance con `nextElement()`.
- **Varios recorridos sobre el mismo agregado:** `createIterator()` devuelve un
  `ListIterator` y `createIterator("reverse")` un `ReverseIterator`.
- **NullIterator:** lo devuelve `Casillero` (no tiene hijos), así se puede
  pedir un iterador a cualquier `ElementoLocker` sin preguntar si es hoja.
- **Iterador sobre el Composite:** `CasilleroIterator` recorre en profundidad
  todos los casilleros de un subárbol con una pila de iteradores (uno por
  nivel). Lo usa `CentroReparto.localizarPaquete()`.

### Diagrama de secuencia: localizar un paquete

El cliente teclea su código y se recorren todos los casilleros de la red con el
`CasilleroIterator`, sin conocer la forma del árbol.

```mermaid
sequenceDiagram
    participant C as CentroReparto
    participant R as RedLockers
    participant CI as CasilleroIterator
    participant IR as ListIterator (hijos de la red)
    participant T as Taquilla T1
    participant IT as ListIterator (hijos de T1)

    C->>R: createCasilleroIterator()
    R->>CI: new CasilleroIterator(this)
    CI->>R: createIterator()
    R-->>CI: IR
    CI->>IR: currentElement()
    IR-->>CI: T1 (compuesto)
    CI->>T: createIterator()
    T-->>CI: IT (se apila)
    Note over CI: baja hasta la primera columna<br/>y se queda en su primer casillero
    loop mientras hasMoreElements()
        C->>CI: currentElement()
        CI-->>C: casillero
        C->>C: ¿getPaquete() == código?
        C->>CI: nextElement()
    end
```

---

## Builder

### Problema

Montar el árbol de un locker a mano es largo y repetitivo: una taquilla
estándar tiene 3 columnas y 9 casilleros, y una red puede tener decenas de
taquillas. Además, con la misma configuración Amazon necesita dos cosas
distintas: el **árbol de objetos** para gestionar los paquetes y el **plano de
instalación** para el técnico.

Con *Builder*, el Director conoce los modelos de taquilla y el orden de montaje
(red → taquilla → columna → casillero), y cada ConcreteBuilder decide qué
representación se genera. El mismo proceso crea dos productos distintos.

### Diagrama de clases

![Diagrama de clases del patrón Builder](etc/images/builder.png)

### Roles

| Rol | Clase |
|---|---|
| **Director** | `DirectorLocker` (modelo `MODELO_ESTANDAR`) |
| **Builder** | `LockerBuilder` (abstracta) |
| **ConcreteBuilder** | `ArbolLockerBuilder` y `PlanoLockerBuilder` |
| **Product** | árbol de `ElementoLocker` y `PlanoInstalacion` |

### Decisiones

- **Sin clase abstracta de producto:** los productos (un árbol y un plano de
  texto) no comparten interfaz, así que `getResultado()` está en cada
  ConcreteBuilder.
- **Métodos vacíos por defecto:** `PlanoLockerBuilder` no sobrescribe
  `buildRed()` porque la red no se instala físicamente.
- **Ensamblaje:** cada parte se añade a la última parte contenedora construida
  (el casillero a la última columna...). `ArbolLockerBuilder` lanza
  `IllegalStateException` si el orden no es válido.

### Diagrama de secuencia: construir una taquilla estándar

```mermaid
sequenceDiagram
    participant Cl as Cliente
    participant B as ArbolLockerBuilder
    participant D as DirectorLocker

    Cl->>B: new ArbolLockerBuilder()
    Cl->>D: new DirectorLocker(builder)
    Cl->>D: construirTaquillaEstandar("T1", "Campus")
    D->>B: buildTaquilla("T1", "Campus")
    D->>B: buildColumna("T1-A")
    loop 4 veces
        D->>B: buildCasillero("T1-A1..A4", PEQUENO)
    end
    D->>B: buildColumna("T1-B")
    loop 3 veces
        D->>B: buildCasillero("T1-B1..B3", MEDIANO)
    end
    D->>B: buildColumna("T1-C")
    loop 2 veces
        D->>B: buildCasillero("T1-C1..C2", GRANDE)
    end
    Cl->>B: getResultado()
    B-->>Cl: Taquilla T1 (árbol Composite)
```

---

## Observer

### Problema

Cuando se deposita o se recoge un paquete cambia el estado del casillero y
también el de su columna, taquilla y red. Varios objetos dependen de ese estado:
la **luz de la puerta** (verde/roja), la **pantalla de la taquilla** (casilleros
libres) y la **alerta de logística** (ocupación de una red). Si el casillero los
llamase directamente quedaría acoplado a todos y habría que modificarlo cada vez
que se añade un dispositivo.

Con *Observer*, cada elemento del locker guarda una lista de observadores y les
avisa con `notifyObservers()` cuando cambia.

### Diagrama de clases

![Diagrama de clases del patrón Observer](etc/images/observer.png)

### Roles

| Rol | Clase |
|---|---|
| **Subject** | `Subject` (abstracta) |
| **ConcreteSubject** | `Casillero` (y por propagación `Columna`, `Taquilla`, `RedLockers`) |
| **Observer** | `Observer` (interfaz) |
| **ConcreteObserver** | `IndicadorLuz`, `PantallaTaquilla` y `AlertaOcupacion` |

### Decisiones

- **`ElementoLocker` hereda de `Subject`:** se puede observar cualquier nivel
  del árbol.
- **Propagación hacia arriba:** tras avisar a sus observadores, cada elemento
  avisa a su padre. Por eso la pantalla solo observa la taquilla, no sus 9
  casilleros.
- **Modelo pull:** `update()` no recibe datos; cada observador guarda una
  referencia a su sujeto y le consulta el estado.
- **Quién y cuándo notifica:** el propio sujeto, al final de `depositar()` y
  `retirar()`, cuando su estado ya es coherente. Si la operación falla no se
  notifica.

### Diagrama de secuencia: depositar un paquete

El casillero avisa a su luz y el aviso sube por el árbol hasta la taquilla,
que actualiza su pantalla.

```mermaid
sequenceDiagram
    participant CR as CentroReparto
    participant C as Casillero T1-A1
    participant L as IndicadorLuz
    participant Col as Columna T1-A
    participant T as Taquilla T1
    participant P as PantallaTaquilla

    CR->>C: depositar("PKG-001")
    C->>C: notifyObservers()
    C->>L: update()
    L->>C: estaLibre()
    C-->>L: false (luz ROJA)
    C->>Col: notifyObservers() (padre)
    Col->>T: notifyObservers() (padre)
    T->>P: update()
    P->>T: getCasillerosLibres()
    T-->>P: 8
```

---

## Estructura

```
pom.xml
etc/                       diagramas UML (.drawio) y sus imágenes (etc/images)
src/main/java/amazonlocker/
├── composite/             Composite (+ CasilleroIterator y CentroReparto)
├── iterator/              Iterator, Aggregate, VectorAggregate, ListIterator...
├── builder/               LockerBuilder, Director, ConcreteBuilders, PlanoInstalacion
├── observer/              Subject, Observer y observadores concretos
└── Main.java              demo de todos los patrones juntos
src/test/java/amazonlocker/   tests JUnit 5
```

## Ejecución

```bash
mvn test                                                  # tests unitarios
mvn compile exec:java -Dexec.mainClass=amazonlocker.Main  # demo
```
