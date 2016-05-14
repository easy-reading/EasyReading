package nu.info.zeeshan.rnf;

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.model.NYTResult;
import nu.info.zeeshan.rnf.model.NewsItem;
import nu.info.zeeshan.rnf.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * FragmentNews
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentNews extends FragmentMain {
    public static String TAG = FragmentNews.class.getSimpleName();

    public FragmentNews() {
        Util.log(TAG, "fetching news constructor");
    }

    public void startFetchingFeed() {
        Util.log(TAG, "fetching news");
        NYTApiClient client = NYTClientGenerator.getNYTClient();
        client.home().enqueue(new Callback<NYTResult>() {
            @Override
            public void onResponse(Call<NYTResult> call, Response<NYTResult> response) {
                if (response != null && response.isSuccessful()) {
                    Util.log(TAG, "fetching news successful" + response.body().getResults());
                    fillAdapter(response.body().getResults());
                    stopRefresh();
                } else {
                    Util.log(TAG, "fetching news failed gracefully");
                }
            }

            @Override
            public void onFailure(Call<NYTResult> call, Throwable t) {
                Util.log(TAG, "fetching news failed " + t.getLocalizedMessage());
            }
        });
    }

    private void fillAdapter(List<NewsItem> items) {
        if (items != null && items.size() > 0) {
            List<Item> iItems = new ArrayList<>();
            for (Item i : items) {
                iItems.add(i);
            }
            itemAdapter.addAll(iItems);
        }
    }
}
