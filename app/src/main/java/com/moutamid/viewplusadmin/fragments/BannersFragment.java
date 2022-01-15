package com.moutamid.viewplusadmin.fragments;

import static android.view.LayoutInflater.from;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.viewplusadmin.R;
import com.moutamid.viewplusadmin.databinding.FragmentBannersBinding;
import com.moutamid.viewplusadmin.models.AdminUserModel;
import com.moutamid.viewplusadmin.models.BannerModel;
import com.moutamid.viewplusadmin.utils.Constants;
import com.moutamid.viewplusadmin.utils.Utils;

import java.util.ArrayList;

public class BannersFragment extends Fragment {

    private FragmentBannersBinding b;
    ArrayList<BannerModel> bannersArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentBannersBinding.inflate(inflater, container, false);

        Utils.databaseReference().child("Banners").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bannersArrayList.clear();
                    String TAG = "TAGG";

                    if (snapshot.child("like").exists()) {
                        for (DataSnapshot likeSnapshot : snapshot.child("like").getChildren()) {
                            BannerModel model = new BannerModel();
                            model.setLink(likeSnapshot.getValue(String.class));
                            model.setPushKey(likeSnapshot.getKey());
                            model.setType(Constants.TYPE_LIKE);

                            bannersArrayList.add(model);
                        }
                    } else Log.i(TAG, "onDataChange: no like");

                    if (snapshot.child("subscribe").exists()) {
                        for (DataSnapshot subscribeSnapshot : snapshot.child("subscribe").getChildren()) {
                            Log.i(TAG, "onDataChange: "+subscribeSnapshot.getValue(String.class));
                            BannerModel model = new BannerModel();
                            model.setLink(subscribeSnapshot.getValue(String.class));
                            model.setPushKey(subscribeSnapshot.getKey());
                            model.setType(Constants.TYPE_SUBSCRIBE);

                            bannersArrayList.add(model);
                        }
                    } else Log.i(TAG, "onDataChange: no subscribe");

                    if (snapshot.child("view").exists()) {
                        for (DataSnapshot viewSnapshot : snapshot.child("view").getChildren()) {
                            Log.i(TAG, "onDataChange: "+viewSnapshot.getValue(String.class));
                            BannerModel model = new BannerModel();
                            model.setLink(viewSnapshot.getValue(String.class));
                            model.setPushKey(viewSnapshot.getKey());
                            model.setType(Constants.TYPE_VIEW);

                            bannersArrayList.add(model);
                        }
                    } else Log.i(TAG, "onDataChange: no view");

                    initRecyclerView();
                } else {
                    Utils.toast("Not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.createBannersBtn.setOnClickListener(view -> {
            showDialog();
        });

        return b.getRoot();
    }

    private void initRecyclerView() {

        conversationRecyclerView = b.recyclerViewBanners;
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

    }

    private void showDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_banner);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText linkEt = dialog.findViewById(R.id.linkEtDialogBanner);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroupDialogBanner);
        RadioButton likeRBtn = dialog.findViewById(R.id.likeRadioBtnDialogBanner);
        RadioButton subscribeRBtn = dialog.findViewById(R.id.subscribeRadioBtnDialogBanner);
        RadioButton viewRBtn = dialog.findViewById(R.id.viewRadioBtnDialogBanner);

        dialog.findViewById(R.id.doneBtnDialogBanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (linkEt.getText().toString().isEmpty())
                    return;

                String type = "like";

                if (viewRBtn.isChecked())
                    type = "view";

                if (subscribeRBtn.isChecked())
                    type = "subscribe";

                Utils.databaseReference().child("Banners").child(type)
                        .push().setValue(linkEt.getText().toString());

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
            View view = from(parent.getContext()).inflate(R.layout.layout_banner, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {

            BannerModel model = bannersArrayList.get(position);

            holder.link.setText(model.getType() + model.getLink());

            holder.link.setOnClickListener(view -> {
                Utils.toast(model.getPushKey());
            });

            with(requireContext())
                    .asBitmap()
                    .load(model.getLink())
                    .apply(new RequestOptions()
                            .placeholder(R.color.browser_actions_bg_grey)
                            .error(R.color.browser_actions_bg_grey)
                    )
                    .diskCacheStrategy(DATA)
                    .into(holder.imageview);

            holder.deleteBtn.setOnClickListener(view -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Are you sure?")
                        .setMessage("Do you really want to delete this banner?")
                        .setPositiveButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setNegativeButton("Yes", (dialogInterface, i) -> {
                            String type = "like";

                            if (model.getType().equals(Constants.TYPE_VIEW))
                                type = "view";

                            if (model.getType().equals(Constants.TYPE_SUBSCRIBE))
                                type = "subscribe";

                            Utils.databaseReference().child("Banners").child(type)
                                    .child(model.getPushKey())
                                    .removeValue();
                        })
                        .show();
            });

        }

        @Override
        public int getItemCount() {
            if (bannersArrayList == null)
                return 0;
            return bannersArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView link;
            ImageView imageview, deleteBtn;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                deleteBtn = v.findViewById(R.id.deleteBtnB);
                link = v.findViewById(R.id.linkk);
                imageview = v.findViewById(R.id.imageviewB);
            }
        }

    }

}