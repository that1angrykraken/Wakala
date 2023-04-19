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
import com.kraken.wakala.models.GroupMember;
import com.kraken.wakala.models.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> {

    ArrayList<GroupMember> data;
    final ListItemListener itemListener;

    public GroupMemberListAdapter(Fragment fragmentImpl, ArrayList<GroupMember> data) {
        this.itemListener = (ListItemListener) fragmentImpl;
        this.data = data;
    }

    public ArrayList<GroupMember> getData() {
        return data;
    }

    public void setData(ArrayList<GroupMember> data) {
        this.data = data;
        notifyDataSetChanged();
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
        GroupMember member = data.get(position);
        holder.binding.setMember(member);
        holder.binding.setPosition(position);
        Picasso.get().load(member.getProfilePhoto()).into(holder.binding.imgMemberProfilePhoto);
//        if(position < 3) holder.binding.textRanked.setTextAppearance(R.style.text_bold);
//        holder.binding.textRanked.setText(String.valueOf(position+1));
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
