package io.github.arieslab.dtolocal;

import java.io.Serializable;
import java.util.Date;

public class Commit implements Serializable {
    private static final long serialVersionUID = 1L;

    public String id;
    public String authorName;
    public Date date;
    public String msg;
    public String tag;

    public Commit(String id, String authorName, Date date, String msg) {
        this.id = id;
        this.authorName = authorName;
        this.date = date;
        this.msg = msg;
    }

    public Commit(String id, String authorName, Date date, String msg, String tag) {
        this.id = id;
        this.authorName = authorName;
        this.date = date;
        this.msg = msg;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                ", authorName='" + authorName + '\'' +
                ", date=" + date +
                ", msg='" + msg + '\'' +
                ", tag='" + tag + '\'' +
                '}';
    }
}
