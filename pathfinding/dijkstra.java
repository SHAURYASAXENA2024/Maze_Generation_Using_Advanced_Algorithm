package pathfinding;

import java.util.Arrays;
import java.util.PriorityQueue;

/** Dijkstra shortest paths from a source on a weighted {@link Graph}. */
public class dijkstra {

    public static void dijkstra(Graph graph, int src) {
        int V = graph.getVertexCount();
        int[] dist = new int[V];
        Arrays.fill(dist, Integer.MAX_VALUE);

        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        dist[src] = 0;
        minHeap.add(new int[]{src, 0});

        while (!minHeap.isEmpty()) {
            int[] curr = minHeap.poll();
            int u = curr[0];
            int du = curr[1];

            if (du != dist[u]) {
                continue;
            }

            for (Graph.Edge e : graph.neighbors(u)) {
                int v = e.to;
                int w = e.weight;
                if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    minHeap.add(new int[]{v, dist[v]});
                }
            }
        }

        for (int i = 0; i < V; i++) {
            System.out.println("Distance from " + src + " to " + i + " = " + dist[i]);
        }
    }

    public static void main(String[] args) {
        Graph graph = Graph.sampleWeightedGraph5();
        dijkstra(graph, 0);
    }
}
