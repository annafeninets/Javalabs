package viewer.gui;

import controller.CommandTetris;
import controller.Controller;
import model.Package;
import model.Tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class MainWindow extends JFrame {
    private final Controller controller;
    private final GraphicUI myGui;
    private final GameField gameField;
    private final RightBank rightBank;
    private Package pkgNow = null;

    public void updateWindow(Package pkg) {
        rightBank.setPkg(pkg);
        pkgNow = pkg;

        rightBank.updateTextSection(pkg.getScore(), pkg.getSpeed());
        rightBank.updateNextFigure(pkg.getNextFigureDescriptor(), pkg.getNextColor());
        gameField.updateFallingFigure(pkg.getFallingFigureDescriptor(), pkg.getCurrColor());
        gameField.updateField(pkg.getGameField());

        gameField.repaint();
        rightBank.repaint();
    }

    public void askAuth(Package pkg, Tetris model) {
        controller.execute(CommandTetris.Pause);
        String name = JOptionPane.showInputDialog(this,
                "Enter your name for the new game:",
                "Player Name", JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            Package.setNickname(name.trim());
        } else if (name == null) {
            controller.execute(CommandTetris.Resume);
            requestFocus(true);
            return;
        } else {
            Package.setNickname("Anonymous");
        }

        // Сохраняем имя в статистике
        if (pkg != null && Package.getNickname() != null) {
            pkg.getPlayersStatistics().putIfAbsent(Package.getNickname(), 0);
        }

        requestFocus(true);

        // Запускаем новую игру напрямую через модель
        model.startNewGame();
    }

    public void showScores(Package pkg) {
        controller.execute(CommandTetris.Pause);

        Map<String, Integer> stat = pkg.getPlayersStatistics();
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("=== HIGH SCORES ===\n\n");

        java.util.List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(stat.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedList) {
            if (count >= Package.POSITIONS_IN_STAT) break;
            msgBuilder.append(++count).append(". ")
                    .append(entry.getKey()).append(" --- ")
                    .append(entry.getValue()).append("\n");
        }

        if (count == 0) {
            msgBuilder.append("No scores yet. Play a game!");
        }

        JTextArea textArea = new JTextArea(msgBuilder.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 250));

        JOptionPane.showMessageDialog(this, scrollPane, "Game Records", JOptionPane.INFORMATION_MESSAGE);

        requestFocus(true);
        controller.execute(CommandTetris.Resume);
    }

    public void getBackFocus() {
        this.requestFocus(true);
    }

    public void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Tetris Game\nVersion 1.0\n\n" +
                        "Controls:\n" +
                        "← → - Move left/right\n" +
                        "↑ - Rotate\n" +
                        "↓ - Move down faster\n" +
                        "ESC - Pause/Menu\n\n" +
                        "New Game will ask for your name again.",
                "About", JOptionPane.INFORMATION_MESSAGE);
        controller.execute(CommandTetris.Resume);
        requestFocus(true);
    }

    MainWindow(Controller controller, GraphicUI myGui) {
        super("Tetris");
        this.myGui = myGui;
        this.controller = controller;

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        gameField = new GameField();
        rightBank = new RightBank(controller, this);

        setLayout(new BorderLayout());
        add(gameField, BorderLayout.CENTER);
        add(rightBank, BorderLayout.EAST);

        setPreferredSize(new Dimension(600, 600));
        setMinimumSize(new Dimension(600, 600));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) controller.execute(CommandTetris.Left);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) controller.execute(CommandTetris.Right);
                if (e.getKeyCode() == KeyEvent.VK_UP) controller.execute(CommandTetris.Up);
                if (e.getKeyCode() == KeyEvent.VK_DOWN) controller.execute(CommandTetris.Down);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) controller.execute(CommandTetris.Resume);
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                Object[] options = {"Yes", "No"};
                int rc = JOptionPane.showOptionDialog(
                        event.getWindow(), "Close window?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null,
                        options, options[0]);

                if (rc == 0) {
                    event.getWindow().setVisible(false);
                    if (pkgNow != null) {
                        String nickname = Package.getNickname();
                        if (nickname != null && pkgNow.getScore() > 0) {
                            pkgNow.getPlayersStatistics().merge(nickname, pkgNow.getScore(), Integer::max);
                        }
                    }
                    controller.execute(CommandTetris.Exit);
                    myGui.interrupt();
                    dispose();
                }
            }
        });

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setVisible(true);
    }
}