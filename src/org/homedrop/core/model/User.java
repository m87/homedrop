package org.homedrop.core.model;

public class User {
    private static long ID = 0;
    private String login, passowrd;
    private String home;
    private long id;
    public User(String login, String passowrd, String home){
        this.id = ID++;
        this.login = login;
        this.passowrd = passowrd;
        this.home = home;
    }

    public String getLogin() {
        return login;
    }

    public String getPassowrd() {
        return passowrd;
    }

    public String getHome() {
        return home;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
