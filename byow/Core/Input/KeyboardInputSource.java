package byow.Core.Input;

import edu.princeton.cs.algs4.StdDraw;

public class KeyboardInputSource implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = false;

    public KeyboardInputSource() {
    }

    public Character getNextKey() {
        if (StdDraw.hasNextKeyTyped()) {
            return Character.toUpperCase(StdDraw.nextKeyTyped());
        }
        return null;
    }

    public boolean possibleNextInput() {
        return true;
    }
}
