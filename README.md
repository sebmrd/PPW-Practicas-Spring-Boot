# Practica de Spring-Boot

Estudiante: Alvarado Sebastián  
Correo estudiantil: salvaradom1@est.ups.edu.ec  
GitHUB: sebmrd

---

## Explicación breve de 01_configuración

### ¿Cómo funciona el endpoint que creamos?

Entendí que un endpoint es como una "ventanilla de atención" en una dirección específica de nuestra aplicación (en este caso, /api/status) que está configurada para recibir peticiones GET . Cuando nosotros (o el navegador) llamamos a esa ruta, nuestro código se activa e inmediatamente nos devuelve una respuesta con datos estructurados directamente en formato JSON .

### ¿Cuál es la función de Spring Boot en la creación del servidor?

Su función principal es simplificarnos la vida utilizando algo llamado "servidores embebidos" . En lugar de obligarnos a instalar, configurar y encender un servidor externo manualmente para poder correr nuestra aplicación web , Spring Boot ya trae un servidor (Tomcat) empaquetado por defecto dentro del proyecto . Así, al momento de ejecutar nuestro código, el framework se encarga de encender automáticamente el servidor web y habilitar nuestros endpoints sin que tengamos que hacer configuraciones complejas.

---

## Explicación breve de 02_estructura_proyecto

### ¿Por qué es importante tener módulos separados?

Separar un proyecto en módulos basados en dominios (como products, users, auth) imita la arquitectura de las aplicaciones empresariales reales. Esta organización divide las responsabilidades del sistema, lo que facilita la escalabilidad del proyecto, permite que múltiples desarrolladores trabajen en paralelo sin generar conflictos y simplifica la creación de pruebas unitarias al mantener el código altamente cohesivo.

### ¿Cómo se relacionan los Controllers, Services y Repositories?

Estas tres capas trabajan en conjunto siguiendo un flujo de comunicación estricto y unidireccional (Arquitectura MVCS):

* Controllers (Capa de Presentación): Son el punto de entrada de la API. Interceptan las peticiones HTTP del cliente, validan los datos de entrada y delegan la lógica pesada al servicio correspondiente. Finalmente, estructuran y devuelven la respuesta HTTP.

* Services (Capa de Negocio): Contienen el "corazón" de la aplicación. Reciben las instrucciones del controlador, ejecutan todas las reglas de negocio, validaciones o cálculos necesarios, y se comunican con el repositorio si necesitan guardar o extraer datos.

* Repositories (Capa de Persistencia): Se encargan exclusivamente de la comunicación con la base de datos (usando JPA/Hibernate). Reciben órdenes del servicio para insertar, actualizar, eliminar o buscar registros.

El flujo siempre es:
Cliente → Controller → Service → Repository → Base de Datos (y viceversa para la respuesta).

### ¿Qué problema evita mantener una estructura clara?

Mantener esta separación estricta evita el "código espagueti", una mala práctica donde la lógica de acceso a datos, las reglas de negocio y el manejo de rutas HTTP se mezclan en un solo archivo. Al evitar esto, se previenen problemas como el alto acoplamiento (donde cambiar una línea de código rompe otra parte del sistema de forma inesperada), la dificultad para rastrear errores ("bugs") y la imposibilidad de reutilizar el código en el futuro.

---


---

## Explicación breve de 05_repositorios_persistencia

### Resultados y Evidencias - Persistencia con PostgreSQL

En esta etapa de la práctica, se migró la persistencia de datos desde una lista en memoria hacia una base de datos real utilizando PostgreSQL, Spring Data JPA e Hibernate.

#### Flujo de Datos
Se implementó un flujo de arquitectura en capas que asegura el desacoplamiento entre la base de datos y la interfaz del usuario:

1. Cliente: Envía peticiones HTTP.

2. Controller: Recibe la petición y delega al servicio.

3. Service: Implementa la lógica de negocio y utiliza el repositorio para las operaciones. 

4. Repository: Ejecuta las operaciones de persistencia mediante JPA.

5. Entity: Representa la estructura de la tabla en PostgreSQL.

6. Mapper: Realiza la conversión entre DTO, Model y Entity.

#### Auditoría con BaseEntity
Se utilizó una superclase abstracta BaseEntity para centralizar la auditoría de todas las entidades . Esta clase, marcada con @MappedSuperclass, permite que UserEntity y ProductEntity hereden automáticamente los campos de auditoría (id, createdAt, updatedAt, deleted) sin duplicidad de código.

#### Evidencia: Lista de productos en PostgreSQL
Tras realizar las peticiones POST a través de la API, se verificó la persistencia mediante una consulta directa a la base de datos.

![LIsta de Productos](assets\00_lista.png)

---

## Explicación de 06_ValidaciónDTOs_&_ReglasNegocio

En esta práctica, se implementó una capa de validación robusta utilizando **Jakarta Validation** para asegurar la integridad de los datos de entrada en los módulos de `users` y `products`.

### Implementaciones clave:
- [cite_start]**Validación de DTOs:** Se utilizaron anotaciones como `@NotBlank`, `@Size`, `@Email`, `@NotNull` y `@Min` para restringir los valores permitidos desde la capa de controlador mediante `@Valid` [cite: 625, 726-813, 1019-1040].
- [cite_start]**Lógica de Dominio:** Se refactorizó la arquitectura eliminando los *Mappers* externos, moviendo la lógica de conversión a *Factory Methods* dentro de las clases modelo (`Product.java`), lo cual facilita la construcción y conversión de entidades [cite: 625, 1026-1034].
- **Reglas de Negocio:** Se integraron validaciones en `ProductServiceImpl` para:
    - Evitar la actualización o eliminación de productos ya marcados como eliminados lógicamente.
    - [cite_start]Filtrar el listado (`findAll`) para no exponer productos eliminados [cite: 625, 1041-1045].

### Evidencias

**1. Validación de Entrada (Error 400):**
Al enviar datos que no cumplen las restricciones de validación, la API intercepta la petición y devuelve un error de tipo `Bad Request`.

![Error POST inválido](ruta/a/tu/captura_error_400.png)

**2. Control de Reglas de Negocio:**
Se validó exitosamente que el sistema impide operar sobre productos eliminados y los excluye de las consultas generales, garantizando la consistencia de los datos.

![CRUD validado correctamente](ruta/a/tu/captura_reglas_negocio.png)
