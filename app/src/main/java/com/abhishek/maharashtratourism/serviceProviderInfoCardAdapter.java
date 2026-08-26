package com.abhishek.maharashtratourism;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class serviceProviderInfoCardAdapter extends RecyclerView.Adapter<serviceProviderInfoCardAdapter.serviceProviderInfoViewHolder> {

    private List<Guide> guideList;
    String userId;

    public serviceProviderInfoCardAdapter(List<Guide> guideList,String userId) {
        this.guideList = guideList;
        this.userId=userId;
    }

    @NonNull
    @Override
    public serviceProviderInfoCardAdapter.serviceProviderInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.service_provider_info_layout,parent,false);
        return new serviceProviderInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull serviceProviderInfoCardAdapter.serviceProviderInfoViewHolder holder, int position) {
        Guide guides=guideList.get(position);

        holder.guideName.setText(guides.getName());
        if(guides.getProfileImageUrl() != null){
            Picasso.get().load(guides.getProfileImageUrl()).transform(new CropCircleTransformation()).placeholder(R.drawable.baseline_person_24).into(holder.guideImage);
        }
        else{
            holder.guideImage.setImageResource(R.drawable.baseline_person_24);
        }

        holder.guideCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber=guides.getPhone();
                if(phoneNumber!=null && !phoneNumber.isEmpty()){
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + guides.getPhone()));
                    holder.itemView.getContext().startActivity(callIntent);

                    //send notification to guide
                    sendNotificationToGuide(guides.getId(), userId,guides.getName(),"call");
                }
            }
        });

        holder.guideMessageBtn.setOnClickListener(v -> {
            if (guides.getPhone() != null && !guides.getPhone().isEmpty()) {
                String url = "https://api.whatsapp.com/send?phone=" + guides.getPhone()+"&text=" + Uri.encode("Hello, I need your service.");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                holder.guideMessageBtn.getContext().startActivity(intent);

                //send notification to guide
                sendNotificationToGuide(guides.getId(), userId,guides.getName(),"message");
            }
        });

    }

    @Override
    public int getItemCount() {
        return guideList.size();
    }

    public static class serviceProviderInfoViewHolder extends RecyclerView.ViewHolder {
        TextView guideName;
        ImageView guideImage;
        ImageButton guideCallBtn,guideMessageBtn;

        public serviceProviderInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            guideName=itemView.findViewById(R.id.guideName);
            guideImage=itemView.findViewById(R.id.guideImage);
            guideCallBtn=itemView.findViewById(R.id.guideCallBtn);
            guideMessageBtn=itemView.findViewById(R.id.guideMessageBtn);
        }
    }

    private void sendNotificationToGuide(String guideId, String userId, String userName, String type) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("guides").child(guideId).child("notifications");

        String notificationId = notificationRef.push().getKey();
        HashMap<String, Object> notificationData = new HashMap<>();
        notificationData.put("type", type); // "call" or "message"
        notificationData.put("userId", userId);
        notificationData.put("userName", userName);
        notificationData.put("timestamp", System.currentTimeMillis());

        assert notificationId != null;
        notificationRef.child(notificationId).setValue(notificationData);
    }
}
