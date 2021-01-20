package me.juhezi.mediademo;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class Base64GsonConverterFactory extends Converter.Factory {

    private final Gson mGson;

    public static Base64GsonConverterFactory create() {
        return create(new Gson());
    }

    public static Base64GsonConverterFactory create(Gson gson) {
        return new Base64GsonConverterFactory(gson);
    }

    private Base64GsonConverterFactory(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson is null");
        }
        this.mGson = gson;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(type));
        return new Base64GsonBodyConverter<>(adapter);
    }

    private static class Base64GsonBodyConverter<T> implements Converter<ResponseBody, T> {

        private final TypeAdapter<T> adapter;

        public Base64GsonBodyConverter(TypeAdapter<T> adapter) {
            this.adapter = adapter;
        }

        @Nullable
        @Override
        public T convert(ResponseBody value) throws IOException {
            String temp = value.string();
            temp = new String(Base64.decode(temp, Base64.DEFAULT));
            return adapter.fromJson(temp);
        }
    }

}
