package com.lemric.workflow.supportStrategy;

import com.lemric.workflow.WorkflowInterface;

import java.util.Objects;

public class InstanceOfSupportStrategy implements WorkflowSupportStrategyInterface {
    private String className;

    public InstanceOfSupportStrategy(Class<?> className) {
        this.className = className.getName();
    }

    @Override
    public boolean supports(WorkflowInterface workflow, Object subject) {
        String subjectClass = subject.getClass().getName();
        boolean ex = this.getClassName() == subjectClass;
        return Objects.equals(this.getClassName(), subjectClass);
    }

    public String getClassName() {
        return className;
    }
}
