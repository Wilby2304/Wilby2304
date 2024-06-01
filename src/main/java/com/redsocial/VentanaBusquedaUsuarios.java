package com.redsocial;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class VentanaBusquedaUsuarios extends JFrame {
    private JTextField campoNombreUsuario;
    private DefaultListModel<Usuario> modeloUsuarios;
    private JList<Usuario> listaUsuarios;
    private RedSocial redSocial;

    public VentanaBusquedaUsuarios(RedSocial redSocial) {
        this.redSocial = redSocial;

        setTitle("Buscar Usuarios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        campoNombreUsuario = new JTextField();
        panelPrincipal.add(campoNombreUsuario, BorderLayout.NORTH);

        modeloUsuarios = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloUsuarios);
        panelPrincipal.add(new JScrollPane(listaUsuarios), BorderLayout.CENTER);

        cargarUsuariosDesdeDB();

        campoNombreUsuario.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarUsuarios();
            }
        });

        add(panelPrincipal);
    }

    private void cargarUsuariosDesdeDB() {

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            // Conexión a la base de datos
            Connection conexion = redSocial.getConexionDB();
            statement = conexion.createStatement();

            // Consulta para obtener los usuarios de la base de datos
            String query = "SELECT * FROM usuarios";
            resultSet = statement.executeQuery(query);

            // Agregar usuarios a la lista
            while (resultSet.next()) {
                String nombre = resultSet.getString("nombre");
                String nombreUsuario = resultSet.getString("usuario");
                String contraseña = resultSet.getString("contrasena");
                Usuario usuario = new Usuario(nombre, nombreUsuario, contraseña);
                modeloUsuarios.addElement(usuario);
            }

            // Cerrar recursos
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios desde la base de datos",
                    "Error", JOptionPane.ERROR_MESSAGE);
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

    private void filtrarUsuarios() {
        String filtro = campoNombreUsuario.getText().toLowerCase();
        modeloUsuarios.clear();

        List<Usuario> usuarios = redSocial.obtenerUsuariosDisponibles();
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().toLowerCase().contains(filtro)
                    || usuario.getNombreUsuario().toLowerCase().contains(filtro)) {
                modeloUsuarios.addElement(usuario);
            }
        }
    }
}
