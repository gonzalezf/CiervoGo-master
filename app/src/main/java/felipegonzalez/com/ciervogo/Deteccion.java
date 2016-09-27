package felipegonzalez.com.ciervogo;

/**
 * Created by felipe on 18-09-16.
 */
public class Deteccion {
    private  int idDeteccion;
    private int idFacebook;
    private int idAnimal;
    private float Latitud;
    private float Longitud;

    public Deteccion(){}

    public Deteccion(int idDeteccion,int idFacebook,int idAnimal,float Latitud, float Longitud){
        this.idDeteccion = idDeteccion;
        this.idFacebook = idFacebook;
        this.idAnimal = idAnimal;
        this.Latitud = Latitud;
        this.Longitud = Longitud;
    }
    public void setIdDeteccion(int idDeteccion){
        this.idDeteccion = idDeteccion;
    }
    public void setIdFacebook(int idFacebook){
        this.idFacebook = idFacebook;
    }
    public void setIdAnimal(int idAnimal){
        this.idAnimal = idAnimal;
    }
    public void setLatitud(float Latitud){
        this.Latitud = Latitud;
    }
    public void setLongitud(float Longitud){
        this.Longitud = Longitud;
    }
    public int getIdDeteccion(){
        return this.idDeteccion;
    }
    public int getIdFacebook(){
        return this.idFacebook;
    }
    public int getIdAnimal(){
        return this.idAnimal;
    }
    public float getLatitud(){
        return this.Latitud;
    }
    public float getLongitud(){
        return this.Longitud;
    }

}