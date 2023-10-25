package byow.Core.Component;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FruitBuilder {
    private TETile[][] world;
    private List<RoomBuilder.Room> rooms;
    private Random random;
    private List<Boolean> fruitVisibilities;
    private List<Point> fruitPositions;

    public FruitBuilder(TETile[][] world, List<RoomBuilder.Room> rooms, Random random, List<Boolean> fruitVisibilities) {
        this.world = world;
        this.rooms = rooms;
        this.random = random;
        this.fruitVisibilities = fruitVisibilities;
        this.fruitPositions = new ArrayList<>();
    }

    public void buildFruits() {
        for (int idx = 0; idx < rooms.size(); ++idx) {
            int fruitX = RandomUtils.uniform(random, rooms.get(idx).bottomLeft.x + 1, rooms.get(idx).topRight.x - 1);
            int fruitY = RandomUtils.uniform(random, rooms.get(idx).bottomLeft.y + 1, rooms.get(idx).topRight.y - 1);
            fruitPositions.add(new Point(fruitX, fruitY));

            if (fruitVisibilities == null) {
                world[fruitX][fruitY] = Tileset.APPLE;
            } else {
                if (fruitVisibilities.get(idx)) {
                    world[fruitX][fruitY] = Tileset.APPLE;
                }
            }
        }
    }

    public List<Boolean> getFruitVisibilities() {
        List<Boolean> visibilities = new ArrayList<>();

        for (Point fruitPosition: this.fruitPositions) {
            if (world[fruitPosition.x][fruitPosition.y] != Tileset.FLOOR &&
                    world[fruitPosition.x][fruitPosition.y] != Tileset.AVATAR) {
                visibilities.add(true);
            } else {
                visibilities.add(false);
            }
        }
        return visibilities;
    }

    public boolean areFruitsAvailable() {
        for (boolean isFruitVisible: getFruitVisibilities()) {
            if (isFruitVisible) {
                return true;
            }
        }
        return false;
    }
}
