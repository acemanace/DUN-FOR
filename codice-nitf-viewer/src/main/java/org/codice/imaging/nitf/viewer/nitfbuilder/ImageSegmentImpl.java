package org.codice.imaging.nitf.viewer.nitfbuilder;

import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.common.FileType;
import org.codice.imaging.nitf.core.common.NitfFormatException;
import org.codice.imaging.nitf.core.common.impl.DateTimeImpl;
import org.codice.imaging.nitf.core.common.impl.TaggedRecordExtensionHandlerImpl;
import org.codice.imaging.nitf.core.image.*;
import org.codice.imaging.nitf.core.security.SecurityMetadata;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ImageSegmentImpl extends TaggedRecordExtensionHandlerImpl implements ImageSegment {
    private static final int BITS_PER_BYTE = 8;
    private DateTime imageDateTime;
    private TargetId imageTargetId;
    private String imageIdentifier2;
    private String imageSource;
    private long numberOfRows;
    private long numberOfColumns;
    private PixelValueType pixelValueType;
    private ImageRepresentation imageRepresentation;
    private ImageCategory imageCategory;
    private int actualBitsPerPixelPerBand;
    private PixelJustification pixelJustification;
    private ImageCoordinatesRepresentation imageCoordinatesRepresentation;
    private List<String> imageComments;
    private ImageCompression imageCompression;
    private String compressionRate;
    private List<ImageBand> imageBands;
    private ImageMode imageMode;
    private int numberOfBlocksPerRow;
    private int numberOfBlocksPercolumn;
    private int numberOfPixelsPerBlockHorizontal;
    private int numberOfPixelsPerBlockVertical;
    private int numberOfBitsPerPixelPerBand;
    private int imageDisplayLevel;
    private int imageLocationRow;
    private int imageLocationColumn;
    private String imageMagnification;
    private ImageCoordinates imageCoordinates;
    private int userDefinedHeaderOverflow;
    private ImageInputStream imageData;
    private long imageDataLength;
    private int extendedHeaderDataOverflow;
    private int attachmentLevel;
    private String identifier;
    private SecurityMetadata securityMetadata;
    private FileType fileType;

    ImageSegmentImpl() {
        imageDateTime = DateTimeImpl.getNitfDateTimeForNow();
        imageTargetId = null;
        imageIdentifier2 = null;
        imageSource = "";
        numberOfRows = 0L;
        numberOfColumns = 0L;
        pixelValueType = PixelValueType.UNKNOWN;
        imageRepresentation = ImageRepresentation.UNKNOWN;
        imageCategory = ImageCategory.UNKNOWN;
        actualBitsPerPixelPerBand = 0;
        pixelJustification = PixelJustification.UNKNOWN;
        imageCoordinatesRepresentation = ImageCoordinatesRepresentation.UNKNOWN;
        imageCoordinates = null;
        imageComments = new ArrayList<>();
        imageCompression = ImageCompression.UNKNOWN;
        compressionRate = null;
        imageBands = new ArrayList<>();
        imageMode = ImageMode.UNKNOWN;
        numberOfBlocksPerRow = 0;
        numberOfBlocksPercolumn = 0;
        numberOfPixelsPerBlockHorizontal = 0;
        numberOfPixelsPerBlockVertical = 0;
        numberOfBitsPerPixelPerBand = 0;
        imageDisplayLevel = 0;
        imageLocationRow = 0;
        imageLocationColumn = 0;
        userDefinedHeaderOverflow = 0;
        imageMagnification = "1.0 ";
        imageData = null;
        imageDataLength = 0;
    }

    @Override
    public DateTime getImageDateTime() {
        return this.imageDateTime;
    }

    @Override
    public TargetId getImageTargetId() {
        return this.imageTargetId;
    }

    @Override
    public String getImageIdentifier2() {
        return this.imageIdentifier2;
    }

    @Override
    public String getImageSource() {
        return this.imageSource;
    }

    @Override
    public long getNumberOfRows() {
        return this.numberOfRows;
    }

    @Override
    public long getNumberOfColumns() {
        return this.numberOfColumns;
    }

    @Override
    public PixelValueType getPixelValueType() {
        return this.pixelValueType;
    }

    @Override
    public ImageRepresentation getImageRepresentation() {
        return this.imageRepresentation;
    }

    @Override
    public ImageCategory getImageCategory() {
        return this.imageCategory;
    }

    @Override
    public void setImageCategory(ImageCategory imageCategory) {
        this.imageCategory = imageCategory;
    }

    @Override
    public void setActualBitsPerPixelPerBand(int i) {
        this.actualBitsPerPixelPerBand = i;
    }

    @Override
    public int getActualBitsPerPixelPerBand() {
        return this.actualBitsPerPixelPerBand;
    }

    @Override
    public void setPixelJustification(PixelJustification pixelJustification) {
        this.pixelJustification = pixelJustification;
    }

    @Override
    public PixelJustification getPixelJustification() {
        return this.pixelJustification;
    }

    @Override
    public void setImageCoordinatesRepresentation(ImageCoordinatesRepresentation imageCoordinatesRepresentation) {
        this.imageCoordinatesRepresentation = imageCoordinatesRepresentation;
    }

    @Override
    public ImageCoordinatesRepresentation getImageCoordinatesRepresentation() {
        return this.imageCoordinatesRepresentation;
    }

    @Override
    public void addImageComment(String s) {
        imageComments.add(s);
    }

    @Override
    public List<String> getImageComments() {
        return this.imageComments;
    }

    @Override
    public void setImageCompression(ImageCompression imageCompression) {
        this.imageCompression = imageCompression;
    }

    @Override
    public ImageCompression getImageCompression() {
        return this.imageCompression;
    }

    @Override
    public void setCompressionRate(String s) {
        this.compressionRate = s;
    }

    @Override
    public String getCompressionRate() {
        return this.compressionRate;
    }

    @Override
    public void addImageBand(ImageBand imageBand) {
        this.imageBands.add(imageBand);
    }

    @Override
    public int getNumBands() {
        return this.imageBands.size();
    }

    @Override
    public ImageBand getImageBand(int i) {
        return this.imageBands.get(i - 1);
    }

    @Override
    public ImageBand getImageBandZeroBase(int i) {
        return this.imageBands.get(i);
    }

    @Override
    public void setImageMode(ImageMode imageMode) {
        this.imageMode = imageMode;
    }

    @Override
    public ImageMode getImageMode() {
        return this.imageMode;
    }

    @Override
    public void setNumberOfBlocksPerRow(int i) {
        this.numberOfBlocksPerRow = i;
    }

    @Override
    public int getNumberOfBlocksPerRow() {
        return this.numberOfBlocksPerRow;
    }

    @Override
    public void setNumberOfBlocksPerColumn(int i) {
        this.numberOfBlocksPercolumn = i;
    }

    @Override
    public int getNumberOfBlocksPerColumn() {
        return this.numberOfBlocksPercolumn;
    }

    @Override
    public void setNumberOfPixelsPerBlockHorizontalRaw(int i) {
        this.numberOfPixelsPerBlockHorizontal = i;
    }

    @Override
    public int getNumberOfPixelsPerBlockHorizontalRaw() {
        return this.numberOfPixelsPerBlockHorizontal;
    }

    @Override
    public long getNumberOfPixelsPerBlockHorizontal() {
        return (long) this.numberOfPixelsPerBlockHorizontal;
    }

    @Override
    public void setNumberOfPixelsPerBlockVerticalRaw(int i) {
        this.numberOfPixelsPerBlockVertical = i;
    }

    @Override
    public int getNumberOfPixelsPerBlockVerticalRaw() {
        return this.numberOfPixelsPerBlockVertical;
    }

    @Override
    public long getNumberOfPixelsPerBlockVertical() {
        return (long) this.numberOfPixelsPerBlockVertical;
    }

    @Override
    public void setNumberOfBitsPerPixelPerBand(int i) {
        this.numberOfBitsPerPixelPerBand = i;
    }

    @Override
    public int getNumberOfBitsPerPixelPerBand() {
        return this.numberOfBitsPerPixelPerBand;
    }

    @Override
    public void setImageDisplayLevel(int i) {
        this.imageDisplayLevel = i;
    }

    @Override
    public int getImageDisplayLevel() {
        return this.imageDisplayLevel;
    }

    @Override
    public void setImageLocationRow(int i) {
        this.imageLocationRow = i;
    }

    @Override
    public int getImageLocationRow() {
        return this.imageLocationRow;
    }

    @Override
    public void setImageLocationColumn(int i) {
        this.imageLocationColumn = i;
    }

    @Override
    public int getImageLocationColumn() {
        return this.imageLocationColumn;
    }

    @Override
    public void setImageDateTime(DateTime dateTime) {
        this.imageDateTime = dateTime;
    }

    @Override
    public void setImageTargetId(TargetId targetId) {
        this.imageTargetId = targetId;
    }

    @Override
    public void setImageIdentifier2(String s) {
        this.imageIdentifier2 = s;
    }

    @Override
    public void setImageSource(String s) {
        this.imageSource = s;
    }

    @Override
    public void setNumberOfColumns(long l) {
        this.numberOfColumns = l;
    }

    @Override
    public void setNumberOfRows(long l) {
        this.numberOfRows = l;
    }

    @Override
    public void setImageMagnification(String s) {
        this.imageMagnification = s;
    }

    @Override
    public void setPixelValueType(PixelValueType pixelValueType) {
        this.pixelValueType = pixelValueType;
    }

    @Override
    public void setImageRepresentation(ImageRepresentation imageRepresentation) {
        this.imageRepresentation = imageRepresentation;
    }

    @Override
    public String getImageMagnification() {
        return this.imageMagnification;
    }

    @Override
    public double getImageMagnificationAsDouble() {
        return Double.parseDouble(this.imageMagnification);
    }

    @Override
    public void setImageCoordinates(ImageCoordinates imageCoordinates) {
        this.imageCoordinates = imageCoordinates;
    }

    @Override
    public ImageCoordinates getImageCoordinates() {
        return this.imageCoordinates;
    }

    @Override
    public int getUserDefinedHeaderOverflow() {
        return this.userDefinedHeaderOverflow;
    }

    @Override
    public void setUserDefinedHeaderOverflow(int i) {
        this.userDefinedHeaderOverflow = i;
    }

    @Override
    public final long getNumberOfBytesPerBlock() {
        long numberOfPixelsPerBlockPerBand = getNumberOfPixelsPerBlockHorizontal() * getNumberOfPixelsPerBlockVertical();
        long numberOfPixelsPerBlock = numberOfPixelsPerBlockPerBand * getNumBands();
        long numberOfBytesPerBlock = numberOfPixelsPerBlock * getNumberOfBitsPerPixelPerBand() / BITS_PER_BYTE;
        return numberOfBytesPerBlock;
    }

    @Override
    public ImageInputStream getData() {
        return this.imageData;
    }

    @Override
    public void setData(ImageInputStream imageInputStream) {
        this.imageData = imageInputStream;
    }

    @Override
    public long getDataLength() {
        return this.imageDataLength;
    }

    @Override
    public void setDataLength(long l) {
        this.imageDataLength = l;
    }

    @Override
    public int getExtendedHeaderDataOverflow() {
        return this.extendedHeaderDataOverflow;
    }

    @Override
    public void setExtendedHeaderDataOverflow(int i) {
        this.extendedHeaderDataOverflow = i;
    }

    @Override
    public int getAttachmentLevel() {
        return this.attachmentLevel;
    }

    @Override
    public void setAttachmentLevel(int i) {
        this.attachmentLevel = i;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public void setIdentifier(String s) {
        this.identifier = s;
    }

    @Override
    public SecurityMetadata getSecurityMetadata() {
        return this.securityMetadata;
    }

    @Override
    public void setSecurityMetadata(SecurityMetadata securityMetadata) {
        this.securityMetadata = securityMetadata;
    }

    @Override
    public long getHeaderLength() throws NitfFormatException, IOException {
        return 0;
    }

    @Override
    public FileType getFileType() {
        return this.fileType;
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
