package nu.info.zeeshan.rnf;

import nu.info.zeeshan.rnf.model.NYTResult;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Zeeshan Khan on 5/14/2016.
 */
public interface NYTApiClient {
    @GET("home.json")
    Call<NYTResult> home();
}
