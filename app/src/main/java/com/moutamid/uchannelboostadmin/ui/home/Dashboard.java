package com.moutamid.uchannelboostadmin.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.activities.VipPaymentActivity;
import com.moutamid.uchannelboostadmin.databinding.FragmentDashboardBinding;
import com.moutamid.uchannelboostadmin.utils.Utils;

public class Dashboard extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentDashboardBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        b = FragmentDashboardBinding.inflate(inflater, container, false);

        Utils.databaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Log.d("dataaaa", snapshot.toString() + "-------");
                    b.totalAdminUserTv.setText(snapshot.child("WebUser").getChildrenCount() + "");
                    b.totalMobileUserTv.setText(snapshot.child("userinfo").getChildrenCount() + "");
                    b.totalViewTasksTv.setText(snapshot.child("view_tasks").getChildrenCount() + "");
                    b.totalSubscribeTasksTv.setText(snapshot.child("subscribe_tasks").getChildrenCount() + "");
                    b.totalLikeTasksTv.setText(snapshot.child("like_tasks").getChildrenCount() + "");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        b.vipRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), VipPaymentActivity.class));
            }
        });
        return b.getRoot();
    }
}