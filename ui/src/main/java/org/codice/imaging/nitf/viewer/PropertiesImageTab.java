package org.codice.imaging.nitf.viewer;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.codice.imaging.nitf.core.NitfFileHeader;
import org.codice.imaging.nitf.core.image.ImageCoordinatePair;
import org.codice.imaging.nitf.core.image.ImageCoordinates;
import org.codice.imaging.nitf.core.image.ImageSegment;
import org.codice.imaging.nitf.core.security.FileSecurityMetadata;

public class PropertiesImageTab extends JSplitPane {

    private ImagePanel imagePanel;

    private ImageSegment imageSegment;

    public PropertiesImageTab(final BufferedImage bufferedImage, final NitfFileHeader fileHeader,
            final ImageSegment imageSegment) {
        super(JSplitPane.HORIZONTAL_SPLIT);

        this.imageSegment = imageSegment;
        this.imagePanel = new ImagePanel(bufferedImage, 400);
        JTable imageProperties = getImagePropertyTable();
        JScrollPane imagePropertiesScrollPane = new JScrollPane(imageProperties);
        JTabbedPane imagePropertiesTab = new JTabbedPane();
        imagePropertiesTab.addTab("Image Properties", imagePropertiesScrollPane);

        JTable fileProperties = getFilePropertyTable(fileHeader);

        JScrollPane filePropertiesScrollPane = new JScrollPane(fileProperties);
        JTabbedPane filePropertiesTab = new JTabbedPane();
        filePropertiesTab.addTab("File Properties", filePropertiesScrollPane);

        JSplitPane propertiesPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        propertiesPane.setDividerLocation(260);
        propertiesPane.setTopComponent(imagePropertiesTab);
        propertiesPane.setBottomComponent(filePropertiesTab);

        this.setLeftComponent(imagePanel);
        this.setRightComponent(propertiesPane);

        SwingUtilities.invokeLater(() -> propertiesPane.setDividerLocation(0.7d));
    }

    private static JTable getFilePropertyTable(NitfFileHeader header) {
        FileSecurityMetadata securityMetadata = header.getFileSecurityMetadata();

        String[][] data = {{"Title:", header.getFileTitle()}, {"ID:", header.getFileTitle()},
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

    private JTable getImagePropertyTable() {
        ImageCoordinates imageCoordinates = imageSegment.getImageCoordinates();

        String[][] data =
                {{"Identifier: ", imageSegment.getIdentifier()}, {"Source: ", imageSegment.getImageSource()},
                        {"Horizontal Pixels/Block",
                                "" + imageSegment.getNumberOfPixelsPerBlockHorizontal()},
                        {"Vertical Pixels/Block", "" + imageSegment.getNumberOfPixelsPerBlockVertical()},
                        {"Block Size:", "" + imageSegment.getNumberOfPixelsPerBlockHorizontal()
                                * imageSegment.getNumberOfPixelsPerBlockVertical()},
                        {"Blocks/Row: ", "" + imageSegment.getNumberOfBlocksPerRow()},
                        {"Blocks/Column: ", "" + imageSegment.getNumberOfBlocksPerColumn()},
                        {"Rows: ", "" + imageSegment.getNumberOfRows()},
                        {"Columns: ", "" + imageSegment.getNumberOfColumns()},
                        {"Image Compression: ", imageSegment.getImageCompression().name()},
                        {"Image Mode: ", "" + imageSegment.getImageMode()},
                        {"Image Representation: ", imageSegment.getImageRepresentation().name()},
                        {"Image Category: ", imageSegment.getImageCategory().name()},
                        {"Number bpp/Band:", "" + imageSegment.getActualBitsPerPixelPerBand()},
                        {"Actual bpp/Band:", "" + imageSegment.getActualBitsPerPixelPerBand()},
                        {"Bands:", "" + imageSegment.getNumBands()},
                        {"Pixel Value Type:", imageSegment.getPixelValueType().name()},
                        {"Pixel Justification:", imageSegment.getPixelJustification().name()},
                        {"Image Coordinates:", ""}, {"    Coord 0,0:", getImageCoordinate(
                        imageCoordinates,
                        () -> imageCoordinates.getCoordinate00())},
                        {"    Coord 0,max:", getImageCoordinate(imageCoordinates,
                                () -> imageCoordinates.getCoordinate0MaxCol())},
                        {"    Coord max,max:", getImageCoordinate(imageCoordinates,
                                () -> imageCoordinates.getCoordinateMaxRowMaxCol())},
                        {"    Coord max,0:", getImageCoordinate(imageCoordinates,
                                () -> imageCoordinates.getCoordinateMaxRow0())},};

        String[] headers = {"Property", "Value"};
        JTable imageProperties = new JTable(data, headers);
        imageProperties.setShowGrid(false);
        return imageProperties;
    }

    private String getImageCoordinate(ImageCoordinates imageCoordinates,
            Supplier<ImageCoordinatePair> supplier) {
        if (imageCoordinates != null) {
            ImageCoordinatePair icp = supplier.get();
            return String.format("%1$.3f, %2$.3f", icp.getLatitude(), icp.getLongitude());
        }

        return "";
    }

    public ImageSegment getImageSegment() {
        return this.imageSegment;
    }

    public ImagePanel getImagePanel() {
        return imagePanel;
    }
}
