package com.abhishek.maharashtratourism;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private Context context;
    private List<Hotel> hotelList;

    public HotelAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelAdapter.HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_hotel_card, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelAdapter.HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        holder.hotelName.setText(hotel.getName());
        holder.hotelPrice.setText(hotel.getPricePerNight());
        holder.hotelTypeBadge.setText(hotel.getType());
        holder.hotelRating.setText(String.format("%.1f (%d)", hotel.getAverageRating(), hotel.getTotalReviews()));

        if (hotel.getImage() != null && !hotel.getImage().isEmpty()) {
            Picasso.get().load(hotel.getImage()).placeholder(R.drawable.logo_image).into(holder.hotelImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaceDetailActivity.class);
            intent.putExtra("isHotel", true);
            intent.putExtra("hotelId", hotel.getHotelId());
            intent.putExtra("placeName", hotel.getName());
            intent.putExtra("placeCity", hotel.getCity());
            intent.putExtra("placeDescription", hotel.getDescription());
            intent.putExtra("placeEntryFee", hotel.getPricePerNight()); // Repurposing entry fee for price
            intent.putExtra("placeWebsite", hotel.getWebsite());
            intent.putExtra("placeContact", hotel.getContact());
            intent.putExtra("placeImage", hotel.getImage());
            intent.putExtra("placeLatitude", "19.7515");
            intent.putExtra("placeLongitude", "75.7139");
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView hotelImage;
        TextView hotelName, hotelPrice, hotelTypeBadge, hotelRating;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelImage = itemView.findViewById(R.id.hotelImage);
            hotelName = itemView.findViewById(R.id.hotelName);
            hotelPrice = itemView.findViewById(R.id.hotelPrice);
            hotelTypeBadge = itemView.findViewById(R.id.hotelTypeBadge);
            hotelRating = itemView.findViewById(R.id.hotelRating);
        }
    }
}
