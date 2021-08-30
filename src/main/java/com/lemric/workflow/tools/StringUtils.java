/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow.tools;

public class StringUtils {
    public static String ucfirst(String subject) {
        return Character.toUpperCase(subject.charAt(0)) + subject.substring(1);
    }

}
