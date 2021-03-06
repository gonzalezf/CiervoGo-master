package felipegonzalez.com.ciervogo;

/**
 * Created by felipe on 03-09-16.
 */

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class Animal {
    //Esto se hizo para mostrar los ultimos 5 animales!
    public String nombreAnimal;
    public String linkFotoAnimal;
    public String extincionAnimal;
    public String descripcionAnimal;
    public int idAnimal;


    public Animal(int idAnimal,String nombreAnimal, String linkFotoAnimal){

        this.idAnimal = idAnimal;
        this.nombreAnimal = nombreAnimal;
        this.linkFotoAnimal = linkFotoAnimal;
    }
    public Animal(){}

    public static ArrayList<Animal> getRecipesFromFile(String username, Context context){
        final ArrayList<Animal> AnimalList = new ArrayList<>();
        ///String link = "http://192.168.50.11/pokedex.php?username="+username;

        String link = "http://nachotp.asuscomm.com:8111/pokedex.php?username="+username;


        //String jsonString = loadJsonFromAsset("animal.json", context);

        ServiceHandler jsonParser = new ServiceHandler();
        String json = jsonParser.makeServiceCall(link, ServiceHandler.GET);

        Log.e("pokedex : ", "> " + link);

        if (json != null){
            Log.e("json = ",json);

            try{
                JSONObject jsonObj = new JSONObject(json);
                if (jsonObj != null) {

                    JSONArray animal = jsonObj.getJSONArray("animales");
                    // Get Recipe objects from data
                    for(int i = 0; i < animal.length(); i++){
                        Animal recipe = new Animal();

                        recipe.nombreAnimal = animal.getJSONObject(i).getString("nombreAnimal");
                        recipe.descripcionAnimal= animal.getJSONObject(i).getString("descripcionAnimal");
                        recipe.linkFotoAnimal = animal.getJSONObject(i).getString("linkFotoAnimal");
                        recipe.extincionAnimal = animal.getJSONObject(i).getString("extincionAnimal");

                        AnimalList.add(recipe);
                    }

                }


            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        else {
            Log.e("JSON Data", "Didn't receive any data from server!");
        }

        return AnimalList;
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public void setNombreAnimal(String nombreAnimal){
        this.nombreAnimal = nombreAnimal;
    }

    public void setDescripcionAnimal(String descripcionAnimal){
        this.descripcionAnimal = descripcionAnimal;
    }
    public void setExtincionAnimal(String extincionAnimal){
        this.extincionAnimal = extincionAnimal;
    }

    public String getNombreAnimal(){
        return this.nombreAnimal;
    }

    public String getDescripcionAnimal(){
        return this.descripcionAnimal;
    }

    public String getExtincionAnimal(){
        return this.extincionAnimal;
    }

    public String getLinkFotoAnimal(){return this.linkFotoAnimal;}
    public Integer getIdAnimal(){return this.idAnimal;}

}
