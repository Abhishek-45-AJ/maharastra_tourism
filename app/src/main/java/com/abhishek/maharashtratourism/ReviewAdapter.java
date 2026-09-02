package com.abhishek.maharashtratourism;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_card, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvReviewerName.setText(review.getUserName());
        holder.tvReviewRating.setText("★ " + review.getRating());
        holder.tvReviewComment.setText(review.getComment());

        if (review.getPhotoUrls() != null && !review.getPhotoUrls().isEmpty()) {
            holder.recyclerViewPhotos.setVisibility(View.VISIBLE);
            ReviewPhotoAdapter photoAdapter = new ReviewPhotoAdapter(context, review.getPhotoUrls());
            holder.recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerViewPhotos.setAdapter(photoAdapter);
        } else {
            holder.recyclerViewPhotos.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvReviewRating, tvReviewComment;
        RecyclerView recyclerViewPhotos;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewRating = itemView.findViewById(R.id.tvReviewRating);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            recyclerViewPhotos = itemView.findViewById(R.id.recyclerViewReviewPhotos);
        }
    }
}
