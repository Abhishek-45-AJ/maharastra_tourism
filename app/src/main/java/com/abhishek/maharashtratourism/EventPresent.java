package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventPresent extends Fragment {

    private List<Event> eventlist;
    private RecyclerView eventPresentRecyclerView;
    private eventsCardAdapter adapter;
    private DatabaseReference databaseReference;


    public EventPresent() {
        // Required empty public constructor
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.event_present_fragment, container, false);

       eventPresentRecyclerView=view.findViewById(R.id.eventPresentRecyclerView);
       eventlist=new ArrayList<>();

       adapter=new eventsCardAdapter(eventlist);
       eventPresentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
       eventPresentRecyclerView.setAdapter(adapter);

       databaseReference= FirebaseDatabase.getInstance().getReference("events");
       eventlist.clear();

       Query query=databaseReference.orderByChild("status").startAt("Ongoing").endAt("Ongoing\uf8ff");
       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @SuppressLint("NotifyDataSetChanged")
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ds:snapshot.getChildren()){
                   Event event=ds.getValue(Event.class);
                   if(event != null && !eventlist.contains(event)){
                       eventlist.add(event);
                   }
               }
               adapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(getContext(),"Database error!", Toast.LENGTH_SHORT).show();
           }
       });

        return view;
    }
}