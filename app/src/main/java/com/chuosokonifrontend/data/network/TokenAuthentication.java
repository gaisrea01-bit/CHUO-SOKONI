package com.chuosokonifrontend.data.network;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import java.io.IOException;

public class TokenAuthentication implements Authenticator {
    private final TokenManager tokenManager;

    public TokenAuthentication(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        tokenManager.clear();
        return null;
    }
}
