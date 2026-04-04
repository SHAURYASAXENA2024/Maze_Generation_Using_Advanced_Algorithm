package pathfinding;

import models.Cell;
import models.Maze;

import java.util.*;
import java.util.AbstractMap;

/** Runs maze pathfinding algorithms on a {@link Maze} grid graph. */
public final class PathfinderFactory {

    private PathfinderFactory() {}

    public enum Algorithm {
        BFS,
        DFS,
        DIJKSTRA,
        GREEDY_BFS,
        ASTAR
    }

    public static final class Result {
        public final List<Cell> visited;
        public final List<Cell> path;

        public Result(List<Cell> visited, List<Cell> path) {
            this.visited = visited;
            this.path = path;
        }
    }

    public static Result solve(Maze maze, Algorithm algorithm) {
        clearCellState(maze);
        Cell start = maze.getStart();
        Cell end = maze.getEnd();
        List<Cell> empty = Collections.emptyList();
        if (start == null || end == null) {
            return new Result(empty, empty);
        }
        switch (algorithm) {
            case DFS:
                return dfs(maze, start, end);
            case DIJKSTRA:
                return dijkstra(maze, start, end);
            case GREEDY_BFS:
                return greedyBestFirst(maze, start, end);
            case ASTAR:
                return Astar.findPath(maze, start, end);
            case BFS:
            default:
                return bfs(maze, start, end);
        }
    }

    private static void clearCellState(Maze maze) {
        for (Cell[] row : maze.getGrid()) {
            for (Cell c : row) {
                c.setVisited(false);
                c.setParent(null);
            }
        }
    }

    private static int manhattan(Cell a, Cell b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }

    private static List<Cell> reconstruct(Cell start, Cell end) {
        if (end == null) return Collections.emptyList();
        LinkedList<Cell> rev = new LinkedList<>();
        Cell cur = end;
        while (cur != null) {
            rev.addFirst(cur);
            if (cur.equals(start)) break;
            cur = cur.getParent();
        }
        if (rev.isEmpty() || !rev.getFirst().equals(start)) return Collections.emptyList();
        return new ArrayList<>(rev);
    }

    private static Result bfs(Maze maze, Cell start, Cell end) {
        List<Cell> order = new ArrayList<>();
        Queue<Cell> q = new ArrayDeque<>();
        start.setVisited(true);
        q.add(start);
        while (!q.isEmpty()) {
            Cell u = q.poll();
            order.add(u);
            if (u.equals(end)) {
                return new Result(order, reconstruct(start, end));
            }
            for (Cell v : maze.getNeighbors(u)) {
                if (!v.isVisited()) {
                    v.setVisited(true);
                    v.setParent(u);
                    q.add(v);
                }
            }
        }
        return new Result(order, Collections.emptyList());
    }

    /**
     * Depth-first search with backtracking so it still finds a path when the open region has cycles
     * (e.g. recursive-division mazes). A plain stack-DFS that marks nodes permanent breaks on cyclic graphs.
     */
    private static Result dfs(Maze maze, Cell start, Cell end) {
        List<Cell> order = new ArrayList<>();
        boolean found = dfsVisit(maze, start, end, order);
        if (!found) {
            return new Result(order, Collections.emptyList());
        }
        return new Result(order, reconstruct(start, end));
    }

    private static boolean dfsVisit(Maze maze, Cell u, Cell end, List<Cell> order) {
        order.add(u);
        if (u.equals(end)) {
            return true;
        }
        u.setVisited(true);
        for (Cell v : maze.getNeighbors(u)) {
            if (!v.isVisited()) {
                v.setParent(u);
                if (dfsVisit(maze, v, end, order)) {
                    return true;
                }
                v.setParent(null);
            }
        }
        u.setVisited(false);
        return false;
    }

    /** Uniform edge cost; uses stale-entry rejection so duplicate PQ keys stay correct. */
    private static Result dijkstra(Maze maze, Cell start, Cell end) {
        List<Cell> order = new ArrayList<>();
        Map<Cell, Integer> dist = new HashMap<>();
        PriorityQueue<AbstractMap.SimpleImmutableEntry<Cell, Integer>> pq =
                new PriorityQueue<>(Comparator.comparingInt(AbstractMap.SimpleImmutableEntry::getValue));
        dist.put(start, 0);
        pq.add(new AbstractMap.SimpleImmutableEntry<>(start, 0));
        while (!pq.isEmpty()) {
            AbstractMap.SimpleImmutableEntry<Cell, Integer> entry = pq.poll();
            Cell u = entry.getKey();
            int du = entry.getValue();
            if (du != dist.getOrDefault(u, Integer.MAX_VALUE)) {
                continue;
            }
            order.add(u);
            if (u.equals(end)) {
                return new Result(order, reconstruct(start, end));
            }
            for (Cell v : maze.getNeighbors(u)) {
                int alt = du + 1;
                if (alt < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, alt);
                    v.setParent(u);
                    pq.add(new AbstractMap.SimpleImmutableEntry<>(v, alt));
                }
            }
        }
        return new Result(order, Collections.emptyList());
    }

    /**
     * Greedy best-first can get stuck on grids with obstacles. If the goal is never expanded,
     * fall back to BFS so a path is still shown whenever one exists.
     */
    private static Result greedyBestFirst(Maze maze, Cell start, Cell end) {
        List<Cell> order = new ArrayList<>();
        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(c -> manhattan(c, end)));
        Set<Cell> settled = new HashSet<>();
        pq.add(start);
        while (!pq.isEmpty()) {
            Cell u = pq.poll();
            if (!settled.add(u)) {
                continue;
            }
            order.add(u);
            if (u.equals(end)) {
                return new Result(order, reconstruct(start, end));
            }
            for (Cell v : maze.getNeighbors(u)) {
                if (!settled.contains(v)) {
                    v.setParent(u);
                    pq.add(v);
                }
            }
        }
        clearCellState(maze);
        return bfs(maze, start, end);
    }

    /** One row per algorithm after an independent {@link #solve(Maze, Algorithm)} run. */
    public static final class AlgorithmStat {
        public final Algorithm algorithm;
        public final int pathLength;
        public final int visitedCount;
        public final long timeNanos;
        public final boolean pathFound;

        public AlgorithmStat(Algorithm algorithm, int pathLength, int visitedCount, long timeNanos, boolean pathFound) {
            this.algorithm = algorithm;
            this.pathLength = pathLength;
            this.visitedCount = visitedCount;
            this.timeNanos = timeNanos;
            this.pathFound = pathFound;
        }
    }

    /**
     * Runs every {@link Algorithm} on the same maze (each run clears cell state first).
     * Order matches {@link Algorithm#values()}.
     */
    public static List<AlgorithmStat> compareAll(Maze maze) {
        List<AlgorithmStat> rows = new ArrayList<>(Algorithm.values().length);
        for (Algorithm a : Algorithm.values()) {
            long t0 = System.nanoTime();
            Result r = solve(maze, a);
            long t1 = System.nanoTime();
            boolean found = !r.path.isEmpty();
            rows.add(new AlgorithmStat(a, r.path.size(), r.visited.size(), t1 - t0, found));
        }
        return rows;
    }
}
