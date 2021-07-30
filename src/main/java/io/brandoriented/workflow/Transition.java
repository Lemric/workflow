package io.brandoriented.workflow;

public class Transition {
    private final String name;
    private final String[] froms;
    private final String[] tos;

    public Transition(String name, String[] froms, String[] tos) {
        this.name = name;
        this.froms = froms;
        this.tos = tos;
    }

    public String getName() {
        return name;
    }

    public String[] getFroms() {
        return froms;
    }

    public String[] getTos() {
        return tos;
    }
}
