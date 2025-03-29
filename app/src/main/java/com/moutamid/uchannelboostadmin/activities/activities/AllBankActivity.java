package com.moutamid.uchannelboostadmin.activities.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.models.BankModel;
import com.moutamid.uchannelboostadmin.utils.Constants;

import java.util.ArrayList;
import java.util.List;
public class AllBankActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BankAdapter bankAdapter;
    private List<BankModel> bankList;
    private DatabaseReference databaseReference;
    Button addBankButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bank);

        addBankButton = findViewById(R.id.addBankButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bankList = new ArrayList<>();
        bankAdapter = new BankAdapter(this, bankList);
        recyclerView.setAdapter(bankAdapter);
        databaseReference = Constants.databaseReference().child("Banks");
        addBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllBankActivity.this, AddBankActivity.class));
            }
        });
        loadBankData();
    }

    private void loadBankData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bankList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BankModel bank = dataSnapshot.getValue(BankModel.class);
                    if (bank != null) {
                        bankList.add(bank);
                    }
                }
                bankAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllBankActivity.this, "Failed to load banks", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
