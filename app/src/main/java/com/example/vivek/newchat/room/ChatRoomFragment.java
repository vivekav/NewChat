package com.example.vivek.newchat.room;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vivek.newchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment {

    private DatabaseReference dbrroot;
    private EditText etchat;
    private Button btnchat;
    private TextView tvtext;
    private String username,roomname,temp_key;
    private String chat_message, chat_username;
    private Map<String,Object> map;
    private FragmentManager frag;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_chat_room, container, false);

        etchat = myview.findViewById(R.id.etchat);
        tvtext = myview.findViewById(R.id.tv);
        btnchat = myview.findViewById(R.id.btnsend);


        username = getArguments().getString("user_name");
        roomname=getArguments().getString("room_name");
//     username = getActivity().getIntent().getExtras().getString("user_name");
 //       roomname = getActivity().getIntent().getExtras().getString("room_name");
        getActivity().setTitle("Room:" + roomname);

        dbrroot = FirebaseDatabase.getInstance().getReference().child(roomname);
        map = new HashMap<String, Object>();
        temp_key = dbrroot.push().getKey();
        dbrroot.updateChildren(map);

        return myview;
    }


    @Override
    public void onStart() {
        super.onStart();

        btnchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference messages= dbrroot.child(temp_key);
                Map<String,Object> map2=new HashMap<String,Object>();
                map2.put("name",username);
                map2.put("messages", etchat.getText().toString());
                messages.updateChildren(map2);
                etchat.setText("");
                etchat.setHint("Type your message here");
            }
        });




        dbrroot.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_data(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_data(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                append_data(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void append_data(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            chat_message = (String) ((DataSnapshot) i.next()).getValue();
            chat_username = (String) ((DataSnapshot) i.next()).getValue();
            tvtext.append(chat_username + ": " + chat_message + " \n\n");
        }

    }


}
