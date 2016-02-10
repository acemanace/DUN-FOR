package org.codice.imaging.nitf.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ImagePanel extends JPanel {
    private PaintSurface paintSurface;
    private JSlider slider;

    public ImagePanel(BufferedImage bufferedImage, int maxZoom) {
        this.paintSurface = new PaintSurface(bufferedImage);
        this.setLayout(new BorderLayout());

        paintSurface.setPreferredSize(new Dimension(bufferedImage.getWidth(),
                bufferedImage.getHeight()));
        JScrollPane imagePane = new JScrollPane(paintSurface);
        imagePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        imagePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(imagePane, BorderLayout.CENTER);

        slider = new JSlider(1, maxZoom);
        slider.setValue(100);
        slider.setMajorTickSpacing(20);
        slider.setMinorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!slider.getValueIsAdjusting()) {
                    paintSurface.setScale(slider.getValue()/100.0);
                    imagePane.setPreferredSize(paintSurface.getPreferredSize());
                    imagePane.repaint();
                }
            }
        });

        this.add(slider, BorderLayout.SOUTH);
    }

    @Override
    public Dimension getPreferredSize() {
        return ((BorderLayout) this.getLayout()).preferredLayoutSize(this);
    }

    public PaintSurface getPaintSurface() {
        return this.paintSurface;
    }
}
