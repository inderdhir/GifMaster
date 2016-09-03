//package com.inderdhir.gifmaster.core;
//
//
//import com.google.gson.GsonBuilder;
//import com.google.gson.reflect.TypeToken;
//import com.inderdhir.gifmaster.BuildConfig;
//import com.inderdhir.gifmaster.model.GifItem;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.List;
//
//import okhttp3.HttpUrl;
//import okhttp3.Interceptor;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class GiphyServiceGenerator {
//
//    public static final String API_BASE_URL = "https://api.giphy.com/v1/gifs/";
//
//    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//
//    private static Retrofit.Builder builder =
//            new Retrofit.Builder()
//                    .baseUrl(API_BASE_URL)
//                    .addConverterFactory(buildGsonConverter())
//                    .addConverterFactory(GsonConverterFactory.create());
//
//    public static <S> S createService(Class<S> serviceClass) {
//        if (BuildConfig.DEBUG) {
//            httpClient.addInterceptor(new LoggingInterceptor());
//        }
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(final Chain chain) throws IOException {
//                Request original = chain.request();
//                HttpUrl originalHttpUrl = original.url();
//
//                HttpUrl url = originalHttpUrl.newBuilder()
//                        .addQueryParameter("api_key", "dc6zaTOxFJmzC")
//                        .build();
//
//                // Request customization: add request headers
//                Request.Builder requestBuilder = original.newBuilder()
//                        .url(url);
//
//                Request request = requestBuilder.build();
//                return chain.proceed(request);
//            }
//        });
//        Retrofit retrofit = builder.client(httpClient.build()).build();
//        return retrofit.create(serviceClass);
//    }
//
//
//    private static GsonConverterFactory buildGsonConverter() {
//        GsonBuilder gsonBuilder = new GsonBuilder();
//
//        // Adding custom deserializers
//        Type gifItemsListType = new TypeToken<List<GifItem>>() {
//        }.getType();
//        gsonBuilder.registerTypeAdapter(gifItemsListType, new GifDeserializer());
//        return GsonConverterFactory.create(gsonBuilder.create());
//    }
//}
