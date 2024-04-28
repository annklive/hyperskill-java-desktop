package tictactoe;
import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;

public class StatusBar extends JPanel {
    public static final String GAME_NOT_STARTED = "Game is not started";
    public static final String GAME_IN_PRORGRESS = "Game in progress";
    public static final String X_WINS = "X wins";
    public static final String O_WINS = "O wins";
    public static final String DRAW = "Draw";
    JLabel statusLabel;

    public StatusBar() {
        super();
        setLayout(new FlowLayout(FlowLayout.LEADING));
        statusLabel = new JLabel(GAME_NOT_STARTED);
        statusLabel.setName("LabelStatus");
        statusLabel.setPreferredSize(
                new Dimension(200, 40)
        );
        statusLabel.setName("LabelStatus");
        add(statusLabel);
    }
    public void setStatus(String statusMessage) {
        statusLabel.setText(statusMessage);
    }
    public String getStatus() {
        return statusLabel.getText();
    }

    public void setPlayerTurn(String player, String playerSymbol) {
        String message = getPlayerTurn(player, playerSymbol);
        setStatus(message);
    }

    public static String getPlayerTurn(String player, String playerSymbol) {
        return MessageFormat.format(
                "The turn of {0} Player ({1})",
                player, playerSymbol);
    }

    public void setWinner(String player, String playerSymbol) {
        String message = MessageFormat.format(
                "The {0} Player ({1}) wins",
                player, playerSymbol);
        setStatus(message);
    }
}
