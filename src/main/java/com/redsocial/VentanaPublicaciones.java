package com.redsocial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaPublicaciones extends JFrame {;
    private JButton botonComentario;
    private JButton botonLike;
    private Usuario usuarioActual;

    private Publicacion publicacion;
    private JTextArea areaDetalles;
    private JButton botonEliminar;
    private RedSocial redSocial; // Agrega esta línea

    private VentanaPrincipal ventanaPrincipal;

    private DefaultListModel<Comentario> modeloComentarios;  // Mueve la definición aquí

    public VentanaPublicaciones(Usuario usuario, Publicacion publicacion, RedSocial redSocial,
            VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.redSocial = redSocial;
        this.publicacion = publicacion;
        usuarioActual = usuario;


        setTitle("Publicaciones");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        areaDetalles = new JTextArea();
        areaDetalles.setEditable(false);
        panelPrincipal.add(new JScrollPane(areaDetalles), BorderLayout.WEST);
        areaDetalles.setText(publicacion.toString());

        JPanel panelComentario = new JPanel(new BorderLayout());
        modeloComentarios = new DefaultListModel<>();
        JList<Comentario> listaComentarios = new JList<>(modeloComentarios);
        panelComentario.add(new JScrollPane(listaComentarios), BorderLayout.CENTER);

                // Agregar MouseListener a la lista de comentarios
                listaComentarios.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) { // Doble clic
                            int index = listaComentarios.locationToIndex(e.getPoint());
                            if (index != -1) {
                                Comentario comentarioSeleccionado = modeloComentarios.getElementAt(index);
                                eliminarComentario(comentarioSeleccionado);
                            }
                        }
                    }
                });




        botonEliminar = new JButton("Eliminar");
        botonEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPublicacion();
            }
        });
        panelPrincipal.add(botonEliminar, BorderLayout.NORTH);

        botonComentario = new JButton("Comentar");
        botonComentario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarComentario();
            }
        });
        panelComentario.add(botonComentario, BorderLayout.SOUTH);
        panelPrincipal.add(panelComentario, BorderLayout.CENTER);

        botonLike = new JButton("Like");
        botonLike.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                darLike();
            }
        });
        panelPrincipal.add(botonLike, BorderLayout.EAST);

        add(panelPrincipal);

        // Ahora que modeloComentarios se ha inicializado, puedes cargar los comentarios
        cargarComentarios();
        cargarLikes();
        cargarTodosLosLikes();
    }

    // Método para eliminar la publicación
    private void eliminarPublicacion() {
        // Verifica si el usuario logueado es el creador de la publicación
        if (usuarioActual.getNombreUsuario().equals(publicacion.getUsuario().getNombreUsuario())) {
            // Verifica si la publicación es la última que publicó el usuario
            System.out.println(usuarioActual.getPublicaciones().size());
            System.out.println(usuarioActual.getPublicaciones().isEmpty());
            System.out.println(usuarioActual.getPublicaciones().getLast().equals(publicacion));
            Publicacion pruebapublicacion = usuarioActual.getPublicaciones().getLast();
            System.out.println(pruebapublicacion);
            System.out.println(publicacion);
            if (!usuarioActual.getPublicaciones().isEmpty()
                    && usuarioActual.getPublicaciones().getLast().equals(publicacion)) {
                try {
                    Statement statement = redSocial.getConexionDB().createStatement();
                    statement.executeUpdate("DELETE FROM publicaciones WHERE id = " + publicacion.getId());
                    usuarioActual.getPublicaciones().removeLast();
                    JOptionPane.showMessageDialog(this, "Publicación eliminada correctamente.", "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.dispose(); // Cierra la ventana de la publicación

                    LinkedList<Publicacion> todasLasPublicaciones = redSocial.obtenerPublicacionesUsuarioYSeguidos(usuarioActual);
                    Publicacion[] publicacionesArray = todasLasPublicaciones.toArray(new Publicacion[0]);
                    ventanaPrincipal.actualizarPublicaciones(publicacionesArray);
                    ventanaPrincipal.actualizarUI();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Solo puedes eliminar tu última publicación.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No puedes eliminar una publicación de otro usuario.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarComentario() {
        if (publicacion != null) {
            VentanaComentario ventanaComentario = new VentanaComentario(usuarioActual, publicacion, redSocial, this);
            ventanaComentario.setVisible(true);
        }
    }

    public void actualizarComentarios() {
        // Obtiene los comentarios de la base de datos
        LinkedList<Comentario> comentariosDeLaBaseDeDatos = obtenerComentariosDeLaBaseDeDatos();
    
        // Agrega los nuevos comentarios a la lista de comentarios
        for (Comentario comentario : comentariosDeLaBaseDeDatos) {
            if (!modeloComentarios.contains(comentario)) {
                modeloComentarios.addElement(comentario);
            }
        }
    }
    

    private LinkedList<Comentario> obtenerComentariosDeLaBaseDeDatos() {
        LinkedList<Comentario> comentariosDeLaBaseDeDatos = new LinkedList<>();
        try {
            Statement statement = redSocial.getConexionDB().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Comentarios WHERE publicacion = " + publicacion.getId());
            while (resultSet.next()) {
                int id=resultSet.getInt("id");
                String contenido = resultSet.getString("contenido");
                String nombreUsuario = resultSet.getString("usuario");
                Usuario usuario = redSocial.obtenerUsuarioPorNombreUsuario(nombreUsuario);
                Date fecha = resultSet.getDate("fecha");
                Comentario comentario = new Comentario(id,contenido, usuario, fecha);
                comentariosDeLaBaseDeDatos.add(comentario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comentariosDeLaBaseDeDatos;
    }

    private void cargarComentarios() {
        try {
            Statement statement = redSocial.getConexionDB().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Comentarios WHERE publicacion = " + publicacion.getId());
            while (resultSet.next()) {
                int id= resultSet.getInt("id");
                String contenido = resultSet.getString("contenido");
                String nombreUsuario = resultSet.getString("usuario");
                Usuario usuario = redSocial.obtenerUsuarioPorNombreUsuario(nombreUsuario);
                Comentario comentario = new Comentario(id,contenido, usuario, resultSet.getDate("fecha"));
                modeloComentarios.addElement(comentario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void eliminarComentario(Comentario comentario) {
        if (comentario.getUsuario().getNombreUsuario().equals(usuarioActual.getNombreUsuario())) {
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar este comentario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                // Eliminar el comentario de la base de datos
                eliminarComentarioDeLaBaseDeDatos(comentario);

                // Eliminar el comentario del modelo
                modeloComentarios.removeElement(comentario);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No eres el autor del comentario, no puedes eliminarlo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarComentarioDeLaBaseDeDatos(Comentario comentario) {
        try {
            Statement statement = redSocial.getConexionDB().createStatement();
            statement.executeUpdate("DELETE FROM comentarios WHERE id = " + comentario.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private void darLike() {
        if (publicacion != null) {
            try {
                Statement statement = redSocial.getConexionDB().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM likes WHERE publicacion = " + publicacion.getId() + " AND usuario = '" + usuarioActual.getNombreUsuario() + "'");
                if (resultSet.next()) {
                    // El usuario ya ha dado like o dislike a esta publicación, entonces se actualiza el valor
                    int likeId = resultSet.getInt("id");
                    boolean currentLike = resultSet.getBoolean("like");
                    if (currentLike) {
                        // El usuario ya ha dado like, entonces se elimina el like
                        statement.executeUpdate("UPDATE likes SET like = 0 WHERE id = " + likeId);
                        botonLike.setText("Like");
                    } else {
                        // El usuario ya ha quitado el like, entonces se agrega un nuevo like
                        statement.executeUpdate("UPDATE likes SET like = 1 WHERE id = " + likeId);
                        botonLike.setText("Dislike");
                    }
                } else {
                    // El usuario no ha interactuado con esta publicación, entonces se agrega un nuevo like
                    statement.executeUpdate("INSERT INTO likes (publicacion, usuario, like) VALUES (" + publicacion.getId() + ", '" + usuarioActual.getNombreUsuario() + "', 1)");
                    botonLike.setText("Dislike");
                }
                cargarTodosLosLikes();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarLikes() {
        if (publicacion != null) {
            try {
                Statement statement = redSocial.getConexionDB().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM likes WHERE publicacion = " + publicacion.getId() + " AND usuario = '" + usuarioActual.getNombreUsuario() + "'");
                if (resultSet.next()) {
                    boolean like = resultSet.getBoolean("like");
                    if (like) {
                        botonLike.setText("Dislike");
                    } else {
                        botonLike.setText("Like");
                    }
                } else {
                    botonLike.setText("Like");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void cargarTodosLosLikes() {
        if (publicacion != null) {
            // Limpiar la lista de likes
            publicacion.getLikes().clear();
            try {
                Statement statement = redSocial.getConexionDB().createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM likes WHERE publicacion = " + publicacion.getId() + " AND like = 1");
                while (resultSet.next()) {
                    String nombreUsuario = resultSet.getString("usuario");
                    Usuario usuario = redSocial.obtenerUsuarioPorNombreUsuario(nombreUsuario);
                    // Obtener la fecha actual
                    Date fechaActual = new Date();
                    Like nuevoLike = new Like(usuario, fechaActual); // Crear el objeto Like con el usuario y la fecha actual
                    publicacion.agregarLike(nuevoLike); // Agregar el like a la lista de likes de la publicación
                    System.out.println("likes almacenados en la lista enlazada de likes de la publicacion");
                    System.out.println(publicacion.getLikes());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
