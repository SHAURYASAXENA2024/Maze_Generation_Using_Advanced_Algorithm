package models;

import java.util.ArrayList;
import java.util.List;

public class Maze {
    private Cell[][] grid;
    private int rows;
    private int cols;
    private Cell start;
    private Cell end;
    
    // Constructor
    public Maze(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        
        // Create all cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
        
        // Set default start and end
        this.start = grid[0][0];
        this.end = grid[rows - 1][cols - 1];
    }
    
    // Get a specific cell
    public Cell getCell(int row, int col) {
        if (isValid(row, col)) {
            return grid[row][col];
        }
        return null;
    }
    
    // Check if position is valid
    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /** Alias for {@link #isValid(int, int)} — used by the GUI layer. */
    public boolean inBounds(int row, int col) {
        return isValid(row, col);
    }

    public boolean isWall(int row, int col) {
        if (!isValid(row, col)) return true;
        return grid[row][col].isWall();
    }

    public boolean isOpen(int row, int col) {
        return isValid(row, col) && !grid[row][col].isWall();
    }

    /** Carve a passage at (row, col). */
    public void open(int row, int col) {
        if (isValid(row, col)) {
            grid[row][col].setWall(false);
        }
    }

    /** {@code v != 0} means wall, {@code 0} means open passage. */
    public void setCell(int row, int col, int v) {
        if (isValid(row, col)) {
            grid[row][col].setWall(v != 0);
        }
    }
    
    // Get neighbors of a cell (up, down, left, right)
    public List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int row = cell.getRow();
        int col = cell.getCol();
        
        // Up
        if (isValid(row - 1, col)) {
            Cell neighbor = grid[row - 1][col];
            if (!neighbor.isWall()) {
                neighbors.add(neighbor);
            }
        }
        
        // Down
        if (isValid(row + 1, col)) {
            Cell neighbor = grid[row + 1][col];
            if (!neighbor.isWall()) {
                neighbors.add(neighbor);
            }
        }
        
        // Left
        if (isValid(row, col - 1)) {
            Cell neighbor = grid[row][col - 1];
            if (!neighbor.isWall()) {
                neighbors.add(neighbor);
            }
        }
        
        // Right
        if (isValid(row, col + 1)) {
            Cell neighbor = grid[row][col + 1];
            if (!neighbor.isWall()) {
                neighbors.add(neighbor);
            }
        }
        
        return neighbors;
    }
    
    // Reset all cells (clear visited status)
    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j].setVisited(false);
                grid[i][j].setParent(null);
            }
        }
    }
    
    // Clear all walls
    public void clearWalls() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j].setWall(false);
            }
        }
    }
    
    // Fill with walls (for maze generation)
    public void fillWithWalls() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j].setWall(true);
            }
        }
    }
    
    // Getters
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public Cell getStart() {
        return start;
    }
    
    public Cell getEnd() {
        return end;
    }
    
    public Cell[][] getGrid() {
        return grid;
    }
    
    // Setters
    public void setStart(Cell start) {
        this.start = start;
    }
    
    public void setEnd(Cell end) {
        this.end = end;
    }
    
    // Print maze (for debugging)
    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = grid[i][j];
                
                if (cell.equals(start)) {
                    System.out.print("S ");  // Start
                } else if (cell.equals(end)) {
                    System.out.print("E ");  // End
                } else if (cell.isWall()) {
                    System.out.print("█ ");  // Wall
                } else if (cell.isVisited()) {
                    System.out.print("· ");  // Visited
                } else {
                    System.out.print("  ");  // Path
                }
            }
            System.out.println();
        }
    }
}