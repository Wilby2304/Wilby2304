package com.redsocial;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Stack;

public class VentanaPublicacion extends JFrame {
    private JTextArea areaPublicacion;
    private JButton botonPublicar;
    private Stack<Publicacion> pilaPublicaciones;
    private Usuario usuarioActual;

    private RedSocial redSocial;
    private VentanaPrincipal ventanaPrincipal;

    public VentanaPublicacion(Usuario usuario, RedSocial redSocial, VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.redSocial = redSocial;
        usuarioActual = usuario;
        pilaPublicaciones = new Stack<>();

        setTitle("Publicar Tuit");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        areaPublicacion = new JTextArea(5, 20);
        areaPublicacion.setLineWrap(true);
        areaPublicacion.setWrapStyleWord(true);
        limitarCaracteres(areaPublicacion, 300);
        panelPrincipal.add(new JScrollPane(areaPublicacion), BorderLayout.CENTER);

        botonPublicar = new JButton("Publicar");
        botonPublicar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                publicarTuit();
            }

        }
        );
        panelPrincipal.add(botonPublicar, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void publicarTuit() {
        String contenido = areaPublicacion.getText();
        if (!contenido.isEmpty()) {
            // Define el formato de la fecha
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Publicacion nuevaPublicacion = new Publicacion(contenido, usuarioActual, new Date());
            pilaPublicaciones.push(nuevaPublicacion);
            areaPublicacion.setText("");
            JOptionPane.showMessageDialog(this, "Tuit publicado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Formatea la fecha a una cadena de texto
            String fechaFormateada = formatoFecha.format(nuevaPublicacion.getFecha());
            // Guarda la publicación en la base de datos
            try {
                Statement statement = redSocial.getConexionDB().createStatement();
                String sql = "INSERT INTO publicaciones (contenido, usuario, fecha) VALUES ('"
                        + nuevaPublicacion.getContenido() + "', '"
                        + nuevaPublicacion.getUsuario().getNombreUsuario() + "', '"
                        + fechaFormateada + "')";
                int affectedRows = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        nuevaPublicacion.setId(generatedKeys.getInt(1)); // Establece el ID de la Publicacion
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Después de publicar el tuit, actualiza la lista de publicaciones en
            // VentanaPrincipal
            LinkedList<Publicacion> publicaciones = redSocial.obtenerPublicacionesUsuarioYSeguidos(usuarioActual);
            ventanaPrincipal.actualizarPublicaciones(publicaciones.toArray(new Publicacion[0]));
            usuarioActual.getPublicaciones().add(nuevaPublicacion);
            ventanaPrincipal.actualizarUI();
        } else {
            JOptionPane.showMessageDialog(this, "El contenido del tuit no puede estar vacío.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limitarCaracteres(JTextArea textArea, int maxCaracteres) {
        AbstractDocument doc = (AbstractDocument) textArea.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if ((doc.getLength() + text.length()) <= maxCaracteres) {
                    super.insertString(fb, offset, text, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if ((doc.getLength() - length + text.length()) <= maxCaracteres) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}
