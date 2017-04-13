package org.codice.imaging.nitf.viewer.nitfbuilder;

import org.codice.imaging.nitf.core.common.FileType;
import org.codice.imaging.nitf.core.security.SecurityClassification;
import org.codice.imaging.nitf.core.security.SecurityMetadata;

class SecurityMetadataImpl implements SecurityMetadata {
    private static final int XSCLAS_LENGTH = 1;
    private static final int XSCLSY_LENGTH = 2;
    private static final int XSCODE_LENGTH = 11;
    private static final int XSCTLH_LENGTH = 2;
    private static final int XSREL_LENGTH = 20;
    private static final int XSDCTP_LENGTH = 2;
    private static final int XSDCDT_LENGTH = 8;
    private static final int XSDCXM_LENGTH = 4;
    private static final int XSDG_LENGTH = 1;
    private static final int XSDGDT_LENGTH = 8;
    private static final int XSCLTX_LENGTH = 43;
    private static final int XSCATP_LENGTH = 1;
    private static final int XSCAUT_LENGTH = 40;
    private static final int XSCRSN_LENGTH = 1;
    private static final int XSSRDT_LENGTH = 8;
    private static final int XSCTLN_LENGTH = 15;
    private static final int XSDEVT20_LENGTH = 40;

    private FileType nitfFileType = FileType.NITF_TWO_ONE;
    private SecurityClassification securityClassification = SecurityClassification.UNKNOWN;
    private String nitfSecurityClassificationSystem = null;
    private String nitfCodewords = null;
    private String nitfControlAndHandling = null;
    private String nitfReleaseInstructions = null;
    private String nitfDeclassificationType = null;
    private String nitfDeclassificationDate = null;
    private String nitfDeclassificationExemption = null;
    private String nitfDowngrade = null;
    private String nitfDowngradeDate = null;
    private String nitfClassificationText = null;
    private String nitfClassificationAuthorityType = null;
    private String nitfClassificationAuthority = null;
    private String nitfClassificationReason = null;
    private String nitfSecuritySourceDate = null;
    private String nitfSecurityControlNumber = null;
    private String downgradeDateOrSpecialCase = null;
    private String downgradeEvent = null;

    @Override
    public FileType getFileType() {
        return this.nitfFileType;
    }

    @Override
    public SecurityClassification getSecurityClassification() {
        return this.securityClassification;
    }

    @Override
    public String getSecurityClassificationSystem() {
        return this.nitfSecurityClassificationSystem;
    }

    @Override
    public String getCodewords() {
        return this.nitfCodewords;
    }

    @Override
    public String getControlAndHandling() {
        return this.nitfControlAndHandling;
    }

    @Override
    public String getReleaseInstructions() {
        return this.nitfReleaseInstructions;
    }

    @Override
    public String getDeclassificationType() {
        return this.nitfDeclassificationType;
    }

    @Override
    public String getDeclassificationDate() {
        return this.nitfDeclassificationDate;
    }

    @Override
    public String getDeclassificationExemption() {
        return this.nitfDeclassificationExemption;
    }

    @Override
    public String getDowngrade() {
        return this.nitfDowngrade;
    }

    @Override
    public String getDowngradeDate() {
        return this.nitfDowngradeDate;
    }

    @Override
    public String getDowngradeDateOrSpecialCase() {
        return this.downgradeDateOrSpecialCase;
    }

    @Override
    public String getDowngradeEvent() {
        return this.downgradeEvent;
    }

    @Override
    public String getClassificationText() {
        return this.nitfClassificationText;
    }

    @Override
    public String getClassificationAuthorityType() {
        return this.nitfClassificationAuthorityType;
    }

    @Override
    public String getClassificationAuthority() {
        return this.nitfClassificationAuthority;
    }

    @Override
    public String getClassificationReason() {
        return this.nitfClassificationReason;
    }

