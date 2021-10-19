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
import java.lang.*;
import java.util.*;
import nl.tue.geometrycore.geometry.linear.PolyLine;


class Cluster {
    List<Integer> item;
    List<Double> distance;

    Tuple(List<Integer> items, List<Double> distances) {
        this.items = items;
        this.distance = distance;
    }
}

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

    private double getMinFrechetDistance(double[][] distances) {
        // TODO: make a better minimal
        double min = 1000000000;
        for (int j = 1; j < distances.length; j++) {
            for (int i = 0; i < j; i++) {
                double selectedDistance = distances[i][j];
                if (selectedDistance < min) {
                    min = selectedDistance;
                }
            }
        }
        return min;
    }

    private double getMaxFrechetDistance(double[][] distances) {
        // TODO: make a better max
        double max = -1000000000;
        for (int j = 1; j < distances.length; j++) {
            for (int i = 0; i < j; i++) {
                double selectedDistance = distances[i][j];
                if (selectedDistance > max) {
                    max = selectedDistance;
                }
            }
        }
        return max;
    }

    private int getSmallestDistanceTrajectory(double[][] distances, List<Integer> excludedLineIds) {
        logger.info(excludedLineIds.toString());
        double minSum = 10000000;
        int selectedPolyline = 0;
        for (int i = 0; i < distances.length; i++) {
            double sum = 0;
            for (int j = 0; j < distances.length; j++) {
                sum += distances[i][j];
            }
            if (sum < minSum && !excludedLineIds.contains(i)) {
                selectedPolyline = i;
            }
        }
        return selectedPolyline;
    }

    private List<List<Integer>> performGrouping(double[][] distances, double averageDistance,
            List<Integer> excludedLineIds, List<List<Integer>> groups) {
        logger.info("Running performGrouping");
        if (excludedLineIds.size() == distances.length) {
            return groups;
        }
        int q = getSmallestDistanceTrajectory(distances, excludedLineIds);
        List<Integer> group = new ArrayList<Integer>();
        for (int i = 0; i < distances.length; i++) {
            if (distances[q][i] <= averageDistance) {
                group.add(i);
            }
        }
        groups.add(group);
        excludedLineIds.addAll(group);
        return performGrouping(distances, averageDistance, excludedLineIds, groups);
    }

    private List<List<Integer>> findOptimalGrouping(double[][] distances, double averageDistance, double maxDistance,
            double minDistance, int targetGroupNumber) {
        List<List<Integer>> groups = new ArrayList();
        List<Integer> excludedLineIds = new ArrayList();

        groups = performGrouping(distances, averageDistance, excludedLineIds, groups);
        if (groups.size() == targetGroupNumber) {
            return groups;
        }

        double newAverageDistance, newMaxDistance, newMinDistance = 0;
        if (groups.size() > targetGroupNumber) {
            newMinDistance = averageDistance;
            newAverageDistance = (averageDistance + maxDistance) / 2;
            newMaxDistance = maxDistance;
        } else {
            newMinDistance = minDistance;
            newAverageDistance = (averageDistance + minDistance) / 2;
            newMaxDistance = averageDistance;
        }
        return findOptimalGrouping(distances, newAverageDistance, newMaxDistance, newMinDistance, targetGroupNumber);
    }

    private List<List<InputPolyLine>> computePolylineGroups(double[][] distances, List<InputPolyLine> inputPolylines,
            int k) {
        double maxDistance = getMaxFrechetDistance(distances);
        double minDistance = getMinFrechetDistance(distances);
        double averageDistance = (maxDistance + minDistance) / 2;
        List<List<Integer>> idGroups = findOptimalGrouping(distances, averageDistance, maxDistance, minDistance, k);
        List<List<InputPolyLine>> lineGroups = new ArrayList();
        for (List<Integer> group : idGroups) {
            List<InputPolyLine> linesOfGroup = new ArrayList();
            for (Integer id : group) {
                linesOfGroup.add(inputPolylines.get(id));
            }
            lineGroups.add(linesOfGroup);
        }
        return lineGroups;
    }

    // === Binary search for grouping ===
    // 1. Find the pair of lines with minimum dF between each other, call this
    // distance dMin. Do the same for the pair of lines with maximum dF, call it
    // dMax.
    // 2. Take the average of dMin and dMax, let's call it dAvg.
    // 3. Pick the line Q with the lowest total summed dF in the matrix, add it to a
    // new group.
    // 4. Add all lines whose dF to Q is smaller than dAvg to this group.
    // 5. Remove the whole group you just constructed from consideration.
    // 6. Pick a new line P with the lowest total summed dF among the lines left in
    // the matrix, construct a group for it in the same way we did for Q, remove it
    // from consideration.
    // 7. Repeat this process until all lines are part of a group.
    // 8. Case distinction for binary search:
    // - If the number of groups is greater than k, pick a new dAvg as the average
    // of the old dAvg and dMax, then do steps 3 through 7 on a "fresh" matrix with
    // this dAvg.
    // - If the number of groups is smaller than k, pick the new dAvg between dMin
    // and the old dAvg instead (and do the steps).
    // - If the number of groups is exactly k, we have found a nice grouping.

    // Running time for this part looks like n log n to me but idk.

    private List<List<Integer>> mergeClosestClusters(
        List<Cluster> clusters,
        double[][] distances
    ) {
        List<List<Integer>> newClusterItems = new ArrayList();
        List<Integer> mergedClusterIds = new ArrayList();
        for (int i = 0; i < clusters.size(); i++) {
            if (!mergedClusterIds.contains(i)){
                double minDistance = 1000000;
                int closestClusterId = 100000;
                for (int j = 0; j < c.distances.size(); j++) {
                    if (c.distances.get(j) < minDistance && i != j) {
                        minDistance = c.distances.get(j);
                        closestClusterId = j;
                    }
                }
                List<Integer> mergedClusterItems = new ArrayList();
                mergedClusterItems.addAll(clusters.get(i).items);
                mergedClusterItems.addAll(clusters.get(closestClusterId).items);
                newClusterItems.add(mergedClusterItems);
                mergedClusterIds.add(j);
            }
        }
        logger.info(newClusterItems.asString());
        logger.info(mergedClusterIds.asString());
        for (int i = 0; i < newClusterItems.size(); i++){
            for (int j=0; i<)
        }
    }

    private List<List<InputPolyLine>> clusterPolylines(
        List<InputPolyLine> inputPolylines, 
        double[][] distances,
        int targetClusterCount
    ) {
        List<Cluster> lineIdClusters = new ArrayList();
        int clusterCount = lineIdClusters.size();
        for (InputPolyLine p : inputPolylines) {
            lineIdClusters.add(
                new Cluster(
                    Arrays.asList(p.index),
                    Arrays.asList(distances[p.index])
                )
            );
        }
        while (clusterCount < targetClusterCount) {
            lineIdClusters = mergeClosestClusters(lineIdClusters, distances);
            clusterCount = lineIdClusters.size();
            logger.info(clusterCount.toString());
            logger.info(lineIdClusters.toString());
        }
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
        List<List<InputPolyLine>> groupedPolylines = computePolylineGroups(distances, input.polylines, input.k);

        // TODO: Step 4: compute the mean/median over groups

        // TODO: Step 5: simplify the output?? (Or not?? it might be handy to compute
        // the mean)

        for (int i = 0; i < input.k; i++) {
            OutputPolyLine outputPolyline = new OutputPolyLine();
            output.polylines.add(outputPolyline);
            List<InputPolyLine> outputGroup = groupedPolylines.get(i);

            // pretend we get a median computed somehow
            PolyLine medianPolyline = outputGroup.get(0);
            for (int j = 0; j < medianPolyline.vertexCount() && j < input.c; j++) {
                outputPolyline.addVertex(medianPolyline.vertex(j).clone());
            }

            // map all input polylines to the outputted median
            for (InputPolyLine p : outputGroup) {
                output.input_to_output[p.index] = outputPolyline;
            }
        }

        return output;
    }

}
