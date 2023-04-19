package com.kraken.wakala.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;

public class AppViewModelStore extends ViewModelStore {
    private static ViewModelStore instance;

    public AppViewModelStore() {
    }

    @NonNull
    public static ViewModelStore getInstance() {
        if(instance == null) instance = new ViewModelStore();
        return instance;
    }
}
