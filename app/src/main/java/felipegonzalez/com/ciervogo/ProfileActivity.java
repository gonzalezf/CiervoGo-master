package felipegonzalez.com.ciervogo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// My Facebook ID: 10210814575779573
public class ProfileActivity extends ActionBarActivity {
    private TextView idAnimalTextView;
    private ListView mListView;
    ProgressBar pbPoints;
    TextView tvPoints;
    private String URL_GET_POINT_USER = "http://nachotp.asuscomm.com:8111/getPointsUser.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();

        String birthdayDate = intent.getStringExtra("user.birthdayDate");
        //idAnimalTextView = (TextView)findViewById(R.id.textView5);
        tvPoints = (TextView )findViewById(R.id.textfieldPoints);
        pbPoints = (ProgressBar) findViewById(R.id.progressbarPoints);


        FacebookSdk.sdkInitialize(getApplicationContext());
        if(isLoggedIn()){ //Esta logueado en facebook
            Profile profile = Profile.getCurrentProfile(); //Obtener profile usuario de facebook
            //Obtener Profile Picture Facebook y mandarla al layout
            ProfilePictureView profilePictureView;
            profilePictureView = (ProfilePictureView) findViewById(R.id.imageProfileFacebook);
            profilePictureView.setProfileId(profile.getId());


            String name = profile.getName();
            TextView myTextView= (TextView) findViewById(R.id.nameProfile);
            myTextView.setText(name);
            String username = profile.getId();
            //new GetAnimalActivity(this,idAnimalTextView,0).execute(username);

            mListView = (ListView) findViewById(R.id.animal_list_view);

            final ArrayList<Animal> animalList = Animal.getRecipesFromFile(username, this);

            AnimalAdapter adapter = new AnimalAdapter(this, animalList);
            mListView.setAdapter(adapter);// 2
            tvPoints.setText("Puntos Acumulados: "+GetUserPoints(username));
            pbPoints.setProgress(Integer.valueOf(GetUserPoints(username)));


        }

        else{ //No esta logueado
            Intent intent2 = new Intent(this, LoginActivity.class);
            startActivity(intent2);
        }

    }

    private String GetUserPoints(String idFacebook) {

        boolean getPoint = false;
        // Preparing post params
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idFacebook", idFacebook));


        ServiceHandler serviceClient = new ServiceHandler();

        Log.e("LINK UPDATE = ",URL_GET_POINT_USER);

        String json = serviceClient.makeServiceCall(URL_GET_POINT_USER,
                ServiceHandler.POST, params);

        Log.e("GETPOINTUSER: ", "> " + json);
        if (json != null) {
            try {
                JSONObject jsonObj = new JSONObject(json);
                if (jsonObj != null) {
                    JSONArray usuario = jsonObj
                            .getJSONArray("usuario");

                    for (int i = 0; i < usuario.length(); i++) {
                        JSONObject catObj = (JSONObject) usuario.get(i);
                        String resultPuntaje = catObj.getString("puntajeUsuario");
                        Log.e("Result Puntaje", resultPuntaje);
                        return  resultPuntaje;
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

    public boolean isLoggedIn() { //Esta logueado en facebook ?
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    public static Bitmap getFacebookProfilePicture(String userID) throws IOException {
        URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
        Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        return bitmap;

    }
}
