/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest_2ima20.core.trajectorysummarization;

import contest_2ima20.core.problem.Solution;
import java.awt.Color;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.geometryrendering.GeometryRenderer;
import nl.tue.geometrycore.geometryrendering.glyphs.ArrowStyle;
import nl.tue.geometrycore.geometryrendering.styling.Dashing;
import nl.tue.geometrycore.geometryrendering.styling.ExtendedColors;
import nl.tue.geometrycore.geometryrendering.styling.Hashures;
import nl.tue.geometrycore.geometryrendering.styling.SizeMode;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class Output extends Solution {

    public Input input;
    public OutputPolyLine[] input_to_output; // index-based map, based on the index storted in InputPolyline
    public List<OutputPolyLine> polylines;
    
    //TODO: Remove all below, it is just for TESTING 
    public int clusters = 1;
    Set<Color> distinctColors = new HashSet<Color>();


    ////////////////////////////////////////////////

    public Output(Input input) {
        this.input = input;
        input_to_output = new OutputPolyLine[input.polylines.size()];
        polylines = new ArrayList();
    }

    public Output(Input input, int group) {
        this.input = input;
        input_to_output = new OutputPolyLine[input.polylines.size()];
        polylines = new ArrayList();
    }

    public void setClusters(int kNumber) {
        this.clusters = kNumber;
    }

    @Override
    public boolean isValid() {
        
        if (input_to_output.length != input.polylines.size()) {
            return false;
        }
        if (polylines.size() > input.k) {
            return false;
        }
        for (OutputPolyLine P :polylines) {
            if (P.vertexCount() > input.c) {
                return false;
            }
        }
        for (OutputPolyLine P : input_to_output) {
            if (P == null || !polylines.contains(P)) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public double computeQuality() {
        double dists = 0;
        for (InputPolyLine P : input.polylines) {
            OutputPolyLine Q = input_to_output[P.index];
            dists += FrechetDistance.compute(P,Q);
        }
        return dists;
    }
    
    @Override
    public void write(Writer out) throws IOException {
      
        for (OutputPolyLine P : polylines) {
            boolean first = true;
            for (Vector v : P.vertices()) {
                if (first) {
                    first =false;
                } else {
                    out.append("\t");
                }
                out.append(v.getX() + "\t" +v.getY());
            }
            out.append("\n");
        }
        input.sortToIndex();
        for (InputPolyLine P : input.polylines) {
            OutputPolyLine Q = input_to_output[P.index];
            out.append(polylines.indexOf(Q) + "\n");
        }
    }

    @Override
    public void draw(GeometryRenderer render) {
        input.draw(render);
        render.setSizeMode(SizeMode.VIEW);
        render.setForwardArrowStyle(ArrowStyle.TRIANGLE_SOLID, 2);
        render.setFill(null, Hashures.SOLID);
        //Group by colour
        List<OutputPolyLine>[] polylinesByGroup = new ArrayList[clusters];
        List<OutputPolyLine> outputPolylines = new ArrayList();
        for (int i = 0; i < clusters; i ++) {
            polylinesByGroup[i] = new ArrayList<OutputPolyLine>();
        }
        while (distinctColors.size() != clusters) {
            Color color = generateRandomColor();
            distinctColors.add(color);
        }
        List<Color> colors = new ArrayList<Color>(distinctColors);

        for (OutputPolyLine p : polylines) {
            int cluster = p.getCluster();
            if (cluster > -1) {
                List <OutputPolyLine> o = polylinesByGroup[cluster];
                o.add(p);
            } else {
                outputPolylines.add(p);
            }
        }

        for (int i = 0 ; i < clusters; i++) {
            render.setStroke(colors.get(i), 2, Dashing.SOLID);
            render.draw(polylinesByGroup[i]);
        }
        
        render.setStroke(Color.black, 2, Dashing.SOLID);
        render.draw(outputPolylines);
    }

    @Override
    public Rectangle getBoundingBox() {
        Rectangle R = Rectangle.byBoundingBox(polylines);
        R.includeGeometry(input.getBoundingBox());
        return R;
    }

    public Color generateRandomColor() {
        Random random = new Random();
        float hue = random.nextFloat();
        // Saturation between 0.1 and 0.3
        float saturation = (random.nextInt(2000) + 1000) / 10000f;
        float luminance = 0.9f;
        Color color = Color.getHSBColor(hue, saturation, luminance);
        return color;
    }
}
