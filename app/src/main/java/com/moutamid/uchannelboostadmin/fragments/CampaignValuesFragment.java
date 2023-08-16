package com.moutamid.uchannelboostadmin.fragments;

import static android.view.LayoutInflater.from;
import static com.bumptech.glide.Glide.init;
import static com.bumptech.glide.Glide.with;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.databinding.FragmentCampaignValuesBinding;
import com.moutamid.uchannelboostadmin.models.ArrayModel;
import com.moutamid.uchannelboostadmin.utils.Constants;
import com.moutamid.uchannelboostadmin.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CampaignValuesFragment extends Fragment {

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    private FragmentCampaignValuesBinding b;

    private boolean isTimeList = true;

    private ProgressDialog progressDialog;

    List<ArrayModel> viewQuantityListModel = new ArrayList<>();
    List<ArrayModel> viewTimeListModel = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentCampaignValuesBinding.inflate(inflater, container, false);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        new Thread(() ->
                Utils.databaseReference().child(Constants.ADD_TASK_VARIABLES).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            viewQuantityListModel.clear();
                            viewTimeListModel.clear();

                            if (snapshot.child(Constants.TIME_ARRAY).exists()) {
                                for (DataSnapshot dataSnapshot : snapshot
                                        .child(Constants.TIME_ARRAY).getChildren()) {
                                    ArrayModel model = new ArrayModel();
                                    model.setValue(dataSnapshot.getValue(Integer.class));
                                    model.setKey(dataSnapshot.getKey());
                                    viewTimeListModel.add(model);
                                }

                            }

                            if (snapshot.child(Constants.QUANTITY_ARRAY).exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.child(Constants.QUANTITY_ARRAY)
                                        .getChildren()) {
                                    ArrayModel model = new ArrayModel();
                                    model.setValue(dataSnapshot.getValue(Integer.class));
                                    model.setKey(dataSnapshot.getKey());
                                    viewQuantityListModel.add(model);
                                }
                            }
                            if (snapshot.child(Constants.CUT_OFF_AMOUNT_OF_VIEWS).exists()) {
                                b.cutOffAmountETView.setText(""+snapshot.child(Constants.CUT_OFF_AMOUNT_OF_VIEWS)
                                        .getValue(Integer.class));
                            } else {
                                b.cutOffAmountETView.setText("60");
                            }

                            if (snapshot.child(Constants.CUT_OFF_AMOUNT_OF_LIKE).exists()) {
                                b.cutOffAmountETLike.setText(""+snapshot.child(Constants.CUT_OFF_AMOUNT_OF_LIKE)
                                        .getValue(Integer.class));
                            } else {
                                b.cutOffAmountETLike.setText("60");
                            }

                            if (snapshot.child(Constants.CUT_OFF_AMOUNT_OF_SUBSCRIBE).exists()) {
                                b.cutOffAmountETSubscribe.setText(""+snapshot.child(Constants.CUT_OFF_AMOUNT_OF_SUBSCRIBE)
                                        .getValue(Integer.class));
                            } else {
                                b.cutOffAmountETSubscribe.setText("60");
                            }

                            /*if (snapshot.child(Constants.CUT_OFF_AMOUNT_OF_TASKS).exists()) {
                                b.cutOffAmountET.setText(""+snapshot.child(Constants.CUT_OFF_AMOUNT_OF_TASKS)
                                        .getValue(Integer.class));
                            } else {
                                b.cutOffAmountET.setText("60");
                            }*/

                            initRecyclerView();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                })
        ).start();

        b.timeListBtn.setOnClickListener(view -> {
            b.timeListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
            b.quantityListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.tasksCutOfBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));

            isTimeList = true;
            b.scrollViewLayout.setVisibility(View.VISIBLE);
            initRecyclerView();

            b.cutOffAmountLayout.setVisibility(View.GONE);
        });
        b.quantityListBtn.setOnClickListener(view -> {
            b.quantityListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
            b.timeListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.tasksCutOfBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));

            isTimeList = false;
            b.scrollViewLayout.setVisibility(View.VISIBLE);
            initRecyclerView();

            b.cutOffAmountLayout.setVisibility(View.GONE);
        });

        b.tasksCutOfBtn.setOnClickListener(view -> {
            b.quantityListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.timeListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.tasksCutOfBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.black));

            b.scrollViewLayout.setVisibility(View.GONE);

            b.cutOffAmountLayout.setVisibility(View.VISIBLE);
        });


        b.cutOffAmountETView.addTextChangedListener(cutOffAmountEtWatcher(Constants.CUT_OFF_AMOUNT_OF_VIEWS));
        b.cutOffAmountETLike.addTextChangedListener(cutOffAmountEtWatcher(Constants.CUT_OFF_AMOUNT_OF_LIKE));
        b.cutOffAmountETSubscribe.addTextChangedListener(cutOffAmountEtWatcher(Constants.CUT_OFF_AMOUNT_OF_SUBSCRIBE));

        return b.getRoot();
    }

    private TextWatcher cutOffAmountEtWatcher(String cutOffAmountChild) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (String.valueOf(charSequence).isEmpty())
                    return;

                int value = Integer.parseInt(String.valueOf(charSequence));

                Utils.databaseReference().child(Constants.ADD_TASK_VARIABLES)
                        .child(cutOffAmountChild)
                        .setValue(value);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private void initRecyclerView() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();

        conversationRecyclerView = b.campaignValuesRecyclerView;
        conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);
        conversationRecyclerView.setItemViewCacheSize(20);

        conversationRecyclerView.setAdapter(adapter);
    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_campaign_values, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            ArrayModel model;

            if (isTimeList)
                model = viewTimeListModel.get(position);
            else model = viewQuantityListModel.get(position);

            holder.editText.setText(model.getValue() + "");

            holder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (String.valueOf(charSequence).isEmpty())
                        return;

                    int value = Integer.parseInt(String.valueOf(charSequence));

                    if (isTimeList)
                        Utils.databaseReference().child(Constants.ADD_TASK_VARIABLES)
                                .child(Constants.TIME_ARRAY).child(model.getKey())
                                .setValue(value);

                    else Utils.databaseReference().child(Constants.ADD_TASK_VARIABLES)
                            .child(Constants.QUANTITY_ARRAY).child(model.getKey())
                            .setValue(value);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }

        @Override
        public int getItemCount() {
            if (isTimeList) {
                if (viewTimeListModel == null)
                    return 0;
                return viewTimeListModel.size();
            } else {
                if (viewQuantityListModel == null)
                    return 0;
                return viewQuantityListModel.size();
            }
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            EditText editText;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                editText = v.findViewById(R.id.valueEt);
            }
        }

    }

}