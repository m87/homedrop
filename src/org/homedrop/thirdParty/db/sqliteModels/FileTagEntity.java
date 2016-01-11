package org.homedrop.thirdParty.db.sqliteModels;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.homedrop.core.model.FileTag;
import org.homedrop.core.utils.Identifiable;

@DatabaseTable(tableName = "fileTag")
public class FileTagEntity implements Identifiable {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false, foreign = true)
    private FileEntity file;

    @DatabaseField(canBeNull = false, foreign = true)
    private TagEntity tag;

    public FileTagEntity(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FileEntity getFile() {
        return file;
    }

    public void setFile(FileEntity file) {
        this.file = file;
    }

    public TagEntity getTag() {
        return tag;
    }

    public void setTag(TagEntity tag) {
        this.tag = tag;
    }
}
