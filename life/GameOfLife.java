package life;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Timer;

public class GameOfLife extends JFrame {
    private final JLabel generationLabel;
    private final JLabel aliveLabel;
    private final WorldModel worldModel;
    private boolean pause;
    private boolean restarted;
    private static final int N = 100;
    private static final int SLEEP_MS = 100;
    private static final int BOARD_SIZE = 550;
    private static final int CONTROL_PANEL_SIZE = 200;

    public GameOfLife() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(CONTROL_PANEL_SIZE+BOARD_SIZE, BOARD_SIZE);
        setLayout(new BoxLayout(this.getContentPane(),
                BoxLayout.X_AXIS));
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,
                BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 450, 10));
        generationLabel = new JLabel("Generation #0");
        generationLabel.setName("GenerationLabel");
        aliveLabel = new JLabel("Alive: 0");
        aliveLabel.setName("AliveLabel");
        JToggleButton playToggleButton = new JToggleButton("Play/Pause");
        playToggleButton.setSelected(true);
        playToggleButton.addItemListener( itemEvent -> {
            int state = itemEvent.getStateChange();

            // if selected print selected in console
            if (state == ItemEvent.SELECTED) {
                setPause(false);
            }
            else {
                setPause(true);
            }
        });
        playToggleButton.setName("PlayToggleButton");
        JButton resetButton = new JButton("Reset");
        resetButton.setName("ResetButton");
        resetButton.addActionListener(e -> setRestarted(true));
        controlPanel.add(playToggleButton);
        controlPanel.add(resetButton);
        controlPanel.add(generationLabel);
        controlPanel.add(aliveLabel);
        controlPanel.setPreferredSize(new Dimension(CONTROL_PANEL_SIZE, BOARD_SIZE));
        add(controlPanel);
        WorldPanel worldPanel = new WorldPanel();
        add(worldPanel);

        setTitle("Game of Life");
        pause = false;
        worldModel = new WorldModel(N);

        Timer timer = new Timer(SLEEP_MS, actionEvent -> {
            if (isRestarted()) {
                worldModel.setWorld(worldModel.generateWorld());
                worldModel.setGeneration(0);
                worldModel.setAlive(0);
                setRestarted(false);
            }
            if (!isPause()) {
                worldModel.setWorld(worldModel.nextState());
                worldModel.setAlive(worldModel.countAlive());
                worldModel.setGeneration(worldModel.getGeneration() + 1);
            }
            repaint();
        });
        timer.start();
        setVisible(true);
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isRestarted() {
        return restarted;
    }

    public void setRestarted(boolean restarted) {
        this.restarted = restarted;
    }

    private class WorldPanel extends JPanel {
        private static final int blockSize = 5;
        private static final int margin = 10;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            boolean[][] world = worldModel.getWorld();
            generationLabel.setText("Generation #" + worldModel.getGeneration());
            aliveLabel.setText("Alive: " + worldModel.getAlive());
            for (int i = 0; i < world.length; i++) {
                for (int j = 0; j < world[i].length; j++) {
                    g.drawRect(margin+i*blockSize, margin+j*blockSize, blockSize, blockSize);
                    if (world[i][j]) {
                        g.fillRect(margin+i*blockSize, margin+j*blockSize, blockSize, blockSize);
                    }

                }
            }
        }
    }

}

