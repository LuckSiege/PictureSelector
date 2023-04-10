package com.luck.picture.lib.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.InjectResourceSource;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectorConfig;
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
    private final SelectorConfig selectorConfig;

    public PictureAlbumAdapter(SelectorConfig config) {
        this.selectorConfig = config;
    }

    public void bindAlbumData(List<LocalMediaFolder> albumList) {
        this.albumList = new ArrayList<>(albumList);
    }

    public List<LocalMediaFolder> getAlbumList() {
        return albumList != null ? albumList : new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResourceId = InjectResourceSource.getLayoutResource(parent.getContext(), InjectResourceSource.ALBUM_ITEM_LAYOUT_RESOURCE, selectorConfig);
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layoutResourceId != InjectResourceSource.DEFAULT_LAYOUT_RESOURCE ? layoutResourceId : R.layout.ps_album_folder_item, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LocalMediaFolder folder = albumList.get(position);
        String name = folder.getFolderName();
        int imageNum = folder.getFolderTotalNum();
        String imagePath = folder.getFirstImagePath();
        holder.tvSelectTag.setVisibility(folder.isSelectTag() ? View.VISIBLE : View.INVISIBLE);
        LocalMediaFolder currentLocalMediaFolder = selectorConfig.currentLocalMediaFolder;
        holder.itemView.setSelected(currentLocalMediaFolder != null
                && folder.getBucketId() == currentLocalMediaFolder.getBucketId());
        String firstMimeType = folder.getFirstMimeType();
        if (PictureMimeType.isHasAudio(firstMimeType)) {
            holder.ivFirstImage.setImageResource(R.drawable.ps_audio_placeholder);
        } else {
            if (selectorConfig.imageEngine != null) {
                selectorConfig.imageEngine.loadAlbumCover(holder.itemView.getContext(),
                        imagePath, holder.ivFirstImage);
            }
        }
        Context context = holder.itemView.getContext();
        holder.tvFolderName.setText(context.getString(R.string.ps_camera_roll_num, name, imageNum));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAlbumItemClickListener == null) {
                    return;
                }
                onAlbumItemClickListener.onItemClick(position, folder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFirstImage;
        TextView tvFolderName, tvSelectTag;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFirstImage = itemView.findViewById(R.id.first_image);
            tvFolderName = itemView.findViewById(R.id.tv_folder_name);
            tvSelectTag = itemView.findViewById(R.id.tv_select_tag);
            PictureSelectorStyle selectorStyle = selectorConfig.selectorStyle;
            AlbumWindowStyle albumWindowStyle = selectorStyle.getAlbumWindowStyle();
            int itemBackground = albumWindowStyle.getAlbumAdapterItemBackground();
            if (itemBackground != 0) {
                itemView.setBackgroundResource(itemBackground);
            }
            int itemSelectStyle = albumWindowStyle.getAlbumAdapterItemSelectStyle();
            if (itemSelectStyle != 0) {
                tvSelectTag.setBackgroundResource(itemSelectStyle);
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
