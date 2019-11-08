package com.luck.picture.lib.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.adapter
 * email：893855882@qq.com
 * data：16/12/31
 */
public class PictureAlbumDirectoryAdapter extends RecyclerView.Adapter<PictureAlbumDirectoryAdapter.ViewHolder> {
    private Context mContext;
    private List<LocalMediaFolder> folders = new ArrayList<>();
    private int mimeType;

    public PictureAlbumDirectoryAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void bindFolderData(List<LocalMediaFolder> folders) {
        this.folders = folders;
        notifyDataSetChanged();
    }

    public void setMimeType(int mimeType) {
        this.mimeType = mimeType;
    }

    public List<LocalMediaFolder> getFolderData() {
        if (folders == null) {
            folders = new ArrayList<>();
        }
        return folders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.picture_album_folder_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LocalMediaFolder folder = folders.get(position);
        String name = folder.getName();
        int imageNum = folder.getImageNum();
        String imagePath = folder.getFirstImagePath();
        boolean isChecked = folder.isChecked();
        int checkedNum = folder.getCheckedNum();
        holder.tvSign.setVisibility(checkedNum > 0 ? View.VISIBLE : View.INVISIBLE);
        holder.itemView.setSelected(isChecked);
        if (mimeType == PictureMimeType.ofAudio()) {
            holder.ivFirstImage.setImageResource(R.drawable.audio_placeholder);
        } else {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_placeholder)
                    .centerCrop()
                    .sizeMultiplier(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(160, 160);
            Glide.with(holder.itemView.getContext())
                    .asBitmap()
                    .load(imagePath)
                    .apply(options)
                    .into(new BitmapImageViewTarget(holder.ivFirstImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.
                                            create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCornerRadius(8);
                            holder.ivFirstImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
        holder.imageNum.setText("(" + imageNum + ")");
        holder.tvFolderName.setText(name);
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                for (LocalMediaFolder mediaFolder : folders) {
                    mediaFolder.setChecked(false);
                }
                folder.setChecked(true);
                notifyDataSetChanged();
                onItemClickListener.onItemClick(folder.getName(), folder.getImages());
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFirstImage;
        TextView tvFolderName, imageNum, tvSign;

        public ViewHolder(View itemView) {
            super(itemView);
            ivFirstImage = itemView.findViewById(R.id.first_image);
            tvFolderName = itemView.findViewById(R.id.tv_folder_name);
            imageNum = itemView.findViewById(R.id.image_num);
            tvSign = itemView.findViewById(R.id.tv_sign);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(String folderName, List<LocalMedia> images);
    }
}
