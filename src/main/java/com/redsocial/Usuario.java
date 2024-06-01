package com.redsocial;

import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Usuario {
    private int id;
    private String nombre;
    private String nombreUsuario;
    private String contrasena;
    private LinkedList<Usuario> seguidos;
    private LinkedList<Usuario> seguidores;
    private LinkedList<Publicacion> publicaciones;

    public Usuario(String nombre, String nombreUsuario, String contrasena) {
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.seguidos = new LinkedList<>();
        this.seguidores = new LinkedList<>();
        this.publicaciones = new LinkedList<>();
    }

    public Usuario(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.seguidos = new LinkedList<>();
        this.seguidores = new LinkedList<>();
    }

    public void agregarSeguido(Usuario usuario) {
        seguidos.add(usuario);
    }

    public void eliminarSeguido(Usuario usuario) {
        seguidos.remove(usuario);
    }

    public void agregarSeguidor(Usuario usuario) {
        seguidores.add(usuario);
    }

    public void eliminarSeguidor(Usuario usuario) {
        seguidores.remove(usuario);
    }

    public LinkedList<Publicacion> getPublicaciones() {
        return publicaciones;
    }

    @Override
    public String toString() {
        return nombreUsuario; // Devuelve el nombre de usuario como representación en String del objeto
    }
}
