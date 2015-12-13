package org.homedrop.thirdParty.db;

import org.homedrop.core.model.File;

import java.util.List;

public interface HDDB {
    void onCreate();
    void onUpgrade();
    void onDrop();
    List<File> getAllFiles();
    void addFile(File file);
}
