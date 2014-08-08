package com.ulap_research.weatherforecasterproject.Wifi3GSignalTool;
import android.content.Context;

/**
 * Created by naoya on 13/07/02.
 */
public class Sensor3GWifiManager {
    private static Sensor3GWifiManager instance = null;
    private Sensor3G sensor3G;
    private SensorWifi sensorWifi;

    /*
     * コンストラクタ
     */
    private Sensor3GWifiManager(Context context) {
        // 3Gセンサのインスタンス化
        this.sensor3G = new Sensor3G(context);

        // wifiセンサのインスタンス化
        this.sensorWifi = new SensorWifi(context);
    }

    /*
     * インスタンスが一つしか作られないことを保証する
     */
    public static Sensor3GWifiManager getInstance(Context context) {
        if (Sensor3GWifiManager.instance == null) Sensor3GWifiManager.instance = new Sensor3GWifiManager(context);
        return Sensor3GWifiManager.instance;
    }

    /*
     * 3Gセンサ値を返す
     */
    public int get3GASU() {
        return this.sensor3G.getASU();
    }

    /*
     * Wifiのセンサ値を返す
     */
    public int getWifiRssi() {
        return this.sensorWifi.getRssi();
    }
}
