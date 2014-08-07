package com.ulap_research.weatherforecasterproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.ulap_research.weatherforecasterproject.BestLocation.BestLocationListener;
import com.ulap_research.weatherforecasterproject.BestLocation.BestLocationProvider;
import com.ulap_research.weatherforecasterproject.Resources.SharedPrefResources;
import com.ulap_research.weatherforecasterproject.RestHelper.RestClient;
import com.ulap_research.weatherforecasterproject.RestHelper.RestResources;
import com.ulap_research.weatherforecasterproject.SensorTool.EnviSensorManager;
import com.ulap_research.weatherforecasterproject.Wifi3GSignalTool.Sensor3GWifiManager;

import org.json.JSONObject;

import java.sql.Timestamp;

public class UploadDataService extends Service {
    private static final String TAG = "UploadDataService";
    private static final int UPLOAD_NOTI_ID = 195;
    private static final int CONNECTION_ERROR_NOTI_ID = 196;

    private SharedPreferences sharedPref;

    private String latitude = null;
    private String longitude = null;

    private Handler mHandler;

    private EnviSensorManager enviSensorManager;
    private Sensor3GWifiManager sensor3GWifiManager;

    private BestLocationProvider mBestLocationProvider;
    private BestLocationListener mBestLocationListener;

    private Thread uploadDataThread;

    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // setup shared preferences
        sharedPref = this.getSharedPreferences(SharedPrefResources.PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        // set handler for thread
        mHandler = new Handler();

        // set object of sensor data reading class
        enviSensorManager = EnviSensorManager.getInstance(this.getApplicationContext());
        sensor3GWifiManager = Sensor3GWifiManager.getInstance(this.getApplicationContext());

        // enable GPS location
        initLocation();
        mBestLocationProvider.startLocationUpdatesWithListener(mBestLocationListener);

        // Start notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_sys_upload)
                        .setTicker(getString(R.string.upload_start_noti))
                        .setContentTitle(getString(R.string.upload_start_noti_title))
                        .setContentText(getString(R.string.upload_noti_open_app))
                        .setOngoing(true);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // set notification
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // start notification
        mNotificationManager.notify(UPLOAD_NOTI_ID, mBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Service started");

        // run thread to upload data every 1 sec
        uploadDataThread = new Thread()
        {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(1000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //UPLOAD DATA
                                uploadDataTask task = new uploadDataTask(
                                        new Timestamp(System.currentTimeMillis()).toString(),
                                        latitude + "", longitude + "",
                                        enviSensorManager.getEnviTemperature() + "",
                                        enviSensorManager.getEnviHumidity() + "",
                                        enviSensorManager.getEnviPressure() + "",
                                        enviSensorManager.getProximity() + "",
                                        enviSensorManager.getOrientationZ() + "",
                                        enviSensorManager.getOrientationY() + "",
                                        enviSensorManager.getOrientationX() + "",
                                        enviSensorManager.getAccelerationZ() + "",
                                        enviSensorManager.getAccelerationY() + "",
                                        enviSensorManager.getAccelerationX() + "",
                                        enviSensorManager.getMagnetismZ() + "",
                                        enviSensorManager.getMagnetismY() + "",
                                        enviSensorManager.getMagnetismX() + "",
                                        sensor3GWifiManager.get3GASU() + "",
                                        sensor3GWifiManager.getWifiRssi() + "",
                                        enviSensorManager.getEnviAltitude() + "",
                                        enviSensorManager.getEnviLight() + ""
                                );
                                task.execute((Void) null);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    // Restore the interrupted status
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        };
        uploadDataThread.start();

        // make sure the service will always run
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
        try {
            // dismiss notification
            mNotificationManager.cancel(UPLOAD_NOTI_ID);
            // stop upload thread
            uploadDataThread.interrupt();
            // stop GPS
            mBestLocationProvider.stopLocationUpdates();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void initLocation(){
        if(mBestLocationListener == null){
            mBestLocationListener = new BestLocationListener() {
                public void onStatusChanged(String provider, int status
                        , Bundle extras) { }
                public void onProviderEnabled(String provider) { }
                public void onProviderDisabled(String provider) { }
                public void onLocationUpdateTimeoutExceeded(BestLocationProvider.LocationType type) { }

                public void onLocationUpdate(Location location, BestLocationProvider.LocationType type
                        , boolean isFresh) {
                    latitude = location.getLatitude() + "";
                    longitude = location.getLongitude() + "";
                }
            };

            if(mBestLocationProvider == null){
                mBestLocationProvider = new BestLocationProvider(this.getApplicationContext()
                        , true, true, 10000, 1000, 2, 0);
            }
        }
    }

    /**
     * An asynchronous to upload data task
     */
    public class uploadDataTask extends AsyncTask<Void, Void, Boolean> {
        private String timestamp;
        private String latitude;
        private String longitude;
        private String temperature;
        private String humidity;
        private String pressure;
        private String proximity;
        private String orientationZ;
        private String orientationY;
        private String orientationX;
        private String accelerationZ;
        private String accelerationY;
        private String accelerationX;
        private String magnetismZ;
        private String magnetismY;
        private String magnetismX;
        private String threeGSignal;
        private String wifiSignal;
        private String altitude;
        private String light;


        public uploadDataTask(String timestamp, String latitude, String longitude, String temperature,
                              String humidity, String pressure, String proximity,
                              String orientationZ, String orientationY, String orientationX,
                              String accelerationZ, String accelerationY, String accelerationX,
                              String magnetismZ, String magnetismY, String magnetismX,
                              String threeGSignal, String wifiSignal, String altitude, String light) {
            // get all sensor data
            this.timestamp = timestamp;
            this.latitude = latitude;
            this.longitude = longitude;
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
            this.proximity = proximity;
            this.orientationZ = orientationZ;
            this.orientationY = orientationY;
            this.orientationX = orientationX;
            this.accelerationZ = accelerationZ;
            this.accelerationY = accelerationY;
            this.accelerationX = accelerationX;
            this.magnetismZ = magnetismZ;
            this.magnetismY = magnetismY;
            this.magnetismX = magnetismX;
            this.threeGSignal = threeGSignal;
            this.wifiSignal = wifiSignal;
            this.altitude = altitude;
            this.light = light;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String apiKey = sharedPref.getString(SharedPrefResources.PREFERENCE_KEY_APIKEY, "");

            // Upload data to the server using REST API
            RestClient client = new RestClient(RestResources.UPLOAD_SENSOR_DATA);
            client.addHeader("Authorization", apiKey);
            client.addParam("timestamp", timestamp);
            client.addParam("latitude", latitude);
            client.addParam("longitude", longitude);
            client.addParam("temperature", temperature);
            client.addParam("humidity", humidity);
            client.addParam("pressure", pressure);
            client.addParam("proximity", proximity);
            client.addParam("orientationZ", orientationZ);
            client.addParam("orientationY", orientationY);
            client.addParam("orientationX", orientationX);
            client.addParam("accelerationZ", accelerationZ);
            client.addParam("accelerationY", accelerationY);
            client.addParam("accelerationX", accelerationX);
            client.addParam("magnetismZ", magnetismZ);
            client.addParam("magnetismY", magnetismY);
            client.addParam("magnetismX", magnetismX);
            client.addParam("threeGSignal", threeGSignal);
            client.addParam("wifiSignal", wifiSignal);
            client.addParam("altitude", altitude);
            client.addParam("light", light);

            try {
                client.execute(RestClient.RequestMethod.POST);

                JSONObject jObject = new JSONObject(client.getResponse());
                Log.d(TAG, jObject.getString("message"));
                return jObject.getBoolean("error");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        protected void onPostExecute(Boolean error) {
            // if error stop uploading and show notification
            if(error) {
                sharedPref.edit().putBoolean(SharedPrefResources.PREFERENCE_KEY_UPLOAD_START, false).commit();
                showErrorNoti();
                stopSelf();
            }
        }
    }

    /**
     * Error notification when something went wrong
     */
    private void showErrorNoti(){
        // Start notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setTicker(getString(R.string.upload_fail_noti))
                        .setContentTitle(getString(R.string.upload_fail_noti))
                        .setContentText(getString(R.string.upload_fail_noti_desc))
                        .setVibrate(new long[]{0, 500});

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(CONNECTION_ERROR_NOTI_ID, mBuilder.build());
    }


}
