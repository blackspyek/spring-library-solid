package org.pollub.library.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.pollub.library.item.model.utils.IHasGenre;
import org.pollub.library.item.model.utils.IVideoItem;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class MovieDisc extends LibraryItem implements IVideoItem, IHasGenre {
    @Column(nullable = false)
    private String director;
    @Column(nullable = false)
    private String resolution;
    @Column(nullable = false)
    private String fileFormat;
    @Column(nullable = false)
    private String digitalRights;
    @Column(nullable = false)
    private Integer duration;
    @Column(nullable = false)
    private String genre;
    @Column(nullable = false)
    private Integer shelfNumber;

    @Override
    public String getResolution() {
        return resolution;
    }

    @Override
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public String getFileFormat() {
        return fileFormat;
    }

    @Override
    public void setFileFormat(String format) {
        this.fileFormat = format;
    }

    @Override
    public String getDigitalRights() {
        return digitalRights;
    }

    @Override
    public void setDigitalRights(String rights) {
        this.digitalRights = rights;
    }

    @Override
    public Integer getDuration() {
        return duration;
    }

    @Override
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public Integer getShelfNumber() {
        return shelfNumber;
    }

    @Override
    public void setShelfNumber(Integer shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    @Override
    public LocalDateTime calculateDueTime() {
        return getRentedAt().plusDays(7);
    }
}
