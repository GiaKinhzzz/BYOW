package byow.Core.Component;

import byow.Core.RandomUtils;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.*;

public class HallwayBuilder {
    private class Distance implements Comparable<Distance> {
        private double value;

        public Distance(RoomBuilder.Room firstRoom, RoomBuilder.Room secondRoom) {
            double firstRoomCenterX = (firstRoom.bottomLeft.x + firstRoom.topRight.x) / 2;
            double firstRoomCenterY = (firstRoom.bottomLeft.y + firstRoom.topRight.y) / 2;
            double secondRoomCenterX = (secondRoom.bottomLeft.x + secondRoom.topRight.x) / 2;
            double secondRoomCenterY = (secondRoom.bottomLeft.y + secondRoom.topRight.y) / 2;
            value = Math.sqrt(Math.pow(firstRoomCenterX - secondRoomCenterX, 2) + Math.pow(firstRoomCenterY - secondRoomCenterY, 2));
        }

        @Override
        public int compareTo(Distance distance) {
            if (this.value > distance.value) {
                return 1;
            } else if (this.value < distance.value) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private TETile[][] world;
    private List<RoomBuilder.Room> rooms;
    private Random random;
    private Point diggerPosition;
    private Point start;
    private Point destination;
    private WeightedQuickUnionUF weightedQuickUnionUF;

    public HallwayBuilder(TETile[][] world, List<RoomBuilder.Room> rooms, Random random) {
        this.world = world;
        this.rooms = rooms;
        this.random = random;
        this.weightedQuickUnionUF = new WeightedQuickUnionUF(rooms.size());
    }

    public void buildHallways() {
        RoomBuilder.Room room;
        for (int roomIdx = 0; roomIdx < rooms.size() - 1; ++roomIdx) {
            room = rooms.get(roomIdx);
            buildHallway(room, findClosestRoomOf(room));
        }
    }

    private RoomBuilder.Room findClosestRoomOf(RoomBuilder.Room room) {
        PriorityQueue<RoomBuilder.Room> distanceQueue = new PriorityQueue<>(new Comparator<RoomBuilder.Room>() {
            @Override
            public int compare(RoomBuilder.Room fstRoom, RoomBuilder.Room sndRoom) {
                Distance fstDistance = new Distance(fstRoom, room);
                Distance sndDistance = new Distance(sndRoom, room);
                return fstDistance.compareTo(sndDistance);
            }
        });

        for (RoomBuilder.Room currentRoom : rooms) {
            if (!currentRoom.equals(room)) {
                distanceQueue.add(currentRoom);
            }
        }

        int roomIdx = rooms.indexOf(room);
        RoomBuilder.Room closetRoom = null;
        int closetRoomIdx;
        while (!distanceQueue.isEmpty()) {
            closetRoom = distanceQueue.remove();
            closetRoomIdx = rooms.indexOf(closetRoom);

            if (!weightedQuickUnionUF.connected(roomIdx, closetRoomIdx)) {
                weightedQuickUnionUF.union(roomIdx, closetRoomIdx);
                break;
            }
        }
        return closetRoom;
    }

    private void buildHallway(RoomBuilder.Room roomA, RoomBuilder.Room roomB) {
        start = getRandomPointInside(roomA);
        destination = getRandomPointInside(roomB);
        diggerPosition = new Point(start.x, start.y);

        while (diggerPosition.x != destination.x || diggerPosition.y != destination.y) {
            if (diggerPosition.y == start.y && diggerPosition.x == destination.x) {
                buildTurnSegment();
            } else if (diggerPosition.x != destination.x) {
                buildHorizontalSegment();
            } else {
                buildVerticalSegment();
            }
        }
    }

    private Point getRandomPointInside(RoomBuilder.Room room) {
        Point point = new Point();
        point.x = RandomUtils.uniform(random, room.bottomLeft.x + 1, room.topRight.x - 1);
        point.y = RandomUtils.uniform(random, room.bottomLeft.y + 1, room.topRight.y - 1);
        return point;
    }

    private void buildTile(int x, int y, TETile teTile) {
        if (world[x][y] != Tileset.FLOOR && world[x][y] != Tileset.APPLE) {
            world[x][y] = teTile;
        }
    }

    private void buildHorizontalSegment() {
        buildTile(diggerPosition.x, diggerPosition.y - 1, Tileset.WALL);
        buildTile(diggerPosition.x, diggerPosition.y, Tileset.FLOOR);
        buildTile(diggerPosition.x, diggerPosition.y + 1, Tileset.WALL);
        diggerPosition.x += (diggerPosition.x < destination.x) ? 1 : -1;
    }

    private void buildVerticalSegment() {
        buildTile(diggerPosition.x - 1, diggerPosition.y, Tileset.WALL);
        buildTile(diggerPosition.x, diggerPosition.y, Tileset.FLOOR);
        buildTile(diggerPosition.x + 1, diggerPosition.y, Tileset.WALL);
        diggerPosition.y += (diggerPosition.y < destination.y) ? 1 : -1;
    }

    private void buildTurnSegment() {
        int deltaX = (start.x < destination.x) ? 1 : -1;
        int deltaY = (start.y < destination.y) ? 1 : -1;

        buildTile(diggerPosition.x, diggerPosition.y + deltaY, Tileset.FLOOR);
        buildTile(diggerPosition.x, diggerPosition.y, Tileset.FLOOR);
        buildTile(diggerPosition.x, diggerPosition.y - deltaY, Tileset.WALL);

        buildTile(diggerPosition.x + 1 * deltaX, diggerPosition.y + deltaY, Tileset.WALL);
        buildTile(diggerPosition.x + 1 * deltaX, diggerPosition.y, Tileset.WALL);
        buildTile(diggerPosition.x + 1 * deltaX, diggerPosition.y - deltaY, Tileset.WALL);
        diggerPosition.y += deltaY;
    }
}
