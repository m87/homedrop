package org.homedrop.thirdParty.db.sqliteModels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "fileTag")
public class FileTag {
    @DatabaseField(id = true, generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private File file;

    @DatabaseField(canBeNull = false, foreign = true)
    private Tag tag;

    public FileTag(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
