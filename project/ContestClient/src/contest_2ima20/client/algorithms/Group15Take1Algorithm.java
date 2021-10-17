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

class Tuple {
    int item;
    double metric;

    Tuple(int item, double metric) {
        this.item = item;
        this.metric = metric;
    }
}

class TupleComparator implements Comparator<Tuple> {
    public int compare(Tuple t1, Tuple t2) {
        if (t1.metric > t2.metric) {
            return 1;
        }
        if (t1.metric < t2.metric) {
            return -1;
        }
        return 0;
    }
}

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

    private double getAverageFrechetDistance(double[][] distances){
        return (
            getMaxFrechetDistance(distances) + getMinFrechetDistance(distances)
        ) / 2;
    }
    
    private int getSmallestDistanceTrajectory(double[][] distances){
        double minSum = 10000000;
        int selectedPolyline = 0;
        for (int i = 0; i < distances.length; i++){
            double sum = 0;
            for (int j = 0; j < distances.length; j++) {
                sum += distances[i][j];
            }
            if (sum < minSum) {
                selectedPolyline = i;
            }
        }
        return selectedPolyline;
    }

    private double[][] excludeLines(double[][] distances, double[] excludedLines){
        double[][] newDistances = new double[excludedLines.length][distances.size];
        // TODO: filter the disrtances
    }


    private List<List<? extends PolyLine>> getPolylineGroups(
        double[][] distances,
        int k,
    ){
        int[][] groups = new int[k][];
        double averageDistance = getAverageFrechetDistance(distances);
        int q = getSmallestDistanceTrajectory(distances);
        int[] group = new int[];
        for (int i = 0; i < distances.length){
            // FIXME: recall all the right java syntax here
            if (distances[q][i] <= averageDistance) {
                group.add(i);
            }
        }
        groups[0] = group


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

        // Step 1: get simplification / clusters of related trajectories
        List<? extends PolyLine> simplifiedTrajectories = simplifyInput(input.polylines);

        // Step 2: get the distance matrix and the minHeap of total sums
        double[][] distances = computeFrechetDistanceMatrix(simplifiedTrajectories);

        // Step 3: 
        // 1. take the minimal (i, total) from minHeap
        // 2. take a j furthest away from i

        return output;
    }

    public Output doNotDoAlgorithm(Input input) {

        // creating an output
        Output output = new Output(input);

        // define k output trajectories
        for (int i = 0; i < input.k; i++) {

            OutputPolyLine P = new OutputPolyLine();
            output.polylines.add(P); // dont forget to add it!

            // copy an arbitrary input trajectory
            // make sure they're not too long
            InputPolyLine Q = input.polylines.get(i);
            for (int j = 0; j < Q.vertexCount() && j < input.c; j++) {
                P.addVertex(Q.vertex(j).clone());
                // the clone bit is important, vectors are objects!
                // otherwise, nudging the output also changes the input
            }

            // this is bound to be a good one!
            output.input_to_output[Q.index] = P;

            // nudge their points a bit (fingers crossed)
            for (Vector v : P.vertices()) {
                v.translate(i, 0);
            }
        }

        // make sure to map the rest as well
        for (int i = 0; i < input.polylines.size(); i++) {
            if (output.input_to_output[i] == null) {
                output.input_to_output[i] = output.polylines.get(i % input.k);
            }
        }

        // and make sure to return the result
        return output;
    }

}
