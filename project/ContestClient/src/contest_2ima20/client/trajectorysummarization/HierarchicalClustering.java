
package contest_2ima20.client.trajectorysummarization;

import java.util.ArrayList;
import java.lang.*;
import java.util.*;
import contest_2ima20.core.trajectorysummarization.InputPolyLine;
import nl.tue.geometrycore.geometry.linear.PolyLine;
import contest_2ima20.client.trajectorysummarization.Cluster;


public class HierarchicalClustering {
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("clustering");
    private double[][] distances;

    public HierarchicalClustering(double[][] distances) {
        this.distances = distances;
    }

    private List<Cluster> mergeClosestClusters(
        List<Cluster> clusters,
        int targetClusterCount
    ) {

        /**
            > Merging -> 
                1. from all clusters find two such that Frechet distance between any 2 of their trajectories is the smallest
                2. merge the two identified clusters C_1, C_2 into a new C'
                3. return a new set of Clusters as (Cs \ {C_1, C_2}) U {C'} 
         */
        int maxPlaceholder = 10000000;

        int idSmallestClusterA = maxPlaceholder;
        int idSmallestClusterB = maxPlaceholder;
        double minClusterDistance = maxPlaceholder;

        // Find smallest cluster pair
        for (int i = 0; i < clusters.size(); i++){
            for (int j = 0; j < clusters.size(); j++) {
                if (i < j){
                    Cluster c_A = clusters.get(i);
                    Cluster c_B = clusters.get(j);
                    // Check if pair of clusters i,j is the closest by compairing all included items of the cluster
                    // logger.info(String.format("Considering %d -> %d", i, j));
                    for (int idItemOfA: c_A.items){
                        for (int idItemOfB: c_B.items) {
                            double itemDistance = this.distances[idItemOfA][idItemOfB];
                            // logger.info(String.format("Item distance %f", itemDistance));
                            if (
                                itemDistance < minClusterDistance
                                // TODO: maybe not needed?
                                && idItemOfA != idItemOfB
                            ) {
                                minClusterDistance = itemDistance;
                                idSmallestClusterA = i;
                                idSmallestClusterB = j;
                            }
                        }
                    }
                }
            }
        }
        // logger.info(String.format("Smallest %d -> %d : %f", idSmallestClusterA, idSmallestClusterB, minClusterDistance));


        // Merge 2 closest clusters
        List<List<Integer>> newClusterItems = new ArrayList();
        List<Integer> mergedClusterItems = new ArrayList();
        mergedClusterItems.addAll(clusters.get(idSmallestClusterA).items);
        mergedClusterItems.addAll(clusters.get(idSmallestClusterB).items);
        newClusterItems.add(mergedClusterItems);
        
        // Add remaining untouched clusters
        for (int i = 0; i < clusters.size(); i++){
            if (i != idSmallestClusterA && i != idSmallestClusterB){
                newClusterItems.add(clusters.get(i).items);
            }
        }
        // logger.info(newClusterItems.toString());
        // logger.info(String.format("Count: %d", newClusterItems.size()));
        
        // Assembling the new cluster set
        List<Cluster> newClusters = new ArrayList();
        for (int i = 0; i < newClusterItems.size(); i++){
            newClusters.add(new Cluster(newClusterItems.get(i)));
        }
        return newClusters;
    }

    public List<Cluster> clusterInputPolylines(
        List<InputPolyLine> inputPolylines,
        int targetClusterCount
    ){
        List<Cluster> clusters = new ArrayList();
        for (InputPolyLine p : inputPolylines) {
            clusters.add(new Cluster(Arrays.asList(p.index)));
        }
        int clusterCount = clusters.size();
        while (clusterCount > targetClusterCount) {
            clusters = mergeClosestClusters(clusters, targetClusterCount);
            clusterCount = clusters.size();
        }
        return clusters;
    }
}
