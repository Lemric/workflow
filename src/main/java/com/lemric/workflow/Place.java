/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow;

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
