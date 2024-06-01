# tecnologias
- java 8
- sqlite3 para base de datos
- java swing para interfaz gráfica
- maven como manejador de dependencias


# importante
1. cuando se exporta el programa mediante java o mediante maven y luego se ejecuta por primera vez va a tardar un poco ya que estará cargando los 5000 registros de usuarios en la tabla usuario.

2. Para visualizar la base de datos descargar DB Browser, o tambien DBeaver. Cualquier programa que permita abrir archivos .db

# CODIGO:

## Usuario.java
Clase que contiene todas las propiedades y métodos para el manejo de los usuarios

## Publicacion.java
Clase que contiene todas las propiedades y métodos para el manejo de las publicaciones

## PilaPublicaciones.java
Clase que se utiliza para apilar las publicaciones y utilizarlas según indican los requerimientos

## Comentario.java
Clase que contiene todas las propiedades y métodos para el manejo de los comentarios

## Like.java
Clase que contiene todas las propiedades y métodos para el manejo de los likes

## RedSocial.java
Clase que maneja todas las consultas sql para conectarse con la base de datos. También es la que genera la instancia "REDSOCIAL". Dentro de ella están los usuarios logueados, publicaciones, etc. de la sesion.

## VentanaPrincipal.java
Clase donde se encuentra la interfaz referida a la Ventana Principal, que aparece luego del logueo. Dentro se encuentran todos los métodos para su correcto funcionamiento.

## VentanaRegistroInicio.java
Clase donde se encuentra la interfaz referida a la inicio de sesión y registro de usuarios. Dentro se encuentran todos los métodos para su correcto funcionamiento.

## VentanaPublicacion.java
Clase donde se encuentra la interfaz que aparece cuando se quiere dejar un twit. Dentro se encuentran todos los métodos para su correcto funcionamiento.

## VentanaPublicaciones.java
Clase donde se encuentra la interfaz que aparece cuando se hace doble click en una publicación. En ella se pueden dejar likes, ver, eliminar y escribir comentarios, etc. Dentro se encuentran todos los métodos para su correcto funcionamiento.

## VentanaComentario.java
Clase donde se encuentra la interfaz que aparece cuando se quiere dejar un comentario en una publicacion. Dentro se encuentran todos los métodos para su correcto funcionamiento.

## VentanaSeguirUsuarios.java
Clase donde se encuentra la interfaz que aparece cuando se hace click en "Seguir", en ella se selecciona el usuario a Seguir. Dentro se encuentran todos los métodos para su correcto funcionamiento.

## VentanaBusquedaUsuarios.java
Clase donde se encuentra la interfaz que aparece cuando se hace click en "Buscar Usuarios", en ella se buscan usuarios. Dentro se encuentran todos los métodos para su correcto funcionamiento.
