package com.tara.downloader;

public interface AuthenticationListener {
    void onTokenReceived(String auth_token);
}
