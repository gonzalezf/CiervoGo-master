package felipegonzalez.com.ciervogo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.facebook.FacebookSdk;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*

Key hash facebook: qbVxOyyMZIx6QIq4XGpeX0WxgbE=

t0wOizNSFao7DtVJolehtNbtk0E= (deberia ser esta)
 */

public class MapsActivity extends FragmentActivity implements LocationProvider.LocationCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;
    public int durationToast = Toast.LENGTH_SHORT;
    private HashMap<Marker,String > idDeteccionHashMap;


    private Marker marker = null;
    private SharedPreferences prefs = null;
    private int flag = 0;
    private String link = "http://nachotp.asuscomm.com:8111/getAllDeteccion.php";
    private String linkGetAnimal = "http://nachotp.asuscomm.com:8111/getAllAnimal.php";
    private String getErroneos = "http://nachotp.asuscomm.com:8111/numero_erroneos.php";

    private ArrayList<Deteccion> deteccionList;
    private ProgressDialog pDialog;
    public ArrayList<Animales> animalList;
    public int LastId=0;
    public int numeroErroneos = -1;

    public  int GetNumeroErroneos(String idFacebook){

        // Preparing post params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
         params.add(new BasicNameValuePair("idFacebook",idFacebook));

        ServiceHandler serviceClient = new ServiceHandler();
        String json = serviceClient.makeServiceCall(getErroneos,
                ServiceHandler.POST, params);

        if (json != null) {
            Log.e("R - ",json);
            try {
                JSONObject jsonObj = new JSONObject(json);
                if (jsonObj != null) {
                    JSONArray deteccion = jsonObj
                            .getJSONArray("favorito");

                    for (int i = 0; i < deteccion.length(); i++) {
                        JSONObject catObj = (JSONObject) deteccion.get(i);
                        numeroErroneos = catObj.getInt("NUM");
                        Log.e("NUMERO ERRONEOS",String.valueOf(numeroErroneos));
                        return numeroErroneos;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("JSON Data", "Didn't receive any data from server! - num erroneos");
        }
        return -2;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        FacebookSdk.sdkInitialize(getApplicationContext());

        /*
*/
        setUpMapIfNeeded();
        mLocationProvider = new LocationProvider(this, this); //Llamada a API para detectar ubicación.

        if(!isLoggedIn()){
            Intent intent = new Intent(MapsActivity.this,LoginActivity.class);
            startActivity(intent);
        }



    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    @Override
    protected void onStart(){
        super.onStart();
        String longText= "Has sido catalogado como un mal explorador. Considera usar la app con responsabilidad ";
        Profile profile = Profile.getCurrentProfile(); //Obtener profile usuario de facebook
        String username = profile.getId();
        numeroErroneos = GetNumeroErroneos(username);
        if(numeroErroneos>2){
            Notification n = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.mano)
                    .setContentTitle("HEY!! MAL MUCHACHO")
                    .setAutoCancel(true)
                    .setColor(721679)
                    .setStyle(new Notification.BigTextStyle().bigText(longText)).build();

            NotificationManager notificationManager =    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if(!isLoggedIn()){
            Intent intent = new Intent(MapsActivity.this,LoginActivity.class);
            startActivity(intent);

        }
        mLocationProvider.connect();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                marker = mMap.addMarker(new MarkerOptions().position(point).draggable(true).title("Editar"));
                LastId = LastId+1;
                idDeteccionHashMap.put(marker,String.valueOf(LastId));

                /* This code will save your location coordinates in SharedPrefrence when you click on the map and later you use it  */
                String Latitude = String.valueOf(point.latitude);
                String Longitud = String.valueOf(point.longitude);
                /*prefs.edit().putString("Lat",Latitude).commit();
                prefs.edit().putString("Lng",Longitud).commit();
                */
                Profile profile = Profile.getCurrentProfile(); //Obtener profile usuario de facebook
                String username = profile.getId();

                new InsertAnimalActivity(0).execute(username,"1",Latitude,Longitud); //LLamada a base de datos, se crea deteccion en bd
                // como lo hacemos para esto? reiniciar actividad ???

            }

        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() { //Funciona, hay que obtener datos de marcador
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().equals("Editar")){ //hacer que cualquier marcador al ser clickeado lleve a editar, si la id es distinta no pued eeditar
                    Intent intent = new Intent(MapsActivity.this,EditAnimalActivity.class);

                    //Mandar datos a la siguiente actividad..

                    Double Latitud = marker.getPosition().latitude;
                    Double Longitud = marker.getPosition().longitude;
                    intent.putExtra("Longitud",Longitud);
                    intent.putExtra("Latitud",Latitud);
                    Profile profile = Profile.getCurrentProfile();
                    String username = profile.getId();
                    intent.putExtra("idFacebook",username);
                    String idDeteccion = idDeteccionHashMap.get(marker);
                    intent.putExtra("idDeteccion",idDeteccion);
                    Log.e("Mandar a edit ",idDeteccion);


                    startActivity(intent);

                }
                else{
                    Intent intent = new Intent(MapsActivity.this,ShowAnimalActivity.class);

                    //Mandar datos a la siguiente actividad..
                    Double Latitud = marker.getPosition().latitude;
                    Double Longitud = marker.getPosition().longitude;
                    intent.putExtra("Longitud",Longitud);
                    intent.putExtra("Latitud",Latitud);
                    Profile profile = Profile.getCurrentProfile();
                    String username = profile.getId();
                    intent.putExtra("idFacebook",username);
                    intent.putExtra("nombreAnimal",marker.getTitle());
                    Log.e("Mandar a show ",String.valueOf(Latitud));
                    Log.e("ID DETECCION == ",idDeteccionHashMap.get(marker));
                    intent.putExtra("idDeteccion",idDeteccionHashMap.get(marker));

                    startActivity(intent);
                }

            }
        });

    }



    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }


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


    private void setUpMap() {
        mMap.getUiSettings().setCompassEnabled(true); //habilitar brujula
        mMap.getUiSettings().setZoomControlsEnabled(true);//habilitar zoom
        mMap.setMyLocationEnabled(true); //habilitar boton ubicacion actual
        getMarkers();

    }

    public void getMarkers(){

        deteccionList = new ArrayList<Deteccion>();
        animalList = new ArrayList<Animales>();
        new GetMarkersAll().execute();




    }
    private class GetMarkersAll extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MapsActivity.this);
            pDialog.setMessage("Obteniendo detecciones ..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(link, ServiceHandler.GET);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray deteccion = jsonObj
                                .getJSONArray("deteccion");

                        for (int i = 0; i < deteccion.length(); i++) {
                            JSONObject catObj = (JSONObject) deteccion.get(i);
                            Deteccion cat = new Deteccion(catObj.getInt("idDeteccion"),
                                    catObj.getInt("idFacebook"),catObj.getInt("idAnimal"),catObj.getLong("Latitud"),catObj.getLong("Longitud"));

                            deteccionList.add(cat); //cat es un objeto deteccion y deteccionList alberga todas las detecciones
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }


            //llenar animales
            String json2 = jsonParser.makeServiceCall(linkGetAnimal, ServiceHandler.GET);

            Log.e("Response Animal: ", "> " + json2);

            if (json2 != null) {
                try {
                    JSONObject jsonObj2 = new JSONObject(json2);
                    if (jsonObj2 != null) {
                        JSONArray animals = jsonObj2
                                .getJSONArray("animales");

                        for (int i = 0; i < animals.length(); i++) {
                            JSONObject catObj2 = (JSONObject) animals.get(i);
                            Animales cat2 = new Animales(catObj2.getInt("idAnimal"),
                                    catObj2.getString("nombreAnimal"));

                            Log.e("Null",cat2.getName()+String.valueOf(cat2.getId()));
                            if (cat2 != null) {
                                Log.e("es distinto de null","yei!");
                                animalList.add(cat2);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }



        private String getNameAnimal(int idAnimal){
            if(!animalList.isEmpty()){
                for(int i = 0; i < animalList.size();i++){
                    Animales obj = animalList.get(i);
                    if(idAnimal == obj.getId()){
                        return obj.getName();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            idDeteccionHashMap = new HashMap<Marker, String>();

            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            //Llenar de marcadores desde la base de datos
            for (int i = 0; i < deteccionList.size(); i++) {
                Deteccion obj = deteccionList.get(i);
                marker = mMap.addMarker(new MarkerOptions().position(new LatLng(obj.getLatitud(), obj.getLongitud())).title(getNameAnimal(obj.getIdAnimal())));
                idDeteccionHashMap.put(marker,String.valueOf(obj.getIdDeteccion()));
                LastId = obj.getIdDeteccion(); //quedara con el ultimo valor agregado

            }

            }


    }



    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Estoy aqui!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }



    // Otras Actividades
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

    public void getExplorer(View view)
    {
        Intent intent = new Intent(MapsActivity.this, ExplorerActivity.class);
        startActivity(intent);
    }

    public void getLogin(View view)
    {
        Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void getOptionsMenu(View view)
    {
        Intent intent = new Intent(MapsActivity.this, OptionsMenu.class);
        startActivity(intent);
    }


}
