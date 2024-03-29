package org.woen.team18742.Modules.Brush;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.woen.team18742.Collectors.BaseCollector;
import org.woen.team18742.Modules.Manager.IRobotModule;
import org.woen.team18742.Modules.Manager.Module;
import org.woen.team18742.Tools.Configs.Configs;
import org.woen.team18742.Tools.Devices;

@Module
public class StaksBrush implements IRobotModule {
    private Servo stacklift;
    private ServoImplEx leftStacksBrush;
    private ServoImplEx rightStacksBrush;
    private Brush _Brush;

    private BrushUpState newState = BrushUpState.STATE_UP;


    enum BrushUpState {
        STATE_UP, STATE_DOWN;
    }

    private void normalRun() {
        leftStacksBrush.setPwmEnable();
        rightStacksBrush.setPwmEnable();

        leftStacksBrush.setPosition(Configs.StackBrush.LEFT_SERVO_FWD);
        rightStacksBrush.setPosition(Configs.StackBrush.RIGHT_SERVO_FWD);
    }

    private void reversRun() {
        leftStacksBrush.setPwmEnable();
        rightStacksBrush.setPwmEnable();

        leftStacksBrush.setPosition(Configs.StackBrush.LEFT_SERVO_REV);
        rightStacksBrush.setPosition(Configs.StackBrush.RIGHT_SERVO_REV);
    }

    private void stop() {
        leftStacksBrush.setPosition(Configs.StackBrush.LEFT_SERVO_STOP);
        rightStacksBrush.setPosition(Configs.StackBrush.RIGHT_SERVO_STOP);
        servoSetUpPose();

        //leftStacksBrush.setPwmDisable();
        //rightStacksBrush.setPwmDisable();
    }

    public void servoSetUpPose() {
        stacklift.setPosition(Configs.StackBrush.SERVO_LIFT_UP);

        newState = BrushUpState.STATE_UP;
    }

    public void servoSetDownPose() {
        stacklift.setPosition(Configs.StackBrush.SERVO_LIFT_DOWN);
        newState = BrushUpState.STATE_DOWN;
    }

    public boolean brushIsDown() {
        if (BrushUpState.STATE_DOWN == newState) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void Init(BaseCollector collector) {

        _Brush = collector.GetModule(Brush.class);
        rightStacksBrush = Devices.rightStackBrush;
        leftStacksBrush = Devices.leftStackBrush;
        stacklift = Devices.stackLift;
    }

    @Override
    public void Update() {
        if (brushIsDown()) {
            switch (_Brush.trueStateBrush) {
                case 1:
                    normalRun();
                    break;
                case 2:
                    reversRun();
                    break;
                case 3:
                    stop();
                    break;
            }
        } else {
            stop();
        }
    }
}
