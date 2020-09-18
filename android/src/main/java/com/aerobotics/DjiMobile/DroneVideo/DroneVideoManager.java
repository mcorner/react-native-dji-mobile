package com.aerobotics.DjiMobile.DroneVideo;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import com.aerobotics.DjiMobile.DJIMobile;

public class DroneVideoManager extends SimpleViewManager<DroneVideo> {

  public static final int COMMAND_SAVE_IMAGE = 1;

  @Override
  public String getName() {
    return "DroneVideo";
  }

  @Override
  protected DroneVideo createViewInstance(ThemedReactContext reactContext) {
    DroneVideo view = new DroneVideo(reactContext);

    DJIMobile mobileModule = context.getNativeModule(DJIMobile.class);
    mobileModule.setVideo(view);

    return view;
  }
}