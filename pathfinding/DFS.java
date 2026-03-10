import java.util.*;

public class DFS {

    static void dfs(List<List<Integer>> graph, boolean[] visited, int node) {

        visited[node] = true;
        System.out.print(node + " ");

        for (int neighbor : graph.get(node)) {

            if (!visited[neighbor]) {
                dfs(graph, visited, neighbor);
            }
        }
    }

    public static void main(String[] args) {

        int V = 5;

        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
        }

        graph.get(0).add(1);
        graph.get(0).add(2);
        graph.get(1).add(3);
        graph.get(2).add(4);

        boolean[] visited = new boolean[V];

        dfs(graph, visited, 0);
    }
}