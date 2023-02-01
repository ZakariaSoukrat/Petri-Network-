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
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Martin Riesz <riesz.martin at gmail.com>
 */
public class GraphicsTools {

    //PNEConstantsConfiguration.RESOURCESDIR already ends with a File.separator
    //private final static String RESOURCESDIR = File.separator+org.pneditor.PNEConstantsConfiguration.RESOURCESDIR;

    public static ImageIcon getIcon(final String fileName) {
        return new ImageIcon(GraphicsTools.class.getResource(fileName));
        //return new ImageIcon(GraphicsTools.class.getResource(RESOURCESDIR + fileName));
    }

    public static Cursor getCursor(final String fileName, final Point center) {
    	final Toolkit tk = Toolkit.getDefaultToolkit();
    	//final Image image = tk.getImage(GraphicsTools.class.getResource(RESOURCESDIR + fileName));
    	final Image image = tk.getImage(GraphicsTools.class.getResource(fileName));
        return tk.createCustomCursor(image, center, fileName);
    }

    public static BufferedImage getBufferedImage(final String fileName) {
        try {
            return ImageIO.read(GraphicsTools.class.getResource(fileName));
        } catch (IOException ex) {
            Logger.getLogger(GraphicsTools.class.getName()).log(Level.SEVERE, null, ex); //TODO: change this
            return new BufferedImage(1, 1, IndexColorModel.TRANSLUCENT);
        }
    }

    public static void setDashedStroke(final Graphics g) {
        final float dash1[] = {4.0f};
        final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4.0f, dash1, 0.0f);
        ((Graphics2D) g).setStroke(dashed);
    }

    public static void setDefaultStroke(final Graphics g) {
        final BasicStroke defaultStroke = new BasicStroke();
        ((Graphics2D) g).setStroke(defaultStroke);
    }


    public enum HorizontalAlignment {

        LEFT,
        RIGHT,
        CENTER
    }

    public enum VerticalAlignment {

        TOP,
        CENTER,
        BOTTOM
    }

    public static void drawString(final Graphics g, final String str, final int x, final int y,
    		final HorizontalAlignment horizontalAlignment,
    		final VerticalAlignment verticalAlignment) {
    	final int textWidth = g.getFontMetrics().stringWidth(str);
        final int textHeight = g.getFontMetrics().getAscent();
        int resultX = x;
        int resultY = y;

        // if horizontalAligment is left, do nothing
        if (horizontalAlignment == HorizontalAlignment.CENTER) {
            resultX -= textWidth / 2;
        } else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
            resultX -= textWidth;
        }

        // if verticalAligment is bottom, do nothing
        if (verticalAlignment == VerticalAlignment.TOP) {
            resultY += textHeight;
        } else if (verticalAlignment == VerticalAlignment.CENTER) {
            resultY += textHeight / 2 - 1;
        }
        

        final Color previousColor = g.getColor();
        g.setColor(new Color(1f, 1f, 1f, 0.7f));
