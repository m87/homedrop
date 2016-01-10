package org.homedrop.thirdParty.com;

import org.homedrop.Result;

/** Wrapper for ftp result models */
public interface FtpResultWrapper<T> {
    Result from(T res);
    T to(Result res);
}
