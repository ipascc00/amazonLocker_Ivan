# Patrones de diseño — Amazon Locker

Programación II · Grado en Ingeniería Informática

Cada semana se aplica un patrón de diseño a la temática de los
[Amazon Locker](https://en.wikipedia.org/wiki/Amazon_Locker). Cada patrón tiene
su documento (problema, UML, roles, decisiones y diagrama de secuencia) y su
paquete de código con tests unitarios.

| Tema | Patrón | Documento | Paquete |
|------|--------|-----------|---------|
| 4 | Composite | [docs/composite.md](docs/composite.md) | `amazonlocker.composite` |
| 5 | Iterator  | [docs/iterator.md](docs/iterator.md)   | `amazonlocker.iterator` |
| 7 | Builder   | [docs/builder.md](docs/builder.md)     | `amazonlocker.builder` |
| 8 | Observer  | [docs/observer.md](docs/observer.md)   | `amazonlocker.observer` |

## Cómo encajan los patrones

```
DirectorLocker + LockerBuilder  ──construye──▶  árbol Composite (Red > Taquilla > Columna > Casillero)
                                                        │
                                  Iterator ◀──recorre───┤   (hijos en VectorAggregate, CasilleroIterator)
                                                        │
                                  Observer ◀──avisa─────┘   (cada elemento es un Subject: luz, pantalla y alerta)
```

## Estructura

```
pom.xml
docs/                      documentación de cada patrón
etc/                       diagramas UML (.drawio) y sus imágenes (etc/images: .png y .svg)
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
