package org.woen.team18742.Modules.Odometry;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.woen.team18742.Collectors.AutonomCollector;
import org.woen.team18742.Collectors.BaseCollector;
import org.woen.team18742.Modules.Gyroscope;
import org.woen.team18742.Modules.Manager.AutonomModule;
import org.woen.team18742.Modules.Manager.Module;
import org.woen.team18742.Modules.Manager.IRobotModule;
import org.woen.team18742.Tools.Bios;
import org.woen.team18742.Tools.Configs.Configs;
import org.woen.team18742.Tools.Vector2;

@Module
public class OdometrsOdometry implements IRobotModule {
    private double _oldRotate = 0, _oldOdometrXLeft, _oldOdometrXRight, _oldOdometrY;
    public Vector2 Position = new Vector2(), ShiftPosition = new Vector2(), Speed = new Vector2();
    private Gyroscope _gyro;
    private OdometryHandler _odometrs;

    private final ElapsedTime _deltaTime = new ElapsedTime();


    @Override
    public void Init(BaseCollector collector) {
        _gyro = collector.GetModule(Gyroscope.class);
        _odometrs = collector.GetModule(OdometryHandler.class);
    }

    @Override
    public void LastUpdate() {
        double odometrXLeft = _odometrs.GetOdometerXLeft(), odometrY = _odometrs.GetOdometerY(), odometrXRight = _odometrs.GetOdometerXRight();
        double odometrSpeedXLeft = _odometrs.GetSpeedOdometerXLeft(), odometrSpeedY = _odometrs.GetSpeedOdometerY(), odometrSpeedXRight = _odometrs.GetSpeedOdometerXRight();

        double deltaX = (odometrXLeft - _oldOdometrXLeft + odometrXRight - _oldOdometrXRight) / 2;

        double deltaY = (_oldOdometrY - odometrY) - Configs.Odometry.RadiusOdometrY * Gyroscope.ChopAngle(_oldRotate - _gyro.GetRadians());

        Speed.X = (odometrSpeedXLeft + odometrSpeedXRight) / 2;

        Speed.Y = odometrSpeedY - Configs.Odometry.RadiusOdometrY * _gyro.GetSpeedRadians();

        _oldOdometrXLeft = odometrXLeft;
        _oldOdometrXRight = odometrXRight;
        _oldOdometrY = odometrY;

        _oldRotate = _gyro.GetRadians();

        ShiftPosition = new Vector2(deltaX * cos(_gyro.GetRadians()) + deltaY * sin(_gyro.GetRadians()),
                -(-deltaX * sin(_gyro.GetRadians()) + deltaY * cos(_gyro.GetRadians())));

        Position = Vector2.Plus(ShiftPosition, Position);

        _deltaTime.reset();
    }

    @Override
    public void Start() {
        _deltaTime.reset();

        Position = Bios.GetStartPosition().Position.clone();

        double odometrXLeft = _odometrs.GetOdometerXLeft(), odometrY = _odometrs.GetOdometerY(), odometrXRight = _odometrs.GetOdometerXRight();
        _oldOdometrXLeft = odometrXLeft;
        _oldOdometrXRight = odometrXRight;
        _oldOdometrY = odometrY;
    }
}