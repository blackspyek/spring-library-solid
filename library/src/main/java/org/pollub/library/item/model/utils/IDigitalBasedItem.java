package org.pollub.library.item.model.utils;

public interface IDigitalBasedItem extends IPhysicalItem {
    String getFileFormat();
    void setFileFormat(String format);

    String getDigitalRights();
    void setDigitalRights(String rights);

}
