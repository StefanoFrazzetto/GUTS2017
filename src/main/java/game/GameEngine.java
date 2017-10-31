package game;

import algorithms.*;
import game.entities.*;
import game.entities.Character;

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
    private GameStatus gameStatus;

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
        String filename = "Levels/MansionLevel.txt";
        loadLevel(filename);
        setGameStatus(GameStatus.RUNNING);
    }

    private void checkGameStatus() {
        for (Entity entity : entityList) {
            if (entity instanceof Enemy) {
                if (entity.getDistance(agent) == 0) {
                    setGameStatus(GameStatus.GAME_OVER);
                }
            }

            if (entity instanceof Exit) {
                if (entity.getDistance(agent) == 0) {
                    setGameStatus(GameStatus.VICTORY);
                }
            }
        }
    }

    /**
     * @return the game status
     */
    public GameStatus getGameStatus() {
        return gameStatus;
    }

    private void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
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
        int x = agent.getRow();
        int y = agent.getColumn();

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

        // Check the game status
        checkGameStatus();
    }

    public boolean isNodeFree(int x, int y) {
        if (gameGrid.getGameObjectAt(x, y) == GameObject.WALL)
            return false;

        for (Entity entity : entityList) {
            if (entity.getRow() == x && entity.getColumn() == y && !(entity instanceof Agent)) {

                if (entity instanceof Door && !((Door) entity).isOpen()) {
                    return false;
                }

                if (entity instanceof Character) {
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

        for (Enemy enemy : enemies) {
            AStarPathFinder aStarPathFinder = new AStarPathFinder(new GameMap(this), 500, false);
            Path path = aStarPathFinder.findPath(
                    new UnitMover(),
                    enemy.getRow(),
                    enemy.getColumn(),
                    agent.getRow(),
                    agent.getColumn()
            );

            if (path != null) {
                Path.Step step = path.getStep(1);
                enemy.setCoordinates(step.getX(), step.getY());
            }
        }
    }

    public void attack(Character attacker, Direction direction) {
        Character victim = null;
        ArrayList<Character> possibleTargets = new ArrayList<>();
        int attackerRow = attacker.getRow();
        int attackerColumn = attacker.getColumn();

        // if free
        // continue
        // else
        // if target
        // shoot
        // else
        // break
        switch (direction) {
            case WEST:
            case EAST:
                for (Entity entity : entityList) {
                    if (entity instanceof Character) {
                        if (entity.getRow() == attacker.getRow()) {
                            possibleTargets.add((Character) entity);
                        }
                    }
                }

                // Get the maximum distance for the weapon
                if (direction == Direction.EAST) {
                    int maxReachableColumn = attacker.getColumn() + attacker.getWeapon().getRange();
                    if (maxReachableColumn > gameGrid.getDimension().getWidth()) {
                        maxReachableColumn = (int) gameGrid.getDimension().getWidth();
                    }

                    for (int i = attacker.getColumn() + 1; i < maxReachableColumn; i++) {
                        if (isNodeFree(attackerRow, i)) {
                            continue;
                        } else {
                            for (Character character : possibleTargets) {
                                if (character.getColumn() == i) {
                                    victim = character;
                                    break;
                                }
                            }

                            // Is a WALL or a CLOSED DOOR
                            break;
                        }
                    }
                } else { // WEST
                    int maxReachableColumn = attacker.getColumn() - attacker.getWeapon().getRange();
                    if (maxReachableColumn < 0) {
                        maxReachableColumn = 0;
                    }

                    for (int i = attacker.getColumn() - 1; i > maxReachableColumn; i--) {
                        if (isNodeFree(attackerRow, i)) {
                            continue;
                        } else {
                            for (Character character : possibleTargets) {
                                if (character.getColumn() == i) {
                                    victim = character;
                                    break;
                                }
                            }

                            // Is a WALL or a CLOSED DOOR
                            break;
                        }
                    }
                }


                break;

            case NORTH:
            case SOUTH:
                for (Entity entity : entityList) {
                    if (entity instanceof Character) {
                        if (entity.getColumn() == attacker.getColumn()) {
                            possibleTargets.add((Character) entity);
                        }
                    }
                }

                // Get the maximum distance for the weapon
                if (direction == Direction.NORTH) {
                    int maxReachableRow = attacker.getRow() - attacker.getWeapon().getRange();
                    if (maxReachableRow < 0) {
                        maxReachableRow = 0;
                    }

                    for (int i = attacker.getRow() - 1; i > maxReachableRow; i--) {
                        if (isNodeFree(i, attackerColumn)) {
                            continue;
                        } else {
                            for (Character character : possibleTargets) {
                                if (character.getRow() == i) {
                                    victim = character;
                                    break;
                                }
                            }

                            // Is a WALL or a CLOSED DOOR
                            break;
                        }
                    }
                } else { // WEST
                    int maxReachableRow = attacker.getRow() + attacker.getWeapon().getRange();
                    if (maxReachableRow > gameGrid.getDimension().getHeight()) {
                        maxReachableRow = (int) gameGrid.getDimension().getHeight();
                    }

                    for (int i = attacker.getRow() + 1; i < maxReachableRow; i++) {
                        if (isNodeFree(i, attackerColumn)) {
                            continue;
                        } else {
                            for (Character character : possibleTargets) {
                                if (character.getRow() == i) {
                                    victim = character;
                                    break;
                                }
                            }

                            // Is a WALL or a CLOSED DOOR
                            break;
                        }
                    }
                }
        }

        // Check that there is a victim (target) and it's in range
        if (victim != null) {
            attacker.attack(victim);

            if (victim.getHealth() <= 0) {
                if (victim instanceof Agent) {
                    System.out.println("GAME OVER!");
                }

                if (victim instanceof Enemy) {
                    System.out.println("You KILLED AN ENEMY!");
                    entityList.remove(victim);
                }
            }
        }

        System.out.println("Shooting direction: " + direction);
        System.out.println("Victim: " + victim);

        // Let's move the enemies
//        moveEnemies();

    }

    /**
     * @return the agent
     */
    public Agent getAgent() {
        return agent;
    }

    /**
     * Initialize the agent
     */
    private void initAgent() {
        this.agent = new Agent();
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
