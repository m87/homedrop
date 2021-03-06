package org.homedrop.manager;

import org.homedrop.RulesCommons;
import org.homedrop.core.utils.Log;
import org.homedrop.core.utils.LogTag;
import org.homedrop.meta.MetaFile;
import org.homedrop.meta.MetaRule;
import org.homedrop.meta.MetaSetting;
import org.homedrop.core.model.Rule;
import org.homedrop.core.utils.exceptions.ItemNotFoundException;
import org.homedrop.thirdParty.db.HDDB;
import org.homedrop.thirdParty.db.sqliteModels.RuleEntity;

import java.util.Date;
import java.util.List;

public class RulesManager {
    private static RulesManager ourInstance = new RulesManager();

    public static RulesManager getInstance() {
        return ourInstance;
    }

    private RulesManager() {
    }

    public void addFromMeta(MetaRule metaRule) throws ItemNotFoundException {
        HDDB db = DBManager.getInstance().getDb();
        Rule rule = null;
        if(metaRule.type == RulesCommons.BACKUP_RULE){
            List<Rule> rules = db.getValidSpecificRulesByType(metaRule.ownerName, RulesCommons.BACKUP_RULE,metaRule.filePath);
            if(rules.size()==0){
                rule = new RuleEntity();
            }else{
                rule = rules.get(0);
            }
        }else{
            rule = new RuleEntity();
        }
        rule.setOwner(db.getUserByName(metaRule.ownerName));
        rule.setFilePath(metaRule.filePath);
        rule.setBody(metaRule.json);
        rule.setHoldsSince(new Date(metaRule.holdsSince));
        rule.setHoldsUntil(new Date(metaRule.holdsUntil));
        rule.setType(metaRule.type);

        db.addRule(rule);


    }
    public void delete(Rule rule){
    }

    public void process(java.io.File file, int specialKey){

    }

    public void addLocalFromMeta(MetaFile file){
        for(MetaRule rule : file.rules){
            try {
                addFromMeta(rule);
            } catch (ItemNotFoundException e) {
                Log.d(LogTag.DB, "home not found");
            }
        }

    }
    public void addGlobalFromMeta(MetaSetting setting){
        for(MetaRule rule : setting.rules){
            try {
                addFromMeta(rule);
            } catch (ItemNotFoundException e) {
                Log.d(LogTag.DB, "home not found");
            }
        }

    }


    public void process(MetaSetting setting){

    }

}
