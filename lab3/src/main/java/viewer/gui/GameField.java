package viewer.gui;

import model.figure.FigureDescriptor;

import javax.swing.*;
import java.awt.*;

class GameField extends JPanel {
    private static final int X_FIELD_SIZE = 10;
    private static final int Y_FIELD_SIZE = 20;
    private static final int CELL_SIZE = 25;
    private static final int BEGIN_FIELD_X = 10;
    private static final int BEGIN_FIELD_Y = 10;

    private int[] gameField;
    private byte[] figureView;
    private int figureWidth;
    private int figureLength;
    private int figurePosX;
    private int figurePosY;
    private int figureColor;

    GameField() {
        gameField = new int[X_FIELD_SIZE * Y_FIELD_SIZE];
        setBackground(new Color(229, 255, 234));
        setPreferredSize(new Dimension(
                X_FIELD_SIZE * CELL_SIZE + 2 * BEGIN_FIELD_X,
                Y_FIELD_SIZE * CELL_SIZE + 2 * BEGIN_FIELD_Y
        ));
    }

    public void updateField(int[] gf) {
        if (gf != null) {
            System.arraycopy(gf, 0, gameField, 0, gf.length);
        }
        repaint();
    }

    public void updateFallingFigure(FigureDescriptor fd, int color) {
        if (fd != null) {
            figureView = fd.getCurrentView();
            figureWidth = fd.getWidth();
            figureLength = fd.getLength();
            figurePosX = fd.getPosX();
            figurePosY = fd.getPosY();
            figureColor = color;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear background
        g.setColor(new Color(229, 255, 234));
        g.fillRect(0, 0, getWidth(), getHeight());

        drawField(g);
        drawFallingFigure(g);
        drawBorder(g);
    }

    private void drawField(Graphics g) {
        for (int i = 0; i < Y_FIELD_SIZE; ++i) {
            for (int j = 0; j < X_FIELD_SIZE; ++j) {
                int val = gameField[i * X_FIELD_SIZE + j];
                if (val != 0) {
                    g.setColor(new Color(val * 10 % 256, val * 5 % 256, val * 2 % 256));
                    g.fillRect(BEGIN_FIELD_X + j * CELL_SIZE,
                            BEGIN_FIELD_Y + i * CELL_SIZE,
                            CELL_SIZE - 1,
                            CELL_SIZE - 1);
                }
            }
        }
    }

    private void drawFallingFigure(Graphics g) {
        if (figureView == null) return;

        g.setColor(new Color(figureColor * 10 % 256, figureColor * 5 % 256, figureColor * 2 % 256));
        for (int i = 0; i < figureLength; ++i) {
            for (int j = 0; j < figureWidth; ++j) {
                if (figureView[i * figureWidth + j] == 1) {
                    g.fillRect(BEGIN_FIELD_X + (figurePosX + j) * CELL_SIZE,
                            BEGIN_FIELD_Y + (figurePosY + i) * CELL_SIZE,
                            CELL_SIZE - 1,
                            CELL_SIZE - 1);
                }
            }
        }
    }

    private void drawBorder(Graphics g) {
        g.setColor(new Color(158, 182, 185));
        int fieldWidth = X_FIELD_SIZE * CELL_SIZE;
        int fieldHeight = Y_FIELD_SIZE * CELL_SIZE;

        g.drawRect(BEGIN_FIELD_X - 2, BEGIN_FIELD_Y - 2, fieldWidth + 4, fieldHeight + 4);
        g.drawRect(BEGIN_FIELD_X - 1, BEGIN_FIELD_Y - 1, fieldWidth + 2, fieldHeight + 2);

        // Draw grid lines
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i <= X_FIELD_SIZE; i++) {
            g.drawLine(BEGIN_FIELD_X + i * CELL_SIZE, BEGIN_FIELD_Y,
                    BEGIN_FIELD_X + i * CELL_SIZE, BEGIN_FIELD_Y + fieldHeight);
        }
        for (int i = 0; i <= Y_FIELD_SIZE; i++) {
            g.drawLine(BEGIN_FIELD_X, BEGIN_FIELD_Y + i * CELL_SIZE,
                    BEGIN_FIELD_X + fieldWidth, BEGIN_FIELD_Y + i * CELL_SIZE);
        }
    }
}