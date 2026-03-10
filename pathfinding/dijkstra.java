
import java.util.*;

class Edge{
    int dest;
    int weight;
    Edge(int d , int w){
        dest = d;
        weight= w;
    }
}

public class dijkstra{
    static void dijkstra(List<List<Edge>> graph, int V , int src){
        int[] dist = new int [V];
        Arrays.fill(dist,Integer.MAX_VALUE);

        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a,b)->a[1]-b[1]);
        dist[src]=0;
        minHeap.add(new int[]{src,0});

        while(!minHeap.isEmpty()){
            int[] curr = minHeap.poll();
            int u = curr[0];

            for(Edge e: graph.get(u) ){
                int v = e.dest;
                int weight = e.weight;
                
                if(dist[u] + weight < dist[v]){
                    dist[v] = dist[u] + weight;
                    minHeap.add(new int[]{v,dist[v]});
                }

            }
        }
        for(int i=0; i<V; i++){
            System.out.println("Distance from " + src +" to "+ i + "=" + dist[i]);
        }

    }
    public static void main(String[] args) {
        PriorityQueue<Integer>  minHeap = new PriorityQueue<>();
        minHeap.add(10);
        minHeap.add(2);
        minHeap.add(5);
        minHeap.add(1);

        while(!minHeap.isEmpty()){
            System.out.println(minHeap.poll());
        }
        int V =5;
        List<List<Edge>> graph = new ArrayList<>();
        for(int i=0; i<V; i++){
             graph.add(new ArrayList<>());
        }
        graph.get(0).add(new Edge(1,4));
        graph.get(0).add(new Edge(2,1));

        graph.get(2).add(new Edge(1,2));
        graph.get(1).add(new Edge(3,1));

        graph.get(2).add(new Edge(3,5));
        graph.get(3).add(new Edge(4,3));

        dijkstra(graph, V, 0);
    }
}