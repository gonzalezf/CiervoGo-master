package felipegonzalez.com.ciervogo;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecomendadorActivity extends ActionBarActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String GetFavoritiesAnimals = "http://nachotp.asuscomm.com:8111/selectFavoritiesAnimals.php";
    private String GetAnimalMasBuscado = "http://nachotp.asuscomm.com:8111/getAnimalMasBuscado.php";
    private String GetFavoriesAnimalByHistory = "http://nachotp.asuscomm.com:8111/selectFavoritiesAnimalsByHistory.php";

    private GoogleMap mMap;
    private  String Distancia = "100"; //100 km
    private LocationProvider mLocationProvider;
    TextView textViewNombreAnimal;
    TextView textViewDistanciaAnimal;
    ImageView imageViewLinkFotoAnimal;
    Button buttonNext;
    Button buttonBack;
    private Marker marker = null;
    public int contador = 0;
    GoogleApiClient mGoogleApiClient = null;

    double LatitudActual = 0.0;
    double LongitudActual = 0.0;
    int distancia = 10000;

    ArrayList<AnimalFavoritoEncontrado> favoritosList = new ArrayList<AnimalFavoritoEncontrado>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendador2);
        FacebookSdk.sdkInitialize(getApplicationContext());
//        new GetDeteccionesCercanas(String.valueOf(0.0),String.valueOf(0.0),String.valueOf(10000),username).execute(); //LLamada a base de datos, se crea deteccion en bd

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        textViewNombreAnimal = (TextView) findViewById(R.id.textviewNombreAnimal);
        textViewDistanciaAnimal = (TextView) findViewById(R.id.textviewDistancia);
        imageViewLinkFotoAnimal = (ImageView) findViewById(R.id.imageViewLinkFotoAnimal);
        buttonNext = (Button) findViewById(R.id.buttonNext);
        buttonBack = (Button) findViewById(R.id.buttonBack);



        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapRecomendador);

            mMap = mapFragment.getMap();
            mMap.getUiSettings().setCompassEnabled(true); //habilitar brujula
            mMap.getUiSettings().setZoomControlsEnabled(true);//habilitar zoom
            mMap.setMyLocationEnabled(true); //habilitar boton ubicacion actual


        }




    }

    protected void Otro(){
        Profile profile = Profile.getCurrentProfile(); //Obtener profile usuario de facebook
        String username = profile.getId();
        favoritosList =  GetDeteccionesCercanas(String.valueOf(LatitudActual),String.valueOf(LongitudActual),String.valueOf(distancia),username,favoritosList); //LLamada a base de datos, se crea deteccion en bd
        favoritosList = ObtenerAnimalFavoritoPorHistorial(String.valueOf(LatitudActual),String.valueOf(LongitudActual),username,String.valueOf(distancia),favoritosList);

        if (favoritosList == null || favoritosList.size()== 0) {
            Log.e("Lista Animal", "No se cargan animales favoritos del user");
            Intent intent = new Intent(RecomendadorActivity.this, MapsActivity.class);
            startActivity(intent);
        }



        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+username+"/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.e("FACEBOOK",response.getRawResponse());
                        JSONObject friend = response.getJSONObject();


                        try {
                            JSONArray info = friend.getJSONArray("data");
                            JSONObject user = info.getJSONObject(0);
                            Log.e("id fb = ",user.getString("id")); //ID DE NACHO!! OJO, HACER FOR SI SON MAS USERS!
                            String idFacebookFriend = user.getString("id");
                            Log.e("HOLI!!!","HOLI");
                            favoritosList =  GetDeteccionesCercanas(String.valueOf(LatitudActual),String.valueOf(LongitudActual),String.valueOf(distancia),idFacebookFriend,favoritosList); //LLamada a base de datos, se crea deteccion en bd
                            Log.e("FAVORITOS LIST = ",favoritosList.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
        ).executeAsync();

        if(contador == 0){
            buttonBack.setEnabled(false);
        }
        if(contador == favoritosList.size()-1){
            buttonNext.setEnabled(false);
        }
        textViewNombreAnimal.setText(favoritosList.get(contador).getNombreAnimal());
        textViewDistanciaAnimal.setText("Distancia = "+String.valueOf(favoritosList.get(contador).getDistancia())+" km");
        Picasso.with(this)
                .load(favoritosList.get(contador).getLinkFotoAnimal())
                .into(imageViewLinkFotoAnimal);
        //       setUpMapIfNeeded();
        setMap(mMap);

    }
    protected void onStart() {
        Log.e("U","start");
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
                Otro();
            }
        }, 3000); // 3000 milliseconds delay
        //mGoogleApiClient.connect();

        super.onStart();

    }

    protected void onStop() {
        Log.e("U","stop");

        mGoogleApiClient.disconnect();
        super.onStop();
    }
    public void onConnected(Bundle connectionHint) {
        Log.e("U","onconnected");

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.e("Encontrado",String.valueOf(mLastLocation.getLatitude()));
            LatitudActual = mLastLocation.getLatitude();
            LongitudActual = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("U","suspended");

    }

    public void goNext(View view) {
        buttonNext.setEnabled(true);
        buttonBack.setEnabled(true);
        contador++;
        Log.e("COntador es = ",String.valueOf(contador));
        if(contador== favoritosList.size()){
            contador = favoritosList.size()-1;
        }
        if(contador == favoritosList.size()-1){
            buttonNext.setEnabled(false);
        }
        textViewNombreAnimal.setText(favoritosList.get(contador).getNombreAnimal());
        textViewDistanciaAnimal.setText("Distancia = "+String.valueOf(favoritosList.get(contador).getDistancia())+" km");
        Picasso.with(this)
                .load(favoritosList.get(contador).getLinkFotoAnimal())
                .into(imageViewLinkFotoAnimal);
        setMap(mMap);
    }

    public void goBack(View view) {
        buttonBack.setEnabled(true);
        buttonNext.setEnabled(true);
        contador--;
        Log.e("COntador es = ",String.valueOf(contador));
        if(contador== -1){
            contador = 0;

        }
        if(contador==0){
            buttonBack.setEnabled(false);
        }
        textViewNombreAnimal.setText(favoritosList.get(contador).getNombreAnimal());
        textViewDistanciaAnimal.setText("Distancia = "+String.valueOf(favoritosList.get(contador).getDistancia())+" km");
        Picasso.with(this)
                .load(favoritosList.get(contador).getLinkFotoAnimal())
                .into(imageViewLinkFotoAnimal);
        setMap(mMap);
    }


    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public ArrayList<AnimalFavoritoEncontrado> GetDeteccionesCercanas(String Latitud, String Longitud, String Distancia, String idFacebook,ArrayList<AnimalFavoritoEncontrado> favoritosList){
            Log.e("Latitud = ",Latitud);
            Log.e("Longitud = ",Longitud);
            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Latitud",Latitud));
            params.add(new BasicNameValuePair("Longitud",Longitud));
            params.add(new BasicNameValuePair("Distancia",Distancia));
            params.add(new BasicNameValuePair("idFacebook",idFacebook));

            ServiceHandler serviceClient = new ServiceHandler();
            String json = serviceClient.makeServiceCall(GetFavoritiesAnimals,
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
                            AnimalFavoritoEncontrado cat = new AnimalFavoritoEncontrado(catObj.getString("nombreAnimal"),
                                    catObj.getString("linkFotoAnimal"),catObj.getDouble("distance"),catObj.getDouble("Latitud"),catObj.getDouble("Longitud"));
                            favoritosList.add(cat); //cat es un objeto deteccion y deteccionList alberga todas las detecciones

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server! - recomendador");
                return null;
            }
            return favoritosList;
        }

    public ArrayList<AnimalFavoritoEncontrado> ObtenerAnimalFavoritoPorHistorial(String Latitud, String Longitud, String idFacebook,String distance, ArrayList<AnimalFavoritoEncontrado> favoritosList){
        Log.e("Latitud = ",Latitud);
        Log.e("Longitud = ",Longitud);
        //Obtener ID ANIMAL FAVORITO!
        int idAnimalFavorito = -1;
        List<NameValuePair> params0 = new ArrayList<NameValuePair>();
        params0.add(new BasicNameValuePair("idFacebook",idFacebook));

        ServiceHandler serviceClient0 = new ServiceHandler();
        String json0 = serviceClient0.makeServiceCall(GetAnimalMasBuscado,
                ServiceHandler.POST, params0);

        if (json0 != null) {
            Log.e("R - ",json0);
            try {
                JSONObject jsonObj = new JSONObject(json0);
                if (jsonObj != null) {
                    JSONArray deteccion = jsonObj
                            .getJSONArray("favorito");

                    for (int i = 0; i < deteccion.length(); i++) {
                        JSONObject catObj = (JSONObject) deteccion.get(i);
                        idAnimalFavorito = catObj.getInt("idAnimal");

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("JSON Data", "Didn't receive any data from server! - recomendador");
            return null;
        }

        // Preparing post params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Latitud",Latitud));
        params.add(new BasicNameValuePair("Longitud",Longitud));
        params.add(new BasicNameValuePair("idFacebook",idFacebook));
        params.add(new BasicNameValuePair("distance",distance));
        params.add(new BasicNameValuePair("idAnimal",String.valueOf(idAnimalFavorito)));
        Log.e("ID ANIMAL MAS BUSCADO",String.valueOf(idAnimalFavorito));
        ServiceHandler serviceClient = new ServiceHandler();
        String json = serviceClient.makeServiceCall(GetFavoriesAnimalByHistory,
                ServiceHandler.POST, params);

        if (json != null) {
            Log.e("POR HISTORIAL - ",json);
            try {
                JSONObject jsonObj = new JSONObject(json);
                if (jsonObj != null) {
                    JSONArray deteccion = jsonObj
                            .getJSONArray("favorito");

                    for (int i = 0; i < deteccion.length(); i++) {
                        JSONObject catObj = (JSONObject) deteccion.get(i);
                        AnimalFavoritoEncontrado cat = new AnimalFavoritoEncontrado(catObj.getString("nombreAnimal"),
                                catObj.getString("linkFotoAnimal"),catObj.getDouble("distance"),catObj.getDouble("Latitud"),catObj.getDouble("Longitud"));

                        Log.e("10",cat.getLinkFotoAnimal());
                        Log.e("20",cat.getNombreAnimal());
                        Log.e("30", String.valueOf(cat.getDistancia()));
                        Log.e("40", String.valueOf(cat.getLatitud()));
                        Log.e("50", String.valueOf(cat.getLongitud()));
                        favoritosList.add(cat); //cat es un objeto deteccion y deteccionList alberga todas las detecciones

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.e("JSON Data", "Didn't receive any data from server! - recomendador");
            return null;
        }
        return favoritosList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
    public void  setMap(GoogleMap map){
        LatLng posicionAnimal = new LatLng(favoritosList.get(contador).getLatitud(), favoritosList.get(contador).getLongitud());
        map.addMarker(new MarkerOptions().position(new LatLng(favoritosList.get(contador).getLatitud(), favoritosList.get(contador).getLongitud())).title(favoritosList.get(contador).getNombreAnimal()));
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(posicionAnimal)      // Sets the center of the map to Mountain View
                .zoom(8)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}


