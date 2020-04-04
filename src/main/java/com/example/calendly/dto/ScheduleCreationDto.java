package com.example.calendly.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@ToString
public class ScheduleCreationDto {

    @NotBlank(message = "required")
    @Size(min = 3, max = 250, message = "min 3 and max 250 character limit name")
    private String name;

    @NotBlank(message = "required")
    @Size(min = 5, max = 250, message = "min 5 and max 250 character limit email")
    @Email(message = "email should be a valid")
    private String email;

    @NotNull(message = "date field can't be null")
    private Long date;

    @NotNull(message = "from time field can't be null")
    private Integer fromTime;

    @NotNull(message = "to time field can't be null")
    private Integer toTime;

    @NotNull(message = "url field can't be null")
    private String url;
}
