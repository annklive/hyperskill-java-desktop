package life;

import java.util.Scanner;
import java.util.Random;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        int N = scanner.nextInt();
        int N = 100;
        GameOfLife game = new GameOfLife();
    }
}

class WorldState {
    private boolean[][] world;
    private final int N;
    private int generation;
    private int alive;

    private static final int MAX_ITERATION = Integer.MAX_VALUE;

    WorldState(int N) {
        this.N = N;
        this.world = generateWorld();
        this.generation = 0;
        this.alive = countAlive();
    }

    public boolean[][] getWorld() {
        return world;
    }

    public void setWorld(boolean[][] world) {
        this.world = world;
    }

    public int getGeneration() {
        return generation;
    }

    public int getAlive() {
        return alive;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setAlive(int alive) {
        this.alive = alive;
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

    boolean[][] nextState() {

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

    int countAliveNeighbor(int i, int j) {
        int alive = 0;
        int r, c;

        // north
        r = (i - 1) < 0 ? N-1 : i-1;
        c = j;
        if (world[r][c]) {
            alive++;
        }
        // north-west
        c = (j - 1) < 0 ? N-1 : j-1;
        if (world[r][c]) {
            alive++;
        }
        // north-east
        c = (j + 1) < N ? j+1 : 0;
        if (world[r][c]) {
            alive++;
        }
        // west
        r = i;
        c = (j - 1) < 0 ? N-1 : j-1;
        if (world[r][c]) {
            alive++;
        }
        // east
        c = (j + 1) < N ? j+1 : 0;
        if (world[r][c]) {
            alive++;
        }
        // south
        r = (i + 1) < N ? i+1 : 0;
        c = j;
        if (world[r][c]) {
            alive++;
        }
        // south-west
        c = (j - 1) < 0 ? N-1 : j-1;
        if (world[r][c]) {
            alive++;
        }
        // south-east
        c = (j + 1) < N ? j+1 : 0;
        if (world[r][c]) {
            alive++;
        }

        return alive;
    }

    void evolve() {
        while (generation <= 10) {
            clearScreen();
            printWorld();
            world = nextState();
            alive = countAlive();
            generation++;
        }
    }
    void clearScreen() {
        //System.out.print("\033[H\033[2J");
        //System.out.flush();
        try{
            String operatingSystem = System.getProperty("os.name");

            if(operatingSystem.contains("Windows")){
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }

        }catch(IOException | InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}
