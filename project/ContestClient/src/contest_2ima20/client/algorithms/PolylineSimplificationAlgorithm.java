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
import contest_2ima20.client.trajectorysummarization.PolylineSimplification;
import nl.tue.geometrycore.geometry.Vector;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class PolylineSimplificationAlgorithm extends TrajectorySummarizationAlgorithm {

    @Override
    public Output doAlgorithm(Input input) {

        // creating an output
        Output output = new Output(input);

        // define k output trajectories
        for (int i = 0; i < input.k; i++) {
            
           //TEST
            PolylineSimplification pSimple = new PolylineSimplification();
            OutputPolyLine P = pSimple.simplifyFrechetVertices(input.polylines.get(i), input.c);
            output.polylines.add(P); // dont forget to add it!
            
            InputPolyLine Q = input.polylines.get(i);
            // this is bound to be a good one!
            output.input_to_output[Q.index] = P;
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
