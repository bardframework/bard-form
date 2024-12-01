package org.bardframework.flow.processor;

import org.bardframework.flow.form.FormProcessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public abstract class FormProcessorAbstract implements FormProcessor {

    private Expression executeExpression = null;

    public void setExecuteExpression(String executeExpression) {
        this.executeExpression = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, null)).parseExpression(executeExpression);
    }

    @Override
    public boolean mustExecute(Map<String, Object> args) {
        return null == executeExpression || Boolean.TRUE.equals(executeExpression.getValue(new StandardEvaluationContext(args), Boolean.class));
    }
}
