package com.kraken.wakala.interfaces;

import android.view.View;

public interface ListItemListener {
    void onItemClickListener(View view, Object object, int i);

    void onItemLongClickListener(View view, Object object, int i);
}
