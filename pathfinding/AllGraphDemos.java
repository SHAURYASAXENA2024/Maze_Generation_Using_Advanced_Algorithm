package pathfinding;

/**
 * Single entry point to print all classic graph demos (BFS, DFS, Dijkstra, greedy best-first)
 * using {@link Graph} and the same sample graphs as each algorithm’s {@code main}.
 *
 * <pre>
 *   java -cp out pathfinding.AllGraphDemos
 * </pre>
 */
public final class AllGraphDemos {

    private AllGraphDemos() {}

    public static void main(String[] args) {
        System.out.println("=== BFS (sample 5-vertex graph) ===");
        BFS.bfs(Graph.sampleGraph5(), 0);

        System.out.println("=== DFS (same graph) ===");
        DFS.dfs(Graph.sampleGraph5(), 0);

        System.out.println("=== Dijkstra (weighted 5-vertex graph) ===");
        dijkstra.dijkstra(Graph.sampleWeightedGraph5(), 0);

        System.out.println("=== Greedy best-first (6-vertex graph, heuristic array) ===");
        int[] h = {5, 3, 4, 2, 6, 1};
        greedyBFS.greedyBFS(Graph.sampleGraph6(), h, 0, 5);
    }
}
