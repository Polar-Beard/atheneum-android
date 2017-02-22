package com.satchlapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sara on 2/8/2017.
 */
public class Content {

    private int type;
    private String value;
    private ArrayList<Qualifier> qualifiers;

    public Content(){
        qualifiers = new ArrayList<>();
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    //Returns the position of the newly created qualifier
    public Qualifier addNewQualifier(){
        Qualifier q = new Qualifier();
        qualifiers.add(q);
        return q;
    }

    public void removeQualifier(int index){
        qualifiers.remove(index);
    }

    public List<Qualifier> getQualifiers(){
        return qualifiers;
    }

    public Qualifier getQualifier(int index){
        return qualifiers.get(index);
    }

    public class Qualifier{

        private int type;
        private ArrayList<Specification> specifications;

        public Qualifier(){
            specifications = new ArrayList<>();
        }

        public int getType(){
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Specification addNewSpecification(){
            Specification s = new Specification();
            specifications.add(s);
            return s;
        }

        public void removeSpecification(int index){
            specifications.remove(index);
        }

        public ArrayList<Specification> getSpecifications(){
            return specifications;
        }

        public Specification getSpecification(int index){
            return specifications.get(index);
        }

    }

    public class Specification {

        private String name;
        private String type;
        private String value;

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public Specification setName(String name) {
            this.name = name;
            return this;
        }

        public Specification setType(String type) {
            this.type = type;
            return this;
        }

        public Specification setValue(String value) {
            this.value = value;
            return this;
        }
    }

}
