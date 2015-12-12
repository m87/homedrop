package org.homedrop.thirdParty.server;

import org.homedrop.core.conifg.Common;

public class ServerFactory {
    public static FtpServer createServer(String type){
        switch (type){
            case Common.APACHE:{
                return new ApacheFtpServer();
            }
        }
        return new ApacheFtpServer();
    }
}
