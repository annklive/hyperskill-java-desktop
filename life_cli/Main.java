package life_cli;

import java.util.Scanner;
import java.util.Random;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();

        WorldState worldState = new WorldState(N);
        worldState.evolve();
    }
}

class WorldState {
    private boolean[][] world;
    private int N;
    private int generation;
    private int alive;
    
    WorldState(int N) {
        this.N = N;
        this.world = generateWorld();             
        this.generation = 0;
        this.alive = countAlive();
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

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                char cell = world[i][j] ? 'O' : ' '; 
                System.out.printf("%c", cell);
            }
            System.out.println();
        }
    }

    public int countAlive() {
        int cnt = 0;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (world[i][j]) {
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
                    if (aliveNeighbors == 2 || aliveNeighbors == 3) {
                        nextWorld[i][j] = true;
                    } else {
                        nextWorld[i][j] = false;
                    }
                } else {                 // dead cell
                    if (aliveNeighbors == 3) {
                        nextWorld[i][j] = true;
                    } else {
                        nextWorld[i][j] = false;
                    }
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
        r = (i - 1) < 0 ? N-1 : i-1;
        c = (j - 1) < 0 ? N-1 : j-1; 
        if (world[r][c]) {
            alive++;
        }
        // north-east
        r = (i - 1) < 0 ? N-1 : i-1;
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
        r = i;
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
        r = (i + 1) < N ? i+1 : 0; 
        c = (j - 1) < 0 ? N-1 : j-1; 
        if (world[r][c]) {
            alive++;
        }
        // south-east
        r = (i + 1) < N ? i+1 : 0; 
        c = (j + 1) < N ? j+1 : 0; 
        if (world[r][c]) {
            alive++;
        }

        return alive;
    }

    void evolve() {
        while (true) {
            printWorld();
            world = nextState();
            alive = countAlive();
            generation++;
            clearScreen();
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
        }
    }
}

