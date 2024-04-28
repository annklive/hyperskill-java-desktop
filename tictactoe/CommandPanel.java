package tictactoe;

import javax.swing.*;
import java.awt.*;

public class CommandPanel extends JPanel {
    BoardField boardField;
    StatusBar statusBar;
    String player1, player2;
    JButton player1Btn, player2Btn;
    JButton startResetBtn;

    public static final String PLAYER = "Human";
    public static final String COMPUTER = "Robot";
    public CommandPanel() {
        super();
        player1 = PLAYER;
        player2 = PLAYER;
        player1Btn = new JButton(player1);
        player1Btn.setName("ButtonPlayer1");
        player1Btn.setPreferredSize(
                new Dimension(100, 40));

        player2Btn = new JButton(player2);
        player2Btn.setName("ButtonPlayer2");
        player2Btn.setPreferredSize(
                new Dimension(100, 40));

        startResetBtn = new JButton("Start");
        startResetBtn.setName("ButtonStartReset");
        startResetBtn.setPreferredSize(
                new Dimension(100, 40));

        add(player1Btn);
        player1Btn.addActionListener(e -> {
            JButton playerBtn = (JButton) e.getSource();
            player1 = switchButtonPlayerType(player1, playerBtn);
        });

        add(startResetBtn);
        startResetBtn.addActionListener(e -> {
            if (statusBar.getStatus().equals(StatusBar.GAME_NOT_STARTED)) {
                boardField.start(player1, player2);
                startResetBtn.setText("Reset");
            } else {
                boardField.reset();
                startResetBtn.setText("Start");
            }
        });

        add(player2Btn);
        player2Btn.addActionListener(e -> {
            JButton playerBtn = (JButton) e.getSource();
            player2 = switchButtonPlayerType(player2, playerBtn);
        });
    }

    public void setBoard(BoardField boardField) {
        this.boardField = boardField;
    }
    String switchButtonPlayerType(String player, JButton playerBtn) {
        player = switchPlayerType(player);
        playerBtn.setText(player);
        return player;
    }
    String switchPlayerType(String currentPlayer) {
        if (currentPlayer == PLAYER) {
            return COMPUTER;
        } else {
            return PLAYER;
        }
    }
    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
        statusBar.setStatus(StatusBar.GAME_NOT_STARTED);
    }


}
