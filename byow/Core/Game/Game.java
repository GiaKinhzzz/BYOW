package byow.Core.Game;

import byow.Core.Component.Player;
import byow.Core.Component.Point;
import byow.Core.Component.WorldBuilder;
import byow.Core.Input.InputSource;
import byow.Core.Input.KeyboardInputSource;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.toLowerCase;

public abstract class Game {
    private final String savedFile;
    protected int width;
    protected int height;
    protected int hudHeight;
    protected InputSource inputSource;
    protected Player player = null;
    protected TETile[][] world = null;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;
        this.hudHeight = 3;
        this.savedFile = "world.txt";
    }

    public abstract TETile[][] play();

    protected TETile[][] handleInput() {
        long seed = 0;
        boolean isNewSeed = false;
        WorldBuilder worldBuilder = null;
        final int minNumRoom = 20;
        final int maxNumRoom = 40;

        while (inputSource.possibleNextInput()) {
            Character input = inputSource.getNextKey();

            if (input != null) {
                switch (toLowerCase(input)) {
                    // Move up
                    case 'w':
                        if (player != null) {
                            player.moveUp();
                        }
                        break;

                    // Move down or New world
                    case 's':
                        //TODO: check ns without enter seed
                        if (isNewSeed) {
                            // Generate the world here
                            isNewSeed = false;
                            worldBuilder = new WorldBuilder(this.width,this.height - this.hudHeight,
                                    seed,
                                    minNumRoom, maxNumRoom,
                                    null,
                                    null);
                            world = worldBuilder.getWorld();
                            player = worldBuilder.getPlayer();
                        } else {
                            // Move down
                            if (player != null) {
                                player.moveDown();
                            }
                        }
                        break;

                    // Move left
                    case 'a':
                        if (player != null) {
                            player.moveLeft();
                        }
                        break;

                    // Move right
                    case 'd':
                        if (player != null) {
                            player.moveRight();
                        }
                        break;

                    // Load world
                    case 'l':
                        if (world == null) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(savedFile));
                                // Load seed
                                reader.readLine();   // read description line, useless info
                                seed = Long.parseLong(reader.readLine());

                                // Load player's position
                                reader.readLine();  // read description line, useless info
                                int playerX = Integer.parseInt(reader.readLine());
                                int playerY = Integer.parseInt(reader.readLine());

                                // Load light status
                                reader.readLine();  // read description line, useless info
                                boolean isLightOn = Boolean.parseBoolean(reader.readLine());

                                // Load fruit positions
                                reader.readLine();  // read description line, useless info
                                String line;
                                List<Boolean> fruitVisibilities = new ArrayList<>();
                                while ((line = reader.readLine()) != null) {
                                    boolean isVisible = Boolean.parseBoolean(line.split(",")[0]);
                                    fruitVisibilities.add(isVisible);
                                }

                                worldBuilder = new WorldBuilder(this.width, this.height - this.hudHeight,
                                        seed,
                                        minNumRoom, maxNumRoom,
                                        new Point(playerX, playerY),
                                        fruitVisibilities);
                                world = worldBuilder.getWorld();
                                player = worldBuilder.getPlayer();
                                if (this instanceof KeyboardGame kb && !isLightOn) {
                                    kb.toggleLight(); //turn light off
                                }
                            } catch (Exception e) {
                                if (inputSource instanceof KeyboardInputSource) {
                                    System.exit(0);
                                } else {
                                    world = new TETile[width][height];
                                    for (int x = 0; x < width; x++) {
                                        for (int y = 0; y < height; y++) {
                                            world[x][y] = Tileset.NOTHING;
                                        }
                                    }
                                    return world;
                                }
                            }
                        }
                        break;

                    // Build new world
                    case 'n':
                        if (this instanceof InputStringGame) {
                            isNewSeed = true;
                        } else if (world == null && this instanceof KeyboardGame) {
                            ((KeyboardGame) this).addSeedHintToMenu();
                            isNewSeed = true;
                        }
                        break;

                    // Quit the main screen
                    case 'q':
                        if (world == null) {
                            if (this instanceof KeyboardGame) {
                                System.exit(0);
                            }
                        }
                        break;

                    // ready to Quit
                    case ':':
                        if (world != null) {
                            while (true) {
                                input = inputSource.getNextKey();
                                if (input != null && toLowerCase(input) == 'q') {
                                    try {
                                        FileWriter fw = new FileWriter(savedFile);
                                        // Write seed
                                        fw.write("Seed:\r\n");
                                        fw.write(seed + "\r\n");

                                        // Write player's position
                                        fw.write("Player's Position:\r\n");
                                        fw.write(worldBuilder.getPlayer().x() + "\r\n");
                                        fw.write(worldBuilder.getPlayer().y() + "\r\n");

                                        // Write light's status
                                        fw.write("Light Status:\r\n");
                                        if (this instanceof KeyboardGame kb) {
                                            fw.write(kb.isLightOn()+ "\r\n");
                                        } else {
                                            fw.write(true + "\r\n"); //default light on
                                        }

                                        // Write fruits
                                        fw.write("Fruit Visibilities:\r\n");
                                        for (boolean isVisible : worldBuilder.getFruitVisibilities()) {
                                            fw.write(isVisible + "\r\n");
                                        }
                                        fw.close();
                                    } catch (Exception e) {
                                        System.out.println("Can not save");
                                    }
                                    if (this instanceof KeyboardGame) {
                                        System.exit(0);
                                    }
                                    return world;
                                }

                                if (this instanceof KeyboardGame && world != null) {
                                    ((KeyboardGame) this).render(world);
                                }
                            }
                        }
                        break;

                    //Toggle light on and off
                    case 'o':
                        if (this instanceof KeyboardGame) {
                            ((KeyboardGame) this).toggleLight();
                        }
                        break;

                    default:
                        if (isNewSeed && Character.isDigit(input)) {
                            final int factor = 10;
                            seed = seed * factor + Character.getNumericValue(input);
                            if (this instanceof KeyboardGame) {
                                ((KeyboardGame) this).addSeedToMenu(seed);
                            }
                        }
                        break;
                }
            }

            if (this instanceof KeyboardGame && world != null) {
                ((KeyboardGame) this).render(world);
                if (!worldBuilder.areFruitsAvailable()) {
                    return world;
                }
            }
        }

        return world;
    }
}
