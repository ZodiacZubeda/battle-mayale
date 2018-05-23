package entities;

import mayflower.Actor;
import mayflower.Keyboard;
import mayflower.Text;
import util.Vector2;
import weapons.*;

import javax.imageio.IIOException;
import java.io.*;

import java.awt.*;
import java.util.ConcurrentModificationException;

/**
 * @author Shivashriganesh Mahato
 */
public class Player extends Actor {
    private int score;
    public boolean winner;
    private String name;
    private int id;
    private int health;
    private int ammo;
    private int fireSpeed;
    private boolean isAlive;
    private Keyboard keyListener;
    private PointerInfo mousePos;
    private Text tag;
    private boolean canMove;
    private boolean didJustMove;
    private Vector2 absPos;
    private Vector2 velocity;
    private Weapon weapon;
    private boolean hasDied;
    private boolean[] stopped;
    private int pickupTimer;
    private boolean canPickup;

    public Player(String name, int id, double x, double y, boolean canMove) {
        score = 0;
        winner = false;
        this.name = name;
        this.id = id;
        this.canMove = canMove;
        didJustMove = false;
        health = 100;
        isAlive = true;
        fireSpeed = 0;
        ammo = 0;
        setPicture("images/player.png");
        setPosition(x, y);
        tag = new Text(name, Color.WHITE);
        absPos = new Vector2(x, y);
        weapon = new Pistol(this);
        hasDied = false;
        velocity = new Vector2(0, 0);
        stopped = new boolean[]{false, false, false, false};
        pickupTimer = 0;
        canPickup = true;
    }

    public Player(String name, int id, double x, double y) {
        this(name, id, x, y, true);
    }

    public Player(String name, int id) {
        this(name, id, 0, 0, true);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void update() {
        velocity.zero();
        if(winner)
        {
            writeScores(score);
        }
        if (!isAlive) {
            health += 10000000;
            isAlive = true;
        }
        Actor[] actors = getTouching();
        for (Actor a : actors) {
            if (a instanceof Bullet && ((Bullet) a).getPlayer().getId() != getId()) {
                health -= ((Bullet) a).getPlayer().getWeapon().getDamage();
                ((Bullet) a).kill();
            }
            if (health <= 0)
            {
                ((Bullet) a).getPlayer().playerAddScore();
                isAlive = false;
                hasDied = true;

            }
        }


        if (canMove && !hasDied) {
            didJustMove = false;
            try {
                keyListener = getKeyboard();
                mousePos = MouseInfo.getPointerInfo();
                if (keyListener.isKeyPressed("W") && !stopped[Direction.UP.ind]) {
                    //weapon.move(1,"NORTH");
                    move(1, "NORTH");
                    setAbsPos(getAbsX(), getAbsY() - 1);
                    velocity.add(0, -1);
                    didJustMove = true;
                }
                if (keyListener.isKeyPressed("S") && !stopped[Direction.DOWN.ind]) {
                    // weapon.move(1,"SOUTH");
                    move(1, "SOUTH");
                    setAbsPos(getAbsX(), getAbsY() + 1);
                    velocity.add(0, 1);
                    didJustMove = true;
                }
                if (keyListener.isKeyPressed("A") && !stopped[Direction.LEFT.ind]) {
                    //weapon.move(1,"WEST");
                    move(1, "WEST");
                    setAbsPos(getAbsX() - 1, getAbsY());
                    velocity.add(-1, 0);
                    didJustMove = true;
                }
                if (keyListener.isKeyPressed("D") && !stopped[Direction.RIGHT.ind]) {
                    //weapon.move(1,"EAST");
                    move(1, "EAST");
                    setAbsPos(getAbsX() + 1, getAbsY());
                    velocity.add(1, 0);
                    didJustMove = true;
                }
            } catch (ConcurrentModificationException e) {
                System.out.println("Mayflower is bad");
            }
        }

        unstop();

        if (!canPickup)
            pickupTimer++;
        if (pickupTimer > 25) {
            pickupTimer = 0;
            canPickup = true;
        }
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean didJustMove() {
        return didJustMove;
    }
    public void isWinner()
    {
        winner = true;
    }

    public boolean isStillAlive() {
        return isAlive;
    }

    public boolean isHasDied() {
        return hasDied;
    }

    public Text getTag() {
        return tag;
    }

    public double getAbsX() {
        return absPos.getX();
    }

    public double getAbsY() {
        return absPos.getY();
    }

    public Vector2 getAbsPos() {
        return absPos;
    }

    public void setAbsPos(double x, double y) {
        absPos.set(x, y);
        weapon.setAbsPos(x, y);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setStopped(Direction direction, boolean isStopped) {
        stopped[direction.ind] = isStopped;
    }

    private void unstop() {
        stopped[0] = stopped[1] = stopped[2] = stopped[3] = false;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public String pickUp(String type) {
        try {
            if (keyListener.isKeyPressed("E") && canPickup) {
                String curType = weapon.getName();
                getStage().removeActor(weapon);
                weapon = getWeaponFromType(type);
                getStage().addActor(weapon, getX(), getY());
                canPickup = false;
                return curType;
            }
        } catch (NullPointerException e) {
            System.out.println("Java is bad 2.0.");
        }
        return type;
    }

    private Weapon getWeaponFromType(String type) {
        switch (type) {
            case "LMG":
                return new LMG(this);
            case "Pistol":
                return new Pistol(this);
            case "Railgun":
                return new RailGun(this);
            case "Rifle":
                return new Rifle(this);
            case "Shotgun":
                return new Shotgun(this);
            case "SMG":
                return new SMG(this);
            case "Sniper":
                return new Sniper(this);
            default:
                return null;
        }
    }
    public void playerAddScore()
    {
        score++;
    }

    public int getScore()
    {
        return score;
    }
    public void writeScores(int bore)
    {
        try
        {
            FileWriter fw = new FileWriter("scores.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter cut = new PrintWriter(bw,true);
            cut.println(getName() + " got " + bore + " kills!");
            cut.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void writeScores(int bore, String message)
    {
        try
        {
            FileWriter fw = new FileWriter("scores.txt",true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter cut = new PrintWriter(bw,true);
            cut.println(getName() + " got " + bore + " kills! "  + getName() + " was a winner!");
            cut.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public enum Direction {
        LEFT(0), UP(1), RIGHT(2), DOWN(3);

        public int ind;

        Direction(int ind) {
            this.ind = ind;
        }
    }
}
