<h1>Descripción del proyecto</h1>
Este sistema es una aplicación de escritorio desarrollada en java SE utilizando la librería Swing para la interfaz gráfica. El objetivo es la gestión administrativa de un consultorio médico. Permitiendo realizar el ciclo completo de un CRUD sobre la información de pacientes y su vinculación con citas médicas y especialidades.
La aplicación implementa el patrón de diseño modelo-Vista-Controlador para garantizar una separación clara entre la lógica de negocio, la interfaz de usuarios y el acceso a los datos,
<h2>Arquitectura de datos</h2>
El sistema se fundamenta en una base de datos relacional estructurada de la siguiente manera: <br>
•	Pacientes: Almacena información personal (documento, nombre, edad, tipo de afiliación) <br>
•	Especialidad: Catalogo de servicio médicos con sus respectivos costo base. <br>
•	Citas: Entidad intermedia que relaciona a un paciente con una especialidad, registrando la fecha y el valor del copago. <br>
<h2>Tecnologías usadas</h2>
o	Lenguaje Java SE <br>
o	Interfaz Gráfica: Java Swing (JFrame Form) <br>
o	Base de datos: SQlite <br>
o	Persistencia: JDBC <br>
o	Patron de diseño: modelo-vista-controlador <br>
<h2>Estructura del código fuente</h2>
Modelo (Package: newpackage) contiene las clases que representan los datos y el acceso a la base de datos <br>
Paciente.java: clase que define los atributos del paciente <br>
ConexionBase.java: Gestiona la conexión física con el archivo de base de datos .db <br>
PacintesDAO.java: Objeto de acceso a datos que contiene las consultas SQL para interactuar con SQLite. <br>
Vista (Package: Vista): InterfazGrafica.java: desarrollada con JFrameForm contiene la disposición visual: tabla de datos, campos de texto, botones de acción y selectores de especialidad. <br>
Controlador (Package: Controlador): Controlador.java es el cerebro del sistema. Escucha los eventos de los botones, captura los datos de la vista, invoca los métodos del DAO y actualiza la tabla en tiempo real. <br>
<h2>Funcionalidades principales</h2>
1.	Registro de pacientes: Validación de campos obligatorios y asignación automática de cita al insertar un nuevo registro <br>
2.	Visualización dinámica: Carga de especialidades desde la base de datos directamente al JComboBox <br>
3.	Gestión de citas: Al eliminar un paciente, el sistema realiza una eliminación en cascada programada para limpiar los registros de citas asociados. <br>
4.	Calculo de indicadores: Conteo automático de pacientes registrados visibles en la interfaz <br>
