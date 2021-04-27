package mud;

import java.util.ArrayList;

public class User {

    private String name;
    private ArrayList<String> items = new ArrayList<>();
    private Boolean logged;

    public User(String name, Boolean logged){
        this.name = name;
        this.logged = logged;
    }

    public ArrayList<String> getItems(){
        return items;
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    public Boolean getLogged(){
        return logged;
    }

    public void setLogged(Boolean logged){
        this.logged = logged;
    }

    public void setName(String name ){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void addItem(String item){
        items.add(item);
    }

}
