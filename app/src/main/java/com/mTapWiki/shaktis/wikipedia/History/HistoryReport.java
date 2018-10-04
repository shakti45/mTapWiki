package com.mTapWiki.shaktis.wikipedia.History;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mTapWiki.shaktis.wikipedia.Article.Article;
import com.mTapWiki.shaktis.wikipedia.Article.ArticleAdapter;
import com.mTapWiki.shaktis.wikipedia.Article.WikiDetail;
import com.mTapWiki.shaktis.wikipedia.Helper.Volley.MyDividerItemDecoration;
import com.mTapWiki.shaktis.wikipedia.Helper.Volley.VolleyController;
import com.mTapWiki.shaktis.wikipedia.Login.SharedPreference.SharedPrefManager;
import com.mTapWiki.shaktis.wikipedia.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryReport extends Fragment implements ArticleAdapter.FilteredArticleListListener {

    private List<Article> articleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ArticleAdapter mAdapter;
    private DatabaseReference ref;
    View view;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=null;
        view=inflater.inflate(R.layout.content_main,container,false);
        recyclerView = view.findViewById(R.id.recycler_view);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("History");
        setRecycler();
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setRecycler() {
        mAdapter = new ArticleAdapter(articleList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);


        prepareWikiData();
    }

    @Override
    public void onArticleSelected(Article article) {
        callFullUrl(String.valueOf(article.getPageID()),article.getTitle());
    }

    public void prepareWikiData() {
        articleList.clear();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("user1/"+ SharedPrefManager.getInstance(getActivity()).getUser().getUsername()+"/");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> kids = dataSnapshot.getChildren();
                for (DataSnapshot data: kids){
                History post = data.getValue(History.class);
                System.out.println("Read success = "+post);
                Article article = new Article();
                article.setPageID(Integer.parseInt(post.getPageID()));
                article.setImgSrc(post.getImgSrc());
                article.setTitle(post.getTitle());
                article.setExtract(post.getTime());
                articleList.add(article);
                mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
    public void callFullUrl(final String pageid,final String title){
        String url = "https://en.wikipedia.org/w/api.php?" +
                "action=query" +
                "&prop=info" +
                "&pageids="+ pageid+
                "&inprop=url"+
                "&format=json";

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String furl = (String)response.getJSONObject("query").getJSONObject("pages").getJSONObject(pageid).get("fullurl");
                            if(furl!=null){
                                Intent i = new Intent(getActivity(),WikiDetail.class);
                                i.putExtra("url",furl);
                                i.putExtra("title",title);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        })
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000;
                    final long cacheExpired = 24 * 60 * 60 * 1000;
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        } ;
        VolleyController.getInstance().addToRequestQueue(req);
    }





}