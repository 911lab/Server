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
public class TestUI extends JFrame {
    private TestUI.MyPanel8 p;
    double scale = 1;
    double beaconW,beaconH,w1;
    int i;
    int j;



    ArrayList<Integer> hjx;
    ArrayList<Integer> hjy;
    ArrayList<Integer> elsex;
    ArrayList<Integer> elsey;

    public TestUI (double w, double h) {
        setTitle("Ble Location App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//        beaconW=w;
//        beaconH=h;
//        double widthScale = d.width/1050.0;
//        double heightScale = (d.height-50)/1050.0;

        i = 0;
        j = 0;

//        hjx = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1 ,-1));
//        hjy = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1 ,-1));
//        elsex = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1 ,-1));
//        elsey = new ArrayList<Integer>(Arrays.asList(-1, -1, -1, -1 ,-1));

        w1=w;
        beaconW=w*3+w/2.0;
        beaconH=h;
        double widthScale = d.width/4050.0;
        double heightScale = (d.height-50)/1050.0;

        if(widthScale>heightScale)
            scale=heightScale;
        else
            scale=widthScale;

        setSize((int)(4050*scale),(int)(1050*scale)+50+100);
        setLocationRelativeTo(null);
        makeUI();

        setVisible(true);
    }
    private void makeUI() {
        //p = new MyPanel();
        p = new TestUI.MyPanel8();
        add(p, BorderLayout.CENTER);
    }

        public void setUserLocation(ArrayList<UserLocation> ul) {
//    public void setUserLocation(UserLocation ul) {
        //4개짜리
        p.wx = ul.get(0).getX();
        p.wy = ul.get(0).getY();
        p.kx = ul.get(1).getX();
        p.ky = ul.get(1).getY();
        p.px = ul.get(2).getX();
        p.py = ul.get(2).getY();
        p.pwpx = ul.get(3).getX();
        p.pwpy = ul.get(3).getY();

        //1개짜리
//        p.x = ul.getX();
//        p.y = ul.getY();
//        p.deviceName = ul.getDeviceName();
        //p.repaint();
        //p.revalidate();
        //p.repaint();

        //p.g2.dispose();
        //p.update(p.g2);
        p.repaint();

        //p.revalidate();
        //p.repaint();


    }

    public class MyPanel extends JPanel {
        Graphics2D g2;
        int radius;
        int maxX,maxY;
        double x = -1;
        double y = -1;



        double ox = -1;
        double oy = -1;
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
            g2.drawString("Ap3("+beaconW/2+", "+beaconH+")",maxX/2-radius, 0+5*radius);  //3

            g2.fillOval(0-radius, maxY-radius, radius*2, radius*2);    //1
            g2.drawString("Ap1(0.0, 0.0)",0-radius, maxY-3*radius);            //1
            g2.fillOval(maxX-radius, maxY-radius, radius*2, radius*2);     //2
            g2.drawString("Ap2("+beaconW+", 0.0)",maxX-20*radius, maxY-3*radius);     //2


            if(x!=-1 && y!=-1) {
//                x=movePoint(x*(m/beaconW),0,maxX);
//                y=movePoint(y*(m/beaconH),0,maxY);
//                ox=movePoint(ox*(m/beaconW),0,maxX);
//                oy=movePoint(oy*(m/beaconH),0,maxY);

                x=x*(m/beaconW);
                y=y*(m/beaconH);
                ox=ox*(m/beaconW);
                oy=oy*(m/beaconH);

                g2.translate(0,maxY);
                //if(i%2==0)
                g2.setColor(Color.RED);
                //else if(i%2==1)
                //  g2.setColor(Color.GREEN);
                g2.fillRect((int)x-radius, -((int)y+radius), radius*2, radius*2);

                i++;

//                if(i%2==0)
//                    g2.setColor(Color.magenta);
//                else if(i%2==1)
//                    g2.setColor(Color.blue);

                g2.setColor(Color.BLUE);
                g2.drawOval((int)ox-radius, -((int)oy+radius), radius*2, radius*2);
            }
        }
    }

    public class MyPanel8 extends JPanel {
        Graphics2D g2;
        int radius;
        int maxX,maxY;

        double wx = -1;
        double wy = -1;
        double kx = -1;
        double ky = -1;
        double px = -1;
        double py = -1;
        double pwpx = -1;
        double pwpy = -1;

