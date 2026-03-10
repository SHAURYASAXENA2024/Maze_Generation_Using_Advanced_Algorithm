import java.util.*;

class Edge {
    int dest;
    int weight;

    Edge(int d, int w) {
        dest = d;
        weight = w;
    }
}

public class prismAlgorithm {

    static void prim(List<List<Edge>> graph, int V) {

        boolean[] visited = new boolean[V];

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);

        pq.add(new int[]{0, 0}); // {node, weight}

        int mstCost = 0;

        while (!pq.isEmpty()) {

            int[] curr = pq.poll();
            int node = curr[0];
            int weight = curr[1];

            if (visited[node]) continue;

            visited[node] = true;
            mstCost += weight;

            for (Edge e : graph.get(node)) {

                if (!visited[e.dest]) {
                    pq.add(new int[]{e.dest, e.weight});
                }
            }
        }

        System.out.println("Total MST Cost = " + mstCost);
    }

    public static void main(String[] args) {

        int V = 5;

        List<List<Edge>> graph = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
        }

        graph.get(0).add(new Edge(1, 2));
        graph.get(0).add(new Edge(3, 6));

        graph.get(1).add(new Edge(0, 2));
        graph.get(1).add(new Edge(2, 3));
        graph.get(1).add(new Edge(3, 8));
        graph.get(1).add(new Edge(4, 5));

        graph.get(2).add(new Edge(1, 3));
        graph.get(2).add(new Edge(4, 7));

        graph.get(3).add(new Edge(0, 6));
        graph.get(3).add(new Edge(1, 8));

        graph.get(4).add(new Edge(1, 5));
        graph.get(4).add(new Edge(2, 7));

        prim(graph, V);
    }
}