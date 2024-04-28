package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TicTacToeBoard {
    static char[] players = {'X', 'O'};
    int[][] board;
    static final int EMPTY = 2;

    int boardSize;

    TicTacToeBoard(int boardSize) {
        this.boardSize = boardSize;
        board = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    String addPiece(int piece, int row, int col) {
        System.out.println(piece + " : "  + row + "," + col);
        board[row][col] = piece;
        return checkGameStatus();
    }

    String checkWinner(int player) {
        if (players[player] == 'O') {
            return StatusBar.O_WINS;
        } else {
            return StatusBar.X_WINS;
        }
    }

    String checkGameStatus() {
        // three-in-a-row
        for (int i = 0; i < boardSize; i++) {
            int player = board[i][0];
            if (player == EMPTY) {
                continue;
            }
            boolean winnerFound = true;
            for (int j = 1; j < boardSize; j++) {
                if (board[i][j] != player) {
                    winnerFound = false;
                    break;
                }
            }
            if (player != EMPTY && winnerFound) {
                return checkWinner(player);
            }
        }
        //three-in-a-column
        for (int j = 0; j < boardSize; j++) {
            int player = board[0][j];
            if (player == EMPTY) {
                continue;
            }
            boolean winnerFound = true;
            for (int i = 1; i < boardSize; i++) {
                if (board[i][j] != player) {
                    winnerFound = false;
                    break;
                }
            }
            if (player != EMPTY && winnerFound) {
                return checkWinner(player);
            }
        }
        //topleft-to-bottomright
        boolean winnerFound = true;
        int player = board[0][0];
        if (board[0][0] != EMPTY) {
            for (int i = 1, j = 1; i < boardSize; i++, j++) {
                if (player != board[i][j]) {
                    winnerFound = false;
                    break;
                }
            }
            if (winnerFound) {
                return checkWinner(player);
            }
        }
        //topright-to-bottomleft
        winnerFound = true;
        if (board[0][boardSize-1] != EMPTY) {
            player = board[0][boardSize - 1];
            for (int i = 1, j = boardSize - 2; i < boardSize; i++, j--) {
                if (player != board[i][j]) {
                    winnerFound = false;
                    break;
                }
            }
            if (winnerFound) {
                return checkWinner(player);
            }
        }

        //all occupied?
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == EMPTY) {
                    return StatusBar.GAME_IN_PRORGRESS;
                }
            }
        }
        // board is full and no player wins
        return StatusBar.DRAW;
    }
}

public class BoardField extends JPanel {
    StatusBar statusBar;
    CommandPanel commandPanel;
    JButton[][] cells;
    TicTacToeBoard tictactoeBoard;
    int boardSize = 3;
    public static final String EMPTY = " ";
    public static final String X = "X";
    public static final String O = "O";

