package model;

import model.exception.*;
import model.gamefield.GameField2D;

import observation.Subject;
import observation.Observer;

import java.io.*;
import java.util.*;

public class Tetris extends Thread implements Subject {
    private static final byte N_FIGURES_IN_TETRIS = 7;
    private static final byte COLOR_STEP = 10;
    private static final int TIME_FALLING_PERIOD_START = 1000;
    private static final String STAT_FILE_PATH = "tetris_scores.dat";

    private final Map<Observer, Package> observers = new HashMap<>();

    private volatile boolean gameRunning = false;
    private volatile boolean gameOver = false;
    private volatile boolean waitingForAuth = false;

    private final Random randomGenerator;
    private int figureFallingPeriod = TIME_FALLING_PERIOD_START;
    private int gameScore = 0;

    private final Figure[] tetrisFigures;
    private Figure fallingFigure;
    private int nextFigureNum;
    private final GameField gameField;
    private int currColor;
    private int nextColor;

    private Map<String, Integer> playersStat = new TreeMap<>();
    private volatile Package.PackageState stateForOutcomePackage = Package.PackageState.defaultState;

    public Tetris() {
        this(10, 20);
    }

    public Tetris(int xDim, int yDim) {
        gameField = new GameField2D(xDim, yDim);
        tetrisFigures = Figure.values();
        randomGenerator = new Random();

        // Инициализация
        fallingFigure = tetrisFigures[0];
        nextFigureNum = 0;
        currColor = 1;
        nextColor = currColor + COLOR_STEP;

        readGameStatFile();
        this.start();
    }

