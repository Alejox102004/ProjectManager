package com.example.projectmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    private List<Project> projects;
    private OnProjectClickListener clickListener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    public interface OnEditClickListener {
        void onEditClick(Project project);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Project project);
    }

    public ProjectAdapter(List<Project> projects, OnProjectClickListener clickListener,
                          OnEditClickListener editListener, OnDeleteClickListener deleteListener) {
        this.projects = projects;
        this.clickListener = clickListener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.tvName.setText(project.getName());
        holder.tvDescription.setText(project.getDescription());
        holder.itemView.setOnClickListener(v -> clickListener.onProjectClick(project));
        holder.btnEdit.setOnClickListener(v -> editListener.onEditClick(project));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(project));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription;
        Button btnEdit, btnDelete;

        ProjectViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_project_name);
            tvDescription = itemView.findViewById(R.id.tv_project_description);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}