package org.example;

public class Gift {
    private String name;
    private int personId;

    public Gift(String name, int personId) {
        this.name = name;
        this.personId = this.personId;
    }

    public String getName() {
        return name;
    }

    public int getpersonId() {
        return personId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setpersonId(int personId) {
        this.personId = personId;
    }

    @Override
    public String toString() {
        return name;
    }
}
