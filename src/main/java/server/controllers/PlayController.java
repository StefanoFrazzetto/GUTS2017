package server.controllers;

import game.Direction;
import game.GameEngine;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.Application;
import server.responses.Status;

/**
 * PlayController
 *
 * @author stefano
 * @version 1.0.0
 */
@RestController
public class PlayController {

    GameEngine gameEngine = Application.getEngine();

    @RequestMapping("/play")
    public Status status() {

        gameEngine.initializeGame();


        return Status.fromGameEngine(gameEngine);
    }

    @RequestMapping("/play/move")
    public Status move(@RequestParam(value = "d") String direction) {

        Direction dir = Direction.fromCode(direction.charAt(0));

        gameEngine.handleMovement(dir);

        return Status.fromGameEngine(gameEngine);
    }
}
