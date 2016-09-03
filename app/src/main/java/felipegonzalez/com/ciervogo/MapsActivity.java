package felipegonzalez.com.ciervogo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.facebook.FacebookSdk;

/*

Key hash facebook: qbVxOyyMZIx6QIq4XGpeX0WxgbE=

t0wOizNSFao7DtVJolehtNbtk0E= (deberia ser esta)
 */

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;
    public int durationToast = Toast.LENGTH_SHORT;

    private Marker marker = null;
    private SharedPreferences prefs = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mLocationProvider = new LocationProvider(this, this); //Llamada a API para detectar ubicación.

        prefs = this.getSharedPreferences("LatLng",MODE_PRIVATE);
        //Check whether your preferences contains any values then we get those values
        if((prefs.contains("Lat")) && (prefs.contains("Lng")))
        {
            String lat = prefs.getString("Lat","");
            String lng = prefs.getString("Lng","");
            LatLng l =new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
            mMap.addMarker(new MarkerOptions().position(l));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mLocationProvider.connect();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                marker = mMap.addMarker(new MarkerOptions().position(point).draggable(true).title("Nuevo Marcador"));

                /* This code will save your location coordinates in SharedPrefrence when you click on the map and later you use it  */
                prefs.edit().putString("Lat",String.valueOf(point.latitude)).commit();
                prefs.edit().putString("Lng",String.valueOf(point.longitude)).commit();

            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }


    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.getUiSettings().setCompassEnabled(true); //habilitar brujula
        mMap.getUiSettings().setZoomControlsEnabled(true);//habilitar zoom
        mMap.setMyLocationEnabled(true); //habilitar boton ubicacion actual
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").draggable(true));
    }



    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Estoy aqui!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    public void getSearcher(View view)
    {
        Intent intent = new Intent(MapsActivity.this,SearchActivity.class);
        startActivity(intent);
    }

    public void getProfile(View view)
    {
        Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    public void getLogin(View view)
    {
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
