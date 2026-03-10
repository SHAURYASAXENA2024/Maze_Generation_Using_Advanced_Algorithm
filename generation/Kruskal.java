import java.util.*;

class Edge implements Comparable<Edge> {
    int src, dest, weight;

    Edge(int s, int d, int w) {
        src = s;
        dest = d;
        weight = w;
    }

    public int compareTo(Edge e) {
        return this.weight - e.weight;
    }
}

public class Kruskal {

    static int find(int parent[], int i) {
        if (parent[i] == i)
            return i;
        return find(parent, parent[i]);
    }

    static void union(int parent[], int x, int y) {
        int xset = find(parent, x);
        int yset = find(parent, y);
        parent[xset] = yset;
    }

    static void kruskal(List<Edge> edges, int V) {

        Collections.sort(edges);

        int parent[] = new int[V];

        for (int i = 0; i < V; i++)
            parent[i] = i;

        int mstCost = 0;

        System.out.println("Edges in MST:");

        for (Edge e : edges) {

            int x = find(parent, e.src);
            int y = find(parent, e.dest);

            if (x != y) {

                System.out.println(e.src + " - " + e.dest + " : " + e.weight);
                mstCost += e.weight;

                union(parent, x, y);
            }
        }

        System.out.println("Total MST Cost = " + mstCost);
    }

    public static void main(String[] args) {

        int V = 4;

        List<Edge> edges = new ArrayList<>();

        edges.add(new Edge(0, 1, 10));
        edges.add(new Edge(0, 2, 6));
        edges.add(new Edge(0, 3, 5));
        edges.add(new Edge(1, 3, 15));
        edges.add(new Edge(2, 3, 4));

        kruskal(edges, V);
    }
}