package felipegonzalez.com.ciervogo;

/**
 * Created by felipe on 18-09-16.
 */
public class Animales {


    private int idAnimal;
    private String nombreAnimal;

    public  Animales(){}

    public Animales(int idAnimal, String nombreAnimal){
        this.idAnimal = idAnimal;
        this.nombreAnimal = nombreAnimal;
    }

    public void setId(int idAnimal){
        this.idAnimal = idAnimal;
    }

    public void setName(String nombreAnimal){
        this.nombreAnimal = nombreAnimal;
    }

    public int getId(){
        return this.idAnimal;
    }

    public String getName(){
        return this.nombreAnimal;
    }

}