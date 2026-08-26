package com.abhishek.maharashtratourism;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class suggestedDestinationCardAdapter extends RecyclerView.Adapter<suggestedDestinationCardAdapter.DestinationViewHolder>{

    private List<Place> destination;
    private String userId;
    private DatabaseReference favoritesRef;

    private OnItemClickListner listner;
    public interface OnItemClickListner{
        void onItemClick(Place place);
    }

    public suggestedDestinationCardAdapter(List<Place> destination,OnItemClickListner listner,String userId) {
        this.destination=destination;
        this.listner=listner;
        this.userId=userId;
        this.favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
    }

    @NonNull
    @Override
    public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sugessted_destination_card,parent,false);
        return new DestinationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationViewHolder holder, int position) {
        Place destinations=destination.get(position);
        holder.destinationName.setText(destinations.getName());
        Picasso.get().load(destinations.getImageUrl()).placeholder(R.drawable.baseline_image_24).into(holder.destinationImage);

        favoritesRef.child(destinations.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.likeUnlikePlace.setImageResource(R.drawable.baseline_favorite);
                    holder.likeUnlikePlace.setTag("liked");
                } else {
                    holder.likeUnlikePlace.setImageResource(R.drawable.baseline_favorite_border);
                    holder.likeUnlikePlace.setTag("unliked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.likeUnlikePlace.setOnClickListener(v -> {
            if ("unliked".equals(holder.likeUnlikePlace.getTag())) {
                // Add to favorites
                favoritesRef.child(destinations.getName()).setValue(true);
                holder.likeUnlikePlace.setImageResource(R.drawable.baseline_favorite);
                holder.likeUnlikePlace.setTag("liked");
            } else {
                // Remove from favorites
                favoritesRef.child(destinations.getName()).removeValue();
                holder.likeUnlikePlace.setImageResource(R.drawable.baseline_favorite_border);
                holder.likeUnlikePlace.setTag("unliked");
            }
        });

        holder.itemView.setOnClickListener(v -> listner.onItemClick(destinations));
    }

    @Override
    public int getItemCount() {
        return destination.size();
    }

    public static class DestinationViewHolder extends RecyclerView.ViewHolder{
        TextView destinationName;
        ImageView destinationImage;
        ImageButton likeUnlikePlace;

        public DestinationViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationName=itemView.findViewById(R.id.destinationName);
            destinationImage=itemView.findViewById(R.id.destinationImage);
            likeUnlikePlace=itemView.findViewById(R.id.likeUnlikePlace);
        }

    }
}
