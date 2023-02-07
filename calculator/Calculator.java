package calculator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventObject;

public class Calculator extends JFrame {
    private static final int NUM_BUTTON_WIDTH = 100;
    private static final int NUM_BUTTON_HEIGHT = 60;
    private static final int RESULT_BOX_HEIGHT = 100;
    private static final int INPUT_BOX_START_X = 30;
    private static final int INPUT_BOX_START_Y = 0;
    private static final int INPUT_BOX_HEIGHT = 40;
    private static final int NUM_BUTTON_START_X = 10;
    private static final int NUM_BUTTON_GAP_X = 0;
    private static final int NUM_BUTTON_GAP_Y = 0;
    public static final char ADD = '\u002B';
    public static final char MINUS = '-';
    public static final char TIMES = '\u00D7';
    public static final char DIVIDE = '\u00F7';
    public static final char SQRT = '\u221A';
    public static final char EXPONENT = '^';

    public static final char LEFT_PARENTHESIS = '(';
    public static final char RIGHT_PARENTHESIS = ')';

    private static final String DEFAULT_RESULT = "0";
    private static final String DEFAULT_EQUATION = "";
    private static final Font RESULT_FONT = new Font("Cordia New", Font.BOLD, 42);
    private static final Font EQUATION_FONT = new Font("Cordia New", Font.BOLD, 16);
    private static final Font KEYPAD_FONT = new Font("Cordia New", Font.PLAIN, 18);
    private static final int NUM_BUTTON_START_Y;
    private static final int WINDOW_WIDTH;
    private static final int WINDOW_HEIGHT;
    private static final int INPUT_BOX_WIDTH;
    private static final String[] NUMBERS;
    private JLabel resultLabel;
    private JLabel equationLabel;

