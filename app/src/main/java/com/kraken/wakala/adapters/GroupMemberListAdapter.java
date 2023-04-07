package com.kraken.wakala.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.kraken.wakala.R;
import com.kraken.wakala.databinding.ViewItemGroupMemberBinding;
import com.kraken.wakala.interfaces.ListItemListener;
import com.kraken.wakala.models.User;

import java.util.ArrayList;

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> {

    ArrayList<User> data;
    final ListItemListener itemListener;

    public GroupMemberListAdapter(Fragment fragmentImpl, ArrayList<User> data) {
        this.itemListener = (ListItemListener) fragmentImpl;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setMember(data.get(position));
        holder.binding.textRanked.setText(String.valueOf(position));
        holder.binding.getRoot().setOnClickListener(view -> {
            itemListener.onItemClickListener(view, data.get(position), position);
        });
        holder.binding.getRoot().setOnLongClickListener(view -> {
            itemListener.onItemLongClickListener(view, data.get(position), position);
            return true;
        });
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public final ViewItemGroupMemberBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewItemGroupMemberBinding.bind(itemView);
        }
    }
}
