package com.ulap_research.weatherforecasterproject.Wifi3GSignalTool;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * Created by naoya on 13/07/02.
 */
class Sensor3G extends PhoneStateListener {
    private int asu = Integer.MIN_VALUE;

    /*
     * コンストラクタ(自動的に開始する)
     */
    public Sensor3G(Context context) {
        /*
         * TelephonyManagerの取得
         */
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        /*
         * 記録(電波強度)の開始
         */
        tm.listen(this, PhoneStateListener.LISTEN_SIGNAL_STRENGTH);
    }

    /*
     * 3G回線の電波強度が変化した時に呼ばれるコールバック
     * (注)DOCOMO, Softbankで動くが、通信方式が異なるためAuでは動かない
     */
    @Override
    public void onSignalStrengthChanged(int asu) {
        this.asu = asu-116;
        Log.v("signal3g", String.format("asu=%d", asu));
    }


    /*
     * asuのデータを返すメソッド
     */
    public int getASU() {
        return this.asu;
    }
}
