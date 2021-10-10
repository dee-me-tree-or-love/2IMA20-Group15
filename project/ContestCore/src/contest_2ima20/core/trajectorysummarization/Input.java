/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest_2ima20.core.trajectorysummarization;

import contest_2ima20.core.problem.Problem;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Rectangle;
import nl.tue.geometrycore.geometryrendering.GeometryRenderer;
import nl.tue.geometrycore.geometryrendering.glyphs.ArrowStyle;
import nl.tue.geometrycore.geometryrendering.styling.Dashing;
import nl.tue.geometrycore.geometryrendering.styling.Hashures;
import nl.tue.geometrycore.geometryrendering.styling.SizeMode;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class Input extends Problem<Output> {

    public int k, c;
    public List<InputPolyLine> polylines;

    public void sortToIndex() {
        int i = 0;
        while (i < polylines.size()) {
            InputPolyLine r = polylines.get(i);
            int ri = r.index;
            if (ri == i) {
                i++;
            } else {
                polylines.set(i, polylines.get(ri));
                polylines.set(ri, r);
            }
        }
    }

    @Override
    public void read(BufferedReader read) throws IOException {
        String line = read.readLine();
        polylines = new ArrayList();
        int n = Integer.parseInt(line.split("\t")[0]);
        k = Integer.parseInt(line.split("\t")[1]);
        c = Integer.parseInt(line.split("\t")[2]);
        for (int i = 1; i <= n; i++) {
            line = read.readLine();
            String[] splitline = line.split("\t");
            InputPolyLine P = new InputPolyLine(polylines.size());
            polylines.add(P);
            for (int j = 0; j < splitline.length; j += 2) {
                P.addVertex(new Vector(
                        Double.parseDouble(splitline[j]),
                        Double.parseDouble(splitline[j + 1])
                ));
            }
        }
    }

    @Override
    public void write(Writer writer) throws IOException {

        sortToIndex();
        writer.write(polylines.size() + "\t" + k + "\t" + c + "\n");
        for (InputPolyLine P : polylines) {
            boolean first = true;
            for (Vector v : P.vertices()) {
                if (first) {
                    first = false;
                } else {
                    writer.write("\t");
                }
                writer.write(v.getX() + "\t" + v.getY());
            }
            writer.write("\n");
        }
    }

    @Override
    public Output parseSolution(BufferedReader read) throws IOException {
        Output output = new Output(this);
        String line = read.readLine();
        int i = 0;
        while (line.contains("\t")) {
            String[] splitline = line.split("\t");
            OutputPolyLine P = new OutputPolyLine();
            output.polylines.add(P);
            for (int j = 0; j < splitline.length; j += 2) {
                P.addVertex(new Vector(
                        Double.parseDouble(splitline[j]),
                        Double.parseDouble(splitline[j + 1])
                ));
            }
            line = read.readLine();
            i++;
        }
        int start = i;
        while (line != null) {
            int index = Integer.parseInt(line);
            output.input_to_output[i - start] = output.polylines.get(index);
            i++;
            line = read.readLine();
        }
        return output;
    }

    @Override
    public void draw(GeometryRenderer render) {
        render.setSizeMode(SizeMode.VIEW);
        render.setStroke(Color.black, 1, Dashing.SOLID);
        render.setForwardArrowStyle(ArrowStyle.TRIANGLE_SOLID, 2);
        render.setFill(null, Hashures.SOLID);
        render.draw(polylines);
    }

    @Override
    public Rectangle getBoundingBox() {
        return Rectangle.byBoundingBox(polylines);
    }

    @Override
    public boolean isValidInstance() {
        return polylines != null && polylines.size() > 0 && k > 0 && c > 0;
    }
}
