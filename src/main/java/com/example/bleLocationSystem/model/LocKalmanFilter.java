package com.example.bleLocationSystem.model;

public class LocKalmanFilter {
    double dt;
    double[][] u;
    double[][] x;
    double[][] A;
    double[][] B;
    double[][] H;
    double[][] Q;
    double[][] R;
    double[][] P;
    double[][] S;
    double[][] K;
    double[][] I;

    public LocKalmanFilter (double dt, double u_x, double u_y, double std_acc, double x_std_meas, double y_std_meas) {
        dt = dt;

        //Define the  control input variables
        u = new double[][] {{u_x}, {u_y}};

        //Intial State
        x = new double[][] {{0},
                            {0},
                            {0},
                            {0}};

        //Define the State Transition Matrix A
        A = new double[][] {{1, 0, dt, 0},
                            {0, 1, 0, dt},
                            {0, 0, 1, 0},
                            {0 , 0, 0, 1}};

        //Define the State Transition Matrix A 2
//        A = new double[][] {{1, dt, 0, 0},
//                            {0, 1, 0, 0},
//                            {0, 0, 1, dt},
//                            {0 , 0, 0, 1}};

        //Define the Control Input Matrix B
        B = new double[][] {{Math.pow(dt,2)/2, 0} ,
                            {0,Math.pow(dt,2)/2},
                            {dt, 0},
                            {0, dt} };

        //Define Measurement Mapping Matrix
        H = new double[][] {{1, 0, 0, 0},
                            {0, 1, 0, 0}};

        //Define Measurement Mapping Matrix 2
//        H = new double[][] {{1, 0, 0, 0},
//                            {0, 0, 1, 0}};

        //Initial Process Noise Covariance
        Q = new double[][] {{Math.pow(dt,4)/4, 0, Math.pow(dt, 3)/2, 0},
                            {0, Math.pow(dt,4)/4, 0, Math.pow(dt,3)/2},
                            {Math.pow(dt,3)/2, 0, Math.pow(dt, 2), 0},
                            {0, Math.pow(dt,3)/2, 0, Math.pow(dt,2)}};

        for(int i=0; i<4; i++) {
            for(int j=0; j<4; j++) {
                Q[i][j] = Q[i][j] * Math.pow(std_acc, 2);
            }
        }

        //Initial Process Noise Covariance 2
//        Q = new double[][] {{1, 0, 0, 0},
//                            {0, 1, 0, 0},
//                            {0, 0, 1, 0},
//                            {0, 0, 0, 1}};


        //Initial Measurement Noise Covariance
        R = new double[][] {{Math.pow(x_std_meas, 2), 0},
                            {0, Math.pow(y_std_meas, 2)}};

        //Initial Measurement Noise Covariance 2
//        R = new double[][] {{50, 0},
//                            {0, 50}};

        //Initial Covariance Matrix
        P = new double[][] {{1, 0, 0, 0},
                            {0, 1, 0, 0},
                            {0, 0, 1, 0},
                            {0, 0, 0, 1}};

        //Initial Covariance Matrix 2
//        P = new double[][] {{100, 0, 0, 0},
//                            {0, 100, 0, 0},
//                            {0, 0, 100, 0},
//                            {0, 0, 0, 100}};
    }

    public double[][] predict() {

        //x_k =Ax_(k-1) + Bu_(k-1)  //1
        x = hangHap(hangGop(A, x), hangGop(B, u));
        //2
//        x = hangGop(A, x);

        //P= A*P*A' + Q
        P = hangHap(hangGop(hangGop(A, P), hangShift(A)), Q);
        return x;
    }

    public double[][] update(double[][] z) {
        //S = H*P*H'+R
        S = hangHap(hangGop(H, hangGop(P, hangShift(H))), R);
        //K = P * H'* inv(H*P*H'+R)
        K = hangGop(hangGop(P, hangShift(H)), inverse(S));

        x = hangHap(x , hangGop(K, hangCha(z, hangGop(H, x))));

//        for (int i=0; i< x.length; i++) {
//            for(int j=0; j< x[0].length; j++) {
//                x[i][j] = Math.round(x[i][j]);
//            }
//        }

        I = new double[][] {{1, 0, 0, 0},
                            {0, 1, 0, 0},
                            {0, 0, 1, 0},
                            {0, 0, 0, 1}};

        //1
        P = hangGop(hangCha(I, hangGop(K, H)), P);
        //2
//        P = hangCha(P, hangGop(hangGop(K, H), P));
        return x;
    }

    public double[][] generalGop(double[][] arr1, double[][] arr2) {
        double[][] answer = new double[arr1.length][arr2[0].length];
        for(int i = 0 ; i < arr1.length ; ++i){
            for(int j = 0 ; j < arr2[0].length ; ++j){
                answer[i][j] = arr1[i][j] * arr2[i][j];
            }
        }

        return answer;
    }

    public double[][] hangGop(double[][] arr1, double[][] arr2) {
        double[][] answer = new double[arr1.length][arr2[0].length];

        for(int i = 0 ; i < arr1.length ; ++i){
            for(int j = 0 ; j < arr2[0].length ; ++j){
                for(int k = 0 ; k < arr1[0].length ; ++k) {
                    answer[i][j] += arr1[i][k] * arr2[k][j];
                }
            }
        }

        return answer;
    }

    public double[][] hangHap(double[][] arr1, double[][] arr2) {
        double[][] answer = new double[arr1.length][arr2[0].length];

        for (int i=0; i < arr1.length; i++) {
            for (int j=0; j < arr1[i].length; j++) {
                answer[i][j] = arr1[i][j] + arr2[i][j];
            }
        }
        return answer;
    }

    public double[][] hangCha(double[][] arr1, double[][] arr2) {
        double[][] answer = new double[arr1.length][arr2[0].length];

        for (int i=0; i < arr1.length; i++) {
            for (int j=0; j < arr1[i].length; j++) {
                answer[i][j] = arr1[i][j] - arr2[i][j];
            }
        }
        return answer;
    }

    public double[][] hangShift(double[][] arr) {
        double[][] shiftedArr = new double[arr[0].length][arr.length];

        for (int i=0; i < arr.length; i++) {
            for(int j=0; j < arr[0].length; j++) {
                shiftedArr[j][i] = arr[i][j];
            }
        }
        return shiftedArr;
    }

    public double[][] inverse(double[][] arr) {
        double det = arr[0][0]*arr[1][1] - arr[0][1]*arr[1][0];

        double[][] inversedArr = new double[][] {{arr[1][1]/det, -arr[0][1]/det},
                                                {-arr[1][0]/det, arr[0][0]/det}};
        return inversedArr;
    }
}
