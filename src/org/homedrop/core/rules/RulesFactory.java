package org.homedrop.core.rules;

import org.homedrop.meta.MetaRule;
import org.homedrop.RulesCommons;

public class RulesFactory {

    public static Rule create(int type, MetaRule rule){
        switch (type){
            case RulesCommons.BACKUP_RULE: return new BackupRule(rule);
        }

        return new NoRule(rule);
    }
}
