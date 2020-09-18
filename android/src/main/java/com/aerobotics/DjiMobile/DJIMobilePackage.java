
package com.aerobotics.DjiMobile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

//import com.aerobotics.DjiMobile.DroneVideo.DroneVideoManager;

public class DJIMobilePackage implements ReactPackage {
  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    return Arrays.<NativeModule>asList(
      new DJIMobile(reactContext),
      new DJIMissionControlWrapper(reactContext),
      new CameraControlNative(reactContext),
      new DJIMedia(reactContext)
    );
  }

//  @Override
//  public List<Class<? extends JavaScriptModule>> createJSModules() {
//    return null;
//  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Arrays.<ViewManager>asList(
//      new DroneVideoManager()
    );  
  }
}