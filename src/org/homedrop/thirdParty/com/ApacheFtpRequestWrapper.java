package org.homedrop.thirdParty.com;

import org.apache.ftpserver.ftplet.FtpRequest;
import org.homedrop.Command;
import org.homedrop.Request;
import org.homedrop.core.Default;

import java.util.Map;

/** Apache ftp request representation */
public class ApacheFtpRequestWrapper implements FtpRequestWrapper<FtpRequest>{
    @Override
    public Map<String, String> argsAsMap(String args) {
        return null;
    }

    @Override
    public String[] argsAsArray(String args) {
        if(null == args){
            return null;
        }
        return args.split(Default.ARG_SEP);
    }

    @Override
    public Request from(FtpRequest req, String userName, int special) {
        Command cmd = new Command(req.getCommand(), /*argsAsArray(*/new String[]{req.getArgument()}/*)*/);
        return new Request(cmd, userName, special);
    }

    @Override
    public FtpRequest to(Request req) {
        return null;
    }
}
