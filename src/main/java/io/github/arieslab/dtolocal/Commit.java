package io.github.arieslab.dtolocal;

import java.io.Serializable;
import java.util.Date;

public record Commit(String id, String authorName, Date date, String msg, String tag) implements Serializable {
    private static final long serialVersionUID = 1L;

    public Commit(String id, String authorName, Date date, String msg) {
        this(id, authorName, date, msg, null);
    }
}
