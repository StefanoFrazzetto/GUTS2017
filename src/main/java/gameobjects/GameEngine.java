package gameobjects;

import algorithms.*;

import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * GameEngine
 *
 * @author stefano
 * @version 1.0.0
 */
public class GameEngine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The agent
     */
    private Agent agent;

    /**
     * The current level
     */
    private Level currentLevel;

    /**
     * The game status
     * <p>
     * TODO: migrate to enum
     */
    private String status = "RUNNING";

    /**
     * The game grid containing the object
     */
    private GameGrid gameGrid;

    /**
     * The list of entities in the level
     */
    private List<Entity> entityList;

    public GameEngine() {
        initializeGame();
    }

    public void initializeGame() {
        String filename = "level1.txt";
        loadLevel(filename);
    }

    /**
     * Load the level from a txt file.
     *
     * @param fileName the file name
     */
    public void loadLevel(String fileName) {
        Objects.requireNonNull(fileName);

        // Import the level from a file
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        currentLevel = new Level(inputStream);
        gameGrid = currentLevel.getGameGrid();
        entityList = currentLevel.getEntityList();

        for (Entity entity : entityList) {
            if (entity instanceof Agent) {
                agent = (Agent) entity;
                break;
            }
        }

        if (agent == null)
            throw new RuntimeException("No agent found!");
    }

    /**
     * @return the game grid
     */
    public GameGrid getGameGrid() {
        return gameGrid;
    }

    /**
     * @return the list containing the entities
     */
    public List<Entity> getEntityList() {
        return entityList;
    }

    /**
     * Initialise the right player type by decoding the string.
     * <p>
     * We should probably check if the player type was already initialised
     * (this would avoid other players from intruding the game).
     *
     * @param playerTypeString the player type string
     * @throws IllegalArgumentException if string does not match any player type
     */
    public PlayerType handlePlayerInit(String playerTypeString) throws IllegalArgumentException {
        PlayerType playerType = PlayerType.fromString(playerTypeString);

        if (playerType == PlayerType.AGENT) {
            initAgent();
        }

        return playerType;
    }

    public void handleMovement(Direction direction) {
        int x = agent.getxCoordinate();
        int y = agent.getyCoordinate();

        switch (direction) {
            case NORTH:
                x -= 1;
                break;
            case EAST:
                y += 1;
                break;
            case SOUTH:
                x += 1;
                break;
            case WEST:
                y -= 1;
                break;
        }

        if (isNodeFree(x, y)) {
            agent.setCoordinates(x, y);
            moveEnemies();
        }
    }

    public boolean isNodeFree(int x, int y) {
        if (gameGrid.getGameObjectAt(x, y) == GameObject.WALL)
            return false;

        for (Entity entity : entityList) {
            if(entity.getxCoordinate() == x && entity.getyCoordinate() == y && !(entity instanceof Agent)) {

                if (entity instanceof Door && !((Door) entity).isOpen()) {
                    return false;
                }


            }
        }

        return true;
    }

    /**
     * 100% money back guarantee
     */
    public Set<Node> freeAdjacentNodes(Node node) {
        Set<Node> nodes = new HashSet<>();


        if (isNodeFree(node.getX() - 1, node.getY()))
            nodes.add(new Node(node.getX() - 1, node.getY()));


        if (isNodeFree(node.getX() + 1, node.getY()))
            nodes.add(new Node(node.getX() + 1, node.getY()));

        if (isNodeFree(node.getX(), node.getY() + 1))
            nodes.add(new Node(node.getX(), node.getY() + 1));

        if (isNodeFree(node.getX(), node.getY() - 1))
            nodes.add(new Node(node.getX(), node.getY() - 1));

        return nodes;
    }

    private List<Enemy> getEnemies() {
        List<Enemy> enemies = new ArrayList<>();
        for (Entity entity : entityList) {
            if (entity instanceof Enemy) {
                enemies.add((Enemy) entity);
            }
        }

        return enemies;
    }

    private void moveEnemies() {
        List<Enemy> enemies = getEnemies();


//        AStarPathFinder aStarPathFinder = new AStarPathFinder(new GameMap(), 500, false);
//        Path solution = aStarPathFinder.findPath(new UnitMover(), 4, 4, 1, 1);


        for (Enemy enemy : enemies) {
            AStarPathFinder aStarPathFinder = new AStarPathFinder(new GameMap(this), 100000000, false);
            Path path = aStarPathFinder.findPath(
                    new UnitMover(),
                    enemy.getxCoordinate(),
                    enemy.getyCoordinate(),
                    agent.getxCoordinate(),
                    agent.getyCoordinate()
            );

            System.out.println("AGENT");
            System.out.println(agent.getxCoordinate());
            System.out.println(agent.getyCoordinate());

            System.out.println("ENEMY");
            System.out.println(enemy.getxCoordinate());
            System.out.println(enemy.getyCoordinate());


            System.out.println("Move enemies 2 [FOR]");

            if (path != null) {
                Path.Step step = path.getStep(1);
                enemy.setCoordinates(step.getX(), step.getY());
                System.out.println("Move enemies 3 [Move the enemy!]");
            }
        }
    }

    /**
     * Initialize the agent
     */
    private void initAgent() {
        this.agent = new Agent();
    }

    public String getStatus() {
        return status;
    }

    /**
     * Open all the doors of the same type while closing the others.
     * <p>
     * We can only have one type of door open at a time.
     *
     * @param doorType the door type
     */
    public void openDoors(GameObject doorType) {
        for (Entity entity : entityList) {
            if (entity instanceof Door) {
                if (((Door) entity).getType() == doorType)
                    ((Door) entity).open(); // if the door matches, open it
                else
                    ((Door) entity).close(); // otherwise close it
            }
        }
    }

    public void closeDoors(GameObject doorType) {
        for (Entity entity : entityList) {
            if (entity instanceof Door && ((Door) entity).getType() == doorType) {
                ((Door) entity).close();
            }
        }
    }
}
