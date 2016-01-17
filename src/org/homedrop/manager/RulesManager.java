package org.homedrop.manager;

import org.homedrop.MetaRule;
import org.homedrop.core.model.File;
import org.homedrop.core.model.Rule;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.sqliteModels.RuleEntity;

import java.util.Date;

public class RulesManager {
    private static RulesManager ourInstance = new RulesManager();

    public static RulesManager getInstance() {
        return ourInstance;
    }

    private RulesManager() {
    }


    public void addFromMeta(MetaRule metaRule) throws ItemNotFoundException {
        HDDB db = DBManager.getInstance().getDb();
        Rule rule = new RuleEntity();
        rule.setOwner(db.getUserByName(metaRule.ownerName));
        rule.setFile(db.getFileByPath(metaRule.filePath, db.getUserByName(metaRule.ownerName)));
        rule.setBody(metaRule.json);
        rule.setHoldsSince(new Date(metaRule.holdsSince));
        rule.setHoldsUntil(new Date(metaRule.holdsUntil));

        db.addRule(rule);


    }
    public void delete(Rule rule){
    }

}
