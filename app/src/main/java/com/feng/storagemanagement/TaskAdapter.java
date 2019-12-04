package com.feng.storagemanagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * @author Feng Zhaohao
 * Created on 2019/12/4
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private Context mContext;
    private List<TaskData> mDataList;

    public TaskAdapter(Context mContext, List<TaskData> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TaskViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_task, null));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int i) {
        taskViewHolder.id.setText(String.valueOf(mDataList.get(i).getId()));
        taskViewHolder.size.setText(String.valueOf(mDataList.get(i).getSize()));
        taskViewHolder.zoneId.setText(mDataList.get(i).getZoneId());
        taskViewHolder.initialAddress.setText(mDataList.get(i).getInitialAddress());
        taskViewHolder.state.setText(mDataList.get(i).getState());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView size;
        TextView zoneId;
        TextView initialAddress;
        TextView state;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_item_task_id);
            size = itemView.findViewById(R.id.tv_item_task_size);
            zoneId = itemView.findViewById(R.id.tv_item_task_zone_id);
            initialAddress = itemView.findViewById(R.id.tv_item_task_initial_address);
            state = itemView.findViewById(R.id.tv_item_task_state);
        }
    }
}
