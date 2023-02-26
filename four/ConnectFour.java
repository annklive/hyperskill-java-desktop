package four;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class ConnectFour extends JFrame {
    private static final int NUM_ROW = 6;
    private static final int NUM_COL = 7;
    private static int currentTurn = 0;
    private static final ConnectFourBoard board = new ConnectFourBoard(NUM_ROW, NUM_COL);

    private static JButton createCellButton(String label, JPanel boardPanel) {
        JButton button = new JButton(ConnectFourBoard.unClickedCell);
        button.setName("Button" + label);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            JButton btn = (JButton) e.getSource();
            String btnName = btn.getName().split("Button")[1];
            int c = btnName.charAt(0) - 'A';
            int r = board.firstFreeRowInAColumn(c);
            int clickedRow = btnName.charAt(1) - '0' - 1;
            if (! board.isClickedCell(clickedRow, c)) {
                if (r != ConnectFourBoard.NO_FREE_CELL) {
                    if (currentTurn == 0) {
                        playOneTurn(r, c, ConnectFourBoard.xTurn);
                        currentTurn++;
                    } else {
                        playOneTurn(r, c, ConnectFourBoard.oTurn);
                        currentTurn = 0;
                    }
                }
                boardPanel.repaint();
            }
        });
        button.setBackground(ConnectFourBoard.BASELINE_COLOR);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        return button;
    }

    static void playOneTurn(int r, int c, String turn) {
        board.getCell(r, c).setText(turn);
        int boardState = board.checkState(r, c, turn);
        if (boardState == ConnectFourBoard.WINNING_STATE) {
            board.disableAllCells();
        }
    }
    private static JPanel initBoard() {
        JPanel boardPanel = new JPanel();
        GridLayout boardLayout = new GridLayout(NUM_ROW, NUM_COL);
        boardPanel.setLayout(boardLayout);
        for (int row = NUM_ROW-1; row >= 0; row--) {
            for (int j = 0; j < NUM_COL; j++) {
                char column = (char)('A' + (j));
                String label = String.format("%c%d", column, row + 1);
                JButton cellButton = createCellButton(label, boardPanel);
                boardPanel.add(cellButton);
                board.setCell(row, j, cellButton);
            }
        }
        return boardPanel;
    }

    private JPanel initCommandBar(JPanel boardPanel) {
        JPanel cmdPanel = new JPanel();
        FlowLayout layout = new FlowLayout();
        layout.setAlignment(FlowLayout.TRAILING);
        cmdPanel.setLayout(layout);

        JButton btnReset = new JButton("Reset");
        btnReset.setName("ButtonReset");
        btnReset.addActionListener(e -> {
            board.resetAllCells();
            currentTurn = 0;
            boardPanel.repaint();
        });
        cmdPanel.add(btnReset);
        return cmdPanel;
    }
    public ConnectFour() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setTitle("Connect Four");

        JPanel boardPanel = initBoard();
        JPanel commandPanel = initCommandBar(boardPanel);

        JPanel gamePanel = new JPanel();
        BorderLayout gameLayout = new BorderLayout();
        gamePanel.setLayout(gameLayout);
        gamePanel.add(boardPanel, BorderLayout.CENTER);
        gamePanel.add(commandPanel, BorderLayout.SOUTH);
        add(gamePanel);
        setVisible(true);
    }
}

class ConnectFourBoard {
    public static final String xTurn = "X";
    public static final String oTurn = "O";
    public static final String unClickedCell = " ";
    public static final int WINNING_STATE = 1;
    public static final int PLAYING_STATE = 0;
    public static final Color BASELINE_COLOR = Color.GRAY;
    public static final Color WINNING_COLOR = Color.CYAN;
    public static final int WINNING_CONNECTION_SIZE = 4;

    public static final int NO_FREE_CELL = -1;

    private final JButton[][] board;

    ConnectFourBoard(int numRow, int numCol) {
        board = new JButton[numRow][numCol];
    }

    JButton getCell(int row, int col) {
        return board[row][col];
    }

    void setCell(int row, int col, JButton btn) {
        board[row][col] = btn;
    }

    void resetAllCells() {
        for (JButton[] btnRow : board) {
            for (JButton cellBtn : btnRow) {
                cellBtn.setText(unClickedCell);
                cellBtn.setBackground(BASELINE_COLOR);
                cellBtn.setEnabled(true);
            }
        }

    }

    void disableAllCells() {
        for (JButton[] btnRow : board) {
            for (JButton cellBtn : btnRow) {
                cellBtn.setEnabled(false);
            }
        }
    }

    boolean notWinningState(ArrayList<String> connectedCells) {
        return connectedCells.size() != 4;
    }

    int boardState(ArrayList<String> connectedCells) {
        return connectedCells.size() == 4 ? WINNING_STATE : PLAYING_STATE;
    }

