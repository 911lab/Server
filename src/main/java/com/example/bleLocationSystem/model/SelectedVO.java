package com.example.bleLocationSystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@JsonFormat
public class SelectedVO {
    private String deviceName;

    private double distance1;
    private double rssi1;

    private double distance2;
    private double rssi2;

    private double distance3;
    private double rssi3;
}
