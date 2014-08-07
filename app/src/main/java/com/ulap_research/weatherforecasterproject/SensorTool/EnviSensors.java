package com.ulap_research.weatherforecasterproject.SensorTool;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by LouieZamora on 7/30/13.
 */
public class EnviSensors implements SensorEventListener {
    private static final String TAG = "EnviSensors";

    private SensorManager enviSensorManager;
    private Sensor pressureSensor, humiditySensor, temperatureSensor, accelerometerSensor, gyroSensor,
                    orientationSensor, lightSensor, proximitySensor, magneticSensor;
    private Float pressureValue, altitudeValue, humidityValue, temperatureValue, lightValue, proximityValue;
    private Float accelerometerValueX, accelerometerValueY, accelerometerValueZ;
    private Float orientationAzimuthZ, orientationPitchX, orientationRollY;
    private Float magneticValueX, magneticValueY, magneticValueZ;

    /*
     * Constructor
     */
    public EnviSensors(Context context) {
        enviSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        humiditySensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        temperatureSensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        accelerometerSensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        orientationSensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        lightSensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        magneticSensor = enviSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    /*
     * Call to start listening, but how to stop? Implement stop method.
     */
        enviSensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener (this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener(this, lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        enviSensorManager.registerListener(this, magneticSensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public final void onAccuracyChanged(Sensor pressure, int accuracy){
        //do something
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_PRESSURE){
            this.pressureValue = event.values[0];
            this.altitudeValue = enviSensorManager.getAltitude(enviSensorManager.PRESSURE_STANDARD_ATMOSPHERE, this.pressureValue);
        }

        if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY){
            this.humidityValue = event.values[0];
        }

        if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            this.temperatureValue = event.values[0];
        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            this.accelerometerValueX = event.values[0];
            this.accelerometerValueY = event.values[1];
            this.accelerometerValueZ = event.values[2];
        }

        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            this.orientationAzimuthZ = event.values[0];
            this.orientationPitchX = event.values[1];
            this.orientationRollY = event.values[2];
        }

        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            this.lightValue = event.values[0];
        }

        if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            this.proximityValue = event.values[0];
        }

        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            this.magneticValueX = event.values[0];
            this.magneticValueY = event.values[1];
            this.magneticValueZ = event.values[2];
        }
    }

    public Float getEnviPressure() {
        return this.pressureValue;
    }

    public Integer getEnviAltitude() {
        if(this.altitudeValue == null) return null;
        else return this.altitudeValue.intValue();
    }

    public Float getEnviHumidity() {
        return this.humidityValue;
    }

    public Float getEnviTemperature() {
        return this.temperatureValue;
    }

    public Float getEnviLight() {
        return this.lightValue;
    }

    public Integer getOrientationZ() {
        if(this.orientationAzimuthZ == null) return null;
        else return this.orientationAzimuthZ.intValue();
    }

    public Integer getOrientationY() {
        if(this.orientationRollY == null) return null;
        else return this.orientationRollY.intValue();
    }

    public Integer getOrientationX() {
        if(this.orientationPitchX == null) return null;
        else return this.orientationPitchX.intValue();
    }

    public Integer getAccelerometerZ() {
        if(this.accelerometerValueZ == null) return null;
        else return this.accelerometerValueZ.intValue();
    }

    public Integer getAccelerometerY() {
        if(this.accelerometerValueY == null) return null;
        else return this.accelerometerValueY.intValue();
    }

    public Integer getAccelerometerX() {
        if(this.accelerometerValueX == null) return null;
        else return this.accelerometerValueX.intValue();
    }

    public Integer getMagnetismZ() {
        if(this.magneticValueZ == null) return null;
        else return this.magneticValueZ.intValue();
    }

    public Integer getMagnetismY() {
        if(this.magneticValueY == null) return null;
        else return this.magneticValueY.intValue();
    }

    public Integer getMagnetismX() {
        if(this.magneticValueX == null) return null;
        else return this.magneticValueX.intValue();
    }

    public Float getProximity() {
        return this.proximityValue;
    }

}
