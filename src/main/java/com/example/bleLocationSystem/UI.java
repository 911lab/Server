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


//    public UI () {
//        setTitle("Ble Location App");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
//
//        double widthScale = d.width/1050.0;
//        double heightScale = (d.height-50)/1050.0;
//
//        if(widthScale>heightScale)
//            scale=heightScale;
//        else
//            scale=widthScale;
//
//        setSize((int)(1050*scale),(int)(1050*scale));
//        setLocationRelativeTo(null);
//        makeUI();
//
//        setVisible(true);
//
//    }
    public UI (double w, double h) {
        setTitle("Ble Location App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        beaconW=w;
        beaconH=h;
        double widthScale = d.width/1050.0;
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

        double x = -1;
        double y = -1;
        double ox = -1;
        double oy = -1;
        int i =0;
        public void paintComponent(Graphics g) {


//            Graphics2D g2=(Graphics2D)g;
            g2=(Graphics2D)g;

            float dash0[] = {1,0f};
            float dash3[] = {3,3f};

            int c = (int)(500*scale);
//            g2.translate(c+10, c);                // 원점을 (300, 300)로 이동시킨다.

            g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash3,0));

//
//            for (int i=-c; i<=c; i = i+c/10) {
//                g2.draw(new Line2D.Float(-c, i, c, i));     // x축과 평행선을 그린다.
//            }
//            for (int j=-c; j<=c; j = j+c/10) {
//                g2.draw(new Line2D.Float(j, -c, j, c));     // y축과 평행선을 그린다.
//            }
            int m = (int)(1000*scale);
            g2.translate(10, 5);
            for (int i=0; i<=m; i = i+(int)(m/beaconH)) {
                g2.draw(new Line2D.Float(0, i, m, i));     // x축과 평행선을 그린다.
            }
            for (int j=0; j<=m; j = j+(int)(m/beaconW)) {
                g2.draw(new Line2D.Float(j, 0, j, m));     // y축과 평행선을 그린다.
            }


            g2.setStroke(new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));


            g2.draw(new Line2D.Float(0, m/2, m, m/2));     // x축을 그린다.

            g2.draw(new Line2D.Float(m/2, 0, m/2, m));     // y축을 그린다.



            g2.setColor(Color.BLUE);


//            for ( int x=20, y=20, radius=1; x<300; x=x+20,y=y+20,radius++ ) {
//
//                g2.fillOval(x-radius, y-radius, radius*2, radius*2);
//
//            }


//            int radius = 5;
            radius = (int)(5*scale);
            g2.setFont(new Font("궁서",Font.BOLD,15));
            g2.fillOval(m/2-radius, 0-radius, radius*2, radius*2);      //3
            g2.drawString("Ap3("+beaconW/2+", "+beaconH+")",m/2-radius, 0+5*radius);  //3

            g2.fillOval(0-radius, m-radius, radius*2, radius*2);    //1
            g2.drawString("Ap1(0.0, 0.0)",0-radius, m-3*radius);
            g2.fillOval(m-radius, m-radius, radius*2, radius*2);     //2
            g2.drawString("Ap2("+beaconW+", 0.0)",m-20*radius, m-3*radius);

//            g2.fillOval(0-radius, 0-radius, radius*2, radius*2);
//            g2.fillOval(0-radius, -250-radius, radius*2, radius*2);
            //정삼각형
//            g2.fillOval(0-radius, (int)(-Math.sqrt(750000)/2)-radius, radius*2, radius*2);      //3
//            g2.fillOval(-500-radius, (int)(Math.sqrt(750000)/2)-radius, radius*2, radius*2);    //1
//            g2.fillOval(500-radius, (int)(Math.sqrt(750000)/2)-radius, radius*2, radius*2);     //2

            //삼각형(20x15)
//            g2.setFont(new Font("궁서",Font.BOLD,15));
//            g2.fillOval(0-radius, -c-radius, radius*2, radius*2);      //3
//            g2.drawString("Ap3",0-radius, -c+4*radius);
//            g2.fillOval(-c-radius, c/2-radius, radius*2, radius*2);    //1
//            g2.drawString("Ap1",-c-radius, c/2-3*radius);
//            g2.fillOval(c-radius, c/2-radius, radius*2, radius*2);     //2
//            g2.drawString("Ap2",c-radius, c/2-3*radius);

            //삼각형(5x5)
//            g2.fillOval(0-radius, -500-radius, radius*2, radius*2);      //3
//            g2.fillOval(-500-radius, 500-radius, radius*2, radius*2);    //1
//            g2.fillOval(500-radius, 500-radius, radius*2, radius*2);     //2
            if(x!=-1 && y!=-1) {
                //정삼각형
//                g2.translate(-500, (int)(Math.sqrt(750000)/2));
//                g2.setColor(Color.RED);
//                g2.fillRect((int)(x*100)-5, -((int)(y*100)-5), 10, 10);

                //삼각형(20x15)
                g2.translate(0,m);
                if(i%2==0)
                    g2.setColor(Color.RED);
                else if(i%2==1)
                    g2.setColor(Color.GREEN);
                g2.fillRect((int)(x*(m/beaconW))-radius, -((int)(y*(m/beaconH))+radius), radius*2, radius*2);
                i++;
                if(i%2==0)
                    g2.setColor(Color.magenta);
                else if(i%2==1)
                    g2.setColor(Color.blue);
                g2.drawOval((int)(ox*(m/beaconW))-radius, -((int)(oy*(m/beaconH))+radius), radius*2, radius*2);
                //삼각형(5x5)
//                g2.translate(-500,500);
//                g2.setColor(Color.RED);
//                g2.fillRect((int)(x*200)-5, -((int)(y*200)+5), 10, 10);
            }
        }
    }
}
