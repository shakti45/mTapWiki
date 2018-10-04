package com.mTapWiki.shaktis.wikipedia.Article;

import android.Manifest;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.mTapWiki.shaktis.wikipedia.History.History;
import com.mTapWiki.shaktis.wikipedia.config.Config;
import com.mTapWiki.shaktis.wikipedia.GoogleAnalytics.MyApplication;
import com.mTapWiki.shaktis.wikipedia.Helper.Volley.VolleyController;
import com.mTapWiki.shaktis.wikipedia.Login.SharedPreference.SharedPrefManager;
import com.mTapWiki.shaktis.wikipedia.Helper.Volley.MyDividerItemDecoration;
import com.mTapWiki.shaktis.wikipedia.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;

public class ArticleListFragment extends Fragment implements ArticleAdapter.FilteredArticleListListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private List<Article> articleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ArticleAdapter mAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int MY_LOCATION_REQUEST_CODE =1 ;
    View view;
    private DatabaseReference mDatabase;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Toast.makeText(getActivity(),"View Created",Toast.LENGTH_LONG).show();

        view=null;
        view=inflater.inflate(R.layout.content_main,container,false);
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefresh);
        recyclerView = view.findViewById(R.id.recycler_view);
        if(isPermissionGranted()){
            Toast.makeText(getActivity(),"Location Permission Granted",Toast.LENGTH_SHORT).show();
        }
        setRecycler();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setQueryHint("Type Here...");
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Config.setUrl("query",query.trim());
                prepareWikiData( Config.getUrl());
                if(query.length()==0){
                    Config.setUrl("default",Config.getLatLng());
                    prepareWikiData(Config.getUrl());
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                Config.setUrl("query",query.trim());
                prepareWikiData( Config.getUrl());
                if(query.length()==0){
                    Config.setUrl("default",Config.getLatLng());
                    prepareWikiData(Config.getUrl());
                }
                return false;
            }
        });
    }

    public void setRecycler(){
        mAdapter = new ArticleAdapter(articleList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL,16));
        recyclerView.setAdapter(mAdapter);

        Config.setUrl("def",null);
        prepareWikiData(Config.getUrl());
    }
    public void prepareWikiData(String url){
        articleList.clear();
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject res = response.getJSONObject("query").getJSONObject("pages");
                            Iterator keyItr = res.keys();
                            while(keyItr.hasNext()){
                                int pageID,index;
                                String title,imgSrc,extract;
                                Article  article =new Article();
                                String key = (String) keyItr.next();
                                JSONObject readObj = res.getJSONObject(key);
                                pageID = readObj.getInt("pageid");
                                index = readObj.getInt("index");
                                title = readObj.getString("title");

                                if(readObj.has("thumbnail")){
                                    imgSrc = readObj.getJSONObject("thumbnail").getString("source");
                                    article.setImgSrc(imgSrc);
                                }
                                if(readObj.has("extract")){
                                    extract = readObj.getString("extract");
                                    article.setExtract(extract);
                                }
                                else{
                                    if(readObj.has("terms")){
                                        extract = (String) readObj.getJSONObject("terms").getJSONArray("description").get(0);
                                        article.setExtract(extract);
                                    }
                                    else{
                                        extract = null;
                                    }

                                }
                                article.setPageID(pageID);
                                article.setIndex(index);
                                article.setTitle(title);
                                articleList.add(article);
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
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
        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(req);

    }

    @Override
    public void onArticleSelected(Article article) {
        History history = new History(
                String.valueOf(article.getPageID()),
                article.getTitle(),
                article.getImgSrc(),
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
        );
        mDatabase.child("user1").child(SharedPrefManager.getInstance(getActivity()).getUser().getUsername()).child(String.valueOf(article.getPageID())).setValue(history);

        callFullUrl(String.valueOf(article.pageID),article.title);
        MyApplication.getInstance().trackEvent(SharedPrefManager.getInstance(getActivity()).getUser().getUsername(),"Read",String.valueOf(article.pageID)+" = "+article.title);
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
    public void getLoc() {
        try{
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener( getActivity(),new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                try {
                                    Double lat=location.getLatitude();
                                    Double lng=location.getLongitude();
                                    Config.setLatLng(String.valueOf(lat)+"|"+String.valueOf(lng));
                                }catch (Exception e){
                                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }catch (SecurityException e){
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void checkLocationRequest(){
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex) {

        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        catch(Exception ex) {

        }
        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("GPS not enabled");
            dialog.setPositiveButton("Cool Turn IT on", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {


                }
            });
            dialog.show();
        }
    }
    boolean isPermissionGranted(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                checkLocationRequest();
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                getLoc();
                Config.setUrl("default",Config.getLatLng());
            } catch (SecurityException e) {
                Toast.makeText(getActivity(), "Not a valid request" + e.getMessage(), Toast.LENGTH_LONG);
            }
            return true;
        } else {
            checkLocationRequest();
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);

        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                } catch (SecurityException e) {
                    Toast.makeText(getActivity(), "Not a valid request" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else {
                isPermissionGranted();
                Toast.makeText(getActivity(), "Not a allowed to acess location", Toast.LENGTH_LONG).show();
                try {

                } catch (SecurityException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
