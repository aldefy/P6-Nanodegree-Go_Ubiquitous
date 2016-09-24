package techgravy.sunshine.sync;

/**
 * Created by aditlal on 24/09/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

import techgravy.sunshine.utils.logger.Logger;

/**
 * This is a Service that listens and send messages to the wearable device
 */
public class WearableDataService
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private GoogleApiClient mGoogleApiClient;

    public static String WEATHER_DATA_PATH = "/today-weather-data";

    public WearableDataService(Context context){

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();

    }

    public void onNotifyWearable(int weatherId, String high, String low){

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WEATHER_DATA_PATH);
        putDataMapRequest.getDataMap().putInt("weatherId",weatherId);
        putDataMapRequest.getDataMap().putString("high",high);
        putDataMapRequest.getDataMap().putString("low",low);
        putDataMapRequest.getDataMap().putLong("time", new Date().getTime());

        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient,putDataRequest)
                .setResultCallback(dataItemResult -> {
                    if (!dataItemResult.getStatus().isSuccess()){
                        Logger.t("onNotifyWearable").i( "Error sending weather data");
                    }else{
                        Logger.t("onNotifyWearable").i( "Success sending weather data");
                    }
                });



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}