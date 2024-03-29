package org.woen.team18742.Modules.Lift;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.woen.team18742.Collectors.BaseCollector;
import org.woen.team18742.Modules.Intake.Intake;
import org.woen.team18742.Modules.Manager.IRobotModule;
import org.woen.team18742.Modules.Manager.Module;
import org.woen.team18742.Tools.Configs.Configs;
import org.woen.team18742.Tools.Devices;
import org.woen.team18742.Tools.PIDF;
import org.woen.team18742.Tools.Timers.Timer;

import java.sql.Time;

@Module
public class Lift implements IRobotModule {
    private DcMotor _liftMotor;

    private DigitalChannel _endSwitchUp, _endswitchDown;

    private boolean _endingUpState = false, _endingDownState = false;

    private final PIDF _liftPIDF = new PIDF(Configs.LiftPid.PCoef, Configs.LiftPid.ICoef, Configs.LiftPid.DCoef, Configs.LiftPid.GCoef, 0, 1, 0.3);
    private Intake _intake;

    @Override
    public void Init(BaseCollector collector) {
        _intake = collector.GetModule(Intake.class);

        _endSwitchUp = Devices.EndSwitchUp;
        _endswitchDown = Devices.EndSwitchDown;

        _liftMotor = Devices.LiftMotor;
        _liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        _liftMotor.setPower(0);

        ResetLift();

        _endSwitchUp.setMode(DigitalChannel.Mode.INPUT);
        _endswitchDown.setMode(DigitalChannel.Mode.INPUT);
    }

    private void ResetLift() {
        _liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        _liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void Update() {
        _liftPIDF.UpdateCoefs(Configs.LiftPid.PCoef, Configs.LiftPid.ICoef, Configs.LiftPid.DCoef, Configs.LiftPid.GCoef, 0);

        _endingUpState = _endSwitchUp.getState();
        _endingDownState = _endswitchDown.getState();

        if (_liftPose != LiftPose.DOWN)
            _liftMotor.setPower(_liftPIDF.Update(_liftPose.encoderPose() - _liftMotor.getCurrentPosition()));
        else if (!_intake.IsTurnNormal() && Math.abs(Configs.Lift.TurnPos - _liftMotor.getCurrentPosition()) < 60)
            _liftMotor.setPower(Math.max(_liftPIDF.Update(Configs.Lift.TurnPos - _liftMotor.getCurrentPosition()), Configs.LiftPid.DOWN_MOVE_POWER));
        else {
            if (_endingDownState)
                _liftMotor.setPower(Configs.LiftPid.DOWN_AT_TARGET_POWER);
            else {
                if (isTurnPosPassed())
                    _liftMotor.setPower(Math.min(Configs.LiftPid.DOWN_MOVE_POWER * (_liftMotor.getCurrentPosition() / 580d), Configs.LiftPid.DOWN_MOVE_POWER));
                else
                    _liftMotor.setPower(Configs.LiftPid.DOWN_MOVE_POWER_FAST);
            }
        }

        if (_endingDownState)
            ResetLift();
    }

    public boolean isATarget() {
        return (_liftPose == LiftPose.UP && _endingUpState) || ((_liftPose == LiftPose.MIDDLE_LOWER || _liftPose == LiftPose.MIDDLE_UPPER) && Math.abs(_liftPIDF.Err) < 100) || (_liftPose == LiftPose.DOWN && _endingDownState);
    }

    public boolean isDown() {
        return _liftPose == LiftPose.DOWN && isATarget();
    }

    public boolean isUp() {
        return _liftPose == LiftPose.UP && isATarget();
    }

    public boolean isAverage() {
        return (_liftPose == LiftPose.MIDDLE_UPPER || _liftPose == LiftPose.MIDDLE_LOWER) && isATarget();
    }

    public void SetLiftPose(LiftPose pose) {
        if (!_intake.isPixelGripped() && pose != LiftPose.DOWN)
            return;

        _liftPose = pose;
    }

    @Override
    public void Start() {
        _liftPIDF.Start();
        _liftPIDF.Update(0);
        _liftPose = LiftPose.UP;
        _kostilTimer.Start(15, ()->_liftPose = LiftPose.DOWN);
    }

    private Timer _kostilTimer = new Timer();

    public boolean isTurnPosPassed() {
        return _liftMotor.getCurrentPosition() > Configs.Lift.TurnPos;
    }

    private LiftPose _liftPose = LiftPose.DOWN;
}