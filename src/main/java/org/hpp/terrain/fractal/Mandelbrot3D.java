package org.hpp.terrain.fractal;

// 3D Mandelbrot mountains,  Evgeny Demidov  11 Oct 2001
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

public class Mandelbrot3D extends java.applet.Applet
        implements MouseListener, MouseMotionListener {

    int MaxIt = 300, n = 2, n1, h, w, h2, w2, mx0, my0, xPol[], yPol[];
    double fiX = .2, fiY = .3, dfi = .01, scale = .8, m20, m21, m22,
            re0 = -2, im0 = -1.5, del = 3;
    double vert[][][], vert1[][][], Norm[][][][], Norm1z[][][];
    Image buffImage;
    Graphics buffGraphics;
    Color[] col;
    boolean painted;

    public double Fun(double cr, double ci) {
        double I = 0.0, R = 0.0, I2 = 0.0, R2 = 0.0;
        int n = 0;
        do {
            I = R * (I + I) + ci;
            R = R2 - I2 + cr;
            R2 = R * R;
            I2 = I * I;
            n++;
        } while ((R2 + I2 < 4.0) && (n < MaxIt));
        return 40. * Math.log(n) - 100;
    }

    public void init() {
        w = getSize().width;
        h = getSize().height;
        w2 = w / 2;
        h2 = h / 2;
        String s = getParameter("N");
        if (s != null) {
            n = Integer.parseInt(s);
        }
        xPol = new int[3];
        yPol = new int[3];
        buffImage = createImage(w, h);
        buffGraphics = buffImage.getGraphics();
        col = new Color[256];
        for (int i = 0; i < 256; i++) {
            col[i] = new Color(i, i, i);
        }
        s = getParameter("coord");
        if (s != null) {
            StringTokenizer st = new StringTokenizer(s);
            re0 = Double.valueOf(st.nextToken()).doubleValue();
            im0 = Double.valueOf(st.nextToken()).doubleValue();
            del = Double.valueOf(st.nextToken()).doubleValue();
        }
        s = getParameter("bgColor");
        if (s != null) {
            StringTokenizer st = new StringTokenizer(s);
            int red = Integer.parseInt(st.nextToken());
            int green = Integer.parseInt(st.nextToken());
            int blue = Integer.parseInt(st.nextToken());
            setBackground(new Color(red, green, blue));
        } else {
            setBackground(new Color(255, 255, 255));
        }
        addMouseListener(this);
        addMouseMotionListener(this);
        setup();
    }

    public void setup() {
        n1 = n - 1;
        vert = new double[n][n][3];
        vert1 = new double[n][n][2];
        double n2 = n / 2., dx = w / (double) n1, dr = del / n1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                vert[i][j][0] = dx * i - w2;
                vert[i][j][2] = dx * j - w2;
                vert[i][j][1] = Fun(re0 + dr * i, im0 + dr * j);
            }
        }
        Norm = new double[n][n][2][3];
        Norm1z = new double[n][n][2];
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n1; j++) {
                Norm[i][j][0][0] = vert[i][j][1] - vert[i + 1][j][1];
                Norm[i][j][0][1] = dx;
                Norm[i][j][0][2] = vert[i + 1][j][1] - vert[i + 1][j + 1][1];
                double mod = Math.sqrt(Norm[i][j][0][0] * Norm[i][j][0][0] + Norm[i][j][0][1]
                        * Norm[i][j][0][1] + Norm[i][j][0][2] * Norm[i][j][0][2]) / 255.5;
                Norm[i][j][0][0] /= mod;
                Norm[i][j][0][1] /= mod;
                Norm[i][j][0][2] /= mod;
                Norm[i][j][1][0] = vert[i][j + 1][1] - vert[i + 1][j + 1][1];
                Norm[i][j][1][1] = dx;
                Norm[i][j][1][2] = vert[i][j][1] - vert[i][j + 1][1];
                mod = Math.sqrt(Norm[i][j][1][0] * Norm[i][j][1][0] + Norm[i][j][1][1]
                        * Norm[i][j][1][1] + Norm[i][j][1][2] * Norm[i][j][1][2]) / 255.5;
                Norm[i][j][1][0] /= mod;
                Norm[i][j][1][1] /= mod;
                Norm[i][j][1][2] /= mod;
            }
        }
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
        if (e.isControlDown()) {
            n /= 2;
            if (n < 4) {
                n = 4;
            }
            setup();
            repaint();
        }
        if (e.isAltDown()) {
            n *= 2;
            setup();
            repaint();
        }
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
        if (e.isShiftDown()) {
            scale *= Math.exp(-(y1 - my0) / (double) w);
        } else {
            fiX += dfi * (y1 - my0);
        }
        fiY += dfi * (x1 - mx0);
        mx0 = x1;
        my0 = y1;
        rotate();
        repaint();
        e.consume();
    }

    public void rotate() {
        double ct = Math.cos(fiX), cf = Math.cos(fiY),
                st = Math.sin(fiX), sf = Math.sin(fiY),
                m00 = scale * cf, m02 = scale * sf,
                m10 = scale * st * sf, m11 = scale * ct, m12 = -scale * st * cf;
        m20 = -ct * sf;
        m21 = st;
        m22 = ct * cf;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                vert1[i][j][0] = m00 * vert[i][j][0] + m02 * vert[i][j][2];
                vert1[i][j][1] = m10 * vert[i][j][0] + m11 * vert[i][j][1] + m12 * vert[i][j][2];
            }
        }
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n1; j++) {
                for (int k = 0; k < 2; k++) {
                    Norm1z[i][j][k] = m20 * Norm[i][j][k][0] + m21 * Norm[i][j][k][1]
                            + m22 * Norm[i][j][k][2];
                }
            }
        }
        painted = false;
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void paint(Graphics g) {
        if (!painted) {
            buffGraphics.clearRect(0, 0, w, h);
            int ib = 0, ie = n1, sti = 1, jb = 0, je = n1, stj = 1;
            if (m20 < 0) {
                ib = n1;
                ie = -1;
                sti = -1;
            }
            if (m22 < 0) {
                jb = n1;
                je = -1;
                stj = -1;
            }
            for (int i = ib; i != ie; i += sti) {
                for (int j = jb; j != je; j += stj) {
                    if (Norm1z[i][j][0] > 0) {
                        xPol[0] = w2 + (int) vert1[i][j][0];
                        xPol[1] = w2 + (int) vert1[i + 1][j][0];
                        xPol[2] = w2 + (int) vert1[i + 1][j + 1][0];
                        yPol[0] = h2 - (int) vert1[i][j][1];
                        yPol[1] = h2 - (int) vert1[i + 1][j][1];
                        yPol[2] = h2 - (int) vert1[i + 1][j + 1][1];
                        buffGraphics.setColor(col[(int) (Norm1z[i][j][0])]);
                        buffGraphics.fillPolygon(xPol, yPol, 3);
                    }
                    if (Norm1z[i][j][1] > 0) {
                        xPol[0] = w2 + (int) vert1[i][j][0];
                        xPol[1] = w2 + (int) vert1[i][j + 1][0];
                        xPol[2] = w2 + (int) vert1[i + 1][j + 1][0];
                        yPol[0] = h2 - (int) vert1[i][j][1];
                        yPol[1] = h2 - (int) vert1[i][j + 1][1];
                        yPol[2] = h2 - (int) vert1[i + 1][j + 1][1];
                        buffGraphics.setColor(col[(int) (Norm1z[i][j][1])]);
                        buffGraphics.fillPolygon(xPol, yPol, 3);
                    }
                }
            }
            painted = true;
        }
        g.drawImage(buffImage, 0, 0, this);
        showStatus("n=" + n);
    }

    public void update(Graphics g) {
        paint(g);
    }
}