//      g.setColor(new Color(0.7f, 0.7f, 1f, 0.7f)); //debug with this
        g.fillRect(resultX, resultY - textHeight + 1, textWidth, g.getFontMetrics().getHeight() - 1);
        g.setColor(previousColor);
        g.drawString(str, resultX, resultY);
    }

    //Jan Tancibok Inhibitor arc, Taken from http://stackoverflow.com/questions/21465570/two-points-and-then-finds-the-smallest-circle-and-the-smallest-rectangle-contain?rq=1
    public static void drawCircle(final Graphics g, final int xCenter, final int yCenter, final int x2, final int y2) {
    	final Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(1f));

        final double aDir = Math.atan2(xCenter - x2, yCenter - y2);
        final int i2 = 9; //diameter

        final int x1 = x2 + xCor(i2, aDir);
        final int y1 = y2 + yCor(i2, aDir);

        final double diameter = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        final int cx = ((x2 + x1) / 2);
        final int cy = (y2 + y1) / 2;
        final int tlCornerx = (int) (cx - diameter / 2);
        final int tlCornery = (int) (cy - diameter / 2);

        g2d.drawOval(tlCornerx, tlCornery, (int) diameter, (int) diameter);
        g2d.fillOval(tlCornerx, tlCornery, (int) diameter, (int) diameter);
    }

    //Taken from http://forum.java.sun.com/thread.jspa?threadID=378460&tstart=135
    public static void drawArrow(final Graphics g, final int xCenter, final int yCenter, final int x, final int y) {
    	final Graphics2D g2d = (Graphics2D) g;
    	final double aDir = Math.atan2(xCenter - x, yCenter - y);
        //g2d.drawLine(x, y, xCenter, yCenter);
        g2d.setStroke(new BasicStroke(1f));                 // make the arrow head solid even if dash pattern has been specified
        final Polygon tmpPoly = new Polygon();
        final int i1 = 12;
        final int i2 = 6;                         // make the arrow head the same size regardless of the length
        tmpPoly.addPoint(x, y);                         // arrow tip
        tmpPoly.addPoint(x + xCor(i1, aDir + 0.5), y + yCor(i1, aDir + 0.5));
        tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
        tmpPoly.addPoint(x + xCor(i1, aDir - 0.5), y + yCor(i1, aDir - 0.5));
        tmpPoly.addPoint(x, y);                         // arrow tip
        g2d.drawPolygon(tmpPoly);
        g2d.fillPolygon(tmpPoly);                       // remove this line to leave arrow head unpainted
    }

    //Jan Tancibok Reset arc
    public static void drawArrowDouble(final Graphics g, final int xCenter, final int yCenter, final int x, final int y) {
    	final Graphics2D g2d = (Graphics2D) g;
    	final double aDir = Math.atan2(xCenter - x, yCenter - y);
        //g2d.drawLine(x, y, xCenter, yCenter);
        g2d.setStroke(new BasicStroke(1f));                 // make the arrow head solid even if dash pattern has been specified
        final Polygon tmpPoly = new Polygon();
        int i1 = 12;
        final int i2 = 6;                         // make the arrow head the same size regardless of the length length
        tmpPoly.addPoint(x, y);                         // arrow tip
        tmpPoly.addPoint(x + xCor(i1, aDir + 0.5), y + yCor(i1, aDir + 0.5));
        tmpPoly.addPoint(x + xCor(i2, aDir), y + yCor(i2, aDir));
        tmpPoly.addPoint(x + xCor(i1, aDir - 0.5), y + yCor(i1, aDir - 0.5));
        tmpPoly.addPoint(x, y);                         // arrow tip
        g2d.drawPolygon(tmpPoly);
        g2d.fillPolygon(tmpPoly);

        i1 = 24;
        final int move = 6;
        final int dmove = 12;
        tmpPoly.addPoint(x + xCor(i2 + move, aDir), y + yCor(i2 + move, aDir));                         // arrow tip
        tmpPoly.addPoint(x + xCor(i1, aDir - 0.25), y + yCor(i1, aDir - 0.25));
        tmpPoly.addPoint(x + xCor(i2 + dmove, aDir), y + yCor(i2 + dmove, aDir));
        tmpPoly.addPoint(x + xCor(i1, aDir + 0.25), y + yCor(i1, aDir + 0.25));
        tmpPoly.addPoint(x + xCor(i2 + move, aDir), y + yCor(i2 + move, aDir));                         // arrow tip
        g2d.drawPolygon(tmpPoly);
        g2d.fillPolygon(tmpPoly);// remove this line to leave arrow head unpainted
    }

    private static int yCor(final int len, final double dir) {
        return (int) (len * Math.cos(dir));
    }

    private static int xCor(final int len, final double dir) {
        return (int) (len * Math.sin(dir));
    }

    public static boolean isPointNearSegment(final Point from, final Point to, final Point testPos, final int nearTolerance) {
    	final Rectangle r = new Rectangle(testPos.x - nearTolerance / 2, testPos.y - nearTolerance / 2, nearTolerance, nearTolerance);
        return r.intersectsLine(from.x, from.y, to.x, to.y);
    }

    public static boolean isPointNearPoint(final Point from, final Point testPos, final int nearTolerance) {
    	final Rectangle r1 = new Rectangle(from.x - nearTolerance / 2, from.y - nearTolerance / 2, nearTolerance, nearTolerance);
    	final Rectangle r2 = new Rectangle(testPos.x, testPos.y, 1, 1);
        return r2.intersects(r1);
    }
}
