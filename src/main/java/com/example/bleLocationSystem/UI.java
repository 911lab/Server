package com.example.bleLocationSystem;

import com.example.bleLocationSystem.model.UserLocation;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.UnknownHostException;


@Slf4j
public class UI extends JFrame {
    private UI.MyPanel8 p;
    double scale = 1;
    double beaconW, beaconH, w1;
    int i;
    int j;

    ArrayList<Integer> hjx;
    ArrayList<Integer> hjy;
    String hjdeviceName;
    ArrayList<Integer> elsex;
    ArrayList<Integer> elsey;

    public UI(double w, double h) {
        setTitle("Ble Location App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        beaconW=w;
//        beaconH=h;
//        double widthScale = d.width/1050.0;
//        double heightScale = (d.height-50)/1050.0;

        i = 0;
        j = 0;

        hjx = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1, -1));
        hjy = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1, -1));
        elsex = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1, -1));
        elsey = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1, -1));

        w1 = w;
        beaconW = w * 3 + w / 2.0;
        beaconH = h;
        double widthScale = d.width / 4050.0;
        double heightScale = (d.height - 50) / 1050.0;

        if (widthScale > heightScale)
            scale = heightScale;
        else
            scale = widthScale;

        setSize((int) (4050 * scale), (int) (1050 * scale) + 50 + 100);
        setLocationRelativeTo(null);
        makeUI();

        setVisible(true);
    }

    private void makeUI() {
        //p = new MyPanel();
        p = new UI.MyPanel8();
        add(p, BorderLayout.CENTER);
    }

    //2개짜리(ulList)
    public void setUserLocation(ArrayList<UserLocation> ul) {
        p.ox = ul.get(0).getX();
        p.oy = ul.get(0).getY();
        p.x = ul.get(1).getX();
        p.y = ul.get(1).getY();
        p.repaint();
    }
    public void setUserLocation(UserLocation ul) {
        //1개짜리
        p.x = ul.getX();
        p.y = ul.getY();
        p.deviceName = ul.getDeviceName();
        p.repaint();
    }

    //삼각형 하나
    public class MyPanel extends JPanel {
        Graphics2D g2;
        int radius;
        int maxX,maxY;
        double x = -1;
        double y = -1;

        double ox = -1;
        double oy = -1;

        String deviceName;
        int i =0;

        int j = 0;
        public void paintComponent(Graphics g) {
            g2=(Graphics2D)g;

            float dash0[] = {1,0f};
            float dash3[] = {3,3f};

            g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash3,0));//점선

            int m = (int)(1000*scale);

            g2.translate(10, 5); // 원점을 (10, 5)로 이동시킨다.
            maxX=(int)beaconW*(int)(m/beaconW);
            maxY=(int)beaconH*(int)(m/beaconH);

            for (int i=0; i<=m; i = i+(int)(m/beaconH)) {
                g2.draw(new Line2D.Float(0, i, m, i));     // x축과 평행선을 그린다.
            }
            for (int j=0; j<=m; j = j+(int)(m/beaconW)) {
                g2.draw(new Line2D.Float(j, 0, j, m));     // y축과 평행선을 그린다.
            }

            g2.setStroke(new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));//실선
            g2.draw(new Line2D.Float(0, maxY/2, maxX, maxY/2));     // x축을 그린다.
            g2.draw(new Line2D.Float(maxX/2, 0, maxX/2, maxY));     // y축을 그린다.

            g2.setColor(Color.BLUE);

            g2.draw(new Line2D.Float(0, maxY, maxX/2, 0));     // 1-3
            g2.draw(new Line2D.Float(maxX, maxY, maxX/2, 0));     // 2-3
            g2.draw(new Line2D.Float(0, maxY, maxX, maxY));     // 1-2

            radius = (int)(5*scale);
            g2.setFont(new Font("궁서",Font.BOLD,15));
            g2.fillOval(maxX/2-radius, 0-radius, radius*2, radius*2);      //3
            g2.drawString("Ap3("+String.format("%.1f",beaconW/2)+", "+String.format("%.1f",beaconH)+")",maxX/2-radius, 0+5*radius);  //3

            g2.fillOval(0-radius, maxY-radius, radius*2, radius*2);    //1
            g2.drawString("Ap1(0.0, 0.0)",0-radius, maxY-3*radius);            //1
            g2.fillOval(maxX-radius, maxY-radius, radius*2, radius*2);     //2
            g2.drawString("Ap2("+String.format("%.1f",beaconW)+", 0.0)",maxX-20*radius, maxY-3*radius);     //2


            if(x!=-1 && y!=-1) {
//                삼각형내로 포인트 이동
//                x=movePoint(x*(m/beaconW),0,maxX);
//                y=movePoint(y*(m/beaconH),0,maxY);
//                ox=movePoint(ox*(m/beaconW),0,maxX);
//                oy=movePoint(oy*(m/beaconH),0,maxY);

                x=x*(m/beaconW);
                y=y*(m/beaconH);
                ox=ox*(m/beaconW);
                oy=oy*(m/beaconH);

                g2.translate(0,maxY);

                g2.setColor(Color.RED);
                g2.fillRect((int)x-radius, -((int)y+radius), radius*2, radius*2);
                g2.setColor(Color.BLUE);
                g2.drawOval((int)ox-radius, -((int)oy+radius), radius*2, radius*2);
            }
        }
    }

    public class MyPanel8 extends JPanel {
        Graphics2D g2;
        int radius;
        int maxX,maxY;
        double x = -1;
        double y = -1;
        double ox = -1;
        double oy = -1;

        String deviceName;

//        두명 위치 5번 연속 찍을 때
//        ArrayList<Double> hjx = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));
//        ArrayList<Double> hjy = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));
//        ArrayList<Double> elsex = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));
//        ArrayList<Double> elsey = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));


//        String deviceName;
        public void paintComponent(Graphics g) {


            g2=(Graphics2D)g;

            g2.clearRect(0, 0, 4000, 1000);

            float dash0[] = {1,0f};
            float dash3[] = {3,3f};

            g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash3,0));//점선

            int h = (int)(1000*scale);
            int w = (int)(4000*scale);


            g2.translate(10, 50); // 원점을 (10, 50)로 이동시킨다.

            try {
                InetAddress ipAddress = InetAddress.getLocalHost();
                String ipNum = ipAddress.getHostAddress();
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("맑은 고딕",Font.BOLD,15));
                g2.drawString("서버 IP : " + ipNum,0, 0);            //1
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }


            g2.translate(0, 25); // 원점을 (0, 25)로 이동시킨다.


            maxX=(int)(beaconW*(w/beaconW));
            maxY=(int)(beaconH*(h/beaconH));


            for (int i=0; i<=h; i = i+(int)(h/beaconH)) {
                g2.draw(new Line2D.Float(0, i, w, i));     // x축과 평행선을 그린다.
            }
            for (int j=0; j<=w; j = j+(int)(w/beaconW)) {
                g2.draw(new Line2D.Float(j, 0, j, h));     // y축과 평행선을 그린다.
            }

            //g2.setStroke(new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));//실선
            //g2.draw(new Line2D.Float(0, maxY/2, maxX, maxY/2));     // x축을 그린다.
            //g2.draw(new Line2D.Float(maxX/2, 0, maxX/2, maxY));     // y축을 그린다.
            g2.setStroke(new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash3,0));//점선
            g2.setColor(Color.RED);
            //삼각형선 그리기

            g2.draw(new Line2D.Float(0, maxY, (int)((w/beaconW)*(w1/2)), 0));//12
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)), 0, (int)((w/beaconW)*w1), maxY));//23
            g2.draw(new Line2D.Float((int)((w/beaconW)*w1), maxY, (int)((w/beaconW)*(w1/2)*3), 0));//34
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)*3), 0, (int)((w/beaconW)*w1*2), maxY));//45
            g2.draw(new Line2D.Float((int)((w/beaconW)*w1*2), maxY, (int)((w/beaconW)*(w1/2)*5), 0));//56
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)*5), 0, (int)((w/beaconW)*w1*3), maxY));//67
            g2.draw(new Line2D.Float((int)((w/beaconW)*w1*3), maxY, (int)((w/beaconW)*(w1/2)*7), 0));//78
            g2.draw(new Line2D.Float(0, maxY, (int)((w/beaconW)*w1*3), maxY));//17
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)), 0, (int)((w/beaconW)*(w1/2)*7), 0));//28

            //ap찍기
            g2.setColor(Color.BLUE);
            radius = (int)(10*scale);
            g2.setFont(new Font("맑은 고딕",Font.BOLD,15));

            g2.fillOval(0-radius, maxY-radius, radius*2, radius*2);    //1(0,0)
            g2.drawString("Ap1(0.0, 0.0)",0-radius, maxY+5*radius);            //1
            g2.fillOval((int)((w/beaconW)*w1)-radius, maxY-radius, radius*2, radius*2);     //3(10,0)
            g2.drawString("Ap3("+String.format("%.1f",(w1))+", 0.0)",(int)(w/beaconW*w1)-20*radius, maxY+5*radius);     //3
            g2.fillOval((int)((w/beaconW)*w1*2)-radius, maxY-radius, radius*2, radius*2);     //5(20,0)
            g2.drawString("Ap5("+String.format("%.1f",(w1*2))+", 0.0)",(int)(w/beaconW*w1*2)-20*radius, maxY+5*radius);     //5
            g2.fillOval((int)((w/beaconW)*w1*3)-radius, maxY-radius, radius*2, radius*2);     //7(30,0)
            g2.drawString("Ap7("+String.format("%.1f",(w1*3))+", 0.0)",(int)(w/beaconW*w1*3)-20*radius, maxY+5*radius);     //7

            g2.fillOval((int)((w/beaconW)*(w1/2))-radius, 0-radius, radius*2, radius*2);      //2(5,10)
            g2.drawString("Ap2("+String.format("%.1f",((w1/2)))+", "+String.format("%.1f",beaconH)+")",(int)(w/beaconW*(w1/2))-radius, 0-radius);  //2
            g2.fillOval((int)((w/beaconW)*(w1/2)*3)-radius, 0-radius, radius*2, radius*2);     //4(15,10)
            g2.drawString("Ap4("+String.format("%.1f",((w1/2)*3))+", "+String.format("%.1f",beaconH)+")",(int)(w/beaconW*(w1/2)*3)-20*radius, 0-radius);     //4
            g2.fillOval((int)((w/beaconW)*(w1/2)*5)-radius, 0-radius, radius*2, radius*2);     //6(25,10)
            g2.drawString("Ap6("+String.format("%.1f",((w1/2)*5))+", "+String.format("%.1f",beaconH)+")",(int)(w/beaconW*(w1/2)*5)-20*radius, 0-radius);     //6
            g2.fillOval((int)((w/beaconW)*(w1/2)*7)-radius, 0-radius, radius*2, radius*2);     //8(35,10)
            g2.drawString("Ap8("+String.format("%.1f",(w1/2)*7)+", "+String.format("%.1f",beaconH)+")",(int)(w/beaconW*(w1/2)*7)-30*radius, 0-radius);     //8



            log.info("x={} y={}",x, y);

            if(x!=-1 && y!=-1) {

//                //device 여러개 있는 경우
                if(!deviceName.equals("ddd")) {
                    hjdeviceName = deviceName;
                    hjx.set(i % 5, (int) (x * (w / beaconW)));
                    hjy.set(i % 5, (int) (y * (h / beaconH)));
                    i++;
                }
//                else {
//                    elsex.set(j%5, (int)(x*(w/beaconW)));
//                    elsey.set(j%5, (int)(y*(h/beaconH)));
//                    j++;
//                }
//
//                //1개 찍을때
                g2.translate(0,maxY); //원점이동
//                log.info("hjx.size() = {}", hjx.size());
//                log.info("i = {}", i);
                for(int k=0; k<hjx.size(); k++) {
                    g2.setColor(Color.RED);
                    int n = (k+i)%5;
//                    log.info("{} : [{}]=({},{})",hjdeviceName, n, hjx.get(n), hjy.get(n));
                    if(hjx.get(n) >= 0 && hjy.get(n) >= 0) {
//                        log.info("{} : [{}]=({},{})",hjdeviceName, n, hjx.get(n), hjy.get(n));
                        g2.fillRect(hjx.get(n)-radius, -(hjy.get(n)+radius), radius*2, radius*2);
                        g2.setColor(Color.black);
                        g2.drawString(hjdeviceName,hjx.get(n)-radius, -(hjy.get(n)+radius));
                    }
                }
//
//                log.info("elsex.size() = {}", elsex.size());
//                log.info("j = {}", j);
//                for(int k=0; k<elsex.size(); k++) {
//                    g2.setColor(Color.BLUE);
//                    int q = (k+j)%5;
//                    log.info("BG[{}]=({},{})", q, elsex.get(q), elsey.get(q));
//                    if(elsex.get(q) >= 0 && elsey.get(q) >= 0) {
//                        //g2.fillRect((int)Math.round(elsex.get(q))-radius, -((int)Math.round(elsey.get(q))+radius), radius*2, radius*2);
//                        //g2.fillRect(elsex.get(q)-radius, -(elsey.get(q)+radius), radius*2, radius*2);
//                        g2.setColor(Color.black);
//                        //g2.drawString("BG",elsex.get(q)-radius, -(elsey.get(q)+radius));
//                    }
//                }


//                //device name 없이 화면
//                x=x*(w/beaconW);
//                y=y*(h/beaconH);
//                ox=ox*(w/beaconW);
//                oy=oy*(h/beaconH);


//                //사각형안으로
//                x=movePoint(x*(m/beaconW),0,maxX);
//                y=movePoint(y*(m/beaconH),0,maxY);
//                ox=movePoint(ox*(m/beaconW),0,maxX);
//                oy=movePoint(oy*(m/beaconH),0,maxY);

                //1개 찍을때
//                g2.translate(0,maxY); //원점이동
//                g2.setColor(Color.blue);
//                g2.fillRect((int)(x-radius), -(int)(y+radius), radius*2, radius*2);
//                g2.setColor(Color.black);
//                g2.drawString(deviceName,(int)(x-radius), -(int)(y+radius));


                //2개 같이 찍을때
                //if(i%2==0)
                //g2.setColor(Color.magenta);
                //else if(i%2==1)
//                g2.setColor(Color.blue);
//                g2.fillOval((int)ox-radius, -((int)oy+radius), radius*2, radius*2);

//                g2.setColor(Color.black);
//                g2.drawString(String.valueOf(i),(int)ox-radius, -((int)oy+radius));

            }
        }
    }
    public double movePoint(double p, double min, double max){
        if(p<min)
            return min;
        else if(p>max)
            return max;
        return p;

    }
}
