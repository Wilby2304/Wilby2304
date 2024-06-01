package com.redsocial;

import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Publicacion {
    private int id;
    private String contenido;
    private Usuario usuario;
    private Date fecha;

    private LinkedList<Comentario> comentarios;
    private LinkedList<Like> likes;

    // todo falta ver que hacer con el int id
    public Publicacion(int id,String contenido, Usuario usuario, Date fecha) {
        this.id = id;
        this.contenido = contenido;
        this.usuario = usuario;
        this.fecha = fecha;
        this.comentarios = new LinkedList<>();  // Inicializa la lista de comentarios
        this.likes = new LinkedList<>();  // Inicializa la lista de likes
    }

    public Publicacion(String contenido, Usuario usuario, Date fecha) {
        this(0, contenido, usuario, fecha);  // Llama al otro constructor con un ID predeterminado de 0
    }
    // Constructores, getters y setters
    // ...

    public void agregarComentario(Comentario comentario) {
        comentarios.add(comentario);
    }

    public void eliminarComentario(Comentario comentario) {
        comentarios.remove(comentario);
    }

    public LinkedList<Comentario> getComentarios() {
        return comentarios;
    }

    public void agregarLike(Like like) {
        likes.add(like);
    }

    public void eliminarLike(Like like) {
        likes.remove(like);
    }

    public LinkedList<Like> getLikes() {
        return likes;
    }

    @Override
    public String toString() {
        return usuario.getNombreUsuario() + ": " + contenido + " - " + fecha;
    }

    //que verifique solo la fecha
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Publicacion otraPublicacion = (Publicacion) obj;
        return
            Objects.equals(id, otraPublicacion.id);
    }

}
