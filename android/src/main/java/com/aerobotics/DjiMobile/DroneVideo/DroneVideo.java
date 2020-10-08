package com.aerobotics.DjiMobile.DroneVideo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import android.os.Environment;

import com.aerobotics.DjiMobile.R;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.sdkmanager.DJISDKManager;

import dji.thirdparty.afinal.core.AsyncTask;

// Based on:
// https://github.com/DJI-Mobile-SDK-Tutorials/Android-VideoStreamDecodingSample/blob/7893630bbb133f6c9b22cf6020b411325223e1b1/android-videostreamdecodingsample/app/src/main/java/com/dji/videostreamdecodingsample/MainActivity.java#L611

public class DroneVideo extends RelativeLayout implements TextureView.SurfaceTextureListener, DJICodecManager.YuvDataCallback {
  private DJICodecManager codecManager;
  private int count; //for sampling FPS

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
    // This will pause the live view while we capture
    codecManager.enabledYuvData(true);
    codecManager.setYuvDataCallback(this);


    // TODO: RESET after capture to reenable live view
//    codecManager.enabledYuvData(false);
 //   mCodecManager.setYuvDataCallback(null);
  };


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

    @Override
    public void onYuvDataReceived(final MediaFormat format, final ByteBuffer yuvFrame, int dataSize, final int width, final int height) {
        //In this demo, we test the YUV data by saving it into JPG files.
        //DJILog.d(TAG, "onYuvDataReceived " + dataSize);
        if (count++ % 30 == 0 && yuvFrame != null) {
            final byte[] bytes = new byte[dataSize];
            yuvFrame.get(bytes);
            //DJILog.d(TAG, "onYuvDataReceived2 " + dataSize);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    // two samples here, it may has other color format.
                    int colorFormat = format.getInteger(MediaFormat.KEY_COLOR_FORMAT);
                    switch (colorFormat) {
                        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                        //Only on Android >=24
                            //NV12
                            newSaveYuvDataToJPEG(bytes, width, height);
                            break;
                        case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                            //YUV420P
                            newSaveYuvDataToJPEG420P(bytes, width, height);
                            break;
                        default:
                            break;
                    }

                }
            });
        }
    }
  

  private void newSaveYuvDataToJPEG(byte[] yuvFrame, int width, int height){
    if (yuvFrame.length < width * height) {
        //DJILog.d(TAG, "yuvFrame size is too small " + yuvFrame.length);
        return;
    }
    int length = width * height;

    byte[] u = new byte[width * height / 4];
    byte[] v = new byte[width * height / 4];
    for (int i = 0; i < u.length; i++) {
        v[i] = yuvFrame[length + 2 * i];
        u[i] = yuvFrame[length + 2 * i + 1];
    }
    for (int i = 0; i < u.length; i++) {
        yuvFrame[length + 2 * i] = u[i];
        yuvFrame[length + 2 * i + 1] = v[i];
    }
    screenShot(yuvFrame,Environment.getExternalStorageDirectory() + "/DJI_ScreenShot", width, height);
  }

  private void newSaveYuvDataToJPEG420P(byte[] yuvFrame, int width, int height) {
    if (yuvFrame.length < width * height) {
        return;
    }
    int length = width * height;

    byte[] u = new byte[width * height / 4];
    byte[] v = new byte[width * height / 4];

    for (int i = 0; i < u.length; i ++) {
        u[i] = yuvFrame[length + i];
        v[i] = yuvFrame[length + u.length + i];
    }
    for (int i = 0; i < u.length; i++) {
        yuvFrame[length + 2 * i] = v[i];
        yuvFrame[length + 2 * i + 1] = u[i];
    }
    screenShot(yuvFrame, Environment.getExternalStorageDirectory() + "/DJI_ScreenShot", width, height);
  }
      /**
     * Save the buffered data into a JPG image file
     */
    private void screenShot(byte[] buf, String shotDir, int width, int height) {
      File dir = new File(shotDir);
      if (!dir.exists() || !dir.isDirectory()) {
          dir.mkdirs();
      }
      YuvImage yuvImage = new YuvImage(buf,
              ImageFormat.NV21,
              width,
              height,
              null);
      OutputStream outputFile;
      final String path = dir + "/ScreenShot_" + System.currentTimeMillis() + ".jpg";
      try {
          outputFile = new FileOutputStream(new File(path));
      } catch (FileNotFoundException e) {
          Log.e("REACT", "test screenShot: new bitmap output file error: " + e);
          return;
      }
      if (outputFile != null) {
          yuvImage.compressToJpeg(new Rect(0,
                  0,
                  width,
                  height), 100, outputFile);
      }
      try {
          outputFile.close();
      } catch (IOException e) {
          Log.e("REACT", "test screenShot: compress yuv image error: " + e);
          e.printStackTrace();
      }
/*      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              displayPath(path);
          }
      });*/
  }
}
