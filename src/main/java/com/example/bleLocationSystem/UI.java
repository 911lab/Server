package com.example.bleLocationSystem;

import com.example.bleLocationSystem.model.UserLocation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class UI extends JFrame {
    private MyPanel p;
    private JScrollPane scrollPane;
    //JScrollPane scroll;


    public UI () {
//        setContentPane(MainPanel);
        setTitle("Ble Location App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(1050,1050);
        setLocationRelativeTo(null);
//        scroll = new JScrollPane(p);
//        scroll.setBounds(0,0,1050,1050);
//        add(scroll, BorderLayout.EAST);
        makeUI();

        setVisible(true);

    }
    private void makeUI() {
        //JScrollBar scroll = new JScrollBar(1, 30, 0, -1050, 1100);

        p = new MyPanel();


        add(p, BorderLayout.CENTER);

    }

    public void setUserLocation(UserLocation ul) {
        p.x = ul.getX();
        p.y = ul.getY();

        p.repaint();
    }

    public class MyPanel extends JPanel {


        Graphics2D g2;
        int radius;

        double x = -1;
        double y = -1;

        double realLocX = 0;
        double realLocY = 0;

        public void paintComponent(Graphics g) {


//            Graphics2D g2=(Graphics2D)g;
            g2=(Graphics2D)g;

            float dash0[] = {1,0f};
            float dash3[] = {3,3f};

            g2.translate(510, 500);                // 원점을 (300, 300)로 이동시킨다.


            g2.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash3,0));


            for (int i=-500; i<=500; i = i+50) {

                g2.draw(new Line2D.Float(-500, i, 500, i));     // x축과 평행선을 그린다.

            }
            for (int j=-500; j<=500; j = j+50) {

                g2.draw(new Line2D.Float(j, -500, j, 500));     // y축과 평행선을 그린다.


            }


            g2.setStroke(new BasicStroke(2,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1,dash0,0));


            g2.draw(new Line2D.Float(-500, 0, 500, 0));     // x축을 그린다.

            g2.draw(new Line2D.Float(0, -500, 0, 500));     // y축을 그린다.


            g2.setColor(Color.BLUE);


//            for ( int x=20, y=20, radius=1; x<300; x=x+20,y=y+20,radius++ ) {
//
//                g2.fillOval(x-radius, y-radius, radius*2, radius*2);
//
//            }


//            int radius = 5;
            radius = 5;
//            g2.fillOval(0-radius, 0-radius, radius*2, radius*2);
//            g2.fillOval(0-radius, -250-radius, radius*2, radius*2);
            //정삼각형
//            g2.fillOval(0-radius, (int)(-Math.sqrt(750000)/2)-radius, radius*2, radius*2);      //3
//            g2.fillOval(-500-radius, (int)(Math.sqrt(750000)/2)-radius, radius*2, radius*2);    //1
//            g2.fillOval(500-radius, (int)(Math.sqrt(750000)/2)-radius, radius*2, radius*2);     //2

            //삼각형(20x15)
            g2.fillOval(0-radius, -500-radius, radius*2, radius*2);      //3
            g2.fillOval(-500-radius, 250-radius, radius*2, radius*2);    //1
            g2.fillOval(500-radius, 250-radius, radius*2, radius*2);     //2

            if(x!=-1 && y!=-1) {
                //정삼각형
//                g2.translate(-500, (int)(Math.sqrt(750000)/2));
//                g2.setColor(Color.RED);
//                g2.fillRect((int)(x*100)-5, -((int)(y*100)-5), 10, 10);

                g2.translate(-500,250);
                g2.setColor(Color.RED);
                g2.fillRect((int)(x*50)-5, -((int)(y*50)+5), 10, 10);
            }


        }
    }
}
