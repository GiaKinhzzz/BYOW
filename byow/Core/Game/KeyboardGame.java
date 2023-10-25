package byow.Core.Game;

import byow.Core.Input.KeyboardInputSource;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KeyboardGame extends Game {
    private final Font smallFont;
    private final Font mediumFont;
    private final Font bigFont;
    private final int lightSize = 3;
    private boolean isLightOn = true;

    public KeyboardGame(int width, int height) {
        super(width, height);
        this.inputSource = new KeyboardInputSource();

        // Set available fonts
        smallFont = new Font("Monaco", Font.BOLD, 14);
        mediumFont = new Font("Monaco", Font.BOLD, 20);
        bigFont = new Font("Monaco", Font.BOLD, 30);

        // Configure GUI parameters
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();
    }

    public TETile[][] play() {
        displayMenu();
        handleInput();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        displayGameWin();
        return world;
    }

    private void displayMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.setFont(bigFont);
        StdDraw.text(this.width / 2, this.height - 8, "CS61B: Play the game");

        StdDraw.setFont(mediumFont);
        StdDraw.text(this.width / 2, this.height - 12, "(N)ew Game");
        StdDraw.text(this.width / 2, this.height - 14, "(L)oad Game");
        StdDraw.text(this.width / 2, this.height - 16, "(Q)uit");
        StdDraw.show();
    }

    public void addSeedHintToMenu() {
        displayMenu();
        StdDraw.text(this.width / 2, this.height - 22, "Enter Seed:");
        StdDraw.show();
    }

    public void addSeedToMenu(long seed) {
        addSeedHintToMenu();
        StdDraw.text(this.width / 2, this.height - 24, Long.toString(seed));
        StdDraw.show();
    }

    private void displayRealDateAndTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dtnow = now.format(dtf);
        StdDraw.textLeft(3, this.height - 1, dtnow);
    }

    private void convertWorldToImage(TETile[][] world) {
        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                if (world[x][y].equals(Tileset.FLOOR)) {
                    world[x][y] = Tileset.FLOOR_IMG;
                } else if (world[x][y].equals(Tileset.WALL)) {
                    world[x][y] = Tileset.WALL_IMG;
                } else if (world[x][y].equals(Tileset.AVATAR)) {
                    world[x][y] = Tileset.AVATAR_IMG;
                } else if (world[x][y].equals(Tileset.APPLE)) {
                    world[x][y] = Tileset.APPLE_IMG;
                }
            }
        }
    }

    public void render(TETile[][] world) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(smallFont);
        StdDraw.setPenColor(Color.WHITE);
        displayRealDateAndTime();
        StdDraw.textRight(this.width - 3, this.height - 1, "(o): light on/off");

        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseY < world[0].length) {
            String text = world[mouseX][mouseY].description();
            StdDraw.text(this.width / 2, this.height - 1, text);
        }

        TETile[][] newWorld;
        if (!isLightOn) {
            newWorld = getMaskedWorld();
        } else {
            newWorld = TETile.copyOf(world);
        }
        convertWorldToImage(newWorld);

        for (int x = 0; x < world.length; x += 1) {
            for (int y = 0; y < world[0].length; y += 1) {
                if (newWorld[x][y] == Tileset.APPLE_IMG || newWorld[x][y] == Tileset.AVATAR_IMG) {
                    TETile tmp = newWorld[x][y];
                    newWorld[x][y] = Tileset.FLOOR_IMG;
                    newWorld[x][y].draw(x, y);
                    newWorld[x][y] = tmp;
                    newWorld[x][y].draw(x, y);
                } else {
                    newWorld[x][y].draw(x, y);
                }
            }
        }

        StdDraw.show();
    }

    public void displayGameWin() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(this.width / 2, this.height / 2, "You won!!!");
        StdDraw.show();
    }

    public void toggleLight() {
        isLightOn = !isLightOn;
    }

    public boolean isLightOn() {
        return isLightOn;
    }

    private TETile[][] getMaskedWorld() {
        TETile[][] maskWorld = new TETile[world.length][world[0].length];
        Point bottomLeft = new Point(player.x() - lightSize, player.y() - lightSize);
        Point topRight = new Point(player.x() + lightSize, player.y() + lightSize);
        for (int i = 0; i < maskWorld.length; i++) {
            for (int j = 0; j < maskWorld[0].length; j++) {
                if (i >= bottomLeft.x && i <= topRight.x && j >= bottomLeft.y && j <= topRight.y) {
                    maskWorld[i][j] = world[i][j];
                } else {
                    maskWorld[i][j] = Tileset.NOTHING;
                }
            }
        }
        return maskWorld;
    }
}
