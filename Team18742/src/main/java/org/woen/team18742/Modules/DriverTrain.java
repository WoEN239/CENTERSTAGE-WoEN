package org.woen.team18742.Modules;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.woen.team18742.Collectors.BaseCollector;
import org.woen.team18742.Tools.Devices;
import org.woen.team18742.Tools.Vector2;

public class DriverTrain {
    private final DcMotor _leftForwardDrive;
    private final DcMotor _rightForwardDrive;
    private final DcMotor _leftBackDrive;
    private final DcMotor _rightBackDrive;

    private final double diametr = 9.8, encoderconstat = 1440;
    private final Gyroscope _gyro;

    public DriverTrain(BaseCollector collector) {
        _gyro = collector.Gyro;

        _leftForwardDrive = Devices.LeftForwardDrive;
        _rightBackDrive = Devices.RightBackDrive;
        _rightForwardDrive = Devices.RightForwardDrive;
        _leftBackDrive = Devices.LeftBackDrive;


        _leftForwardDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        _leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        _rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        _rightForwardDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        _rightForwardDrive.setDirection(REVERSE);
        _rightBackDrive.setDirection(REVERSE);

        ResetIncoder();
    }


    public void DriveDirection(Vector2 speed, double rotate){
       _leftForwardDrive.setPower(speed.X - speed.Y + rotate);
       _rightBackDrive.setPower(speed.X - speed.Y - rotate);
       _leftBackDrive.setPower(speed.X + speed.Y + rotate);
       _rightForwardDrive.setPower(speed.X + speed.Y - rotate);
    }

    public void ResetIncoder() {
        _leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        _rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        _leftForwardDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        _rightForwardDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        _rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        _leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        _leftForwardDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        _rightForwardDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double GetForwardDistance(){
        return (_leftBackDrive.getCurrentPosition()-_rightForwardDrive.getCurrentPosition()+_leftForwardDrive.getCurrentPosition()-_rightBackDrive.getCurrentPosition())/4.0/encoderconstat*PI*diametr;
    }

    public double GetSideDistance(){
        return (_leftBackDrive.getCurrentPosition()+_rightForwardDrive.getCurrentPosition()+_leftForwardDrive.getCurrentPosition()+_rightBackDrive.getCurrentPosition())/4.0/encoderconstat*PI*diametr;
    }

    public double GetLeftBackIncoder(){
        return  _leftBackDrive.getCurrentPosition() / encoderconstat * PI * diametr;
    }

    public double GetLeftForwardIncoder(){
        return  _leftForwardDrive.getCurrentPosition() / encoderconstat * PI * diametr;
    }

    public double GetRightBackIncoder(){
        return  _rightBackDrive.getCurrentPosition() / encoderconstat * PI * diametr;
    }

    public double GetRightForwardIncoder(){
        return  _rightForwardDrive.getCurrentPosition() / encoderconstat * PI * diametr;
    }

    public void Stop(){
        _leftForwardDrive.setPower(0);
        _rightBackDrive.setPower(0);
        _leftBackDrive.setPower(0);
        _rightForwardDrive.setPower(0);
    }

    public void SetSpeedWorldCoords(Vector2 speed, double rotate) {
        DriveDirection(speed.Turn(_gyro.GetRadians()), rotate);
    }
}
