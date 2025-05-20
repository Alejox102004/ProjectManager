package com.example.projectmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private List<Activity> activities;
    private OnActivityClickListener clickListener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnActivityClickListener {
        void onActivityClick(Activity activity);
    }

    public interface OnEditClickListener {
        void onEditClick(Activity activity);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Activity activity);
    }

    public ActivityAdapter(List<Activity> activities, OnActivityClickListener clickListener,
                           OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
        this.activities = activities;
        this.clickListener = clickListener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);
        holder.tvName.setText(activity.getName());
        holder.tvStatus.setText(activity.getStatus());
        holder.itemView.setOnClickListener(v -> clickListener.onActivityClick(activity));
        holder.btnEdit.setOnClickListener(v -> editListener.onEditClick(activity));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(activity));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus;
        Button btnEdit, btnDelete;

        ActivityViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_activity_name);
            tvStatus = itemView.findViewById(R.id.tv_activity_status);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}