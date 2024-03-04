// Generated by view binder compiler. Do not edit!
package com.learn.machinelearningandroid.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.learn.machinelearningandroid.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btnCustomView;

  @NonNull
  public final Button btnImageClassification;

  @NonNull
  public final Button btnImageClassificationMediaPipe;

  @NonNull
  public final Button btnImageClassificationTflite;

  @NonNull
  public final Button btnMlkitRecognition;

  @NonNull
  public final Button btnObjectDetectionTflite;

  @NonNull
  public final Button btnPredictionTflite;

  private ActivityMainBinding(@NonNull LinearLayout rootView, @NonNull Button btnCustomView,
      @NonNull Button btnImageClassification, @NonNull Button btnImageClassificationMediaPipe,
      @NonNull Button btnImageClassificationTflite, @NonNull Button btnMlkitRecognition,
      @NonNull Button btnObjectDetectionTflite, @NonNull Button btnPredictionTflite) {
    this.rootView = rootView;
    this.btnCustomView = btnCustomView;
    this.btnImageClassification = btnImageClassification;
    this.btnImageClassificationMediaPipe = btnImageClassificationMediaPipe;
    this.btnImageClassificationTflite = btnImageClassificationTflite;
    this.btnMlkitRecognition = btnMlkitRecognition;
    this.btnObjectDetectionTflite = btnObjectDetectionTflite;
    this.btnPredictionTflite = btnPredictionTflite;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_custom_view;
      Button btnCustomView = ViewBindings.findChildViewById(rootView, id);
      if (btnCustomView == null) {
        break missingId;
      }

      id = R.id.btn_image_classification;
      Button btnImageClassification = ViewBindings.findChildViewById(rootView, id);
      if (btnImageClassification == null) {
        break missingId;
      }

      id = R.id.btn_image_classification_media_pipe;
      Button btnImageClassificationMediaPipe = ViewBindings.findChildViewById(rootView, id);
      if (btnImageClassificationMediaPipe == null) {
        break missingId;
      }

      id = R.id.btn_image_classification_tflite;
      Button btnImageClassificationTflite = ViewBindings.findChildViewById(rootView, id);
      if (btnImageClassificationTflite == null) {
        break missingId;
      }

      id = R.id.btn_mlkit_recognition;
      Button btnMlkitRecognition = ViewBindings.findChildViewById(rootView, id);
      if (btnMlkitRecognition == null) {
        break missingId;
      }

      id = R.id.btn_object_detection_tflite;
      Button btnObjectDetectionTflite = ViewBindings.findChildViewById(rootView, id);
      if (btnObjectDetectionTflite == null) {
        break missingId;
      }

      id = R.id.btn_prediction_tflite;
      Button btnPredictionTflite = ViewBindings.findChildViewById(rootView, id);
      if (btnPredictionTflite == null) {
        break missingId;
      }

      return new ActivityMainBinding((LinearLayout) rootView, btnCustomView, btnImageClassification,
          btnImageClassificationMediaPipe, btnImageClassificationTflite, btnMlkitRecognition,
          btnObjectDetectionTflite, btnPredictionTflite);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
