package org.homedrop.manager;

import org.homedrop.meta.MetaFile;
import org.homedrop.meta.MetaTag;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Tag;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.sqliteModels.TagEntity;

import java.util.Arrays;
import java.util.List;

public class TagsManager {
    private static TagsManager ourInstance = new TagsManager();

    public static TagsManager getInstance() {
        return ourInstance;
    }

    private TagsManager() {
    }


    public void process(MetaFile metaFile, String userName) throws ItemNotFoundException{
        HDDB db = DBManager.getInstance().getDb();
        File file = db.getFileByPath(metaFile.path, db.getUserByName(userName));
        List<Tag> tags = db.getFileTags(file);
        List<MetaTag> metaTags = Arrays.asList(metaFile.tags);

        //diff
        for(Tag tag : tags){
            MetaTag tmp= null;
            for(MetaTag metaTag : metaTags){
                if(tag.getName().equals(metaTag.name)){
                    tmp = metaTag;
                    break;
                }
            }
            if(null != tmp){
                metaTags.remove(tmp);
            }else{
                unassign(file, tag);
            }
        }
        for(MetaTag metaTag : metaTags){
            assignFromMeta(file, metaTag);
        }


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
