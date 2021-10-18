
package contest_2ima20.client.algorithms.graph;

import java.io.*;
import java.util.*;

public class Graph {
    private int V;
    private LinkedList<Integer> adj[];

    public Graph(int v) {
        V = v;
        adj = new LinkedList[v];
        for (int i=0; i<v; ++i) {
            adj[i] = new LinkedList();
        }	
    }

    public void addEdge(int v, int w) {
        if (!adj[v].contains(w)) {
            adj[v].add(w);
        }
    }

    public int[] BFS(int s) {

        boolean visited[] = new boolean[V];
        int prev[] = new int[V];
                
        for (int i = 0; i < V; i++) {
            prev[i] = -1;
        }

        LinkedList<Integer> queue = new LinkedList<Integer>();
        visited[s]=true;
        queue.add(s);

        while (queue.size() != 0) {
            s = queue.poll();

            Iterator<Integer> i = adj[s].listIterator();
            while (i.hasNext())
            {
                int n = i.next();
                if (!visited[n]) {
                    visited[n] = true;
                    prev[n] = s;
                    queue.add(n);
                }
            }
        }
        return prev;
    }


    public ArrayList<Integer> reconstructPath(int s, int e, int[] prev) {
        ArrayList<Integer> path = new ArrayList<Integer>();
            
        for (int at = e; at != -1; at = prev[at]) {
            path.add(at); 
        }

        Collections.reverse(path);

        if (path.get(0) == s) {
            return path;
        } else {
            return null;
        }
    }


    public ArrayList<Integer> shortestPathBFS(int s, int e) {
            int[] prev = this.BFS(s);
            ArrayList<Integer> path = this.reconstructPath(s, e, prev);
            return path;
    }

}

