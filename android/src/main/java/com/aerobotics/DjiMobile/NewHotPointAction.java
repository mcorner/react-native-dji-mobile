package com.aerobotics.DjiMobile;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.mission.hotpoint.HotpointHeading;
import dji.common.mission.hotpoint.HotpointMission;
import dji.common.mission.hotpoint.HotpointMissionEvent;
import dji.common.mission.hotpoint.HotpointMissionState;
import dji.common.mission.hotpoint.HotpointStartPoint;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.midware.data.model.P3.DataFlycHotPointMissionSwitch;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.hotpoint.HotpointMissionOperatorListener;
import dji.sdk.mission.timeline.TimelineElement;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


public class NewHotpointAction extends TimelineElement implements HotpointMissionOperatorListener, DroneStatusListener {
    private MissionControl missionControl;
    private AuthVideoApplication authVideoApplication;
    private HotpointMission mission;
    private int angle;
    private boolean isRunning;

    NewHotpointAction (HotpointMission mission, int absoluteAngle){
        this.mission = mission;
        this.angle = absoluteAngle;
        this.missionControl = MissionControl.getInstance();
        this.isRunning = false;
        authVideoApplication = MApplication.getInstance().authVideoApplication;
    }

    public void run(){
        System.out.println("NewHotPoint run");
        System.out.println("NewHotPoint hpo state: " + missionControl.getHotpointMissionOperator().getCurrentState());

        missionControl.getHotpointMissionOperator().addListener(this);
        NewHotpointAction that = this;
        missionControl.getHotpointMissionOperator().startMission(mission, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    System.out.println("NewHotPoint hotpoint mission started");
                    isRunning = true;
                    authVideoApplication.addDroneStatusListener(that);

                } else{
                    System.out.println("NewHotPoint hotpoint mission start error: " + djiError);
                }
            }
        });
    }

    public void updateDroneStatus(DroneStatus d){
        LocationCoordinate2D center = mission.getHotpoint();
        LatLng centerLL = new LatLng(center.getLatitude(), center.getLongitude());

        double currentHeading = (((int)SphericalUtil.computeHeading(centerLL, new LatLng(d.lat,d.lng))) + 360) % 360;
        double diff = Math.abs((int)currentHeading-this.angle);

        System.out.println("Hotpoint current heading: " + currentHeading + " target angle: " + this.angle + " diff: " + diff);

        if ((diff < 15.0) && this.isRunning){
            this.stop();
        }
    }


    public void stop() {
        System.out.println("NewHotPoint stop");
        this.isRunning = false;
        NewHotpointAction that = this;

        missionControl.getHotpointMissionOperator().stop(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    System.out.println("NewHotPoint hotpoint mission stop completed");
                } else{
                    System.out.println("NewHotPoint hotpoint mission stop error: " + djiError);
                    authVideoApplication.removeDroneStatusListener(that);
                }
            }
        });
    }

    public DJIError checkValidity(){
        return null;
    }

    public void finishRun(DJIError e){
        return;
    }

    public boolean isPausable(){
        return false;
    }

    public void onExecutionUpdate(HotpointMissionEvent event){
        System.out.println("onExecutionUpdate: " + event);
    }

    public void onExecutionStart(){
        missionControl.onStart(this);
    }

    public void onExecutionFinish(DJIError error){
        if (error != null){
            System.out.println("NewHotPoint hotpoint onExecutionFinish ERROR: " + error);

        } else{
            System.out.println("NewHotPoint hotpoint onExecutionFinish");
        }

        authVideoApplication.removeDroneStatusListener(this);
        missionControl.getHotpointMissionOperator().removeListener(this);

        missionControl.onFinishWithError(this, error);
    }

}
