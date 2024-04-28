package tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToe extends JFrame {


    public TicTacToe() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Tic Tac Toe");
        setResizable(false);
        setSize(450, 450);
        setLayout(new BorderLayout());
        BoardField boardField = new BoardField();

        StatusBar statusBar = new StatusBar();
        CommandPanel commandPanel = new CommandPanel();
        commandPanel.setBoard(boardField);
        commandPanel.setStatusBar(statusBar);

        boardField.setStatusBar(statusBar);
        boardField.setCommandPanel(commandPanel);

        add(commandPanel, BorderLayout.PAGE_START);
        add(boardField, BorderLayout.CENTER);
        add(statusBar, BorderLayout.PAGE_END);

        // add menu
        JMenuBar mb = new JMenuBar();
        JMenu game = new JMenu("Game");
        game.setName("MenuGame");

        JMenuItem m1 = new JMenuItem("Human vs Human");
        m1.setName("MenuHumanHuman");
        m1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardField.reset();
                commandPanel.player1 = CommandPanel.PLAYER;
                commandPanel.player2 = CommandPanel.PLAYER;
                commandPanel.player1Btn.setText(commandPanel.player1);
                commandPanel.player2Btn.setText(commandPanel.player2);
                commandPanel.startResetBtn.setText("Reset");
                boardField.start(commandPanel.player1, commandPanel.player2);
            }
        });
        JMenuItem m2 = new JMenuItem("Human vs Robot");
        m2.setName("MenuHumanRobot");
        m2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardField.reset();
                commandPanel.player1 = CommandPanel.PLAYER;
                commandPanel.player2 = CommandPanel.COMPUTER;
                commandPanel.player1Btn.setText(commandPanel.player1);
                commandPanel.player2Btn.setText(commandPanel.player2);
                commandPanel.startResetBtn.setText("Reset");
                boardField.start(commandPanel.player1, commandPanel.player2);
            }
        });
        JMenuItem m3 = new JMenuItem("Robot vs Human");
        m3.setName("MenuRobotHuman");
        m3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardField.reset();
                commandPanel.player1 = CommandPanel.COMPUTER;
                commandPanel.player2 = CommandPanel.PLAYER;
                commandPanel.player1Btn.setText(commandPanel.player1);
                commandPanel.player2Btn.setText(commandPanel.player2);
                commandPanel.startResetBtn.setText("Reset");
                boardField.start(commandPanel.player1, commandPanel.player2);
            }
        });
        JMenuItem m4 = new JMenuItem("Robot vs Robot");
        m4.setName("MenuRobotRobot");
        m4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardField.reset();
                commandPanel.player1 = CommandPanel.COMPUTER;
                commandPanel.player2 = CommandPanel.COMPUTER;
                commandPanel.player1Btn.setText(commandPanel.player1);
                commandPanel.player2Btn.setText(commandPanel.player2);
                commandPanel.startResetBtn.setText("Reset");
                boardField.start(commandPanel.player1, commandPanel.player2);
            }
        });
        JMenuItem ex = new JMenuItem("Exit");
        ex.setName("MenuExit");
        ex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        game.add(m1);
        game.add(m2);
        game.add(m3);
        game.add(m4);
        game.addSeparator();
        game.add(ex);
        mb.add(game);

        setJMenuBar(mb);
        setVisible(true);
    }
}