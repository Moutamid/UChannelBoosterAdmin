package com.moutamid.uchannelboostadmin.fragments;

import static android.view.LayoutInflater.from;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.uchannelboostadmin.R;
import com.moutamid.uchannelboostadmin.databinding.FragmentCampaignValuesBinding;
import com.moutamid.uchannelboostadmin.models.ArrayModel;
import com.moutamid.uchannelboostadmin.utils.Constants;
import com.moutamid.uchannelboostadmin.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CampaignValuesFragment extends Fragment {

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    private FragmentCampaignValuesBinding b;
    private boolean isTimeList = true;
    private ProgressDialog progressDialog;
    List<ArrayModel> viewQuantityListModel = new ArrayList<>();
    List<ArrayModel> viewTimeListModel = new ArrayList<>();
    int type = 0;

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
                                    ArrayModel model = dataSnapshot.getValue(ArrayModel.class);
                                    if (model != null) {
                                        int timeValue = model.getValue();  // Extracting the integer value
                                        String timeKey = model.getKey();   // Extracting the key
                                        model.setValue(timeValue);
                                        model.setKey(timeKey);
                                        viewTimeListModel.add(model);
                                    }


                                }
                            }

                            if (snapshot.child(Constants.QUANTITY_ARRAY).exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.child(Constants.QUANTITY_ARRAY)
                                        .getChildren()) {
                                    ArrayModel model = dataSnapshot.getValue(ArrayModel.class);
                                    if (model != null) {
                                        int timeValue = model.getValue();  // Extracting the integer value
                                        String timeKey = model.getKey();   // Extracting the key
                                        model.setValue(timeValue);
                                        model.setKey(timeKey);
                                        viewQuantityListModel.add(model);
                                    }
                                }
                            }
                            if (snapshot.child(Constants.CUT_OFF_AMOUNT_OF_VIEWS).exists()) {
                                b.cutOffAmountETView.setText("" + snapshot.child(Constants.CUT_OFF_AMOUNT_OF_VIEWS)
                                        .getValue(Integer.class));
                            } else {
                                b.cutOffAmountETView.setText("60");
                            }

                            if (snapshot.child(Constants.CUT_OFF_AMOUNT_OF_LIKE).exists()) {
                                b.cutOffAmountETLike.setText("" + snapshot.child(Constants.CUT_OFF_AMOUNT_OF_LIKE)
                                        .getValue(Integer.class));
                            } else {
                                b.cutOffAmountETLike.setText("60");
                            }

                            if (snapshot.child(Constants.CUT_OFF_AMOUNT_OF_SUBSCRIBE).exists()) {
                                b.cutOffAmountETSubscribe.setText("" + snapshot.child(Constants.CUT_OFF_AMOUNT_OF_SUBSCRIBE)
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
        b.addTimeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTimeDialog(type);
            }
        });

        b.timeListBtn.setOnClickListener(view -> {
            b.timeListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
            b.quantityListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.tasksCutOfBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.addTimeList.setVisibility(VISIBLE);
            type = 0;
            isTimeList = true;
            b.scrollViewLayout.setVisibility(VISIBLE);
            initRecyclerView();
            b.cutOffAmountLayout.setVisibility(GONE);
        });
        b.quantityListBtn.setOnClickListener(view -> {
            type = 1;
            b.quantityListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
            b.timeListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.tasksCutOfBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.addTimeList.setVisibility(VISIBLE);
            isTimeList = false;
            b.scrollViewLayout.setVisibility(VISIBLE);
            initRecyclerView();
            b.cutOffAmountLayout.setVisibility(GONE);
        });

        b.tasksCutOfBtn.setOnClickListener(view -> {
            b.quantityListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.timeListBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.grey));
            b.tasksCutOfBtn.setBackgroundColor(requireContext().getResources().getColor(R.color.black));
            b.addTimeList.setVisibility(GONE);
            b.scrollViewLayout.setVisibility(GONE);

            b.cutOffAmountLayout.setVisibility(VISIBLE);
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

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter<RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_campaign_values, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            ArrayModel model = isTimeList ? viewTimeListModel.get(position) : viewQuantityListModel.get(position);
            holder.editText.setText(String.valueOf(model.getValue()));

            // Handle Edit button click
            holder.editIcon.setOnClickListener(v -> showEditDialog(holder.getAdapterPosition(), model));

            // Handle Delete button click
            holder.deleteIcon.setOnClickListener(v -> deleteItem(holder.getAdapterPosition(), model));
        }

        @Override
        public int getItemCount() {
            return isTimeList ? (viewTimeListModel == null ? 0 : viewTimeListModel.size())
                    : (viewQuantityListModel == null ? 0 : viewQuantityListModel.size());
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {
            TextView editText;
            ImageView editIcon, deleteIcon;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                editText = v.findViewById(R.id.valueEt);
                editIcon = v.findViewById(R.id.editIcon);
                deleteIcon = v.findViewById(R.id.deleteIcon);
            }
        }

        // Edit Dialog
        private void showEditDialog(int position, ArrayModel model) {
            AlertDialog.Builder builder = new AlertDialog.Builder(conversationRecyclerView.getContext());
            builder.setTitle("Edit Value");

            final EditText input = new EditText(conversationRecyclerView.getContext());
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setText(String.valueOf(model.getValue()));
            builder.setView(input);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String newValue = input.getText().toString().trim();
                if (!newValue.isEmpty()) {
                    updateValueInFirebase(position, model.getKey(), Integer.parseInt(newValue));
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        }

        // Update value in Firebase
        private void updateValueInFirebase(int position, String key, int newValue) {
            DatabaseReference timeRef;
            if (type == 0) {
                timeRef = Utils.databaseReference()
                        .child(Constants.ADD_TASK_VARIABLES)
                        .child(Constants.TIME_ARRAY)
                        .child(key);
            } else {
                timeRef = Utils.databaseReference()
                        .child(Constants.ADD_TASK_VARIABLES)
                        .child(Constants.QUANTITY_ARRAY)
                        .child(key);
            }


            timeRef.child("value").setValue(newValue).addOnSuccessListener(unused -> {
                if (type == 0) {
                    viewTimeListModel.get(position).setValue(newValue);
                } else {
                    viewQuantityListModel.get(position).setValue(newValue);
                }
                notifyItemChanged(position);
                Toast.makeText(conversationRecyclerView.getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(conversationRecyclerView.getContext(), "Update Failed!", Toast.LENGTH_SHORT).show()
            );
        }

        // Delete item from Firebase
        private void deleteItem(int position, ArrayModel model) {
            DatabaseReference timeRef;
            if (type == 0) {
                timeRef = Utils.databaseReference()
                        .child(Constants.ADD_TASK_VARIABLES)
                        .child(Constants.TIME_ARRAY)
                        .child(model.getKey());
            } else {
                timeRef = Utils.databaseReference()
                        .child(Constants.ADD_TASK_VARIABLES)
                        .child(Constants.QUANTITY_ARRAY)
                        .child(model.getKey());
            }
            timeRef.removeValue().addOnSuccessListener(unused -> {
                if (type == 0) {
                    viewTimeListModel.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, viewTimeListModel.size());

                } else {
                    viewQuantityListModel.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, viewQuantityListModel.size());
                }

                Toast.makeText(conversationRecyclerView.getContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e ->
                    Toast.makeText(conversationRecyclerView.getContext(), "Delete Failed!", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void showAddTimeDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (i == 0) {
            builder.setTitle("Add Time Value");
        } else {
            builder.setTitle("Add Quantity Value");

        }
        // Create an EditText inside the dialog
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (i == 0) {
            input.setHint("Enter Time Value");
        } else {
            input.setHint("Enter Quantity Value");
        }
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String timeValue = input.getText().toString().trim();
                if (!timeValue.isEmpty()) {
                    saveTimeToFirebase(Integer.parseInt(timeValue), i);
                } else {
                    Toast.makeText(getContext(), "Please enter a value", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void saveTimeToFirebase(int timeValue, int i) {
        DatabaseReference timeArrayRef;
        String timeKey;
        if (i == 0) {
            timeArrayRef = Utils.databaseReference().child(Constants.ADD_TASK_VARIABLES).child(Constants.TIME_ARRAY);
            timeKey = timeArrayRef.push().getKey();
        } else {
            timeArrayRef = Utils.databaseReference().child(Constants.ADD_TASK_VARIABLES).child(Constants.QUANTITY_ARRAY);
            timeKey = timeArrayRef.push().getKey();
        }


        if (timeKey != null) {
            HashMap<String, Object> timeData = new HashMap<>();
            timeData.put("key", timeKey);
            timeData.put("value", timeValue);
            timeArrayRef.child(timeKey).setValue(timeData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess(Void unused) {
                    if (i == 0) {
                        viewTimeListModel.add(new ArrayModel(timeValue, timeKey));
                    } else {
                        viewQuantityListModel.add(new ArrayModel(timeValue, timeKey));
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Value added successfully!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to add value", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

