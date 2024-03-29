package org.woen.team17517.RobotModules.DriveTrain;

import static java.lang.Math.abs;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.woen.team17517.RobotModules.UltRobot;import org.woen.team17517.Service.PID;
import org.woen.team17517.Service.RobotModule;
import org.woen.team17517.Service.Vector2D;

import java.util.HashMap;
@Config

public class DriveTrain implements RobotModule{
    UltRobot robot;
    private double voltage = 12;
    public boolean autoMode = false;
    public DriveTrain(UltRobot robot){
        this.robot = robot;
        voltage = 12;
        targetH = 0;
        targetVector.setCord(0,0);
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

    private double targetH = 0;
    private Vector2D targetVector = new Vector2D();

    private double posH = 0;
    private Vector2D positionVector = new Vector2D();

    private double X;
    private double Y;
    private double H;

    private double errX;
    private double errY;
    private double errH;
    public static double minErrX;
    public static double minErrY;
    public static double minErrH;

    public static double kg = 0;

    public static double u_X = 0;
    public static double u_H = 0;
    public static double u_Y = 0;
    public static double u_max = 2000;


    private PID pidX = new PID(kPX,kIX,kDX,0,ImaxX,0);
    private PID pidY = new PID(kPY,kIY,kDY,0,ImaxY,0);
    private PID pidH = new PID(kPH,kIH,kDH,0,ImaxH,0);
    public static double kt = 5;

    HashMap<String,Double> positionMap = new HashMap<>();
    HashMap<String,Double> targetMap = new HashMap<>();
    HashMap<String,Double> errorMap = new HashMap<>();
    public HashMap<String,Double> getPosition(){
        positionMap.put("X",positionVector.getX());
        positionMap.put("Y",positionVector.getY());
        positionMap.put("H",posH);
        return positionMap;
    }
    public HashMap<String,Double> getTargets() {
        targetMap.put("X",targetVector.getX());
        targetMap.put("H",targetH);
        targetMap.put("Y",targetVector.getY());
        return targetMap;
    }
    public HashMap<String,Double> getErrors() {
        errorMap.put("X",errX);
        errorMap.put("H",errH);
        errorMap.put("Y",errY);
        return errorMap;
    }
    public void reset(){
        timer.reset();
        timer.seconds();
    }
    ElapsedTime timer = new ElapsedTime();
    public void moveGlobal(double x, double y, double h) {
        targetVector.setCord(x,y);
        targetH = h;
        reset();
        autoMode = true;
    }
    public void moveRobot(double x, double y, double h){
        targetVector = Vector2D.vectorSum(positionVector,new Vector2D(x,y));
        targetH = posH + h;
        reset();
        autoMode = true;
    }
    public void update(){
        if (autoMode) {
            voltage = robot.voltageSensorPoint.getVol();

            positionVector = robot.odometry.getPositionVector();
            posH = robot.odometry.getH();

            errX = targetVector.getX() - positionVector.getX();
            errY = targetVector.getY() - positionVector.getY();
            errH = targetH - posH;

            while (Math.abs(errH) > 180) {
                targetH -= 360 * Math.signum(targetH - posH);
            }


            pidX.setCoeficent(kPX, kIX, kDX, 0, ImaxX, kg);
            pidY.setCoeficent(kPY, kIY, kDY, 0, ImaxY, kg);
            pidH.setCoeficent(kPH, kIH, kDH, 0, ImaxH, kg);

            X = pidX.pid(targetVector.getX(), positionVector.getX(), voltage);
            Y = pidY.pid(targetVector.getY(), positionVector.getY(), voltage);
            H = pidH.pid(targetH, posH, voltage);
            /*
            u_X = timer.seconds()*kt;

            if (u_X > u_max) {
                u_X = u_max;
            }

            if (abs(X) > u_X) {
                X = u_X * Math.signum(u_X);
            }

            u_H = timer.seconds() * kt;

            if (u_H > u_max) {
                u_H = u_max;
            }

            if (abs(X) > u_H) {
                X = u_H * Math.signum(u_H);
            }


            u_Y = timer.seconds() * kt;

            if (u_Y > u_max) {
                u_Y = u_max;
            }
            if (abs(Y) > u_Y) {
                Y = u_Y * Math.signum(u_Y);
            }
            */
            robot.driveTrainVelocityControl.moveGlobalCord(X, Y, H);
        }
        }

    public boolean isAtPosition(){
        return !autoMode || (Math.abs(errH) < minErrH && Math.abs(errX) < minErrX && Math.abs(errY) < minErrY);
    }
}


