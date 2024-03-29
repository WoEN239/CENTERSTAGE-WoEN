package org.woen.team17517.Programms.Test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.woen.team17517.RobotModules.UltRobot;

@Autonomous
@Config
public class TestDriveTrainVelocity extends LinearOpMode {
    UltRobot robot;
    public static double x = 0;
    public static double y = 0;
    public static double h = 0;
    @Override
    public void runOpMode() throws InterruptedException {
        robot = new UltRobot(this);
        waitForStart();
        while (opModeIsActive()){
            robot.updateWhilePositionFalse(new Runnable[]{
                    () -> robot.driveTrainVelocityControl.moveRobotCord(x,y,h),
                    () -> robot.timer.getTimeForTimer(3),
                    () -> robot.driveTrainVelocityControl.moveRobotCord(0,0,0),
                    () -> robot.timer.getTimeForTimer(1),
                    () -> robot.driveTrainVelocityControl.moveRobotCord(-x,-y,-h),
                    () -> robot.timer.getTimeForTimer(3),
                    () -> robot.driveTrainVelocityControl.moveRobotCord(0,0,0),
                    () -> robot.timer.getTimeForTimer(1),
            });
            robot.allUpdate();
        }
    }
}

