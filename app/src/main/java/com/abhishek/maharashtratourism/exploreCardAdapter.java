package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class exploreCardAdapter extends RecyclerView.Adapter<exploreCardAdapter.exploreViewHolder2> {

    private List<Place> explorelist;
    private OnItemClickListner listner;
    public interface OnItemClickListner{
        void onItemClick(Place place);
    }

    public exploreCardAdapter(List<Place> explorelist,OnItemClickListner listner) {
        this.explorelist=explorelist;
        this.listner=listner;
    }

    @NonNull
    @Override
    public exploreViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_card_layout,parent,false);
        return new exploreCardAdapter.exploreViewHolder2(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull exploreViewHolder2 holder, int position) {
        Place place=explorelist.get(position);
        holder.exploreHeading.setText(place.getName());
        holder.exploreLocation.setText(place.getCity());
        if(place.getDescription().length() > 200){
            holder.exploreDescription.setText(place.getDescription().substring(0,200)+"...\nRead more");
        }else{
            holder.exploreDescription.setText(place.getDescription());
        }

        Picasso.get().load(place.getImageUrl()).placeholder(R.drawable.dahihandi).into(holder.exploreImage);
        holder.itemView.setOnClickListener(v -> listner.onItemClick(place));
    }

    @Override
    public int getItemCount() {
        return explorelist.size();
    }

    public class exploreViewHolder2 extends RecyclerView.ViewHolder {
        TextView exploreHeading,exploreLocation,exploreDescription;
        ImageView exploreImage;

        public exploreViewHolder2(@NonNull View itemView) {
            super(itemView);
            exploreHeading=itemView.findViewById(R.id.exploreHeading);
            exploreLocation=itemView.findViewById(R.id.exploreLocation);
            exploreDescription=itemView.findViewById(R.id.exploreDescription);
            exploreImage=itemView.findViewById(R.id.exploreImage);

        }
    }
}
