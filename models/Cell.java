package models;

public class Cell {
    private int row;
    private int col;
    private boolean isWall;
    private boolean visited;
    private Cell parent;
    
    // Constructor
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.isWall = false;
        this.visited = false;
        this.parent = null;
    }
    
    // Getters
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
    
    public boolean isWall() {
        return isWall;
    }
    
    public boolean isVisited() {
        return visited;
    }
    
    public Cell getParent() {
        return parent;
    }
    
    // Setters
    public void setWall(boolean isWall) {
        this.isWall = isWall;
    }
    
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    
    public void setParent(Cell parent) {
        this.parent = parent;
    }
    
    // Check if this cell equals another
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell cell = (Cell) obj;
        return row == cell.row && col == cell.col;
    }
    
    @Override
    public int hashCode() {
        return row * 1000 + col;  // Simple hash
    }
    
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}