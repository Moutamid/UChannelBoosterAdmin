package com.moutamid.uchannelboostadmin.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.models.CoinPack;
import com.moutamid.uchannelboostadmin.utils.Constants;
import com.moutamid.uchannelboostadmin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ManageCoinPacksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CoinPackAdapter adapter;
    private List<CoinPack> coinPackList = new ArrayList<>();
    private DatabaseReference reference;
    private EditText coinsInput, priceInput;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_coin_packs);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        coinsInput = findViewById(R.id.coinsInput);
        priceInput = findViewById(R.id.priceInput);
        addButton = findViewById(R.id.addButton);

        reference = Utils.databaseReference().child(Constants.COIN_PACK);
        adapter = new CoinPackAdapter(coinPackList, new CoinPackAdapter.OnItemClickListener() {
            @Override
            public void onEdit(CoinPack coinPack) {
                showEditDialog(coinPack);
            }

            @Override
            public void onDelete(String id) {
                reference.child(id).removeValue();
            }
        });

        recyclerView.setAdapter(adapter);
        loadCoinPacks();

        addButton.setOnClickListener(v -> addCoinPack());
    }

    private void loadCoinPacks() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                coinPackList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    CoinPack coinPack = data.getValue(CoinPack.class);
                    coinPackList.add(coinPack);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ManageCoinPacksActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCoinPack() {
        String id = reference.push().getKey();
        int coins = Integer.parseInt(coinsInput.getText().toString());
        double price = Double.parseDouble(priceInput.getText().toString());

        CoinPack coinPack = new CoinPack(id, coins, price);
        reference.child(id).setValue(coinPack);
    }

    private void showEditDialog(CoinPack coinPack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Coin Pack");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_coin_pack, null);
        EditText coinsInput = view.findViewById(R.id.editCoinsInput);
        EditText priceInput = view.findViewById(R.id.editPriceInput);

        coinsInput.setText(String.valueOf(coinPack.getCoins()));
        priceInput.setText(String.valueOf(coinPack.getPrice()));

        builder.setView(view);
        builder.setPositiveButton("Update", (dialog, which) -> {
            int newCoins = Integer.parseInt(coinsInput.getText().toString());
            double newPrice = Double.parseDouble(priceInput.getText().toString());

            reference.child(coinPack.getId()).setValue(new CoinPack(coinPack.getId(), newCoins, newPrice));
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
