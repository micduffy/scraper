package com.theinnerpeaceguru.crawler.master;
import ai.preferred.venom.uagent.UserAgent;

public class StandardUserAgent implements UserAgent {

    @Override
    public String get() {
        return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    }

}
