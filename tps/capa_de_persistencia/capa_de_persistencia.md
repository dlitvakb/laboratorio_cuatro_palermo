# Capa de Persistencia

## El modelo de Capas

El modelo de capas es un tipo de arquitectura de aplicaciones diseñado para
abstraer los distintos componentes de dicha arquitectura en subsitemas simples
que se dedican a resolver las problematicas especificas a cada área del desarrollo de un sistema.

Hay diferentes esquemas comunes para el desarrollo de aplicaciones, donde entre ellos se destacan dos:

* Modelo de Siete Capas
* Model-View-Controller (mas comunmente llamado MVC)

Otras arquitecturas pueden incluir:

* Model-View-Service-Controller (MVC + Service, es muy similar a MVC, pero separando la lógica de negocios del Modelo)
* Model-View-ViewList-Controller (MVVC, comunmente utilizado en aplicaciones web cliente)
* Model-Operation-View-Event (MOVE, utilizado en programación reactiva)

> Estas arquitecturas son similares a MVC en lo que respecta a la capa de persistencia.

### Modelo de Siete Capas

Esta arquitectura, hasta hace unos 10 años, era la arquitectura mas popular de desarrollo de
aplicaciones.

Una de sus ventajas es la clara separación de responsabilidades entre las capas.
Una de sus mayores desventajas es la complejidad en la interacción entre las mismas.

* Cliente

> Cualquier interacción que se realiza a nivel de cliente para alterar/optimizar sobre
> nuestra aplicación. Por ejemplo, en el caso de una aplicación web,
> el post-procesamiento que realiza un Web Browser.

* Presentación

> Donde se define "estéticamente" nuestra aplicación, el formato en el que se muestra,
> las fuentes que se utilizan, etc. Por ejemplo, el HTML, JavaScript y CSS de una pagina web.

* Vista

> En la vista se define cómo se muestran los datos. Esta capa debe ser lo mas liviana
> y libre de código posible. Se debe enfocar mayormente en resolver la interacción con el usuario.


* Controlador

> A través de esta capa se comunican las distintas capas. Se encarga mayormente de distribuir,
> modificar y ordenar los datos recibidos del cliente para que sea utilizado por la lógica de negocio y
> lo mismo para los datos devueltos por la logica de negocio para ser enviados a la vista.

* Logica de negocios

> En ésta capa se encuentra definida la lógica necesaria para realizar las tareas para la cual nuestro
> producto fue diseñado. Por ejemplo, si nuestro sistema es un sistema de administración hospitalario,
> debo ser capaz en esta capa de definir las interacciones entre médicos y pacientes.
> Esta capa suele estar embebida en el controlador o en el modelo de objetos, o totalmente aislado en una
> capa llamada de servicios.

* Modelo de Objetos

> En esta capa se realiza una abstracción entre los objetos que participan en la lógica de negocios y
> la capa de almacenamiento, es una capa que es utilizada para separar el código específico de persistencia
> y el código que representa el modelo de negocio.

* Almacenamiento Persistente

> En esta capa se define la interacción con el SGBD. Debe ser específico para el tipo de BBDD que utilicemos
> para nuestra aplicación. En caso de utilizar distintos tipos de almacenamiento para los mismos datos, se
> deben crear distintos manejadores que sepan comunicarse con dicho sistema de almacenamiento.

#### Interacciones de la Capa de Persistencia

En este esquema, la capa de persistencia es una capa que está especificamente creada para este proposito.
Debe ser creada para cada tipo de almacenamiento utilizado. En la lógica de negocios, se deben realizar las
interacciones necesarias entre la capa de persistencia y el modelo de objetos.

### Model-View-Controller

Esta es la arquitectura más popular actualmente.

Su mayor ventaja es la simplicidad para el desarrollo y la cantidad amplia de Frameworks existentes para
facilitar el desarrollo utilizando esta arquitectura.
Su mayor desventaja es la falta de claridad sobre la responsabilidad de las capas, es muy debatido donde
debe ser colocada la lógica de negocios, donde una gran cantidad de gente opta por tener Modelos "gordos"
y Controladores "flacos", mientras que hay una cantidad de gente que hace lo opuesto.

* Vista

> En esta capa se modela la interacción del usuario con lo que finalmente ve.
> Es la capa donde el usuario se comunica con la capa Controlador.
> Cualquier lógica que deba tener la presentación, por ejemplo,
> mostrar u ocultar campos de un formulario dependiendo del estado de un dato,
> es lógica que pertenece a esta capa.

