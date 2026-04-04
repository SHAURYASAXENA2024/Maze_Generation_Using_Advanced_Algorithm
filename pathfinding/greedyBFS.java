package pathfinding;

import java.util.PriorityQueue;

/**
 * Greedy best-first search: expands the vertex with lowest heuristic estimate toward the goal.
 */
public class greedyBFS {

    private static final class FrontierNode {
        final int vertex;
        final int heuristic;

        FrontierNode(int v, int h) {
            vertex = v;
            heuristic = h;
        }
    }

    public static void greedyBFS(Graph graph, int[] h, int start, int goal) {
        boolean[] visited = new boolean[graph.getVertexCount()];

        PriorityQueue<FrontierNode> pq =
                new PriorityQueue<>((a, b) -> Integer.compare(a.heuristic, b.heuristic));

        pq.add(new FrontierNode(start, h[start]));

        while (!pq.isEmpty()) {
            FrontierNode current = pq.poll();
            int u = current.vertex;

            if (visited[u]) {
                continue;
            }

            visited[u] = true;
            System.out.print(u + " ");

            if (u == goal) {
                System.out.println("\nGoal reached");
                return;
            }

            for (Graph.Edge e : graph.neighbors(u)) {
                int neighbor = e.to;
                if (!visited[neighbor]) {
                    pq.add(new FrontierNode(neighbor, h[neighbor]));
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph graph = Graph.sampleGraph6();
        int[] heuristic = {5, 3, 4, 2, 6, 1};
        greedyBFS(graph, heuristic, 0, 5);
    }
}
