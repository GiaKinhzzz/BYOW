package byow.Core.Component;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.Random;

public class WorldBuilder {
    private Random random;
    private TETile[][] world;
    private RoomBuilder roomBuilder;
    private HallwayBuilder hallwayBuilder;
    private FruitBuilder fruitBuilder;
    private Player player;

    public WorldBuilder(int width, int height,
                        long seed,
                        int minNumRoom, int maxNumRoom,
                        Point playerPosition,
                        List<Boolean> fruitVisibilities) {
        random = new Random(seed);
        world = new TETile[width][height];
        buildWorld(minNumRoom, maxNumRoom, fruitVisibilities);

        if (playerPosition == null) {
            player = getPlayerPosition();
        } else {
            player = new Player(new Point(playerPosition.x, playerPosition.y), world);
        }
    }

    public void buildWorld(int minNumRoom, int maxNumRoom, List<Boolean> fruitVisibilities) {
        roomBuilder = new RoomBuilder(world, minNumRoom, maxNumRoom, random);
        roomBuilder.buildRooms();
        fruitBuilder = new FruitBuilder(world, roomBuilder.getRooms(), random, fruitVisibilities);
        fruitBuilder.buildFruits();
        hallwayBuilder = new HallwayBuilder(world, roomBuilder.getRooms(), random);
        hallwayBuilder.buildHallways();
        buildOutdoorSpace();
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Boolean> getFruitVisibilities() {
        return fruitBuilder.getFruitVisibilities();
    }
    public boolean areFruitsAvailable() {
        return fruitBuilder.areFruitsAvailable();
    }

    private Player getPlayerPosition() {
        while (true) {
            int x = RandomUtils.uniform(random, this.world.length);
            int y = RandomUtils.uniform(random, this.world[0].length);

            if (world[x][y] == Tileset.FLOOR) {
                return new Player(new Point(x, y), world);
            }
        }

    }

    private void buildOutdoorSpace() {
        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (world[x][y] != Tileset.FLOOR && world[x][y] != Tileset.WALL && world[x][y] != Tileset.FLOWER && world[x][y] != Tileset.APPLE) {
                    world[x][y] = Tileset.NOTHING;
                }
            }
        }
    }

    public TETile[][] getWorld() {
        if (roomBuilder == null && hallwayBuilder == null) {
            return null;
        }
        return world;
    }
}
