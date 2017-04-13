package org.codice.imaging.simple.viewer;

import java.awt.*;

import javax.imageio.spi.IIORegistry;
import javax.swing.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.jaiimageio.jpeg2000.impl.J2KImageReaderSpi;

public class Viewer {
    public static void main(String... args) throws InterruptedException {
        UIManager.getDefaults()
                .put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.getDefaults()
                .put("TabbedPane.tabsOverlapBorder", true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);

        ApplicationContext context = new AnnotationConfigApplicationContext(ViewConfiguration.class);
        JFrame topFrame = context.getBean(JFrame.class, "topFrame");

        topFrame.setVisible(true);
        ViewManager ViewManager = context.getBean(ViewManager.class, "ViewManager");
        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize()
                .getWidth());
        int ySize = ((int) tk.getScreenSize()
                .getHeight());
        topFrame.setSize(xSize, ySize);

        IIORegistry.getDefaultInstance()
                .registerServiceProvider(new J2KImageReaderSpi());
        ViewManager.openFile();
    }
}
