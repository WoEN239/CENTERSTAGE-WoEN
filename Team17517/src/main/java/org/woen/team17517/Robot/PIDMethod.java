package org.woen.team17517.Robot;

import com.acmerobotics.dashboard.config.Config;


@Config
public  class PIDMethod {

    public PIDMethod(double kp, double ki, double kd){
            setCoefficent(kp, ki, kd,0,Double.POSITIVE_INFINITY);
    }
    public PIDMethod(double kp, double ki, double kd, double ks,Double maxI){
        setCoefficent(kp,ki,kd,ks,maxI);

    }
    double kp,kd,ki,ks = 0;
    Double maxI = Double.POSITIVE_INFINITY;
    public void setCoefficent(double kp, double ki, double kd, double ks,Double maxI){

        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.ks = ks;
        this.maxI = maxI;


    }
    double timeOld = System.nanoTime();
    Double deltaTime = null;
    double lastWrong = 0;
    private double P = 0;
    private double D =0;
    private double I = 0;
    private double C = 0;
    public double getI(){
        return I;
    }
    public double PID(double target,double voltage,double enc,double ks) {
        double wrong = target - enc;
        P = kp * wrong;
        if (deltaTime != null) {
            I = I + wrong * deltaTime * ki;

            D = ((wrong - lastWrong) / deltaTime) * kd;
        }else{
            I = 0;
            D = 0;
        }
        lastWrong = wrong;

        C = 12/voltage;
        if(I>maxI){
            I = maxI;
        }
        if(I<-maxI){
            I = -maxI;
        }
        double PID = (I + D + P)*C+target*ks;
        double timeNow = (double) System.nanoTime() / 1_000_000d;
        deltaTime = Double.valueOf(timeNow - timeOld);
        timeOld = timeNow;
        return PID;


    }
}