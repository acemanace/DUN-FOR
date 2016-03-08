package org.codice.imaging.nitf.viewer;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.codice.imaging.nitf.core.NitfFileHeader;
import org.codice.imaging.nitf.core.image.ImageSegment;

public class NitfInternalFrame extends JInternalFrame {
    private TabPanelFactory tabPanelFactory;

    private NitfFileHeader fileHeader;

    private JTabbedPane jTabbedPane;

    public NitfInternalFrame(NitfFileHeader fileHeader, TabPanelFactory tabPanelFactory) {
        super(fileHeader.getFileTitle(), true, true, true, true);
        this.fileHeader = fileHeader;
        this.tabPanelFactory = tabPanelFactory;
        this.jTabbedPane = new JTabbedPane();
        this.getContentPane()
                .add(jTabbedPane, BorderLayout.CENTER);
    }

    public void addPropertiesImageTab(BufferedImage bufferedImage, ImageSegment imageSegment) {
        String tabName = imageSegment.getIdentifier();
        tabName = tabName.substring(0, tabName.length() - 4);

        PropertiesImageTab propertiesImageTab = tabPanelFactory.createNitfImagePanel(bufferedImage,
                fileHeader,
                imageSegment);

        JPanel tab = tabPanelFactory.createTab(tabName);
        jTabbedPane.addTab(tabName, propertiesImageTab);
        jTabbedPane.setSelectedComponent(propertiesImageTab);
        jTabbedPane.setTabComponentAt(jTabbedPane.getSelectedIndex(), tab);

        SwingUtilities.invokeLater(() -> propertiesImageTab.setDividerLocation(0.85d));
    }

    public NitfFileHeader getNitfFileHeader() {
        return this.fileHeader;
    }

    public PropertiesImageTab getSelectedTab() {
        return (PropertiesImageTab) jTabbedPane.getSelectedComponent();
    }

    public JTabbedPane getjTabbedPane() {
        return this.jTabbedPane;
    }
}
