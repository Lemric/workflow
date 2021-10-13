package com.lemric.workflow;

import java.util.HashMap;

public class WorkflowContext {
    private Workflow workflow;
    private Object subject;
    private String subjectId;

    public WorkflowContext(Workflow workflow, Object subject, String subjectId) {

        this.workflow = workflow;
        this.subject = subject;
        this.subjectId = subjectId;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public Object getSubject() {
        return subject;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public HashMap<String, String> getLoggerContext() {
        return new HashMap<String, String>() {{
            put("workflow", workflow.getName());
            put("class", subject.getClass().getName());
            put("id", subjectId);
        }};
    }
}
