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

            int m = (int)(1000*scale);

            g2.translate(10, 5); // 원점을 (10, 5)로 이동시킨다.
            maxX=(int)beaconW*(int)(m/beaconW);
            maxY=(int)beaconH*(int)(m/beaconH);
            System.out.println("maxX:"+maxX+"maxY"+maxY);

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

            radius = (int)(5*scale);
            g2.setFont(new Font("궁서",Font.BOLD,15));
            g2.fillOval(maxX/2-radius, 0-radius, radius*2, radius*2);      //3
            g2.drawString("Ap3("+beaconW/2+", "+beaconH+")",maxX/2-radius, 0+5*radius);  //3

            g2.fillOval(0-radius, maxY-radius, radius*2, radius*2);    //1
            g2.drawString("Ap1(0.0, 0.0)",0-radius, maxY-3*radius);            //1
            g2.fillOval(maxX-radius, maxY-radius, radius*2, radius*2);     //2
            g2.drawString("Ap2("+beaconW+", 0.0)",maxX-20*radius, maxY-3*radius);     //2


            if(x!=-1 && y!=-1) {
                x=movePoint(x*(m/beaconW),0,maxX);
                y=movePoint(y*(m/beaconH),0,maxY);
                ox=movePoint(ox*(m/beaconW),0,maxX);
                oy=movePoint(oy*(m/beaconH),0,maxY);

                g2.translate(0,maxY);
                if(i%2==0)
                    g2.setColor(Color.RED);
                else if(i%2==1)
                    g2.setColor(Color.GREEN);
                g2.fillRect((int)x-radius, -((int)y+radius), radius*2, radius*2);

                i++;

                if(i%2==0)
                    g2.setColor(Color.magenta);
                else if(i%2==1)
                    g2.setColor(Color.blue);
                g2.drawOval((int)ox-radius, -((int)oy+radius), radius*2, radius*2);
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
