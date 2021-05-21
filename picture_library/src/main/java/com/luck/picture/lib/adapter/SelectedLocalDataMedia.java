package com.luck.picture.lib.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class SelectedLocalDataMedia extends RecyclerView
        .Adapter<SelectedLocalDataMedia.SelectedLocalDataMediaViewHolder> {

    private List<LocalMedia> list = new ArrayList<>();
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public SelectedLocalDataMedia.SelectedLocalDataMediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View inflate = View.inflate(parent.getContext(), R.layout.selected_local_data, null);

        SelectedLocalDataMediaViewHolder selectedLocalDataMediaViewHolder
                = new SelectedLocalDataMediaViewHolder(inflate);
        return selectedLocalDataMediaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedLocalDataMedia
            .SelectedLocalDataMediaViewHolder holder, int position) {

        if (PictureSelectionConfig.imageEngine != null) {
            PictureSelectionConfig.imageEngine.loadGridImageRound(holder.itemView.getContext(),
                    list.get(position).getPath(), holder.ivCover);
        }

        holder.tvDuration.setText(secToTime((int) (list.get(position).getDuration() / 1000)));

        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.setOnItemClickListener(list.get(position));
                }
            }
        });
    }

    private String secToTime(int time) {

        String timeStr;
        int hour = time / 3600;
        int minute = time / 60 % 60;
        int second = time % 60;

        if (hour > 0) {
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
        } else {
            timeStr = unitFormat(minute) + ":" + unitFormat(second);
        }
        return timeStr;
    }


    private String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10) {
            retStr = "0" + i;
        } else {
            retStr = "" + i;
        }
        return retStr;
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<LocalMedia> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class SelectedLocalDataMediaViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivClose;
        private ImageView ivCover;
        private TextView tvDuration;
        public SelectedLocalDataMediaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivClose = itemView.findViewById(R.id.iv_close);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvDuration = itemView.findViewById(R.id.tv_duration);
        }
    }

   public interface OnItemClickListener{

        void setOnItemClickListener(LocalMedia media);

    }
}
