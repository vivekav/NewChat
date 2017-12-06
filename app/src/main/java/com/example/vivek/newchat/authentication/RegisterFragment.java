package com.example.vivek.newchat.authentication;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivek.newchat.MainActivity;
import com.example.vivek.newchat.R;
import com.example.vivek.newchat.room.RoomList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment {

    private LoginFragment loginfragment;
    private EditText etemail,etpass,etinputname1,etpassmatch;
    private Button btnregister;
    private ProgressDialog progressDialog;
     FirebaseAuth firebaseAuth;
    private AlertDialog alertDialog=null;
    private TextView textview;
    Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        context=getActivity();
        getActivity().setTitle("Register");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_register, container, false);

        textview=myview.findViewById(R.id.tvlog);
        etinputname1 = myview.findViewById(R.id.etinputname1);
        etpassmatch = myview.findViewById(R.id.etpassmatch);
        btnregister = myview.findViewById(R.id.btnregister);
        etemail = myview.findViewById(R.id.etloginmail);
        etpass = myview.findViewById(R.id.etpass);

        return  myview;
    }


    @Override
    public void onStart() {
        super.onStart();

        if(etpassmatch.getText().toString().compareTo(etpass.getText().toString())!=0)
            etpassmatch.setError("Should be same as password");

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });



        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                loginfragment= new LoginFragment();
                ft.replace(R.id.fraglay,loginfragment,"reg");
                ft.disallowAddToBackStack();
                ft.commit();
            }
        });

    }

    private void registerUser() {
        final String inputusername = etinputname1.getText().toString();
        final String checkpass = etpassmatch.getText().toString();
        final String email = etemail.getText().toString().trim();
        final String pass = etpass.getText().toString().trim();

        etinputname1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (TextUtils.isEmpty(inputusername))
                    etinputname1.setError("Input User Name");
            }
        });

        etpass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (TextUtils.isEmpty(pass))
                    etpass.setError("Password is Mandatory for login");
            }
        });

        etemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (TextUtils.isEmpty(email))
                    etemail.setError("Email is mandatory for login");
            }
        });

        etpassmatch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (checkpass.compareTo(pass) != 0)
                    etpassmatch.setError("Should be same as password");
            }
        });

        if (TextUtils.isEmpty(inputusername)) {
            Toast.makeText(getActivity(), "Please Enter Username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getActivity(), "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(checkpass)) {
            Toast.makeText(getActivity(), "Please Re-enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.length() < 6) {
            Toast.makeText(getActivity(), "6 or more characters for password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkpass.compareTo(pass) != 0) {
            Toast.makeText(getActivity(), "Password does not match", Toast.LENGTH_SHORT).show();
            return;
        }


if(!checkInternet()) {
    alertDialog = new AlertDialog.Builder(getActivity()).create();
    alertDialog.setTitle("Info");
    alertDialog.setMessage("Internet not available, Check your internet connectivity and try again");
    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            alertDialog.dismiss();
        }
    });
    alertDialog.show();
}


        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    loginfragment = new LoginFragment();
                    ft.replace(R.id.fraglay, loginfragment);
                    ft.disallowAddToBackStack();
                    ft.commit();
                } else {
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Registeration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


        //to check internet connection

    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onPause() {
        super.onPause();
        if(alertDialog!=null)
            alertDialog.dismiss();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(alertDialog!=null)
            alertDialog.cancel();
    }
}