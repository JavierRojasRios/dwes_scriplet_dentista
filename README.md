# Clínica Dental

Aplicación web para gestionar pacientes y citas de una clínica dental. Está implementada
con **Servlets, JSP, JSTL y JDBC** sobre MySQL, siguiendo el patrón MVC:

- Un único `DentistaServlet` recibe las peticiones y selecciona la operación y la vista.
- `DentistaDao` centraliza el acceso a datos y las consultas SQL.
- Los modelos Java transportan los datos.
- Las JSP bajo `WEB-INF/vistas` solo presentan información; no contienen scriptlets y no
  se pueden solicitar directamente desde el navegador.

## Estructura

```text
src/main/
├── java/es/dwes/dentista/
│   ├── dao/DentistaDao.java
│   ├── model/                 # Paciente, PacienteOpcion, Cita y Odontologo
│   └── servlet/DentistaServlet.java
├── resources/dentista.sql
└── webapp/
    ├── index.jsp
    ├── estilos.css
    └── WEB-INF/vistas/        # JSP de presentación con JSTL
```

## Base de datos

La base de datos `dentista` contiene las tablas `paciente`, `odontologos` y `cita`.
Las citas hacen referencia a un paciente y un odontólogo; al borrar un paciente también
se borran sus citas asociadas (`ON DELETE CASCADE`).

1. Importa el esquema y los datos de ejemplo:

   ```bash
   mysql -u user -puser < src/main/resources/dentista.sql
   ```

2. Por defecto, la aplicación se conecta a MySQL en `localhost:3306`, con usuario y
   contraseña `user`. La URL y las credenciales están centralizadas en
   `DentistaDao`; cámbialas allí si tu instalación utiliza otros valores.

Maven incluye MySQL Connector/J en el WAR como dependencia de ejecución. La aplicación
carga explícitamente `com.mysql.cj.jdbc.Driver` al abrir la conexión para asegurar que
Tomcat lo registre desde el classloader de la aplicación.

## Requisitos y despliegue

- JDK 25 y Maven 3.8 o posterior para compilar el proyecto.
- Apache Tomcat 11 para desplegar la aplicación. Tomcat 11 implementa Jakarta EE 11 y
  requiere Java 17 o posterior.
- MySQL Server con la base de datos preparada como se indica arriba.

Empaqueta el proyecto con:

```bash
mvn clean package
```

Se genera `target/scriplet_dentista.war`. Despliega ese WAR en Tomcat 11; el contexto
predeterminado será `/scriplet_dentista`.

El `pom.xml` declara Servlet 6.1 como dependencia `provided` (la proporciona Tomcat) y
JSTL 3.0 junto con su implementación para las vistas.

## Rutas de la aplicación

| Ruta | Función |
|---|---|
| `/` | Menú principal |
| `/pacientes` | Listado de pacientes y número de citas |
| `/pacientes/nuevo` | Formulario y alta de pacientes |
| `/pacientes/borrar` | Baja de paciente mediante `POST` |
| `/citas` | Próximas citas; añade `?todas=1` para ver el histórico |
| `/citas/nueva` | Formulario y alta de citas |
| `/citas/borrar` | Anulación de cita mediante `POST` |

Las altas y bajas siguen el patrón POST/Redirect/GET, y las operaciones de escritura
utilizan `PreparedStatement`. El único servlet enruta las distintas direcciones según
la ruta y el método HTTP; las JSP se ocupan de presentar los formularios y resultados.
