package com.abhishek.maharashtratourism;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import java.util.List;

public class ReviewPhotoAdapter extends RecyclerView.Adapter<ReviewPhotoAdapter.PhotoViewHolder>{
    private Context context;
    private List<String> photoUrls;

    public ReviewPhotoAdapter(Context context, List<String> photoUrls) {
        this.context = context;
        this.photoUrls = photoUrls;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String url = photoUrls.get(position);
        Picasso.get().load(url).placeholder(R.drawable.logo_image).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return photoUrls != null ? photoUrls.size() : 0;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivReviewPhotoItem);
        }
    }
}
