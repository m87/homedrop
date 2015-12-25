package org.homedrop.core.model;

import java.sql.Date;
import java.util.List;

public class File {

    private long id;
    private String name;
    private String path;
    private List<Long> tags;
    private long checkSum;
    private long version;
    private Date lastChange;
    private long ownerId;
    private List<Long> shareIds;

}
