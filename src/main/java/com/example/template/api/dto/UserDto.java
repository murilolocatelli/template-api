package com.example.template.api.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
public class UserDto {

    private Long id;

    @NotNull
    private String name;

    @Min(value = 1)
    @Max(value = 100)
    private Long age;

    private Double height;

    @NotNull
    private Boolean active;

    private LocalDate date;

    private ZonedDateTime dateTime;

}
