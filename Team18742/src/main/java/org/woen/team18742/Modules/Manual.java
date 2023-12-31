package org.woen.team18742.Modules;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.woen.team18742.Collectors.BaseCollector;
import org.woen.team18742.Modules.Lift.Lift;
import org.woen.team18742.Modules.Lift.LiftPose;
import org.woen.team18742.Modules.Manager.IRobotModule;
import org.woen.team18742.Modules.Manager.Module;
import org.woen.team18742.Modules.Manager.TeleopModule;
import org.woen.team18742.Tools.Vector2;

@TeleopModule
public class Manual implements IRobotModule {
    private boolean _brushReversOld = false, _brushOld = false;

    private Plane _plane;

    private Gamepad _gamepad;

    private Brush _brush;
    private Intake _intake;
    private Lift _lift;
    private DriverTrain _driverTrain;

    @Override
    public void Init(BaseCollector collector) {
        _plane = new Plane(collector.Time);

        _gamepad = collector.Robot.gamepad1;

        _brush = collector.GetModule(Brush.class);
        _intake = collector.GetModule(Intake.class);
        _lift = collector.GetModule(Lift.class);
        _driverTrain = collector.GetModule(DriverTrain.class);
    }

    @Override
    public void Start() {}

    @Override
    public void Update() {
        _plane.Update();

        _driverTrain.DriveDirection(
                new Vector2(_gamepad.left_stick_y, _gamepad.left_stick_x),
                _gamepad.right_stick_x);

        boolean A = _gamepad.square;
        boolean liftUp = _gamepad.dpad_up;
        boolean liftDown = _gamepad.dpad_down;
        boolean brushRevers = _gamepad.circle;
        boolean grip = _gamepad.triangle;
        boolean brush = _gamepad.cross;
        boolean zajat = _gamepad.left_bumper;// зажать эту кнопку чтоб досрочно запустить самолетик
        boolean average = _gamepad.dpad_right;
        double railgunopen = _gamepad.left_trigger;
        double railgunnoopen = _gamepad.right_trigger;

        _plane.BezpolezniRailgunUp(railgunopen * 0.3);
        _plane.BezpolezniRailgunDown(railgunnoopen * 0.3);

        if(grip)
            _intake.releaseGripper();

        if (brush && !_brushOld) {
            if(_brush.IsIntake())
                _brush.Stop();
            else
                _brush.IntakePowerWithDefense();
        } else if(brushRevers && !_brushReversOld){
            if(_brush.IsRevers())
                _brush.Stop();
            else
                _brush.Revers();
        }

        if (A)
            _plane.Launch(zajat);
        else
            _plane.DeLaunch();

        if (liftUp && _intake.isPixelLocated)
            _lift.SetLiftPose(LiftPose.UP);

        if(liftDown)
            _lift.SetLiftPose(LiftPose.DOWN);

        if(average && _intake.isPixelLocated)
            _lift.SetLiftPose(LiftPose.AVERAGE);

        _brushOld = brush;
        _brushReversOld = brushRevers;
    }

    @Override
    public void Stop() {}
}