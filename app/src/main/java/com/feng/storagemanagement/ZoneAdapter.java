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
public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ZoneViewHolder>{

    private Context mContext;
    private List<ZoneData> mDataList;

    public ZoneAdapter(Context mContext, List<ZoneData> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ZoneViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_zone, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder zoneViewHolder, int i) {
        zoneViewHolder.id.setText(String.valueOf(mDataList.get(i).getId()));
        zoneViewHolder.size.setText(String.valueOf(mDataList.get(i).getSize()));
        zoneViewHolder.initialAddress.setText(mDataList.get(i).getInitialAddress());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ZoneViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView size;
        TextView initialAddress;

        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_item_zone_id);
            size = itemView.findViewById(R.id.tv_item_zone_size);
            initialAddress = itemView.findViewById(R.id.tv_item_zone_initial_address);
        }
    }
}
