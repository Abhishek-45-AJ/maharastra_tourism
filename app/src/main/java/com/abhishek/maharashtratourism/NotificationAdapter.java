package com.abhishek.maharashtratourism;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private List<GuideNotificationClass> notificationList;

    public NotificationAdapter(List<GuideNotificationClass> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {

        GuideNotificationClass notification = notificationList.get(position);

        holder.userNameTextView.setText(notification.getUserName());

        // Format timestamp to readable date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(notification.getTimestamp()));
        holder.timestampTextView.setText(formattedDate);

        // Display icon based on notification type
        if (notification.getType().equals("call")) {
            holder.notificationIcon.setImageResource(R.drawable.number);
            holder.notificationText.setText("called you.");
        } else if (notification.getType().equals("message")) {
            holder.notificationIcon.setImageResource(R.drawable.baseline_message_24);
            holder.notificationText.setText("sent you a message.");
        }

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    public void updateList(List<GuideNotificationClass> newList) {
//        notificationList = newList;
//        notifyDataSetChanged();
//    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationIcon;
        TextView userNameTextView, notificationText, timestampTextView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationIcon = itemView.findViewById(R.id.notificationIcon);
            userNameTextView = itemView.findViewById(R.id.notificationUserName);
            notificationText = itemView.findViewById(R.id.notificationText);
            timestampTextView = itemView.findViewById(R.id.notificationTimestamp);
        }
    }
}
