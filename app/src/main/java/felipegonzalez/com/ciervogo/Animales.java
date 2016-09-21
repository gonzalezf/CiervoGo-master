package felipegonzalez.com.ciervogo;

/**
 * Created by felipe on 18-09-16.
 */
public class Animales {


    private int idAnimal;
    private String nombreAnimal;

    public Animales(){}

    public Animales(int id, String name){
        this.idAnimal = id;
        this.nombreAnimal = name;
    }

    public void setId(int id){
        this.idAnimal = id;
    }

    public void setName(String name){
        this.nombreAnimal = name;
    }

    public int getId(){
        return this.idAnimal;
    }

    public String getName(){
        return this.nombreAnimal;
    }

}