/*
 * Copyright (C) 2008-2010 Martin Riesz <riesz.martin at gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pneditor.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class EPSGraphics2D extends Graphics2D {

    private static final String APP_NAME = "PNEditor";
    private final Graphics graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();
    private Font currentFont = new Font(null);
    private final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    private final PrintStream out = new PrintStream(this.arrayOutputStream);
    private final CachedGraphics2D cachedGraphics = new CachedGraphics2D();
    private Color currentColor = Color.black;

    public void writeToFile(final File file) throws FileNotFoundException {
    	final PrintStream fileOut = new PrintStream(file);
        fileOut.println("%!PS-Adobe-3.0 EPSF-3.0");
        fileOut.println("%%Creator: " + APP_NAME);
        fileOut.println("%%Pages: 1");
        fileOut.println("%%Orientation: Portrait");
        final Rectangle2D bounds = xy(this.cachedGraphics.getRealBounds());
        fileOut.println("%%BoundingBox: "
                + (long) Math.floor(bounds.getMinX()) + " "
                + (long) Math.floor(bounds.getMinY()) + " "
                + (long) Math.ceil(bounds.getMaxX()) + " "
                + (long) Math.ceil(bounds.getMaxY()));
        fileOut.println("%%HiResBoundingBox: "
                + bounds.getMinX() + " "
                + bounds.getMinY() + " "
                + bounds.getMaxX() + " "
                + bounds.getMaxY());
        fileOut.println("%%EndComments");
        fileOut.println("%%Page: 1 1");
        fileOut.println("0 0 0 setrgbcolor");
        fileOut.println("[] 0 setdash");
        fileOut.println("1 setlinewidth");
        fileOut.println("0 setlinejoin");
        fileOut.println("0 setlinecap");
        fileOut.println("gsave [1 0 0 1 0 0] concat");
        fileOut.println("/Times-Bold findfont");
        fileOut.println("12 scalefont");
        fileOut.println("setfont");
        fileOut.print(this.arrayOutputStream.toString());
        fileOut.println("grestore");
        fileOut.println("showpage");
        fileOut.println("%%EOF");
        fileOut.close();
    }

    private double x(final double x) {
        return x;
    }

    private double y(final double y) {
        return -y;
    }

    private Rectangle2D xy(final Rectangle2D rectangle) {
    	final Rectangle2D result = new Rectangle2D.Double();
    	final double x1 = x(rectangle.getX());
    	final double y1 = y(rectangle.getY());
    	final double x2 = x(rectangle.getMaxX());
    	final double y2 = y(rectangle.getMaxY());
        result.setFrameFromDiagonal(x1, y1, x2, y2);
        return result;
    }

    private void newPath() {
        this.out.println("newpath");
    }

    private void closePath() {
        this.out.println("closepath");
    }

    private void stroke() {
        this.out.println("stroke");
    }

    private void fill() {
        this.out.println("fill");
    }

    private void moveTo(final double x, final double y) {
        this.out.println(x(x) + " " + y(y) + " moveto");
    }

    private void lineTo(final double x, final double y) {
        this.out.println(x(x) + " " + y(y) + " lineto");
    }

    private void curveTo(final double x1, final double y1, final double x2, final double y2, final double x3, final double y3) {
        this.out.println(x(x1) + " " + y(y1) + " " + x(x2) + " " + y(y2) + " " + x(x3) + " " + y(y3) + " curveto");
    }

    private void circle(final double centerX, final double centerY, final double radius) {
        this.out.println(x(centerX) + " " + y(centerY) + " " + radius + " 0 360 arc");
    }

    private void setColor(final double red, final double green, final double blue) {
        this.out.println(red + " " + green + " " + blue + " setrgbcolor");
    }

    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        this.cachedGraphics.drawLine(x1, y1, x2, y2);
        this.out.println();
        this.out.println("% begin drawLine");
        newPath();
        moveTo(x1, y1);
        lineTo(x2, y2);
        stroke();
        this.out.println("% end drawLine");
        this.out.println();
    }

    private void makeRectanglePath(final int x, final int y, final int width, final int height) {
        newPath();
        moveTo(x, y);
        lineTo(x + width, y);
        lineTo(x + width, y + height);
        lineTo(x, y + height);
        closePath();
    }

    @Override
    public void drawRect(final int x, final int y, final int width, final int height) {
        this.cachedGraphics.drawRect(x, y, width, height);
        this.out.println();
        this.out.println("% begin drawRect");
        makeRectanglePath(x, y, width + 1, height + 1);
        stroke();
        this.out.println("% end drawRect");
        this.out.println();
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        this.cachedGraphics.fillRect(x, y, width, height);
        this.out.println();
        this.out.println("% begin fillRect");
        makeRectanglePath(x, y, width, height);
        fill();
        this.out.println("% end fillRect");
        this.out.println();
    }

    private void makePolygonPath(final int[] xPoints, final int[] yPoints, final int nPoints) {
        newPath();
        moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            lineTo(xPoints[i], yPoints[i]);
        }
        closePath();
    }

    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        this.cachedGraphics.drawPolygon(xPoints, yPoints, nPoints);
        this.out.println();
        this.out.println("% begin drawPolygon");
        makePolygonPath(xPoints, yPoints, nPoints);
        stroke();
        this.out.println("% end drawPolygon");
        this.out.println();
    }

    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        this.cachedGraphics.fillPolygon(xPoints, yPoints, nPoints);
        this.out.println();
        this.out.println("% begin fillPolygon");
        makePolygonPath(xPoints, yPoints, nPoints);
        fill();
        this.out.println("% end fillPolygon");
        this.out.println();
    }

    @Override
    public Color getColor() {
        return this.currentColor;
    }

    @Override
    public void setColor(final Color c) {
    	final double red = (double) c.getRed() / 255;
    	final double green = (double) c.getGreen() / 255;
    	final double blue = (double) c.getBlue() / 255;
        setColor(red, green, blue);
        this.currentColor = c;
    }

    private void makeOvalPath(final int x, final int y, final int width, final int height) {
        newPath();
        if (width == height) {
        	final double radius = (double) width / 2;
        	final double centerX = x + radius;
        	final double centerY = y + radius;
            circle(centerX, centerY, radius);
            closePath();
        } else {
            final double kappa = 0.552_284_749_8;
            final double lHorizontal = kappa * width / 2;
            final double lVertical = kappa * height / 2;
            final double halfWidth = (double) width / 2;
            final double halfHeight = (double) height / 2;
            moveTo(x + halfWidth, y);
            curveTo(x + halfWidth + lHorizontal, y,
                    x + width, y + lVertical,
                    x + width, y + halfHeight);
            curveTo(x + width, y + halfHeight + lVertical,
                    x + halfWidth + lHorizontal, y + height,
                    x + halfWidth, y + height);
            curveTo(x + lHorizontal, y + height,
                    x, y + halfHeight + lVertical,
                    x, y + halfHeight);
            curveTo(x, y + lVertical,
                    x + lHorizontal, y,
                    x + halfWidth, y);
            closePath();
        }
    }

    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        this.cachedGraphics.drawOval(x, y, width, height);
        this.out.println();
        this.out.println("% begin drawOval");
        makeOvalPath(x, y, width + 1, height + 1);
        stroke();
        this.out.println("% end drawOval");
        this.out.println();
    }

    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        this.cachedGraphics.fillOval(x, y, width, height);
        this.out.println();
        this.out.println("% begin fillOval");
        makeOvalPath(x, y, width, height);
        fill();
        this.out.println("% end fillOval");
        this.out.println();
    }

    @Override
    public void setStroke(final Stroke s) {
        this.cachedGraphics.setStroke(s);
        if (s instanceof BasicStroke) {
        	final BasicStroke stroke = (BasicStroke) s;
        	final double currentLineWidth = stroke.getLineWidth();
        	final float[] dashArray = stroke.getDashArray();
            final int lineCap = stroke.getEndCap();
            final int lineJoin = stroke.getLineJoin();
            this.out.println(currentLineWidth + " setlinewidth");
            this.out.println(lineCap + " setlinecap");
            this.out.println(lineJoin + " setlinejoin");
            if (dashArray != null) {
            	final float dashPhase = stroke.getDashPhase();
                this.out.print("[");
                for (final float d : dashArray) {
                    this.out.print(d + " ");
                }
                this.out.println("] " + dashPhase + " setdash");
            } else {
                this.out.println("[] 0 setdash");
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    @Override
    public void drawString(final String str, final int x, final int y) {
        this.cachedGraphics.drawString(str, x, y);
        final String fileStr = str.replace("(", "\\(").replace(")", "\\)");
        moveTo(x, y);
        this.out.println("(" + fileStr + ") show");
    }

    @Override
    public Font getFont() {
        return this.currentFont;
    }

    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return this.graphics.getFontMetrics(f);
    }

    @Override
    public void setFont(final Font font) {
        this.cachedGraphics.setFont(font);
        this.out.println("/Times-Bold findfont");
//      System.out.println("/" + font.getFamily() + "-" + font.getStyle() + " findfont");
        this.out.println(font.getSize() + (2 * font.getSize() / 12) + " scalefont");
        this.out.println("setfont");
        this.currentFont = font;
    }

    //########################// NOT SUPPORTED YET //#########################//
    @Override
    public void draw(final Shape s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawString(final String str, final float x, final float y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fill(final Shape s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setComposite(final Composite comp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPaint(final Paint paint) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRenderingHint(final Key hintKey, final Object hintValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getRenderingHint(final Key hintKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRenderingHints(final Map<?, ?> hints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addRenderingHints(final Map<?, ?> hints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RenderingHints getRenderingHints() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void translate(final int x, final int y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void translate(final double tx, final double ty) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rotate(final double theta) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void rotate(final double theta, final double x, final double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void scale(final double sx, final double sy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shear(final double shx, final double shy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void transform(final AffineTransform tx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTransform(final AffineTransform tx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AffineTransform getTransform() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Paint getPaint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Composite getComposite() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBackground(final Color color) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Color getBackground() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Stroke getStroke() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clip(final Shape s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Graphics create() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPaintMode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setXORMode(final Color c1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Rectangle getClipBounds() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Shape getClip() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setClip(final Shape clip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dispose() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