        double preWx = -1;
        double preWy = -1;
        double preKx = -1;
        double preKy = -1;
        double prePx = -1;
        double prePy = -1;
        double prePwpx = -1;
        double prePwpy = -1;


        //ArrayList<Double> hjx = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));
        //ArrayList<Double> hjy = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));
        //ArrayList<Double> elsex = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));
        //ArrayList<Double> elsey = new ArrayList<Double>(Arrays.asList(-1.0, -1.0, -1.0, -1.0 ,-1.0));


        String deviceName;
        public void paintComponent(Graphics g) {


            g2=(Graphics2D)g;

            //화면 초기화할때
//            g2.clearRect(0, 0, 4000, 1000);

            float dash0[] = {1,0f};
            float dash3[] = {3,3f};

            g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash3,0));//점선

            int h = (int)(1000*scale);
            int w = (int)(4000*scale);


            g2.translate(10, 50); // 원점을 (10, 30)로 이동시킨다.

            try {
                InetAddress ipAddress = InetAddress.getLocalHost();
                String ipNum = ipAddress.getHostAddress();
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("맑은 고딕",Font.BOLD,15));
                g2.drawString("서버 IP : " + ipNum,0, 0);            //1
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }


            g2.translate(0, 25); // 원점을 (10, 30)로 이동시킨다.


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
            //g2.draw(new Line2D.Float(0, maxY, maxX/2, 0));     // 1-3
            //g2.draw(new Line2D.Float(maxX, maxY, maxX/2, 0));     // 2-3
            //g2.draw(new Line2D.Float(0, maxY, maxX, maxY));     // 1-2
            g2.draw(new Line2D.Float(0, maxY, (int)((w/beaconW)*(w1/2)), 0));//12
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)), 0, (int)((w/beaconW)*w1), maxY));//23
            g2.draw(new Line2D.Float((int)((w/beaconW)*w1), maxY, (int)((w/beaconW)*(w1/2)*3), 0));//34
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)*3), 0, (int)((w/beaconW)*w1*2), maxY));//45
            g2.draw(new Line2D.Float((int)((w/beaconW)*w1*2), maxY, (int)((w/beaconW)*(w1/2)*5), 0));//56
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)*5), 0, (int)((w/beaconW)*w1*3), maxY));//67
            g2.draw(new Line2D.Float((int)((w/beaconW)*w1*3), maxY, (int)((w/beaconW)*(w1/2)*7), 0));//78
            g2.draw(new Line2D.Float(0, maxY, (int)((w/beaconW)*w1*3), maxY));//17
            g2.draw(new Line2D.Float((int)((w/beaconW)*(w1/2)), 0, (int)((w/beaconW)*(w1/2)*7), 0));//28
            //g2.draw(new Line2D.Float(0, maxY, (int)(w/beaconW)*10, maxY));//13
            //g2.draw(new Line2D.Float((int)(w/beaconW)*5, 0, (int)(w/beaconW)*15, 0));//24
            //g2.draw(new Line2D.Float((int)(w/beaconW)*10, maxY, (int)(w/beaconW)*20, maxY));//35
            //g2.draw(new Line2D.Float((int)(w/beaconW)*15, 0, (int)(w/beaconW)*25, 0));//46
            //g2.draw(new Line2D.Float((int)(w/beaconW)*20, maxY, (int)(w/beaconW)*30, maxY));//57
            //g2.draw(new Line2D.Float((int)(w/beaconW)*25, 0, (int)(w/beaconW)*35, 0));//68

            //ap찍기
            g2.setColor(Color.BLUE);
            radius = (int)(10*scale);
            g2.setFont(new Font("맑은 고딕",Font.BOLD,15));
            /*
            g2.fillOval(maxX/2-radius, 0-radius, radius*2, radius*2);      //3
            g2.drawString("Ap3("+beaconW/2+", "+beaconH+")",maxX/2-radius, 0+5*radius);  //3
            g2.fillOval(0-radius, maxY-radius, radius*2, radius*2);    //1
            g2.drawString("Ap1(0.0, 0.0)",0-radius, maxY-3*radius);            //1
            g2.fillOval(maxX-radius, maxY-radius, radius*2, radius*2);     //2
            g2.drawString("Ap2("+beaconW+", 0.0)",maxX-20*radius, maxY-3*radius);     //2
*/

            g2.fillOval(0-radius, maxY-radius, radius*2, radius*2);    //1(0,0)
            g2.drawString("Ap1(0.0, 0.0)",0-radius, maxY+5*radius);            //1
            g2.fillOval((int)((w/beaconW)*w1)-radius, maxY-radius, radius*2, radius*2);     //3(10,0)
            g2.drawString("Ap3("+(w1)+", 0.0)",(int)(w/beaconW*w1)-20*radius, maxY+5*radius);     //3
            g2.fillOval((int)((w/beaconW)*w1*2)-radius, maxY-radius, radius*2, radius*2);     //5(20,0)
            g2.drawString("Ap5("+(w1*2)+", 0.0)",(int)(w/beaconW*w1*2)-20*radius, maxY+5*radius);     //5
            g2.fillOval((int)((w/beaconW)*w1*3)-radius, maxY-radius, radius*2, radius*2);     //7(30,0)
            g2.drawString("Ap7("+(w1*3)+", 0.0)",(int)(w/beaconW*w1*3)-20*radius, maxY+5*radius);     //7

            g2.fillOval((int)((w/beaconW)*(w1/2))-radius, 0-radius, radius*2, radius*2);      //2(5,10)
            g2.drawString("Ap2("+((w1/2))+", "+beaconH+")",(int)(w/beaconW*(w1/2))-radius, 0-radius);  //2
            g2.fillOval((int)((w/beaconW)*(w1/2)*3)-radius, 0-radius, radius*2, radius*2);     //4(15,10)
            g2.drawString("Ap4("+((w1/2)*3)+", "+beaconH+")",(int)(w/beaconW*(w1/2)*3)-20*radius, 0-radius);     //4
            g2.fillOval((int)((w/beaconW)*(w1/2)*5)-radius, 0-radius, radius*2, radius*2);     //6(25,10)
            g2.drawString("Ap6("+((w1/2)*5)+", "+beaconH+")",(int)(w/beaconW*(w1/2)*5)-20*radius, 0-radius);     //6
            g2.fillOval((int)((w/beaconW)*(w1/2)*7)-radius, 0-radius, radius*2, radius*2);     //8(35,10)
            g2.drawString("Ap8("+String.format("%.1f", (w1/2)*7)+", "+String.format("%.1f", beaconH)+")",(int)(w/beaconW*(w1/2)*7)-30*radius, 0-radius);     //8

            //측위 결과 출력
            g2.translate(0,maxY); //원점이동

            //ex3 : 이동 경로 실선
