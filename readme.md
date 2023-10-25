# Build Your Own World Design Document

**Partner 1:**
Chi Tam Nguyen

**Partner 2:**
Gia Kinh Nguyen

## Classes and Data Structures
```
class Point {
    public int x;
    public int y;
}

Purpose: this class tells where a tile is
```
```
class WorldBuilder {
    private Random random;                 // Random generator
    private TETile[][] tiles;              // 2D represation of the world
    private RoomBuilder roomBuilder;       // This variable is responsible for building rooms
    private HallwayBuilder hallwayBuilder; // This variable is responsible for building hallways
    
    // Constructor
    public WorldBuilder(int width, int height, int seed);
    
    // Build the world with the number of rooms in the range [min, max]
    public void buildWorld(int minNumRoom, int maxNumRoom);
    
    // Return the 2D representation of the world
    public TETile[][] getWorld();
}   

Purpose: this class is used to build the world
```
```
class RoomBuilder {
    private int minNumRoom;
    private int maxNumRoom;
    private final int minRoomSize = 4;      // Minimum room size
    private final int maxRoomSize = 10;     // Maximum room size
    private TETile[][] world;               // Keep tracks of the world
    private Random random;                  // Random generator
    private List<Room> rooms;               // Lists of room
    
    // Constructor
    public RoomBuilder(TETile[][] world, int minNumRoom, int maxNumRoom, Random random);
    
    // Build rooms
    public void buildRooms();
}
```

```
class HallwayBuilder {
    private TETile[][] world;
    private List<RoomBuilder.Room> rooms;
    private Random random;
    private Point diggerPosition;
    private Point start;
    private Point destination;
    private WeightedQuickUnionUF weightedQuickUnionUF;
    
    // Constructor
    public HallwayBuilder(TETile[][] world, List<RoomBuilder.Room> rooms, Random random);
    
    // Build hallway
    public void buildHallways();
}
```

## Algorithms
1. Build all random rooms first using RoomBuilder
2. Connect all rooms using HallwayBuilder, repeating the following steps:
   1. Find the shortest room around a room. If two rooms were already connected before, find another close room
   2. Connect two rooms
3. Build outdoor space (NOTHING)
    
## Persistence
