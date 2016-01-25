package org.codice.imaging.nitf.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import org.codice.imaging.nitf.core.NitfFileHeader;
import org.codice.imaging.nitf.core.image.NitfImageSegmentHeader;
import org.codice.imaging.nitf.core.security.FileSecurityMetadata;

class NitfGuiComponentFactory {
    final static String TITLE = "Don's Ultimate NITF-File Optical Renderer";

    static final String COPYRIGHT = "(c) Codice Foundation 2016";

    private static Set<ProgressMonitor> runningMonitors = new HashSet<>();

    static JSplitPane getjSplitPane(final BufferedImage bufferedImage,
            final NitfFileHeader fileHeader,
            final NitfImageSegmentHeader header) {
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bufferedImage, 0, 0, null);
            }
        };

        imagePanel.setPreferredSize(new Dimension(bufferedImage.getWidth(),
                bufferedImage.getHeight()));
        JScrollPane imagePane = new JScrollPane(imagePanel);

        JTable imageProperties = NitfGuiComponentFactory.getImagePropertyTable(header);
        JScrollPane imagePropertiesScrollPane = new JScrollPane(imageProperties);
        JTabbedPane imagePropertiesTab = new JTabbedPane();
        imagePropertiesTab.addTab("Image Properties", imagePropertiesScrollPane);

        JTable fileProperties = NitfGuiComponentFactory.getFilePropertyTable(fileHeader);

        JScrollPane filePropertiesScrollPane = new JScrollPane(fileProperties);
        JTabbedPane filePropertiesTab = new JTabbedPane();
        filePropertiesTab.addTab("File Properties", filePropertiesScrollPane);

        JSplitPane propertiesPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        propertiesPane.setDividerLocation(260);
        propertiesPane.setTopComponent(imagePropertiesTab);
        propertiesPane.setBottomComponent(filePropertiesTab);

        JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        outer.setLeftComponent(imagePane);
        outer.setRightComponent(propertiesPane);
        outer.setDividerLocation(750);
        return outer;
    }

    private static JTable getFilePropertyTable(NitfFileHeader header) {
        FileSecurityMetadata securityMetadata = header.getFileSecurityMetadata();

        String[][] data = {{"Title:", header.getFileTitle()}, {"ID:", header.getIdentifier()},
                {"Classification:", securityMetadata.getSecurityClassification().name()},
                {"Origin ID:", header.getOriginatingStationId()},
                {"Origin Phone:", header.getOriginatorsPhoneNumber()},
                {"Standard Type:", header.getStandardType()},
                {"Complexity Level:", "" + header.getComplexityLevel()},
                {"Date:", header.getFileDateTime().getSourceString()}, {"Image Count:",
                "" + header.getImageSegmentSubHeaderLengths()
                        .size()},
                {"Class Authority:", securityMetadata.getClassificationAuthority()},
                {"Class Auth Type:", securityMetadata.getClassificationAuthorityType()},
                {"Class Text:", securityMetadata.getClassificationText()},
                {"Class Reason:", securityMetadata.getClassificationReason()}};

        String[] headers = {"Property", "Value"};
        JTable fileProperties = new JTable(data, headers);
        fileProperties.setShowGrid(false);
        return fileProperties;
    }

    private static JTable getImagePropertyTable(NitfImageSegmentHeader header) {
        String[][] data =
                {{"Identifier: ", header.getIdentifier()}, {"Source: ", header.getImageSource()},
                        {"Blocks/Row: ", "" + header.getNumberOfBlocksPerRow()},
                        {"Blocks/Column: ", "" + header.getNumberOfBlocksPerColumn()},
                        {"Rows: ", "" + header.getNumberOfRows()},
                        {"Columns: ", "" + header.getNumberOfColumns()},
                        {"Image Mode: ", "" + header.getImageMode()},
                        {"Image Representation: ", "" + header.getImageRepresentation()},
                        {"Image Category: ", "" + header.getImageCategory()},
                        {"bpp/Band:", "" + header.getActualBitsPerPixelPerBand()},
                        {"Bands:", "" + header.getNumBands()},
                        {"Pixel Value Type:", header.getPixelValueType().name()},
                        {"Pixel Justification:", header.getPixelJustification().name()}};

        String[] headers = {"Property", "Value"};
        JTable imageProperties = new JTable(data, headers);
        imageProperties.setShowGrid(false);
        return imageProperties;
    }

    static JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));

        JLabel startupLabel1 = new JLabel();
        startupLabel1.setText(TITLE + " (DUN-FOR)");
        startupLabel1.setHorizontalAlignment(JLabel.CENTER);
        startupLabel1.setVerticalAlignment(JLabel.BOTTOM);
        Font defaultLabelFont = startupLabel1.getFont();
        int fontSize = defaultLabelFont.getSize();
        startupLabel1.setFont(new Font(defaultLabelFont.getName(), Font.PLAIN, fontSize));
        titlePanel.add(startupLabel1);

        JLabel startupLabel2 = new JLabel();
        startupLabel2.setText(COPYRIGHT);
        startupLabel2.setHorizontalAlignment(JLabel.CENTER);
        startupLabel2.setVerticalAlignment(JLabel.TOP);
        startupLabel2.setFont(new Font(defaultLabelFont.getName(), Font.PLAIN, fontSize - 2));
        startupLabel2.setForeground(Color.BLUE);
        titlePanel.add(startupLabel2);

        return titlePanel;
    }

    static BufferedImage getDisplayedImage(JTabbedPane mainPanel) {
        JSplitPane currentTab = (JSplitPane) mainPanel.getSelectedComponent();
        JScrollPane scrollPane = (JScrollPane) currentTab.getLeftComponent();
        JViewport viewport = (JViewport) scrollPane.getComponent(0);
        JComponent imagePanel = (JComponent) viewport.getComponent(0);
        BufferedImage bi = new BufferedImage(imagePanel.getWidth(),
                imagePanel.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        imagePanel.paint(bi.getGraphics());
        return bi;
    }

    static void startProgressMonitor(ProgressMonitor progressMonitor, int initialProgress, int maxIncrement) {
        runningMonitors.add(progressMonitor);
        Random random = new Random();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ThreadLocal<Integer> progress = new ThreadLocal<>();
                progress.set(initialProgress);

                while (!progressMonitor.isCanceled() &&
                        runningMonitors.contains(progressMonitor)) {
                    progressMonitor.setProgress(progress.get());
                    Thread.sleep(500);
                    progress.set(progress.get() + random.nextInt(maxIncrement) + 1);
                }

                if (runningMonitors.contains(progressMonitor)) {
                    runningMonitors.remove(progressMonitor);
                }

                return null;
            }
        };

        worker.execute();
    }

    static void haltProgressMonitor(ProgressMonitor progressMonitor) {
        if (runningMonitors.contains(progressMonitor)) {
            runningMonitors.remove(progressMonitor);
        }

        progressMonitor.setProgress(progressMonitor.getMaximum());
    }

    static void createTab(JTabbedPane mainPanel, String tabName, JComponent parent) {
        mainPanel.addTab(tabName, parent);
        mainPanel.setSelectedComponent(parent);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.add(new JLabel(tabName));

        URL iconUrl = ClassLoader.getSystemClassLoader().getResource(
                "images/Close_Tab.png");
        URL pressedIconUrl = ClassLoader.getSystemClassLoader().getResource(
                "images/Close_Tab-pressed.png");

        ImageIcon icon = new ImageIcon(iconUrl, "close this tab");
        ImageIcon pressedIcon = new ImageIcon(pressedIconUrl);
        JLabel closeLabel = new JLabel(icon);

        closeLabel.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                mainPanel.remove(mainPanel.indexOfComponent(parent));
                System.gc();
            }

            public void mousePressed(MouseEvent e) {
                closeLabel.setIcon(pressedIcon);
            }

            public void mouseReleased(MouseEvent e) {
                closeLabel.setIcon(icon);
            }
        });

        panel.add(closeLabel);
        mainPanel.setTabComponentAt(mainPanel.getSelectedIndex(), panel);
    }
}
