package com.gsclimbing.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Data;

@Data
public class FilterProjectDTO {
	
	private String country;
	private String location;
	private String name;
	private String number;
	private String site;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

