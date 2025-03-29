package com.moutamid.uchannelboostadmin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.activities.activities.AddBankActivity;
import com.moutamid.uchannelboostadmin.activities.activities.AllBankActivity;
import com.moutamid.uchannelboostadmin.activities.activities.BankAdapter;
import com.moutamid.uchannelboostadmin.models.BankModel;
import com.moutamid.uchannelboostadmin.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class BankFragment extends Fragment {

    private RecyclerView recyclerView;
    private BankAdapter bankAdapter;
    private List<BankModel> bankList;
    private DatabaseReference databaseReference;
    Button addBankButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.activity_all_bank, container, false);

        addBankButton = view.findViewById(R.id.addBankButton);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bankList = new ArrayList<>();
        bankAdapter = new BankAdapter(getContext(), bankList);
        recyclerView.setAdapter(bankAdapter);
        databaseReference = Constants.databaseReference().child("Banks");
        addBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddBankActivity.class));
            }
        });
        loadBankData();

    return view;
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
                Toast.makeText(getContext(), "Failed to load banks", Toast.LENGTH_SHORT).show();
            }
        });
    }

}