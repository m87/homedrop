package org.homedrop.thirdParty.db.sqliteModels;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.model.User;

import java.util.Date;

@DatabaseTable(tableName = "rules")
public class RuleEntity extends Rule {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = true)
    private String filePath;

    @DatabaseField(canBeNull = false)
    private String body;

    @DatabaseField(canBeNull = false, foreign = true)
    private UserEntity owner;

    @DatabaseField(canBeNull = true, dataType = DataType.DATE_LONG)
    private Date holdsSince;

    @DatabaseField(canBeNull = true, dataType = DataType.DATE_LONG)
    private Date holdsUntil;

    @DatabaseField(canBeNull = false)
    private int type;

    @DatabaseField(canBeNull = false)
    private boolean global;

    public RuleEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = (UserEntity) owner;
    }

    public Date getHoldsSince() {
        return holdsSince;
    }

    public void setHoldsSince(Date date) {
        holdsSince = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean getGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public Date getHoldsUntil() {
        return holdsUntil;
    }

    public void setHoldsUntil(Date date) {
        holdsUntil = date;
    }
}
