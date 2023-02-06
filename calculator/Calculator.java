package calculator;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;

public class Calculator extends JFrame {
    private static final int NUM_BUTTON_WIDTH = 70;
    private static final int NUM_BUTTON_HEIGHT = 50;
    private static final int RESULT_BOX_HEIGHT = 100;
    private static final int INPUT_BOX_START_X = 30;
    private static final int INPUT_BOX_START_Y = 0;
    private static final int INPUT_BOX_HEIGHT = 40;
    private static final int NUM_BUTTON_START_X = 30;
    private static final int NUM_BUTTON_GAP_X = 10;
    private static final int NUM_BUTTON_GAP_Y = 10;
    private static final char ADD = '\u002B';
    private static final char MINUS = '-';
    private static final char TIMES = '\u00D7';
    private static final char DIVIDE = '\u00F7';
    private static final String DEFAULT_RESULT = "0";
    private static final String DEFAULT_EQUATION = "";
    private static final Font RESULT_FONT = new Font("Cordia New", Font.BOLD, 40);
    private static final Font EQUATION_FONT = new Font("Cordia New", Font.BOLD, 16);
    private static final Font KEYPAD_FONT = new Font("Cordia New", Font.PLAIN, 18);
    private static final int NUM_BUTTON_START_Y;
    private static final int WINDOW_WIDTH;
    private static final int WINDOW_HEIGHT;
    private static int INPUT_BOX_WIDTH;
    private static String[] NUMBERS;
    private JLabel resultLabel;
    private JLabel equationLabel;

    static {
        NUM_BUTTON_START_Y = INPUT_BOX_START_Y + RESULT_BOX_HEIGHT +
                INPUT_BOX_HEIGHT + NUM_BUTTON_GAP_Y * 6 + NUM_BUTTON_HEIGHT * 3;
        WINDOW_WIDTH = NUM_BUTTON_START_X * 2 + NUM_BUTTON_WIDTH * 4 +
                NUM_BUTTON_GAP_X * 3;
        WINDOW_HEIGHT = RESULT_BOX_HEIGHT + INPUT_BOX_HEIGHT*2 + INPUT_BOX_START_Y * 2 +
                NUM_BUTTON_HEIGHT * 6 + NUM_BUTTON_GAP_Y * 5;
        INPUT_BOX_WIDTH = WINDOW_WIDTH - INPUT_BOX_START_X * 2;
        NUMBERS = new String[]{ "Zero", "One", "Two", "Three", "Four",
                "Five", "Six", "Seven", "Eight", "Nine" };
    }
    private void enterInput(String input) {
        equationLabel.setText(equationLabel.getText() + input);
    }

    private boolean isMathOperator(char ch) {
        return ch == ADD || ch == MINUS || ch == TIMES || ch == DIVIDE;
    }
    private int operatorPrecedence(char op) {
        switch (op) {
            case ADD, MINUS -> { return 1; }
            case TIMES, DIVIDE -> { return 2; }
            default -> { return 0; }
        }
    }
    private boolean isHigherPrecedence(char left, char right) {
        return operatorPrecedence(left) >= operatorPrecedence(right);
    }

    private int nextOperandEndPosition(String infix, int i) {
        while (i < infix.length() && !isMathOperator(infix.charAt(i))) {
            i++;
        }
        return i;
    }
    private String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Deque<Character> opStack = new ArrayDeque<>();
        String operand;

