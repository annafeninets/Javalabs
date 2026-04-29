package main;

import controller.CommandTetris;
import controller.Controller;
import controller.TetrisController;
import model.Tetris;
import viewer.View;
import viewer.cui.ConsoleUI;
import viewer.gui.GraphicUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Tetris game = new Tetris();
        Controller controller = new TetrisController(game);

        StartBy start = StartBy.SWING;

        switch(start){
            case CONSOLE -> {
                View ui = new ConsoleUI(controller, game);
                game.attach(ui);
                try {
                    while (ui.getState() != Thread.State.WAITING || game.getState() != Thread.State.WAITING) {
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                controller.execute(CommandTetris.Launch);
            }
            case SWING -> {
                SwingUtilities.invokeLater(() -> {
                    GraphicUI ui = new GraphicUI(controller, game);
                    game.attach(ui);
                    controller.execute(CommandTetris.Launch);
                });
            }
        }
    }

    private enum StartBy{
        CONSOLE,
        SWING
    }
}