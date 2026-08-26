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

public class searchPlaceAdapter extends RecyclerView.Adapter<searchPlaceAdapter.PlaceViewHolder> {

    private List<Place> placeList;
    private OnItemClickListner listner;
    public interface OnItemClickListner{
        void onItemClick(Place place);
    }

    public searchPlaceAdapter(List<Place> placeList,OnItemClickListner listner){
        this.placeList=placeList;
        this.listner=listner;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place,parent,false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place=placeList.get(position);
        holder.placeName.setText(place.getName());
        holder.placeCity.setText(place.getCity());
        Picasso.get().load(place.getImageUrl()).placeholder(R.drawable.logo_image).into(holder.placeImage);

        holder.itemView.setOnClickListener(v -> listner.onItemClick(place));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeName,placeCity;
        ImageView placeImage;

        @SuppressLint({"WrongViewCast", "CutPasteId"})
        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeName=itemView.findViewById(R.id.placeName);
            placeCity=itemView.findViewById(R.id.placeCity);
            placeImage=itemView.findViewById(R.id.placeImage);
        }
    }
}
