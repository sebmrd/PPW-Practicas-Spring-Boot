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