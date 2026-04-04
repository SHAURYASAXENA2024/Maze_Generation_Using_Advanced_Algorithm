package pathfinding;

/** Depth-first traversal on a {@link Graph}. */
public class DFS {

    public static void dfs(Graph graph, boolean[] visited, int node) {
        visited[node] = true;
        System.out.print(node + " ");

        for (Graph.Edge e : graph.neighbors(node)) {
            int neighbor = e.to;
            if (!visited[neighbor]) {
                dfs(graph, visited, neighbor);
            }
        }
    }

    public static void dfs(Graph graph, int start) {
        boolean[] visited = new boolean[graph.getVertexCount()];
        dfs(graph, visited, start);
        System.out.println();
    }

    public static void main(String[] args) {
        Graph graph = Graph.sampleGraph5();
        dfs(graph, 0);
    }
}
