package com.example.calendly.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.*;
import java.util.List;


@Getter
@Setter
@Builder
@ToString
public class EventDto {

    @NotBlank(message = "required")
    @Size(min = 5, max = 250, message = "min 5 and max 250 character limit title")
    private String title;

    @NotBlank(message = "required")
    @Size(min = 5, max = 250, message = "min 5 and max 250 character limit description")
    private String description;

    @NotNull(message = "from date field can't be null")
    private Long fromDate;

    @NotNull(message = "to date field can't be null")
    private Long toDate;

    private String url;

    @NotNull(message = "interval can't be null")
    @Max(value = 1440, message = "interval max limit exceeded")
    @Min(value = 10, message = "minimum interval is 10 min")
    private Integer interval;

    private List<CustomDateTime> customDateTime;
    private boolean live;
    private boolean isActive;
}
