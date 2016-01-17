package org.homedrop.manager;

import org.homedrop.MetaTag;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.thirdParty.db.sqliteModels.TagEntity;

public class TagsManager {
    private static TagsManager ourInstance = new TagsManager();

    public static TagsManager getInstance() {
        return ourInstance;
    }

    private TagsManager() {
    }

    public void assignFromMeta(File file, MetaTag metaTag){
        Tag tag = new TagEntity();
        tag.setName(metaTag.name);

        DBManager.getInstance().getDb().addTag(tag);
        DBManager.getInstance().getDb().assignTag(file, tag);

    }
    public void unassign(File file, Tag tag){
        DBManager.getInstance().getDb().unassignTag(file, tag);
    }


}
