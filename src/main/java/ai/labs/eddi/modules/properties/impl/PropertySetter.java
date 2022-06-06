package ai.labs.eddi.modules.properties.impl;

import ai.labs.eddi.models.Property;
import ai.labs.eddi.models.SetOnActions;
import ai.labs.eddi.modules.nlp.expressions.Expression;
import ai.labs.eddi.modules.nlp.expressions.Expressions;
import ai.labs.eddi.modules.nlp.expressions.value.Value;
import ai.labs.eddi.modules.properties.IPropertySetter;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static ai.labs.eddi.models.Property.Scope.conversation;

/**
 * @author ginccc
 */

public class PropertySetter implements IPropertySetter {
    private static final String PROPERTY_EXPRESSION = "property";

    @Getter
    private final List<SetOnActions> setOnActionsList;

    public PropertySetter(List<SetOnActions> setOnActionsList) {
        this.setOnActionsList = setOnActionsList;
    }

    @Override
    public List<Property> extractProperties(Expressions expressions) {
        return expressions.stream().
                filter(expression ->
                        PROPERTY_EXPRESSION.equals(expression.getExpressionName()) &&
                                expression.getSubExpressions().length > 0).
                map(expression -> {
                    List<String> meanings = new LinkedList<>();
                    Value value = new Value();
                    extractMeanings(meanings, value, expression.getSubExpressions()[0]);

                    var propertyName = String.join(".", meanings);
                    if (value.isNumeric()) {
                        if (value.isDouble()) {
                            return new Property(propertyName, value.toFloat(), conversation);
                        } else {
                            return new Property(propertyName, value.toInteger(), conversation);
                        }
                    } else {
                        return new Property(propertyName, value.getExpressionName(), conversation);
                    }

                }).collect(Collectors.toCollection(LinkedList::new));
    }

    private void extractMeanings(List<String> meanings, Value value, Expression expression) {
        if (expression.getSubExpressions().length > 0) {
            meanings.add(expression.getExpressionName());
            extractMeanings(meanings, value, expression.getSubExpressions()[0]);
        } else {
            value.setExpressionName(expression.getExpressionName());
        }
    }
}
