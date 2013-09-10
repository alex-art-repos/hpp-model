package org.hpp.terrain.fractal;

// 3D non-perspective Stellated polyhedra,  Evgeny Demidov  9 Sep 2001
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

public class Stella3D extends java.applet.Applet
        implements MouseListener, MouseMotionListener {

    int nVert, nFace, nRay, h, w, h2, w2, mx0, my0;
    int face[][][], xPol[], yPol[], Zsort[];
    double fiX = .2, fiY = .3, dfi = .01, scale;
    double vert[][], vert1[][], Norm[][][], Norm1z[][], ray[][], ray1z[];
    Image buffImage;
    Graphics buffGraphics;
    Color[] col;

    public void init() {
        w = getSize().width;
        h = getSize().height;
        w2 = w / 2;
        h2 = h / 2;
        String s = getParameter("N");
        StringTokenizer st = new StringTokenizer(s);
        nVert = Integer.parseInt(st.nextToken());
        nRay = Integer.parseInt(st.nextToken());
        nFace = Integer.parseInt(st.nextToken());
        vert = new double[nVert][3];
        vert1 = new double[nVert][2];
        s = getParameter("Vert");
        st = new StringTokenizer(s, " ,");
        double max = 0;
        for (int i = 0; i < nVert; i++) {
            vert[i][0] = Double.valueOf(st.nextToken()).doubleValue();
            vert[i][1] = Double.valueOf(st.nextToken()).doubleValue();
            vert[i][2] = Double.valueOf(st.nextToken()).doubleValue();
            double vv = vert[i][0] * vert[i][0] + vert[i][1] * vert[i][1]
                    + vert[i][2] * vert[i][2];
            if (max < vv) {
                max = vv;
            }
        }
        scale = w2 / Math.sqrt(max);
        face = new int[nRay][nFace][];
        ray = new double[nRay][3];
        ray1z = new double[nRay];
        Zsort = new int[nRay];
        int tmp[] = new int[30];
        for (int r = 0; r < nRay; r++) {
            Zsort[r] = r;
            s = getParameter("Faces" + r);
            st = new StringTokenizer(s);
            int m = 0;
            for (int i = 0; i < nFace; i++) {
                int l = 0;
                while ((tmp[l] = Integer.parseInt(st.nextToken())) != -1) {
                    l++;
                }
//System.out.println(" r="+r+"  i="+i);
                face[r][i] = new int[l];
                for (int j = 0; j < l; j++) {
                    face[r][i][j] = tmp[j];
                    ray[r][0] += vert[tmp[j]][0];
                    ray[r][1] += vert[tmp[j]][1];
                    ray[r][2] += vert[tmp[j]][2];
                    m++;
                }
            }
            ray[r][0] /= m;
            ray[r][1] /= m;
            ray[r][2] /= m;
        }
        buffImage = createImage(w, h);
        buffGraphics = buffImage.getGraphics();
        col = new Color[256];
        Norm = new double[nRay][nFace][3];
        Norm1z = new double[nRay][nFace];
        for (int r = 0; r < nRay; r++) {
            for (int i = 0; i < nFace; i++) {
                Norm[r][i][0] = (vert[face[r][i][1]][1] - vert[face[r][i][0]][1])
                        * (vert[face[r][i][2]][2] - vert[face[r][i][1]][2])
                        - (vert[face[r][i][2]][1] - vert[face[r][i][1]][1])
                        * (vert[face[r][i][1]][2] - vert[face[r][i][0]][2]);
                Norm[r][i][1] = -(vert[face[r][i][1]][0] - vert[face[r][i][0]][0])
                        * (vert[face[r][i][2]][2] - vert[face[r][i][1]][2])
                        + (vert[face[r][i][2]][0] - vert[face[r][i][1]][0])
                        * (vert[face[r][i][1]][2] - vert[face[r][i][0]][2]);
                Norm[r][i][2] = (vert[face[r][i][1]][0] - vert[face[r][i][0]][0])
                        * (vert[face[r][i][2]][1] - vert[face[r][i][1]][1])
                        - (vert[face[r][i][2]][0] - vert[face[r][i][1]][0])
                        * (vert[face[r][i][1]][1] - vert[face[r][i][0]][1]);
                double mod = Math.sqrt(Norm[r][i][0] * Norm[r][i][0]
                        + Norm[r][i][1] * Norm[r][i][1] + Norm[r][i][2] * Norm[r][i][2]) / 255.5;
                Norm[r][i][0] /= mod;
                Norm[r][i][1] /= mod;
                Norm[r][i][2] /= mod;
            }
        }
        xPol = new int[30];
        yPol = new int[30];
        for (int i = 0; i < 256; i++) {
            col[i] = new Color(i, i, i);
        }
        setBackground(new Color(200, 200, 255));
        addMouseListener(this);
        addMouseMotionListener(this);
        rotate();
    }

    public void destroy() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
    }

    public void mouseClicked(MouseEvent e) {
    }       // event handling
    public void mousePressed(MouseEvent e) {
        mx0 = e.getX();
        my0 = e.getY();
        e.consume();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        int x1 = e.getX();
        int y1 = e.getY();
        fiY += dfi * (x1 - mx0);
        mx0 = x1;
        fiX += dfi * (y1 - my0);
        my0 = y1;
        rotate();
        repaint();
        e.consume();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void rotate() {
        double ct = Math.cos(fiX), cf = Math.cos(fiY),
                st = Math.sin(fiX), sf = Math.sin(fiY),
                m00 = scale * cf, m02 = scale * sf,
                m10 = scale * st * sf, m11 = scale * ct, m12 = -scale * st * cf,
                m20 = -ct * sf, m21 = st, m22 = ct * cf;
        for (int i = 0; i < nVert; i++) {
            vert1[i][0] = m00 * vert[i][0] + m02 * vert[i][2];
            vert1[i][1] = m10 * vert[i][0] + m11 * vert[i][1] + m12 * vert[i][2];
        }
        for (int r = 0; r < nRay; r++) {
            ray1z[r] = m20 * ray[r][0] + m21 * ray[r][1] + m22 * ray[r][2];
            for (int i = 0; i < nFace; i++) {
                Norm1z[r][i] = m20 * Norm[r][i][0] + m21 * Norm[r][i][1] + m22 * Norm[r][i][2];
            }
        }

        for (int i = nRay - 1; --i >= 0;) {
            boolean flipped = false;
            for (int j = 0; j <= i; j++) {
                int a = Zsort[j], b = Zsort[j + 1];
                if (ray1z[a] > ray1z[b]) {
                    Zsort[j + 1] = a;
                    Zsort[j] = b;
                    flipped = true;
                }
            }
            if (!flipped) {
                break;
            }
        }
    }

    public void paint(Graphics g) {
        buffGraphics.clearRect(0, 0, w, h);
        for (int r = 0; r < nRay; r++) {
            int rs = Zsort[r];
            for (int i = 0; i < nFace; i++) {
                if (Norm1z[rs][i] > 0) {
                    for (int j = 0; j < face[rs][i].length; j++) {
                        xPol[j] = w2 + (int) vert1[face[rs][i][j]][0];
                        yPol[j] = h2 - (int) vert1[face[rs][i][j]][1];
                    }
                    buffGraphics.setColor(col[(int) (Norm1z[rs][i])]);
                    buffGraphics.fillPolygon(xPol, yPol, face[rs][i].length);
                }
            }
        }
        g.drawImage(buffImage, 0, 0, this);
//  showStatus( "fiX=" + (int)(360*fiX) + "   fiY=" + (int)(360*fiY));
    }

    public void update(Graphics g) {
        paint(g);
    }
}
