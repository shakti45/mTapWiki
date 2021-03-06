package com.mTapWiki.shaktis.wikipedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.mTapWiki.shaktis.wikipedia.Article.ArticleListFragment;
import com.mTapWiki.shaktis.wikipedia.ContactUs.ContactUsFragment;
import com.mTapWiki.shaktis.wikipedia.GoogleAnalytics.MyApplication;
import com.mTapWiki.shaktis.wikipedia.History.HistoryReport;
import com.mTapWiki.shaktis.wikipedia.Login.SharedPreference.SharedPrefManager;
import com.mTapWiki.shaktis.wikipedia.Login.SharedPreference.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    boolean doubleBackToExitPressedOnce = false;
    FragmentManager fragment;
    android.support.v4.app.FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        User user = SharedPrefManager.getInstance(this).getUser();
        drawer = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wikipedia");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView mTextViewUsername= navigationView.getHeaderView(0).findViewById(R.id.name);
        mTextViewUsername.setText(user.getUsername());
        navigationView.setItemIconTintList(null);
        TextView mtextViewAccountType= navigationView.getHeaderView(0).findViewById(R.id.type);
        mtextViewAccountType.setText("Read");
        fragment = getSupportFragmentManager();
        ft= fragment.beginTransaction();
        ft.replace(R.id.container,new ArticleListFragment());
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().trackScreenView(getLocalClassName());
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@Nullable MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            fragment = getSupportFragmentManager();
            ft= fragment.beginTransaction();
            ft.replace(R.id.container,new ArticleListFragment());
            ft.commit();
        }
        else if (id == R.id.nav_history) {
            fragment = getSupportFragmentManager();
            ft= fragment.beginTransaction();
            ft.replace(R.id.container,new HistoryReport());
            ft.commit();
        }
        else if (id == R.id.nav_logout) {
            Toast.makeText(getApplicationContext(),"Logged out",Toast.LENGTH_LONG).show();
            finish();
            SharedPrefManager.getInstance(getApplicationContext()).logout();
        }
        else if (id == R.id.nav_contact) {
            ft= fragment.beginTransaction();
            ft.replace(R.id.container,new ContactUsFragment());
            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        MyApplication.getInstance().trackEvent("MainActivity","Back Press","mLabelBackPress");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
        else {
            fragment = getSupportFragmentManager();
            ft= fragment.beginTransaction();
            ft.replace(R.id.container,new ArticleListFragment());
            ft.commit();
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_LONG).show();
        }
    }
}
