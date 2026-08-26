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

public class popularFestivalCardAdapter extends RecyclerView.Adapter<popularFestivalCardAdapter.popularFestivalHolder> {

    private List<Event> festiavlList;
    private OnItemClickListner listner;
    public interface OnItemClickListner{
        public void onItemClick(Event e);
    }

    public popularFestivalCardAdapter(List<Event> festiavlList, HomeFragment listner) {
        this.festiavlList = festiavlList;
        this.listner=listner;
    }

    @NonNull
    @Override
    public popularFestivalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.sugessted_destination_card,parent,false);

        return new popularFestivalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull popularFestivalHolder holder, int position) {
        Event festivals=festiavlList.get(position);
        holder.festivalName.setText(festivals.getName());
        Picasso.get().load(festivals.getImageUrl()).placeholder(R.drawable.baseline_image_24).into(holder.festivalImage);

        holder.itemView.setOnClickListener(v -> listner.onItemClick(festivals));
    }

    @Override
    public int getItemCount() {
        return festiavlList.size();
    }

    public class popularFestivalHolder extends RecyclerView.ViewHolder {
        TextView festivalName;
        ImageView festivalImage;

        public popularFestivalHolder(@NonNull View itemView) {
            super(itemView);
            festivalName=itemView.findViewById(R.id.destinationName);
            festivalImage=itemView.findViewById(R.id.destinationImage);
        }
    }
}
