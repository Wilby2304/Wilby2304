package com.redsocial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VentanaComentario extends JFrame {
    private JTextArea areaComentario;
    private JButton botonComentar;
    private Usuario usuarioActual;
    private Publicacion publicacionActual;
    private RedSocial redSocial;

    private VentanaPublicaciones ventanaPublicaciones;

    public VentanaComentario(Usuario usuarioActual, Publicacion publicacionActual, RedSocial redSocial,
            VentanaPublicaciones ventanaPublicaciones) {
        this.ventanaPublicaciones = ventanaPublicaciones;
        this.usuarioActual = usuarioActual;
        this.publicacionActual = publicacionActual;
        this.redSocial = redSocial;

        setTitle("Comentar Publicación");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        areaComentario = new JTextArea(5, 20);
        areaComentario.setLineWrap(true);
        areaComentario.setWrapStyleWord(true);
        panelPrincipal.add(new JScrollPane(areaComentario), BorderLayout.CENTER);

        botonComentar = new JButton("Comentar");
        botonComentar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comentarPublicacion();
            }
        });
        panelPrincipal.add(botonComentar, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void comentarPublicacion() {
        String contenido = areaComentario.getText();
        if (!contenido.isEmpty()) {
            // Define el formato de la fecha
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Comentario nuevoComentario = new Comentario(contenido, usuarioActual, new Date());
            publicacionActual.agregarComentario(nuevoComentario);
            areaComentario.setText("");
            JOptionPane.showMessageDialog(this, "Comentario publicado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            // Formatea la fecha a una cadena de texto
            String fechaFormateada = formatoFecha.format(nuevoComentario.getFecha());
            // Guarda el comentario en la base de datos
            try {
                Statement statement = redSocial.getConexionDB().createStatement();
                String sql = "INSERT INTO comentarios (contenido, usuario, publicacion, fecha) VALUES ('"
                        + nuevoComentario.getContenido() + "', '"
                        + nuevoComentario.getUsuario().getNombreUsuario() + "', "
                        + publicacionActual.getId() + ", '"
                        + fechaFormateada + "')";
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Actualiza la lista de comentarios en VentanaPublicaciones
            ventanaPublicaciones.actualizarComentarios();
        } else {
            JOptionPane.showMessageDialog(this, "El contenido del comentario no puede estar vacío.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
