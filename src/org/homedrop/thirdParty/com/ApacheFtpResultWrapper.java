package org.homedrop.thirdParty.com;

import org.apache.ftpserver.ftplet.FtpletResult;
import org.homedrop.Result;

/** Apache result representation */
public class ApacheFtpResultWrapper implements FtpResultWrapper<FtpletResult>{

    @Override
    public Result from(FtpletResult res) {
        return null;
    }

    @Override
    public FtpletResult to(Result res) {
        return null;
    }
}
