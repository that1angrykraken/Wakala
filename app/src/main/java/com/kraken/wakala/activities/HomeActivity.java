package com.kraken.wakala.activities;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.kraken.wakala.R;
import com.kraken.wakala.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    private int backPressedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_1);
        NavController navController = navHostFragment.getNavController();
        getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backPressedCount++;
                if(backPressedCount == 1) Toast.makeText(getBaseContext(), getString(R.string.toast_exit_message), Toast.LENGTH_SHORT).show();
                if(backPressedCount == 2) finish();
            }
        });
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
    }
}