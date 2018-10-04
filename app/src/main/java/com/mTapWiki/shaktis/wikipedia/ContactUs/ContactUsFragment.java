package com.mTapWiki.shaktis.wikipedia.ContactUs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.Toast;

import com.mTapWiki.shaktis.wikipedia.R;

import java.util.ArrayList;


public class ContactUsFragment extends Fragment {

    View view;
    AppCompatImageButton mButtonCallSupport,mButtonSendEmail;
    EditText mEditTextComplaint;

    FragmentManager fragment;
    FragmentTransaction ft;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view=null;
        view=inflater.inflate(R.layout.fragment_contactus,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Contact Us");

        mButtonSendEmail= view.findViewById(R.id.mSendEmailToSupport);
        mButtonCallSupport= view.findViewById(R.id.mButtonCallSupport);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = getChildFragmentManager();
        mButtonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailIntent();
            }
        });

        mButtonCallSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(isPermissionGranted()){
                   call_action("7905367810");
               }

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public void call_action(String number){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        try {
            startActivity(callIntent);
        }catch (SecurityException e){
            Toast.makeText(getActivity(), "Not a valid number"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void sendEmailIntent(){
        ArrayList<Uri> contentUris=new ArrayList<Uri>();
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"shakti45@gmail.com"}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MTapWiki Support Complaint");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text:\n"+ "");
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(emailIntent, "Send email:"),1000);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1000: {
            }
        }
    }
}