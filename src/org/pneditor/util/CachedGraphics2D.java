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
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class CachedGraphics2D extends Graphics2D {

    final private Graphics graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();
    private Font currentFont = new Font(null);
    private Color currentColor = Color.white;
    final private List<Drawable> toBeDrawn = new ArrayList<>();
    private float currentLineWidth = 1;
    final private Rectangle integerBounds = new Rectangle(-1, -1);
    final private Rectangle2D realBounds = new Rectangle2D.Float(0, 0, -1, -1);

    public Rectangle getIntegerBounds() {
        Rectangle result = new Rectangle(this.integerBounds);
        result.width++;
        result.height++;
        return result;
    }

    public Rectangle2D getRealBounds() {
        return this.realBounds;
    }

    public void applyToGraphics(final Graphics2D g) {
        for (final Drawable drawable : this.toBeDrawn) {
            drawable.draw(g);
        }
    }

    private void addPointToBounds(final int x, final int y) {
        final int excess = (int) Math.ceil(this.currentLineWidth / 2 - 0.5f);
        this.integerBounds.add(x + excess, y + excess);
        this.integerBounds.add(x - excess, y - excess);
        this.integerBounds.add(x + excess, y - excess);
        this.integerBounds.add(x - excess, y + excess);
        final double realExcess = this.currentLineWidth / 2;
        addPointToRectangle2D(this.realBounds, x + realExcess, y + realExcess);
        addPointToRectangle2D(this.realBounds, x - realExcess, y - realExcess);
        addPointToRectangle2D(this.realBounds, x + realExcess, y - realExcess);
        addPointToRectangle2D(this.realBounds, x - realExcess, y + realExcess);
    }

    private void addPointToRectangle2D(final Rectangle2D rectangle, final double x, final double y) {
        if (rectangle.getWidth() < 0 || rectangle.getHeight() < 0) {
            rectangle.setRect(x, y, 0, 0);
        } else {
            rectangle.add(x, y);
        }
    }

    private void addRectangleToBounds(final int x, final int y, final int width, final int height) {
        addPointToBounds(x, y);
        addPointToBounds(x + width, y + height);
        addPointToBounds(x, y + height);
        addPointToBounds(x + width, y);
    }

    private interface Drawable {

        void draw(Graphics2D g);
    }

    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
        addPointToBounds(x1, y1);
        addPointToBounds(x2, y2);
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.drawLine(x1, y1, x2, y2);
            }
        });
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        addRectangleToBounds(x, y, width - 1, height - 1);
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.fillRect(x, y, width, height);
            }
        });
    }

    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        for (int i = 0; i < nPoints; i++) {
            addPointToBounds(xPoints[i], yPoints[i]);
        }
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.drawPolygon(xPoints, yPoints, nPoints);
            }
        });
    }

    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
        for (int i = 0; i < nPoints; i++) {
            addPointToBounds(xPoints[i], yPoints[i]);
        }
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.fillPolygon(xPoints, yPoints, nPoints);
            }
        });
    }

    @Override
    public Color getColor() {
        return this.currentColor;
    }

    @Override
    public void setColor(final Color c) {
        this.currentColor = c;
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.setColor(c);
            }
        });
    }

    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
        addRectangleToBounds(x, y, width, height);
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.drawOval(x, y, width, height);
            }
        });
    }

    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
        addRectangleToBounds(x, y, width - 1, height - 1);
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.fillOval(x, y, width, height);
            }
        });
    }

    @Override
    public void setStroke(final Stroke s) {
        if (s instanceof BasicStroke) {
            final BasicStroke stroke = (BasicStroke) s;
            this.currentLineWidth = stroke.getLineWidth();
        }
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.setStroke(s);
            }
        });
    }

    @Override
    public void drawString(final String str, final int x, final int y) {
        final Rectangle stringBounds = getFontMetrics(this.currentFont).getStringBounds(str, this.graphics).getBounds();
        addRectangleToBounds(x, y - stringBounds.height, stringBounds.width, stringBounds.height);
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.drawString(str, x, y);
            }
        });
    }

    @Override
    public Font getFont() {
        return this.currentFont;
    }

    @Override
    public void setFont(final Font font) {
        this.currentFont = font;
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.setFont(font);
            }
        });
    }

    @Override
    public FontMetrics getFontMetrics(final Font f) {
        return this.graphics.getFontMetrics(f);
    }

    @Override
    public void setRenderingHint(final Key hintKey, final Object hintValue) {
        this.toBeDrawn.add(new Drawable() {
            @Override
			public void draw(final Graphics2D g) {
                g.setRenderingHint(hintKey, hintValue);
            }
        });
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
