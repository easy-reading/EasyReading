package nu.info.zeeshan.rnf.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import nu.info.zeeshan.rnf.R;

/**
 * Created by Zeeshan Khan on 1/15/2016.
 */
public class AddItemViewHolder extends RecyclerView.ViewHolder {
    AdView adView;
    public AddItemViewHolder(View parent){
        super(parent);
        adView = (AdView) parent.findViewById(R.id.adView);
    }

    public void initAd(){
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("3451D7D6CB474027A1B4D8F8499A52E3").build();
        adView.loadAd(adRequest);
    }
}
