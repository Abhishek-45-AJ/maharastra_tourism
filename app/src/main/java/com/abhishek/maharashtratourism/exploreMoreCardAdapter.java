package com.abhishek.maharashtratourism;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class exploreMoreCardAdapter extends RecyclerView.Adapter<exploreMoreCardAdapter.exploreViewHolder>{

    private List<exploreMoreCard> explore;
    private OnItemClickListner listner;
    public interface OnItemClickListner{
        public void onItemClick(String n);
    }

    public exploreMoreCardAdapter(List<exploreMoreCard> explore, OnItemClickListner listner) {
        this.explore=explore;
        this.listner=listner;
    }

    @NonNull
    @Override
    public exploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_more_card,parent,false);
        return new exploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull exploreViewHolder holder, int position) {
        exploreMoreCard exploreItem=explore.get(position);
        holder.exploreTitle.setText(exploreItem.getName());
        holder.exploreImage.setImageResource(exploreItem.getImageResource());

        holder.itemView.setOnClickListener(v -> listner.onItemClick(exploreItem.getName()));
    }

    @Override
    public int getItemCount() {
        return explore.size();
    }

    public class exploreViewHolder extends RecyclerView.ViewHolder{
        TextView exploreTitle;
        ImageView exploreImage;

        public exploreViewHolder(@NonNull View itemView) {
            super(itemView);
            exploreTitle=itemView.findViewById(R.id.exploreTitle);
            exploreImage=itemView.findViewById(R.id.exploreImage);
        }
    }
}
