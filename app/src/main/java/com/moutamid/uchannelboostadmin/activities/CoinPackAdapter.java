package com.moutamid.uchannelboostadmin.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.models.CoinPack;

import java.util.List;

public class CoinPackAdapter extends RecyclerView.Adapter<CoinPackAdapter.ViewHolder> {
    private List<CoinPack> coinPackList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEdit(CoinPack coinPack);
        void onDelete(String id);
    }

    public CoinPackAdapter(List<CoinPack> coinPackList, OnItemClickListener listener) {
        this.coinPackList = coinPackList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_pack, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoinPack coinPack = coinPackList.get(position);
        holder.coinsTextView.setText("Coins: " + coinPack.getCoins());
        holder.priceTextView.setText("$" + coinPack.getPrice());

        holder.editButton.setOnClickListener(v -> listener.onEdit(coinPack));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(coinPack.getId()));
    }

    @Override
    public int getItemCount() {
        return coinPackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinsTextView, priceTextView;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coinsTextView = itemView.findViewById(R.id.coinsTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
