package com.yandex.app.adapters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import com.yandex.app.model.Task;


public class GsonAdapt {

    private GsonAdapt() {
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapt())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapt())
                .create();
    }
}
