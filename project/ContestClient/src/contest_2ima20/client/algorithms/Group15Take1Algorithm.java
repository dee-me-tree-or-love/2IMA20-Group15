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
import contest_2ima20.client.trajectorysummarization.HierarchicalClustering;
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

    private List<List<InputPolyLine>> computePolylineGroupings(
        List<InputPolyLine> inputPolylines, 
        double[][] distances,
        int targetClusterCount
    ) {
        // logger.info("computing clusters");
        HierarchicalClustering clusterBuilder = new HierarchicalClustering(distances);
        List<Cluster> lineIdClusters = clusterBuilder.clusterInputPolylines(
            inputPolylines,
            targetClusterCount
        );
        return clustersToPolylineGroups(
            inputPolylines,
            lineIdClusters
        );
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

    private PolyLine computeMeanPoLyline(List<InputPolyLine> polyLineGroup) {
        // logger.info("computing clusters");
        double sampleRate = 30; // We can change this
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
        // logger.info(groupedPolylines.toString());

        for (int i = 0; i < input.k && i < groupedPolylines.size(); i++) {

            List<InputPolyLine> outputGroup = groupedPolylines.get(i);
            // logger.info(outputGroup.toString());

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
