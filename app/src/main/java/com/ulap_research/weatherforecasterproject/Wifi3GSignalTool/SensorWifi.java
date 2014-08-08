package com.ulap_research.weatherforecasterproject.Wifi3GSignalTool;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by naoya on 13/07/02.
 */
public class SensorWifi {
    private WifiManager wm;
    /*
     * コンストラクタ
     */
    public SensorWifi(Context context) {
        /*
         * wifi managerの格納
         */
        this.wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

    }

    /*
     * 電波強度を返す
     */
    public int getRssi() {
        WifiInfo data = this.wm.getConnectionInfo();
        return data.getRssi();
    }
}
