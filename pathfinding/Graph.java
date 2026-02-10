package graph;
public class Graph{
    static class Node{
        int vertex;
        Node next;
        Node(int v){
            vertex=v;
            next=null;
        }
    }
    private int V;
    private Node[] adj;
    public Graph(int vertices){
        V = vertices;
        adj = new Node[V];
        for(int i=0; i<V; i++){
            adj[i]=null;
        }
    }
    public void addEdge(int u, int v){
        if(u<0 || u>=v || v<0 || v>=u){
            return ;
        }
        Node newnode = new Node(v);
        newnode.next = adj[u];
        adj[u]=newnode;

        newnode = new Node(u);
        newnode.next=adj[v];
        adj[v]=newnode;
    }
}