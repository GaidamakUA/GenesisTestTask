package com.blogspot.androidgaidamak.genesistesttask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.blogspot.androidgaidamak.genesistesttask.network.VolleySingleton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by gaidamak on 6/11/15.
 */
public class HtmlParser {
    private static final String TAG = "HtmlParser";
    private LinearLayout mLinearLayout;
    private Context mContext;

    public HtmlParser(LinearLayout linearLayout, Context context) {
        mLinearLayout = linearLayout;
        mContext = context;
    }

    public void parse(String s) {
        Document document = Jsoup.parseBodyFragment(s);
        // html element and body element
        Element body = document.child(0).child(1);
        for (Element element : body.children()) {
            handleTag(element);
        }
    }

    private void handleTag(final Element element) {
        Log.v(TAG, "element=" + element.nodeName());
        Log.v(TAG, "children=" + element.children().size());

        if (element.nodeName().equals("p")) {
            Log.v(TAG, "hasText=" + element.hasText());
//            if (element.childNodeSize() == 0) {
            if (element.hasText()) {
                Log.v(TAG, "text:" + element.html());

                TextView textView = new TextView(mContext);
                textView.setText(Html.fromHtml(element.html()));
                mLinearLayout.addView(textView);
            } else {
                for (Element child : element.children()) {
                    handleTag(child);
                }
            }
        } else if (element.nodeName().equals("div")) {
            for (Element child : element.children()) {
                handleTag(child);
            }
        } else if (element.nodeName().equals("a")) {
            // making assumption that <a> tag can only contain <img> tag
            Element image = element.child(0);
            int imageWidth = Integer.parseInt(image.attr("width"));
            int imageHeight = Integer.parseInt(image.attr("height"));

            NetworkImageView networkImageView = new NetworkImageView(mContext);
            mLinearLayout.addView(networkImageView, imageWidth, imageHeight);

            String imageSrc = image.attr("src");
            networkImageView.setImageUrl(imageSrc,
                    VolleySingleton.getInstance(mContext).getImageLoader());

            networkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = element.attr("href");

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    mContext.startActivity(i);
                }
            });
        } else if (element.nodeName().equals("iframe")) {
            Log.v(TAG, "outerHtml=" + element.outerHtml());
            int videoWidth = Integer.parseInt(element.attr("width"));
            int videoHeight = Integer.parseInt(element.attr("height"));

            WebView webView = new WebView(mContext);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.setWebChromeClient(new WebChromeClient());

            mLinearLayout.addView(webView, videoWidth, videoHeight);
            webView.loadData("<iframe frameborder=\"0\" height=\"315\" src=\"//www.youtube.com/embed/yF0yPOhcHIk\" width=\"420\"></iframe>", "text/html", null);
        }
    }
}