        int i = 0;
        while (i < infix.length()) {
            int j = nextOperandEndPosition(infix, i);
            operand = infix.substring(i, j);
            postfix.append(operand).append(" ");
            if (j < infix.length()) {
                char ch = infix.charAt(j);
                i = j + 1;
                while (!opStack.isEmpty() && isHigherPrecedence(opStack.peekFirst(), ch)) {
                    // add the operators
                    postfix.append(opStack.removeFirst()).append(" ");
                }
                opStack.push(ch);
            } else {
                break;
            }
        }
        while (!opStack.isEmpty()) {
            postfix.append(opStack.removeFirst()).append(" ");
        }
        return postfix.toString();
    }

    private Double evaluatePostfix(String postfix) {
        String[] expr = postfix.split(" ");
        Deque<Double> values = new ArrayDeque<>();
        for (int i = 0; i < expr.length; i++) {
            char ch = expr[i].charAt(0);
            if (isMathOperator(ch)) {
                double right = values.pop();
                double left = values.pop();
                double v = 0;
                switch (ch) {
                    case ADD -> { v = left + right; }
                    case MINUS -> { v = left - right; }
                    case TIMES -> { v = left * right; }
                    case DIVIDE -> { v = left / right; }
                    default -> { }
                }
                values.push(v);
            } else {
                // operand
                Double x = Double.parseDouble(expr[i]);
                values.push(x);
            }
        }
        return values.pop();
    }

    private void calculateResult(JLabel equationLabel) {
        String expression = equationLabel.getText();
        String postfix = infixToPostfix(expression);
        Double result = evaluatePostfix(postfix);
        if (result != null) {
            if (result % 1 == 0.0) {
                resultLabel.setText(String.valueOf(result.intValue()));
            } else {
                resultLabel.setText(result.toString());
            }
        }
    }

    private JButton addKeypad(int x, int y, String keyLabel, String name) {
        JButton keyButton = new JButton(keyLabel);
        keyButton.setBounds(x, y, NUM_BUTTON_WIDTH, NUM_BUTTON_HEIGHT);
        keyButton.setName(name);
        keyButton.setFont(KEYPAD_FONT);
        add(keyButton);
        return keyButton;
    }

    private void createNumPad() {
        ArrayList<JButton> numberKeys = new ArrayList<>(15);
        for (Integer number = 1; number <= 9; number++) {
            int x = NUM_BUTTON_START_X + ((number - 1) % 3) * (NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X);
            int y = NUM_BUTTON_START_Y - ((number - 1) / 3) * (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
            numberKeys.add(addKeypad(x, y, number.toString(), NUMBERS[number]));
        }

        int x = NUM_BUTTON_START_X;
        int y = NUM_BUTTON_START_Y + NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y;
        numberKeys.add(addKeypad(x, y, ".", "Dot"));

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        numberKeys.add(addKeypad(x, y, "0", NUMBERS[0]));

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        JButton equalButton = addKeypad(x, y, "=", "Equals");

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        numberKeys.add(addKeypad(x, y, Character.toString(MINUS), "Subtract"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        numberKeys.add(addKeypad(x, y, Character.toString(ADD), "Add"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        numberKeys.add(addKeypad(x, y, Character.toString(TIMES), "Multiply"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        numberKeys.add(addKeypad(x, y, Character.toString(DIVIDE), "Divide"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        JButton deleteButton = addKeypad(x, y, "Del", "Delete");
        deleteButton.addActionListener(e -> {
            String currentEq = equationLabel.getText();
            if (!currentEq.isEmpty()) {
                String deleted = currentEq.substring(0, equationLabel.getText().length() - 1);
                equationLabel.setText(deleted);
            }
        });

        x -= NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        JButton clearButton = addKeypad(x, y, "C", "Clear");
        clearButton.addActionListener(e -> {
            equationLabel.setText(DEFAULT_EQUATION);
            resultLabel.setText(DEFAULT_RESULT);
        });

        for (int i = 0; i < numberKeys.size(); i++)
            numberKeys.get(i).addActionListener(e -> {
                String input = ((JButton) (e.getSource())).getText();
                enterInput(input);
            });
        equalButton.addActionListener(e -> {
            calculateResult(equationLabel);
        });
    }

    private void createResultLabel() {
        resultLabel = new JLabel(DEFAULT_RESULT, SwingConstants.RIGHT);
        resultLabel.setName("ResultLabel");
        resultLabel.setBounds(INPUT_BOX_START_X, INPUT_BOX_START_Y, INPUT_BOX_WIDTH, RESULT_BOX_HEIGHT);
        resultLabel.setFont(RESULT_FONT);
        add(resultLabel);
    }

    private void createEquationLabbel() {
        equationLabel = new JLabel(DEFAULT_EQUATION, SwingConstants.RIGHT);
        equationLabel.setName("EquationLabel");
        equationLabel.setBounds(INPUT_BOX_START_X, INPUT_BOX_START_Y+RESULT_BOX_HEIGHT, INPUT_BOX_WIDTH, INPUT_BOX_HEIGHT);
        equationLabel.setFont(EQUATION_FONT);
        equationLabel.setForeground(Color.BLUE);
        add(equationLabel);
    }

    private void initializeUI() {
        createNumPad();
        createResultLabel();
        createEquationLabbel();
    }
    public Calculator() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setLayout(null);
        setTitle("Calculator");

        initializeUI();
        setVisible(true);
    }
}