    @Override
    public String getSecuritySourceDate() {
        return this.nitfSecuritySourceDate;
    }

    @Override
    public String getSecurityControlNumber() {
        return this.nitfSecurityControlNumber;
    }

    @Override
    public boolean hasDowngradeMagicValue() {
        return this.hasDowngradeMagicValue();
    }

    @Override
    public long getSerialisedLength() {
        long len = XSCLAS_LENGTH + XSCLSY_LENGTH + XSCODE_LENGTH
                + XSCTLH_LENGTH + XSREL_LENGTH + XSDCTP_LENGTH
                + XSDCDT_LENGTH + XSDCXM_LENGTH + XSDG_LENGTH
                + XSDGDT_LENGTH + XSCLTX_LENGTH + XSCATP_LENGTH
                + XSCAUT_LENGTH + XSCRSN_LENGTH + XSSRDT_LENGTH
                + XSCTLN_LENGTH;

        if ((getFileType().equals(FileType.NITF_TWO_ZERO)) && hasDowngradeMagicValue()) {
            len += XSDEVT20_LENGTH;
        }

        return len;
    }

    public void setNitfFileType(FileType nitfFileType) {
        this.nitfFileType = nitfFileType;
    }

    public void setSecurityClassification(SecurityClassification securityClassification) {
        this.securityClassification = securityClassification;
    }

    public void setNitfSecurityClassificationSystem(String nitfSecurityClassificationSystem) {
        this.nitfSecurityClassificationSystem = nitfSecurityClassificationSystem;
    }

    public void setNitfCodewords(String nitfCodewords) {
        this.nitfCodewords = nitfCodewords;
    }

    public void setNitfControlAndHandling(String nitfControlAndHandling) {
        this.nitfControlAndHandling = nitfControlAndHandling;
    }

    public void setNitfReleaseInstructions(String nitfReleaseInstructions) {
        this.nitfReleaseInstructions = nitfReleaseInstructions;
    }

    public void setNitfDeclassificationType(String nitfDeclassificationType) {
        this.nitfDeclassificationType = nitfDeclassificationType;
    }

    public void setNitfDeclassificationDate(String nitfDeclassificationDate) {
        this.nitfDeclassificationDate = nitfDeclassificationDate;
    }

    public void setNitfDeclassificationExemption(String nitfDeclassificationExemption) {
        this.nitfDeclassificationExemption = nitfDeclassificationExemption;
    }

    public void setNitfDowngrade(String nitfDowngrade) {
        this.nitfDowngrade = nitfDowngrade;
    }

    public void setNitfDowngradeDate(String nitfDowngradeDate) {
        this.nitfDowngradeDate = nitfDowngradeDate;
    }

    public void setNitfClassificationText(String nitfClassificationText) {
        this.nitfClassificationText = nitfClassificationText;
    }

    public void setNitfClassificationAuthorityType(String nitfClassificationAuthorityType) {
        this.nitfClassificationAuthorityType = nitfClassificationAuthorityType;
    }

    public void setNitfClassificationAuthority(String nitfClassificationAuthority) {
        this.nitfClassificationAuthority = nitfClassificationAuthority;
    }

    public void setNitfClassificationReason(String nitfClassificationReason) {
        this.nitfClassificationReason = nitfClassificationReason;
    }

    public void setNitfSecuritySourceDate(String nitfSecuritySourceDate) {
        this.nitfSecuritySourceDate = nitfSecuritySourceDate;
    }

    public void setNitfSecurityControlNumber(String nitfSecurityControlNumber) {
        this.nitfSecurityControlNumber = nitfSecurityControlNumber;
    }

    public void setDowngradeDateOrSpecialCase(String downgradeDateOrSpecialCase) {
        this.downgradeDateOrSpecialCase = downgradeDateOrSpecialCase;
    }

    public void setDowngradeEvent(String downgradeEvent) {
        this.downgradeEvent = downgradeEvent;
    }
}
