package com.redsocial;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import java.util.Stack;
public class VentanaPrincipal extends JFrame {
    private JPanel panelPrincipal;
    private JTextField campoNombre;
    private JTextField campoUsuario;
    private JList<String> listaSeguidos;
    private JList<String> listaSeguidores;
    private JButton botonPublicar;
    private JButton botonSeguir;

    private RedSocial redSocial;
    private Usuario usuarioActual; // Asegúrate de tener una referencia al usuario actual

    private JList<Publicacion> listaPublicaciones;
    

    // Crear una lista para almacenar las publicaciones ocultas
    private Stack<Publicacion> publicacionesOcultas = new Stack<>();

    // Agregar el botón "Buscar Usuarios" debajo del panel de seguidores
    JButton botonBuscarUsuarios = new JButton("Buscar Usuarios");

    

    public VentanaPrincipal(RedSocial redSocial, Usuario usuarioActual) {
        this.redSocial = redSocial;
        this.usuarioActual = usuarioActual;
        // Configurar la ventana principal
        setTitle("Bienvenido, " + usuarioActual.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Crear el panel principal
        panelPrincipal = new JPanel(new BorderLayout());

        // Obtener la lista de usuarios seguidos del usuario actual
        LinkedList<Usuario> seguidosList = redSocial.obtenerSeguidos(usuarioActual);

        // Crear un array de cadenas para almacenar los nombres de usuario seguidos
        String[] nombresSeguidos = new String[seguidosList.size()];
        for (int i = 0; i < seguidosList.size(); i++) {
            nombresSeguidos[i] = seguidosList.get(i).getNombreUsuario();
        }

        // Configurar el modelo de datos de la lista de seguidos
        DefaultListModel<String> modeloSeguidos = new DefaultListModel<>();
        for (String nombreSeguido : nombresSeguidos) {
            modeloSeguidos.addElement(nombreSeguido);
        }

        // Crear la lista de seguidos con el modelo de datos configurado
        listaSeguidos = new JList<>(modeloSeguidos);

        // Crear los componentes de la interfaz
        campoNombre = new JTextField(usuarioActual.getNombre());
        campoNombre.setEditable(false);
        campoUsuario = new JTextField(usuarioActual.getNombreUsuario());
        campoUsuario.setEditable(false);
        listaSeguidores = new JList<>();
        botonPublicar = new JButton("Publicar");
        botonSeguir = new JButton("Seguir");

        // Organizar los componentes en paneles
        JPanel panelInicio = new JPanel(new FlowLayout());
        panelInicio.add(new JLabel("Usuario:"));
        panelInicio.add(campoUsuario);
        panelInicio.add(new JLabel("Nombre:"));
        panelInicio.add(campoNombre);

        JPanel panelPublicacion = new JPanel(new BorderLayout());
        panelPublicacion.add(new JLabel("Publicación:"), BorderLayout.NORTH);

        DefaultListModel<Publicacion> modeloPublicaciones = new DefaultListModel<>();
        listaPublicaciones = new JList<>(modeloPublicaciones);
        panelPublicacion.add(new JScrollPane(listaPublicaciones), BorderLayout.CENTER);

        // Agregar MouseListener para la lista de publicaciones
        listaPublicaciones.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Verificar si se hizo clic derecho
                if (SwingUtilities.isRightMouseButton(e)) {
                    // Obtener el índice de la publicación seleccionada
                    int index = listaPublicaciones.locationToIndex(e.getPoint());
                    // Seleccionar la publicación en base al índice
                    listaPublicaciones.setSelectedIndex(index);
                    // Mostrar el menú contextual para eliminar la publicación
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem menuItem = new JMenuItem("Esconder");
                    // Dentro del ActionListener para el menú "Esconder"

                    menuItem.addActionListener(ev -> {
                        // Obtén la publicación seleccionada en la lista
                        Publicacion publicacionSeleccionada = listaPublicaciones.getSelectedValue();
                        if (publicacionSeleccionada != null) {
                            // Agrega la publicación seleccionada al Stack de publicaciones ocultas
                            publicacionesOcultas.push(publicacionSeleccionada);
                    
                            // Elimina la publicación del modelo de datos
                            modeloPublicaciones.removeElement(publicacionSeleccionada);
                    
                            // Actualizar la vista de la lista de publicaciones con el modelo de datos actualizado
                            actualizarPublicacionesLocal(modeloPublicaciones);
                            actualizarUI();
                        } else {
                            // Si no se selecciona ninguna publicación, muestra un mensaje de advertencia
                            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                    "Por favor, selecciona una publicación para esconder", "Aviso",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    });
                    popupMenu.add(menuItem);
                    popupMenu.show(listaPublicaciones, e.getX(), e.getY());
                } else if (e.getClickCount() == 2) { // Doble clic
                    // Obtén la publicación seleccionada
                    Publicacion publicacionSeleccionada = listaPublicaciones.getSelectedValue();
                    if (publicacionSeleccionada != null) {
                        // Abre la ventana de publicaciones para la publicación seleccionada
                        VentanaPublicaciones ventanaPublicaciones = new VentanaPublicaciones(usuarioActual,
                                publicacionSeleccionada, redSocial, VentanaPrincipal.this);
                        ventanaPublicaciones.setVisible(true);
                    }
                }
            }
        });

