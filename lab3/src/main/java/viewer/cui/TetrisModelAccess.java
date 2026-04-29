package viewer.cui;

import controller.Controller;
import model.Tetris;

import java.lang.reflect.Field;

public class TetrisModelAccess {
    private final Tetris model;

    public TetrisModelAccess(Controller controller) {
        Tetris tetrisModel = null;
        try {
            Field field = controller.getClass().getDeclaredField("myModel");
            field.setAccessible(true);
            tetrisModel = (Tetris) field.get(controller);
        } catch (Exception e) {
            System.err.println("Cannot access model: " + e.getMessage());
        }
        this.model = tetrisModel;
    }

    public void startNewGame() {
        if (model != null) {
            model.startNewGame();
        }
    }
}
