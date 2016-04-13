package nu.info.zeeshan.rnf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentFullStory extends Fragment {
    WebView webView;

    public FragmentFullStory() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_full_story, container, false);
        webView = (WebView) rootView.findViewById(R.id.webview);
        String url = getArguments().getString("url");
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        return webView;
    }
}
