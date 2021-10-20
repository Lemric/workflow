package com.lemric.workflow;

import com.lemric.eventdispatcher.exceptions.InvalidArgumentException;
import com.lemric.workflow.supportStrategy.WorkflowSupportStrategyInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Registry {
    private List<List<Object>> workflows = new ArrayList<>();

    public void addWorkflow(WorkflowInterface workflow, WorkflowSupportStrategyInterface supportStrategy) {
        this.workflows.add(new ArrayList<>() {{
            add(workflow);
            add(supportStrategy);
        }});
    }

    public boolean has(Object subject) {
        return this.has(subject, null);
    }

    public boolean has(Object subject, String workflowName) {
        for (List<Object> entry : this.workflows) {
            if (this.supports((WorkflowInterface) entry.get(0), (WorkflowSupportStrategyInterface) entry.get(1), subject, workflowName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return Workflow
     */
    public WorkflowInterface get(Object subject) throws InvalidArgumentException {
        return this.get(subject, null);
    }

    public WorkflowInterface get(Object subject, String workflowName) throws InvalidArgumentException {
        List<WorkflowInterface> matched = new ArrayList<>();
        for (List<Object> entry : this.workflows) {
            if (this.supports((WorkflowInterface) entry.get(0), (WorkflowSupportStrategyInterface) entry.get(1), subject, workflowName)) {
                matched.add((WorkflowInterface) entry.get(0));
            }
        }

        if (matched.isEmpty()) {
            throw new InvalidArgumentException(String.format("Unable to find a workflow for class \"%s\".", subject.getClass().getTypeName()));
        }

        if (2 <= matched.size()) {

            String names = matched.stream().map(WorkflowInterface::getName)
                    .collect(Collectors.joining(", "));
            throw new InvalidArgumentException(String.format("Too many workflows (%s) match this subject (%s); set a different name on each and use the second (name) argument of this method.", names, subject.getClass().getTypeName()));
        }

        return matched.get(0);
    }

    /**
     * @return Workflow[]
     */
    public List<WorkflowInterface> all(Object subject) {
        List<WorkflowInterface> matched = new ArrayList<>();
        for (List<Object> entry : this.workflows) {
            if (this.supports((WorkflowInterface) entry.get(0), (WorkflowSupportStrategyInterface) entry.get(1), subject)) {
                matched.add((WorkflowInterface) entry.get(0));
            }
        }

        return matched;
    }

    private boolean supports(WorkflowInterface workflow, WorkflowSupportStrategyInterface supportStrategy, Object subject) {
        return this.supports(workflow, supportStrategy, subject, null);
    }

    private boolean supports(WorkflowInterface workflow, WorkflowSupportStrategyInterface supportStrategy, Object subject, String workflowName) {
        if (null != workflowName && !workflowName.equals(workflow.getName())) {
            return false;
        }

        return supportStrategy.supports(workflow, subject);
    }
}