    int checkState(int r, int c, String turn) {
        ArrayList<String> connectedCells;

        connectedCells = checkHorizontalWinning(r, c, turn);
        if (notWinningState(connectedCells)) {
            connectedCells = checkVerticalWinning(r, c, turn);
        }
        if (notWinningState(connectedCells)) {
            connectedCells = checkLeftDiagonalWinning(r, c, turn);
        }
        if (notWinningState(connectedCells)) {
            connectedCells = checkRightDiagonalWinning(r, c, turn);
        }
        int boardState = boardState(connectedCells);
        if (boardState == WINNING_STATE) {
            updateConnectedFourCells(connectedCells);
        }
        return boardState;
    }

    ArrayList<String> checkLeftDiagonalWinning(int r, int c, String turn) {
        return connectFourLeftDiagonally(r, c, turn);
    }
    ArrayList<String> checkRightDiagonalWinning(int r, int c, String turn) {
        return connectFourRightDiagonally(r, c, turn);
    }
    ArrayList<String> checkVerticalWinning(int r, int c, String turn) {
        return connectFourVertically(r, c, turn);
    }
    ArrayList<String> checkHorizontalWinning(int r, int c, String turn) {
        return connectFourHorizontally(r, c, turn);
    }

    void updateConnectedFourCells(ArrayList<String> connectedCells) {
        if (connectedCells.size() > 0) {
            for (String cellCoordinate : connectedCells) {
                String[] coordinate = cellCoordinate.split(":");
                int highlightRow = Integer.parseInt(coordinate[0]);
                int highlightCol = Integer.parseInt(coordinate[1]);
                getCell(highlightRow, highlightCol)
                        .setBackground(ConnectFourBoard.WINNING_COLOR);
            }
        }
    }
    ArrayList<String> connectFourLeftDiagonally(int r, int c, String turn) {
        ArrayList<String> connectedCells = new ArrayList<>();
        int ul = 1;
        while (r+ul < board.length && c-ul >= 0 &&
                board[r+ul][c-ul].getText().equals(turn)) {
            ul++;
        }
        int dr = 1;
        while (r-dr >= 0 && c+dr < board[r].length &&
                board[r-dr][c+dr].getText().equals(turn)) {
            dr++;
        }
        if (ul + dr - 1 >= WINNING_CONNECTION_SIZE) {
            for (int i = 0; i < ul; i++) {
                connectedCells.add(String.format("%d:%d", r+i, c-i));
            }
            for (int j = 1; j < dr; j++) {
                connectedCells.add(String.format("%d:%d", r-j, c+j));
            }
        }

        return connectedCells;
    }
    ArrayList<String> connectFourRightDiagonally(int r, int c, String turn) {
        ArrayList<String> connectedCells = new ArrayList<>();
        int ur = 1;
        while (r+ur < board.length && c+ur < board[r].length &&
                board[r+ur][c+ur].getText().equals(turn)) {
            ur++;
        }
        int dl = 1;
        while (r-dl >= 0 && c-dl >= 0 &&
                board[r-dl][c-dl].getText().equals(turn)) {
            dl++;
        }
        if (ur + dl - 1 >= WINNING_CONNECTION_SIZE) {
            for (int i = 0; i < ur; i++) {
                connectedCells.add(String.format("%d:%d", r+i, c+i));
            }
            for (int j = 1; j < dl; j++) {
                connectedCells.add(String.format("%d:%d", r-j, c-j));
            }
        }

        return connectedCells;
    }

    ArrayList<String> connectFourVertically(int r, int c, String turn) {
        ArrayList<String> connectedCells = new ArrayList<>();
        int down = 1;
        while (r-down >= 0 && board[r-down][c].getText().equals(turn)) {
            down++;
        }
        int up = 1;
        while (r+up < board.length && board[r+up][c].getText().equals(turn)) {
            up++;
        }
        if (up + down - 1 >= WINNING_CONNECTION_SIZE) {
            for (int i = r - down + 1; i <= r + up - 1; i++) {
                connectedCells.add(String.format("%d:%d", i, c));
            }
        }
        return connectedCells;
    }

    ArrayList<String> connectFourHorizontally(int r, int c, String turn) {
        ArrayList<String> connectedCells = new ArrayList<>();
        int left = 1;
        while (c-left >= 0 && board[r][c-left].getText().equals(turn)) {
            left++;
        }
        int right = 1;
        while (c+right < board[r].length && board[r][c+right].getText().equals(turn)) {
            right++;
        }
        if (left + right - 1 >= WINNING_CONNECTION_SIZE) {
            for (int i = c - left + 1; i <= c + right - 1; i++) {
                connectedCells.add(String.format("%d:%d", r, i));
            }
        }
        return connectedCells;
    }

    public int firstFreeRowInAColumn(int column) {
        for (int row = 0; row < board.length; row++) {
            if (getCell(row, column).getText().equals(unClickedCell)) {
                return row;
            }
        }
        return NO_FREE_CELL;
    }
    boolean isClickedCell(int r, int c) {
        return ! board[r][c].getText().equals(unClickedCell);
    }
}