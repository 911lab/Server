package com.example.bleLocationSystem.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class CO {
    private int danger_limit = 70000;
    private int COValue;

    public boolean checkDanger () {
        boolean checkTmp = false;

        if (COValue > danger_limit) {
            checkTmp = true;
        }

        return checkTmp;
    }
}
