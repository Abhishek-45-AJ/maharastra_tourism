package com.abhishek.maharashtratourism;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class eventsCardAdapter extends RecyclerView.Adapter<eventsCardAdapter.eventViewHolder> {

    private List<Event> eventlist;
    public eventsCardAdapter(List<Event> eventlist){
        this.eventlist=eventlist;

    }

    @NonNull
    @Override
    public eventsCardAdapter.eventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_layout,parent,false);
        return new eventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull eventsCardAdapter.eventViewHolder holder, int position) {
        Event events=eventlist.get(position);
        holder.eventHeading.setText(events.getName());
        holder.eventDate.setText(events.getStartDate()+" to "+events.getEndDate());
        holder.eventLocation.setText(events.getAddress());
        holder.eventDescription.setText(events.getDescription());
        holder.eventEntryFee.setText(events.getEntryFee());
        holder.eventWebsite.setText(events.getWebsite());
        holder.eventContact.setText(events.getContact());

        Picasso.get().load(events.getImageUrl()).placeholder(R.drawable.dahihandi).into(holder.eventImage);
//        holder.itemView.setOnClickListener(v -> listner.onItemClick(place));
    }

    @Override
    public int getItemCount() {
        return eventlist.size();
    }

    public class eventViewHolder extends RecyclerView.ViewHolder {
        TextView eventHeading,eventDate,eventLocation,eventDescription,eventEntryFee,eventWebsite,eventContact;
        ImageView eventImage;

        public eventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventHeading=itemView.findViewById(R.id.eventHeading);
            eventDate=itemView.findViewById(R.id.eventDate);
            eventLocation=itemView.findViewById(R.id.eventLocation);
            eventDescription=itemView.findViewById(R.id.eventDescription);
            eventEntryFee=itemView.findViewById(R.id.eventEntryFee);
            eventImage=itemView.findViewById(R.id.eventImage);
            eventWebsite=itemView.findViewById(R.id.eventWebsite);
            eventContact=itemView.findViewById(R.id.eventContact);
        }
    }
}
