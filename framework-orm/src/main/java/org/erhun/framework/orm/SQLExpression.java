package org.erhun.framework.orm;

/**
 * @author gorilla
 */
public enum SQLExpression {

    /**
     * 等于
     */
    EQUAL("="),

    /**
     * 不等于
     */
    NOT_EQUAL("<>"),

    /**
     * 模糊
     */
    LIKE("like"),
    /**
     * 前缀模糊
     */
    START_LIKE("like"),
    /**
     * 后缀模糊
     */
    END_LIKE("like"),
    /**
     * NOT_LIKE
     */
    NOT_LIKE("like"),
    /**
     * 前缀模糊
     */
    START_NOT_LIKE("like"),
    /**
     * 后缀模糊
     */
    END_NOT_LIKE("like"),
    /**
     * 大于
     */
    GREET(">"),
    /**
     * 大于等于
     */
    GREET_EQUAL(">="),
    /**
     * 小于
     */
    LESS("<"),
    /**
     * 小于等于
     */
    LESS_EQUAL("<="),

    /**
     * IN
     */
    IN("in"),

    NOT_IN("not in"),

    IS_NULL("is null"),

    IS_NOT_NULL("is not null"),

    BETEEN("between");

    private String operatorSymbol;

    SQLExpression(String operatorSymbol){
        this.operatorSymbol = operatorSymbol;
    }

    public String getOperatorSymbol() {
        return operatorSymbol;
    }

    public static String parse(SQLExpression c, String value) {
        String expr;
        switch (c) {
            case EQUAL: {
                expr = "=" + value;
                break;
            }
            case NOT_EQUAL: {
                expr = "!" + value;
                break;
            }
            case LIKE: {
                expr = "*" + value + "*";
                break;
            }
            case START_LIKE: {
                expr = "*" + value;
                break;
            }
            case END_LIKE: {
                expr = value + "*";
                break;
            }
            case NOT_LIKE: {
                expr = "!*" + value + "*";
                break;
            }
            case START_NOT_LIKE: {
                expr = "!*" + value;
                break;
            }
            case END_NOT_LIKE: {
                expr = "!" + value + "*";
                break;
            }
            case GREET: {
                expr = ">" + value;
                break;
            }
            case GREET_EQUAL: {
                expr = ">=" + value;
                break;
            }
            case LESS: {
                expr = "<" + value;
                break;
            }
            case LESS_EQUAL: {
                expr = " <= " + value;
                break;
            }
            case IN:
            case NOT_IN: {
                expr = value;
                break;
            }
            default: {
                expr = "=" + value;
            }
        }
        return expr;
    }
}
