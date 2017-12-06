package com.example.vivek.newchat;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.vivek.newchat.authentication.LoginFragment;
import com.example.vivek.newchat.authentication.RegisterFragment;
import com.example.vivek.newchat.room.RoomList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoginFragment loginfragment;

        FragmentManager fm=getFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        loginfragment= new LoginFragment();
        ft.add(R.id.fraglay,loginfragment);
        ft.disallowAddToBackStack();
        ft.commit();

    }

}
