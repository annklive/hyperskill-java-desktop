package life;

import java.io.IOException;
import java.util.Random;

public class WorldModel {
    private boolean[][] world;
    private final int N;
    private int generation;
    private int alive;
    private static final int MAX_ITERATION = 10;

    WorldModel(int N) {
        this.N = N;
        this.world = generateWorld();
        this.generation = 0;
        this.alive = countAlive();
    }

    public int getGeneration() {
        return generation;
    }

    public int getAlive() {
        return alive;
    }

    public boolean[][] getWorld() {
        return world;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public void setWorld(boolean[][] world) {
        this.world = world;
    }

    public boolean[][] nextState() {

        boolean[][] nextWorld = new boolean[N][N];

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                int aliveNeighbors = countAliveNeighbor(i, j);
                if (world[i][j]) { // live cell
                    nextWorld[i][j] = aliveNeighbors == 2 || aliveNeighbors == 3;
                } else {                 // dead cell
                    nextWorld[i][j] = aliveNeighbors == 3;
                }
            }
        }

        return nextWorld;
    }

    public boolean[][] generateWorld() {
        Random rnd = new Random();
        boolean[][] world = new boolean[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                world[i][j] = rnd.nextBoolean();
            }
        }
        return world;
    }

    public void printWorld() {
        System.out.printf("Generation #%d\n", generation);
        System.out.printf("Alive: %03d\n", alive);

        for (boolean[] booleans : world) {
            for (boolean aBoolean : booleans) {
                char cell = aBoolean ? 'O' : ' ';
                System.out.printf("%c", cell);
            }
            System.out.println();
        }
    }

    public void evolve() {
        while (generation <= MAX_ITERATION) {
            clearScreen();
            printWorld();
            world = nextState();
            alive = countAlive();
            generation++;
        }
    }

    public int countAlive() {
        int cnt = 0;
        for (boolean[] booleans : world) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    private int topRow(int r) {
        return (r - 1) < 0 ? N - 1 : r - 1;
    }

    private int bottomRow(int r) {
        return (r + 1) < N ? r + 1 : 0;
    }

    private int leftColumn(int c) {
        return (c - 1) < 0 ? N - 1 : c - 1;
    }

    private int rightColumn(int c) {
        return (c + 1) < N ? c + 1 : 0;
    }

    private int countAliveNeighbor(int i, int j) {
        int alive = 0;
        // north
        if (world[topRow(i)][j]) {
            alive++;
        }
        // north-west
        if (world[topRow(i)][leftColumn(j)]) {
            alive++;
        }
        // north-east
        if (world[topRow(i)][rightColumn(j)]) {
            alive++;
        }
        // west
        if (world[i][leftColumn(j)]) {
            alive++;
        }
        // east
        if (world[i][rightColumn(j)]) {
            alive++;
        }
        // south
        if (world[bottomRow(i)][j]) {
            alive++;
        }
        // south-west
        if (world[bottomRow(i)][leftColumn(j)]) {
            alive++;
        }
        // south-east
        if (world[bottomRow(i)][rightColumn(j)]) {
            alive++;
        }

        return alive;
    }

    void clearScreen() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
            Thread.sleep(100);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}
