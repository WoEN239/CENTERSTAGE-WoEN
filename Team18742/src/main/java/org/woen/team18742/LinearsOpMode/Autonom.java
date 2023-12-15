package org.woen.team18742.LinearsOpMode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.woen.team18742.Collectors.AutonomCollector;
import org.woen.team18742.Collectors.BaseCollector;

@Autonomous
public class Autonom extends LinearOpMode {
    private BaseCollector _collector;

    @Override
    public void runOpMode() {
        _collector = new AutonomCollector(this);

        waitForStart();
        resetRuntime();

        _collector.Start();

        _collector.Driver.DriveDirection(0,0,0);
        _collector.Driver.DriveDirection(0.5,0,0);
        sleep(1700);
        _collector.Driver.DriveDirection(-0.2,0,0);
        sleep(1000);
        _collector.Driver.Stop();

        while (opModeIsActive()) {
            return;
           // _collector.Update();
            //telemetry.update();
        }

        _collector.Stop();
    }
}