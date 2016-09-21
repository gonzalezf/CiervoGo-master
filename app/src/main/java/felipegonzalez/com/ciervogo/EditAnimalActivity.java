package felipegonzalez.com.ciervogo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EditAnimalActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private String link = "http://192.168.50.11/getAllAnimal.php";
    private String URL_NEW_ANIMAL = "http://192.168.50.11/insertNewAnimal.php";
    private String URL_UPDATE_ANIMAL = "http://192.168.50.11/updateidAnimal.php";

    private Button btnUpdateAnimal;
    private Button btnAddNewAnimal;
    private TextView txtAnimal;
    private Spinner spinnerAnimal;
    // array list for spinner adapter
    private ArrayList<Animales> animalList;
    private ProgressDialog pDialog;
    private List<Integer> idSpinner = new ArrayList<>();


    public int idnewAnimal = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_animal);

        btnAddNewAnimal = (Button) findViewById(R.id.btnAddNewAnimal);
        btnUpdateAnimal = (Button) findViewById(R.id.btnUpdateAnimal);

        spinnerAnimal = (Spinner) findViewById(R.id.spinAnimal);
        txtAnimal = (TextView) findViewById(R.id.txtAnimal);

        animalList = new ArrayList<Animales>();

        // spinner item select listener
        spinnerAnimal.setOnItemSelectedListener(this);
        // Add new category click event
        btnAddNewAnimal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (txtAnimal.getText().toString().trim().length() > 0) {

                    // new category name
                    String newAnimal = txtAnimal.getText().toString();

                    // Call Async task to create new category
                    new AddNewAnimal().execute(newAnimal);
                    Intent myIntent = new Intent(v.getContext(), MapsActivity.class);
                    startActivity(myIntent);



                } else {
                    Toast.makeText(getApplicationContext(),
                            "Ingresa Nombre Animal", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        new GetAnimals().execute();

        btnUpdateAnimal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String idnewAnimal = String.valueOf(idSpinner.get(spinnerAnimal.getSelectedItemPosition()));

                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    String Latitud = String.valueOf(extras.getDouble("Latitud"));
                    String Longitud = String.valueOf(extras.getDouble("Longitud"));
                    String idFacebook = extras.getString("idFacebook");

                    //EL id new animal, sera el proporcionado por el spinnner!
                    new UpdateAnimal().execute(idnewAnimal,Latitud,Longitud, idFacebook);

                }
                Intent myIntent = new Intent(v.getContext(), MapsActivity.class);
                startActivity(myIntent);





            }
        });

    }


    private class GetAnimals extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditAnimalActivity.this);
            pDialog.setMessage("Cargando Animales....");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(link, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray animals = jsonObj
                                .getJSONArray("animales");

                        for (int i = 0; i < animals.length(); i++) {
                            JSONObject catObj = (JSONObject) animals.get(i);
                            Animales cat = new Animales(catObj.getInt("idAnimal"),
                                    catObj.getString("nombreAnimal"));
                            animalList.add(cat);
                            Log.e("List",String.valueOf(catObj.getInt("idAnimal")));
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }

    }

    /**
     * Adding spinner data
     */
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        txtAnimal.setText("");
        Log.e("tamanno spinner", String.valueOf(animalList.size()));

        for (int i = 0; i < animalList.size(); i++) {
            lables.add(animalList.get(i).getName());
            idSpinner.add(animalList.get(i).getId());

        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerAnimal.setAdapter(spinnerAdapter);
    }

    //Crear nuevo Animal/

    private class AddNewAnimal extends AsyncTask<String, Void, Void> {

        boolean isNewCategoryCreated = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditAnimalActivity.this);
            pDialog.setMessage("Creando nuevo animal..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... arg) {

            String newAnimal = arg[0];

            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("nombreAnimal", newAnimal));

            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(URL_NEW_ANIMAL,
                    ServiceHandler.POST, params);

            Log.e("Insert New Animal: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        isNewCategoryCreated = true;

                        //Actualizar marcador en bd

                        idnewAnimal = Integer.parseInt(jsonObj.getString("idAnimal"));


                    } else {
                        Log.e("Create Animal Error: ", "> " + jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isNewCategoryCreated) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // fetching all categories
                        Bundle extras = getIntent().getExtras();
                        if (extras != null) {
                            String Latitud = String.valueOf(extras.getDouble("Latitud"));
                            String Longitud = String.valueOf(extras.getDouble("Longitud"));
                            String idFacebook = extras.getString("idFacebook");
                            Log.e("VAR=",String.valueOf(idnewAnimal)+"="+Latitud+"="+Longitud+"="+idFacebook);
                            new UpdateAnimal().execute(String.valueOf(idnewAnimal),Latitud,Longitud, idFacebook);

                        }

                        //new GetAnimals().execute();
                    }
                });
            }
        }

    }


    private class UpdateAnimal extends AsyncTask<String, Void, Void> {

        boolean isUpdateAnimal = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditAnimalActivity.this);
            pDialog.setMessage("Actualizando Animal..");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... arg) {

            String newAnimal = arg[0];
            String  Latitud = arg[1];
            String Longitud = arg[2];
            String idFacebook = arg[3];

            // Preparing post params
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("idAnimal", newAnimal));
            params.add(new BasicNameValuePair("Latitud",Latitud));
            params.add(new BasicNameValuePair("Longitud",Longitud));
            params.add(new BasicNameValuePair("idFacebook",idFacebook));

            ServiceHandler serviceClient = new ServiceHandler();

            String json = serviceClient.makeServiceCall(URL_UPDATE_ANIMAL,
                    ServiceHandler.POST, params);

            Log.e("Actualizar New Animal: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    boolean error = jsonObj.getBoolean("error");
                    // checking for error node in json
                    if (!error) {
                        // new category created successfully
                        isUpdateAnimal = true;

                    } else {
                        Log.e("Update Animal Error: ", "> " + jsonObj.getString("message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (isUpdateAnimal) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // fetching all categories
                        //new GetAnimals().execute();



                    }
                });
            }

        }

    }

    @Override //CUando se selecciona el spinner
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {



    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }


}