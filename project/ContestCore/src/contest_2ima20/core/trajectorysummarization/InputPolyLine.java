/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest_2ima20.core.trajectorysummarization;

import nl.tue.geometrycore.geometry.linear.PolyLine;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class InputPolyLine extends PolyLine {

    public final int index;

    public InputPolyLine(int index) {
        this.index = index;
    }

}
