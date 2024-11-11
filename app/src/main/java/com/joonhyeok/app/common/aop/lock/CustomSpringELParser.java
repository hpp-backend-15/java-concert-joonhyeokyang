package com.joonhyeok.app.common.aop.lock;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;

/**
 * Spring Expression Language Parser
 */
public class CustomSpringELParser {
    private CustomSpringELParser() {
    }

    public static Object getDynamicValue(String[] parameterNames, Object[] args, String key) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 첫 번째 파라미터가 객체일 경우 그 객체의 필드를 가져옵니다.
        if (args.length > 0 && args[0] != null) {
            Object paramObject = args[0];
            for (Field field : paramObject.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    context.setVariable(field.getName(), field.get(paramObject));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }

        }
        return parser.parseExpression(key).getValue(context, Object.class);
    }
}