    @Override
    synchronized public void run() {
        while (!interrupted()) {
            // Если ждем авторизацию - спим
            if (waitingForAuth) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            // Если игра не запущена или закончена - ждем
            if (!gameRunning || gameOver) {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            // Игровой цикл
            down();
            stateForOutcomePackage = Package.PackageState.defaultState;
            signalizeAll();

            try {
                Thread.sleep(figureFallingPeriod);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void readGameStatFile() {
        File statFile = new File(STAT_FILE_PATH);
        if (statFile.exists()) {
            try (ObjectInputStream bis = new ObjectInputStream(new FileInputStream(statFile))) {
                playersStat = (TreeMap<String, Integer>) bis.readObject();
                System.out.println("Scores loaded: " + playersStat.size() + " records");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Could not read scores: " + e.getMessage());
                playersStat = new TreeMap<>();
            }
        } else {
            playersStat = new TreeMap<>();
            System.out.println("No scores file found, creating new");
        }
    }

    private void writeGameStatFile() {
        try (ObjectOutputStream bis = new ObjectOutputStream(new FileOutputStream(STAT_FILE_PATH))) {
            bis.writeObject(playersStat);
            System.out.println("Scores saved: " + playersStat.size() + " records");
        } catch (IOException e) {
            System.out.println("Could not save scores: " + e.getMessage());
        }
    }

    public void launch() {
        waitingForAuth = true;
        stateForOutcomePackage = Package.PackageState.authState;
        signalizeAll();
    }

    public void exit() {
        System.out.println("EXIT");
        gameRunning = false;
        writeGameStatFile();
        this.interrupt();
    }

    public void highScores() {
        System.out.println("HIGH SCORES");
        gameRunning = false;
        stateForOutcomePackage = Package.PackageState.onlyScoresState;
        signalizeAll();
    }

    public void about() {
        System.out.println("ABOUT");
        gameRunning = false;
        stateForOutcomePackage = Package.PackageState.onlyAboutState;
        signalizeAll();
    }

    public void newGame() {
        System.out.println("NEW GAME REQUESTED");
        gameRunning = false;
        gameOver = false;
        waitingForAuth = true;
        stateForOutcomePackage = Package.PackageState.authState;
        signalizeAll();
    }

    // Вызывается после ввода имени
    public void startNewGame() {
        System.out.println("STARTING NEW GAME");
        renewGameState();
        waitingForAuth = false;
        gameRunning = true;
        gameOver = false;
        stateForOutcomePackage = Package.PackageState.defaultState;
        signalizeAll();
        synchronized (this) {
            this.notifyAll();
        }
    }

    private void renewGameState() {
        System.out.println("RENEWING GAME STATE");
        gameField.renew();
        currColor = 1;
        nextColor = currColor + COLOR_STEP;

        gameScore = 0;
        nextFigureNum = randomGenerator.nextInt(0, N_FIGURES_IN_TETRIS);
        figureFallingPeriod = TIME_FALLING_PERIOD_START;
        generateNewFallingFigure();
    }

    public void pauseGame() {
        if (!gameOver) {
            gameRunning = false;
        }
    }

    public void continueGame() {
        System.out.println("CONTINUE");
        if (!gameOver && !waitingForAuth && !gameRunning) {
            gameRunning = true;
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    public void down() {
        if (gameRunning && !gameOver && fallingFigure != null) {
            try {
                fallingFigure.moveDown(gameField);
            } catch (ImpossibleToMoveFigureDown e) {
                figureFell();
                if (!gameOver) {
                    generateNewFallingFigure();
                }
            }
            signalizeAll();
        }
    }

    public void left() {
        if (gameRunning && !gameOver && fallingFigure != null) {
            try {
                fallingFigure.moveLeft(gameField);
            } catch (ImpossibleToMoveFigureLeft e) {
                return;
            }
            signalizeAll();
        }
    }

    public void right() {
        if (gameRunning && !gameOver && fallingFigure != null) {
            try {
                fallingFigure.moveRight(gameField);
            } catch (ImpossibleToMoveFigureRight e) {
                return;
            }
            signalizeAll();
        }
    }

    public void up() {
        if (gameRunning && !gameOver && fallingFigure != null) {
            try {
                fallingFigure.rotate(gameField);
            } catch (ImpossibleToRotateFigure e) {
                return;
            }
            signalizeAll();
        }
    }

    @Override
    public void attach(Observer obs) {
        observers.put(obs, new Package(gameField.getSizeField()));
    }

    @Override
    public void detach(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void signalizeAll() {
        observers.forEach((observer, pcg) -> observer.update());
    }

    @Override
    public Object getInfo(Observer obs) throws UnattachedObserverException {
        Package packageToObserver = observers.get(obs);
        if (packageToObserver == null) {
            throw new UnattachedObserverException(obs);
        }

        packageToObserver.setPlayersStatistics(playersStat);
        packageToObserver.setState(stateForOutcomePackage);
        packageToObserver.setScore(gameScore);
        packageToObserver.setSpeed((float) TIME_FALLING_PERIOD_START / (float) figureFallingPeriod);

        if (fallingFigure != null) {
            packageToObserver.setFigure(fallingFigure);
        }
        if (nextFigureNum < tetrisFigures.length) {
            packageToObserver.setNextFigure(tetrisFigures[nextFigureNum], nextColor);
        }

        packageToObserver.setGameField((int[]) gameField.getRepresentation());
        packageToObserver.setCurrColor(currColor);
        packageToObserver.setNextColor(nextColor);

        return packageToObserver;
    }

    private void generateNewFallingFigure() {
        currColor = nextColor;
        nextColor += COLOR_STEP;
        fallingFigure = tetrisFigures[nextFigureNum];
        fallingFigure.refresh();

        nextFigureNum = randomGenerator.nextInt(0, N_FIGURES_IN_TETRIS);
        tetrisFigures[nextFigureNum].refresh();
    }

    private void figureFell() {
        try {
            if (fallingFigure != null && fallingFigure.doesntFitOnField()) {
                gameRunning = false;
                gameOver = true;
                System.out.println("GAME OVER! Score: " + gameScore);
                String nickname = Package.getNickname();
                if (nickname != null && gameScore > 0) {
                    playersStat.merge(nickname, gameScore, Integer::max);
                    writeGameStatFile();
                }
                stateForOutcomePackage = Package.PackageState.defaultState;
                signalizeAll();
            } else if (fallingFigure != null) {
                fallingFigure.addFigureToField(gameField, currColor);
                int layersDeleted = gameField.removeFullLayers(fallingFigure.getDescriptor().getPosY(),
                        fallingFigure.getDescriptor().getLength());
                figureFallingPeriod -= layersDeleted * 100;
                if (figureFallingPeriod < 100) figureFallingPeriod = 100;
                gameScore += currColor + layersDeleted * 100;
            }
        } catch (FigureAddingToFieldException e) {
            System.out.println(e.getMessage());
        }
    }
}