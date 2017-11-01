package game;

import game.entities.Agent;
import game.entities.Enemy;
import game.entities.Entity;
import game.gridobjects.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * LevelParser
 */
public class LevelParser {

    private List<String> rawLevel;
    private Grid<Floor> floorGrid;
    private Grid<GridObject> objectsGrid;
    private Entities entities;

    private boolean parsed = false;

    private int xMaxSize = 0;
    private int yMaxSize = 0;

    /**
     * Parse the stream from a level file.
     *
     * @param stream the stream of data
     */
    public LevelParser(InputStream stream) {
        Objects.requireNonNull(stream);

        rawLevel = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            while (true) { // Read the file line by line
                String line = reader.readLine();

                // Break the loop if EOF is reached
                if (line == null) {
                    break;
                }

                // Trim the line, transform to uppercase, and add it to the Array
                line = line.trim();
                line = line.toUpperCase();
                rawLevel.add(line);
            } // END - While loop

        } catch (IOException | NullPointerException e) {
            // Cannot open/load the file
            e.printStackTrace();
        }
    }

    public Grid<Floor> getFloorGrid() {
        return floorGrid;
    }

    public Grid<GridObject> getObjectsGrid() {
        return objectsGrid;
    }

    public Entities getEntities() {
        return entities;
    }

    private void getLevelSize() {

        yMaxSize = rawLevel.size();

        for (String row : rawLevel) {
            if (row.length() > xMaxSize) {
                xMaxSize = row.length();
            }
        }
    }

    /**
     * Parse the raw level into our objects.
     *
     * @param rawLevel the raw level
     */
    private void parseRawLevel(List<String> rawLevel) {

        getLevelSize();
        floorGrid = new Grid<>(Floor.class, xMaxSize, yMaxSize);
        objectsGrid = new Grid<>(GridObject.class, xMaxSize, yMaxSize);
        entities = new Entities();
        // Loop over the List
        for (int y = 0; y < rawLevel.size(); y++) {

            // Loop over the string one char at a time because it should be the fastest way:
            // http://stackoverflow.com/questions/8894258/fastest-way-to-iterate-over-all-the-chars-in-a-string
            for (int x = 0; x < rawLevel.get(y).length(); x++) {

                char currentChar = rawLevel.get(y).charAt(x);
                Floor floor = new Floor();
                GridObject gridObject = null;
                Entity entity = null;

                switch (currentChar) {
                    case 'A': // agent
                        entity = new Agent();
                        break;

                    case 'X': // doors
                    case 'O':
                    case 'P':
                    case 'R':
                    case 'S':
                    case 'T':
                        gridObject = new Door(currentChar);
                        break;

                    case 'E': // exit
                        gridObject = new Exit();
                        break;

                    case 'H': // enemy
                        entity = new Enemy();
                        break;

                    case '.': // grass
                        floor = new Floor();
                        break;

                    case 'W': // wall
                        gridObject = new Wall();
                        break;

                    default:

                }

                if (entity != null) {
                    entity.setPosition(x, y);
                    entities.add(entity);
                }

                floorGrid.put(floor, x, y);
                objectsGrid.put(gridObject, x, y);
            }
        } // END- String loop

        parsed = true;
    } // END - List loop

    public void parse() {
        parseRawLevel(this.rawLevel);
    }

    public boolean isParsed() {
        return parsed;
    }
}
