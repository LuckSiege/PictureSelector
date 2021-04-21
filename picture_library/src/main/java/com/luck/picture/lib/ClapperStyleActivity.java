package com.luck.picture.lib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luck.picture.lib.adapter.PictureImageGridAdapter;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnQueryDataResultListener;
import com.luck.picture.lib.model.LocalMediaPageLoader;

import java.util.ArrayList;
import java.util.List;

public class ClapperStyleActivity extends AppCompatActivity {

    private TextView mEmpty;
    private RecyclerView mPictureRecycler;
    private int mPage = 3;
    protected PictureImageGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clapper_style);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mEmpty = findViewById(R.id.empty);
        mPictureRecycler = findViewById(R.id.picture_recycler);
    }

    private void initData() {

        PictureSelectionConfig selectionConfig = PictureSelectionConfig.getCleanInstance();
        mAdapter = new PictureImageGridAdapter(this, selectionConfig);



    }

    private void initListener() {

    }


    class ImageGridAdapter extends RecyclerView.Adapter {

        public static final int TYPE_CAMERA = 1;
        public static final int TYPE_PICTURE = 2;


        private List<LocalMedia> mediaList = new ArrayList<>();

        public void setData(List<LocalMedia> list) {
            this.mediaList = list;
            notifyDataSetChanged();
        }

        public void addData(List<LocalMedia> list) {
            this.mediaList.addAll(list);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            if (viewType == TYPE_CAMERA) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item_camera, parent, false);
                return new CameraViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_image_grid_item, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (getItemViewType(position) == TYPE_CAMERA) {
                CameraViewHolder cameraViewHolder = (CameraViewHolder) holder;
                cameraViewHolder.itemView.setOnClickListener(v -> {
                    //拍照
                });

            } else {
                //展示图片


            }



        }

        @Override
        public int getItemCount() {
            return mediaList.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_CAMERA;
            } else {
                return TYPE_PICTURE;
            }
        }
    }

    public class CameraViewHolder extends RecyclerView.ViewHolder {
        View headerView;
        TextView tvCamera;

        public CameraViewHolder(View itemView) {
            super(itemView);
            headerView = itemView;
            tvCamera = itemView.findViewById(R.id.tvCamera);
            String title = itemView.getContext().getString(R.string.picture_take_picture);
            tvCamera.setText(title);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        TextView tvCheck;
        TextView tvDuration, tvIsGif, tvLongChart;
        View contentView;
        View btnCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            ivPicture = itemView.findViewById(R.id.ivPicture);
            tvCheck = itemView.findViewById(R.id.tvCheck);
            btnCheck = itemView.findViewById(R.id.btnCheck);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvIsGif = itemView.findViewById(R.id.tv_isGif);
            tvLongChart = itemView.findViewById(R.id.tv_long_chart);
        }
    }
}