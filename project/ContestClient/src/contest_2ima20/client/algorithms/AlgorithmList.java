/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest_2ima20.client.algorithms;

import contest_2ima20.client.trajectorysummarization.TrajectorySummarizationAlgorithm;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class AlgorithmList {

    public static TrajectorySummarizationAlgorithm[] getAlgorithms() {
        return new TrajectorySummarizationAlgorithm[]{
            new ArbitraryAlgorithm(),
            new PolylineSimplificationAlgorithm(),
            new Group15Take1Algorithm(),
            new Take2Algorithm(),
            new ClusterMeanSimplifySampleRate10(),
            new ClusterMeanSimplifySampleRate20(),
            new ClusterMeanSimplifySampleRate30(),
            new ClusterMeanSimplifySampleRate40(),
        };
    }
}
