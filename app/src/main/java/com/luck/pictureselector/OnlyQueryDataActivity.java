package com.luck.pictureselector;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;
import com.luck.picture.lib.interfaces.OnQueryAllAlbumListener;
import com.luck.picture.lib.interfaces.OnQueryDataSourceListener;
import com.luck.picture.lib.loader.IBridgeMediaLoader;
import com.luck.picture.lib.utils.DateUtils;
import com.luck.picture.lib.utils.DensityUtil;
import com.luck.picture.lib.widget.RecyclerPreloadView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：luck
 * @date：2022/2/17 6:24 下午
 * @describe：OnlyQueryDataActivity
 */
public class OnlyQueryDataActivity extends AppCompatActivity {
    private final List<LocalMedia> mData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_query_data);
        RecyclerPreloadView mRecycler = findViewById(R.id.recycler);
        mRecycler.addItemDecoration(new GridSpacingItemDecoration(4,
                DensityUtil.dip2px(this, 1), false));
        mRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        RecyclerView.ItemAnimator itemAnimator = mRecycler.getItemAnimator();
        if (itemAnimator != null) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
            mRecycler.setItemAnimator(null);
        }

        GridAdapter adapter = new GridAdapter(mData);
        mRecycler.setAdapter(adapter);
        PictureSelector.create(this)
                .dataSource(SelectMimeType.ofAll())
                .setQuerySortOrder(MediaStore.MediaColumns.DATE_MODIFIED + " DESC")
                .obtainMediaData(new OnQueryDataSourceListener<LocalMedia>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(List<LocalMedia> result) {
                        mData.addAll(result);
                        adapter.notifyDataSetChanged();
                    }
                });

        findViewById(R.id.tv_build_loader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IBridgeMediaLoader loader = PictureSelector.create(v.getContext())
                        .dataSource(SelectMimeType.ofImage()).buildMediaLoader();
                loader.loadAllAlbum(new OnQueryAllAlbumListener<LocalMediaFolder>() {
                    @Override
                    public void onComplete(List<LocalMediaFolder> result) {

                    }
                });
            }
        });
    }

    public static class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {
        private final List<LocalMedia> list;

        public GridAdapter(List<LocalMedia> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gv_filter_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            viewHolder.mIvDel.setVisibility(View.GONE);
            LocalMedia media = list.get(position);
            int chooseModel = media.getChooseModel();
            String path = media.getPath();
            long duration = media.getDuration();
            viewHolder.tvDuration.setVisibility(PictureMimeType.isHasVideo(media.getMimeType())
                    ? View.VISIBLE : View.GONE);
            if (chooseModel == SelectMimeType.ofAudio()) {
                viewHolder.tvDuration.setVisibility(View.VISIBLE);
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                        (R.drawable.ps_ic_audio, 0, 0, 0);
            } else {
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                        (R.drawable.ps_ic_video, 0, 0, 0);
            }
            viewHolder.tvDuration.setText(DateUtils.formatDurationTime(duration));
            if (chooseModel == SelectMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(R.drawable.ps_audio_placeholder);
            } else {
                Glide.with(viewHolder.itemView.getContext())
                        .load(PictureMimeType.isContent(path) ? Uri.parse(path) : path)
                        .centerCrop()
                        .placeholder(R.drawable.ps_image_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(viewHolder.mImg);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            ImageView mImg;
            ImageView mIvDel;
            TextView tvDuration;

            public ViewHolder(View view) {
                super(view);
                mImg = view.findViewById(R.id.fiv);
                mIvDel = view.findViewById(R.id.iv_del);
                tvDuration = view.findViewById(R.id.tv_duration);
            }
        }
    }
}
