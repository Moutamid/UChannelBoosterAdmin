package com.moutamid.viewplusadmin.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.moutamid.viewplusadmin.R;
import com.moutamid.viewplusadmin.databinding.ActivityNavigationBinding;
import com.moutamid.viewplusadmin.utils.Constants;
import com.moutamid.viewplusadmin.utils.ContextWrapper;
import com.moutamid.viewplusadmin.utils.Utils;

import java.util.Locale;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationBinding binding;

    @Override
    protected void attachBaseContext(Context newBase) {
        Locale newLocale = new Locale(Utils.getString(Constants.CURRENT_LANGUAGE_CODE, "ko"));

        Context context = ContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigation.toolbar);
        binding.appBarNavigation.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_admin_users, R.id.nav_app_user)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        navigationView.getMenu().getItem(0).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_dashboard);
                        closeDrawer();
                        break;
                    case R.id.nav_admin_users:
                        navigationView.getMenu().getItem(1).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_admin_users);
                        closeDrawer();
                        break;
                    case R.id.nav_app_user:
                        navigationView.getMenu().getItem(2).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_app_user);
                        closeDrawer();
                        break;
                    case R.id.nav_view_tasks:
                        navigationView.getMenu().getItem(3).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_view_tasks);
                        closeDrawer();
                        break;
                    case R.id.nav_like_tasks:
                        navigationView.getMenu().getItem(4).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_like_tasks);
                        closeDrawer();
                        break;
                    case R.id.nav_subscribe_tasks:
                        navigationView.getMenu().getItem(5).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_subscribe_tasks);
                        closeDrawer();
                        break;
                    case R.id.nav_banners:
                        navigationView.getMenu().getItem(6).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_banners);
                        closeDrawer();
                        break;
                    case R.id.nav_coins:
                        navigationView.getMenu().getItem(7).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_coins);
                        closeDrawer();
                        break;
                    case R.id.nav_notifications:
                        navigationView.getMenu().getItem(8).setChecked(true);
                        navigationView.setCheckedItem(R.id.nav_notifications);
                        closeDrawer();
                        break;
                    case R.id.nav_language:
                        closeDrawer();
                        showLanguageDialog();
                        break;
                }
                return false;
            }
        });*/

    }

    private void closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Utils.store(Constants.IS_LOGIN, false);
            Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        }
        if (item.getItemId() == R.id.nav_language){
            showLanguageDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        AlertDialog dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
        final CharSequence[] items = {
                "English",
                "Korean"};
        final CharSequence[] code = {
                Constants.LANGUAGE_CODE_ENGLISH,
                Constants.LANGUAGE_CODE_KOREAN};
        builder.setTitle("Change language");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                dialog.dismiss();
                Utils.store(Constants.CURRENT_LANGUAGE_CODE, String.valueOf(code[position]));
                try {
                    Utils.changeLanguage(String.valueOf(code[position]));
//                attachBaseContext(BottomNavigationActivity.this);
                    recreate();
                } catch (Exception e) {
                    Log.e("TAG", "onClick: ERROR: " + e.getMessage());
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}