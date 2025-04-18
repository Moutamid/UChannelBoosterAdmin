package com.moutamid.uchannelboostadmin.fragments;

import static android.view.LayoutInflater.from;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.moutamid.uchannelboostadmin.databinding.FragmentViewTasksBinding;
import com.moutamid.uchannelboostadmin.models.ViewTaskModel;
import com.moutamid.uchannelboostadmin.utils.Utils;

import java.util.ArrayList;

public class ViewTasksFragment extends Fragment {

    ArrayList<ViewTaskModel> viewsModelArrayList = new ArrayList<>();
    ArrayList<ViewTaskModel> viewsModelArrayListAll = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;
    private FragmentViewTasksBinding b;

    private ProgressDialog progressDialog;

    private int count = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentViewTasksBinding.inflate(inflater, container, false);

        count = 0;

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        new Thread(() ->
                Utils.databaseReference().child("view_tasks").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            viewsModelArrayList.clear();
                            viewsModelArrayListAll.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                viewsModelArrayList.add(dataSnapshot.getValue(ViewTaskModel.class));
                                viewsModelArrayListAll.add(dataSnapshot.getValue(ViewTaskModel.class));
                                updateProgress(count++);
                            }
                            requireActivity().runOnUiThread(() -> initRecyclerView());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                })).start();

        b.createTaskBtn.setOnClickListener(view -> {
            ViewTaskModel model = new ViewTaskModel();
            model.setTaskKey("");
            showDialog(model, "Create", true);
        });

        b.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return b.getRoot();
    }

    private void updateProgress(int count) {
        requireActivity().runOnUiThread(()
                -> progressDialog.setMessage("Loading... (" + count + ")"));
    }

    private void initRecyclerView() {
        progressDialog.dismiss();
        conversationRecyclerView = b.recyclerViewTasks;
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

    private void showDialog(ViewTaskModel model, String saveBtnText, boolean isCreating) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tasks);
        dialog.setCancelable(true);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        EditText total, current, completed, time, posterid, videourl, thumbnail, taskkey;

        total = dialog.findViewById(R.id.total_et_task);
        current = dialog.findViewById(R.id.current_et_task);
        completed = dialog.findViewById(R.id.completed_et_task);
        time = dialog.findViewById(R.id.total_time_et_task);
        posterid = dialog.findViewById(R.id.poster_id_et_task);
        videourl = dialog.findViewById(R.id.video_url_et_task);
        thumbnail = dialog.findViewById(R.id.thumbnail_et_task);
        taskkey = dialog.findViewById(R.id.task_key_et_task);
        time.setVisibility(View.VISIBLE);

        if (!isCreating) {
            total.setText(model.getTotalViewsQuantity() + "");
            current.setText(model.getCurrentViewsQuantity() + "");
            completed.setText(model.getCompletedDate() + "");
            time.setText(model.getTotalViewTimeQuantity() + "");
            posterid.setText(model.getPosterUid() + "");
            videourl.setText(model.getVideoUrl() + "");
            thumbnail.setText(model.getThumbnailUrl() + "");
            taskkey.setText(model.getTaskKey() + "");
        }

        Button button = dialog.findViewById(R.id.save_btn_tasks);
        button.setText(saveBtnText);

        if (isCreating) {
            model.setTaskKey(Utils.databaseReference().child("view_tasks").push().getKey());
            taskkey.setText(model.getTaskKey());
            current.setText("0");
            completed.setText("error");
        }

        dialog.findViewById(R.id.save_btn_tasks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                model.setTotalViewsQuantity(total.getText().toString());
                model.setCompletedDate(completed.getText().toString());
                model.setCurrentViewsQuantity(Integer.parseInt(current.getText().toString()));
                model.setTotalViewTimeQuantity(time.getText().toString());
                model.setPosterUid(posterid.getText().toString());
                model.setVideoUrl(videourl.getText().toString());
                model.setThumbnailUrl(thumbnail.getText().toString());
                model.setTaskKey(taskkey.getText().toString());

                Utils.databaseReference().child("view_tasks").child(model.getTaskKey())
                        .setValue(model);

                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);
    }

    String TAG = "FUCK";

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> implements Filterable {

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    Log.d(TAG, "performFiltering: charsequence: " + charSequence.toString());
                    String key = charSequence.toString();
                    if (key.isEmpty()) {
                        Log.d(TAG, "performFiltering: key.isempty");
                        viewsModelArrayList = viewsModelArrayListAll;
                    } else {
                        Log.d(TAG, "performFiltering: else");
                        ArrayList<ViewTaskModel> filtered = new ArrayList<>();

                        for (ViewTaskModel model : viewsModelArrayListAll) {
                            Log.d(TAG, "performFiltering: for loop");
                            if (model.getPosterUid().toLowerCase().contains(key.toLowerCase())) {
                                filtered.add(model);
                            }
                        }
                        viewsModelArrayList = filtered;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = viewsModelArrayList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    Log.d(TAG, "publishResults: ");
                    viewsModelArrayList = (ArrayList<ViewTaskModel>) filterResults.values;
                    adapter.notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_task_item, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {

            ViewTaskModel model = viewsModelArrayList.get(position);

            with(requireContext())
                    .asBitmap()
                    .load(model.getThumbnailUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.color.browser_actions_bg_grey)
                            .error(R.color.browser_actions_bg_grey)
                    )
                    .diskCacheStrategy(DATA)
                    .into(holder.imageView);

            holder.posterid.setText("POSTER ID: " + model.getPosterUid());
            holder.total.setText("TOTAL VIEWS: " + model.getTotalViewsQuantity());
            holder.current.setText("CURRENT VIEWS: " + model.getCurrentViewsQuantity());
            holder.completed.setText("COMPLETED: " + model.getCompletedDate());

            holder.editBtn.setOnClickListener(view -> {
                showDialog(model, "Save", false);
            });

            holder.deleteBtn.setOnClickListener(view -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Are you sure?")
                        .setMessage("Do you really want to delete this task?")
                        .setPositiveButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                        .setNegativeButton("Yes", (dialogInterface, i) ->
                                Utils.databaseReference().child("view_tasks").child(model.getTaskKey())
                                        .removeValue())
                        .show();
            });

            holder.viewBtn.setOnClickListener(view -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(model.getVideoUrl()));
                startActivity(i);
            });

        }

        @Override
        public int getItemCount() {
            if (viewsModelArrayList == null)
                return 0;
            return viewsModelArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView posterid, total, current, completed;
            ImageView editBtn, deleteBtn, viewBtn, imageView;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                imageView = v.findViewById(R.id.image);
                posterid = v.findViewById(R.id.poster_id_tv_task);
                total = v.findViewById(R.id.total_tv_task);
                current = v.findViewById(R.id.current_tv_task);
                editBtn = v.findViewById(R.id.editBtn_task);
                deleteBtn = v.findViewById(R.id.deleteBtn_task);
                viewBtn = v.findViewById(R.id.viewBtn);
                completed = v.findViewById(R.id.completed_tv_task);

            }
        }

    }

}