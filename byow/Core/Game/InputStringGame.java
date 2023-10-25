package byow.Core.Game;

import byow.Core.Input.KeyboardInputSource;
import byow.Core.Input.StringInputDevice;
import byow.TileEngine.TETile;

public class InputStringGame extends Game {
    private String input;

    public InputStringGame(int width, int height, String input) {
        super(width, height);
        this.input = input;
        this.inputSource = new StringInputDevice(this.input);
    }

    @Override
    public TETile[][] play() {
        return handleInput();
    }
}
