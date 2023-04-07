package com.kraken.wakala.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kraken.wakala.databinding.FragmentLeaderboardBinding;
import com.kraken.wakala.viewmodels.UserViewModel;

public class GroupFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private UserViewModel userViewModel;

    public GroupFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        return binding.getRoot();
    }
}