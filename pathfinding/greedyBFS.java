import java.util.*;

class Node {
    int vertex;
    int heuristic;

    Node(int v, int h) {
        vertex = v;
        heuristic = h;
    }
}

public class greedyBFS {

    static void greedyBFS(List<List<Integer>> graph, int[] h, int start, int goal) {

        boolean[] visited = new boolean[graph.size()];

        PriorityQueue<Node> pq =
                new PriorityQueue<>((a, b) -> a.heuristic - b.heuristic);

        pq.add(new Node(start, h[start]));

        while (!pq.isEmpty()) {

            Node current = pq.poll();
            int u = current.vertex;

            if (visited[u]) continue;

            visited[u] = true;

            System.out.print(u + " ");

            if (u == goal) {
                System.out.println("\nGoal reached");
                return;
            }

            for (int neighbor : graph.get(u)) {

                if (!visited[neighbor]) {
                    pq.add(new Node(neighbor, h[neighbor]));
                }
            }
        }
    }

    public static void main(String[] args) {

        int V = 6;

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < V; i++)
            graph.add(new ArrayList<>());

        graph.get(0).add(1);
        graph.get(0).add(2);
        graph.get(1).add(3);
        graph.get(1).add(4);
        graph.get(2).add(5);

        int[] heuristic = {5, 3, 4, 2, 6, 1};

        greedyBFS(graph, heuristic, 0, 5);
    }
}