    static {
        NUM_BUTTON_START_Y = INPUT_BOX_START_Y + RESULT_BOX_HEIGHT + INPUT_BOX_HEIGHT + NUM_BUTTON_HEIGHT * 4;
        WINDOW_WIDTH = NUM_BUTTON_START_X * 2 + NUM_BUTTON_WIDTH * 4;
        WINDOW_HEIGHT = NUM_BUTTON_START_Y + NUM_BUTTON_HEIGHT * 3;
        INPUT_BOX_WIDTH = WINDOW_WIDTH - INPUT_BOX_START_X * 2;
        NUMBERS = new String[]{ "Zero", "One", "Two", "Three", "Four",
                "Five", "Six", "Seven", "Eight", "Nine" };
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
    private void addKeyToEquation(String input) {
        equationLabel.setText(equationLabel.getText() + input);
        equationLabel.setForeground(Color.GREEN.darker());
    }

    private String setEquation(String equation) {
        equationLabel.setText(equation);
        equationLabel.setForeground(Color.GREEN.darker());
        return equationLabel.getText();
    }

    private JButton addKeypad(int x, int y, String keyLabel, String name) {
        JButton keyButton = new JButton(keyLabel);
        keyButton.setBounds(x, y, NUM_BUTTON_WIDTH, NUM_BUTTON_HEIGHT);
        keyButton.setName(name);
        keyButton.setFont(KEYPAD_FONT);
        add(keyButton);
        return keyButton;
    }

    private String getJButtonText(EventObject e) {
        return ((JButton) (e.getSource())).getText();
    }

    private void createNumPad() {
        ArrayList<JButton> numberKeys = new ArrayList<>();
        ArrayList<JButton> operatorKeys = new ArrayList<>();

        for (int number = 1; number <= 9; number++) {
            int x = NUM_BUTTON_START_X + ((number - 1) % 3) * (NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X);
            int y = NUM_BUTTON_START_Y - ((number - 1) / 3) * (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
            numberKeys.add(addKeypad(x, y, Integer.toString(number), NUMBERS[number]));
        }

        int x = NUM_BUTTON_START_X;
        int y = NUM_BUTTON_START_Y + NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y;
        JButton plusMinusButton = addKeypad(x, y, Character.toString('\u00B1'), "PlusMinus");
        plusMinusButton.addActionListener(e -> {
            String equation = equationLabel.getText();
            if (equation.length() == 0) {
                addKeyToEquation(String.format("%c%c", LEFT_PARENTHESIS, MINUS));
            } else {
                int operandStart = EquationProcessor.lastOperandStartPosition(equation, equation.length() - 1);
                String operand = equation.substring(operandStart);
                if (EquationProcessor.isMathOperator(operand)) {
                    if (operandStart >= 1) {
                        if (equation.substring(operandStart - 1, operandStart + 1).equals(String.format("%c%c", LEFT_PARENTHESIS, MINUS))) {
                            setEquation(equation.substring(0, operandStart - 1) + equation.substring(operandStart + 1));
                        }
                    }
                } else if (operandStart >= 2) {
                    if (equation.substring(operandStart - 2, operandStart).equals(String.format("%c%c", LEFT_PARENTHESIS, MINUS))) {
                        setEquation(equation.substring(0, operandStart - 2) + equation.substring(operandStart));
                    } else {
                        setEquation(equation.substring(0, operandStart) +
                                String.format("%c%c", LEFT_PARENTHESIS, MINUS) + operand);
                    }
                } else {
                    setEquation(equation.substring(0, operandStart) +
                            String.format("%c%c", LEFT_PARENTHESIS, MINUS) + operand);
                }
            }
        });

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        numberKeys.add(addKeypad(x, y, "0", NUMBERS[0]));

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        numberKeys.add(addKeypad(x, y, ".", "Dot"));

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        JButton equalButton = addKeypad(x, y, "=", "Equals");

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        operatorKeys.add(addKeypad(x, y, Character.toString(ADD), "Add"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        operatorKeys.add(addKeypad(x, y, Character.toString(MINUS), "Subtract"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        operatorKeys.add(addKeypad(x, y, Character.toString(TIMES), "Multiply"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        operatorKeys.add(addKeypad(x, y, Character.toString(DIVIDE), "Divide"));

        x -= (NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X);
        JButton sqrtButton = addKeypad(x, y, Character.toString(SQRT), "SquareRoot");
        sqrtButton.addActionListener(e -> {
            String input = getJButtonText(e);
            addKeyToEquation(input + LEFT_PARENTHESIS);
        });

        x -= (NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X);
        JButton powerYButton = addKeypad(x, y, "X\u02B8", "PowerY");
        powerYButton.addActionListener(e -> addKeyToEquation(Character.toString(EXPONENT) + LEFT_PARENTHESIS));

        x -= (NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X);
        JButton powerTwoButton = addKeypad(x, y, "X\u00B2", "PowerTwo");
        powerTwoButton.addActionListener(e -> addKeyToEquation(EXPONENT + "(2)"));

        y -= (NUM_BUTTON_HEIGHT + NUM_BUTTON_GAP_Y);
        JButton parenthesesButton = addKeypad(x, y, "( )", "Parentheses");
        parenthesesButton.addActionListener(e -> {
            String currentEq = equationLabel.getText();
            if (currentEq.length() == 0) {
                addKeyToEquation(Character.toString(LEFT_PARENTHESIS));
            } else {
                char lastCharOfEq = currentEq.charAt(currentEq.length() - 1);
                if (EquationProcessor.matchedParentheses(currentEq) || lastCharOfEq == LEFT_PARENTHESIS ||
                        EquationProcessor.isMathOperator(lastCharOfEq)) {
                    addKeyToEquation(Character.toString(LEFT_PARENTHESIS));
                } else {
                    addKeyToEquation(Character.toString(RIGHT_PARENTHESIS));
                }
            }
        });

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        JButton clearEntryButton = addKeypad(x, y, "CE", "ClearEntry");
        clearEntryButton.addActionListener(e -> {
            equationLabel.setText(DEFAULT_EQUATION);
            resultLabel.setText(DEFAULT_RESULT);
        });

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        JButton clearButton = addKeypad(x, y, "C", "Clear");
        clearButton.addActionListener(e -> {
            equationLabel.setText(DEFAULT_EQUATION);
            resultLabel.setText(DEFAULT_RESULT);
        });

        x += NUM_BUTTON_WIDTH + NUM_BUTTON_GAP_X;
        JButton deleteButton = addKeypad(x, y, "Del", "Delete");
        deleteButton.addActionListener(e -> {
            String currentEq = equationLabel.getText();
            if (!currentEq.isEmpty()) {
                String deleted = currentEq.substring(0, equationLabel.getText().length() - 1);
                setEquation(deleted);
            }
        });

        for (JButton numberKey : numberKeys)
            numberKey.addActionListener(e -> {
                String input = getJButtonText(e);
                addKeyToEquation(input);
            });

        for (JButton opKey : operatorKeys)
            opKey.addActionListener(e -> {
                String operator = getJButtonText(e);
                String currentEq = equationLabel.getText();
                // No character should be added to the EquationLabel
                // if users try to enter an operator as the first character;
                if (EquationProcessor.isNotFirstCharacterOfEquation(currentEq)) {
                    if (EquationProcessor.isMathOperator(currentEq.charAt(currentEq.length() - 1))) {
                        // If two operators are inserted consecutively,
                        // then the second operator should replace the first;
                        currentEq = currentEq.substring(0, currentEq.length()-1);
                    }
                    currentEq += operator;
                    currentEq = EquationProcessor.reformatEquation(currentEq);
                    setEquation(currentEq);
                }
            });

        equalButton.addActionListener(e -> {
            String currentEq = equationLabel.getText();
            currentEq = setEquation(EquationProcessor.reformatEquation(currentEq));
            if (EquationProcessor.endsWithOperator(currentEq) || EquationProcessor.containsDivisionByZero(currentEq)) {
                equationLabel.setForeground(Color.RED.darker());
            } else {
                try {
                    String result = EquationProcessor.calculateResult(currentEq);
                    resultLabel.setText(result);
                } catch (NumberFormatException exp) {
                    equationLabel.setForeground(Color.RED.darker());
                }
            }
        });
    }

    private void createResultLabel() {
        resultLabel = new JLabel(DEFAULT_RESULT, SwingConstants.RIGHT);
        resultLabel.setName("ResultLabel");
        resultLabel.setBounds(INPUT_BOX_START_X, INPUT_BOX_START_Y, INPUT_BOX_WIDTH, RESULT_BOX_HEIGHT);
        resultLabel.setFont(RESULT_FONT);
        add(resultLabel);
    }
    private void createEquationLabel() {
        equationLabel = new JLabel(DEFAULT_EQUATION, SwingConstants.RIGHT);
        equationLabel.setName("EquationLabel");
        equationLabel.setBounds(INPUT_BOX_START_X, INPUT_BOX_START_Y+RESULT_BOX_HEIGHT, INPUT_BOX_WIDTH, INPUT_BOX_HEIGHT);
        equationLabel.setFont(EQUATION_FONT);
        equationLabel.setForeground(Color.GREEN.darker());
        add(equationLabel);
    }
    private void initializeUI() {
        createNumPad();
        createResultLabel();
        createEquationLabel();
    }
}
