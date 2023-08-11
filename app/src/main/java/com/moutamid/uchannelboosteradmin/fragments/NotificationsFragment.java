package com.moutamid.uchannelboosteradmin.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.moutamid.uchannelboosteradmin.databinding.FragmentNotificationsBinding;
import com.moutamid.uchannelboosteradmin.notifications.FcmNotificationsSender;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding b;

    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentNotificationsBinding.inflate(inflater, container, false);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        b.sendBtn.setOnClickListener(view -> {
            if (b.titleET.getText().toString().isEmpty())
                return;

            if (b.descET.getText().toString().isEmpty())
                return;

            progressDialog.show();

            new FcmNotificationsSender("/topics/all",
                    b.titleET.getText().toString(),
                    b.descET.getText().toString(),
                    requireContext().getApplicationContext(),
                    requireActivity()
            ).SendNotifications();

            progressDialog.dismiss();

            Toast.makeText(requireContext(), "Sent!", Toast.LENGTH_SHORT).show();

        });

        return b.getRoot();
    }

}