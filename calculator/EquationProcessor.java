package calculator;

import java.util.ArrayDeque;
import java.util.Deque;

public class EquationProcessor {
    public  static String reformatEquation(String equation) {
        // scan numbers starting with decimal point
        // insert leading zero
        int i = 0;
        StringBuilder formattedEq = new StringBuilder();
        while (i < equation.length()) {
            int decimalPoint = equation.indexOf(".", i);
            if (decimalPoint != -1) {
                formattedEq.append(equation, i, decimalPoint);
                if (decimalPoint == 0 || isMathOperator(equation.charAt(decimalPoint - 1))) {
                    // insert leading zero
                    formattedEq.append("0.");
                } else if (decimalPoint < equation.length() &&
                        isMathOperator(equation.charAt(decimalPoint + 1))) {
                    formattedEq.append(".0");
                } else {
                    formattedEq.append(".");
                }
                i = decimalPoint + 1;
            } else {
                formattedEq.append(equation.substring(i));
                i = equation.length();
            }
        }
        return formattedEq.toString();
    }
    public static boolean isHigherPrecedence(char left, char right) {
        return operatorPrecedence(left) >= operatorPrecedence(right);
    }
    public static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Deque<Character> opStack = new ArrayDeque<>();
        String operand;
        boolean negation = false;

        int i = 0;
        while (i < infix.length()) {
            char currentChar = infix.charAt(i);
            if (currentChar == Calculator.LEFT_PARENTHESIS) {
                opStack.push(Calculator.LEFT_PARENTHESIS);
                i++;
                if (infix.charAt(i) == Calculator.MINUS) {
                    negation = true;
                    i++;
                }
            } else if (currentChar == Calculator.EXPONENT) {
                opStack.push(Calculator.EXPONENT);
                i++;
            } else if (currentChar == Calculator.SQRT) {
                opStack.push(Calculator.SQRT);
                i++;
            } else if (currentChar == Calculator.RIGHT_PARENTHESIS) {
                while (!opStack.isEmpty() && opStack.peekFirst() != Calculator.LEFT_PARENTHESIS) {
                    postfix.append(opStack.pop()).append(" ");
                }
                opStack.pop(); // pop the LEFT_PARENTHESIS
                if (!opStack.isEmpty() && opStack.peekFirst() == Calculator.SQRT) {
                    postfix.append(opStack.pop()).append(" ");
                }
                i++;
            } else {
                int j = nextOperandEndPosition(infix, i);
                operand = infix.substring(i, j);
                if (negation) {
                    operand = String.format("%c%s", Calculator.MINUS, operand);
                    negation = false;
                }
                postfix.append(operand).append(" ");
                if (j < infix.length()) {
                    char ch = infix.charAt(j);
                    while (!opStack.isEmpty() && isNotParentheses(opStack.peekFirst()) && isHigherPrecedence(opStack.peekFirst(), ch)) {
                        // add the operators
                        postfix.append(opStack.pop()).append(" ");
                    }
                    if (ch != Calculator.RIGHT_PARENTHESIS) {
                        opStack.push(ch);
                        i = j + 1;
                    } else {
                        i = j;
                    }
                } else {
                    break;
                }
            }
        }
        while (!opStack.isEmpty()) {
            postfix.append(opStack.removeFirst()).append(" ");
        }
        System.out.println(postfix);
        return postfix.toString().trim().replaceAll("\\s{2,}", " ");
    }
    public static String calculateResult(String expression) throws NumberFormatException {
        if (expression.length() == 0) return null;

        String postfix = infixToPostfix(expression);
        Double result = evaluatePostfix(postfix);
        if (result != null) {
            if (result % 1 == 0.0) {
                return String.valueOf(result.intValue());
            } else {
                return result.toString();
            }
        } else {
            return null;
        }
    }
    public static boolean endsWithOperator(String equation) {
        if (equation.length() == 0) return false;
        char lastKey = equation.charAt(equation.length() - 1);
        return isMathOperator(lastKey);
    }
    public static boolean containsDivisionByZero(String equation) {
        boolean found = false;
        int i = 0;

        while (i < equation.length()) {
            int divIndex = equation.indexOf(Calculator.DIVIDE, i);
            if (divIndex == -1) {
                break;
            } else {
                if (equation.charAt(divIndex + 1) == '0') {
                    found = true;
                    break;
                }
            }
            i = divIndex + 1;
        }
        return found;
    }
    public static boolean isNotFirstCharacterOfEquation(String equation) {
        // 1. An equation cannot begin with an operator
        return equation.length() != 0;
    }
    public static boolean isMathOperator(char ch) {
        return ch == Calculator.ADD || ch == Calculator.MINUS ||
                ch == Calculator.TIMES || ch == Calculator.DIVIDE ||
                ch == Calculator.SQRT || ch == Calculator.EXPONENT;
    }

    public static boolean isMathOperator(String ch) {
        return ch.length() == 1 && isMathOperator(ch.charAt(0));
    }

    public static boolean matchedParentheses(String equation) {
        int numLeft = 0;
        int numRight = 0;
        for (int i = 0; i < equation.length(); i++) {
            char ch = equation.charAt(i);
            if (ch == Calculator.LEFT_PARENTHESIS) {
                numLeft++;
            }
            if (ch == Calculator.RIGHT_PARENTHESIS) {
                numRight++;
            }
        }
        return numLeft == numRight;
    }
    public static int lastOperandStartPosition(String equation, int i) {
        boolean found = false;
        while (i > 0 && !isMathOperator(equation.charAt(i)) && isNotParentheses(equation.charAt(i))) {
            found = true;
            i--;
        }
        return found ? i + 1 : i;
    }
    private static int operatorPrecedence(char op) {
        switch (op) {
            case Calculator.SQRT -> { return 0; }
            case Calculator.ADD, Calculator.MINUS -> { return 1; }
            case Calculator.TIMES,  Calculator.DIVIDE -> { return 2; }
            case Calculator.EXPONENT -> { return 3; }
            default -> { return -1; }
        }
    }
    private static boolean isNotParentheses(char ch) {
        return ch != Calculator.LEFT_PARENTHESIS && ch != Calculator.RIGHT_PARENTHESIS;
    }
    private static int nextOperandEndPosition(String infix, int i) {
        while (i < infix.length() && !isMathOperator(infix.charAt(i)) && isNotParentheses(infix.charAt(i))) {
            i++;
        }
        return i;
    }
    private static Double evaluatePostfix(String postfix) throws NumberFormatException {
        String[] expr = postfix.split(" ");
        Deque<Double> values = new ArrayDeque<>();
        for (String s : expr) {
            if (isMathOperator(s)) {
                char ch = s.charAt(0);
                double right = values.pop();
                double left = 0;
                if (ch != Calculator.SQRT) {
                    left = values.pop();
                }
                double v = 0;
                switch (ch) {
                    case Calculator.ADD -> v = left + right;
                    case Calculator.MINUS -> v = left - right;
                    case Calculator.TIMES -> v = left * right;
                    case Calculator.DIVIDE -> v = left / right;
                    case Calculator.EXPONENT -> v = Math.pow(left, right);
                    case Calculator.SQRT -> v = Math.sqrt(right);
                    default -> {
                    }
                }
                if (Double.valueOf(v).isNaN()) {
                    throw new NumberFormatException();
                }
                values.push(v);
            } else {
                // operand
                Double x = Double.parseDouble(s);
                values.push(x);
            }
        }
        return values.pop();
    }


}
