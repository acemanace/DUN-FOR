package org.codice.imaging.nitf.viewer;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

class   PaintSurface extends JComponent {
    private Shape shape = null;

    private Point startDrag, endDrag;

    private BufferedImage background;

    private AffineTransform at = AffineTransform.getScaleInstance(1.0, 1.0);

    public PaintSurface(BufferedImage background) {
        this.background = background;

        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startDrag = new Point(e.getX(), e.getY());
                endDrag = startDrag;
                repaint();
            }

            public void mouseReleased(MouseEvent e) {
                Shape r = makeRectangle(startDrag.x, startDrag.y, e.getX(), e.getY());
                shape = r;
                startDrag = null;
                endDrag = null;
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                endDrag = new Point(e.getX(), e.getY());
                repaint();
            }
        });
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.drawRenderedImage(background, at);

        if (shape != null) {
            g2.setStroke(new BasicStroke(2));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.30f));
            g2.setPaint(Color.BLACK);
            g2.draw(shape);
            g2.setPaint(Color.YELLOW);
            g2.fill(shape);
        }

        if (startDrag != null && endDrag != null) {
            g2.setPaint(Color.WHITE);
            Shape r = makeRectangle(startDrag.x, startDrag.y, endDrag.x, endDrag.y);
            g2.draw(r);
        }
    }

    private Rectangle2D.Float makeRectangle(int x1, int y1, int x2, int y2) {
        return new Rectangle2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    public BufferedImage getSelectedAreaImage () {
        Rectangle2D bounds = this.shape.getBounds();

        return background.getSubimage((int) (bounds.getX() * (1 / at.getScaleX())),
                (int) (bounds.getY() * (1 / at.getScaleY())),
                (int) (bounds.getWidth() * (1 / at.getScaleX())),
                (int) (bounds.getHeight() * (1 / at.getScaleY())));
    }

    public BufferedImage getBackgroundImage() {
        return this.background;
    }

    public void setScale(double scale) {
        this.at = AffineTransform.getScaleInstance(scale, scale);
        getParent().setPreferredSize(new Dimension((int) (background.getWidth() * scale),
                (int) (background.getHeight() * scale)));
        this.repaint();
    }

    public Dimension getPreferredSize() {
        int w = (int) (at.getScaleX() * background.getWidth());
        int h = (int) (at.getScaleY() * background.getHeight());
        return new Dimension(w, h);
    }
}

