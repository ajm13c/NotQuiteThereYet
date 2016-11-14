package edu.fsu.cs.mobile.notquitethereyet;

import android.widget.CheckBox;

/**
 * Created by eric on 11/13/2016.
 */

public class Contact implements Comparable<Contact>{
    private int id;
    private String name;
    private String number;
    boolean checked;

    public int compareTo(Contact T){
        return name.compareTo(T.name);
    }

    public Contact(int id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.checked = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


}
