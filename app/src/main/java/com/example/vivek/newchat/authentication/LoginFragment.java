package com.example.vivek.newchat.authentication;



import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;
import android.widget.Toast;


import com.example.vivek.newchat.MainActivity;
import com.example.vivek.newchat.R;
import com.example.vivek.newchat.room.RoomList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText etloginmail, etloginpass;
    private Button btnlogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    android.app.AlertDialog alertDialog;
    RoomList roomlistfragment;
    TextView textview;
    RegisterFragment registerfragment;
    Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            RoomList roomList = new RoomList();
            ft.replace(R.id.fraglay,roomList);
            ft.disallowAddToBackStack();
            ft.commit();
        }
        context=getActivity();
        getActivity().setTitle("Login");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myview=inflater.inflate(R.layout.fragment_login,container,false);

        textview=myview.findViewById(R.id.tvsignup);
        btnlogin=myview.findViewById(R.id.btnSignIn);
        etloginmail=myview.findViewById(R.id.etloginmail);
        etloginpass=myview.findViewById(R.id.etloginpass);
        progressDialog = new ProgressDialog(getActivity());
        firebaseAuth = FirebaseAuth.getInstance();
        android.app.AlertDialog alertDialog=null;



        return myview;
    }




    @Override
    public void onStart() {
        super.onStart();


        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm=getFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                registerfragment= new RegisterFragment();
                ft.replace(R.id.fraglay,registerfragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
       //internet check
            if(!checkInternet()){
                alertDialog =new android.app.AlertDialog.Builder(getActivity()).create();
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
                userLogin();
            }
        });
    }


   private void userLogin(){

       String loginmail = etloginmail.getText().toString().trim();
       String loginpass = etloginpass.getText().toString().trim();

       if (TextUtils.isEmpty(loginmail)) {
           Toast.makeText(getActivity(), "Please Enter Email", Toast.LENGTH_SHORT).show();
           return;
       }
       if (TextUtils.isEmpty(loginpass)) {
           Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
           return;
       }

       progressDialog.setMessage("Logging In...");
       progressDialog.show();

       firebaseAuth.signInWithEmailAndPassword(loginmail,loginpass).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               e.printStackTrace();
           }
       });
       firebaseAuth.signInWithEmailAndPassword(loginmail, loginpass).addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()) {
                   progressDialog.dismiss();

                   Toast.makeText(getActivity(), "Logged In", Toast.LENGTH_SHORT).show();
                   FragmentManager fm=getFragmentManager();
                   FragmentTransaction ft=fm.beginTransaction();
                   roomlistfragment= new RoomList();
                   ft.replace(R.id.fraglay,roomlistfragment);
                   ft.disallowAddToBackStack();
                   ft.commit();


               } else {
                   progressDialog.dismiss();
                   Toast.makeText(getActivity(), "Login failed. Please enter correct credentials", Toast.LENGTH_SHORT).show();
               }
           }
       });
   }


    private boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog != null)
            alertDialog.dismiss();
        if(progressDialog!=null)
            progressDialog.dismiss();
    }


    @Override
    public void onStop() {
        super.onStop();
        if(alertDialog!=null){
        alertDialog.cancel();}
        if(progressDialog!=null)
        progressDialog.cancel();
    }
}