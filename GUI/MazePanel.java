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

    public enum ClickMode { SET_START, SET_END, DRAW_WALL, ERASE_WALL, MANUAL_SOLVE }

    @FunctionalInterface
    public interface ManualSolveListener {
        /** Called once when the user reaches the goal cell with a valid trail. */
        void onReachedGoal(Maze maze, List<Cell> userPath);
    }

    private Maze maze;
    private InfoPanel infoPanel;
    private ManualSolveListener manualSolveListener;

    private Set<String>  visitedSet  = new HashSet<>();
    private List<Cell>   finalPath   = new ArrayList<>();
    private final List<Cell> manualTrail = new ArrayList<>();
    private ClickMode    clickMode   = ClickMode.SET_START;
    private boolean      isDragging  = false;

    // Animation
    private javax.swing.Timer animTimer;
    private javax.swing.Timer uiPulseTimer;
    private boolean isAnimating = false;
    private boolean isPaused    = false;
    private Runnable resumeTask;

    public MazePanel() {
        setBackground(ColorScheme.BACKGROUND_DARK);
        setPreferredSize(new Dimension(600, 600));

        uiPulseTimer = new javax.swing.Timer(33, e -> {
            if (maze == null) return;
            if (isAnimating || clickMode == ClickMode.MANUAL_SOLVE) repaint();
        });
        uiPulseTimer.start();

        MouseAdapter ma = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e)  { handleClick(e); }
            @Override public void mousePressed(MouseEvent e)  { isDragging = true; handleClick(e); }
            @Override public void mouseReleased(MouseEvent e) { isDragging = false; }
            @Override public void mouseDragged(MouseEvent e)  {
                if (!isDragging) return;
                if (clickMode == ClickMode.DRAW_WALL || clickMode == ClickMode.ERASE_WALL)
                    handleClick(e);
                else if (clickMode == ClickMode.MANUAL_SOLVE)
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
        manualTrail.clear();
        if (clickMode == ClickMode.MANUAL_SOLVE) {
            seedManualTrailFromStart();
        }
        repaint();
    }

    public Maze getMaze() { return maze; }

    public void setInfoPanel(InfoPanel ip) { this.infoPanel = ip; }

    public void setManualSolveListener(ManualSolveListener listener) {
        this.manualSolveListener = listener;
    }

    public void setClickMode(ClickMode mode) {
        if (clickMode == ClickMode.MANUAL_SOLVE && mode != ClickMode.MANUAL_SOLVE) {
            manualTrail.clear();
        }
        this.clickMode = mode;
        if (mode == ClickMode.MANUAL_SOLVE) {
            seedManualTrailFromStart();
            if (infoPanel != null) {
                infoPanel.setStatus("Walk: click/drag through open cells to the goal. Click trail to undo.");
            }
        }
        repaint();
    }

    public void setVisited(Set<String> v) { this.visitedSet = v; repaint(); }

    public void setPath(List<Cell> path) { this.finalPath = new ArrayList<>(path); repaint(); }

    public void clearVisualization() {
        stopAnimation();
        visitedSet.clear();
        finalPath.clear();
        manualTrail.clear();
        repaint();
    }

    /** Restart manual walk from start (used after “Clear path” in manual-solve mode). */
    public void resetManualWalk() {
        seedManualTrailFromStart();
        repaint();
    }

    public ClickMode getClickMode() {
        return clickMode;
    }

    private void seedManualTrailFromStart() {
        manualTrail.clear();
        if (maze == null) return;
        Cell s = maze.getStart();
        if (s != null && !s.isWall()) {
            manualTrail.add(s);
        }
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
        manualTrail.clear();
        isAnimating = true;

        if (maze == null || steps == null || steps.isEmpty()) {
            isAnimating = false;
            if (onDone != null) SwingUtilities.invokeLater(onDone);
            return;
        }

        int[] idx = {0};
        int batchSize = Math.max(1, steps.size() / 200);

        animTimer = new javax.swing.Timer(Math.max(1, delayMs), null);
        animTimer.addActionListener(e -> {
            if (isPaused) { resumeTask = () -> animTimer.restart(); animTimer.stop(); return; }
            for (int b = 0; b < batchSize && idx[0] < steps.size(); b++, idx[0]++) {
                Cell c = steps.get(idx[0]);
                maze.open(c.getRow(), c.getCol());
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
        manualTrail.clear();
        isAnimating = true;

        int[] vi = {0}, pi = {0};
        int batchSize = Math.max(1, result.visited.size() / 150);

        // Phase 1: show visited cells
        animTimer = new javax.swing.Timer(Math.max(1, delayMs), null);
        animTimer.addActionListener(e -> {
            if (isPaused) { resumeTask = () -> animTimer.restart(); animTimer.stop(); return; }
            for (int b = 0; b < batchSize && vi[0] < result.visited.size(); b++, vi[0]++) {
                Cell c = result.visited.get(vi[0]);
                visitedSet.add(c.getRow() + "," + c.getCol());
            }
            if (infoPanel != null) infoPanel.updateVisited(vi[0]);
            repaint();
            if (vi[0] >= result.visited.size()) {
                animTimer.stop();
                // Phase 2: draw final path (batched like phase 1 so long paths finish visibly)
                int pathBatch = Math.max(1, result.path.size() / 120);
                animTimer = new javax.swing.Timer(Math.max(1, delayMs), null);
                animTimer.addActionListener(e2 -> {
                    if (isPaused) { resumeTask = () -> animTimer.restart(); animTimer.stop(); return; }
                    for (int b = 0; b < pathBatch && pi[0] < result.path.size(); b++, pi[0]++) {
                        finalPath.add(result.path.get(pi[0]));
                    }
                    if (infoPanel != null) infoPanel.updatePathLength(finalPath.size());
                    repaint();
                    if (pi[0] >= result.path.size()) {
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
                if (maze.isOpen(r, c)) { maze.setStart(maze.getCell(r, c)); repaint(); }
                break;
            case SET_END:
                if (maze.isOpen(r, c)) { maze.setEnd(maze.getCell(r, c)); repaint(); }
                break;
            case DRAW_WALL:
                maze.setCell(r, c, 1); repaint();
                break;
            case ERASE_WALL:
                maze.setCell(r, c, 0); repaint();
                break;
            case MANUAL_SOLVE:
                handleManualSolveStep(maze.getCell(r, c));
                break;
        }
    }

    private void handleManualSolveStep(Cell clicked) {
        if (clicked == null || clicked.isWall() || maze == null) return;
        Cell start = maze.getStart();
        Cell end = maze.getEnd();
        if (start == null || end == null) return;

        if (manualTrail.isEmpty()) {
            if (clicked.equals(start)) {
                manualTrail.add(clicked);
                repaint();
            }
            return;
        }

        for (int i = 0; i < manualTrail.size(); i++) {
            if (manualTrail.get(i).equals(clicked)) {
                while (manualTrail.size() > i + 1) {
                    manualTrail.remove(manualTrail.size() - 1);
                }
                repaint();
                return;
            }
        }

        Cell last = manualTrail.get(manualTrail.size() - 1);
        if (clicked.equals(last)) return;
        if (!isOrthogonalAdjacent(last, clicked)) return;

        manualTrail.add(clicked);
        repaint();

        if (clicked.equals(end) && manualSolveListener != null) {
            manualSolveListener.onReachedGoal(maze, new ArrayList<>(manualTrail));
        }
    }

    private static boolean isOrthogonalAdjacent(Cell a, Cell b) {
        int dr = Math.abs(a.getRow() - b.getRow());
        int dc = Math.abs(a.getCol() - b.getCol());
        return (dr == 1 && dc == 0) || (dr == 0 && dc == 1);
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
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (maze == null) { drawPlaceholder(g2); return; }

        int cs   = cellSize();
        int offX = (getWidth()  - maze.getCols() * cs) / 2;
        int offY = (getHeight() - maze.getRows() * cs) / 2;

        boolean showSearchPalette = !visitedSet.isEmpty() || !finalPath.isEmpty()
                || (clickMode == ClickMode.MANUAL_SOLVE && !manualTrail.isEmpty());

        // Subtle “glass” gradient behind the maze
        Paint oldPaint = g2.getPaint();
        g2.setPaint(new GradientPaint(0, 0, ColorScheme.PANEL_GLASS, 0, getHeight(), ColorScheme.BACKGROUND_DARK));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(oldPaint);

        long now = System.nanoTime();
        float pulse = (float) (0.5 + 0.5 * Math.sin(now / 500_000_000.0)); // ~2Hz
        float ringAlpha = 0.10f + 0.20f * pulse;

        for (int r = 0; r < maze.getRows(); r++) {
            for (int c = 0; c < maze.getCols(); c++) {
                int x = offX + c * cs, y = offY + r * cs;
                Color col = getCellColor(r, c, showSearchPalette);
                g2.setColor(col);
                int pad = Math.max(1, cs / 12);
                int w = cs - pad * 2;
                int h = cs - pad * 2;
                int arc = Math.max(6, cs / 3);
                g2.fillRoundRect(x + pad, y + pad, w, h, arc, arc);

                if (maze.isWall(r, c) && cs >= 6) {
                    g2.setColor(ColorScheme.CELL_WALL_EDGE);
                    g2.drawRoundRect(x + pad, y + pad, w - 1, h - 1, arc, arc);
                    g2.setColor(ColorScheme.CELL_WALL_HIGHLIGHT);
                    g2.drawLine(x + pad + 1, y + pad + 1, x + pad + w - 2, y + pad + 1);
                    g2.drawLine(x + pad + 1, y + pad + 1, x + pad + 1, y + pad + h - 2);
                } else if (cs >= 7) {
                    g2.setColor(ColorScheme.GRID_LINE_SOFT);
                    g2.drawRoundRect(x + pad, y + pad, w - 1, h - 1, arc, arc);
                }

                // Soft glow overlay for path / manual trail to feel more “alive”
                if (cs >= 9) {
                    if (isOnFinalPath(r, c)) {
                        g2.setComposite(AlphaComposite.SrcOver.derive(0.18f));
                        g2.setColor(ColorScheme.NEON_BLUE);
                        g2.fillRoundRect(x + pad, y + pad, w, h, arc, arc);
                        g2.setComposite(AlphaComposite.SrcOver);
                    } else if (isOnManualTrail(r, c)) {
                        g2.setComposite(AlphaComposite.SrcOver.derive(0.12f));
                        g2.setColor(ColorScheme.ACCENT_PINK);
                        g2.fillRoundRect(x + pad, y + pad, w, h, arc, arc);
                        g2.setComposite(AlphaComposite.SrcOver);
                    }
                }
            }
        }

        // Pulse rings for start/end to guide attention
        Cell s = maze.getStart(), e = maze.getEnd();
        if (cs >= 10) {
            if (s != null) drawPulseRing(g2, offX, offY, cs, s.getRow(), s.getCol(), ColorScheme.CELL_START, ringAlpha);
            if (e != null) drawPulseRing(g2, offX, offY, cs, e.getRow(), e.getCol(), ColorScheme.CELL_END, ringAlpha);
        }
    }

    private void drawPulseRing(Graphics2D g2, int offX, int offY, int cs, int r, int c, Color base, float a) {
        int x = offX + c * cs;
        int y = offY + r * cs;
        int pad = Math.max(1, cs / 12);
        int w = cs - pad * 2;
        int h = cs - pad * 2;
        int arc = Math.max(6, cs / 3);
        g2.setComposite(AlphaComposite.SrcOver.derive(a));
        g2.setColor(base);
        g2.setStroke(new BasicStroke(Math.max(2f, cs / 10f)));
        g2.drawRoundRect(x + pad, y + pad, w - 1, h - 1, arc, arc);
        g2.setStroke(new BasicStroke(1f));
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private Color getCellColor(int r, int c, boolean showSearchPalette) {
        Cell start = maze.getStart(), end = maze.getEnd();
        if (start != null && start.getRow() == r && start.getCol() == c) return ColorScheme.CELL_START;
        if (end   != null && end.getRow() == r && end.getCol() == c) return ColorScheme.CELL_END;
        if (maze.isWall(r, c)) return ColorScheme.CELL_WALL;
        if (isOnFinalPath(r, c)) return ColorScheme.CELL_FINAL_PATH;
        if (isOnManualTrail(r, c)) return ColorScheme.CELL_MANUAL_TRAIL;
        if (visitedSet.contains(r + "," + c)) return ColorScheme.CELL_VISITED;
        if (showSearchPalette) return ColorScheme.CELL_UNVISITED_SEARCH;
        return ColorScheme.CELL_PATH;
    }

    private boolean isOnFinalPath(int r, int c) {
        for (Cell cell : finalPath) if (cell.getRow() == r && cell.getCol() == c) return true;
        return false;
    }

    private boolean isOnManualTrail(int r, int c) {
        for (Cell cell : manualTrail) {
            if (cell.getRow() == r && cell.getCol() == c) return true;
        }
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