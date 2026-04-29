package viewer.gui;

import controller.Controller;
import model.Tetris;
import model.exception.UnattachedObserverException;
import model.Package;
import viewer.View;

import javax.swing.*;

public class GraphicUI extends View {
    private final Tetris model;
    private final MainWindow mainWindow;

    @Override
    synchronized public void run() {
        while (!Thread.interrupted()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                break;
            }

            SwingUtilities.invokeLater(() -> {
                try {
                    Package pkg = (Package) model.getInfo(GraphicUI.this);
                    if (pkg != null) {
                        System.out.println(pkg.getState());
                        switch (pkg.getState()) {
                            case defaultState -> mainWindow.updateWindow(pkg);
                            case onlyAboutState -> mainWindow.showAbout();
                            case onlyScoresState -> mainWindow.showScores(pkg);
                            case authState -> mainWindow.askAuth(pkg, model);
                        }
                    }
                } catch (UnattachedObserverException e) {
                    System.err.println("Observer error: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error in UI: " + e.getMessage());
                }
            });
        }
    }

    public GraphicUI(Controller _controller, Tetris _subject) {
        model = _subject;
        mainWindow = new MainWindow(_controller, this);
        this.start();
    }

    @Override
    synchronized public void update() {
        this.notifyAll();
    }
}