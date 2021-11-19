package com.exercise.hotelsdatamerge.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @JsonIgnore
    private String category;
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


}
