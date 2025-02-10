package com.xubop961.niamniamapp;


public class Recetas {
    private String nombre;
    private int imagen;

    public Recetas(String nombre, int imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public int getImagen() {
        return imagen;
    }
}
