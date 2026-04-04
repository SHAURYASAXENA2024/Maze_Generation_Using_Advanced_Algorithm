package pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Undirected graph as adjacency lists. Edges carry a non‑negative weight (default {@code 1}
 * for unweighted traversals like BFS / DFS).
 */
public class Graph {

    /** Neighbor vertex and edge weight. */
    public static final class Edge {
        public final int to;
        public final int weight;

        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private final int vertexCount;
    private final List<List<Edge>> adj;

    public Graph(int vertices) {
        if (vertices < 0) {
            throw new IllegalArgumentException("vertices must be non-negative");
        }
        this.vertexCount = vertices;
        this.adj = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public int getVertexCount() {
        return vertexCount;
    }

    /** Alias for {@link #getVertexCount()} (common in textbook code). */
    public int V() {
        return vertexCount;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= vertexCount) {
            throw new IndexOutOfBoundsException("vertex " + v + " not in [0," + vertexCount + ")");
        }
    }

    /** Adds an undirected unit-weight edge between {@code u} and {@code v}. */
    public void addEdge(int u, int v) {
        addEdge(u, v, 1);
    }

    /** Adds an undirected edge between {@code u} and {@code v} with the given weight. */
    public void addEdge(int u, int v, int weight) {
        validateVertex(u);
        validateVertex(v);
        if (u == v) {
            return;
        }
        adj.get(u).add(new Edge(v, weight));
        adj.get(v).add(new Edge(u, weight));
    }

    /** Adjacency list for vertex {@code u} (unmodifiable view). */
    public List<Edge> neighbors(int u) {
        validateVertex(u);
        return Collections.unmodifiableList(adj.get(u));
    }

    /**
     * Demo graph matching the classic BFS/DFS examples (5 vertices, edges 0–1, 0–2, 1–3, 2–4).
     */
    public static Graph sampleGraph5() {
        Graph g = new Graph(5);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        return g;
    }

    /**
     * Weighted demo graph for Dijkstra (same topology as the original {@code dijkstra} main).
     */
    public static Graph sampleWeightedGraph5() {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 1);
        g.addEdge(2, 1, 2);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 5);
        g.addEdge(3, 4, 3);
        return g;
    }

    /**
     * Demo graph for greedy best-first (6 vertices).
     */
    public static Graph sampleGraph6() {
        Graph g = new Graph(6);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(1, 4);
        g.addEdge(2, 5);
        return g;
    }
}