* Controlador

> En esta capa se relacionan los datos provistos/enviados a la Vista con el Modelo.
> Debe ser muy lígera, no debe poseer lógica de negocios ni ser muy compleja.

* Modelo

> Aquí se encuentra la lógica de negocios y es una abstracción directa con la capa de
> persistencia. Donde el uso de los modelos es transparente al almacenamiento, pero el
> almacenamiento está embebido en el modelo.

#### Interacciones de la Capa de Persistencia

La capa de persistencia se encuentra embebida dentro del modelo, generalmente son provistas por el
framework utilizado para crear la aplicación y uno no tiene interacción directa mas allá de la
configuración necesaria para conectarse al SGBD. Son provistas a traves de un ORM (Mapeador Objeto-Relacional)
en el caso de BBDD Relacionales o por un ODM (Mapeador Objeto-Documental) en el caso de BBDD no relacionales.
En el caso de sistemas de almacenamiento no incluidos por el framework, permiten extender la interacción
con el sistema de almacenamiento y definir los elementos necesarios para permitir la persistencia en el medio deseado.

## Patrones de la Capa de Persistencia

En ambas arquitecturas presentadas anteriormente, la capa de persistencia (si bien en una se encuentra embebida y es transparente),
utilizan abstracciones del almacenamiento. Estas abstracciones, en el caso de BBDD relacionales utilizan un tipo de abstracción
llamado ORM, para el cual existen diferentes patrones.

Los más populares son:

* Data Mapper
* Active Record
* DAO/DTO (Row Data Gateway y Table Data Gateway)

En los cuatro casos, si bien tienen arquitecturas y formas de uso distintos, uno de sus principales objetivos es enmascarar el
uso de SQL detras de los objetos de dominio. Para ello, se exponen en detalle las diferentes arquitecturas.

### DataMapper

Los objetos y las base de datos relacionales poseen diferentes mecanismos para estructurar los datos. Muchas partes de un objeto, tales como las colecciones y la herencia, no están presentes en las base de datos relacionales. Cuando uno construye un model de objetos con una gran cantidad de lógica de negocios es importante usar estos mecanismos para una mejor organización de los datos y comportamiento. Hacerlo, llevará a que el esquema de objetos y el relacional no coincidan.

Aún se necesita transferir los datos entre estos esquemas, y este proceso se vuelve complejo. Si los objetos conocen la estructura de la base de datos, los cambios en uno tienden a propagarse al otro.

El Data Mapper es una capa de acceso a datos que separa a los objetos de la base de datos. Su responsabilidad es la de transferir datos entre éstos, y además, la de aislarlos entre sí. Con los Data Mappers, los objetos no necesitan siquiera saber que hay una base de datos presente, tampoco necesitan una interfaz de código SQL ni conocimiento del esquema de la base de datos.

### ActiveRecord

Los objetos poseen datos y comportamiento, y la mayor parte necesitan ser almacenados en una base de datos. ActiveRecord, a diferencia de Data Mapper, coloca la lógica de acceso a datos en el dominio del objeto. En el mismo se declaran el nombre de la estructura en la que se persiste (en un esquema relacional: el nombre de la tabla), los atributos que el objeto tiene - los cuales los interpreta como campos en la estructura a persistir - y sus relaciones con otras entidades.

En general, los objetos de ActiveRecord son un espejo de la estructura su estructura en la base de datos.

### DAO/DTO (Data Access Object, Data Transfer Object)

La arquitectura DAO/DTO esta basada en una combinacion de Row Data Gateway y Table Data Gateway.

El DAO es un TDG y el DTO es un modelo de dominio que puede encapsular llamadas al DAO para las operaciones de escritura.

#### Table Data Gateway

Extrae una interfaz a través de la cual uno puede realizar interacciones con una tabla de la BBDD. Las que propone la interfaz son:

* find(id): RecordSet
* update(id, *args)
* insert(*args)
* delete(id)

> *args es la lista de argumentos que corresponden para la tabla abstraida, por ejemplo en el modelo Persona, un TDG podría tener como
> *args lo siguiente: `update(id, firstName, lastName, phoneNumber)`

#### Row Data Gateway

Similar al TGD, pero separa la lógica en 2 partes separadas. La lógica de lectura de datos, se realiza igual al TGD, y la de escritura,
se embebe en el modelo de dominio.
