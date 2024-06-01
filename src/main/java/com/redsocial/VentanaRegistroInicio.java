package com.redsocial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VentanaRegistroInicio extends JFrame {
    private JTextField campoNombre;
    private JTextField campoUsuario;
    private JPasswordField campoContrasena;
    private JButton botonRegistrar;
    private JButton botonIniciarSesion;

    private Connection conexionDB;
    private RedSocial redSocial;

    public VentanaRegistroInicio(RedSocial redSocial) {
        this.redSocial = redSocial;

        setTitle("Registro e Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new GridLayout(4, 2));

        panelPrincipal.add(new JLabel("Nombre:"));
        campoNombre = new JTextField();
        panelPrincipal.add(campoNombre);

        panelPrincipal.add(new JLabel("Usuario:"));
        campoUsuario = new JTextField();
        panelPrincipal.add(campoUsuario);

        panelPrincipal.add(new JLabel("Contraseña:"));
        campoContrasena = new JPasswordField();
        panelPrincipal.add(campoContrasena);

        botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarUsuario();
            }
        });
        panelPrincipal.add(botonRegistrar);

        botonIniciarSesion = new JButton("Iniciar Sesión");
        botonIniciarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSesion();
            }
        });
        panelPrincipal.add(botonIniciarSesion);

        add(panelPrincipal);

        try {
            conexionDB = DriverManager.getConnection("jdbc:sqlite:redsocial.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void registrarUsuario() {
        String nombre = campoNombre.getText();
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());

        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = conexionDB.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM usuarios WHERE usuario = '" + usuario + "'");

            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario ya está en uso.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                statement.executeUpdate("INSERT INTO usuarios (nombre, usuario, contrasena) VALUES ('" + nombre + "', '"
                        + usuario + "', '" + contrasena + "')");
                JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
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

    private void iniciarSesion() {
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conexionDB.createStatement();
            resultSet = statement.executeQuery(
                    "SELECT * FROM usuarios WHERE usuario = '" + usuario + "' AND contrasena = '" + contrasena + "'");
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                Usuario usuarioActual = new Usuario(resultSet.getString("nombre"), usuario, contrasena);
                // usuarioActual.setId(resultSet.getInt("id"));

                // Cargar las listas de seguidos y seguidores
                usuarioActual.setSeguidos(redSocial.obtenerSeguidos(usuarioActual));
                usuarioActual.setSeguidores(redSocial.obtenerSeguidores(usuarioActual));

                // Cargar las publicaciones del usuario
                redSocial.cargarPublicacionesUsuario(usuarioActual);
                System.out.println("Número de publicaciones cargadas: " + usuarioActual.getPublicaciones().size());

                VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(redSocial, usuarioActual);
                ventanaPrincipal.setVisible(true);
                this.dispose(); // Cierra la ventana de inicio de sesión
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
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

    // ? POR ACA SE ARRANCA EL PROGRAMA
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RedSocial redSocial = new RedSocial();
            VentanaRegistroInicio ventana = new VentanaRegistroInicio(redSocial);
            ventana.setVisible(true);
        });
    }
}
