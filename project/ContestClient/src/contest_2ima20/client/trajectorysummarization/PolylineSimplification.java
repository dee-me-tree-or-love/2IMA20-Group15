
package contest_2ima20.client.trajectorysummarization;

import java.util.ArrayList;
import java.io.*;
import contest_2ima20.core.trajectorysummarization.Input;
import contest_2ima20.core.trajectorysummarization.InputPolyLine;
import contest_2ima20.core.trajectorysummarization.Output;
import contest_2ima20.core.trajectorysummarization.OutputPolyLine;
import contest_2ima20.core.trajectorysummarization.FrechetDistance;
import contest_2ima20.client.algorithms.graph.Graph;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.PolyLine;


public class PolylineSimplification {
    
    public OutputPolyLine simplifyFrechetEpsilon(InputPolyLine Q, double epsilon) {
         
        OutputPolyLine O = new OutputPolyLine();
        // Create graph of shortcuts
        Graph shortcuts = new Graph(Q.vertexCount());
            for (int i = 0; i < Q.vertexCount() - 1; i++) {
                shortcuts.addEdge(i, i+1);
                ArrayList<Vector> curveVertices = new ArrayList<Vector>();
                PolyLine curve = new PolyLine(); 
                curve.addVertex(Q.vertex(i).clone());
                for (int j = i+1; j < Q.vertexCount(); j++) {
                    curve.addVertex(Q.vertex(j).clone());
                    PolyLine edge = new PolyLine();
                    edge.addVertex(Q.vertex(i).clone());
                    edge.addVertex(Q.vertex(j).clone());

                    curve.addVertex(Q.vertex(j).clone());
                    if (FrechetDistance.decide(curve, edge, epsilon)) {
                        shortcuts.addEdge(i, j);
                    }
                } 
          }
        ArrayList<Integer> path = shortcuts.shortestPathBFS(0, Q.vertexCount() - 1);
        if (path != null) {
            for (int i = 0; i < path.size(); i++) {
                O.addVertex(Q.vertex(path.get(i)).clone());
            }
        }
        return O;    
    }

    
    public OutputPolyLine simplifyFrechetVertices(InputPolyLine Q, int c) {
        //Binary search
        OutputPolyLine o = null;
        OutputPolyLine lastValid = null;
        double maxEpsilon = Q.perimeter();
        double minEpsilon = 0;
        double epsilon = maxEpsilon/2;
        double prevEpsilon = 0;
        if (Q.vertexCount() <= c) {
        o = new OutputPolyLine();
        for (int i = 0; i < Q.vertexCount(); i++) {
            o.addVertex(Q.vertex(i).clone());
        }
        return o;
        }
         
        do {
            o = this.simplifyFrechetEpsilon(Q, epsilon); 
            int nVertices = o.vertexCount();
            prevEpsilon = epsilon;
            if (lastValid == null || (nVertices > lastValid.vertexCount() && nVertices <= c)) {
                lastValid = o;
            }
            if (nVertices > c) {
                minEpsilon = epsilon;
                epsilon = (epsilon + maxEpsilon) / 2; 
            } else if (nVertices < c) {
                maxEpsilon = epsilon;
                epsilon = (minEpsilon + epsilon) / 2;
            } else {
                return o;
            }
        } while (epsilon != prevEpsilon);
        return lastValid;
    }

}
