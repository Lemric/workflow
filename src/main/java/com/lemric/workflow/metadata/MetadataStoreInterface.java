/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow.metadata;

import com.lemric.workflow.PlaceInterface;
import com.lemric.workflow.Transition;

import java.util.ArrayList;

public interface MetadataStoreInterface {
    ArrayList getWorkflowMetadata();

    Object getPlaceMetadata(PlaceInterface place);

    Object getTransitionMetadata(Transition transition);

    void getMetadata(String key, Object ssubject);
}
