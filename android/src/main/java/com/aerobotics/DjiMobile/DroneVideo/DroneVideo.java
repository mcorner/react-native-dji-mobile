package com.aerobotics.DjiMobile.DroneVideo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import com.aerobotics.DjiMobile.R;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.sdkmanager.DJISDKManager;

public class DroneVideo extends RelativeLayout implements TextureView.SurfaceTextureListener {

  private DJICodecManager codecManager;

  public DroneVideo(Context context) {
    super(context);
    initializeSurfaceTexture();
  }

  private void initializeSurfaceTexture() {
    View.inflate(getContext(), R.layout.drone_video_layout, this);
    TextureView droneVideoTexture = findViewById(R.id.droneVideoTexture);
    droneVideoTexture.setSurfaceTextureListener(this);
    if (DJISDKManager.getInstance().hasSDKRegistered()) {
      VideoFeeder.getInstance().getPrimaryVideoFeed().addVideoDataListener(new VideoFeeder.VideoDataListener() {
        @Override
        public void onReceive(byte[] buffer, int size) {
          if (codecManager != null) {
            codecManager.sendDataToDecoder(buffer, size);
          }
        }
      });
    }
  }

  private void cleanUpVideoFeed() {
    VideoFeeder.getInstance().getPrimaryVideoFeed().destroy();
    if (codecManager != null) {
      codecManager.destroyCodec();
    }
  }

  @Override
  public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
    if (codecManager == null) {
      codecManager = new DJICodecManager(getContext(), surface, width, height);
    }
  }

  @Override
  public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    codecManager.cleanSurface();
    codecManager = new DJICodecManager(getContext(), surface, width, height);
  }

  @Override
  public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
    codecManager.destroyCodec();
    return false;
  }

  @Override
  public void onSurfaceTextureUpdated(SurfaceTexture surface) {

  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    cleanUpVideoFeed();
  }

  /**
    * save the signature to an sd card directory
  */
  public void saveImage() {
    Log.d("DJIMobile", "Save file-======");
    // TODO!!!
    return;

/*    String root = Environment.getExternalStorageDirectory().toString();

    // the directory where the signature will be saved
    File myDir = new File(root + "/images");

    // make the directory if it does not exist yet
    if (!myDir.exists()) {
      myDir.mkdirs();
    }

    // set the file name of your choice
    String fname = "capture.png";

    try {
      Log.d("React Signature", "Save file-======:" + saveFileInExtStorage);
      // save the signature
      if (saveFileInExtStorage) {
        FileOutputStream out = new FileOutputStream(file);

        ///???


        WritableMap event = Arguments.createMap();
        event.putString("pathName", file.getAbsolutePath());
        event.putString("encoded", encoded);
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "topChange", event);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }*/
  }
}
