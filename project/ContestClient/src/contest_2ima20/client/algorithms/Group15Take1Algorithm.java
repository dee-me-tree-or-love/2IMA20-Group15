/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest_2ima20.client.algorithms;

import contest_2ima20.client.trajectorysummarization.TrajectorySummarizationAlgorithm;
import contest_2ima20.core.trajectorysummarization.FrechetDistance;
import contest_2ima20.core.trajectorysummarization.Input;
import contest_2ima20.core.trajectorysummarization.InputPolyLine;
import contest_2ima20.core.trajectorysummarization.Output;
import contest_2ima20.core.trajectorysummarization.OutputPolyLine;
import contest_2ima20.client.trajectorysummarization.PolylineSimplification;
import contest_2ima20.client.trajectorysummarization.Cluster;
import nl.tue.geometrycore.geometry.Vector;
import java.lang.*;
import java.util.*;
import nl.tue.geometrycore.geometry.linear.PolyLine;


/**
 *
 * @author Group15
 */
public class Group15Take1Algorithm extends TrajectorySummarizationAlgorithm {
    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("take1algo");

    private double[][] computeFrechetDistanceMatrix(List<? extends PolyLine> inputPolylines) {
        double[][] distances = new double[inputPolylines.size()][inputPolylines.size()];
        for (int i = 0; i < inputPolylines.size(); i++) {
            for (int j = 0; j < inputPolylines.size(); j++) {
                // TODO: ingore diagonal later because it's not that useful
                distances[i][j] = FrechetDistance.compute(inputPolylines.get(i), inputPolylines.get(j));
            }
        }
        return distances;
    }

    private List<InputPolyLine> simplifyInput(List<InputPolyLine> ps) {
        // FIXME: implement with real approach
        return ps;
    }

    private List<Cluster> mergeClosestClusters(
        List<Cluster> clusters,
        double[][] distances,
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
                    logger.info(String.format("Considering %d -> %d", i, j));
                    for (int idItemOfA: c_A.items){
                        for (int idItemOfB: c_B.items) {
                            double itemDistance = distances[idItemOfA][idItemOfB];
                            logger.info(String.format("Item distance %f", itemDistance));
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
        logger.info(String.format("Smallest %d -> %d : %f", idSmallestClusterA, idSmallestClusterB, minClusterDistance));


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
        logger.info(newClusterItems.toString());
        logger.info(String.format("Count: %d", newClusterItems.size()));
        
        // Assembling the new cluster set
        List<Cluster> newClusters = new ArrayList();
        for (int i = 0; i < newClusterItems.size(); i++){
            newClusters.add(new Cluster(newClusterItems.get(i)));
        }
        return newClusters;
    }

    private List<Cluster> clusterPolylinesHierarchically(
        List<InputPolyLine> inputPolylines,
        double[][] distances,
        int targetClusterCount
    ){
        List<Cluster> clusters = new ArrayList();
        for (InputPolyLine p : inputPolylines) {
            clusters.add(new Cluster(Arrays.asList(p.index)));
        }
        int clusterCount = clusters.size();
        while (clusterCount > targetClusterCount) {
            clusters = mergeClosestClusters(
                clusters,
                distances,
                targetClusterCount);
            clusterCount = clusters.size();
        }
        return clusters;
    }

    private List<List<InputPolyLine>> clustersToPolylineGroups(
        List<InputPolyLine> inputPolylines,
        List<Cluster> lineIdClusters
    ){
        List<List<InputPolyLine>> lineGroups = new ArrayList();
        for (Cluster cluster : lineIdClusters) {
            List<InputPolyLine> linesOfGroup = new ArrayList();
            for (Integer lineId : cluster.items) {
                linesOfGroup.add(inputPolylines.get(lineId));
            }
            lineGroups.add(linesOfGroup);
        }
        return lineGroups;
    }

    private List<List<InputPolyLine>> computePolylineGroupings(
        List<InputPolyLine> inputPolylines, 
        double[][] distances,
        int targetClusterCount
    ) {
        logger.info("computing clusters");
        List<Cluster> lineIdClusters = clusterPolylinesHierarchically(
            inputPolylines,
            distances,
            targetClusterCount
        );
        return clustersToPolylineGroups(
            inputPolylines,
            lineIdClusters
        );
    }

    private PolyLine computeMeanPoLyline(List<InputPolyLine> polyLineGroup) {
        logger.info("computing clusters");
        double sampleRate = 10; // We can change this
        double fraction = 0;
        PolyLine averagePolyline = new PolyLine();
       
        for (int i = 0; i < sampleRate + 1; i++) {
            fraction = i/sampleRate;
            List<Vector> samplePoints = new ArrayList<Vector> ();
            for (int j = 0; j < polyLineGroup.size(); j++) {
                Vector point = polyLineGroup.get(j).getPointAt(fraction);
                samplePoints.add(point);
            }

            double sumX = 0;
            double sumY = 0;

            for (int j = 0; j < samplePoints.size(); j++) {
                sumX += samplePoints.get(j).getX();
                sumY += samplePoints.get(j).getY();
            }

            Vector mean = new Vector(sumX/samplePoints.size(), sumY/samplePoints.size());
            averagePolyline.addVertex(mean); 
        }
        return averagePolyline;
    }

    @Override
    public Output doAlgorithm(Input input) {
        // Initialization steps
        // creating an output
        Output output = new Output(input);

        // Step 1: get simplification / clusters of related trajectories???
        List<? extends PolyLine> simplifiedTrajectories = simplifyInput(input.polylines);

        // Step 2: get the distance matrix and the minHeap of total sums
        double[][] distances = computeFrechetDistanceMatrix(simplifiedTrajectories);

        // Step 3: find the clustering of the polylines
        List<List<InputPolyLine>> groupedPolylines = computePolylineGroupings(input.polylines, distances, input.k);
        logger.info(groupedPolylines.toString());

        for (int i = 0; i < input.k && i < groupedPolylines.size(); i++) {

            List<InputPolyLine> outputGroup = groupedPolylines.get(i);
            logger.info(outputGroup.toString());

            // Step 4: compute the mean/median over groups
            PolyLine medianPolyline = computeMeanPoLyline(outputGroup); 

            // Step 5: simplify the output?? (Or not?? it might be handy to compute the mean)
            PolylineSimplification pSimple = new PolylineSimplification();
            OutputPolyLine outputPolyline = pSimple.simplifyFrechetVertices(medianPolyline, input.c);

            output.polylines.add(outputPolyline);
            // map all input polylines to the outputted median
            for (InputPolyLine p : outputGroup) {
                output.input_to_output[p.index] = outputPolyline;
            }
        }

        return output;
    }

}
