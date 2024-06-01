package com.redsocial;

import java.util.Date;
import java.util.Objects;

public class Comentario {
    private int id;
    private String contenido;
    private Usuario usuario;
    private Date fecha;

    public Comentario(int id, String contenido, Usuario usuario, Date fecha) {
        this.id = id;
        this.contenido = contenido;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public Comentario(String contenido, Usuario usuario, Date fecha) {
        this.contenido = contenido;
        this.usuario = usuario;
        this.fecha = fecha;
    }


    public int getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    @Override
    public String toString() {
        return usuario.getNombreUsuario() + ": " + contenido + " - " + fecha;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Comentario comentario = (Comentario) obj;
        return Objects.equals(id, comentario.id) &&
                Objects.equals(contenido, comentario.contenido);

        // Objects.equals(fecha, comentario.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contenido);
        // return Objects.hash(contenido, usuario, fecha);
    }

}
