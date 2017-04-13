package org.codice.imaging.nitf.viewer.nitfbuilder;

import org.codice.imaging.nitf.core.security.FileSecurityMetadata;

class FileSecurityMetadataImpl extends SecurityMetadataImpl implements FileSecurityMetadata {

    private String fileCopyNumber;

    private String fileNumberOfCopies;

    @Override
    public String getFileCopyNumber() {
        return this.fileCopyNumber;
    }

    @Override
    public String getFileNumberOfCopies() {
        return this.fileNumberOfCopies;
    }

    public void setFileCopyNumber(String fileCopyNumber) {
        this.fileCopyNumber = fileCopyNumber;
    }

    public void setFileNumberOfCopies(String fileNumberOfCopies) {
        this.fileNumberOfCopies = fileNumberOfCopies;
    }
}
