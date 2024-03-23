package org.woen.team17517.Programms.RoadRunner;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.woen.team17517.RobotModules.UltRobot;

@TeleOp
public class RoadrunnerTest extends LinearOpMode {
    UltRobot robot;

    @Override
    public void runOpMode() throws InterruptedException {
        robot = new UltRobot(this);
        waitForStart();
        robot.mover.on();
        while (opModeIsActive()) {
            robot.updateWhilePositionFalse(
                    () -> robot.mover.trajectories(robot.mover.builder()
                            .strafeTo(new Vector2d(-50, 0))
                            .build()),
                    () -> robot.mover.trajectories(robot.mover.builder()
                            .strafeTo(new Vector2d(0, 0))
                            .build())
            );
            robot.allUpdate();
        }
    }
}