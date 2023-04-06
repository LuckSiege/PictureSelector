package com.luck.picture.lib.basic;

import android.content.Intent;
import android.os.Bundle;

import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;

/**
 * @author：luck
 * @date：2021/11/24 10:11 上午
 * @describe：IPictureSelectorCommonEvent
 */
public interface IPictureSelectorCommonEvent {

    /**
     * 创建数据查询器
     */
    void onCreateLoader();

    /**
     * View Layout
     *
     * @return resource Id
     */
    int getResourceId();

    /**
     * onKey back fragment or finish
     */
    void onKeyBackFragmentFinish();

    /**
     * fragment onResume
     */
    void onFragmentResume();

    /**
     * 权限被拒
     */
    void handlePermissionDenied(String[] permissionArray);

    /**
     * onSavedInstance
     *
     * @param savedInstanceState
     */
    void reStartSavedInstance(Bundle savedInstanceState);

    /**
     * 权限设置结果
     */
    void handlePermissionSettingResult(String[] permissions);

    /**
     * 设置app语言
     */
    void initAppLanguage();

    /**
     * 重新创建所需引擎
     */
    void onRecreateEngine();

    /**
     * 选择拍照或拍视频
     */
    void onSelectedOnlyCamera();

    /**
     * 选择相机类型；拍照、视频、或录音
     */
    void openSelectedCamera();

    /**
     * 拍照
     */
    void openImageCamera();

    /**
     * 拍视频
     */
    void openVideoCamera();

    /**
     * 录音
     */
    void openSoundRecording();

    /**
     * 选择结果
     *
     * @param currentMedia 当前操作对象
     * @param isSelected   选中状态
     * @return 返回当前选择的状态
     */
    int confirmSelect(LocalMedia currentMedia, boolean isSelected);

    /**
     * 验证共选类型模式可选条件
     *
     * @param media           选中对象
     * @param isSelected      资源是否被选中
     * @param curMimeType     选择的资源类型
     * @param selectVideoSize 已选的视频数量
     * @param fileSize        文件大小
     * @param duration        视频时长
     * @return
     */
    boolean checkWithMimeTypeValidity(LocalMedia media, boolean isSelected, String curMimeType, int selectVideoSize, long fileSize, long duration);

    /**
     * 验证单一类型模式可选条件
     *
     * @param media         选中对象
     * @param isSelected    资源是否被选中
     * @param curMimeType   选择的资源类型
     * @param existMimeType 已选的资源类型
     * @param fileSize      文件大小
     * @param duration      视频时长
     * @return
     */
    boolean checkOnlyMimeTypeValidity(LocalMedia media, boolean isSelected, String curMimeType, String existMimeType, long fileSize, long duration);

    /**
     * 选择结果数据发生改变
     *
     * @param isAddRemove  isAddRemove  添加还是移除操作
     * @param currentMedia 当前操作的对象
     */
    void onSelectedChange(boolean isAddRemove, LocalMedia currentMedia);

    /**
     * 刷新指定数据
     */
    void onFixedSelectedChange(LocalMedia oldLocalMedia);


    /**
     * 分发拍照后生成的LocalMedia
     *
     * @param media
     */
    void dispatchCameraMediaResult(LocalMedia media);

    /**
     * 发送选择数据发生变化的通知
     *
     * @param isAddRemove  添加还是移除操作
     * @param currentMedia 当前操作的对象
     */
    void sendSelectedChangeEvent(boolean isAddRemove, LocalMedia currentMedia);

    /**
     * 刷新指定数据
     */
    void sendFixedSelectedChangeEvent(LocalMedia currentMedia);

    /**
     * {@link SelectorConfig.selectorStyle.getSelectMainStyle().isSelectNumberStyle}
     * <p>
     * isSelectNumberStyle模式下对选择结果编号进行排序
     * </p>
     */
    void sendChangeSubSelectPositionEvent(boolean adapterChange);

    /**
     * 原图选项发生变化
     */
    void sendSelectedOriginalChangeEvent();

    /**
     * 原图选项发生变化
     */
    void onCheckOriginalChange();

    /**
     * 编辑资源
     */
    void onEditMedia(Intent intent);

    /**
     * 选择结果回调
     *
     * @param result
     */
    void onResultEvent(ArrayList<LocalMedia> result);

    /**
     * 裁剪
     * @param result
     */
    void onCrop(ArrayList<LocalMedia> result);

    /**
     * 裁剪
     * @param result
     */
    void onOldCrop(ArrayList<LocalMedia> result);

    /**
     * 压缩
     *
     * @param result
     */
    void onCompress(ArrayList<LocalMedia> result);

    /**
     * 压缩
     *
     * @param result
     */
    @Deprecated
    void onOldCompress(ArrayList<LocalMedia> result);

    /**
     * 验证是否需要裁剪
     *
     * @return
     */
    boolean checkCropValidity();

    /**
     * 验证是否需要裁剪
     *
     * @return
     */
    @Deprecated
    boolean checkOldCropValidity();

    /**
     * 验证是否需要压缩
     *
     * @return
     */
    boolean checkCompressValidity();

    /**
     * 验证是否需要压缩
     *
     * @return
     */
    @Deprecated
    boolean checkOldCompressValidity();

    /**
     * 验证是否需要做沙盒转换处理
     *
     * @return
     */
    boolean checkTransformSandboxFile();

    /**
     * 验证是否需要做沙盒转换处理
     *
     * @return
     */
    @Deprecated
    boolean checkOldTransformSandboxFile();

    /**
     * 验证是否需要添加水印
     *
     * @return
     */
    boolean checkAddBitmapWatermark();

    /**
     * 验证是否需要处理视频缩略图
     */
    boolean checkVideoThumbnail();

    /**
     * 权限申请
     *
     * @param permissionArray
     */
    void onApplyPermissionsEvent(int event, String[] permissionArray);

    /**
     * 权限说明
     *
     * @param isDisplayExplain  是否显示权限说明
     * @param permissionArray   权限组
     */
    void onPermissionExplainEvent(boolean isDisplayExplain, String[] permissionArray);

    /**
     * 拦截相机事件
     *
     * @param cameraMode {@link SelectMimeType}
     */
    void onInterceptCameraEvent(int cameraMode);

    /**
     * 进入Fragment
     */
    void onEnterFragment();

    /**
     * 退出Fragment
     */
    void onExitFragment();

    /**
     * show loading
     */
    void showLoading();

    /**
     * dismiss loading
     */
    void dismissLoading();
}
