package com.luck.picture.lib.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.luck.picture.lib.R;
import com.luck.picture.lib.listener.OnItemClickListener;
import com.luck.picture.lib.tools.ScreenUtils;

/**
 * @author：luck
 * @date：2019-12-12 16:39
 * @describe：PhotoSelectedDialog
 */
public class PhotoItemSelectedDialog extends DialogFragment implements View.OnClickListener {
    public static final int IMAGE_CAMERA = 0;
    public static final int VIDEO_CAMERA = 1;
    private TextView tvPicturePhoto, tvPictureVideo, tvPictureCancel;

    public static PhotoItemSelectedDialog newInstance() {
        PhotoItemSelectedDialog selectedDialog = new PhotoItemSelectedDialog();
        return selectedDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
        return inflater.inflate(R.layout.picture_dialog_camera_selected, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPicturePhoto = view.findViewById(R.id.picture_tv_photo);
        tvPictureVideo = view.findViewById(R.id.picture_tv_video);
        tvPictureCancel = view.findViewById(R.id.picture_tv_cancel);
        tvPictureVideo.setOnClickListener(this);
        tvPicturePhoto.setOnClickListener(this);
        tvPictureCancel.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialogStyle();
    }

    /**
     * DialogFragment Style
     */
    private void initDialogStyle() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ScreenUtils.getScreenWidth(getContext()), RelativeLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.PictureThemeDialogFragmentAnim);
            }
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (onItemClickListener != null) {
            if (id == R.id.picture_tv_photo) {
                onItemClickListener.onItemClick(v, IMAGE_CAMERA);
            }
            if (id == R.id.picture_tv_video) {
                onItemClickListener.onItemClick(v, VIDEO_CAMERA);
            }
        }

        dismissAllowingStateLoss();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
