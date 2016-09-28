package felipegonzalez.com.ciervogo;

/**
 * Created by felipe on 18-09-16.
 */
public class Deteccion {
    private  int idDeteccion;
    private int idFacebook;
    private int idAnimal;
    private double Latitud;
    private double Longitud;
    private String image;
    private String detalleAnimal;

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
    public void setImage(String image){this.image = image;}
    public void setDetalleAnimal(String detalleAnimal){this.detalleAnimal=detalleAnimal;}

    public String getImage(){return this.image;}
    public String getDetalleAnimal(){return this.detalleAnimal;}
    public int getIdDeteccion(){
        return this.idDeteccion;
    }
    public int getIdFacebook(){
        return this.idFacebook;
    }
    public int getIdAnimal(){
        return this.idAnimal;
    }
    public double getLatitud(){
        return this.Latitud;
    }
    public double getLongitud(){
        return this.Longitud;
    }

}