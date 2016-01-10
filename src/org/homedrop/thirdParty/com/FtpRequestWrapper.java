package org.homedrop.thirdParty.com;

import org.homedrop.Request;

import java.util.List;
import java.util.Map;

/** Wrapper for ftp requeset models*/
public interface FtpRequestWrapper<T> {
    Request from(T req);
    T to(Request req);

    Map<String, String> argsAsMap(String args);
    String[] argsAsArray(String args);
}
