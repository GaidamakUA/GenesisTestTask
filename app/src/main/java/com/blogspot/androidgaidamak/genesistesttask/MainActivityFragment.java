package com.blogspot.androidgaidamak.genesistesttask;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.blogspot.androidgaidamak.genesistesttask.network.GsonRequest;
import com.blogspot.androidgaidamak.genesistesttask.network.Protocol;
import com.blogspot.androidgaidamak.genesistesttask.network.VolleySingleton;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = "MainActivityFragment";
    public static final String URL = "http://api.naij.com/test.json";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_article, container, false);
        final LinearLayout linearLayout =
                ((LinearLayout) mainView.findViewById(R.id.mainLinearLayout));
        GsonRequest<Protocol> request = new GsonRequest<>(Request.Method.GET, URL, Protocol.class,
                null, null, new Response.Listener<Protocol>() {
            @Override
            public void onResponse(Protocol response) {
                Log.v(TAG, "title=" + response.title + "; created=" + response.created);
                HtmlParser parser = new HtmlParser(linearLayout, getActivity());
                parser.parse(response.content);
            }
        }, null);
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
        return mainView;
    }
}
