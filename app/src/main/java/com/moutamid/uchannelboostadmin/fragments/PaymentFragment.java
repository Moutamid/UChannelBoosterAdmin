package com.moutamid.uchannelboostadmin.fragments;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.activities.PaymentActivity;
import com.moutamid.uchannelboostadmin.activities.PaymentAdapter;
import com.moutamid.uchannelboostadmin.databinding.ActivityPaymentBinding;
import com.moutamid.uchannelboostadmin.databinding.FragmentBannersBinding;
import com.moutamid.uchannelboostadmin.databinding.PaymentBinding;
import com.moutamid.uchannelboostadmin.models.BannerModel;
import com.moutamid.uchannelboostadmin.models.PaymentModel;
import com.moutamid.uchannelboostadmin.utils.Constants;
import com.moutamid.uchannelboostadmin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentFragment extends Fragment {

    ActivityPaymentBinding binding;
    PaymentAdapter adapter;
    ArrayList<PaymentModel> list;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityPaymentBinding.inflate(inflater, container, false);


        list = new ArrayList<>();
        binding.paymentRC.setHasFixedSize(false);
        binding.paymentRC.setLayoutManager(new LinearLayoutManager(getContext()));  Constants.initDialog(getContext());
        Constants.showDialog();
        getData();


        return binding.getRoot();
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
                    Toast.makeText(getContext(), "No payments found", Toast.LENGTH_SHORT).show();
                }
                adapter = new PaymentAdapter(getContext(), list);
                binding.paymentRC.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Constants.dismissDialog();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}