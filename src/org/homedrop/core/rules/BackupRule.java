package org.homedrop.core.rules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.homedrop.meta.MetaRule;
import org.homedrop.BackupRuleDummy;

public class BackupRule extends Rule{
    private long deadline; //-1 if not important // backup life
    private int maxHistory; // max number of backups
    private boolean keepAfterDeletion; //keep after deletion // new file with the same path will inharit backups
    private boolean keepAfterMove; //keep after deletion // new file with the same path will inharit backups // in dst act like orinary file
    //replace backups in dst with my backups
    private boolean replaceAfterMove; //replace dst backups with src backpus
    public BackupRule(MetaRule rule) {
        super(rule);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        BackupRuleDummy tmp = gson.fromJson(rule.json, BackupRuleDummy.class);
        this.deadline = tmp.deadline;
        this.maxHistory = tmp.maxHistory;
        this.keepAfterDeletion = tmp.keepAfterDeletion;
        this.keepAfterMove = tmp.keepAfterMove;
        this.replaceAfterMove = tmp.replaceAfterMove;


    }

    public int getMaxHistory() {
        return maxHistory;
    }

    public long getDeadline() {
        return deadline;
    }

    public boolean isKeepAfterDeletion() {
        return keepAfterDeletion;
    }

    public boolean isKeepAfterMove() {
        return keepAfterMove;
    }

    public boolean isReplaceAfterMove() {
        return replaceAfterMove;
    }

}
