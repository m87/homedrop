package org.homedrop.thirdParty.db.sqliteModels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rules")
public class Rule {
    @DatabaseField(id = true, generatedId =  true)
    private long id;

    @DatabaseField(canBeNull = false)
    private int type;

    @DatabaseField(canBeNull = true, foreign = true)
    private File file;

    @DatabaseField(canBeNull = false)
    private String body;

    public void setId(long id) {
        this.id = id;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public int getType() {
        return type;
    }

    public String getBody() {
        return body;
    }
    public Rule(){}
}
