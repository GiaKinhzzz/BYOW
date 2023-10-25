package byow.Core.Component;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    private Point position;
    private TETile[][] world;

    public Player(Point position, TETile[][] world) {
        this.position = position;
        this.world = world;
        world[this.position.x][this.position.y] = Tileset.AVATAR;
    }

    public int x() {
        return this.position.x;
    }

    public int y() {
        return this.position.y;
    }

    public void moveUp() {
        move(0, 1);
    }

    public void moveDown() {
        move(0, -1);
    }

    public void moveLeft() {
        move(-1, 0);
    }

    public void moveRight() {
        move(1, 0);
    }

    // deltaX: -1 move left, 1 move right, 0 stay intact
    // deltaY: -1 move down, 1 move up, 0 stay intact
    private void move(int deltaX, int deltaY) {
        if (world[position.x + deltaX][position.y + deltaY] == Tileset.WALL) {
            return;
        }
        world[position.x][position.y] = Tileset.FLOOR;
        position.x += deltaX;
        position.y += deltaY;
        world[position.x][position.y] = Tileset.AVATAR;
    }
}
