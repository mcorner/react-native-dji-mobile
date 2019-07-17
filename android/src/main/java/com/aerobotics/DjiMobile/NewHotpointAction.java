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

import dji.common.error.DJIError;
import dji.common.gimbal.Attitude;
import dji.common.product.Model;
import dji.keysdk.CameraKey;
import dji.keysdk.FlightControllerKey;
import dji.keysdk.GimbalKey;
import dji.keysdk.KeyManager;
import dji.keysdk.ProductKey;
import dji.keysdk.callback.GetCallback;
import dji.keysdk.callback.KeyListener;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


public class NewHotpointAction extends TimelineElement implements HotpointMissionOperatorListener {
    private MissionControl missionControl;
    private HotpointMission mission;
    private int angle;
    private boolean isRunning;
    private double latitude=0.0, longitude=0.0;

    private FlightControllerKey aircraftLatitudeKey = FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LATITUDE);
    private FlightControllerKey aircraftLongitudeKey = FlightControllerKey.create(FlightControllerKey.AIRCRAFT_LOCATION_LONGITUDE);

    NewHotpointAction (HotpointMission mission, int absoluteAngle){
        this.mission = mission;
        this.angle = absoluteAngle;
        this.missionControl = MissionControl.getInstance();
        this.isRunning = false;
    }


    private KeyListener aircraftLatitudeListener = new KeyListener() {
        @Override
        public void onValueChange(Object oldValue, Object newValue) {
            if (newValue instanceof Double) {
              latitude = (Double)newValue;
              if (longitude != 0.0){
                updateDroneStatus();
              }
            }
        }
    };

    private KeyListener aircraftLongitudeListener = new KeyListener() {
        @Override
        public void onValueChange(Object oldValue, Object newValue) {
            if (newValue instanceof Double) {
              longitude = (Double)newValue;
              if (latitude != 0.0){
                updateDroneStatus();
              }
            }
        }
    };

    public void run(){
        System.out.println("dronecha NewHotPoint hpo state: " + missionControl.getHotpointMissionOperator().getCurrentState());

        missionControl.getHotpointMissionOperator().addListener(this);
        NewHotpointAction that = this;
        missionControl.getHotpointMissionOperator().startMission(mission, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    System.out.println("NewHotPoint hotpoint mission started");
                    isRunning = true;
                    KeyManager.getInstance().addListener(aircraftLatitudeKey, aircraftLatitudeListener);
                    KeyManager.getInstance().addListener(aircraftLongitudeKey, aircraftLongitudeListener);
                } else{
                    System.out.println("NewHotPoint hotpoint mission start error: " + djiError);
                }
            }
        });
    }

    public void updateDroneStatus(){
        LocationCoordinate2D center = mission.getHotpoint();
        LatLng centerLL = new LatLng(center.getLatitude(), center.getLongitude());

        double currentHeading = (((int)SphericalUtil.computeHeading(centerLL, new LatLng(latitude,longitude))) + 360) % 360;
        double diff = Math.abs((int)currentHeading-this.angle);

        System.out.println("NewHotpoint current heading: " + currentHeading + " target angle: " + this.angle + " diff: " + diff);

        if ((diff < 10.0) && this.isRunning){
          this.stop();
        }
    }


    public void stop() {
        System.out.println("NewHotpoint stop");
        this.isRunning = false;
        NewHotpointAction that = this;

        missionControl.getHotpointMissionOperator().stop(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError == null) {
                    System.out.println("NewHotPoint hotpoint mission stop completed");
                } else{
                    System.out.println("NewHotPoint hotpoint mission stop error: " + djiError);
                    KeyManager.getInstance().removeListener(aircraftLatitudeListener);
                    KeyManager.getInstance().removeListener(aircraftLongitudeListener);
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
        KeyManager.getInstance().removeListener(aircraftLatitudeListener);
        KeyManager.getInstance().removeListener(aircraftLongitudeListener);
        missionControl.getHotpointMissionOperator().removeListener(this);

        missionControl.onFinishWithError(this, error);
    }

}
