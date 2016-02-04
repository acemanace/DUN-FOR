package org.codice.imaging.nitf.viewer;

import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@ComponentScan(basePackages = {"org.codice"})
@Component
public class Viewer {
    public static void main(String... args) {
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

        ApplicationContext context =
                new AnnotationConfigApplicationContext(GuiConfiguration.class);
        JFrame topFrame = context.getBean(JFrame.class, "topFrame");
        topFrame.setVisible(true);
    }
}
