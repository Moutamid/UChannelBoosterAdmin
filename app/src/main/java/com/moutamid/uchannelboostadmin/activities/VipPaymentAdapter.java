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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.models.PaymentModel;
import com.moutamid.uchannelboostadmin.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VipPaymentAdapter extends RecyclerView.Adapter<VipPaymentAdapter.PaymentVH> {
    Context context;
    ArrayList<PaymentModel> list;

    public VipPaymentAdapter(Context context, ArrayList<PaymentModel> list) {
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
        holder.account_type.setText(model.getAccount_type() + " ");
        holder.date.setText(model.getDate() + " ");
        holder.amount.setText("$" + model.getAmount() + " for " + model.getDuration());
        holder.buyerEmail.setText(model.getEmail());
        holder.proof.setOnClickListener(v -> showProof(model.getImageLink()));

        holder.approve.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context).setTitle("Approve Payment").setMessage("Are you sure you want to approve this subscription?").setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                Constants.databaseReference().child(Constants.VIP_PAYMENTS).child(model.getUserId()).setValue(model).addOnSuccessListener(unused -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("vipStatus", true);
                    map.put("date", model.getDate());
                    map.put("duration", model.getDuration());
                    Constants.databaseReference().child(Constants.USER_INFO).child(model.getUserId()).updateChildren(map).addOnSuccessListener(unused1 -> {
                        deletePayment(model, 1);
                        Toast.makeText(context, "Subscription Approved", Toast.LENGTH_SHORT).show();
                        notifyUser(model.getUserId(), true);
                    }).addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                }).addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
            }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
        });

        holder.decline.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context).setTitle("Decline Payment").setMessage("Are you sure you want to decline this subscription?").setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                model.setApprove(false);
                deletePayment(model, 0);
            }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();
        });

    }

    private void deletePayment(PaymentModel model, int i) {
        if (model.getImageLink() != null && !model.getImageLink().isEmpty()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(model.getImageLink()).delete().addOnSuccessListener(unused -> {
                Constants.databaseReference().child(Constants.VIP_PAYMENTS).child(model.getUserId()).removeValue().addOnSuccessListener(unused1 -> {
                    if (i == 0) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("vipStatus", false);
                        map.put("date", model.getDate());
                        Constants.databaseReference().child(Constants.USER_INFO).child(model.getUserId()).updateChildren(map).addOnSuccessListener(unused2 -> {
                            Toast.makeText(context, "Subscription Deleted", Toast.LENGTH_SHORT).show();
                            notifyUser(model.getUserId(), false);
                        }).addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
            }).addOnFailureListener(e -> Toast.makeText(context, "Failed to delete image: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Constants.databaseReference().child(Constants.VIP_PAYMENTS).child(model.getUserId()).removeValue().addOnSuccessListener(unused -> {
//                        Toast.makeText(context, "Payment Deleted", Toast.LENGTH_SHORT).show();
                notifyUser(model.getUserId(), false);
            }).addOnFailureListener(e -> Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        }
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

    private void notifyUser(String userId, boolean approved) {
        Constants.databaseReference().child(Constants.USER_INFO).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                    String token = snapshot.child("token").getValue(String.class);
//                    String message = approved ? "Your subscription has been approved." : "Your subscription has been declined.";
//                    sendFCMNotification(token, message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendFCMNotification(String token, String message) {
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PaymentVH extends RecyclerView.ViewHolder {
        TextView buyerEmail, account_type, amount, date;
        MaterialCardView delete;
        MaterialButton proof, approve, decline;

        public PaymentVH(@NonNull View itemView) {
            super(itemView);
            account_type = itemView.findViewById(R.id.account_type);
            buyerEmail = itemView.findViewById(R.id.buyerEmail);
            delete = itemView.findViewById(R.id.delete);
            proof = itemView.findViewById(R.id.proof);
            approve = itemView.findViewById(R.id.approve);
            decline = itemView.findViewById(R.id.decline);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
