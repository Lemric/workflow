package com.lemric.workflow.supportStrategy;

import com.lemric.workflow.WorkflowInterface;

public interface WorkflowSupportStrategyInterface {
    boolean supports(WorkflowInterface workflow, Object subject);
}
