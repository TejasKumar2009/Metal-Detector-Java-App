package com.example.metaldetector;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Button detect_metal_btn;
    TextView metal_detect_text, metal_detect_text2, metal_detected_txt;
    boolean isMetalDetectionOn = false;
//    boolean playingOrNot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detect_metal_btn = findViewById(R.id.detect_metal_btn);
        metal_detect_text = findViewById(R.id.metal_detect_text);
        metal_detect_text2 = findViewById(R.id.metal_detect_text2);
        metal_detected_txt = findViewById(R.id.metal_detected_txt);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mmSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        detect_metal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMetalDetectionOn) {
                    detect_metal_btn.setText("Stop Detecting Metal");
                    if (mmSensor != null) {
                        sensorManager.registerListener(MainActivity.this, mmSensor, SensorManager.SENSOR_DELAY_NORMAL);
                        isMetalDetectionOn = true;
                    } else {
                        Toast.makeText(MainActivity.this, "Sorry ! Required sensors are not available in your phone.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    detect_metal_btn.setText("Start Detecting Metal");
                    if (mmSensor != null) {
                        sensorManager.unregisterListener(MainActivity.this, mmSensor);
                        isMetalDetectionOn = false;
                    } else {
                        Toast.makeText(MainActivity.this, "Sorry ! Required sensors are not available in your phone.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float[] values = sensorEvent.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];

        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);

        float tiltAngle = (float) Math.atan(z / Math.sqrt(x * x + y * y));
        float azimuthAngle = (float) Math.atan(y / x);
        String direction = tiltAngle+", "+azimuthAngle;

        if (tiltAngle>0 && y>0){
            direction = "The object is ABOVE and NORTH of the sensor.";
        }
        else if (tiltAngle>0 && y<0){
            direction = "The object is ABOVE and SOUTH of the sensor.";
        }
        else if (tiltAngle<0 && y<0){
            direction = "The object is BELOW and NORTH of the sensor.";
        }
        else if (tiltAngle<0 && y>0) {
            direction = "The object is BELOW and SOUTH of the sensor.";
        }

        metal_detect_text2.setText(direction);

        float threshold_value = 50.00f;
        float threshold_value2 = 70.00f;
        float threshold_value3 = 100.00f;

        float magneticFieldStrength = (float) Math.sqrt(x * x + y * y + z * z);
        metal_detect_text.setText(String.valueOf(Math.round(magneticFieldStrength)));

        if (magneticFieldStrength >= threshold_value3){
            metal_detected_txt.setText("Metal Detected !!!");
            metal_detected_txt.setTextColor(Color.rgb(235, 52, 52));
            metal_detect_text.setTextColor(Color.rgb(235, 52, 52));
//            if (!playingOrNot){
                mp.start();
//                playingOrNot = true;
//            }
        } else if (magneticFieldStrength >= threshold_value2){
            metal_detected_txt.setText("Metal Detected !!");
            metal_detected_txt.setTextColor(Color.rgb(235, 162, 52));
            metal_detect_text.setTextColor(Color.rgb(235, 162, 52));
//            if (!playingOrNot){
                mp.start();
//                playingOrNot = true;
//            }
        } else if (magneticFieldStrength >= threshold_value) {
            metal_detected_txt.setText("Metal Detected !");
            metal_detected_txt.setTextColor(Color.rgb(235, 217, 52));
            metal_detect_text.setTextColor(Color.rgb(235, 217, 52));
//            if (!playingOrNot){
            mp.start();
//                playingOrNot = true;
//              }
        } else{
            mp.stop();
//            playingOrNot = false;
            metal_detected_txt.setText("");
            metal_detected_txt.setTextColor(Color.rgb(255, 255, 255));
            metal_detect_text.setTextColor(Color.rgb(255, 255, 255));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}