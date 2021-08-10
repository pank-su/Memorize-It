package com.example.memorize_it;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.memorize_it.databinding.ActivityGeneralBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class General extends AppCompatActivity {

    private ActivityGeneralBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGeneralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainActivity, R.id.navigation_notifications, R.id.navigation_setiings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_general);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void add_note(View v){
        binding.navView.setSelectedItemId(R.id.mainActivity);
    }

}