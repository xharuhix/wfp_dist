package com.ulap_research.weatherforecasterproject.SensorTool;

import android.content.Context;

public class EnviSensorManager {
    private static EnviSensorManager instance = null;
    private EnviSensors enviSensors;

    private EnviSensorManager(Context context){
        this.enviSensors = new EnviSensors(context);
    }

    public static EnviSensorManager getInstance(Context context) {
        if (EnviSensorManager.instance == null) EnviSensorManager.instance = new EnviSensorManager(context);
        return EnviSensorManager.instance;
    }

    public Float getEnviPressure() {
        return this.enviSensors.getEnviPressure();
    }

    public Integer getEnviAltitude() {
        return this.enviSensors.getEnviAltitude();
    }

    public Float getEnviHumidity() {
        return this.enviSensors.getEnviHumidity();
    }

    public Float getEnviTemperature() {
        return this.enviSensors.getEnviTemperature();
    }

    public Float getEnviLight() {
        return this.enviSensors.getEnviLight();
    }

    public Integer getOrientationZ() {
        return this.enviSensors.getOrientationZ();
    }

    public Integer getOrientationY() {
        return this.enviSensors.getOrientationY();
    }

    public Integer getOrientationX() {
        return this.enviSensors.getOrientationX();
    }

    public Integer getAccelerationZ() {
        return this.enviSensors.getAccelerometerZ();
    }

    public Integer getAccelerationY() {
        return this.enviSensors.getAccelerometerY();
    }

    public Integer getAccelerationX() {
        return this.enviSensors.getAccelerometerX();
    }

    public Integer getMagnetismZ() {
        return this.enviSensors.getMagnetismZ();
    }

    public Integer getMagnetismY() {
        return this.enviSensors.getMagnetismY();
    }

    public Integer getMagnetismX() {
        return this.enviSensors.getMagnetismX();
    }

    public Float getProximity() {
        return this.enviSensors.getProximity();
    }

}
