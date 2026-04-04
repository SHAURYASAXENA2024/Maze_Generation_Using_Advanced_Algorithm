package GUI;

import generation.MazeBuilder;
import models.Cell;
import models.Maze;
import pathfinding.PathfinderFactory;
import pathfinding.PathfinderFactory.Algorithm;
import pathfinding.PathfinderFactory.Result;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class ControlPanel extends JPanel {

    private final MazePanel mazePanel;
    private InfoPanel infoPanel;

    private final JSpinner rowsSpinner = new JSpinner(new SpinnerNumberModel(21, 5, 101, 2));
    private final JSpinner colsSpinner = new JSpinner(new SpinnerNumberModel(31, 5, 101, 2));
    private final JComboBox<String> generatorCombo = new JComboBox<>(new String[]{
            "Recursive Backtracker", "Recursive Division"
    });
    private final JComboBox<String> solverCombo = new JComboBox<>(new String[]{
            "BFS", "DFS", "Dijkstra", "Greedy Best-First", "A*"
    });
    private final JCheckBox animateGeneration = new JCheckBox("Animate generation", true);
    private final JCheckBox animateSolving = new JCheckBox("Animate solving", true);
    private final JSlider speedSlider = new JSlider(1, 50, 12);

    private final ModernButton generateButton = new ModernButton("Generate");
    private final ModernButton solveButton = new ModernButton("Solve");
    private final ModernButton compareAlgorithmsButton = new ModernButton("Compare all algorithms");
    private final ModernButton clearPathButton = new ModernButton("Clear path");
    private final ModernButton resetButton = new ModernButton("Reset maze");
    private final ModernButton pauseResumeButton = new ModernButton("Pause");

    private final ButtonGroup modeGroup = new ButtonGroup();
    private final JRadioButton modeStart = new JRadioButton("Set start");
    private final JRadioButton modeEnd = new JRadioButton("Set end");
    private final JRadioButton modeDraw = new JRadioButton("Draw wall");
    private final JRadioButton modeErase = new JRadioButton("Erase wall");
    private final JRadioButton modeManualSolve = new JRadioButton("Solve manually");

    private final Random random = new Random();

    public ControlPanel(MazePanel mazePanel) {
        this.mazePanel = mazePanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.PANEL_DARK);
        setBorder(new EmptyBorder(8, 8, 8, 8));

        add(header("Maze size"));
        add(labeled("Rows (odd)", rowsSpinner));
        add(Box.createVerticalStrut(4));
        add(labeled("Cols (odd)", colsSpinner));
        add(Box.createVerticalStrut(12));

        add(header("Generation"));
        styleCombo(generatorCombo);
        add(generatorCombo);
        generatorCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(6));
        animateGeneration.setBackground(ColorScheme.PANEL_DARK);
        animateGeneration.setForeground(ColorScheme.TEXT_PRIMARY);
        add(animateGeneration);
        animateGeneration.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(12));

        add(header("Pathfinding"));
        styleCombo(solverCombo);
        add(solverCombo);
        solverCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(6));
        animateSolving.setBackground(ColorScheme.PANEL_DARK);
        animateSolving.setForeground(ColorScheme.TEXT_PRIMARY);
        add(animateSolving);
        animateSolving.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(12));

        add(header("Animation speed"));
        speedSlider.setBackground(ColorScheme.PANEL_DARK);
        speedSlider.setForeground(ColorScheme.NEON_BLUE);
        add(speedSlider);
        speedSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(Box.createVerticalStrut(12));

        add(header("Edit mode"));
        styleRadio(modeStart);
        styleRadio(modeEnd);
        styleRadio(modeDraw);
        styleRadio(modeErase);
        styleRadio(modeManualSolve);
        modeGroup.add(modeStart);
        modeGroup.add(modeEnd);
        modeGroup.add(modeDraw);
        modeGroup.add(modeErase);
        modeGroup.add(modeManualSolve);
        modeStart.setSelected(true);
        add(modeStart);
        add(modeEnd);
        add(modeDraw);
        add(modeErase);
        add(modeManualSolve);
        add(Box.createVerticalStrut(12));

        add(fullWidth(generateButton));
        add(Box.createVerticalStrut(6));
        add(fullWidth(solveButton));
        add(Box.createVerticalStrut(6));
        add(fullWidth(compareAlgorithmsButton));
        add(Box.createVerticalStrut(6));
        add(fullWidth(clearPathButton));
        add(Box.createVerticalStrut(6));
        add(fullWidth(resetButton));
        add(Box.createVerticalStrut(6));
        add(fullWidth(pauseResumeButton));

        wireActions();
    }

    private void wireActions() {
        modeStart.addActionListener(e -> mazePanel.setClickMode(MazePanel.ClickMode.SET_START));
        modeEnd.addActionListener(e -> mazePanel.setClickMode(MazePanel.ClickMode.SET_END));
        modeDraw.addActionListener(e -> mazePanel.setClickMode(MazePanel.ClickMode.DRAW_WALL));
        modeErase.addActionListener(e -> mazePanel.setClickMode(MazePanel.ClickMode.ERASE_WALL));
        modeManualSolve.addActionListener(e -> mazePanel.setClickMode(MazePanel.ClickMode.MANUAL_SOLVE));

        mazePanel.setManualSolveListener(this::onManualReachGoal);

        generateButton.addActionListener(e -> onGenerate());
        solveButton.addActionListener(e -> onSolve());
        compareAlgorithmsButton.addActionListener(e -> onCompareAlgorithms());
        clearPathButton.addActionListener(e -> onClearPath());
        resetButton.addActionListener(e -> onReset());
        pauseResumeButton.addActionListener(e -> onPauseResume());
    }

    public void setInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    private int delayMs() {
        return Math.max(1, 55 - speedSlider.getValue());
    }

    private MazeBuilder.Kind selectedGenerator() {
        return generatorCombo.getSelectedIndex() == 1
                ? MazeBuilder.Kind.RECURSIVE_DIVISION
                : MazeBuilder.Kind.RECURSIVE_BACKTRACKER;
    }

    private Algorithm selectedSolver() {
        switch (solverCombo.getSelectedIndex()) {
            case 1:
                return Algorithm.DFS;
            case 2:
                return Algorithm.DIJKSTRA;
            case 3:
                return Algorithm.GREEDY_BFS;
            case 4:
                return Algorithm.ASTAR;
            default:
                return Algorithm.BFS;
        }
    }

    private void onGenerate() {
        if (mazePanel.isAnimating()) return;
        int rows = (int) rowsSpinner.getValue();
        int cols = (int) colsSpinner.getValue();
        if (rows % 2 == 0) rows--;
        if (cols % 2 == 0) cols--;
        rowsSpinner.setValue(rows);
        colsSpinner.setValue(cols);

        Maze maze = new Maze(rows, cols);
        MazeBuilder.Kind kind = selectedGenerator();

        if (kind == MazeBuilder.Kind.RECURSIVE_DIVISION) {
            MazeBuilder.recursiveDivision(maze, random);
            mazePanel.stopAnimation();
            mazePanel.setMaze(maze);
            mazePanel.clearVisualization();
            if (infoPanel != null) {
                infoPanel.setStatus("Maze ready");
                infoPanel.updateMazeSize(rows, cols);
                infoPanel.resetStats();
            }
            if (mazePanel.getClickMode() == MazePanel.ClickMode.MANUAL_SOLVE) {
                mazePanel.resetManualWalk();
            }
            return;
        }

        List<Cell> steps = MazeBuilder.recursiveBacktracker(maze, random);
        if (animateGeneration.isSelected() && !steps.isEmpty()) {
            maze.fillWithWalls();
            mazePanel.setMaze(maze);
            mazePanel.clearVisualization();
            if (infoPanel != null) {
                infoPanel.setStatus("Generating…");
                infoPanel.updateMazeSize(rows, cols);
            }
            mazePanel.animateGeneration(steps, delayMs(), () -> {
                MazeBuilder.openCornersIfNeeded(maze);
                mazePanel.repaint();
                if (infoPanel != null) infoPanel.setStatus("Maze ready");
                if (mazePanel.getClickMode() == MazePanel.ClickMode.MANUAL_SOLVE) {
                    mazePanel.resetManualWalk();
                }
            });
        } else {
            mazePanel.stopAnimation();
            mazePanel.setMaze(maze);
            mazePanel.clearVisualization();
            if (infoPanel != null) {
                infoPanel.setStatus("Maze ready");
                infoPanel.updateMazeSize(rows, cols);
                infoPanel.resetStats();
            }
        }
    }

    private void onCompareAlgorithms() {
        Maze maze = mazePanel.getMaze();
        if (maze == null || mazePanel.isAnimating()) return;
        Cell s = maze.getStart();
        Cell g = maze.getEnd();
        if (s == null || g == null || s.isWall() || g.isWall()) {
            if (infoPanel != null) infoPanel.setStatus("Set valid start and end on open cells.");
            return;
        }
        AlgorithmCompareDialog.show(this, maze, mazePanel, infoPanel);
    }

    private void onSolve() {
        Maze maze = mazePanel.getMaze();
        if (maze == null || mazePanel.isAnimating()) return;
        Cell s = maze.getStart();
        Cell g = maze.getEnd();
        if (s == null || g == null || s.isWall() || g.isWall()) {
            if (infoPanel != null) infoPanel.setStatus("Set valid start and end on open cells.");
            return;
        }

        Algorithm alg = selectedSolver();
        Result result = PathfinderFactory.solve(maze, alg);

        if (animateSolving.isSelected()) {
            mazePanel.clearVisualization();
            if (infoPanel != null) {
                infoPanel.setStatus("Solving…");
                infoPanel.resetStats();
            }
            mazePanel.animateSolve(result, delayMs(), () -> {
                if (infoPanel != null) {
                    infoPanel.setStatus(result.path.isEmpty() ? "No path" : "Done");
                    infoPanel.updateVisited(result.visited.size());
                    infoPanel.updatePathLength(result.path.size());
                }
            });
        } else {
            mazePanel.stopAnimation();
            java.util.HashSet<String> vis = new java.util.HashSet<>();
            for (Cell c : result.visited) {
                vis.add(c.getRow() + "," + c.getCol());
            }
            mazePanel.setVisited(vis);
            mazePanel.setPath(result.path);
            if (infoPanel != null) {
                infoPanel.setStatus(result.path.isEmpty() ? "No path" : "Done");
                infoPanel.updateVisited(result.visited.size());
                infoPanel.updatePathLength(result.path.size());
            }
        }
    }

    private void onClearPath() {
        if (mazePanel.isAnimating()) return;
        mazePanel.clearVisualization();
        if (mazePanel.getClickMode() == MazePanel.ClickMode.MANUAL_SOLVE) {
            mazePanel.resetManualWalk();
        }
        if (infoPanel != null) infoPanel.resetStats();
    }

    private void onManualReachGoal(Maze maze, List<Cell> userPath) {
        int userSteps = userPath.size();
        Result optimal = PathfinderFactory.solve(maze, Algorithm.BFS);
        int best = optimal.path.size();

        mazePanel.stopAnimation();
        mazePanel.clearVisualization();
        mazePanel.setPath(optimal.path);

        String msg = String.format(
                "You reached the goal!\n\nYour path: %d cells\nShortest path: %d cells",
                userSteps, best);
        if (userSteps > best) {
            msg += "\n\nThe blue line is one shortest route.";
        } else if (userSteps == best && best > 0) {
            msg += "\n\nThat is optimal — nice work!";
        }
        JOptionPane.showMessageDialog(mazePanel, msg, "Solved", JOptionPane.INFORMATION_MESSAGE);

        if (infoPanel != null) {
            infoPanel.setStatus("Shortest path shown (blue)");
            infoPanel.updateVisited(0);
            infoPanel.updatePathLength(best);
        }
    }

    private void onReset() {
        if (mazePanel.isAnimating()) return;
        mazePanel.stopAnimation();
        mazePanel.setMaze(null);
        if (infoPanel != null) {
            infoPanel.setStatus("Reset");
            infoPanel.updateMazeSize(-1, -1);
            infoPanel.resetStats();
        }
    }

    private void onPauseResume() {
        if (!mazePanel.isAnimating()) return;
        mazePanel.togglePause();
        pauseResumeButton.setText(mazePanel.isPaused() ? "Resume" : "Pause");
    }

    public ModernButton getGenerateButton() { return generateButton; }
    public ModernButton getSolveButton() { return solveButton; }
    public ModernButton getClearPathButton() { return clearPathButton; }
    public ModernButton getResetButton() { return resetButton; }
    public ModernButton getPauseResumeButton() { return pauseResumeButton; }

    private static JLabel header(String t) {
        JLabel l = new JLabel(t.toUpperCase());
        l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        l.setForeground(ColorScheme.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JComponent labeled(String name, JComponent c) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setBackground(ColorScheme.PANEL_DARK);
        JLabel lb = new JLabel(name);
        lb.setForeground(ColorScheme.TEXT_PRIMARY);
        p.add(lb, BorderLayout.WEST);
        p.add(c, BorderLayout.CENTER);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return p;
    }

    private static void styleCombo(JComboBox<String> cb) {
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cb.setBackground(ColorScheme.BUTTON_BG);
        cb.setForeground(ColorScheme.TEXT_PRIMARY);
    }

    private static void styleRadio(JRadioButton r) {
        r.setBackground(ColorScheme.PANEL_DARK);
        r.setForeground(ColorScheme.TEXT_PRIMARY);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private static JComponent fullWidth(ModernButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        return b;
    }
}
