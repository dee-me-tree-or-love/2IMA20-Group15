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
import contest_2ima20.client.trajectorysummarization.TrajectorySummarizationAlgorithm;
import nl.tue.geometrycore.geometry.Vector;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class ArbitraryAlgorithm extends TrajectorySummarizationAlgorithm {

    @Override
    public Output doAlgorithm(Input input) {

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
