package com.moutamid.uchannelboosteradmin.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboosteradmin.databinding.FragmentCoinsBinding;
import com.moutamid.uchannelboosteradmin.utils.Constants;
import com.moutamid.uchannelboosteradmin.utils.Utils;

public class CoinsFragment extends Fragment {

    private FragmentCoinsBinding b;

    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentCoinsBinding.inflate(inflater, container, false);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Utils.databaseReference().child(Constants.COINS_PATH)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            if (snapshot.child(Constants.VIEW_COINS).exists())
                                b.viewCoinsET.setText("" + snapshot.child(Constants.VIEW_COINS).getValue(Integer.class));
                            else b.viewCoinsET.setText("120");

                            if (snapshot.child(Constants.LIKE_COINS).exists())
                                b.likeCoinsET.setText("" + snapshot.child(Constants.LIKE_COINS).getValue(Integer.class));
                            else b.likeCoinsET.setText("120");

                            if (snapshot.child(Constants.SUBSCRIBE_COINS).exists())
                                b.subscribeCoinsET.setText("" + snapshot.child(Constants.SUBSCRIBE_COINS).getValue(Integer.class));
                            else b.subscribeCoinsET.setText("120");

                        } else {
                            b.likeCoinsET.setText("120");
                            b.subscribeCoinsET.setText("120");
                            b.viewCoinsET.setText("120");
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        b.updateCoinsBtn.setOnClickListener(view -> {
            if (b.viewCoinsET.getText().toString().isEmpty())
                return;

            if (b.likeCoinsET.getText().toString().isEmpty())
                return;

            if (b.subscribeCoinsET.getText().toString().isEmpty())
                return;

            progressDialog.show();

            Utils.databaseReference().child(Constants.COINS_PATH)
                    .child(Constants.VIEW_COINS)
                    .setValue(Integer.parseInt(b.viewCoinsET.getText().toString()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                Utils.databaseReference().child(Constants.COINS_PATH)
                                        .child(Constants.LIKE_COINS)
                                        .setValue(Integer.parseInt(b.likeCoinsET.getText().toString()));

                                Utils.databaseReference().child(Constants.COINS_PATH)
                                        .child(Constants.SUBSCRIBE_COINS)
                                        .setValue(Integer.parseInt(b.subscribeCoinsET.getText().toString()));

                                Toast.makeText(requireContext(), "DONE", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        return b.getRoot();
    }

}