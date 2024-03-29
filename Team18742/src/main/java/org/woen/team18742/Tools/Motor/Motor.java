package org.woen.team18742.Tools.Motor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.woen.team18742.Tools.Battery;
import org.woen.team18742.Tools.Configs.Configs;
import org.woen.team18742.Tools.PIDF;
import org.woen.team18742.Tools.ToolTelemetry;

public class Motor {
    public final DcMotorEx Motor;

    private PIDF _velocityPid;

    private final ReductorType _encoderType;

    private boolean _isCustomPid = false;

    private final VelocityControl _velControl;

    public Motor(DcMotorEx motor, ReductorType type, PIDF pid){
        this(motor, type);

        _velocityPid = pid;

        _isCustomPid = true;
    }

    public Motor(DcMotorEx motor, ReductorType type){
        Motor = motor;

        _encoderType = type;

        _velocityPid = new PIDF(Configs.Motors.DefultP, Configs.Motors.DefultI, Configs.Motors.DefultD, 0, Configs.Motors.DefultF, 50, 0);

        MotorsHandler.AddMotor(this);

        _velControl = new VelocityControl(motor);
    }

    public void Start(){
        _velocityPid.Start();
        _velControl.Start();
    }

    public void setDirection(DcMotorSimple.Direction dir){
        Motor.setDirection(dir);
    }

    public void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior power){
        Motor.setZeroPowerBehavior(power);
    }

    public void setMode(DcMotor.RunMode mode){
        Motor.setMode(mode);
    }

    public double getCurrentPosition(){
        return Motor.getCurrentPosition();
    }

    public void Update(){
        _velControl.Update();

        if(!_isCustomPid)
            _velocityPid.UpdateCoefs(Configs.Motors.DefultP, Configs.Motors.DefultI, Configs.Motors.DefultD, 0, Configs.Motors.DefultF);

        double pidSpeed = _velocityPid.Update(_targetEncoderSpeed - _velControl.GetVelocity(), _targetEncoderSpeed);

        Motor.setPower(pidSpeed / Battery.ChargeDelta);
    }

    private double _targetEncoderSpeed = 0;

    public void setPower(double speed){
        setEncoderPower(speed * _encoderType.Ticks);
    }

    public void setEncoderPower(double speed){
        _targetEncoderSpeed = speed;
    }
    
    public VelocityControl GetVelocityController(){
        return _velControl;
    }
}
