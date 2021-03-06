package client;

import algorithms.RoomFindingAlgorithm;
import com.mashape.unirest.http.exceptions.UnirestException;
import game.Direction;
import game.entities.Agent;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;

public class Player1Controller extends PlayerController {
    public Player1Controller() throws UnirestException {
        Platform.runLater(() -> {
            model = new Player1Model();
            try {
                model.initGame();
                startTimer();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected EventHandler<KeyEvent> getKeyEventEventHandler() {
        return event -> {
            Direction d = null;
            Player1Model model = (Player1Model) this.model;


            switch (event.getCode().toString().toUpperCase()) {

                // Move
                case "W":
                    model.movePlayerSync(Direction.NORTH, null);
                    break;
                case "S":
                    model.movePlayerSync(Direction.SOUTH, null);
                    break;
                case "D":
                    model.movePlayerSync(Direction.EAST, null);
                    break;
                case "A":
                    model.movePlayerSync(Direction.WEST, null);
                    break;

                // Shoot
                case "RIGHT":
                    model.shootSync(Direction.EAST);
                    break;
                case "LEFT":
                    model.shootSync(Direction.WEST);
                    break;
                case "UP":
                    model.shootSync(Direction.NORTH);
                    break;
                case "DOWN":
                    model.shootSync(Direction.SOUTH);
                    break;


                default:
                    System.out.printf("[%s]%n", event.getCode());
            }

            draw();
        };
    }

    @Override
    protected boolean showCharacters() {
        return true;
    }

    @Override
    public void draw() {
        Agent agent = model.level.getEntities().getAgents().get(0);

        Rectangle2D rectangleRoom = RoomFindingAlgorithm.findRectangularRoom(model.level, (int) agent.getX(), (int) agent.getY());

        super.draw((int) rectangleRoom.getMinX(), (int) rectangleRoom.getMinY(), (int) rectangleRoom.getWidth(), (int) rectangleRoom.getHeight());
    }

}