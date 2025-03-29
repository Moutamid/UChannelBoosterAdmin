package com.moutamid.uchannelboostadmin.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.models.Subscription;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {
    private List<Subscription> subscriptionList;
    private OnItemClickListener listener;
    public interface OnItemClickListener {
        void onEdit(Subscription subscription);
        void onDelete(Subscription subscription);
    }

    public SubscriptionAdapter(List<Subscription> subscriptionList, OnItemClickListener listener) {
        this.subscriptionList = subscriptionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscription subscription = subscriptionList.get(position);
        holder.txtDiscount.setText(subscription.getDuration() + " (Save "+ subscription.getDiscount()+"%)");
        holder.txtPrice.setText("$" + subscription.getPrice());
        holder.itemView.setOnClickListener(v -> listener.onEdit(subscription));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(subscription);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return subscriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPrice, txtDiscount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtDiscount = itemView.findViewById(R.id.txtDiscount);
        }
    }
}
