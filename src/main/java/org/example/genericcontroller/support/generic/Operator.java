package org.example.genericcontroller.support.generic;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.genericcontroller.exception.generic.OperatorNotSupportException;
import org.example.genericcontroller.exception.generic.OperatorPatternInvalidException;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Operator.
 *
 * @author hungp
 */
@Data
@AllArgsConstructor
public class Operator {

    public static final String PATTERN = "\\[(.*)\\](.*)";
    public static final List<String> OPERATOR_SUPPORT = Arrays.asList(">=", "<=", "==", "!=", ">", "<");

    private String operator;

    private String value;

    /**
     * Parse value to Operator.
     *
     * @param value value
     * @return Operator instance
     */
    public static Operator parse(String value) {
        if (!StringUtils.isEmpty(value)) {
            Matcher matcher = Pattern.compile("\\[(.*)\\](.*)").matcher(value);
            if (matcher.matches() && matcher.groupCount() == 2) {
                String operator = matcher.group(1);
                if (!OPERATOR_SUPPORT.contains(operator)) {
                    throw new OperatorNotSupportException("Don't support operator '" + operator + "'");
                }
                return new Operator(operator, matcher.group(2));
            } else {
                throw new OperatorPatternInvalidException("The value '" + value + "' is'n match with pattern " + PATTERN);
            }
        }
        return null;
    }
}
