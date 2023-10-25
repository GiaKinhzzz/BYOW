package byow.Core.Component;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomBuilder {
    private final int minRoomSize = 4;
    private final int maxRoomSize = 10;
    private int minNumRoom;
    private int maxNumRoom;
    private TETile[][] world;
    private Random random;
    private List<Room> rooms;

    public RoomBuilder(TETile[][] world, int minNumRoom, int maxNumRoom, Random random) {
        this.world = world;
        this.random = random;
        this.minNumRoom = minNumRoom;
        this.maxNumRoom = maxNumRoom;
        rooms = new ArrayList<>();
    }

    public void buildRooms() {
        int numTries;
        int numRoom = RandomUtils.uniform(random, minNumRoom, maxNumRoom);
        for (int roomIdx = 0; roomIdx < numRoom; ++roomIdx) {
            // Check maximum 20 times to find a room, if not bypass
            numTries = 0;
            while (numTries++ <= 20) {
                Room room = genRoom();
                if (!doesOverlapExistingRooms(room)) {
                    buildRoom(room);
                    break;
                }
            }
        }
    }

    private Room genRoom() {
        int roomWidth = RandomUtils.uniform(random, minRoomSize, maxRoomSize);
        int roomHeight = RandomUtils.uniform(random, minRoomSize, maxRoomSize);
        int roomX = RandomUtils.uniform(random, 0, world.length - roomWidth);
        int roomY = RandomUtils.uniform(random, 0, world[0].length - roomHeight);
        return new Room(new Point(roomX, roomY), new Point(roomX + roomWidth, roomY + roomHeight));
    }

    private void buildRoom(Room room) {
        rooms.add(room);

        for (int x = room.bottomLeft.x; x <= room.topRight.x; ++x) {
            for (int y = room.bottomLeft.y; y <= room.topRight.y; ++y) {
                if (x == room.bottomLeft.x || x == room.topRight.x ||
                        y == room.bottomLeft.y || y == room.topRight.y) {
                    world[x][y] = Tileset.WALL;
                } else {
                    world[x][y] = Tileset.FLOOR;
                }
            }
        }
    }

    private boolean doesOverlapExistingRooms(Room room) {
        for (Room currentRoom : rooms) {
            if (doRoomsOverlap(currentRoom, room)) {
                return true;
            }
        }
        return false;
    }

    private boolean doRoomsOverlap(Room roomA, Room roomB) {
        if (roomA.topRight.x < roomB.bottomLeft.x || roomB.topRight.x < roomA.bottomLeft.x ||
                (roomA.topRight.y < roomB.bottomLeft.y || roomB.topRight.y < roomA.bottomLeft.y)) {
            return false;
        }
        return true;
    }

    public List<Room> getRooms() {
        return this.rooms;
    }

    public static class Room {
        public Point bottomLeft;
        public Point topRight;

        public Room(Point bottomLeft, Point topRight) {
            this.bottomLeft = bottomLeft;
            this.topRight = topRight;
        }
    }
}
