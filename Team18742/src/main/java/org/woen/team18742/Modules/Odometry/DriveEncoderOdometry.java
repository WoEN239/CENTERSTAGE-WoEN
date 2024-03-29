package org.woen.team18742.Modules.Odometry;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.woen.team18742.Collectors.BaseCollector;
import org.woen.team18742.Modules.DriveTrain.Drivetrain;
import org.woen.team18742.Modules.Gyroscope;
import org.woen.team18742.Modules.Manager.IRobotModule;
import org.woen.team18742.Modules.Manager.Module;
import org.woen.team18742.Tools.Bios;
import org.woen.team18742.Tools.Configs.Configs;
import org.woen.team18742.Tools.Vector2;

@Module
public class DriveEncoderOdometry implements IRobotModule {
    private Drivetrain _driverTrain;
    private double _leftForwardDrive = 0, _leftBackDrive = 0, _rightForwardDrive = 0, _rightBackDrive = 0;
    private Gyroscope _gyro;
    public Vector2 Position = new Vector2(), ShiftPosition = new Vector2(), Speed = new Vector2();
    private final ElapsedTime _deltaTime = new ElapsedTime();

    @Override
    public void Init(BaseCollector collector) {
        _driverTrain = collector.GetModule(Drivetrain.class);
        _gyro = collector.GetModule(Gyroscope.class);
    }

    @Override
    public void Update() {
        double lfd = _driverTrain.GetLeftForwardEncoder();
        double lbd = _driverTrain.GetLeftBackEncoder();
        double rfd = _driverTrain.GetRightForwardEncoder();
        double rbd = _driverTrain.GetRightBackEncoder();

        double deltaLfd = lfd - _leftForwardDrive, deltaLbd = lbd - _leftBackDrive, deltaRfd = rfd - _rightForwardDrive, deltaRbd = rbd - _rightBackDrive;

        double deltaX = (deltaLfd + deltaLbd + deltaRfd + deltaRbd) / 4;
        double deltaY = (-deltaLfd + deltaLbd + deltaRfd - deltaRbd) / 4;

        deltaY = deltaY * Configs.Odometry.YLag;

        _leftForwardDrive = lfd;
        _leftBackDrive = lbd;
        _rightBackDrive = rbd;
        _rightForwardDrive = rfd;

        Speed.X = (_driverTrain.GetSpeedLeftForwardEncoder() + _driverTrain.GetSpeedLeftBackEncoder() + _driverTrain.GetSpeedRightForwardEncoder() + _driverTrain.GetSpeedRightBackEncoder()) / 4;
        Speed.Y = (-_driverTrain.GetSpeedLeftForwardEncoder() + _driverTrain.GetSpeedLeftBackEncoder() + _driverTrain.GetSpeedRightForwardEncoder() - _driverTrain.GetSpeedRightBackEncoder()) / 4;

        ShiftPosition = new Vector2(deltaX *
                cos(-_gyro.GetRadians()) +
                deltaY * sin(-_gyro.GetRadians()),
                -deltaX * sin(-_gyro.GetRadians()) +
                        deltaY * cos(-_gyro.GetRadians()));

        Position = Vector2.Plus(ShiftPosition, Position);
    }

    @Override
    public void Start() {
        _deltaTime.reset();

        Position = Bios.GetStartPosition().Position.clone();
    }
}
