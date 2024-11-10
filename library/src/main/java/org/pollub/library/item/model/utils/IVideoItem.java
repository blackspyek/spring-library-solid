package org.pollub.library.item.model.utils;

public interface IVideoItem extends IDigitalBasedItem, IHasDuration {
    String getResolution();
    void setResolution(String resolution);
}