    static final String[] moves = {X, O};
    int currentMove = 0; // 0 = player1, 1 = player 2
    String player1, player2;
    Thread gameLoop;
    public BoardField() {
        super();
        tictactoeBoard = new TicTacToeBoard(boardSize);

        setLayout(new GridLayout(3, 3));
        cells = new JButton[boardSize][boardSize];

        for (int j = 0; j < boardSize; j++) {
            char rowName = (char)('A' + j);
            for (int i = 0; i < boardSize; i++) {
                String name = Character.toString(rowName) + (boardSize-i);
                JButton cell = new JButton(EMPTY);
                cell.setName("Button"+name);
                cell.setFocusPainted(false);
                cell.setFont(new Font("Tahoma", Font.BOLD, 48));
                cell.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (isGameOver()) {
                            return;
                        }
                        JButton thisBtn = (JButton) e.getSource();

                        if (thisBtn.getText() == EMPTY) {
                            thisBtn.setText(moves[currentMove]);
                            String btnName = thisBtn.getName();
                            int col = btnName.charAt(btnName.length()-2) - 'A';
                            int row = 2 - (btnName.charAt(btnName.length()-1) - '1');
                            String newStatus = tictactoeBoard.addPiece(currentMove, row, col);
                            System.out.println(newStatus);
                            if (!newStatus.equals(StatusBar.GAME_IN_PRORGRESS)) {
                                if (newStatus != StatusBar.DRAW) {
                                    String p;
                                    if (currentMove == 0) {
                                        p = player1;
                                    } else {
                                        p = player2;
                                    }
                                    String s = Character.toString(TicTacToeBoard.players[currentMove]);
                                    statusBar.setWinner(p, s);

                                } else {
                                    statusBar.setStatus(newStatus);
                                }
                            }
                            if (isGameOver()) {
                                commandPanel.player1Btn.setEnabled(true);
                                commandPanel.player2Btn.setEnabled(true);
                            } else {
                                currentMove = (currentMove + 1) % 2;
                                String p;
                                if (currentMove == 0) {
                                    p = player1;
                                }
                                else {
                                    p = player2;
                                }
                                String s = Character.toString(TicTacToeBoard.players[currentMove]);
                                statusBar.setPlayerTurn(p, s);
                            }
                        }
                        repaint();
                    }
                });
                cell.setEnabled(false);
                cells[i][j] = cell;
            }
        }

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                add(cells[i][j]);
            }
        }
    }

    public boolean isGameOver() {
        return ((statusBar.getStatus().endsWith("wins")) ||
                (statusBar.getStatus() == StatusBar.DRAW)
                );

    }

    public void start(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
        reset(true);
        //statusBar.setStatus(StatusBar.GAME_IN_PRORGRESS);
        statusBar.setPlayerTurn(player1, Character.toString(TicTacToeBoard.players[0]));
        commandPanel.player1Btn.setEnabled(false);
        commandPanel.player2Btn.setEnabled(false);
        gameLoop = new Thread() {
            @Override
            public void run() {
                super.run();

                playGame(player1, player2);
            }
        };
        gameLoop.start();
    }
    public void reset() {
        if (gameLoop != null) gameLoop.interrupt();
        this.player1 = CommandPanel.PLAYER;
        this.player2 = CommandPanel.PLAYER;
        reset(false);
        statusBar.setStatus(StatusBar.GAME_NOT_STARTED);
        commandPanel.player1Btn.setEnabled(true);
        commandPanel.player2Btn.setEnabled(true);
    }
    public void reset(boolean started) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                cells[i][j].setText(EMPTY);
                cells[i][j].setEnabled(started);
                currentMove = 0;
            }
        }
        tictactoeBoard = new TicTacToeBoard(boardSize);
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
        statusBar.setStatus(StatusBar.GAME_NOT_STARTED);
    }

    public void setCommandPanel(CommandPanel commandPanel) {
        this.commandPanel = commandPanel;
    }

    void playGame(String player1, String player2) {

        if ((player1.equals(CommandPanel.COMPUTER)) &&
                (player2.equals(CommandPanel.COMPUTER))) {

            while (!isGameOver()) {
                if (Thread.interrupted()) {
                    break;
                }
                JButton playCell;
                playCell = findFirstEmptyCell();
                if (playCell != null) {
                    playCell.doClick();
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException ex) {

                    }
                }
            }
        } else if ((player1.equals(CommandPanel.COMPUTER)) &&
                (player2.equals(CommandPanel.PLAYER))) {
            while (!isGameOver()) {
                if (Thread.interrupted()) {
                    break;
                }
                JButton playCell;
                playCell = findFirstEmptyCell();
                if (playCell != null) {
                    playCell.doClick();
                }
                while (currentMove == 1) {
                    try {
                        Thread.sleep(500);
                    } catch(InterruptedException ex) {
                    }
                }
            }

        } else if ((player1.equals(CommandPanel.PLAYER)) &&
                (player2.equals(CommandPanel.COMPUTER))) {
            while (!isGameOver()) {
                if (Thread.interrupted()) {
                    break;
                }
                while (currentMove == 0) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }
                }
                JButton playCell;
                playCell = findFirstEmptyCell();
                if (playCell != null) {
                    playCell.doClick();
                }
            }
        }
    }

    JButton findFirstEmptyCell() {
        // find the first empty cell
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (cells[i][j].getText().equals(EMPTY)) {
                    return cells[i][j];
                }
            }
        }
        return null;
    }

    public void enableAllCells() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                cells[i][j].setEnabled(true);
            }
        }
    }
}
