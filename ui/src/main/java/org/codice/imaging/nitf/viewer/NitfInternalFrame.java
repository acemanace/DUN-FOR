package org.codice.imaging.nitf.viewer;

import org.codice.imaging.nitf.core.header.NitfHeader;
import org.codice.imaging.nitf.core.image.ImageSegment;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NitfInternalFrame extends JInternalFrame {
    private TabPanelFactory tabPanelFactory;

    private NitfHeader fileHeader;

    private JTabbedPane jTabbedPane;

    public NitfInternalFrame(NitfHeader fileHeader, TabPanelFactory tabPanelFactory) {
        super(fileHeader.getFileTitle(), true, true, true, true);
        this.fileHeader = fileHeader;
        this.tabPanelFactory = tabPanelFactory;
        this.jTabbedPane = new JTabbedPane();
        this.getContentPane()
                .add(jTabbedPane, BorderLayout.CENTER);
    }

    public void addPropertiesImageTab(BufferedImage bufferedImage, ImageSegment imageSegment) {
        String tabName = imageSegment.getIdentifier();

        PropertiesImageTab propertiesImageTab = tabPanelFactory.createNitfImagePanel(bufferedImage,
                fileHeader,
                imageSegment);

        JPanel tab = tabPanelFactory.createTab(tabName);
        jTabbedPane.addTab(tabName, propertiesImageTab);
        jTabbedPane.setSelectedComponent(propertiesImageTab);
        jTabbedPane.setTabComponentAt(jTabbedPane.getSelectedIndex(), tab);

        SwingUtilities.invokeLater(() -> propertiesImageTab.setDividerLocation(0.85d));
    }

    public NitfHeader getNitfFileHeader() {
        return this.fileHeader;
    }

    public PropertiesImageTab getSelectedTab() {
        return (PropertiesImageTab) jTabbedPane.getSelectedComponent();
    }

    public JTabbedPane getjTabbedPane() {
        return this.jTabbedPane;
    }
}
