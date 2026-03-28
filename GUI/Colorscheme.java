package GUI;

import models.Maze;
import models.Cell;
import pathfinding.PathfinderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MazePanel extends JPanel {

    public enum ClickMode { SET_START, SET_END, DRAW_WALL, ERASE_WALL }

    private Maze maze;
    private InfoPanel infoPanel;

    private Set<String>  visitedSet  = new HashSet<>();
    private List<Cell>   finalPath   = new ArrayList<>();
    private ClickMode    clickMode   = ClickMode.SET_START;
    private boolean      isDragging  = false;

    // Animation
    private Timer   animTimer;
    private boolean isAnimating = false;
    private boolean isPaused    = false;
    private Runnable resumeTask;

    public MazePanel() {
        setBackground(ColorScheme.BACKGROUND_DARK);
        setPreferredSize(new Dimension(600, 600));

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { handleClick(e); }
            @Override public void mousePressed(MouseEvent e)  { isDragging = true; handleClick(e); }
            @Override public void mouseReleased(MouseEvent e) { isDragging = false; }
            @Override public void mouseDragged(MouseEvent e)  {
                if (isDragging && (clickMode == ClickMode.DRAW_WALL || clickMode == ClickMode.ERASE_WALL))
                    handleClick(e);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // ─── Public API ──────────────────────────────────────────────────────────

    public void setMaze(Maze maze) {
        this.maze = maze;
        visitedSet.clear();
        finalPath.clear();
        repaint();
    }

    public Maze getMaze() { return maze; }

    public void setInfoPanel(InfoPanel ip) { this.infoPanel = ip; }

    public void setClickMode(ClickMode mode) { this.clickMode = mode; }

    public void setVisited(Set<String> v) { this.visitedSet = v; repaint(); }

    public void setPath(List<Cell> path) { this.finalPath = new ArrayList<>(path); repaint(); }

    public void clearVisualization() {
        stopAnimation();
        visitedSet.clear();
        finalPath.clear();
        repaint();
    }

    public boolean isAnimating() { return isAnimating; }
    public boolean isPaused()    { return isPaused; }

    public void togglePause() {
        isPaused = !isPaused;
        if (!isPaused && resumeTask != null) {
            Runnable r = resumeTask;
            resumeTask = null;
            SwingUtilities.invokeLater(r);
        }
    }

    public void stopAnimation() {
        if (animTimer != null) { animTimer.stop(); animTimer = null; }
        isAnimating = false;
        isPaused    = false;
        resumeTask  = null;
    }

    // ─── Animation entry point called by ControlPanel ────────────────────────

    /**
     * Animate maze generation: carves cells one batch at a time.
     * Calls onDone when finished.
     */
    public void animateGeneration(List<Cell> steps, int delayMs, Runnable onDone) {
        stopAnimation();
        visitedSet.clear();
        finalPath.clear();
        isAnimating = true;

        int[] idx = {0};
        int batchSize = Math.max(1, steps.size() / 200);

        animTimer = new Timer(Math.max(1, delayMs), null);
        animTimer.addActionListener(e -> {
            if (isPaused) { resumeTask = () -> animTimer.restart(); animTimer.stop(); return; }
            for (int b = 0; b < batchSize && idx[0] < steps.size(); b++, idx[0]++) {
                Cell c = steps.get(idx[0]);
                maze.open(c.row, c.col);
            }
            repaint();
            if (idx[0] >= steps.size()) {
                animTimer.stop();
                isAnimating = false;
                SwingUtilities.invokeLater(onDone);
            }
        });
        animTimer.start();
    }

    /**
     * Animate pathfinding: visited cells first, then final path.
     * Calls onDone when finished.
     */
    public void animateSolve(PathfinderFactory.Result result, int delayMs, Runnable onDone) {
        stopAnimation();
        visitedSet.clear();
        finalPath.clear();
        isAnimating = true;

        int[] vi = {0}, pi = {0};
        int batchSize = Math.max(1, result.visited.size() / 150);

        // Phase 1: show visited cells
        animTimer = new Timer(Math.max(1, delayMs), null);
        animTimer.addActionListener(e -> {
            if (isPaused) { resumeTask = () -> animTimer.restart(); animTimer.stop(); return; }
            for (int b = 0; b < batchSize && vi[0] < result.visited.size(); b++, vi[0]++) {
                Cell c = result.visited.get(vi[0]);
                visitedSet.add(c.row + "," + c.col);
            }
            if (infoPanel != null) infoPanel.updateVisited(vi[0]);
            repaint();
            if (vi[0] >= result.visited.size()) {
                animTimer.stop();
                // Phase 2: draw final path
                animTimer = new Timer(Math.max(1, delayMs * 2), null);
                animTimer.addActionListener(e2 -> {
                    if (isPaused) { resumeTask = () -> animTimer.restart(); animTimer.stop(); return; }
                    if (pi[0] < result.path.size()) {
                        finalPath.add(result.path.get(pi[0]++));
                        if (infoPanel != null) infoPanel.updatePathLength(pi[0]);
                        repaint();
                    } else {
                        animTimer.stop();
                        isAnimating = false;
                        SwingUtilities.invokeLater(onDone);
                    }
                });
                animTimer.start();
            }
        });
        animTimer.start();
    }

    // ─── Click / drag interaction ─────────────────────────────────────────────

    private void handleClick(MouseEvent e) {
        if (maze == null) return;
        int[] rc = pixelToCell(e.getX(), e.getY());
        if (rc == null) return;
        int r = rc[0], c = rc[1];
        if (!maze.inBounds(r, c)) return;

        switch (clickMode) {
            case SET_START:
                if (maze.isOpen(r, c)) { maze.setStart(new Cell(r, c)); repaint(); }
                break;
            case SET_END:
                if (maze.isOpen(r, c)) { maze.setEnd(new Cell(r, c)); repaint(); }
                break;
            case DRAW_WALL:
                maze.setCell(r, c, 1); repaint();
                break;
            case ERASE_WALL:
                maze.setCell(r, c, 0); repaint();
                break;
        }
    }

    private int[] pixelToCell(int px, int py) {
        if (maze == null) return null;
        int cs   = cellSize();
        int offX = (getWidth()  - maze.getCols() * cs) / 2;
        int offY = (getHeight() - maze.getRows() * cs) / 2;
        int c = (px - offX) / cs;
        int r = (py - offY) / cs;
        if (r < 0 || r >= maze.getRows() || c < 0 || c >= maze.getCols()) return null;
        return new int[]{r, c};
    }

    // ─── Rendering ───────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (maze == null) { drawPlaceholder(g2); return; }

        int cs   = cellSize();
        int offX = (getWidth()  - maze.getCols() * cs) / 2;
        int offY = (getHeight() - maze.getRows() * cs) / 2;

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                int x = offX + c * cs, y = offY + r * cs;
                Color col = getCellColor(r, c);
                g2.setColor(col);
                g2.fillRect(x + 1, y + 1, cs - 2, cs - 2);
            }
        }
    }

    private Color getCellColor(int r, int c) {
        Cell start = maze.getStart(), end = maze.getEnd();
        if (start != null && start.row == r && start.col == c) return ColorScheme.CELL_START;
        if (end   != null && end.row   == r && end.col   == c) return ColorScheme.CELL_END;
        if (maze.isWall(r, c)) return ColorScheme.CELL_WALL;
        if (isOnFinalPath(r, c))       return ColorScheme.CELL_FINAL_PATH;
        if (visitedSet.contains(r + "," + c)) return ColorScheme.CELL_VISITED;
        return ColorScheme.CELL_PATH;
    }

    private boolean isOnFinalPath(int r, int c) {
        for (Cell cell : finalPath) if (cell.row == r && cell.col == c) return true;
        return false;
    }

    private void drawPlaceholder(Graphics2D g2) {
        int gs = 28;
        int cols = getWidth()  / gs;
        int rows = getHeight() / gs;
        g2.setColor(ColorScheme.GRID_LINE);
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                g2.drawRect(c * gs, r * gs, gs, gs);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(ColorScheme.TEXT_SECONDARY);
        String msg = "Generate a maze to begin";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
    }

    private int cellSize() {
        if (maze == null) return 20;
        int maxCells = Math.max(maze.getRows(), maze.getCols());
        int available = Math.min(getWidth(), getHeight()) - 10;
        return Math.max(4, available / maxCells);
    }

    /** Expose visited set so ControlPanel can populate it in instant-solve mode */
    public Set<String> getVisitedSet() { return visitedSet; }

}