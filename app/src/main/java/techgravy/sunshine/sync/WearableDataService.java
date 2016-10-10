package techgravy.sunshine.sync;

/**
 * Created by aditlal on 24/09/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import techgravy.sunshine.utils.logger.Logger;

/**
 * This is a Service that listens and send messages to the wearable device
 */
public class WearableDataService extends WearableListenerService
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks , DataApi.DataListener {
    private static final String TAG = "onNotifyWearable";
    private GoogleApiClient mGoogleApiClient;
    private int weatherId = 0;
    private String tempH, tempL, location;

    public static String WEATHER_DATA_PATH = "/weather-update";


    public WearableDataService(Context context) {

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

    }

    public void onNotifyWearable(int weatherId, String high, String low) {
        this.weatherId = weatherId;
        this.tempH = high;
        this.tempL = low;

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (!tempH.isEmpty() && !tempL.isEmpty() && weatherId != 0) {
            PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WEATHER_DATA_PATH);
            putDataMapRequest.setUrgent();
            putDataMapRequest.getDataMap().putInt("weatherId", weatherId);
            putDataMapRequest.getDataMap().putString("max-temp", tempH);
            putDataMapRequest.getDataMap().putString("min-temp", tempL);
            putDataMapRequest.getDataMap().putString("location", "");
            putDataMapRequest.getDataMap().putLong("time", System.currentTimeMillis());

            PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
            Logger.t("onNotifyWearable").i("GOogle client " + mGoogleApiClient.isConnected());
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest)
                    .setResultCallback(dataItemResult -> {
                        if (!dataItemResult.getStatus().isSuccess()) {
                            Logger.t("onNotifyWearable").i("Error sending weather data");
                        } else {
                            Logger.t("onNotifyWearable").i("Success sending weather data "+ dataItemResult.toString());
                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);

        String path = messageEvent.getPath();
        Log.d(TAG, "message path: " + path);

        // Check to see if the message is a request for the updated weather conditions
        if (path.equals("/weather-update")) {
            // start the service sending the updated weather condition to the wearable
            Context context = this.getApplicationContext();
            SunshineSyncAdapter.syncImmediately(context);
        }
    }
}