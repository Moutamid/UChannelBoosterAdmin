package com.moutamid.uchannelboostadmin.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.moutamid.uchannelboostadmin.databinding.ActivityMainBinding;
import com.moutamid.uchannelboostadmin.models.AdminUserModel;
import com.moutamid.uchannelboostadmin.utils.Constants;
import com.moutamid.uchannelboostadmin.utils.ContextWrapper;
import com.moutamid.uchannelboostadmin.utils.Utils;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding b;

    ArrayList<AdminUserModel> modelArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale = new Locale(Utils.getString(Constants.CURRENT_LANGUAGE_CODE, "en"));
        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constants.adjustFontScale(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        if (Utils.getBoolean(Constants.IS_LOGIN, false)) {
            Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
            return;
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        b.loginBtn.setOnClickListener(view -> {
            if (b.emailEt.getText().toString().isEmpty()) {
                return;
            }
            if (b.passwordEt.getText().toString().isEmpty()) {
                return;
            }

            String email = b.emailEt.getText().toString();
            String password = b.passwordEt.getText().toString();

            if (email.toLowerCase().equals("admin") && password.toLowerCase().equals("admin")) {
                Utils.store(Constants.IS_LOGIN, true);
                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(intent);
            }

            /*progressDialog.show();
            Utils.databaseReference().child("WebUser").orderByChild("Email").equalTo(b.emailEt.getText().toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                Utils.databaseReference().child("WebUser").orderByChild("Password").equalTo(b.passwordEt.getText().toString())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    progressDialog.dismiss();
                                                    Utils.store(Constants.IS_LOGIN, true);
                                                    Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    finish();
                                                    startActivity(intent);
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Password wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Email not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });*/

        });

    }
}