package com.moutamid.uchannelboostadmin.activities;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.moutamid.uchannelboostadmin.R;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.adapter.SubscriptionAdapter;
import com.moutamid.uchannelboostadmin.models.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubscriptionActivity extends AppCompatActivity implements SubscriptionAdapter.OnItemClickListener {

    private EditText editDuration, editPrice, editDiscount;
    private Button btnAddSubscription;
    private RecyclerView recyclerView;
    private SubscriptionAdapter adapter;
    private List<Subscription> subscriptionList;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        editDuration = findViewById(R.id.editDuration);
        editPrice = findViewById(R.id.editPrice);
        editDiscount = findViewById(R.id.editDiscount);
        btnAddSubscription = findViewById(R.id.btnAddSubscription);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subscriptionList = new ArrayList<>();
        adapter = new SubscriptionAdapter(subscriptionList, this);
        recyclerView.setAdapter(adapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("subscriptions");

        btnAddSubscription.setOnClickListener(view -> addSubscription());

        loadSubscriptions();
    }

    private void addSubscription() {
        String duration = editDuration.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String discountStr = editDiscount.getText().toString().trim();

        if (TextUtils.isEmpty(duration) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(discountStr)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int discount = Integer.parseInt(discountStr);

        String id = UUID.randomUUID().toString();
        Subscription subscription = new Subscription(id, duration, price, discount);

        databaseRef.child(id).setValue(subscription)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SubscriptionActivity.this, "Subscription added!", Toast.LENGTH_SHORT).show();
                    editDuration.setText("");
                    editPrice.setText("");
                    editDiscount.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(SubscriptionActivity.this, "Failed to add", Toast.LENGTH_SHORT).show());
    }

    private void loadSubscriptions() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subscriptionList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Subscription subscription = data.getValue(Subscription.class);
                    subscriptionList.add(subscription);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubscriptionActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onEdit(Subscription subscription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_subscription, null);
        builder.setView(dialogView);

        EditText editDurationDialog = dialogView.findViewById(R.id.editDurationDialog);
        EditText editPriceDialog = dialogView.findViewById(R.id.editPriceDialog);
        EditText editDiscountDialog = dialogView.findViewById(R.id.editDiscountDialog);
        Button btnUpdate = dialogView.findViewById(R.id.btnUpdate);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        editDurationDialog.setText(subscription.getDuration());
        editPriceDialog.setText(String.valueOf(subscription.getPrice()));
        editDiscountDialog.setText(subscription.getDiscount() > 0 ? String.valueOf(subscription.getDiscount()) : "");

        AlertDialog dialog = builder.create();
        dialog.show();

        btnUpdate.setOnClickListener(view -> {
            String duration = editDurationDialog.getText().toString().trim();
            String priceStr = editPriceDialog.getText().toString().trim();
            String discountStr = editDiscountDialog.getText().toString().trim();

            if (TextUtils.isEmpty(duration) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(this, "Duration and price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int discount = TextUtils.isEmpty(discountStr) ? 0 : Integer.parseInt(discountStr);

            subscription.setDuration(duration);
            subscription.setPrice(price);
            subscription.setDiscount(discount);

            databaseRef.child(subscription.getId()).setValue(subscription)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SubscriptionActivity.this, "Subscription updated!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(SubscriptionActivity.this, "Failed to update", Toast.LENGTH_SHORT).show());
        });

        btnCancel.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onDelete(Subscription subscription) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Subscription")
                .setMessage("Are you sure you want to delete this subscription?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    databaseRef.child(subscription.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(SubscriptionActivity.this, "Subscription deleted!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(SubscriptionActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


}

