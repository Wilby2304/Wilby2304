package com.redsocial;

import java.util.Stack;

public class PilaPublicaciones {
    private Stack<Publicacion> pilaPublicaciones;

    public PilaPublicaciones() {
        pilaPublicaciones = new Stack<>();
    }

    public void agregarPublicacion(Publicacion publicacion) {
        pilaPublicaciones.push(publicacion);
    }

    public Publicacion eliminarUltimaPublicacion() {
        return pilaPublicaciones.pop();
    }

    public Publicacion verUltimaPublicacion() {
        return pilaPublicaciones.peek();
    }

    public int cantidadPublicaciones() {
        return pilaPublicaciones.size();
    }
}
