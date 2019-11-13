package ru.anhot.archive.cybereye;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.net.URL;

public class Cybereye extends Applet {
    Image skin;
    int l = 80;
    int z0 = 100;
    int oldx;
    int oldy;
    int k = 10;
    int xshift = 0;
    int yshift = 0;
    String mdname;
    String shading;
    String skinname;
    String wire;
    String pointorder;
    double[][] points;
    int[][] faces;
    int numPoints;
    int numFaces;
    Color[] palette = new Color[255];

    public void draw(Graphics graphics) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];

        for(int face = 0; face < this.numFaces; ++face) {
            int point = 0;

            do {
                xPoints[point] = this.xshift + (int)Math.round(this.points[this.faces[face][point]][0] * (double)this.l / ((double)this.l + this.points[this.faces[face][point]][2] + (double)this.z0) * (double)this.k / 10.0D + (double)(this.size().width / 2));
                yPoints[point] = this.yshift + (int)Math.round(this.points[this.faces[face][point]][1] * (double)this.l / ((double)this.l + this.points[this.faces[face][point]][2] + (double)this.z0) * (double)this.k / 10.0D + (double)(this.size().height / 2));
                ++point;
            } while(point < 3);

            double normal = 0.5D;
            if (this.shading.equals("on")) {
                normal = this.calcNormal(face);
            }

            if (normal > 0.0D) {
                int shadeColor = (int)Math.round(normal * 200.0D);
                graphics.setColor(this.palette[shadeColor]);
                if (this.wire.equals("on")) {
                    graphics.drawPolygon(xPoints, yPoints, 3);
                } else {
                    graphics.fillPolygon(xPoints, yPoints, 3);
                }
            }
        }

        graphics.setColor(Color.white);
        graphics.drawString("(C) ShadowLamer, 2001", 5, this.size().height - 5);
    }

    public void paint(Graphics graphics) {
        Image image = this.createImage(this.size().width, this.size().height);
        Graphics imageGraphics = image.getGraphics();
        imageGraphics.drawImage(this.skin, 0, 0, this);
        this.draw(imageGraphics);
        graphics.drawImage(image, 0, 0, this);
    }

    public Cybereye() {
    }

    void QuickSort(int startFace, int endFace) {
        int tmpStartFace = startFace;
        int tmpEndFace = endFace;
        if (endFace > startFace) {
            double zorder = this.calcZorder((startFace + endFace) / 2);

            while(tmpStartFace <= tmpEndFace) {
                while(tmpStartFace < endFace && this.calcZorder(tmpStartFace) > zorder) {
                    ++tmpStartFace;
                }

                while(tmpEndFace > startFace && this.calcZorder(tmpEndFace) < zorder) {
                    --tmpEndFace;
                }

                if (tmpStartFace <= tmpEndFace) {
                    this.swap(tmpStartFace, tmpEndFace);
                    ++tmpStartFace;
                    --tmpEndFace;
                }
            }

            if (startFace < tmpEndFace) {
                this.QuickSort(startFace, tmpEndFace);
            }

            if (tmpStartFace < endFace) {
                this.QuickSort(tmpStartFace, endFace);
            }
        }

    }

    public void sort() {
        this.QuickSort(0, this.numFaces - 1);
    }

    public double calcZorder(int face) {
        double result = 0.0D;
        int point = 0;

        do {
            result += this.points[this.faces[face][point]][2];
            ++point;
        } while(point < 3);

        return result;
    }

    public double rad(float degrees) {
        return (double)(degrees / 180.0F) * 3.141592654D;
    }

    private void swap(int face1, int face2) {
        int[] tmp = this.faces[face1];
        this.faces[face1] = this.faces[face2];
        this.faces[face2] = tmp;
    }

    public void trans(float alpha, float beta) {
        double[] tmpPoint = new double[3];
        double sinBeta = Math.sin(this.rad(beta));
        double sinAlpha = Math.sin(this.rad(alpha));
        double cosBeta = Math.cos(this.rad(beta));
        double cosAlpha = Math.cos(this.rad(alpha));

        for(int point = 0; point < this.numPoints; ++point) {
            tmpPoint[0] = cosBeta * this.points[point][0] - sinBeta * this.points[point][2];
            tmpPoint[1] = sinAlpha * sinBeta * this.points[point][0] + cosAlpha * this.points[point][1] + sinAlpha * cosBeta * this.points[point][2];
            tmpPoint[2] = cosAlpha * sinBeta * this.points[point][0] - sinAlpha * this.points[point][1] + cosAlpha * cosBeta * this.points[point][2];
            this.points[point][0] = tmpPoint[0];
            this.points[point][1] = tmpPoint[1];
            this.points[point][2] = tmpPoint[2];
        }

    }

    public void init() {
        this.mdname = this.getParameter("model");
        this.shading = this.getParameter("shading");
        this.skinname = this.getParameter("skin");
        this.wire = this.getParameter("wire");
        this.pointorder = this.getParameter("pointorder");
        this.skin = this.getImage(this.getCodeBase(), this.skinname);

        int i;
        try {
            InputStream inputStream = (new URL(this.getDocumentBase(), this.mdname)).openStream();
            StreamTokenizer tokenizer = new StreamTokenizer(new BufferedInputStream(inputStream, 4000));
            tokenizer.eolIsSignificant(true);
            tokenizer.commentChar(35);
            this.waitfor("Vertices", tokenizer);
            this.numPoints = (int)Math.round(tokenizer.nval);
            this.waitfor("Faces", tokenizer);
            this.numFaces = (int)Math.round(tokenizer.nval);
            this.points = new double[this.numPoints][3];
            this.faces = new int[this.numFaces][3];

            for(i = 0; i < this.numPoints; ++i) {
                this.waitfor("X", tokenizer);
                this.points[i][0] = tokenizer.nval;
                this.waitfor("Y", tokenizer);
                this.points[i][1] = tokenizer.nval;
                this.waitfor("Z", tokenizer);
                this.points[i][2] = tokenizer.nval;
            }

            for(i = 0; i < this.numFaces; ++i) {
                this.waitfor("A", tokenizer);
                this.faces[i][0] = (int)Math.round(tokenizer.nval);
                this.waitfor("B", tokenizer);
                this.faces[i][1] = (int)Math.round(tokenizer.nval);
                this.waitfor("C", tokenizer);
                this.faces[i][2] = (int)Math.round(tokenizer.nval);
                if (this.pointorder.equals("inverse")) {
                    int tmpPoint = this.faces[i][0];
                    this.faces[i][0] = this.faces[i][1];
                    this.faces[i][1] = tmpPoint;
                }
            }

            inputStream.close();
        } catch (Exception e) {
        }

        i = 0;

        do {
            this.palette[i] = new Color(i, i, i);
            ++i;
        } while(i < 255);

    }

    public double calcNormal(int face) {
        double var2 = 1.0D;
        double var4 = 1.0D;
        double[] var6 = new double[3];
        double[] var7 = new double[3];
        double[] var8 = new double[3];
        double[] var9 = new double[3];
        int var10 = 0;

        do {
            var6[var10] = this.points[this.faces[face][0]][var10];
            var7[var10] = this.points[this.faces[face][1]][var10] - var6[var10];
            var8[var10] = this.points[this.faces[face][2]][var10] - var6[var10];
            ++var10;
        } while(var10 < 3);

        var6[2] = (double)this.z0;
        var9[0] = var7[1] * var8[2] - var8[1] * var7[2];
        var9[1] = -var7[0] * var8[2] + var8[0] * var7[2];
        var9[2] = var7[0] * var8[1] - var8[0] * var7[1];
        var2 = Math.sqrt(var9[0] * var9[0] + var9[1] * var9[1] + var9[2] * var9[2]);
        var4 = Math.sqrt(var6[0] * var6[0] + var6[1] * var6[1] + var6[2] * var6[2]);
        return var4 != 0.0D && var2 != 0.0D ? (var9[0] * var6[0] + var9[1] * var6[1] + var9[2] * var6[2]) / var4 / var2 : 0.0D;
    }

    public boolean mouseDrag(Event event, int x, int y) {
        if (event.shiftDown()) {
            this.k = this.k + this.oldy - y;
            if (this.k < 5) {
                this.k = 5;
            }
        } else if (event.controlDown()) {
            this.yshift = this.yshift + y - this.oldy;
            this.xshift = this.xshift + x - this.oldx;
        } else {
            this.trans((float)(this.oldy - y), (float)(x - this.oldx));
            this.sort();
        }

        Graphics graphics = this.getGraphics();
        Image image = this.createImage(this.size().width, this.size().height);
        Graphics imageGraphics = image.getGraphics();
        imageGraphics.drawImage(this.skin, 0, 0, this);
        this.draw(imageGraphics);
        graphics.drawImage(image, 0, 0, this);
        this.oldx = x;
        this.oldy = y;
        return true;
    }

    public void waitfor(String token, StreamTokenizer tokenizer) {
        try {
            while(!token.equals(tokenizer.sval)) {
                tokenizer.nextToken();
            }

            tokenizer.nextToken();
        } catch (Exception e) {
        }
    }

    public boolean mouseMove(Event event, int x, int y) {
        this.oldx = x;
        this.oldy = y;
        return true;
    }
}