//            g2.setStroke(new BasicStroke(5,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));
//            g2.setColor(Color.BLACK);
//            g2.draw(new Line2D.Float((int)(0.5 * (w / beaconW)), -(int)(0.5 * (h / beaconH)), (int)(10 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //11->10
//            g2.draw(new Line2D.Float((int)(10 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(13 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //10->9
//            g2.draw(new Line2D.Float((int)(13 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(13 * (w / beaconW)), -(int)(9 * (h / beaconH)))); //9->8
//            g2.draw(new Line2D.Float((int)(13 * (w / beaconW)), -(int)(9 * (h / beaconH)), (int)(16 * (w / beaconW)), -(int)(9 * (h / beaconH)))); //8->7
//            g2.draw(new Line2D.Float((int)(16 * (w / beaconW)), -(int)(9 * (h / beaconH)), (int)(16 * (w / beaconW)), -(int)(12.5 * (h / beaconH)))); //7->6
//            g2.draw(new Line2D.Float((int)(16 * (w / beaconW)), -(int)(12.5 * (h / beaconH)), (int)(25 * (w / beaconW)), -(int)(12.5 * (h / beaconH)))); //6->5
//            g2.draw(new Line2D.Float((int)(25 * (w / beaconW)), -(int)(12.5 * (h / beaconH)), (int)(25 * (w / beaconW)), -(int)(9.5 * (h / beaconH)))); //5->4
//            g2.draw(new Line2D.Float((int)(25 * (w / beaconW)), -(int)(9.5 * (h / beaconH)), (int)(30 * (w / beaconW)), -(int)(1 * (h / beaconH)))); //4->3
//            g2.draw(new Line2D.Float((int)(30 * (w / beaconW)), -(int)(1 * (h / beaconH)), (int)(36 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //3->2
//            g2.draw(new Line2D.Float((int)(36 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(48.5 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //2->1

            //Dynamic Test : 이동 경로 실선
            //이동경로

            g2.setColor(Color.BLACK);

            //1 point (51.5, 12)
            //2 point (48, 6.5)
            //3 point (41, 6.5)
            //4 point (37.5, 12)
            //5 point (31.5, 12)
            //6 point (31.5, 8.5)
            //7 point (26, 8.5)
            //8 point (30, 1)
            //9 point (22.5, 12)
            //10 point (18, 12)
            //11 point (18, 8.5)
            //12 point (13, 8.5)
            //13 point (13, 6.5)
            //14 point (22, 6.5)
            //15 point (22, 4)
            //16 point (9, 4)
            //17 point (9, 6.5)
            //18 point (1, 1)

            //1 point (51.5, 12)
            g2.fillRect((int) (51.5 * (w / beaconW)) - radius, -((int) (12 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //2 point (49, 6.5)
            g2.fillRect((int) (48 * (w / beaconW)) - radius, -((int) (6.5 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //3 point (37.5, 6.5)
//            g2.fillRect((int) (41 * (w / beaconW)) - radius, -((int) (6.5 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //4 point (37.5, 12)
//            g2.fillRect((int) (37.5 * (w / beaconW)) - radius, -((int) (12 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //7 point (26, 8.5)
//            g2.fillRect((int) (26 * (w / beaconW)) - radius, -((int) (8.5 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //8 point (30, 1)
            g2.fillRect((int) (30 * (w / beaconW)) - radius, -((int) (1 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //9 point (22.5, 12)
            g2.fillRect((int) (22.5 * (w / beaconW)) - radius, -((int) (12 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //12 point (13, 8.5)
//            g2.fillRect((int) (13 * (w / beaconW)) - radius, -((int) (8.5 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //13 point (13, 6.5)
//            g2.fillRect((int) (13 * (w / beaconW)) - radius, -((int) (6.5 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //15 point (22, 4)
//            g2.fillRect((int) (22 * (w / beaconW)) - radius, -((int) (6.5 * (h / beaconH)) + radius), radius * 2, radius * 2);

//            g2.fillRect((int) (15 * (w / beaconW)) - radius, -((int) (1 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //17 point (9, 6.5)
//            g2.fillRect((int) (9 * (w / beaconW)) - radius, -((int) (6.5 * (h / beaconH)) + radius), radius * 2, radius * 2);
            //18 point (1, 1)
            g2.fillRect((int) (1 * (w / beaconW)) - radius, -((int) (1 * (h / beaconH)) + radius), radius * 2, radius * 2);

            //직선(3.75,6.5),(48.75,6.5)점
//            g2.fillRect((int) (3.75 * (w / beaconW)) - radius, -((int) (6.5 * (h / beaconH)) + radius), radius * 2, radius * 2);
//            g2.fillRect((int) (48.75 * (w / beaconW)) - radius, -((int) (6.5 * (h / beaconH)) + radius), radius * 2, radius * 2);

            g2.setStroke(new BasicStroke(5,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));
            g2.setColor(Color.BLACK);

//            g2.draw(new Line2D.Float((int)(3.75 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(48.75 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //직선


            g2.draw(new Line2D.Float((int)(51.5 * (w / beaconW)), -(int)(12 * (h / beaconH)), (int)(48 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //1->2
            g2.draw(new Line2D.Float((int)(48 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(30 * (w / beaconW)), -(int)(1 * (h / beaconH)))); //2->3
//            g2.draw(new Line2D.Float((int)(41 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(37.5 * (w / beaconW)), -(int)(12 * (h / beaconH)))); //3->4
//            g2.draw(new Line2D.Float((int)(37.5 * (w / beaconW)), -(int)(12 * (h / beaconH)), (int)(26 * (w / beaconW)), -(int)(8.5 * (h / beaconH)))); //4->5
//            g2.draw(new Line2D.Float((int)(26 * (w / beaconW)), -(int)(8.5 * (h / beaconH)), (int)(30 * (w / beaconW)), -(int)(1 * (h / beaconH)))); //7->8
            g2.draw(new Line2D.Float((int)(30 * (w / beaconW)), -(int)(1 * (h / beaconH)), (int)(22.5 * (w / beaconW)), -(int)(12 * (h / beaconH)))); //8->9
            g2.draw(new Line2D.Float((int)(22.5 * (w / beaconW)), -(int)(12 * (h / beaconH)), (int)(9 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //9->10
//            g2.draw(new Line2D.Float((int)(13 * (w / beaconW)), -(int)(8.5 * (h / beaconH)), (int)(13 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //12->13
//            g2.draw(new Line2D.Float((int)(13 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(22 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //13->14
//            g2.draw(new Line2D.Float((int)(22 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(15 * (w / beaconW)), -(int)(1 * (h / beaconH)))); //14->15
//            g2.draw(new Line2D.Float((int)(15 * (w / beaconW)), -(int)(1 * (h / beaconH)), (int)(9 * (w / beaconW)), -(int)(6.5 * (h / beaconH)))); //15->16
            g2.draw(new Line2D.Float((int)(9 * (w / beaconW)), -(int)(6.5 * (h / beaconH)), (int)(1 * (w / beaconW)), -(int)(1 * (h / beaconH)))); //17->18

            g2.setStroke(new BasicStroke(3,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));//실선

            //Weight method
            if(wx!=-1 && wy!=-1) {
                if(wx!=999 && wy!=999) {
                    if (preWx != -1 && preWy != -1) {
                        g2.setColor(Color.RED);
                        g2.setStroke(new BasicStroke(5,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));//실선
                        g2.draw(new Line2D.Float((int) (preWx * (w / beaconW)), -(int) (preWy * (h / beaconH)), (int) (wx * (w / beaconW)), -(int) (wy * (h / beaconH))));//12
                        preWx = wx;
                        preWy = wy;
                    } else {
                        g2.setColor(Color.RED);
                        g2.fillRect((int) (wx * (w / beaconW)) - radius, -((int) (wy * (h / beaconH)) + radius), radius * 1, radius * 1);
                        preWx = wx;
                        preWy = wy;
                    }
                }
            }

            //Kalman method
            if(kx!=-1 && ky!=-1) {
                if(kx!=999 && ky!=999) {
                    if (preKx != -1 && preKy != -1) {
                        g2.setColor(Color.BLUE);
                        g2.setStroke(new BasicStroke(4,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));//실선
                        g2.draw(new Line2D.Float((int) (preKx * (w / beaconW)), -(int) (preKy * (h / beaconH)), (int) (kx * (w / beaconW)), -(int) (ky * (h / beaconH))));//12
                        preKx = kx;
                        preKy = ky;
                    } else {
                        g2.setColor(Color.BLUE);
                        g2.fillRect((int) (kx * (w / beaconW)) - radius, -((int) (ky * (h / beaconH)) + radius), radius * 1, radius * 1);
                        preKx = kx;
                        preKy = ky;
                    }
                }
            }

            //Proposed method without Proximity
            if(pwpx!=-1 && pwpy!=-1 ) {
                if(pwpx!=999 && pwpy!=999) {
                    if (prePwpx != -1 && prePwpy != -1) {
                        g2.setColor(Color.BLUE);
                        //g2.draw(new Line2D.Float((int) (prePwpx * (w / beaconW)), -(int) (prePwpy * (h / beaconH)), (int) (pwpx * (w / beaconW)), -(int) (pwpy * (h / beaconH))));//12
                        prePwpx = pwpx;
                        prePwpy = pwpy;
                    } else {
                        g2.setColor(Color.BLUE);
                        //g2.fillRect((int) (pwpx * (w / beaconW)) - radius, -((int) (pwpy * (h / beaconH)) + radius), radius * 1, radius * 1);
                        prePwpx = pwpx;
                        prePwpy = pwpy;
                    }
                }
            }

            //Proposed method
            if(px!=-1 && py!=-1) {
                if(px!=999 && py!=999) {
                    if (prePx != -1 && prePy != -1) {
                        g2.setColor(Color.RED);
                        //g2.draw(new Line2D.Float((int) (prePx * (w / beaconW)), -(int) (prePy * (h / beaconH)), (int) (px * (w / beaconW)), -(int) (py * (h / beaconH))));//12
                        prePx = px;
                        prePy = py;
                    } else {
                        g2.setColor(Color.RED);
                        //g2.fillRect((int) (px * (w / beaconW)) - radius, -((int) (py * (h / beaconH)) + radius), radius * 1, radius * 1);
                        prePx = px;
                        prePy = py;
                    }
                }
            }



                //else if(i%2==1)
                //g2.setColor(Color.GREEN);


//                g2.drawString(String.valueOf(i),(int)x-radius, -((int)y+radius));


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
    public double movePoint(double p, double min, double max){
        if(p<min)
            return min;
        else if(p>max)
            return max;
        return p;
    }
}
