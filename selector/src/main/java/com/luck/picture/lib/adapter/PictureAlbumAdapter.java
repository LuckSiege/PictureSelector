package com.luck.picture.lib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnAlbumItemClickListener;
import com.luck.picture.lib.style.AlbumWindowStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2016-12-11 17:02
 * @describe：PictureAlbumDirectoryAdapter
 */
public class PictureAlbumAdapter extends RecyclerView.Adapter<PictureAlbumAdapter.ViewHolder> {
    private List<LocalMediaFolder> albumList;


    public void bindAlbumData(List<LocalMediaFolder> albumList) {
        this.albumList = new ArrayList<>(albumList);
    }

    public List<LocalMediaFolder> getAlbumList() {
        return albumList != null ? albumList : new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ps_album_folder_item, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LocalMediaFolder folder = albumList.get(position);
        String name = folder.getName();
        int imageNum = folder.getImageNum();
        String imagePath = folder.getFirstImagePath();
        boolean isChecked = folder.isChecked();
        int checkedNum = folder.getCheckedNum();
        holder.tvSign.setVisibility(checkedNum > 0 ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setSelected(isChecked);
        String firstMimeType = folder.getFirstMimeType();
        if (PictureMimeType.isHasAudio(firstMimeType)) {
            holder.ivFirstImage.setImageResource(R.drawable.ps_audio_placeholder);
        } else {
            if (PictureSelectionConfig.imageEngine != null) {
                PictureSelectionConfig.imageEngine.loadAlbumCover(holder.itemView.getContext(),
                        imagePath, holder.ivFirstImage);
            }
        }
        Context context = holder.itemView.getContext();
        String firstTitle = folder.getOfAllType() != -1 ? folder.getOfAllType() == SelectMimeType.ofAudio() ?
                context.getString(R.string.ps_all_audio)
                : context.getString(R.string.ps_camera_roll) : name;
        holder.tvFolderName.setText(context.getString(R.string.ps_camera_roll_num, firstTitle, imageNum));
        holder.itemView.setOnClickListener(view -> {
            if (onAlbumItemClickListener == null) {
                return;
            }
            int size = albumList.size();
            for (int i = 0; i < size; i++) {
                LocalMediaFolder mediaFolder = albumList.get(i);
                mediaFolder.setChecked(false);
            }
            folder.setChecked(true);
            notifyDataSetChanged();
            onAlbumItemClickListener.onItemClick(position, folder);
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFirstImage;
        TextView tvFolderName, tvSign;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFirstImage = itemView.findViewById(R.id.first_image);
            tvFolderName = itemView.findViewById(R.id.tv_folder_name);
            tvSign = itemView.findViewById(R.id.tv_sign);
            PictureSelectorStyle selectorStyle = PictureSelectionConfig.selectorStyle;
            AlbumWindowStyle albumWindowStyle = selectorStyle.getAlbumWindowStyle();
            int itemBackground = albumWindowStyle.getAlbumAdapterItemBackground();
            if (itemBackground != 0) {
                itemView.setBackgroundResource(itemBackground);
            }
            int itemSelectStyle = albumWindowStyle.getAlbumAdapterItemSelectStyle();
            if (itemSelectStyle != 0) {
                tvSign.setBackgroundResource(itemSelectStyle);
            }
            int titleColor = albumWindowStyle.getAlbumAdapterItemTitleColor();
            if (titleColor != 0) {
                tvFolderName.setTextColor(titleColor);
            }
            int titleSize = albumWindowStyle.getAlbumAdapterItemTitleSize();
            if (titleSize > 0) {
                tvFolderName.setTextSize(titleSize);
            }
        }
    }

    private OnAlbumItemClickListener onAlbumItemClickListener;

    /**
     * 专辑列表桥接类
     *
     * @param listener
     */
    public void setOnIBridgeAlbumWidget(OnAlbumItemClickListener listener) {
        this.onAlbumItemClickListener = listener;
    }

}
