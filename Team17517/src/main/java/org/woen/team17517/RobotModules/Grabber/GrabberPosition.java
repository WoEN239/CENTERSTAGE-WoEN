package org.woen.team17517.RobotModules.Grabber;

import com.acmerobotics.dashboard.config.Config;

@Config
public enum GrabberPosition {
    FINISH(0.46),SAFE(1),DOWN(1);
    public double value;
    GrabberPosition(double value){this.value = value;}
}