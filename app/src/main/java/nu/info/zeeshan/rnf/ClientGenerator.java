package nu.info.zeeshan.rnf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Zeeshan Khan on 5/14/2016.
 */
public class ClientGenerator {
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public static NYTApiClient getNYTClient() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("api-key",
                        BuildConfig.NYTIMES_API_KEY).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        }).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.nytimes" +
                ".com/svc/topstories/v2/").addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        return retrofit.create(NYTApiClient.class);
    }

    public static FacebookApiClient getFacebookClient() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api.nytimes" +
                ".com/svc/topstories/v2/").addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(FacebookApiClient.class);
    }
}
