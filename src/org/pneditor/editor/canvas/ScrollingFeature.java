package org.pneditor.editor.canvas;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JScrollBar;

import org.pneditor.editor.Root;
import org.pneditor.util.Point;

/**
 *
 * @author matmas
 */
public class ScrollingFeature implements Feature, MouseListener, MouseMotionListener, AdjustmentListener {

    private final Canvas canvas;
    private final Root root;
    private int prevDragX;
    private int prevDragY;
    private boolean scrolling;

    public ScrollingFeature(final Canvas canvas) {
        this.canvas = canvas;
        this.root = canvas.getRoot();
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    @Override
	public void mousePressed(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2
                || e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
            this.prevDragX = e.getX();
            this.prevDragY = e.getY();
            this.scrolling = true;
        }
    }

    @Override
	public void mouseDragged(final MouseEvent e) {
        if (this.scrolling) {
            doTheScrolling(e.getX(), e.getY());
            this.prevDragX = e.getX();
            this.prevDragY = e.getY();
        }
    }

    @Override
	public void mouseReleased(final MouseEvent e) {
        if (this.scrolling) {
            doTheScrolling(e.getX(), e.getY());
            this.scrolling = false;
        }
    }

    private void doTheScrolling(final int mouseX, final int mouseY) {
    	final Point viewTranslation = this.canvas.getViewTranslation();
        this.canvas.setViewTranslation(viewTranslation.getTranslated(mouseX - this.prevDragX, mouseY - this.prevDragY));
        this.canvas.repaint();
    }

    @Override
	public void drawForeground(final Graphics g) {
    }

    @Override
	public void drawBackground(final Graphics g) {
    }

    @Override
	public void drawMainLayer(final Graphics g) {
    	final Rectangle petriNetBounds = this.root.getGraphicPetriNet().getBounds();
    	final Rectangle canvasBounds = this.canvas.getBounds();

    	final JScrollBar horizontalScrollBar = this.root.getDrawingBoard().getHorizontalScrollBar();
    	final JScrollBar verticalScrollBar = this.root.getDrawingBoard().getVerticalScrollBar();

        canvasBounds.translate(-this.canvas.getViewTranslation().getX(), -this.canvas.getViewTranslation().getY()); // to account for translation
        petriNetBounds.translate(this.canvas.getWidth() / 2, this.canvas.getHeight() / 2); // [0, 0] is in center

        // Union of the two rectangles:
        if (!petriNetBounds.isEmpty()) {
            petriNetBounds.add(canvasBounds);
        }

        horizontalScrollBar.setEnabled(false);
        horizontalScrollBar.setMinimum(petriNetBounds.x);
        horizontalScrollBar.setMaximum(petriNetBounds.x + petriNetBounds.width);
        horizontalScrollBar.setVisibleAmount(canvasBounds.width);
        horizontalScrollBar.setValue(-this.canvas.getViewTranslation().getX());
        horizontalScrollBar.setEnabled(true);

        verticalScrollBar.setEnabled(false);
        verticalScrollBar.setMinimum(petriNetBounds.y);
        verticalScrollBar.setMaximum(petriNetBounds.y + petriNetBounds.height);
        verticalScrollBar.setVisibleAmount(canvasBounds.height);
        verticalScrollBar.setValue(-this.canvas.getViewTranslation().getY());
        verticalScrollBar.setEnabled(true);
    }

    @Override
	public void mouseClicked(final MouseEvent e) {
    }

    @Override
	public void mouseEntered(final MouseEvent e) {
    }

    @Override
	public void mouseExited(final MouseEvent e) {
    }

    @Override
	public void mouseMoved(final MouseEvent e) {
    }

    @Override
    public void adjustmentValueChanged(final AdjustmentEvent e) {
    	final JScrollBar scrollBar = (JScrollBar) e.getSource();
        if (!this.scrolling && scrollBar.isEnabled()) {
        	final int value = e.getValue();
            Point viewTranslation = this.canvas.getViewTranslation();
            if (e.getSource() == this.root.getDrawingBoard().getHorizontalScrollBar()) {
                viewTranslation = new Point(-value, viewTranslation.getY());
            }
            if (e.getSource() == this.root.getDrawingBoard().getVerticalScrollBar()) {
                viewTranslation = new Point(viewTranslation.getX(), -value);
            }
            this.canvas.setViewTranslation(viewTranslation);
            this.canvas.repaint();
        }
    }

    @Override
    public void mouseDragged(final int x, final int y) {
    }

    @Override
    public void mouseReleased(final int x, final int y) {
    }

    @Override
    public void mouseMoved(final int x, final int y) {
    }

    @Override
    public void setHoverEffects(final int x, final int y) {
    }

    @Override
    public void setCursor(final int x, final int y) {
        if (this.scrolling) {
            this.canvas.setAlternativeCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }
}
