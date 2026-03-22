
import maze.core.Maze;
import maze.core.Cell;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class MazePanel extends JPanel {
    
    private Maze maze;
    private static final int CELL_SIZE = 25;
    private static final int PADDING = 20;
    
    private List<Cell> exploredCells;
    private List<Cell> pathCells;
    private Cell currentCell;
    
    public MazePanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 600));
        exploredCells = new ArrayList<>();
        pathCells = new ArrayList<>();
    }
    
    public void setMaze(Maze maze) {
        this.maze = maze;
        exploredCells.clear();
        pathCells.clear();
        currentCell = null;
        repaint();
    }
    
    public Maze getMaze() {
        return maze;
    }
    
    public void addExploredCell(Cell cell) {
        exploredCells.add(cell);
        repaint();
    }
    
    public void setPath(List<Cell> path) {
        this.pathCells = path;
        repaint();
    }
    
    public void setCurrentCell(Cell cell) {
        this.currentCell = cell;
        repaint();
    }
    
    public void clearVisualization() {
        exploredCells.clear();
        pathCells.clear();
        currentCell = null;
        if (maze != null) {
            maze.reset();
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (maze == null) {
            // Draw "No Maze" message
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String msg = "Click 'Generate Maze' to start";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(msg)) / 2;
            int y = getHeight() / 2;
            g.drawString(msg, x, y);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        int rows = maze.getRows();
        int cols = maze.getCols();
        
        // Draw all cells
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = maze.getCell(row, col);
                drawCell(g2d, cell, row, col);
            }
        }
        
        // Draw grid lines
        g2d.setColor(new Color(200, 200, 200));
        for (int row = 0; row <= rows; row++) {
            int y = PADDING + row * CELL_SIZE;
            g2d.drawLine(PADDING, y, 
                        PADDING + cols * CELL_SIZE, y);
        }
        for (int col = 0; col <= cols; col++) {
            int x = PADDING + col * CELL_SIZE;
            g2d.drawLine(x, PADDING, 
                        x, PADDING + rows * CELL_SIZE);
        }
    }
    
    private void drawCell(Graphics2D g, Cell cell, int row, int col) {
        int x = PADDING + col * CELL_SIZE;
        int y = PADDING + row * CELL_SIZE;
        
        // Determine color
        Color color = getCellColor(cell);
        
        // Fill cell
        g.setColor(color);
        g.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
        
        // Add label for start/end
        if (cell.equals(maze.getStart())) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("S", x + 8, y + 17);
        } else if (cell.equals(maze.getEnd())) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString("E", x + 8, y + 17);
        }
    }
    
    private Color getCellColor(Cell cell) {
        // Priority order: Start → End → Path → Current → Explored → Wall → Empty
        
        if (cell.equals(maze.getStart())) {
            return new Color(46, 204, 113);  // Green
        }
        
        if (cell.equals(maze.getEnd())) {
            return new Color(231, 76, 60);   // Red
        }
        
        if (pathCells.contains(cell)) {
            return new Color(241, 196, 15);  // Gold/Yellow
        }
        
        if (cell.equals(currentCell)) {
            return new Color(255, 165, 0);   // Orange
        }
        
        if (exploredCells.contains(cell)) {
            return new Color(52, 152, 219);  // Blue
        }
        
        if (cell.isWall()) {
            return new Color(44, 62, 80);    // Dark Blue-Gray
        }
        
        return Color.WHITE;                   // Empty path
    }
}