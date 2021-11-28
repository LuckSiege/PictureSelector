package com.yalantis.ucrop;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;

public class OkHttpClientStore {

    private OkHttpClientStore() {}

    public final static OkHttpClientStore INSTANCE = new OkHttpClientStore();

    private OkHttpClient client;

    /**
     * @return stored OkHttpClient if it was already set,
     *         or just an instance created via empty constructor
     *         and store it
     */
    @NonNull
    public OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

    /**
     * @param client OkHttpClient for downloading bitmap form remote Uri,
     *               it may contain any preferences you need
     */
    void setClient(@NonNull OkHttpClient client) {
        this.client = client;
    }
}
