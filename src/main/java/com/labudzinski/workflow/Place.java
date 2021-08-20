package com.labudzinski.workflow;

public class Place implements PlaceInterface {

    private String name = null;

    public Place(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
