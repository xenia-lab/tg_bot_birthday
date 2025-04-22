package org.example;

public class Gift {
    private String name;
    private int personId;
    private String chatId;

    public Gift(String name, int personId, String chatId) {
        this.name = name;
        this.personId = this.personId;
        this.chatId = chatId;
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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return name;
    }
}
