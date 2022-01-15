package com.moutamid.viewplusadmin.ui.slideshow;

import static android.view.LayoutInflater.from;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.viewplusadmin.R;
import com.moutamid.viewplusadmin.databinding.FragmentAppUsersBinding;
import com.moutamid.viewplusadmin.models.AdminUserModel;
import com.moutamid.viewplusadmin.models.AppUserModel;
import com.moutamid.viewplusadmin.utils.Utils;

import java.util.ArrayList;

public class AppUsersFragment extends Fragment {

    ArrayList<AppUserModel> appUserModelArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    private FragmentAppUsersBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentAppUsersBinding.inflate(inflater, container, false);

        Utils.databaseReference().child("userinfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    appUserModelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AppUserModel model = new AppUserModel();

                        model.setEmail(dataSnapshot.child("email").getValue(String.class));
                        model.setPushKey(dataSnapshot.getKey());

                        long coinss;
                        String coinsss;
                        try {
                            coinss = dataSnapshot.child("coins").getValue(Long.class);
                            model.setCoins(coinss);
                        } catch (Exception e) {
                            coinsss = dataSnapshot.child("coins").getValue(String.class);
                            model.setCoins(Integer.parseInt(coinsss));
                        }

                        boolean vipStatus;
                        String vipStatuss;
                        try {
                            vipStatus = dataSnapshot.child("vipStatus").getValue(Boolean.class);
                            model.setVipStatus(vipStatus);
                        } catch (Exception e) {
                            vipStatuss = dataSnapshot.child("vipStatus").getValue(String.class);
                            model.setVipStatus(Boolean.parseBoolean(vipStatuss));
                        }

                        appUserModelArrayList.add(model);
                    }

                    initRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.createAppUserBtn.setOnClickListener(view -> {
            AppUserModel model = new AppUserModel();
            model.setPushKey("");
            showDialog(model, "Create");
        });

        return b.getRoot();
    }

    private void initRecyclerView() {

        conversationRecyclerView = b.recyclerViewUsers;
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

    }

    private void showDialog(AppUserModel model, String saveBtnText) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_users);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText coins, email;
        RadioButton trueRadioButton = dialog.findViewById(R.id.trueRadioBtn);
        RadioButton falseRadioButton = dialog.findViewById(R.id.falseRadioBtn);


        coins = dialog.findViewById(R.id.coins_et_users);
        email = dialog.findViewById(R.id.email_et_users);

        if (!model.getPushKey().isEmpty()) {
            coins.setText("" + model.getCoins());
            email.setText(model.getEmail());

            if (!model.isVipStatus()) {
                falseRadioButton.setChecked(true);
                trueRadioButton.setChecked(false);
            } else {
                trueRadioButton.setChecked(true);
                falseRadioButton.setChecked(false);
            }


        }

        Button button = dialog.findViewById(R.id.save_btn_users);
        button.setText(saveBtnText);

        if (model.getPushKey().isEmpty()) {
            model.setPushKey(Utils.databaseReference().child("userinfo").push().getKey());
        }

        dialog.findViewById(R.id.save_btn_users).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.databaseReference().child("userinfo").child(model.getPushKey())
                        .child("coins").setValue(Long.parseLong(coins.getText().toString()));

                Utils.databaseReference().child("userinfo").child(model.getPushKey())
                        .child("email").setValue(email.getText().toString());

                Utils.databaseReference().child("userinfo").child(model.getPushKey())
                        .child("vipStatus").setValue(trueRadioButton.isChecked());

                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public RecyclerViewAdapterMessages.ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_users_item, parent, false);
            return new RecyclerViewAdapterMessages.ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapterMessages.ViewHolderRightMessage holder, int position) {

            AppUserModel model = appUserModelArrayList.get(position);

            holder.coins.setText("COINS: " + model.getCoins());
            holder.email.setText("EMAIL: " + model.getEmail());
            holder.vip.setText("VIP STATUS: " + model.isVipStatus());
            holder.id.setText("POSTER ID: " + model.getPushKey());
            holder.editBtn.setOnClickListener(view -> {
                showDialog(model, "Save");
            });

            holder.deleteBtn.setOnClickListener(view -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Are you sure?")
                        .setMessage("Do you really want to delete this user?")
                        .setPositiveButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setNegativeButton("Yes", (dialogInterface, i) ->
                                Utils.databaseReference().child("userinfo").child(model.getPushKey())
                                        .removeValue())
                        .show();
            });

        }

        @Override
        public int getItemCount() {
            if (appUserModelArrayList == null)
                return 0;
            return appUserModelArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView coins, email, vip, id;
            ImageView editBtn, deleteBtn;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                id = v.findViewById(R.id.id_tv_admin);
                email = v.findViewById(R.id.email_tv_admin);
                coins = v.findViewById(R.id.coins_tv);
                vip = v.findViewById(R.id.vip_status_tv);
                editBtn = v.findViewById(R.id.editBtn);
                deleteBtn = v.findViewById(R.id.deleteBtn);

            }
        }

    }

}