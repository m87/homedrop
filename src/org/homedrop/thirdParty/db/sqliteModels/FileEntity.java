package org.homedrop.thirdParty.db.sqliteModels;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.homedrop.core.model.File;
import org.homedrop.core.model.User;

import java.util.Date;

@DatabaseTable(tableName = "files")
public class FileEntity extends File {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private String parentPath;

    @DatabaseField(canBeNull = false)
    private String path;

    @DatabaseField(canBeNull = true)
    private long checkSum;

    @DatabaseField(canBeNull = true)
    private long version;

    @DatabaseField(canBeNull = true, dataType = DataType.DATE)
    private Date lastChange;

    @DatabaseField(canBeNull = true)
    private FileType type;

    @DatabaseField(canBeNull = false, foreign = true)
    private UserEntity owner;

    public FileEntity(){

    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getParentPath() {
        return parentPath;
    }
    public void setParentPath(String parentPath) { this.parentPath = parentPath; }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public long getCheckSum() {
        return checkSum;
    }
    public void setCheckSum(long checkSum) {
        this.checkSum = checkSum;
    }

    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastChange() {
        return lastChange;
    }
    public void setLastChange(Date lastChange) {
        this.lastChange = lastChange;
    }

    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = (UserEntity) owner;
    }

    @Override
    public FileType getType() {
        return type;
    }

    @Override
    public void setType(FileType type) {
        this.type = type;
    }
}
