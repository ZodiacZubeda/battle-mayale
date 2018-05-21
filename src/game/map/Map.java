package game.map;

import mayflower.Actor;
import util.Vector2;

import java.util.Arrays;

/**
 * @author Shivashriganesh Mahato
 */
public class Map extends Actor {
    private final double TreeChance = .1;

    private Vector2 absPos;
    private Cell[][] grid;
    
    public Map(double x, double y, double w, double h) {
        setPicture("images/gudmap_small.jpg");
        absPos = new Vector2(x, y);
        grid = new Cell[(int) h / 40][(int) w / 40];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        generate();
    }

    private void generate() {
        for (int i = 2; i < grid.length - 2; i++) {
            for (int j = 2; j < grid[i].length - 1; j++) {
                boolean flag = true;
                for (int k = i - 2; k <= i + 2; k++) {
                    for (int l = j - 2; l <= j + 1; l++) {
                        if (!grid[k][l].isOpen())
                            flag = false;
                    }
                }
                double noise = flag ? Math.random() : 0;
                grid[i][j].setState(noise > (1 - TreeChance) ? CellState.BLOCKED : CellState.OPEN);
            }
        }
    }

    @Override
    public void update() {

    }

    public double getAX() {
        return absPos.getX();
    }

    public double getAY() {
        return absPos.getY();
    }

    public void setX(double x) {
        this.setPosition(x, this.getY());
    }

    public void setY(double y) {
        this.setPosition(this.getX(), y);
    }

    public void move(double dx, double dy) {
        setX(getX() + dx);
        setY(getY() + dy);
    }

    public Cell[][] getGrid() {
        return grid;
    }

    private static class Grad {
        double x, y, z, w;

        Grad(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Grad(double x, double y, double z, double w) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.w = w;
        }
    }
}
