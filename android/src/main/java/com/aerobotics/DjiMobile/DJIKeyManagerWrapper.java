
package com.aerobotics.DjiMobile;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;


import android.support.annotation.Nullable;
import android.util.Log;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.keysdk.KeyManager;
import dji.keysdk.ProductKey;
import dji.keysdk.callback.KeyListener;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

public class DJIKeyManagerWrapper extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public DJIKeyManagerWrapper(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @ReactMethod
  public void startListeningForChangesOnKey(Promise promise) {
    Log.i("KEYLISTENER: ", ProductKey.CONNECTION);
    KeyManager.getInstance().addListener(ProductKey.create(ProductKey.CONNECTION), new KeyListener() {
      @Override
      public void onValueChange(@Nullable Object oldValue, @Nullable Object newValue) {
        Log.i("KEYLISTENER:", String.valueOf(newValue));
      }
    });
  }

  @Override
  public String getName() {
    return "DJIKeyManagerWrapper";
  }
}