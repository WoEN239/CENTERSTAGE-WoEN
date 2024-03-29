package org.woen.team17517.Programms.Autonomus;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.woen.team17517.RobotModules.Intake.State;
import org.woen.team17517.RobotModules.OpenCV.PipeLine;
import org.woen.team17517.RobotModules.UltRobot;

public class AutnomModules {
    UltRobot robot;
    PipeLine pipeLine;

    public AutnomModules(UltRobot robot) {
        this.robot = robot;
    }

    public void move(double x, double y, double h, double time) {
        robot.updateWhilePositionFalse(new Runnable[]{
                ()-> robot.driveTrainVelocityControl.moveAngle(h),
                () -> robot.driveTrainVelocityControl.moveWithAngleControl(x,y),
                () -> robot.timer.getTimeForTimer(time),
                () -> robot.driveTrainVelocityControl.moveWithAngleControl(0,0),
                () -> robot.timer.getTimeForTimer(0.1),

        });
    }
    public void fullHide(){
        move(0,0,12000,2);
    }
    public void halfHide(){
        move(0,0,12000,1);
    }

    public void scoring() {
        robot.updateWhilePositionFalse(new Runnable[]{
                () -> robot.intake.scoring(),
                () -> robot.timer.getTimeForTimer(0.5)
        });
    }

    public void backdropLow() {
        robot.updateWhilePositionFalse(new Runnable[]{
                () -> robot.intake.setState(State.WAITINGBACKDROPDOWN),
        });
    }

    public void bacBoardPixels() {
        robot.updateWhilePositionFalse(new Runnable[]{
                () -> robot.grabber.close(),
                () -> robot.grabber.safe(),
                () -> robot.timer.getTimeForTimer(0.2),
                () -> robot.lift.moveUP(),
                () -> move(0, 20000, 0, 1),
                () -> robot.grabber.finish(),
                () -> move(0, -20000, 0, 1),
                () -> robot.grabber.open(),
                () -> robot.timer.getTimeForTimer(0.5),
                () -> robot.grabber.safe(),
                () -> robot.lift.moveDown(),
                () -> robot.grabber.down()
        });
    }

    public void eatPixels() {
        robot.updateWhilePositionFalse(new Runnable[]{
                () -> robot.brush.out(),
                () -> robot.timer.getTimeForTimer(1),
                () -> robot.brush.in(),
                () -> robot.timer.getTimeForTimer(2),
                () -> robot.grabber.close(),
                () -> robot.timer.getTimeForTimer(0.1),
                () -> robot.brush.out(),
                () -> robot.timer.getTimeForTimer(1),
                () -> robot.brush.off(),
                () -> robot.timer.getTimeForTimer(0.1),
        });
    }
}
