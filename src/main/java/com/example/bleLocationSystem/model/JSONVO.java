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
public class JSONVO {
    private String deviceName;

    private double distance1;
    private double rssi1;

    private double distance2;
    private double rssi2;

    private double distance3;
    private double rssi3;

    private double distance4;
    private double rssi4;

    private double distance5;
    private double rssi5;

    private double distance6;
    private double rssi6;

    private double distance7;
    private double rssi7;

    private double distance8;
    private double rssi8;

    private int CO;
}
