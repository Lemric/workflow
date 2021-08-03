package io.brandoriented.workflow;

import java.util.ArrayList;

public class Transition {
    private final String name;
    private final ArrayList<PlaceInterface> froms;
    private final ArrayList<PlaceInterface> tos;

    public Transition(String name, ArrayList<PlaceInterface> froms, ArrayList<PlaceInterface> tos) {
        this.name = name;
        this.froms = froms;
        this.tos = tos;
    }

    public String getName() {
        return name;
    }

    public ArrayList<PlaceInterface> getFroms() {
        return froms;
    }

    public ArrayList<PlaceInterface> getTos() {
        return tos;
    }
}
