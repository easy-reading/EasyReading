package nu.info.zeeshan.rnf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.ArrayList;
import java.util.List;

import nu.info.zeeshan.rnf.data.FeedLoader;
import nu.info.zeeshan.rnf.model.Item;
import nu.info.zeeshan.rnf.util.Constants;
import nu.info.zeeshan.rnf.util.FetchTaskUICallbacks;
import nu.info.zeeshan.rnf.util.Util;

/**
 * Created by Zeeshan Khan on 10/28/2015.
 */
public class FragmentFacebook extends FragmentMain implements LoaderManager
        .LoaderCallbacks<List<Item>>, FetchTaskUICallbacks {

    public static String TAG = "FragmentFacebook";
    ArrayList<String> permissions;
    CallbackManager callbackManager;
    private static final int FACEBOOK_FEED_LOADER_ID = 0;

    public FragmentFacebook() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FACEBOOK_FEED_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {

        callbackManager = CallbackManager.Factory.create();
        permissions = new ArrayList<>();
        permissions.addAll(Constants.FACEBOOK_PERMISSIONS);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Util.log(TAG, loginResult.toString());
                        Util.log(TAG, "login done");
                        getLoaderManager().restartLoader(FACEBOOK_FEED_LOADER_ID, null,
                                FragmentFacebook.this);
                    }

                    @Override
                    public void onCancel() {
                        Util.log(TAG, "cancled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Util.log(TAG, "error->" + exception.getLocalizedMessage() + "");
                        Toast.makeText(getActivity().getApplicationContext(), "Error occured. Try" +
                                " again!!", Toast.LENGTH_SHORT).show();
                    }
                });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * this method will be called when onRefresh action is triggered in MainFragment
     */
    public void startFetchingFeed() {
        new FetchFacebookTask(getActivity(), this).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.log(TAG, "onActivity result" + data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onStart() {
        super.onStart();
        new MarkAllReadTask(getActivity()).execute();
    }

    @Override
    public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
        return new FeedLoader(getActivity(), FeedLoader.TYPE.FACEBOOK);
    }

    @Override
    public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
        itemAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Item>> loader) {
        itemAdapter.addAll(null);
    }

    /**
     * if this called with false that means facebook need to log in
     *
     * @param wasSuccessful
     */
    @Override
    public void taskComplete(boolean wasSuccessful) {
        if (wasSuccessful) {
            getLoaderManager().restartLoader(FACEBOOK_FEED_LOADER_ID, null, this);
        } else {
            //login fb required
            Snackbar.make(swipeRefreshLayout, "Login to facebook", Snackbar.LENGTH_LONG)
                    .setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginManager.getInstance().logInWithReadPermissions(FragmentFacebook
                                    .this, permissions);
                        }
                    }).show();
        }
        stopRefresh();
    }
}
