package org.woen.team18742.Modules.DriveTrain;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.woen.team18742.Collectors.BaseCollector;
import org.woen.team18742.Modules.Gyroscope;
import org.woen.team18742.Modules.Manager.IRobotModule;
import org.woen.team18742.Modules.Manager.Module;
import org.woen.team18742.Modules.Manager.TeleopModule;
import org.woen.team18742.Modules.Odometry.OdometryHandler;
import org.woen.team18742.Tools.Color;
import org.woen.team18742.Tools.Configs.Configs;
import org.woen.team18742.Tools.ToolTelemetry;
import org.woen.team18742.Tools.Vector2;

@TeleopModule
public class CrashDefend implements IRobotModule {
    private OdometryHandler _odometry;
    private Gyroscope _gyro;
    private Drivetrain _driveTrain;

    private Square[] _zones = new Square[]{
            new Square(Vector2.ZERO, new Vector2(50, 50))
    };

    private final ElapsedTime _deltaTime = new ElapsedTime();

    private boolean _isDisable = false;

    public void Disable(){
        _isDisable = true;
    }

    public void Enable(){
        _isDisable = false;
    }

    @Override
    public void Init(BaseCollector collector) {
        _odometry = collector.GetModule(OdometryHandler.class);
        _gyro = collector.GetModule(Gyroscope.class);
        _driveTrain = collector.GetModule(Drivetrain.class);
    }

    @Override
    public void LastUpdate() {
        if(false) {
            Square robot = new Square(
                    Vector2.Plus(_odometry.Position,
                            new Vector2(Configs.DriveTrainWheels.WheelsRadius, Configs.DriveTrainWheels.WheelsRadius).Turn(_gyro.GetRadians())),
                    Vector2.Plus(_odometry.Position,
                            new Vector2(Configs.DriveTrainWheels.WheelsRadius, -Configs.DriveTrainWheels.WheelsRadius).Turn(_gyro.GetRadians())),
                    Vector2.Plus(_odometry.Position,
                            new Vector2(-Configs.DriveTrainWheels.WheelsRadius, -Configs.DriveTrainWheels.WheelsRadius).Turn(_gyro.GetRadians())),
                    Vector2.Plus(_odometry.Position,
                            new Vector2(-Configs.DriveTrainWheels.WheelsRadius, Configs.DriveTrainWheels.WheelsRadius).Turn(_gyro.GetRadians()))
            );

            ToolTelemetry.DrawPolygon(robot.GetPoints(), Color.BLUE);

            Vector2 shift = new Vector2(_odometry.Speed.X * _deltaTime.seconds(), _odometry.Speed.Y * _deltaTime.seconds());

            Square nextSquare = new Square(Vector2.Plus(shift, robot.p1),
                    Vector2.Plus(shift, robot.p2),
                    Vector2.Plus(shift, robot.p3),
                    Vector2.Plus(shift, robot.p4));

            for (Square i : _zones) {
                if (IsContains(i, nextSquare)) {
                    ToolTelemetry.DrawPolygon(i.GetPoints(), Color.RED);
                    _driveTrain.Stop();
                } else
                    ToolTelemetry.DrawPolygon(i.GetPoints(), Color.ORANGE);
            }
        }

        _deltaTime.reset();
    }

    private static boolean IsContains(Square s1, Square s2) {
        for (Line i : s1.GetLines()) {
            for (Line j : s2.GetLines()) {
                Vector2 point = new Vector2();

                if (i.isContains(j, point))
                    return true;
            }
        }

        return false;
    }

    private class Line {
        public Vector2 p1, p2;

        public Line(Vector2 p1, Vector2 p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        public boolean isContains(Line l, Vector2 point) {
            double parallelism = (p1.X - p2.X) * (l.p1.Y - l.p2.Y) - (p1.Y - p2.Y) * (l.p1.X - l.p2.X);

            if (Math.abs(parallelism) < 0.01d)
                return false;

            point = new Vector2(((p1.X * p2.Y - p1.Y * p2.X) * (l.p1.X - l.p2.X) - (p1.X - p2.X) * (l.p1.X * l.p2.Y - l.p1.Y * l.p2.X)) / parallelism,
                    ((p1.X * p2.Y - p1.Y * p2.X) * (l.p1.Y - l.p2.Y) - (p1.Y - p2.Y) * (l.p1.X * l.p2.Y - l.p1.Y * l.p2.Y)) / parallelism);

            if (p1.X <= point.X && point.X <= p2.X && p1.Y <= point.Y && point.Y <= p2.Y)
                return true;

            return false;
        }
    }

    private class Square {
        public Vector2 p1, p2, p3, p4;

        public Square(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.p4 = p4;
        }

        public Square(Vector2 pos, Vector2 size) {
            p1 = Vector2.Plus(pos, Vector2.Multiply(size, new Vector2(0.5, 0.5)));
            p2 = Vector2.Plus(pos, Vector2.Multiply(size, new Vector2(0.5, -0.5)));
            p3 = Vector2.Plus(pos, Vector2.Multiply(size, new Vector2(-0.5, -0.5)));
            p4 = Vector2.Plus(pos, Vector2.Multiply(size, new Vector2(-0.5, 0.5)));
        }

        public Vector2[] GetPoints() {
            return new Vector2[]{
                    p1,
                    p2,
                    p3,
                    p4
            };
        }

        public Line[] GetLines() {
            return new Line[]{
                    new Line(p1, p2),
                    new Line(p2, p3),
                    new Line(p3, p4),
                    new Line(p4, p1)
            };
        }
    }

    @Override
    public void Start() {
        _deltaTime.reset();
    }
}
