package com.redsocial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class VentanaSeguirUsuarios extends JFrame {
    public VentanaSeguirUsuarios(List<Usuario> usuariosDisponibles, VentanaPrincipal ventanaPrincipal) {
        setTitle("Seleccionar Usuario a Seguir");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(ventanaPrincipal);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Crear una lista de nombres de usuario a partir de la lista de usuarios disponibles
        DefaultListModel<String> nombresUsuariosModel = new DefaultListModel<>();
        for (Usuario usuario : usuariosDisponibles) {
            nombresUsuariosModel.addElement(usuario.getNombreUsuario());
        }

        // Crear la lista de nombres de usuarios disponibles
        JList<String> listaUsuarios = new JList<>(nombresUsuariosModel);

        // Agregar la lista a un JScrollPane para hacerla desplazable
        JScrollPane scrollPane = new JScrollPane(listaUsuarios);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Agregar un MouseListener para manejar el doble clic en un usuario
        listaUsuarios.addMouseListener(new MouseAdapter() {
            @Override
public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2) {
        int index = listaUsuarios.locationToIndex(e.getPoint());
        if (index >= 0) {
            String usuarioSeleccionado = nombresUsuariosModel.getElementAt(index);
            // Seguir al usuario y actualizar las publicaciones en la ventana principal
            ventanaPrincipal.seguirUsuario(usuarioSeleccionado);
            ventanaPrincipal.actualizarPublicacionesDespuesSeguir(); // Nuevo método para actualizar publicaciones
            ventanaPrincipal.actualizarUI();
            dispose(); // Cerrar la ventana de selección de usuarios
        }
    }
}
        });

        add(panelPrincipal);
    }

    
}
