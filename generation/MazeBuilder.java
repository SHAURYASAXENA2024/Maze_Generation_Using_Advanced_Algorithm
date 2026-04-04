package generation;

import models.Cell;
import models.Maze;

import java.util.*;

/** Builds mazes on {@link Maze} and returns carve order for animation. */
public final class MazeBuilder {

    private MazeBuilder() {}

    public enum Kind {
        RECURSIVE_BACKTRACKER,
        RECURSIVE_DIVISION
    }

    private static final int[][] D2 = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
    private static final int[][] D1 = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    /**
     * Recursive backtracker with 2-cell jumps (classic perfect maze). Returns cells opened in order.
     */
    public static List<Cell> recursiveBacktracker(Maze maze, Random rnd) {
        maze.fillWithWalls();
        boolean[][] vis = new boolean[maze.getRows()][maze.getCols()];
        List<Cell> steps = new ArrayList<>();
        carve(maze, 0, 0, vis, steps, rnd);
        openCornersIfNeeded(maze);
        return steps;
    }

    private static void carve(Maze maze, int r, int c, boolean[][] vis, List<Cell> steps, Random rnd) {
        if (!maze.inBounds(r, c) || vis[r][c]) return;
        vis[r][c] = true;
        maze.open(r, c);
        steps.add(maze.getCell(r, c));

        List<Integer> order = Arrays.asList(0, 1, 2, 3);
        Collections.shuffle(order, rnd);
        for (int k : order) {
            int nr = r + D2[k][0], nc = c + D2[k][1];
            if (maze.inBounds(nr, nc) && !vis[nr][nc]) {
                int br = r + D1[k][0], bc = c + D1[k][1];
                maze.open(br, bc);
                steps.add(maze.getCell(br, bc));
                carve(maze, nr, nc, vis, steps, rnd);
            }
        }
    }

    /** Walls are added (passages removed); returns cells turned into walls, in build order. */
    public static List<Cell> recursiveDivision(Maze maze, Random rnd) {
        maze.clearWalls();
        List<Cell> wallOrder = new ArrayList<>();
        divide(maze, 0, maze.getRows() - 1, 0, maze.getCols() - 1, wallOrder, rnd);
        openCornersIfNeeded(maze);
        return wallOrder;
    }

    private static void divide(Maze maze, int top, int bottom, int left, int right,
                               List<Cell> wallOrder, Random rnd) {
        if (bottom - top < 2 || right - left < 2) return;

        boolean horizontal = (bottom - top) > (right - left);
        if (horizontal) {
            int wall = rnd.nextInt(bottom - top - 1) + top + 1;
            int gap = rnd.nextInt(right - left + 1) + left;
            for (int col = left; col <= right; col++) {
                if (col == gap) continue;
                Cell cell = maze.getCell(wall, col);
                cell.setWall(true);
                wallOrder.add(cell);
            }
            divide(maze, top, wall - 1, left, right, wallOrder, rnd);
            divide(maze, wall + 1, bottom, left, right, wallOrder, rnd);
        } else {
            int wall = rnd.nextInt(right - left - 1) + left + 1;
            int gap = rnd.nextInt(bottom - top + 1) + top;
            for (int row = top; row <= bottom; row++) {
                if (row == gap) continue;
                Cell cell = maze.getCell(row, wall);
                cell.setWall(true);
                wallOrder.add(cell);
            }
            divide(maze, top, bottom, left, wall - 1, wallOrder, rnd);
            divide(maze, top, bottom, wall + 1, right, wallOrder, rnd);
        }
    }

    /** Opens corner cells and assigns start / end for solving. */
    public static void openCornersIfNeeded(Maze maze) {
        int r0 = 0, c0 = 0;
        int r1 = maze.getRows() - 1, c1 = maze.getCols() - 1;
        if (maze.inBounds(r0, c0)) maze.open(r0, c0);
        if (maze.inBounds(r1, c1)) maze.open(r1, c1);
        maze.setStart(maze.getCell(r0, c0));
        maze.setEnd(maze.getCell(r1, c1));
    }

    public static List<Cell> build(Maze maze, Kind kind, Random rnd) {
        switch (kind) {
            case RECURSIVE_DIVISION:
                return recursiveDivision(maze, rnd);
            case RECURSIVE_BACKTRACKER:
            default:
                return recursiveBacktracker(maze, rnd);
        }
    }
}
