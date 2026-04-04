package pathfinding;

import models.Cell;
import models.Maze;

import java.util.*;

/**
 * A* search on a grid maze: unit edge costs and Manhattan distance heuristic.
 * Used by {@link PathfinderFactory}; you can also call {@link #findPath(Maze, Cell, Cell)} directly
 * after resetting cell state on the maze if needed.
 */
public final class Astar {

    private Astar() {}

    /**
     * Assumes {@link Cell} parents and visited flags are already cleared (e.g. by
     * {@link PathfinderFactory#solve(Maze, PathfinderFactory.Algorithm)}).
     */
    static PathfinderFactory.Result findPath(Maze maze, Cell start, Cell end) {
        List<Cell> order = new ArrayList<>();
        Map<Cell, Integer> gScore = new HashMap<>();
        Map<Cell, Integer> fScore = new HashMap<>();
        Set<Cell> closed = new HashSet<>();

        Comparator<Cell> byF = Comparator
                .comparingInt((Cell c) -> fScore.getOrDefault(c, Integer.MAX_VALUE))
                .thenComparingInt(Cell::getRow)
                .thenComparingInt(Cell::getCol);
        PriorityQueue<Cell> open = new PriorityQueue<>(byF);

        gScore.put(start, 0);
        fScore.put(start, manhattan(start, end));
        open.add(start);

        while (!open.isEmpty()) {
            Cell current = open.poll();
            if (!closed.add(current)) {
                continue;
            }
            order.add(current);
            if (current.equals(end)) {
                return new PathfinderFactory.Result(order, reconstruct(start, end));
            }
            int gCur = gScore.getOrDefault(current, Integer.MAX_VALUE);
            for (Cell nb : maze.getNeighbors(current)) {
                int tentative = gCur + 1;
                if (tentative < gScore.getOrDefault(nb, Integer.MAX_VALUE)) {
                    nb.setParent(current);
                    gScore.put(nb, tentative);
                    fScore.put(nb, tentative + manhattan(nb, end));
                    open.add(nb);
                }
            }
        }
        return new PathfinderFactory.Result(order, Collections.emptyList());
    }

    private static int manhattan(Cell a, Cell b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private static List<Cell> reconstruct(Cell start, Cell end) {
        if (end == null) {
            return Collections.emptyList();
        }
        LinkedList<Cell> rev = new LinkedList<>();
        Cell cur = end;
        while (cur != null) {
            rev.addFirst(cur);
            if (cur.equals(start)) {
                break;
            }
            cur = cur.getParent();
        }
        if (rev.isEmpty() || !rev.getFirst().equals(start)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(rev);
    }

    /** Convenience: run with the same contract as other {@link PathfinderFactory} algorithms. */
    public static PathfinderFactory.Result solve(Maze maze) {
        return PathfinderFactory.solve(maze, PathfinderFactory.Algorithm.ASTAR);
    }
}
