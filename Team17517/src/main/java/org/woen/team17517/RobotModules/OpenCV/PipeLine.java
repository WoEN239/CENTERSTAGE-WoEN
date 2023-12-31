package org.woen.team17517.RobotModules.OpenCV;


import static org.opencv.core.Core.*;
import static org.opencv.imgproc.Imgproc.*;

import android.graphics.Canvas;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

@Config
public class PipeLine implements VisionProcessor {
    double x = 640;
    double y = 480;
    double r1 = 5;
    double g1 = 69;
    double b1 = 206;
    double r2 = 60;
    double g2 = 129;
    double b2 = 296;
    Mat img_range_red = new Mat();
    Mat img_range_blue = new Mat();
    public static double hRedDown = 10;
    public static double cRedDown = 30;
    public static double vRedDowm = 110;
    public static double hRedUp = 30;
    public static double cRedUp = 255;
    public static double vRedUp = 255;

    public static double hBlueDown = 90;
    public static double cBlueDown = 53.8;
    public static double vBlueDowm = 95;
    public static double hBlueUp = 100;
    public static double cBlueUp = 255;
    public static double vBlueUp = 255;

    double x1Finish = x * 0.4;
    double x1Start = x * 0;
    double x2Finish = x * 0.7;
    double x2Start = x * 0.4;
    double x3Finish = x;
    double x3Start = x * 0.7;
    double centerOfRectX = 0;
    double centerOfRectY = 0;
    public int pos = 0;
    public boolean team = true;

    public void init(int width, int height, CameraCalibration calibration) {

    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {
        //
        cvtColor(frame, frame, COLOR_RGB2HSV);
        resize(frame, frame, new Size(x, y));

        new Mat(frame, new Rect(0,0,(int)x,(int)y/2)).copyTo(frame);


        inRange(frame, new Scalar(hRedDown, cRedDown, vRedDowm), new Scalar(hRedUp, cRedUp, vRedUp), img_range_red);
        inRange(frame, new Scalar(hBlueDown, cBlueDown, vBlueDowm), new Scalar(hBlueUp, cBlueUp, vBlueUp), img_range_blue);

        Core.bitwise_or(img_range_red,img_range_blue,frame);

        Rect boundingRect = boundingRect(frame);

        centerOfRectX = boundingRect.x + boundingRect.width / 2.0;
        centerOfRectY = boundingRect.y + boundingRect.height / 2.0;

        if (centerOfRectX < x1Finish && centerOfRectX > x1Start) {
            pos = 1;
        }
        if (centerOfRectX < x2Finish && centerOfRectX > x2Start) {
            pos = 2;
        }
        if (centerOfRectX < x3Finish && centerOfRectX > x3Start) {
            pos = 3;
        }

        return frame;
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {

    }
}