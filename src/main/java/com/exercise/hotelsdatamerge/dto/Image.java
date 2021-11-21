package com.exercise.hotelsdatamerge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image implements Comparable<Image> {
    private String link;
    private String description;

    @JsonSetter
    public void setCaption(String caption) {
        this.description = caption;
    }

    @JsonSetter
    public void setUrl(String url) {
        this.link = url;
    }


    @Override
    public int compareTo(Image o) {
        return StringUtils.compare(this.getDescription(), o.getDescription());
    }
}
