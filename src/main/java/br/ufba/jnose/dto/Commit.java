package br.ufba.jnose.dto;

import java.io.Serializable;
import java.util.Date;

public class Commit implements Serializable {
    private static final long serialVersionUID = 11312L;

    public String id;
    public String name;
    public Date date;
    public String msg;

    public Commit(String id, String name, Date date, String msg) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", msg='" + msg + '\'' +
                '}';
    }
}
