/*
 * This file is part of the com.labudzinski package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.workflow;

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
