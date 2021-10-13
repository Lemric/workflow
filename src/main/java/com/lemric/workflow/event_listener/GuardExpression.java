package com.lemric.workflow.event_listener;

import com.lemric.expressionlanguage.ExpressionLanguage;
import com.lemric.workflow.Registry;
import com.lemric.workflow.Transition;
import com.lemric.workflow.WorkflowContext;
import com.lemric.workflow.event.GuardEvent;
import com.lemric.workflow.exceptions.UnsupportedGuardEventException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuardExpression {
    private Transition transition;
    private String expression;
    private Registry workflowRegistry;
    private Object subjectManipulator;
    private ExpressionLanguage language;
    private Map<String, ArrayList<String>> supportedEventsConfig = new HashMap<>();
    protected final Logger logger = LogManager.getLogger(getClass().getName());

    public GuardExpression(Transition transition, String expression) {
        this.transition = transition;
        this.expression = expression;
    }

    public void guardTransition(GuardEvent event, String eventName){
        if (!this.supportedEventsConfig.containsKey(eventName)) {
            throw new UnsupportedGuardEventException(String.format("Cannot find registered guard event by name \"%s\"", eventName));
        }

        ArrayList<String> current = this.supportedEventsConfig.get(eventName);
        String workflowName = current.get(0);
        String expression = current.get(1);

        Object subject = event.getSubject();
        WorkflowContext workflowContext = new WorkflowContext(
                this.workflowRegistry.get(subject, workflowName),
                subject,
                this.subjectManipulator.getSubjectId(subject)
        );
        HashMap<String, String> loggerContext = workflowContext.getLoggerContext();

        String errorMessage      = null;
        Object expressionResult;
        try {
            expressionResult = this.language.evaluate(expression, new HashMap<>() {{
                put("event", event);
            }});
        } catch (Throwable e) {
            errorMessage = String.format(
                    "Guard expression \"%s\" for guard event \"%s\" cannot be evaluated. Details: \"%s\"",
                    expression,
                    eventName,
                    e.getMessage()
            );
            this.logger.error(errorMessage, loggerContext);

            // simply skipping processing here without blocking transition
            return;
        }

        if (expressionResult instanceof Boolean) {
            this.logger.debug(
                    String.format("Guard expression \"%s\" for guard event \"%s\" evaluated with non-boolean result" +
                            " and will be converted to boolean", expression, eventName),
                    loggerContext
            );

            expressionResult = (Boolean) expressionResult;
        }

        event.setBlocked((Boolean) expressionResult);

        if ((Boolean) expressionResult) {
            this.logger.debug(
                    String.format("Transition \"%s\" is blocked by guard expression \"%s\" for guard event \"%s\"",
                            event.getTransition().getName(),
                    expression,
                    eventName
                ),
            loggerContext
            );
        }
    }

    public Transition getTransition() {
        return this.transition;
    }

    public String getExpression() {
        return this.expression;
    }
}
