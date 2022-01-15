package com.moutamid.viewplusadmin.ui.gallery;

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
import com.moutamid.viewplusadmin.databinding.FragmentAdminUsersBinding;
import com.moutamid.viewplusadmin.models.AdminUserModel;
import com.moutamid.viewplusadmin.utils.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdminUsersFragment extends Fragment {

    private FragmentAdminUsersBinding b;
    ArrayList<AdminUserModel> adminUserModelArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentAdminUsersBinding.inflate(inflater, container, false);

        Utils.databaseReference().child("WebUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    adminUserModelArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AdminUserModel model;
                        model = dataSnapshot.getValue(AdminUserModel.class);
                        model.setPushKey(dataSnapshot.getKey());
                        adminUserModelArrayList.add(model);
                    }

                    initRecyclerView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.createAdminUserBtn.setOnClickListener(view -> {
            AdminUserModel model = new AdminUserModel();
            model.setUserID("");
            model.setPushKey("");
            showDialog(model, "Create");
        });

        return b.getRoot();
    }

    private void initRecyclerView() {

        conversationRecyclerView = b.recyclerViewAdmins;
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

    }

    private void showDialog(AdminUserModel model, String saveBtnText) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_admin);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText id, name, email, number, password;

        id = dialog.findViewById(R.id.id_et);
        name = dialog.findViewById(R.id.name_et);
        email = dialog.findViewById(R.id.email_et);
        number = dialog.findViewById(R.id.number_et);
        password = dialog.findViewById(R.id.password_et);

        if (!model.getUserID().isEmpty()) {
            id.setText(model.getUserID());
            name.setText(model.getFullName());
            email.setText(model.getEmail());
            number.setText(model.getMobileNo());
        }

        Button button = dialog.findViewById(R.id.save_btn_admin);
        button.setText(saveBtnText);

        if (model.getPushKey().isEmpty()) {
            model.setPushKey(Utils.databaseReference().child("WebUser").push().getKey());
            password.setVisibility(View.VISIBLE);
        }

        dialog.findViewById(R.id.save_btn_admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utils.databaseReference().child("WebUser").child(model.getPushKey())
                        .child("UserID").setValue(id.getText().toString());

                Utils.databaseReference().child("WebUser").child(model.getPushKey())
                        .child("MobileNo").setValue(number.getText().toString());

                Utils.databaseReference().child("WebUser").child(model.getPushKey())
                        .child("FullName").setValue(name.getText().toString());

                Utils.databaseReference().child("WebUser").child(model.getPushKey())
                        .child("Email").setValue(email.getText().toString());

                if (password.getVisibility() == View.VISIBLE) {
                    Utils.databaseReference().child("WebUser").child(model.getPushKey())
                            .child("Password").setValue(password.getText().toString());
                }

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
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_admin_item, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {

            AdminUserModel model = adminUserModelArrayList.get(position);

            holder.id.setText("ID: " + model.getUserID());
            holder.name.setText("NAME: " + model.getFullName());
            holder.email.setText("EMAIL: " + model.getEmail());
            holder.number.setText("NUMBER: " + model.getMobileNo());

            holder.editBtn.setOnClickListener(view -> {
                showDialog(model, "Save");
            });

            holder.deleteBtn.setOnClickListener(view -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Are you sure?")
                        .setMessage("Do you really want to delete this admin?")
                        .setPositiveButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setNegativeButton("Yes", (dialogInterface, i) ->
                                Utils.databaseReference().child("WebUser").child(model.getPushKey())
                                        .removeValue())
                        .show();
            });

        }

        @Override
        public int getItemCount() {
            if (adminUserModelArrayList == null)
                return 0;
            return adminUserModelArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView id, name, email, number;
            ImageView editBtn, deleteBtn;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                id = v.findViewById(R.id.id_tv);
                name = v.findViewById(R.id.name_tv);
                email = v.findViewById(R.id.email_tv);
                number = v.findViewById(R.id.number_tv);
                editBtn = v.findViewById(R.id.editBtn);
                deleteBtn = v.findViewById(R.id.deleteBtn);

            }
        }

    }

}