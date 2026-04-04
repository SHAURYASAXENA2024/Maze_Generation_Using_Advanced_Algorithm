package pathfinding;

import java.util.ArrayDeque;
import java.util.Queue;

/** Breadth-first traversal on a {@link Graph}. */
public class BFS {

    /**
     * Prints vertices in BFS order from {@code start} (uses only edge endpoints; weights ignored).
     */
    public static void bfs(Graph graph, int start) {
        int V = graph.getVertexCount();
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new ArrayDeque<>();

        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            System.out.print(node + " ");

            for (Graph.Edge e : graph.neighbors(node)) {
                int neighbor = e.to;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Graph graph = Graph.sampleGraph5();
        bfs(graph, 0);
    }
}
