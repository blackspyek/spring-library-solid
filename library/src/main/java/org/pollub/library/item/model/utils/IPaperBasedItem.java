package org.pollub.library.item.model.utils;

public interface IPaperBasedItem extends IPhysicalItem {
    Integer getPageCount();
    void setPageCount(Integer pages);

    String getPaperType();
    void setPaperType(String paperType);

    String getPublisher();
    void setPublisher(String publisher);
}