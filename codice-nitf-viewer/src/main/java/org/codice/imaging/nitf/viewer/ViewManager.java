package org.codice.imaging.nitf.viewer;

import net.coobird.thumbnailator.Thumbnails;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.core.header.NitfHeader;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.codice.imaging.nitf.fluent.impl.NitfParserInputFlowImpl;
import org.codice.imaging.nitf.render.NitfRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
public class ViewManager {
    private static final int MAX_CHIP_ZOOM = 1200;

    private static Set<ProgressMonitor> runningMonitors = new HashSet<>();

    @Autowired
    private JDesktopPane desktopPane;

    @Autowired
    private JFileChooser fileChooser;

    @Autowired
    private TabPanelFactory tabPanelFactory;

    @Autowired
    private JTextArea logPanel;

    static void startProgressMonitor(ProgressMonitor progressMonitor, int initialProgress,
            int maxIncrement) {
        runningMonitors.add(progressMonitor);
        Random random = new Random();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ThreadLocal<Integer> progress = new ThreadLocal<>();
                progress.set(initialProgress);

                while (!progressMonitor.isCanceled() && runningMonitors.contains(progressMonitor)) {
                    progressMonitor.setProgress(progress.get());
                    Thread.sleep(500);
                    progress.set(progress.get() + random.nextInt(maxIncrement) + 1);
                }

                if (runningMonitors.contains(progressMonitor)) {
                    runningMonitors.remove(progressMonitor);
                }

                progressMonitor.setProgress(progressMonitor.getMaximum());
                return null;
            }
        };

        worker.execute();
    }

    static void haltProgressMonitor(ProgressMonitor progressMonitor) {

        SwingUtilities.invokeLater(() -> {
            if (runningMonitors.contains(progressMonitor)) {
                runningMonitors.remove(progressMonitor);
            }

            progressMonitor.setProgress(progressMonitor.getMaximum());
        });
    }

    public NitfInternalFrame getActiveInternalFrame() {
        NitfInternalFrame internalFrame = (NitfInternalFrame) desktopPane.getSelectedFrame();
        return internalFrame;
    }

    public PaintSurface getActivePaintSurface() {
        PropertiesImageTab propertiesImageTab = getActiveInternalFrame().getSelectedTab();
        return propertiesImageTab.getImagePanel().getPaintSurface();
    }

    public void createChip(String chipName) {
        BufferedImage selectedArea = getSelectedAreaImage();
        NitfInternalFrame nitfInternalFrame = getActiveInternalFrame();
        PropertiesImageTab activeTab = nitfInternalFrame.getSelectedTab();

        NitfHeader nitfFileHeader = nitfInternalFrame.getNitfFileHeader();
        ImageSegment imageSegment = activeTab.getImageSegment();

        NitfInternalFrame chipInternalFrame = prepareNewFrame(nitfFileHeader);
        chipInternalFrame.addPropertiesImageTab(selectedArea, imageSegment);
    }

    private BufferedImage getSelectedAreaImage() {
        PaintSurface activePaintSurface = getActivePaintSurface();
        BufferedImage bi = activePaintSurface.getSelectedAreaImage();
        return bi;
    }

    public void saveCurrentTabImage() {
        fileChooser.setDialogTitle("Save image (.png)");
        FileFilter fileFilter = new FileNameExtensionFilter("PNG File", "png", "PNG");
        fileChooser.setFileFilter(fileFilter);
        int c = fileChooser.showSaveDialog(desktopPane);

        if (c == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            info("Saving file: " + outputFile.getName());

            ProgressMonitor progressMonitor = new ProgressMonitor(desktopPane,
                    "Saving File... ",
                    "",
                    0,
                    100);
            BufferedImage bi = getActivePaintSurface().getBackgroundImage();
            startProgressMonitor(progressMonitor, 1, 25);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws IOException {
                    ImageIO.write(bi, "png", outputFile);
                    haltProgressMonitor(progressMonitor);
                    info("File saved.");
                    return null;
                }
            };

            worker.execute();
        }

        fileChooser.removeChoosableFileFilter(fileFilter);
    }

    public void createThumbnail() {
        fileChooser.setDialogTitle("Create thumbnail (.jpg)");
        FileFilter fileFilter = new FileNameExtensionFilter("JPEG File",
                "jpg",
                "JPG",
                "jpeg",
                "JPEG");
        fileChooser.setFileFilter(fileFilter);
        int c = fileChooser.showSaveDialog(desktopPane);

        if (c == JFileChooser.APPROVE_OPTION) {
            ProgressMonitor progressMonitor = new ProgressMonitor(desktopPane,
                    "Creating thumbnail...",
                    "",
                    0,
                    250);

            BufferedImage bi = getActivePaintSurface().getBackgroundImage();

            SwingWorker<Void, Void> worker = new SwingWorker() {
                protected Object doInBackground() {
                    info("Creating thumbnail: " + fileChooser.getSelectedFile()
                            .getName());
                    startProgressMonitor(progressMonitor, 10, 3);
                    createThumbnail(bi, fileChooser.getSelectedFile());

                    haltProgressMonitor(progressMonitor);
                    info("Thumbnail created.");
                    return null;
                }
            };

            worker.execute();
        }

        fileChooser.removeChoosableFileFilter(fileFilter);
    }

    private void createThumbnail(BufferedImage bi, File thumbnailFile) {
        File tempFile = null;

        try {
            tempFile = File.createTempFile(thumbnailFile.getName(), ".tmp");
            ImageIO.write(bi, "jpg", tempFile);

            Thumbnails.of(tempFile)
                    .size(200, 200)
                    .outputFormat("jpg")
                    .toFile(fileChooser.getSelectedFile());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private void parseAndRender(File nitfRgbFile) {

        ProgressMonitor progressMonitor = new ProgressMonitor(desktopPane,
                "Loading File: " + nitfRgbFile.getName(),
                "",
                0,
                100);

        ThreadLocal<NitfInternalFrame> nitfInternalFrame = new ThreadLocal<NitfInternalFrame>();

        try {
            startProgressMonitor(progressMonitor, 0, 5);
            info("Parsing file: " + nitfRgbFile.getName());
            new NitfParserInputFlowImpl()
                    .file(nitfRgbFile)
                    .allData()
                    .fileHeader(header -> nitfInternalFrame.set(prepareNewFrame(header)))
                    .forEachImageSegment((segment) -> {
                        NitfRenderer renderer = new NitfRenderer();
                        BufferedImage img = null;

                        try {
                            if (segment.getData() != null) {
                                img = renderer.render(segment);
                            }

                            nitfInternalFrame.get().addPropertiesImageTab(img, segment);
                        } catch (IOException | UnsupportedOperationException e) {
                            img = new BufferedImage(10,
                                    10,
                                    BufferedImage.TYPE_INT_ARGB);
                            nitfInternalFrame.get().addPropertiesImageTab(img, segment);
                            error("Couldn't parseAndRender image: " + e.getMessage());
                        }
                    });

            info("File parsed and rendered.");
        } catch (FileNotFoundException e) {
            error("Couldn't parse file: " + e.getMessage());
        } catch (NitfFormatException e) {
            e.printStackTrace();
        } finally {
            haltProgressMonitor(progressMonitor);
        }
    }

    private double getPresumableFreeMemory() {
        long allocatedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                .freeMemory());
        return Runtime.getRuntime().maxMemory() - allocatedMemory;
    }

    private NitfInternalFrame prepareNewFrame(NitfHeader nitfFileHeader) {
        NitfInternalFrame internalFrame = new NitfInternalFrame(nitfFileHeader, tabPanelFactory);
        desktopPane.add(internalFrame);
        internalFrame.setVisible(true);

        try {
            internalFrame.setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        return internalFrame;
    }

    public void openFile() {
        fileChooser.setDialogTitle("Open NITF");
        FileFilter fileFilter = new FileNameExtensionFilter("NITF File",
                "ntf",
                "nitf",
                "nsf",
                "NTF",
                "NITF",
                "NSF");
        fileChooser.setFileFilter(fileFilter);
        int userSelection = fileChooser.showOpenDialog(desktopPane);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File path = fileChooser.getSelectedFile();
            info("Opening file: " + path.getName());

            SwingWorker<Void, Void> worker = new SwingWorker() {
                @Override
                protected Object doInBackground ()throws Exception {
                    try {
                        long startTime = System.currentTimeMillis();

                        parseAndRender(path);
                        long endTime = System.currentTimeMillis();
                        System.gc();
                    } catch (Throwable t) {
                        System.err.println(t.getMessage());
                        t.printStackTrace();
                    }

                    return null;
                }
            };

            worker.execute();
        }

        fileChooser.removeChoosableFileFilter(fileFilter);
    }

    public void info(String message) {
        logPanel.append(String.format("Info: %s\n", message));
    }

    public void warning(String message) {
        logPanel.append(String.format("Warning: %s\n", message));
    }

    public void error(String message) {
        logPanel.append(String.format("Error: %s\n", message));
    }
}
