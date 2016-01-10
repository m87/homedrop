package org.homedrop.thirdParty.com;

import org.apache.ftpserver.ftplet.FtpRequest;
import org.homedrop.Command;
import org.homedrop.Request;

import java.util.Map;

/** Apache ftp request representation */
public class ApacheFtpRequestWrapper implements FtpRequestWrapper<FtpRequest>{
    @Override
    public Map<String, String> argsAsMap(String args) {
        return null;
    }

    @Override
    public String[] argsAsArray(String args) {
        return null;
    }

    @Override
    public Request from(FtpRequest req, String userName) {
        Command cmd = new Command(req.getCommand(), argsAsArray(req.getArgument()));
        return new Request(cmd, userName);
    }

    @Override
    public FtpRequest to(Request req) {
        return null;
    }
}
