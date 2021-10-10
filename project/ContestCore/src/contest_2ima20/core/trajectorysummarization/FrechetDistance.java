/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contest_2ima20.core.trajectorysummarization;

import nl.tue.geometrycore.geometry.Vector;
import nl.tue.geometrycore.geometry.linear.Line;
import nl.tue.geometrycore.geometry.linear.PolyLine;
import nl.tue.geometrycore.util.DoubleUtil;
import nl.tue.geometrycore.util.Interval;

/**
 *
 * @author Wouter Meulemans (w.meulemans@tue.nl)
 */
public class FrechetDistance {

    private static final double PREC = 0.001;

    public static boolean decide(PolyLine P, PolyLine Q, double epsilon) {
        int sizeP = P.vertexCount();
        int sizeQ = Q.vertexCount();

        Vector P0 = P.firstVertex();
        Vector Q0 = Q.firstVertex();

        if (P0.distanceTo(Q0) >= epsilon) {
            return false;
        }
        if (P.lastVertex().distanceTo(Q.lastVertex()) >= epsilon) {
            return false;
        }

        if (sizeP == 1) {
            for (int i = 0; i < sizeQ; i++) {
                if (P0.distanceTo(Q.vertex(i)) >= epsilon) {
                    return false;
                }
            }
            return true;
        }
        if (sizeQ == 1) {
            for (int i = 0; i < sizeP; i++) {
                if (P.vertex(i).distanceTo(Q0) >= epsilon) {
                    return false;
                }
            }
            return true;
        }

        Interval[][] LR = new Interval[sizeP - 1][sizeQ - 1];
        Interval[][] BR = new Interval[sizeP - 1][sizeQ - 1];

        for (int i = 0; i < sizeP - 1; i++) {
            for (int j = 0; j < sizeQ - 1; j++) {

                Interval L;
                if (i == 0) {
                    if (j == 0) {// && C1[0].distance(C2[0]) < D + DoubleUtil.eps) {
                        L = computeFreeInterval(P.vertex(i), Q.vertex(j), Q.vertex(j + 1), epsilon);
                    } else {
                        L = new Interval();
                    }
                } else if (!BR[i - 1][j].isEmpty()) {
                    L = computeFreeInterval(P.vertex(i), Q.vertex(j), Q.vertex(j + 1), epsilon);
                } else if (!LR[i - 1][j].isEmpty()) {
                    L = computeFreeInterval(P.vertex(i), Q.vertex(j), Q.vertex(j + 1), epsilon);
                    L.setMin(Math.max(L.getMin(), LR[i - 1][j].getMin()));
                } else {
                    L = new Interval();
                }
                LR[i][j] = L;

                Interval B;
                if (j == 0) {
                    if (i == 0) {// && C1[0].distance(C2[0]) < D + DoubleUtil.eps) {
                        B = computeFreeInterval(Q.vertex(j), P.vertex(i), P.vertex(i + 1), epsilon);
                    } else {
                        B = new Interval();
                    }
                } else if (!LR[i][j - 1].isEmpty()) {
                    B = computeFreeInterval(Q.vertex(j), P.vertex(i), P.vertex(i + 1), epsilon);                    
                } else if (!BR[i][j - 1].isEmpty()) {
                    B = computeFreeInterval(Q.vertex(j), P.vertex(i), P.vertex(i + 1), epsilon);
                    B.setMin(Math.max(B.getMin(), BR[i][j - 1].getMin()));
                } else {
                    B = new Interval();
                }
                BR[i][j] = B;
            }
        }

        return (!LR[sizeP - 2][sizeQ - 2].isEmpty() || !BR[sizeP - 2][sizeQ - 2].isEmpty());
    }

    private static Interval computeFreeInterval(Vector a, Vector u, Vector v, double epsilon) {

        double distAtoLineUV = Line.byThroughpoints(u, v).distanceTo(a);

        if (distAtoLineUV >= epsilon + DoubleUtil.EPS) {
            return new Interval();
        } else {
            double uvdist = u.distanceTo(v);

            // compute "root", the value corresponding to the closest point to a on line spanned by u,v.
            // this is the dotproduct between (a - u) and (v - u)/|v-u|.
            double root = Vector.dotProduct(
                    a.getX() - u.getX(), a.getY() - u.getY(),
                    v.getX() - u.getX(), v.getY() - u.getY()) / uvdist;

            // offset from the closest point to a on line spanned by u,v.
            double offset = Math.sqrt(Math.max(epsilon * epsilon - distAtoLineUV * distAtoLineUV, 0));

            double min = root - offset;
            if (min < 0) {
                min = 0;
            }

            double max = root + offset;
            if (max > uvdist) {
                max = uvdist;
            }
            
            return new Interval(min, max);
        }
    }

    public static double compute(PolyLine P, PolyLine Q) {
        if (P.vertexCount() == 1) {
            double max = 0;
            for (int j = 0; j < Q.vertexCount(); j++) {
                max = Math.max(max, P.firstVertex().distanceTo(Q.vertex(j)));
            }
            return max;
        }
        if (Q.vertexCount() == 1) {
            double max = 0;
            for (int i = 0; i < P.vertexCount(); i++) {
                max = Math.max(max, P.vertex(i).distanceTo(Q.firstVertex()));
            }
            return max;
        }

        // value for which df(P,Q) <= value yields false
        double lowerbound
                = Math.max(P.firstVertex().distanceTo(Q.firstVertex()),
                        P.lastVertex().distanceTo(Q.lastVertex())) - 0.1;
        if (lowerbound < 0) {
            lowerbound = 0;
        }

        // value for which df(P,Q) <= value yields true
        double upperbound = 0;
        for (int i = 0; i < Math.max(P.vertexCount(), Q.vertexCount()); i++) {
            Vector p = P.vertex(Math.min(i, P.vertexCount() - 1));
            Vector q = Q.vertex(Math.min(i, Q.vertexCount() - 1));
            upperbound = Math.max(upperbound, p.distanceTo(q));
        }

        while (lowerbound < upperbound - PREC) {
            double mid = (lowerbound + upperbound) / 2.0;

            if (decide(P, Q, mid)) {
                // df(P,Q) <= mid
                upperbound = mid;
            } else {
                // df(P,Q) > mid
                lowerbound = mid;
            }
        }

        return upperbound;
    }

}
