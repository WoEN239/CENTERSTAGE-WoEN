package org.woen.team17517.RobotModules;

import static java.lang.Math.abs;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.woen.team17517.Service.PIDMethod;
import org.woen.team17517.Service.RobotModule;

import java.util.HashMap;

public class DriveTrain implements RobotModule{
    UltRobot robot;
    public DriveTrain(UltRobot robot){
        this.robot = robot;
        voltage = 12;
        targetH = 0;
        targetX = 0;
        targetY = 0;
    }
    public static double kPX = 0;
    public static double kDX = 0;
    public static double kIX = 0;

    public static double kPY = 0;
    public static double kDY = 0;
    public static double kIY = 2;

    public static double kPH = 0;
    public static double kDH = 0;
    public static double kIH = 0;

    public static double ImaxX = 0;
    public static double ImaxH = 0;
    public static double ImaxY = 0;

    private double targetX = 0;
    private double targetH = 0;
    private double targetY = 0;
    private double voltage;
    private double posX = 0;
    private double posY = 0;
    private double posH = 0;
    private double X;
    private double Y;
    private double H;

    private double errX;
    private double errY;
    private double errH;
    public static double minErrX;
    public static double minErrY;
    public static double minErrH;

    public static double u_maxX = 0;
    public static double u_maxH = 0;
    public static double u_maxY = 0;

    private PIDMethod pidX = new PIDMethod(kPX,kIX,kDX,ImaxX);
    private PIDMethod pidY = new PIDMethod(kPY,kIY,kDY,ImaxY);
    private PIDMethod pidH = new PIDMethod(kPH,kIH,kDH,ImaxH);
    private static double kt = 5;

    public HashMap<String,Double> getTargets() {
        HashMap<String,Double> targetMap = new HashMap<>();
        targetMap.put("X",targetX);
        targetMap.put("H",targetH);
        targetMap.put("Y",targetY);
        return targetMap;
    }
    ElapsedTime timer = new ElapsedTime();
    public void setTarget(double x, double y, double h) {
        targetX = x;
        targetH = h;
        targetY = y;
        timer.reset();
        timer.seconds();
    }

    public void update(){
        voltage = robot.voltageSensorPoint.getVol();
        posX = robot.odometryNew.getX();
        posY = robot.odometryNew.getY();
        posH = robot.odometryNew.getH();

        errX = targetX - posX;
        errY = targetY - posY;
        errH = targetH - posH;
        while (Math.abs(errH)>360){
            targetH -= 360*Math.signum(targetH - posH);
        }

        u_maxX = timer.seconds() * kt;

        if (u_maxX> 2004){
            u_maxX = 2004;
        }

        if (abs(X) > u_maxX){
            X = u_maxX * Math.signum(u_maxX);
        }

        u_maxH = timer.seconds() * kt;

        if (u_maxH> 2004){
            u_maxH = 2004;
        }

        if (abs(X) > u_maxH){
            X = u_maxH * Math.signum(u_maxH);
        }


        u_maxY = timer.seconds() * kt;

        if (u_maxY> 2004){
            u_maxY = 2004;
        }
        if (abs(Y) > u_maxY){
            Y = u_maxY * Math.signum(u_maxY);
        }

        pidX.setCoefficent(kPX,kIX,kDX,0,ImaxX);
        pidY.setCoefficent(kPY,kIY,kDY,0,ImaxY);
        pidH.setCoefficent(kPH,kIH,kDH,0,ImaxH);

        X = pidX.PID(targetX,posX,voltage);
        Y = pidY.PID(targetY,posY,voltage);
        H = pidH.PID(targetH,posH,voltage);

        robot.driveTrainVelocityControl.moveRobotCord(X,Y,H);
    }
    public boolean isAtPosition(){
        return Math.abs(errH)<minErrH && Math.abs(errX)<minErrX && Math.abs(errY)<minErrY;
    }
}

