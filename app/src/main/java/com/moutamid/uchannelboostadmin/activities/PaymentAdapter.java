package com.moutamid.uchannelboostadmin.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.models.PaymentModel;
import com.moutamid.uchannelboostadmin.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentVH> {
    Context context;
    ArrayList<PaymentModel> list;

    public PaymentAdapter(Context context, ArrayList<PaymentModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PaymentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentVH(LayoutInflater.from(context).inflate(R.layout.payment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentVH holder, int position) {
        PaymentModel model = list.get(holder.getAdapterPosition());
        holder.money.setText("$"+model.getAmount()+" ");
        holder.buyerEmail.setText(model.getEmail());

        holder.proof.setOnClickListener(v -> {
            showProof(model.getImageLink());
        });

        holder.approve.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Approve Payment")
                    .setMessage("Are you sure you want to approve this subscription?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dialog.dismiss();
                        model.setApprove(true);
//                        model.setSelectedType(model.getSelectedType());
                        Constants.databaseReference().child(Constants.PAYMENTS).child(model.getKey()).setValue(model)
                                .addOnSuccessListener(unused -> {
                                    Map<String, Object> map = new HashMap<>();
                                    int total_coins = Integer.parseInt(model.getNeed_coins() )+Integer.parseInt( model.getCurrent_coins());
                                    map.put("coins", total_coins);
                                    Constants.databaseReference().child(Constants.USER_INFO).child(model.getUserId()).updateChildren(map)
                                            .addOnSuccessListener(unused1 -> Toast.makeText(context, "Subscription Approved", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                                })
                                .addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        holder.delete.setOnClickListener(v -> {
            Constants.databaseReference().child(Constants.PAYMENTS).child(model.getKey()).removeValue();
        });
    }

    private void showProof(String proofImage) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_preview);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.proof);
        LinearLayout progress = dialog.findViewById(R.id.progress);
        new Handler().postDelayed(() -> {
            progress.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }, 3000);
        Glide.with(context).load(proofImage).into(imageView);
        MaterialCardView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PaymentVH extends RecyclerView.ViewHolder {
        TextView money, buyerEmail;
        MaterialCardView delete;
        MaterialButton proof, approve;

        public PaymentVH(@NonNull View itemView) {
            super(itemView);
            money = itemView.findViewById(R.id.money);
            buyerEmail = itemView.findViewById(R.id.buyerEmail);
            delete = itemView.findViewById(R.id.delete);
            proof = itemView.findViewById(R.id.proof);
            approve = itemView.findViewById(R.id.approve);
        }
    }

}
