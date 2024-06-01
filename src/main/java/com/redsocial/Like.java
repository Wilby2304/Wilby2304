package com.redsocial;

import java.util.Date;
import java.util.Objects;

public class Like {
    private Usuario usuario;
    private Date fecha;

    public Like(Usuario usuario, Date fecha) {
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Like otroLike = (Like) obj;
        return Objects.equals(usuario, otroLike.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario);
    }
}
