package com.luck.picture.lib.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.listener.OnAlbumItemClickListener;
import com.luck.picture.lib.tools.AttrsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2016-12-11 17:02
 * @describe：PictureAlbumDirectoryAdapter
 */
public class PictureAlbumDirectoryAdapter extends RecyclerView.Adapter<PictureAlbumDirectoryAdapter.ViewHolder> {
    private List<LocalMediaFolder> folders = new ArrayList<>();
    private int chooseMode;

    public PictureAlbumDirectoryAdapter(PictureSelectionConfig config) {
        super();
        this.chooseMode = config.chooseMode;
    }

    public void bindFolderData(List<LocalMediaFolder> folders) {
        this.folders = folders == null ? new ArrayList<>() : folders;
        notifyDataSetChanged();
    }

    public void setChooseMode(int chooseMode) {
        this.chooseMode = chooseMode;
    }

    public List<LocalMediaFolder> getFolderData() {
        return folders == null ? new ArrayList<>() : folders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.picture_album_folder_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LocalMediaFolder folder = folders.get(position);
        String name = folder.getName();
        int imageNum = folder.getImageNum();
        String imagePath = folder.getFirstImagePath();
        boolean isChecked = folder.isChecked();
        int checkedNum = folder.getCheckedNum();
        holder.tvSign.setVisibility(checkedNum > 0 ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setSelected(isChecked);
        if (PictureSelectionConfig.uiStyle != null) {
            if (PictureSelectionConfig.uiStyle.picture_album_backgroundStyle != 0) {
                holder.itemView.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_album_backgroundStyle);
            }
        } else if (PictureSelectionConfig.style != null) {
            if (PictureSelectionConfig.style.pictureAlbumStyle != 0) {
                holder.itemView.setBackgroundResource(PictureSelectionConfig.style.pictureAlbumStyle);
            }
        }
        if (chooseMode == PictureMimeType.ofAudio()) {
            holder.ivFirstImage.setImageResource(R.drawable.picture_audio_placeholder);
        } else {
            if (PictureSelectionConfig.imageEngine != null) {
                PictureSelectionConfig.imageEngine.loadFolderImage(holder.itemView.getContext(),
                        imagePath, holder.ivFirstImage);
            }
        }
        Context context = holder.itemView.getContext();
        String firstTitle = folder.getOfAllType() != -1 ? folder.getOfAllType() == PictureMimeType.ofAudio() ?
                context.getString(R.string.picture_all_audio)
                : context.getString(R.string.picture_camera_roll) : name;
        holder.tvFolderName.setText(context.getString(R.string.picture_camera_roll_num, firstTitle, imageNum));
        holder.itemView.setOnClickListener(view -> {
            if (onAlbumItemClickListener != null) {
                int size = folders.size();
                for (int i = 0; i < size; i++) {
                    LocalMediaFolder mediaFolder = folders.get(i);
                    mediaFolder.setChecked(false);
                }
                folder.setChecked(true);
                notifyDataSetChanged();
                onAlbumItemClickListener.onItemClick(position, folder.isCameraFolder(), folder.getBucketId(), folder.getName(), folder.getData());
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFirstImage;
        TextView tvFolderName, tvSign;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFirstImage = itemView.findViewById(R.id.first_image);
            tvFolderName = itemView.findViewById(R.id.tv_folder_name);
            tvSign = itemView.findViewById(R.id.tv_sign);
            if (PictureSelectionConfig.uiStyle != null) {
                if (PictureSelectionConfig.uiStyle.picture_album_checkDotStyle != 0) {
                    tvSign.setBackgroundResource(PictureSelectionConfig.uiStyle.picture_album_checkDotStyle);
                }
                if (PictureSelectionConfig.uiStyle.picture_album_textColor != 0) {
                    tvFolderName.setTextColor(PictureSelectionConfig.uiStyle.picture_album_textColor);
                }
                if (PictureSelectionConfig.uiStyle.picture_album_textSize > 0) {
                    tvFolderName.setTextSize(PictureSelectionConfig.uiStyle.picture_album_textSize);
                }
            } else if (PictureSelectionConfig.style != null) {
                if (PictureSelectionConfig.style.pictureFolderCheckedDotStyle != 0) {
                    tvSign.setBackgroundResource(PictureSelectionConfig.style.pictureFolderCheckedDotStyle);
                }
                if (PictureSelectionConfig.style.folderTextColor != 0) {
                    tvFolderName.setTextColor(PictureSelectionConfig.style.folderTextColor);
                }
                if (PictureSelectionConfig.style.folderTextSize > 0) {
                    tvFolderName.setTextSize(PictureSelectionConfig.style.folderTextSize);
                }
            } else {
                Drawable folderCheckedDotDrawable = AttrsUtils.getTypeValueDrawable(itemView.getContext(), R.attr.picture_folder_checked_dot, R.drawable.picture_orange_oval);
                tvSign.setBackground(folderCheckedDotDrawable);
                int folderTextColor = AttrsUtils.getTypeValueColor(itemView.getContext(), R.attr.picture_folder_textColor);
                if (folderTextColor != 0) {
                    tvFolderName.setTextColor(folderTextColor);
                }
                float folderTextSize = AttrsUtils.getTypeValueSize(itemView.getContext(), R.attr.picture_folder_textSize);
                if (folderTextSize > 0) {
                    tvFolderName.setTextSize(TypedValue.COMPLEX_UNIT_PX, folderTextSize);
                }
            }
        }
    }

    private OnAlbumItemClickListener onAlbumItemClickListener;

    public void setOnAlbumItemClickListener(OnAlbumItemClickListener listener) {
        this.onAlbumItemClickListener = listener;
    }
}
