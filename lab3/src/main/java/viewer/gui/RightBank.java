package viewer.gui;

import controller.CommandTetris;
import controller.Controller;
import model.Package;
import model.figure.FigureDescriptor;

import javax.swing.*;
import java.awt.*;

class RightBank extends JPanel {
    private static final int CELL_SIZE = 35;
    private static final int NEXT_FIGURE_SIZE = 4;

    private final Controller controller;
    private final MainWindow mainWindow;

    private byte[] nextFigureView = null;
    private int nextFigureWidth;
    private int nextFigureHeight;
    private int figureColor;
    private int score;
    private float speed;
    private Package pkg = null;

    private final JLabel scoreLabel;
    private final JLabel speedLabel;
    private final JPanel nextFigurePanel;
    private final JPanel infoPanel;

    RightBank(Controller controller, MainWindow mainWindow) {
        this.controller = controller;
        this.mainWindow = mainWindow;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(52, 77, 79));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(200, 600));

        // Title
        JLabel titleLabel = new JLabel("TETRIS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 15)));

        // Next Figure Panel
        JLabel nextFigureLabel = new JLabel("Next Figure:", SwingConstants.CENTER);
        nextFigureLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nextFigureLabel.setForeground(Color.WHITE);
        nextFigureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(nextFigureLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));

        nextFigurePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawNextFigure(g);
            }
        };
        nextFigurePanel.setPreferredSize(new Dimension(160, 160));
        nextFigurePanel.setBackground(new Color(50, 94, 98));
        nextFigurePanel.setBorder(BorderFactory.createLineBorder(new Color(88, 120, 122), 2));
        nextFigurePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(nextFigurePanel);
        add(Box.createRigidArea(new Dimension(0, 15)));

        // Info Panel (Score and Speed)
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 2, 10, 5));
        infoPanel.setBackground(new Color(88, 120, 122));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setMaximumSize(new Dimension(180, 80));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel scoreText = new JLabel("Score:");
        scoreText.setFont(new Font("Arial", Font.BOLD, 12));
        scoreText.setForeground(Color.WHITE);
        scoreText.setHorizontalAlignment(SwingConstants.RIGHT);

        scoreLabel = new JLabel("0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setForeground(new Color(255, 215, 0));
        scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel speedText = new JLabel("Speed:");
        speedText.setFont(new Font("Arial", Font.BOLD, 12));
        speedText.setForeground(Color.WHITE);
        speedText.setHorizontalAlignment(SwingConstants.RIGHT);

        speedLabel = new JLabel("1.0x");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        speedLabel.setForeground(new Color(100, 255, 100));
        speedLabel.setHorizontalAlignment(SwingConstants.LEFT);

        infoPanel.add(scoreText);
        infoPanel.add(scoreLabel);
        infoPanel.add(speedText);
        infoPanel.add(speedLabel);

        add(infoPanel);
        add(Box.createRigidArea(new Dimension(0, 15)));

        // Buttons
        add(createStyledButton("New Game", e -> {
            // Сохраняем текущий счет перед новой игрой
            if (pkg != null && Package.getNickname() != null && score > 0) {
                pkg.getPlayersStatistics().merge(Package.getNickname(), score, Integer::max);
            }
            controller.execute(CommandTetris.NewGamePrepare);
            mainWindow.getBackFocus();
        }));

        add(Box.createRigidArea(new Dimension(0, 8)));

        add(createStyledButton("High Scores", e -> {
            controller.execute(CommandTetris.HighScores);
        }));

        add(Box.createRigidArea(new Dimension(0, 8)));

        add(createStyledButton("About", e -> {
            controller.execute(CommandTetris.About);
        }));

        add(Box.createVerticalGlue());
    }
    private JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(52, 77, 79));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(160, 35));
        button.setPreferredSize(new Dimension(160, 35));
        button.addActionListener(action);
        return button;
    }


    private void drawNextFigure(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = nextFigurePanel.getWidth();
        int panelHeight = nextFigurePanel.getHeight();
        int startX = (panelWidth - NEXT_FIGURE_SIZE * CELL_SIZE) / 2;
        int startY = (panelHeight - NEXT_FIGURE_SIZE * CELL_SIZE) / 2;

        // Draw background
        g2d.setColor(new Color(50, 94, 98));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        // Draw grid
        g2d.setColor(new Color(158, 182, 185));
        for (int i = 0; i <= NEXT_FIGURE_SIZE; i++) {
            int x = startX + i * CELL_SIZE;
            int y = startY + i * CELL_SIZE;
            g2d.drawLine(x, startY, x, startY + NEXT_FIGURE_SIZE * CELL_SIZE);
            g2d.drawLine(startX, y, startX + NEXT_FIGURE_SIZE * CELL_SIZE, y);
        }

        // Draw next figure
        if (nextFigureView != null) {
            Color color = new Color(
                    figureColor * 10 % 256,
                    figureColor * 5 % 256,
                    figureColor * 2 % 256
            );
            g2d.setColor(color);

            int offsetX = (NEXT_FIGURE_SIZE - nextFigureWidth) / 2;
            int offsetY = (NEXT_FIGURE_SIZE - nextFigureHeight) / 2;

            for (int i = 0; i < nextFigureHeight; i++) {
                for (int j = 0; j < nextFigureWidth; j++) {
                    if (nextFigureView[i * nextFigureWidth + j] == 1) {
                        int x = startX + (offsetX + j) * CELL_SIZE;
                        int y = startY + (offsetY + i) * CELL_SIZE;
                        g2d.fillRect(x, y, CELL_SIZE - 1, CELL_SIZE - 1);
                    }
                }
            }
        }
    }

    public void setPkg(Package p) {
        this.pkg = p;
    }

    public void updateTextSection(int score, float speed) {
        this.score = score;
        this.speed = speed;
        scoreLabel.setText(String.valueOf(score));
        speedLabel.setText(String.format("%.1fx", speed));
    }

    public void updateNextFigure(FigureDescriptor fd, int color) {
        if (fd != null) {
            nextFigureView = fd.getAllPossibleView()[0];
            nextFigureWidth = fd.getInitCondWidth();
            nextFigureHeight = fd.getInitCondLen();
            figureColor = color;
        }
        nextFigurePanel.repaint();
    }
}