package com.example.bleLocationSystem.model;


public class RssiFilter {
    float tempAlpha = -30;
    int lossNum = 4;

    public void setRssiVo(Ap ap1, Ap ap2, Ap ap3, VO beforeFilteredVo, VO originalVo) {
        double maxRssi1 = getMaxRssi(ap1, ap2, ap3);
        double maxRssi2 = getMaxRssi(ap2, ap1, ap3);
        double maxRssi3 = getMaxRssi(ap3, ap1, ap2);


        originalVo.setRssi1(checkRssi(beforeFilteredVo.getRssi1(), originalVo.getRssi1(), maxRssi1));
        originalVo.setDistance1(calcDistance(originalVo.getRssi1()));

        originalVo.setRssi2(checkRssi(beforeFilteredVo.getRssi2(), originalVo.getRssi2(), maxRssi2));
        originalVo.setDistance2(calcDistance(originalVo.getRssi2()));

        originalVo.setRssi3(checkRssi(beforeFilteredVo.getRssi3(), originalVo.getRssi3(), maxRssi3));
        originalVo.setDistance3(calcDistance(originalVo.getRssi3()));

        //return originalVo;
    }

    //거리값 rssi값으로
    public double calcRssi(double distance) {

        double rssi = -10*lossNum*Math.log10(distance)+tempAlpha;

        return rssi;
    }

    //rssi값 거리값으로
    public double calcDistance(double rssi) {

        double distance = Math.pow(10, (tempAlpha-rssi)/(10*lossNum));

        return distance;
    }

    //두 비콘 사이 거리 계산
    public double getDistance(double x, double y, double x1, double y1) {
        double d, xd, yd;
        xd = Math.pow((x1-x), 2);
        yd = Math.pow((y1-y), 2);
        d = Math.sqrt(yd+xd);
        return d;
    }


    public double getMaxRssi(Ap a, Ap b, Ap c){
        double dAB = getDistance(b.getX(),b.getY(),a.getX(),a.getY());
        double dAC = getDistance(c.getX(),c.getY(),a.getX(),a.getY());

        double maxRssi = calcRssi(Math.max(dAB, dAC));

        return maxRssi;
    }

    public double checkRssi(double beforeRssi, double originalRssi, double max){
        double difference = Math.abs(calcDistance(originalRssi)-calcDistance(beforeRssi));
        //10m이상 -> 이전값
        if(difference>=10)
            return beforeRssi;
        //6m이상 -> 두 거리 값 평균의 rssi
        else if(difference>=6){
            double avgDis = (calcDistance(originalRssi)+calcDistance(beforeRssi))/2.0;
            return calcRssi(avgDis);
        }
        //maxrssi값 이하 -> max값
        else if(originalRssi < max)
            return max;

        return originalRssi;
    }
}
