package com.example.bleLocationSystem;

import com.example.bleLocationSystem.model.UserLocation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class UI extends JFrame {
    private MyPanel p;
    double scale = 1;
    double beaconW,beaconH;
    //비콘 8개
    public UI () {
        setTitle("Ble Location App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        beaconW=35;
        beaconH=10;
        double widthScale = d.width/4050.0;
        double heightScale = (d.height-50)/1050.0;

        if(widthScale>heightScale)
            scale=heightScale;
        else
            scale=widthScale;

        setSize((int)(4050*scale),(int)(1050*scale)+100);
        setLocationRelativeTo(null);
        makeUI();

        setVisible(true);

    }
    //비콘 3개
    public UI (double w, double h) {
        setTitle("Ble Location App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        beaconW=w;
        beaconH=h;
        double widthScale = d.width/3050.0;
        double heightScale = (d.height-50)/1050.0;

        if(widthScale>heightScale)
            scale=heightScale;
        else
            scale=widthScale;

        setSize((int)(1050*scale)+50,(int)(1050*scale));
        setLocationRelativeTo(null);
        makeUI();

        setVisible(true);

    }
    private void makeUI() {
        p = new MyPanel();
        add(p, BorderLayout.CENTER);
    }

    public void setUserLocation(ArrayList<UserLocation> ul) {
        p.ox = ul.get(0).getX();
        p.oy = ul.get(0).getY();
        p.x = ul.get(1).getX();
        p.y = ul.get(1).getY();

        p.repaint();
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
        public void paintComponent(Graphics g) {
            g2=(Graphics2D)g;

            float dash0[] = {1,0f};
            float dash3[] = {3,3f};

            g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash3,0));//점선

            int h = (int)(1000*scale);
            int w = (int)(4000*scale);

            g2.translate(10, 5); // 원점을 (10, 5)로 이동시킨다.
            maxX=(int)beaconW*(int)(w/beaconW);
            maxY=(int)beaconH*(int)(h/beaconH);


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
            g2.draw(new Line2D.Float(0, maxY, (int)(w/beaconW)*5, 0));//12
            g2.draw(new Line2D.Float((int)(w/beaconW)*5, 0, (int)(w/beaconW)*10, maxY));//23
            g2.draw(new Line2D.Float((int)(w/beaconW)*10, maxY, (int)(w/beaconW)*15, 0));//34
            g2.draw(new Line2D.Float((int)(w/beaconW)*15, 0, (int)(w/beaconW)*20, maxY));//45
            g2.draw(new Line2D.Float((int)(w/beaconW)*20, maxY, (int)(w/beaconW)*25, 0));//56
            g2.draw(new Line2D.Float((int)(w/beaconW)*25, 0, (int)(w/beaconW)*30, maxY));//67
            g2.draw(new Line2D.Float((int)(w/beaconW)*30, maxY, (int)(w/beaconW)*35, 0));//78
            g2.draw(new Line2D.Float(0, maxY, (int)(w/beaconW)*30, maxY));//17
            g2.draw(new Line2D.Float((int)(w/beaconW)*5, 0, (int)(w/beaconW)*35, 0));//28
            //g2.draw(new Line2D.Float(0, maxY, (int)(w/beaconW)*10, maxY));//13
            //g2.draw(new Line2D.Float((int)(w/beaconW)*5, 0, (int)(w/beaconW)*15, 0));//24
            //g2.draw(new Line2D.Float((int)(w/beaconW)*10, maxY, (int)(w/beaconW)*20, maxY));//35
            //g2.draw(new Line2D.Float((int)(w/beaconW)*15, 0, (int)(w/beaconW)*25, 0));//46
            //g2.draw(new Line2D.Float((int)(w/beaconW)*20, maxY, (int)(w/beaconW)*30, maxY));//57
            //g2.draw(new Line2D.Float((int)(w/beaconW)*25, 0, (int)(w/beaconW)*35, 0));//68

            //ap찍기
            g2.setColor(Color.BLUE);
            radius = (int)(10*scale);
            g2.setFont(new Font("궁서",Font.BOLD,15));
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
            g2.fillOval((int)(w/beaconW)*10-radius, maxY-radius, radius*2, radius*2);     //3(10,0)
            g2.drawString("Ap3("+(beaconW-25)+", 0.0)",(int)(w/beaconW*10)-20*radius, maxY+5*radius);     //3
            g2.fillOval((int)(w/beaconW)*20-radius, maxY-radius, radius*2, radius*2);     //5(20,0)
            g2.drawString("Ap5("+(beaconW-15)+", 0.0)",(int)(w/beaconW*20)-20*radius, maxY+5*radius);     //5
            g2.fillOval((int)(w/beaconW)*30-radius, maxY-radius, radius*2, radius*2);     //7(30,0)
            g2.drawString("Ap7("+(beaconW-5)+", 0.0)",(int)(w/beaconW*30)-20*radius, maxY+5*radius);     //7

            g2.fillOval((int)(w/beaconW)*5-radius, 0-radius, radius*2, radius*2);      //2(5,10)
            g2.drawString("Ap2("+(beaconW-30)+", "+beaconH+")",(int)(w/beaconW*5)-radius, 0+5*radius);  //2
            g2.fillOval((int)(w/beaconW)*15-radius, 0-radius, radius*2, radius*2);     //4(15,10)
            g2.drawString("Ap4("+(beaconW-20)+", "+beaconH+")",(int)(w/beaconW*15)-20*radius, 0+5*radius);     //4
            g2.fillOval((int)(w/beaconW)*25-radius, 0-radius, radius*2, radius*2);     //6(25,10)
            g2.drawString("Ap6("+(beaconW-10)+", "+beaconH+")",(int)(w/beaconW*25)-20*radius, 0+5*radius);     //6
            g2.fillOval((int)(w/beaconW)*35-radius, 0-radius, radius*2, radius*2);     //8(35,10)
            g2.drawString("Ap8("+beaconW+", "+beaconH+")",(int)(w/beaconW*35)-30*radius, 0+5*radius);     //8

            

            if(x!=-1 && y!=-1) {
                /*
                //사각형안으로
                x=movePoint(x*(m/beaconW),0,maxX);
                y=movePoint(y*(m/beaconH),0,maxY);
                ox=movePoint(ox*(m/beaconW),0,maxX);
                oy=movePoint(oy*(m/beaconH),0,maxY);
*/
                g2.translate(0,maxY); //원점이동
                //if(i%2==0)
                //    g2.setColor(Color.RED);
                //else if(i%2==1)
                    g2.setColor(Color.GREEN);
                g2.fillRect((int)x*(int)(w/beaconW)-radius, -((int)y*(int)(h/beaconH)+radius), radius*2, radius*2);

                //i++;

                //if(i%2==0)
                    g2.setColor(Color.magenta);
                //else if(i%2==1)
                //    g2.setColor(Color.blue);
                g2.fillOval((int)ox*(int)(w/beaconW)-radius, -((int)oy*(int)(h/beaconH)+radius), radius*2, radius*2);
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
