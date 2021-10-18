/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest_2ima20.client.algorithms;

import contest_2ima20.core.trajectorysummarization.Input;
import contest_2ima20.core.trajectorysummarization.InputPolyLine;
import contest_2ima20.core.trajectorysummarization.Output;
import contest_2ima20.core.trajectorysummarization.OutputPolyLine;
import contest_2ima20.core.trajectorysummarization.FrechetDistance;
import contest_2ima20.client.trajectorysummarization.TrajectorySummarizationAlgorithm;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.PolyLine;

import java.util.*;
import java.lang.*;


/**
 *
 * @author Group15
 */
public class Group15Take1Algorithm extends TrajectorySummarizationAlgorithm {

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

    private List<? extends PolyLine> simplifyInput(List<? extends PolyLine> ps) {
        // FIXME: implement with real approach
        return ps;
    }

    private double getMinFrechetDistance(double[][] distances) {
        // TODO: make a better minimal
        double min = 1000000000;
        for (int j = 1; j < distances.length; j++){
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
        for (int j = 1; j < distances.length; j++){
            for (int i = 0; i < j; i++) {
                double selectedDistance = distances[i][j];
                if (selectedDistance > max) {
                    max = selectedDistance;
                }
            }
        }
        return max;
    }

    private int getSmallestDistanceTrajectory(double[][] distances, List<Integer> excludedLineIds){
        double minSum = 10000000;
        int selectedPolyline = -1;
        for (int i = 0; i < distances.length; i++){
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

    private List<List<Integer>> performGrouping(
        double[][] distances,
        double averageDistance,
        List<Integer> excludedLineIds,
        List<List<Integer>> groups
    ) {
        if (excludedLineIds.size() == distances.length) {
            return groups;
        }
        int q = getSmallestDistanceTrajectory(distances, excludedLineIds);
        List<Integer> group = new ArrayList<Integer>();
        for (int i = 0; i < distances.length; i++){
            if (distances[q][i] <= averageDistance) {
                group.add(i);
            }
        }
        groups.add(group);
        excludedLineIds.addAll(group);
        return performGrouping(distances, averageDistance, excludedLineIds, groups);
    }

    private List<List<Integer>> findOptimalGrouping(
        double[][] distances,
        double averageDistance,
        double maxDistance,
        double minDistance,
        int targetGroupNumber
    ){
        List<List<Integer>> groups = new ArrayList();
        List<Integer> excludedLineIds = new ArrayList();

        groups = performGrouping(
            distances, averageDistance, excludedLineIds, groups
        );
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
        return findOptimalGrouping(
            distances, 
            newAverageDistance, 
            newMaxDistance, 
            newMinDistance, 
            targetGroupNumber
        ); 
    }

    private List<List<PolyLine>> computePolylineGroups(
        double[][] distances,
        List<? extends PolyLine> inputPolylines,
        int k
    ){
        double maxDistance = getMaxFrechetDistance(distances);
        double minDistance = getMinFrechetDistance(distances);
        double averageDistance = (maxDistance + minDistance) / 2;
        List<List<Integer>> idGroups = findOptimalGrouping(
            distances, 
            averageDistance, 
            maxDistance, 
            minDistance, 
            k
        );
        List<List<PolyLine>> lineGroups = new ArrayList();
        for (List<Integer> group: idGroups) {
            List<PolyLine> linesOfGroup = new ArrayList();
            for (Integer id: group) {
                linesOfGroup.add(inputPolylines.get(id));
            }
            lineGroups.add(linesOfGroup);
        }
        return lineGroups;
    }

// === Binary search for grouping ===
// 1. Find the pair of lines with minimum dF between each other, call this distance dMin. Do the same for the pair of lines with maximum dF, call it dMax.
// 2. Take the average of dMin and dMax, let's call it dAvg.
// 3. Pick the line Q with the lowest total summed dF in the matrix, add it to a new group.
// 4. Add all lines whose dF to Q is smaller than dAvg to this group.
// 5. Remove the whole group you just constructed from consideration.
// 6. Pick a new line P with the lowest total summed dF among the lines left in the matrix, construct a group for it in the same way we did for Q, remove it from consideration.
// 7. Repeat this process until all lines are part of a group.
// 8. Case distinction for binary search: 
// - If the number of groups is greater than k, pick a new dAvg as the average of the old dAvg and dMax, then do steps 3 through 7 on a "fresh" matrix with this dAvg.
// - If the number of groups is smaller than k, pick the new dAvg between dMin and the old dAvg instead (and do the steps).
// - If the number of groups is exactly k, we have found a nice grouping.

// Running time for this part looks like n log n to me but idk.


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
        List<List<PolyLine>> groupedPolylines = computePolylineGroups(
            distances, 
            input.polylines, 
            input.k
        );

        // TODO: Step 4: compute the mean/median over groups

        // TODO: Step 5: simplify the output?? (Or not?? it might be handy to compute the mean)

        return output;
    }

}
