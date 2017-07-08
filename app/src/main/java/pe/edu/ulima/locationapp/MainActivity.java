package pe.edu.ulima.locationapp;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    GoogleApiClient mGoogleApiCliente;
    LocationRequest mLocationRequest;

    private boolean estaUbicando = false;
    private TextView tviLocalizacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tviLocalizacion = (TextView) findViewById(R.id.tviLocalizacion);

        if (mGoogleApiCliente == null) {
            mGoogleApiCliente =
                    new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addApi(LocationServices.API)
                            .addOnConnectionFailedListener(this)
                            .build();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("ERROR", connectionResult.getErrorMessage());

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiCliente.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiCliente.disconnect();
        super.onStop();
    }

    public Location getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 333);
            }
        }
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 333) {
            Location location = getLastLocation();
            Log.i("LocationApp", String.valueOf(location.getLatitude()));
            Log.i("LocationApp", String.valueOf(location.getLongitude()));
        }
    }

    private void createLocationRequest() {
        // Crear una peticion de localizacion
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
    }

    public void localizar(View view) {
        Location location = getLastLocation();
        Log.i("LocationApp", String.valueOf(location.getLatitude()));
        Log.i("LocationApp", String.valueOf(location.getLongitude()));
    }

    public void localizarGPS(View view) {
        createLocationRequest();
        startUpdates();
    }

    private void stopUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiCliente, this);
        estaUbicando = false;
    }

    private void startUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiCliente,
                mLocationRequest,
                this);
        estaUbicando = true;
    }

    @Override
    public void onLocationChanged(Location location) {
        tviLocalizacion.setText(location.getLatitude() +
                " , " + location.getLongitude());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (estaUbicando){
            // Parar la localizacion
            stopUpdates();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mGoogleApiCliente.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "" + mGoogleApiCliente.isConnected());
        if (!estaUbicando && mGoogleApiCliente.isConnected()){
            //activar la localizacion
            startUpdates();
        }
    }
}
