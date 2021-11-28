package com.yalantis.ucrop;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;

public class UCropInitializer {

    /**
     * @param client OkHttpClient for downloading bitmap form remote Uri,
     *               it may contain any preferences you need
     */
    public UCropInitializer setOkHttpClient(@NonNull OkHttpClient client) {
        OkHttpClientStore.INSTANCE.setClient(client);
        return this;
    }

}
