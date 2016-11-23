package felipegonzalez.com.ciervogo;

/**
 * Created by felipe on 16-11-16.
 */
public class AnimalFavoritoEncontrado {


    private double Latitud;
    private double Longitud;
    private String nombreAnimal;
    private String linkFotoAnimal;
    private double distancia; //distancia encontrada

    public AnimalFavoritoEncontrado(){}

    public AnimalFavoritoEncontrado(String nombreAnimal, String linkFotoAnimal ,double distancia,double Latitud, double Longitud){
        this.nombreAnimal = nombreAnimal;
        this.linkFotoAnimal = linkFotoAnimal;
        this.distancia = distancia;
        this.Latitud = Latitud;
        this.Longitud = Longitud;
    }


    public void setLatitud(double Latitud){
        this.Latitud = Latitud;
    }
    public void setLongitud(double Longitud){
        this.Longitud = Longitud;
    }
    public void setDistancia(double distancia){
        this.distancia = distancia;
    }
    public void setNombreAnimal(String nombreAnimal){
        this.nombreAnimal = nombreAnimal;
    }
    public void setLinkFotoAnimal(String linkFotoAnimal){
        this.linkFotoAnimal = linkFotoAnimal;
    }


    public double getLatitud(){
        return this.Latitud;
    }
    public double getLongitud(){
        return this.Longitud;
    }
    public double getDistancia(){return this.distancia;}
    public String getNombreAnimal(){return this.nombreAnimal;}
    public String getLinkFotoAnimal(){return this.linkFotoAnimal;}

}