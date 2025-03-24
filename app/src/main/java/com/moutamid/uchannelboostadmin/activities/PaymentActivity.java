package com.moutamid.uchannelboostadmin.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.databinding.ActivityPaymentBinding;
import com.moutamid.uchannelboostadmin.models.PaymentModel;
import com.moutamid.uchannelboostadmin.utils.Constants;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;
    PaymentAdapter adapter;
    ArrayList<PaymentModel> list;

    @Override
    protected void onResume() {
        super.onResume();
        Constants.initDialog(this);
        Constants.showDialog();
        getData();
    }

    private void getData() {

        Constants.databaseReference().child(Constants.PAYMENTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Constants.dismissDialog();
                list.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        PaymentModel model = data.getValue(PaymentModel.class);
                        Log.d("dataaaaaaaa", data.getKey() + "   key");
                        if (!model.isApprove()) {
                            list.add(model);
                        }

                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "No payments found", Toast.LENGTH_SHORT).show();
                }
                adapter = new PaymentAdapter(PaymentActivity.this, list);
                binding.paymentRC.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constants.dismissDialog();
                Toast.makeText(PaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.back.setOnClickListener(v -> finish());

        list = new ArrayList<>();

        binding.paymentRC.setHasFixedSize(false);
        binding.paymentRC.setLayoutManager(new LinearLayoutManager(this));
    }
}