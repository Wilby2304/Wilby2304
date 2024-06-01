package com.redsocial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RedSocial {
    private Connection conexionDB;

    public RedSocial() {
        // Inicializar la conexión a la base de datos SQLite
        inicializarConexionDB();

        // Cargar los 5000 usuarios en la base de datos solo si es necesario
        if (necesitaCargarUsuarios()) {
            cargarUsuarios();
        }
    }

    private void inicializarConexionDB() {
        try {
            // Cargar el controlador JDBC para SQLite
            Class.forName("org.sqlite.JDBC");

            // Establecer la conexión a la base de datos
            conexionDB = DriverManager.getConnection("jdbc:sqlite:redsocial.db");

            // Crear la tabla 'usuarios' si no existe
            Statement statement = conexionDB.createStatement();
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS usuarios (nombre TEXT, usuario TEXT PRIMARY KEY, contrasena TEXT)");

            // Crear la tabla 'publicaciones' si no existe
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS publicaciones (id INTEGER PRIMARY KEY, contenido TEXT, usuario TEXT, fecha TEXT)");

            // Crear la tabla 'comentarios' si no existe
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS comentarios (" +
                            "id INTEGER PRIMARY KEY," +
                            "contenido TEXT," +
                            "usuario TEXT," +
                            "publicacion INTEGER," +
                            "fecha TEXT," +
                            "FOREIGN KEY(usuario) REFERENCES Usuarios(usuario)," +
                            "FOREIGN KEY(publicacion) REFERENCES Publicaciones(id)" +
                            ")");

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS likes (" +
                            "id INTEGER PRIMARY KEY," +
                            "publicacion INTEGER," +
                            "usuario TEXT," +
                            "like BOOLEAN," +
                            "FOREIGN KEY(publicacion) REFERENCES publicaciones(id)," +
                            "FOREIGN KEY(usuario) REFERENCES usuarios(usuario)" +
                            ")");

            // Crear la tabla 'seguidos' si no existe
            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS seguidos (" +
                        "id INTEGER PRIMARY KEY," +
                        "usuarioseguidor TEXT," +
                        "usuarioseguido TEXT," +
                        "FOREIGN KEY(usuarioseguidor) REFERENCES usuarios(usuario)," +
                        "FOREIGN KEY(usuarioseguido) REFERENCES usuarios(usuario)" +
                        ")");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        
    }

    public Connection getConexionDB() {
        return conexionDB;
    }

    public LinkedList<Publicacion> obtenerTodasLasPublicaciones(RedSocial redSocial) {
        LinkedList<Publicacion> todasLasPublicaciones = new LinkedList<>();

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = redSocial.getConexionDB().createStatement();
            resultSet = statement.executeQuery("SELECT * FROM publicaciones ORDER BY fecha DESC");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String contenido = resultSet.getString("contenido");
                String nombreUsuario = resultSet.getString("usuario");
                Date fecha = null;
                String fechaString = resultSet.getString("fecha");
                // Define el formato de la fecha
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    fecha = formatoFecha.parse(fechaString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Usuario usuario = redSocial.obtenerUsuarioPorNombreUsuario(nombreUsuario);
                Publicacion publicacion = new Publicacion(id, contenido, usuario, fecha);
                todasLasPublicaciones.add(publicacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        return todasLasPublicaciones;
    }

    public Usuario obtenerUsuarioPorNombreUsuario(String nombreUsuario) {

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conexionDB.createStatement();
            resultSet = statement
                    .executeQuery("SELECT * FROM usuarios WHERE usuario = '" + nombreUsuario + "'");
            if (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String contrasena = resultSet.getString("contrasena");
                return new Usuario(nombre, nombreUsuario, contrasena);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        return null;
    }

    public void cargarPublicacionesUsuario(Usuario usuario) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = getConexionDB().createStatement();
            resultSet = statement.executeQuery("SELECT * FROM publicaciones WHERE usuario = '"
                    + usuario.getNombreUsuario() + "' ORDER BY fecha DESC");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String contenido = resultSet.getString("contenido");
                Date fecha = null;
                String fechaString = resultSet.getString("fecha");
                // Define el formato de la fecha
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    fecha = formatoFecha.parse(fechaString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Publicacion publicacion = new Publicacion(id, contenido, usuario, fecha);
                usuario.getPublicaciones().push(publicacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
    }

    public List<Usuario> obtenerUsuariosDisponibles() {
        List<Usuario> usuariosDisponibles = new ArrayList<>();

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conexionDB.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM usuarios");
            while (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String nombreUsuario = resultSet.getString("usuario");
                // No es necesario cargar la contraseña para mostrar los usuarios disponibles
                usuariosDisponibles.add(new Usuario(nombre, nombreUsuario, ""));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }

        return usuariosDisponibles;
    }

        // Método para obtener la lista de usuarios seguidos por un usuario dado
        public LinkedList<Usuario> obtenerSeguidos(Usuario usuario) {
            LinkedList<Usuario> seguidos = new LinkedList<>();
            
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                statement = conexionDB.createStatement();
                resultSet = statement.executeQuery(
                        "SELECT usuarios.nombre, usuarios.usuario, usuarios.contrasena FROM usuarios " +
                                "INNER JOIN seguidos ON usuarios.usuario = seguidos.usuarioseguido " +
                                "WHERE seguidos.usuarioseguidor = '" + usuario.getNombreUsuario() + "'");
                while (resultSet.next()) {
                    String nombre = resultSet.getString("nombre");
                    String nombreUsuario = resultSet.getString("usuario");
                    String contrasena = resultSet.getString("contrasena");
                    seguidos.add(new Usuario(nombre, nombreUsuario, contrasena));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                // Cierra el ResultSet y el Statement
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        /* ignored */ }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        /* ignored */ }
                }
            }
            
            return seguidos;
        }

            // Método para verificar si un usuario sigue a otro
    public boolean usuarioYaSigueAUsuario(Usuario seguidor, String nombreUsuarioSeguido) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conexionDB.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM seguidos WHERE usuarioseguidor = '" +
                    seguidor.getNombreUsuario() + "' AND usuarioseguido = '" + nombreUsuarioSeguido + "'");
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Devuelve verdadero si ya sigue al usuario, falso en caso contrario
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        return false; // Si hay algún error o no hay resultados, devuelve falso
    }

    public LinkedList<Usuario> obtenerSeguidores(Usuario usuario) {
        LinkedList<Usuario> seguidores = new LinkedList<>();
        
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conexionDB.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT usuarios.nombre, usuarios.usuario, usuarios.contrasena FROM usuarios " +
                            "INNER JOIN seguidos ON usuarios.usuario = seguidos.usuarioseguidor " +
                            "WHERE seguidos.usuarioseguido = '" + usuario.getNombreUsuario() + "'");
            while (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String nombreUsuario = resultSet.getString("usuario");
                String contrasena = resultSet.getString("contrasena");
                seguidores.add(new Usuario(nombre, nombreUsuario, contrasena));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        
        return seguidores;
    }


    public LinkedList<Publicacion> obtenerPublicacionesUsuarioYSeguidos(Usuario usuario) {
        LinkedList<Publicacion> publicaciones = new LinkedList<>();
        
        // Agrega las publicaciones del usuario
        publicaciones.addAll(obtenerPublicacionesUsuario(usuario));
        
        // Agrega las publicaciones de los usuarios seguidos
        LinkedList<Usuario> seguidos = obtenerSeguidos(usuario);
        for (Usuario seguido : seguidos) {
            publicaciones.addAll(obtenerPublicacionesUsuario(seguido));
        }
        
        return publicaciones;
    }
    
    private LinkedList<Publicacion> obtenerPublicacionesUsuario(Usuario usuario) {
        LinkedList<Publicacion> publicaciones = new LinkedList<>();
    
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = getConexionDB().createStatement();
            resultSet = statement.executeQuery("SELECT * FROM publicaciones WHERE usuario = '"
                    + usuario.getNombreUsuario() + "' ORDER BY fecha DESC");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String contenido = resultSet.getString("contenido");
                Date fecha = null;
                String fechaString = resultSet.getString("fecha");
                // Define el formato de la fecha
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    fecha = formatoFecha.parse(fechaString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Publicacion publicacion = new Publicacion(id, contenido, usuario, fecha);
                publicaciones.add(publicacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        
        return publicaciones;
    }

     private boolean necesitaCargarUsuarios() {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // Consultar la cantidad actual de usuarios en la tabla
            statement = conexionDB.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM usuarios");

            // Obtener el resultado de la consulta
            if (resultSet.next()) {
                int cantidadUsuarios = resultSet.getInt(1);
                // Verificar si hay menos de 5000 usuarios en la tabla
                return cantidadUsuarios < 5000;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cierra el ResultSet y el Statement
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        // En caso de error, asumimos que es necesario cargar los usuarios
        return true;
    }

    private void cargarUsuarios() {
        try {
            // Preparar la instrucción SQL para insertar usuarios
            String sql = "INSERT INTO usuarios (nombre, usuario, contrasena) VALUES (?, ?, ?)";
            PreparedStatement statement = conexionDB.prepareStatement(sql);

            // Generar y ejecutar las instrucciones SQL para cada usuario
            for (int i = 0; i < 5000; i++) {
                // Generar un usuario único con un nombre y contraseña basados en el índice
                String nombre = "Usuario" + i;
                String usuario = "usuario" + i;
                String contrasena = "contraseña" + i;

                // Establecer los parámetros en la instrucción SQL
                statement.setString(1, nombre);
                statement.setString(2, usuario);
                statement.setString(3, contrasena);

                // Ejecutar la instrucción SQL para insertar el usuario
                statement.executeUpdate();
            }

            // Cerrar la declaración después de usarla
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    
}