        panelPublicacion.add(botonPublicar, BorderLayout.SOUTH);
        // panelPublicacion.add(botonActualizar, BorderLayout.EAST); // Agregar el botón "Actualizar"


        JPanel panelSeguidos = new JPanel(new BorderLayout());
        panelSeguidos.add(new JLabel("Seguidos:"), BorderLayout.NORTH);
        panelSeguidos.add(new JScrollPane(listaSeguidos), BorderLayout.CENTER);
        panelSeguidos.add(botonSeguir, BorderLayout.SOUTH);

        JPanel panelSeguidores = new JPanel(new BorderLayout());
        panelSeguidores.add(new JLabel("Seguidores:"), BorderLayout.NORTH);
        panelSeguidores.add(new JScrollPane(listaSeguidores), BorderLayout.CENTER);
        panelSeguidores.add(botonBuscarUsuarios,BorderLayout.SOUTH);

        // Agregar los paneles al panel principal
        panelPrincipal.add(panelInicio, BorderLayout.NORTH);
        panelPrincipal.add(panelPublicacion, BorderLayout.CENTER);
        panelPrincipal.add(panelSeguidos, BorderLayout.WEST);
        panelPrincipal.add(panelSeguidores, BorderLayout.EAST);

        // Agregar el panel principal a la ventana
        add(panelPrincipal);

        // Imprimir seguidos y seguidores en consola
        System.out.println("Seguidos:");
        for (Usuario seguido : usuarioActual.getSeguidos()) {
            System.out.println(seguido.getNombreUsuario());
        }

        System.out.println("Seguidores:");
        for (Usuario seguidor : usuarioActual.getSeguidores()) {
            System.out.println(seguidor.getNombreUsuario());
        }

        botonBuscarUsuarios.addActionListener(e -> abrirVentanaBusquedaUsuarios());

        botonPublicar.addActionListener(e -> abrirVentanaPublicacion());

        botonSeguir.addActionListener(e -> abrirVentanaSeguirUsuarios());

