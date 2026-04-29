package viewer.cui;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import controller.CommandTetris;
import controller.Controller;
import model.Package;
import model.exception.UnattachedObserverException;
import model.figure.FigureDescriptor;
import observation.Subject;
import viewer.View;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConsoleUI extends View {
    private static final int FIELD_X = 10;
    private static final int FIELD_Y = 20;

    private final Screen screen;
    private final Controller controller;
    private final Subject subject;
    private final TetrisModelAccess modelAccess;

    private volatile boolean running = true;
    private volatile boolean inGame = false;
    private volatile boolean waitingForAuth = false;

    public ConsoleUI(Controller controller, Subject subject) {
        this.controller = controller;
        this.subject = subject;
        this.modelAccess = new TetrisModelAccess(controller);

        screen = TerminalFacade.createScreen();
        screen.startScreen();

        // Запускаем поток обработки клавиш
        startKeyListener();

        // Запускаем основной поток UI
        this.start();
    }

    private void startKeyListener() {
        Thread keyListener = new Thread(() -> {
            while (running) {
                if (waitingForAuth) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                    continue;
                }

                Key key = screen.readInput();
                if (key == null) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                    continue;
                }

                if (inGame) {
                    switch (key.getKind()) {
                        case ArrowLeft -> controller.execute(CommandTetris.Left);
                        case ArrowRight -> controller.execute(CommandTetris.Right);
                        case ArrowUp -> controller.execute(CommandTetris.Up);
                        case ArrowDown -> controller.execute(CommandTetris.Down);
                        case Escape -> {
                            controller.execute(CommandTetris.Pause);
                            showMenu();
                        }
                        case End -> exit();
                    }
                } else if (key.getKind() == Key.Kind.Escape) {
                    exit();
                }
            }
        });
        keyListener.setDaemon(true);
        keyListener.start();
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    break;
                }
            }

            Package pkg;
            try {
                pkg = (Package) subject.getInfo(this);
                if (pkg == null) continue;
            } catch (UnattachedObserverException e) {
                continue;
            }

            switch (pkg.getState()) {
                case defaultState -> {
                    inGame = true;
                    waitingForAuth = false;
                    drawGame(pkg);
                }
                case onlyAboutState -> showAbout();
                case onlyScoresState -> showScores(pkg);
                case authState -> showAuth(pkg);
            }
        }
    }

    private void drawGame(Package pkg) {
        synchronized (screen) {
            screen.clear();

            int[] gameField = pkg.getGameField();
            if (gameField != null) {
                drawField(gameField);
            }

            FigureDescriptor fdNow = pkg.getFallingFigureDescriptor();
            if (fdNow != null) {
                drawFallingFigure(fdNow, pkg);
            }

            FigureDescriptor fdNext = pkg.getNextFigureDescriptor();
            if (fdNext != null) {
                drawNextFigure(fdNext, pkg);
            }

            drawInfo(pkg);
            drawBorder();

            screen.refresh();
        }
    }

    private void showAuth(Package pkg) {
        waitingForAuth = true;
        inGame = false;

        StringBuilder name = new StringBuilder();

        synchronized (screen) {
            screen.clear();
            screen.putString(10, 10, "=== NEW GAME ===", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 12, "Enter your name:", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 14, "> ", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.refresh();
        }

        while (running) {
            Key key = screen.readInput();
            if (key == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            switch (key.getKind()) {
                case Enter -> {
                    String playerName = name.toString();
                    if (playerName.isEmpty()) {
                        playerName = "Anonymous";
                    }
                    Package.setNickname(playerName);
                    if (pkg != null) {
                        pkg.getPlayersStatistics().putIfAbsent(playerName, 0);
                    }

                    // Запускаем игру через модель
                    modelAccess.startNewGame();

                    waitingForAuth = false;
                    return;
                }
                case Backspace -> {
                    if (name.length() > 0) {
                        name.deleteCharAt(name.length() - 1);
                        updateNameDisplay(name.toString());
                    }
                }
                case NormalKey -> {
                    char c = key.getCharacter();
                    if (Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-') {
                        name.append(c);
                        updateNameDisplay(name.toString());
                    }
                }
                case End -> exit();
                default -> {}
            }
        }
    }

    private void updateNameDisplay(String name) {
        synchronized (screen) {
            screen.clear();
            screen.putString(10, 10, "=== NEW GAME ===", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 12, "Enter your name:", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 14, "> " + name + "_", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.refresh();
        }
    }

    private void showScores(Package pkg) {
        inGame = false;

        synchronized (screen) {
            screen.clear();

            Map<String, Integer> stat = pkg.getPlayersStatistics();
            Set<Map.Entry<String, Integer>> records = stat.entrySet();
            Iterator<Map.Entry<String, Integer>> iRecords = records.iterator();

            screen.putString(10, 5, "=== HIGH SCORES ===", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            int line = 7;
            for (int i = 0; i < Package.POSITIONS_IN_STAT && iRecords.hasNext(); i++) {
                Map.Entry<String, Integer> record = iRecords.next();
                screen.putString(10, line, (i + 1) + ". " + record.getKey() + " --- " + record.getValue(),
                        Terminal.Color.WHITE, Terminal.Color.MAGENTA);
                line++;
            }
            screen.putString(10, line + 2, "Press ESC to continue", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.refresh();
        }

        waitForEscape();
        controller.execute(CommandTetris.Resume);
    }

    private void showAbout() {
        inGame = false;

        synchronized (screen) {
            screen.clear();
            screen.putString(10, 5, "=== ABOUT ===", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 7, "Tetris Game v1.0", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 10, "Controls:", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 11, "← → - Move", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 12, "↑ - Rotate", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 13, "↓ - Speed up", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 14, "ESC - Pause/Menu", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 16, "Press ESC to continue", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.refresh();
        }

        waitForEscape();
        controller.execute(CommandTetris.Resume);
    }

    private void showMenu() {
        inGame = false;

        synchronized (screen) {
            screen.clear();
            screen.putString(10, 8, "=== MENU ===", Terminal.Color.WHITE, Terminal.Color.MAGENTA);
            screen.putString(10, 10, "1 - New Game", Terminal.Color.WHITE, Terminal.Color.GREEN);
            screen.putString(10, 11, "2 - High Scores", Terminal.Color.WHITE, Terminal.Color.GREEN);
            screen.putString(10, 12, "3 - About", Terminal.Color.WHITE, Terminal.Color.GREEN);
            screen.putString(10, 14, "ESC - Resume", Terminal.Color.WHITE, Terminal.Color.GREEN);
            screen.refresh();
        }

        while (running) {
            Key key = screen.readInput();
            if (key == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            if (key.getKind() == Key.Kind.Escape) {
                controller.execute(CommandTetris.Resume);
                inGame = true;
                return;
            } else if (key.getKind() == Key.Kind.NormalKey) {
                char c = key.getCharacter();
                if (c == '1') {
                    controller.execute(CommandTetris.NewGamePrepare);
                    return;
                } else if (c == '2') {
                    controller.execute(CommandTetris.HighScores);
                    return;
                } else if (c == '3') {
                    controller.execute(CommandTetris.About);
                    return;
                }
            }
        }
    }

    private void waitForEscape() {
        while (running) {
            Key key = screen.readInput();
            if (key != null && key.getKind() == Key.Kind.Escape) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void drawField(int[] gameField) {
        Terminal.Color[] colors = Terminal.Color.values();
        for (int i = 0; i < FIELD_Y; i++) {
            for (int j = 0; j < FIELD_X; j++) {
                int val = gameField[i * FIELD_X + j];
                if (val != 0) {
                    screen.putString(j + 1, i + 1, "#", Terminal.Color.WHITE, colors[val % colors.length]);
                } else {
                    screen.putString(j + 1, i + 1, ".", Terminal.Color.WHITE, Terminal.Color.CYAN);
                }
            }
        }
    }

    private void drawFallingFigure(FigureDescriptor fd, Package pkg) {
        Terminal.Color[] colors = Terminal.Color.values();
        for (int i = 0; i < fd.getLength(); i++) {
            for (int j = 0; j < fd.getWidth(); j++) {
                if (fd.getCurrentView()[i * fd.getWidth() + j] == 1) {
                    int x = fd.getPosX() + j + 1;
                    int y = fd.getPosY() + i + 1;
                    screen.putString(x, y, "@", Terminal.Color.WHITE, colors[pkg.getCurrColor() % colors.length]);
                }
            }
        }
    }

    private void drawNextFigure(FigureDescriptor fd, Package pkg) {
        Terminal.Color[] colors = Terminal.Color.values();
        for (int i = 0; i < fd.getInitCondLen(); i++) {
            for (int j = 0; j < fd.getInitCondWidth(); j++) {
                if (fd.getAllPossibleView()[0][i * fd.getInitCondWidth() + j] == 1) {
                    screen.putString(15 + j, 2 + i, "@", Terminal.Color.WHITE, colors[pkg.getNextColor() % colors.length]);
                }
            }
        }
    }

    private void drawInfo(Package pkg) {
        screen.putString(15, 7, "Score: " + pkg.getScore(), Terminal.Color.WHITE, Terminal.Color.MAGENTA);
        screen.putString(15, 8, "Speed: " + String.format("%.1fx", pkg.getSpeed()), Terminal.Color.WHITE, Terminal.Color.MAGENTA);

        String nickname = Package.getNickname();
        if (nickname != null && !nickname.isEmpty()) {
            screen.putString(15, 10, "Player: " + nickname, Terminal.Color.WHITE, Terminal.Color.MAGENTA);
        }
    }

    private void drawBorder() {
        for (int i = 0; i <= FIELD_X + 1; i++) {
            screen.putString(i, 0, "-", Terminal.Color.WHITE, Terminal.Color.BLACK);
            screen.putString(i, FIELD_Y + 1, "-", Terminal.Color.WHITE, Terminal.Color.BLACK);
        }
        for (int i = 0; i <= FIELD_Y + 1; i++) {
            screen.putString(0, i, "|", Terminal.Color.WHITE, Terminal.Color.BLACK);
            screen.putString(FIELD_X + 1, i, "|", Terminal.Color.WHITE, Terminal.Color.BLACK);
        }
    }

    private void exit() {
        running = false;
        controller.execute(CommandTetris.Exit);
        screen.stopScreen();
        this.interrupt();
    }

    @Override
    public void update() {
        synchronized (this) {
            this.notifyAll();
        }
    }
}