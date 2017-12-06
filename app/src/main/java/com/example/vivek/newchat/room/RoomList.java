package com.example.vivek.newchat.room;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.newchat.R;
import com.example.vivek.newchat.authentication.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomList extends Fragment {


    private Button btnadd;
    private EditText etroom;
    private ListView lv;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofrooms= new ArrayList<>();
    String name;
    private DatabaseReference dbr;
    private AlertDialog.Builder builder;
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbr= FirebaseDatabase.getInstance().getReference().getRoot();
        setHasOptionsMenu(true);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseAuth.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_room_list, container, false);

        btnadd = myview.findViewById(R.id.btnadd);
        etroom = myview.findViewById(R.id.etroom);
        lv = myview.findViewById(R.id.list);

        getActivity().setTitle("Rooms Available");

        return myview;
    }


    @Override
    public void onStart() {
        super.onStart();

        arrayAdapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listofrooms);
        lv.setAdapter(arrayAdapter);
        request_user_name();

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map= new HashMap<String, Object>();
                map.put(etroom.getText().toString(),"");
                dbr.updateChildren(map);
            }
        });


        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set= new HashSet<String>();
                Iterator i=dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                listofrooms.clear();
                listofrooms.addAll(set);
                arrayAdapter.notifyDataSetChanged();
                etroom.setText("");
                etroom.setHint("Enter a new room name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(),"Cancelled", Toast.LENGTH_SHORT).show();
            }
        });



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Bundle args = new Bundle();
                args.putString("user_name", name);
                args.putString("room_name",((TextView)view).getText().toString());
/*
                Intent intent=new Intent(getActivity(),ChatRoomFragment.class);
                intent.putExtra("room_name",((TextView)view).getText().toString());
                intent.putExtra("user_name",name);
*/
                FragmentManager fm=getFragmentManager();
                fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {

                    }
                });
                FragmentTransaction ft=fm.beginTransaction();
                ChatRoomFragment crf= new ChatRoomFragment();
                crf.setArguments(args);
                ft.replace(R.id.fraglay,crf,"roomlist");
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
                String s = parent.getItemAtPosition(i).toString();
                arrayAdapter.remove(s);
                arrayAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }



    private void request_user_name() {
        builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter name");

        final EditText et= new EditText(getActivity());
        builder.setView(et);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = et.getText().toString();
            }
        });

        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                request_user_name();
            }
        });
        builder.show();
    }



@Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu,menuInflater);
        MenuInflater inflater=getActivity().getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
    }


    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.logout_id:
                LoginFragment loginfragment;
                FragmentManager fm=getFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                firebaseAuth.signOut();
                loginfragment= new LoginFragment();
                ft.replace(R.id.fraglay,loginfragment);
                ft.disallowAddToBackStack();
                ft.commit();
                break;
        }
        return true;
    }





    @Override
    public void onResume() {
        super.onResume();

    }
}
