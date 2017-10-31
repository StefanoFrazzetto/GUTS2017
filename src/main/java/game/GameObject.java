package game;

import java.io.Serializable;
import java.lang.Character;

/**
 * GameObject represents the objects in a game.
 * <p>
 * Each object has a symbol which is used to decode the save files.
 */
public enum GameObject implements Serializable {
    PLAYER('A'),
    GRASS('.'),
    EXIT('Q'),
    FLOOR(' '),
    ENEMY('H'),
    WALL('W'),
    DOOR_X('X'),
    DOOR_O('O'),
    DOOR_P('P'),
    DOOR_R('R'),
    DOOR_S('S'),
    DOOR_T('T');

    private final char symbol;

    GameObject(final char symbol) {
        this.symbol = symbol;
    }

    /**
     * Return the enum associated with a char.
     *
     * If the object is not recognized, returns a wall.
     *
     * @param c - the char to look for
     * @return the {@link GameObject} corresponding to the char
     */
    public static GameObject fromChar(char c) {
        for (GameObject t : GameObject.values()) {
            if (Character.toUpperCase(c) == t.symbol) {
                return t;
            }
        }

        return WALL;
    }

    /**
     * Return the string representation of the symbol.
     *
     * @return String
     */
    public String getStringSymbol() {
        return String.valueOf(symbol);
    }

    /**
     * Returns the symbol associated with the game object.
     *
     * @return the symbol associated with the game object.
     */
    public char getCharSymbol() {
        return symbol;
    }
}