        listaSeguidos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic
                    String usuarioSeguido = listaSeguidos.getSelectedValue();
                    if (usuarioSeguido != null) {
                        try (Statement statement = redSocial.getConexionDB().createStatement()) {
                            String query = "DELETE FROM seguidos WHERE usuarioseguidor = '" +
                                    usuarioActual.getNombreUsuario() + "' AND usuarioseguido = '" + usuarioSeguido
                                    + "'";
                            int rowsAffected = statement.executeUpdate(query);
                            if (rowsAffected > 0) {
                                // Actualizar la lista de seguidos en el objeto usuarioActual
                                LinkedList<Usuario> seguidos = redSocial.obtenerSeguidos(usuarioActual);
                                usuarioActual.setSeguidos(seguidos);

                                // Actualizar la lista de seguidos en la ventana principal
                                actualizarListaSeguidos();

                                // Volver a cargar todas las publicaciones del usuario actual y de los usuarios
                                // seguidos
                                LinkedList<Publicacion> todasLasPublicaciones = redSocial
                                        .obtenerPublicacionesUsuarioYSeguidos(usuarioActual);
                                Publicacion[] publicacionesArray = todasLasPublicaciones.toArray(new Publicacion[0]);

                                // Actualizar la lista de publicaciones en la ventana principal
                                actualizarPublicaciones(publicacionesArray);

                                JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                        "Has dejado de seguir a " + usuarioSeguido, "Éxito",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                        "No se pudo dejar de seguir a " + usuarioSeguido, "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            actualizarUI();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                    "Error al dejar de seguir al usuario", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        // Dentro del MouseAdapter de listaSeguidores
        listaSeguidores.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Doble clic
                    String usuarioSeguidor = listaSeguidores.getSelectedValue();
                    if (usuarioSeguidor != null) {
                        try (Statement statement = redSocial.getConexionDB().createStatement()) {
                            String query = "DELETE FROM seguidos WHERE usuarioseguidor = '" +
                                    usuarioSeguidor + "' AND usuarioseguido = '" + usuarioActual.getNombreUsuario()
                                    + "'";
                            int rowsAffected = statement.executeUpdate(query);
                            if (rowsAffected > 0) {
                                // Actualizar la lista de seguidores en el objeto usuarioActual
                                LinkedList<Usuario> seguidores = redSocial.obtenerSeguidores(usuarioActual);
                                usuarioActual.setSeguidores(seguidores);

                                // Actualizar la lista de seguidores en la ventana principal
                                actualizarListaSeguidores();

                                JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                        usuarioSeguidor + " ha dejado de seguirte", "Éxito",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                        "No se pudo dejar de seguir a " + usuarioSeguidor, "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                    "Error al dejar de seguir al usuario", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        actualizarListaSeguidos();
        actualizarListaSeguidores();

        LinkedList<Publicacion> publicacionesUsuarioYSeguidos = redSocial.obtenerPublicacionesUsuarioYSeguidos(usuarioActual);
        Publicacion[] publicacionesArray = publicacionesUsuarioYSeguidos.toArray(new Publicacion[0]);

        for (Publicacion publicacion : publicacionesArray) {
            modeloPublicaciones.addElement(publicacion);
        }



        actualizarPublicaciones(publicacionesArray);
    }

    public void actualizarUI(){
         // Obtener todas las publicaciones del usuario actual y de los usuarios seguidos
         LinkedList<Publicacion> todasLasPublicaciones = redSocial.obtenerPublicacionesUsuarioYSeguidos(usuarioActual);
            
         // Filtrar las publicaciones ocultas de la lista completa
         todasLasPublicaciones.removeAll(publicacionesOcultas);
         
         // Crear un nuevo modelo de datos con las publicaciones filtradas
         DefaultListModel<Publicacion> nuevoModeloPublicaciones = new DefaultListModel<>();
         for (Publicacion publicacion : todasLasPublicaciones) {
             nuevoModeloPublicaciones.addElement(publicacion);
         }
         
         // Actualizar el modelo de la lista de publicaciones con las publicaciones filtradas
         listaPublicaciones.setModel(nuevoModeloPublicaciones);
         
         // Forzar una actualización adicional de la vista
         listaPublicaciones.updateUI();
    }

    // Método para abrir la ventana de búsqueda de usuarios
    private void abrirVentanaBusquedaUsuarios() {
        // Crear y mostrar la ventana de búsqueda de usuarios
        VentanaBusquedaUsuarios ventanaBusquedaUsuarios = new VentanaBusquedaUsuarios(redSocial);
        ventanaBusquedaUsuarios.setVisible(true);
    }

    private void abrirVentanaPublicacion() {
        VentanaPublicacion ventanaPublicacion = new VentanaPublicacion(usuarioActual, redSocial, this);
        ventanaPublicacion.setVisible(true);
    }

    public void actualizarPublicacionesLocal(DefaultListModel<Publicacion> modeloPublicaciones) {
        listaPublicaciones.setModel(modeloPublicaciones);
    }

    public void actualizarPublicaciones(Publicacion[] publicaciones) {
        listaPublicaciones.setListData(publicaciones);
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    // Método para abrir la ventana de selección de usuarios a seguir
    private void abrirVentanaSeguirUsuarios() {
        // Obtener la lista de usuarios disponibles
        List<Usuario> usuariosDisponibles = redSocial.obtenerUsuariosDisponibles();

        // Filtrar para excluir al usuario logueado
        usuariosDisponibles.removeIf(usuario -> usuario.getNombreUsuario().equals(usuarioActual.getNombreUsuario()));

        // Crear una ventana de selección de usuarios a seguir
        VentanaSeguirUsuarios ventanaSeguirUsuarios = new VentanaSeguirUsuarios(usuariosDisponibles, this);
        ventanaSeguirUsuarios.setVisible(true);
    }

    public void seguirUsuario(String nombreUsuarioSeguido) {
        // Verificar si ya sigue al usuario
        if (redSocial.usuarioYaSigueAUsuario(usuarioActual, nombreUsuarioSeguido)) {
            JOptionPane.showMessageDialog(this, "Ya sigues a " + nombreUsuarioSeguido, "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Realizar la operación de seguir al usuario
        try (Statement statement = redSocial.getConexionDB().createStatement()) {
            String query = "INSERT INTO seguidos (usuarioseguidor, usuarioseguido) VALUES ('" +
                    usuarioActual.getNombreUsuario() + "', '" + nombreUsuarioSeguido + "')";
            statement.executeUpdate(query);

            // Actualizar la lista de seguidos en el objeto usuarioActual
            LinkedList<Usuario> seguidos = redSocial.obtenerSeguidos(usuarioActual);
            usuarioActual.setSeguidos(seguidos);

            // Actualizar la lista de seguidos en la ventana principal
            actualizarListaSeguidos();

            // Volver a cargar todas las publicaciones del usuario actual y de los usuarios
            // seguidos
            LinkedList<Publicacion> todasLasPublicaciones = redSocial
                    .obtenerPublicacionesUsuarioYSeguidos(usuarioActual);
            Publicacion[] publicacionesArray = todasLasPublicaciones.toArray(new Publicacion[0]);

            // Actualizar la lista de publicaciones en la ventana principal
            actualizarPublicaciones(publicacionesArray);

            JOptionPane.showMessageDialog(this, "Ahora sigues a " + nombreUsuarioSeguido, "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            actualizarUI();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al seguir al usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para actualizar la lista de seguidos en la ventana principal
    private void actualizarListaSeguidos() {
        // Obtener la lista de seguidos del usuario actual
        LinkedList<Usuario> seguidos = redSocial.obtenerSeguidos(usuarioActual);

        // Actualizar el LinkedList en el objeto usuarioActual
        usuarioActual.setSeguidos(seguidos);

        // Crear un array de cadenas para almacenar los nombres de usuario de los
        // seguidos
        String[] nombresSeguidos = new String[seguidos.size()];
        for (int i = 0; i < seguidos.size(); i++) {
            nombresSeguidos[i] = seguidos.get(i).getNombreUsuario();
        }

        // Actualizar la lista de seguidos en la interfaz gráfica
        listaSeguidos.setListData(nombresSeguidos);

        // Imprimir la lista de seguidos
        imprimirSeguidos();
    }

    // Método para actualizar la lista de seguidores en la ventana principal
    private void actualizarListaSeguidores() {
        // Obtener la lista de seguidores del usuario actual
        LinkedList<Usuario> seguidores = redSocial.obtenerSeguidores(usuarioActual);

        // Actualizar el LinkedList en el objeto usuarioActual
        usuarioActual.setSeguidores(seguidores);

        // Crear un array de cadenas para almacenar los nombres de usuario de los
        // seguidores
        String[] nombresSeguidores = new String[seguidores.size()];
        for (int i = 0; i < seguidores.size(); i++) {
            nombresSeguidores[i] = seguidores.get(i).getNombreUsuario();
        }

        // Actualizar la lista de seguidores en la interfaz gráfica
        listaSeguidores.setListData(nombresSeguidores);

        // Imprimir la lista de seguidores
        imprimirSeguidores();
    }

    private void imprimirSeguidos() {
        System.out.println("Lista enlazada de seguidos:");
        for (Usuario seguido : usuarioActual.getSeguidos()) {
            System.out.println(seguido.getNombreUsuario());
        }
    }

    private void imprimirSeguidores() {
        System.out.println("Lista enlazada de seguidores:");
        for (Usuario seguidor : usuarioActual.getSeguidores()) {
            System.out.println(seguidor.getNombreUsuario());
        }
    }

    public void actualizarPublicacionesDespuesSeguir() {
        // Obtener todas las publicaciones actualizadas
        LinkedList<Publicacion> todasLasPublicaciones = redSocial.obtenerPublicacionesUsuarioYSeguidos(usuarioActual);
    
        // Filtrar las publicaciones ocultas de la nueva lista
        todasLasPublicaciones.removeAll(publicacionesOcultas);
    
        // Crear un nuevo modelo de datos con las publicaciones actualizadas
        DefaultListModel<Publicacion> nuevoModeloPublicaciones = new DefaultListModel<>();
        for (Publicacion publicacion : todasLasPublicaciones) {
            nuevoModeloPublicaciones.addElement(publicacion);
        }
    
        // Asignar el nuevo modelo de datos a la lista de publicaciones
        listaPublicaciones.setModel(nuevoModeloPublicaciones);
    }
    
}