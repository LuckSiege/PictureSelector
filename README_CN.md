# PictureSelector 3.0
   一款针对Android平台下的图片选择器，支持从相册获取图片、视频、音频&拍照，支持裁剪(单图or多图裁剪)、压缩、主题自定义配置等功能，支持动态获取权限&适配Android 5.0+系统的开源图片选择框架。<br>
    
   [English🇺🇸](README.md)

   [效果体验](https://github.com/LuckSiege/PictureSelector/raw/version_component/app/demo/demo_2023-07-30_110215_kotlin-v1.0.0-beta.apk)<br>

[![Maven Central](https://img.shields.io/badge/maven%20central-kotlin_v1.0.0-yellow)](https://github.com/LuckSiege)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/LuckSiege)
[![Star](https://img.shields.io/github/stars/LuckSiege/PictureSelector.svg)](https://github.com/LuckSiege/PictureSelector)


## 目录
-[最新版本](https://github.com/LuckSiege/PictureSelector/releases/tag/kotlin-v1.0.0-beta)<br>
-[如何引用](#如何引用)<br>
-[进阶使用](#进阶使用)<br>
-[权限](#权限)<br>
-[返回结果说明](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-LocalMedia%E8%AF%B4%E6%98%8E)<br>
-[演示效果](#演示效果)<br>
-[混淆配置](#混淆配置)<br>
-[常见错误](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%B8%B8%E8%A7%81%E9%94%99%E8%AF%AF)<br>
-[如何提Issues](https://github.com/LuckSiege/PictureSelector/wiki/%E5%A6%82%E4%BD%95%E6%8F%90Issues%3F)<br>
-[兼容性测试](#兼容性测试)<br>
-[联系方式](#联系方式)<br>
-[License](#License)<br>



## 如何引用

使用Gradle
```sh
repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation 'io.github.lucksiege:pictureselector:kotlin-v1.0.0-beta'
}
```

或者Maven:

```sh
<dependency>
  <groupId>io.github.lucksiege</groupId>
  <artifactId>pictureselector</artifactId>
  <version>kotlin-v1.0.0-beta</version>
</dependency>
```

## 权限

权限使用说明，请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E6%9D%83%E9%99%90%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)

```sh
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_MEDIA_STORAGE" />
<uses-permission android:name="android.permission.WRITE_SETTINGS" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.VIBRATE" />

Android 13版本适配，细化存储权限
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
```

Android 11 使用相机，需要再AndroidManifest.xm 添加如下代码：

```sh
<queries package="${applicationId}">
    <intent>
        <action android:name="android.media.action.IMAGE_CAPTURE">

        </action>
    </intent>
    <intent>
        <action android:name="android.media.action.ACTION_VIDEO_CAPTURE">

        </action>
    </intent>
</queries>
```

## ImageEngine
[GlideEngine](https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/GlideEngine.java)<br> 
[PicassoEngine](https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/PicassoEngine.java)<br>
[CoilEngine](https://github.com/LuckSiege/PictureSelector/blob/version_component/app/src/main/java/com/luck/pictureselector/CoilEngine.java)<br>


## 进阶使用
想要了解更多功能，请参阅[文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%8A%9F%E8%83%BDapi%E8%AF%B4%E6%98%8E)

简单用例如下所示:

1、获取图片

```sh
PictureSelector.create(this)
   .openGallery(SelectMimeType.ofImage())
   .setImageEngine(GlideEngine.createGlideEngine())
   .forResult(new OnResultCallbackListener<LocalMedia>() {
      @Override
      public void onResult(ArrayList<LocalMedia> result) {

      }

      @Override
      public void onCancel() {

      }
});
```

使用系统相册

```sh
PictureSelector.create(this)
     .openSystemGallery(SelectMimeType.ofImage())
     .forResult(new OnResultCallbackListener<LocalMedia>() {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {

        }

        @Override
        public void onCancel() {

        }
});
```

2、单独拍照

```sh
PictureSelector.create(this)
     .openCamera(SelectMimeType.ofImage())
     .forResult(new OnResultCallbackListener<LocalMedia>() {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {

        }

        @Override
        public void onCancel() {

        }
});
```

Navigation Fragment场景下，单独拍照请使用如下方式:

```sh
PictureSelector.create(this)
     .openCamera(SelectMimeType.ofImage())
     .forResultActivity(new OnResultCallbackListener<LocalMedia>() {
        @Override
        public void onResult(ArrayList<LocalMedia> result) {

        }

        @Override
        public void onCancel() {

        }
});
```


3、您还可以按如下示例使用：

(1)、注入到任意视图层

```sh

PictureSelector.create(this)
   .openGallery(SelectMimeType.ofAll())
   .setImageEngine(GlideEngine.createGlideEngine())
   .buildLaunch(R.id.fragment_container, new OnResultCallbackListener<LocalMedia>() {
      @Override
      public void onResult(ArrayList<LocalMedia> result) {
      
      }

      @Override
      public void onCancel() {
      
      }
});
			
```

(2)、自行注入到任意视图层

```sh

PictureSelectorFragment selectorFragment = PictureSelector.create(this)
     .openGallery(SelectMimeType.ofAll())
     .setImageEngine(GlideEngine.createGlideEngine())
     .build();
     
getSupportFragmentManager().beginTransaction()
     .add(R.id.fragment_container, selectorFragment, selectorFragment.getFragmentTag())
     .addToBackStack(selectorFragment.getFragmentTag())
     .commitAllowingStateLoss();
			
```

4、单独获取数据源

(1)、专辑列表
```sh

PictureSelector.create(this)
    .dataSource(SelectMimeType.ofAll())
    .obtainAlbumData(new OnQueryDataSourceListener<LocalMediaFolder>() {
        @Override
        public void onComplete(List<LocalMediaFolder> result) {

        }
   );

```

(2)、相册列表
```sh

PictureSelector.create(this)
    .dataSource(SelectMimeType.ofAll())
    .obtainMediaData(new OnQueryDataSourceListener<LocalMedia>() {
        @Override
        public void onComplete(List<LocalMedia> result) {

        }
   );

```

(3)、根据IBridgeMediaLoader获取指定数据
```sh

IBridgeMediaLoader loader = PictureSelector.create(this)
    .dataSource(SelectMimeType.ofImage()).buildMediaLoader();
    loader.loadAllAlbum(new OnQueryAllAlbumListener<LocalMediaFolder>() {
        @Override
        public void onComplete(List<LocalMediaFolder> result) {

        }
  });

```


5、预览图片、视频、音频

如果预览网络视频AndroidManifest.xml添加如下代码

```sh
android:usesCleartextTraffic="true"
```

```sh

PictureSelector.create(this)
    .openPreview()
    .setImageEngine(GlideEngine.createGlideEngine())
    .setExternalPreviewEventListener(new OnExternalPreviewEventListener() {
       @Override
       public void onPreviewDelete(int position) {

       }

        @Override
       public boolean onLongPressDownload(LocalMedia media) {
           return false;
       }
    }).startActivityPreview(position, true, list);

```


设置图片选择器主题，更多请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E4%B8%BB%E9%A2%98api%E8%AF%B4%E6%98%8E)

```sh
.setSelectorUIStyle();
```
或者您可以重载布局，更多请参阅[文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E9%87%8D%E8%BD%BD%E5%B8%83%E5%B1%80%EF%BC%9F)

```sh
.setInjectLayoutResourceListener(new OnInjectLayoutResourceListener() {
   @Override
   public int getLayoutResourceId(Context context, int resourceSource) {
	return 0;
   }
```

高级用例如下所示：

1、使用自定义相机功能，详情请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E8%87%AA%E5%AE%9A%E4%B9%89%E7%9B%B8%E6%9C%BA%EF%BC%9F)

```sh
.setCameraInterceptListener(new OnCameraInterceptListener() {
    @Override
    public void openCamera(Fragment fragment, int cameraMode, int requestCode){
	                                    
    }
});
```

2、使用图片压缩功能，详情请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E5%8E%8B%E7%BC%A9%EF%BC%9F)

```sh
.setCompressEngine(new CompressFileEngine() {
   @Override
   public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call){
                                    
   }
});
```

3、使用图片裁剪功能，详情请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E8%A3%81%E5%89%AA%EF%BC%9F)

```sh

.setCropEngine(new CropFileEngine() {
   @Override
   public void onStartCrop(Fragment fragment, Uri srcUri, Uri destinationUri, ArrayList<String> dataSource, int requestCode) {
                                    
   }
});
```

4、使用图片编辑功能，详情请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E7%BC%96%E8%BE%91%E5%9B%BE%E7%89%87%EF%BC%9F)

```sh
.setEditMediaInterceptListener(new OnMediaEditInterceptListener() {
    @Override
    public void onStartMediaEdit(Fragment fragment, LocalMedia currentLocalMedia, int requestCode) {
                                    
    }
});

```

5、加载自定义数据源，详情请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E5%8A%A0%E8%BD%BD%E8%87%AA%E5%AE%9A%E4%B9%89%E6%95%B0%E6%8D%AE%E6%BA%90%EF%BC%9F)

```sh
.setExtendLoaderEngine(new ExtendLoaderEngine() {
    @Override
    public void loadAllAlbumData(Context context, OnQueryAllAlbumListener<LocalMediaFolder> query) {
                                    
    }

    @Override
    public void loadOnlyInAppDirAllMediaData(Context context, OnQueryAlbumListener<LocalMediaFolder> query) {

    }

    @Override
    public void loadFirstPageMediaData(Context context, long bucketId, int page, int pageSize, OnQueryDataResultListener<LocalMedia> query) {

    }

    @Override
    public void loadMoreMediaData(Context context, long bucketId, int page, int limit, int pageSize, OnQueryDataResultListener<LocalMedia> query) {

    }
 });


```

6、自定义权限申请，详情请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E8%87%AA%E5%AE%9A%E4%B9%89%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7-%EF%BC%9F)

```sh
.setPermissionsInterceptListener(new OnPermissionsInterceptListener() {
      @Override
      public void requestPermission(Fragment fragment, String[] permissionArray, OnRequestPermissionListener call) {

      }

      @Override
      public boolean hasPermissions(Fragment fragment, String[] permissionArray) {
      	return false;
      }
});

```

7、Android 10 及以上版本，沙盒机制文件处理，详情请参阅 [文档](https://github.com/LuckSiege/PictureSelector/wiki/PictureSelector-3.0-%E5%A6%82%E4%BD%95%E8%AE%BF%E9%97%AE%E6%B2%99%E7%9B%92%E5%A4%96%E8%B5%84%E6%BA%90%EF%BC%9F)

```sh
.setSandboxFileEngine(new UriToFileTransformEngine() {
    @Override
    public void onUriToFileAsyncTransform(Context context, String srcPath, String mineType, OnKeyValueResultCallbackListener call) {
                                        
    }
});
```


## 混淆配置 
```sh
-keep class com.luck.picture.lib.** { *; }

// 如果引入了Camerax库请添加混淆
-keep class com.luck.lib.camerax.** { *; }
	
// 如果引入了Ucrop库请添加混淆
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
```
## License
```sh
Copyright 2016 Luck

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 联系方式
Android开发交流  [662320389]() <br>
Android开发交流  [619458861]() <br>
Android开发交流  [679824206]() <br>
Android开发交流  [854136996]() <br>
QQ [893855882]() <br>


## 兼容性测试
******腾讯优测-深度测试-通过率达到100%******

![image](https://github.com/LuckSiege/PictureSelector/blob/version_component/image/test.png)


## 演示效果

| 功能列表 |
|:-----------:|
|![](image/home.jpg)|

| 默认风格 | 预览 | 多图裁剪 |
|:-----------:|:--------:|:---------:|
|![](image/picture_default_style_1.jpg) | <img src="image/picture_default_style_2.jpg"/> | ![](image/picture_default_style_new_3.jpg)|

| 数字风格 | 预览 | 多图裁剪 |
|:-----------:|:--------:|:---------:|
|![](image/picture_num_style_new_1.jpg) | ![](image/picture_num_style_new_2.jpg) | ![](image/picture_num_style_new_3.jpg)|

| 白色风格 | 预览 | 单图裁剪 |
|:-----------:|:--------:|:---------:|
|![](image/picture_sina_style_1.jpg) | ![](image/picture_sina_style_new_2.jpg) | ![](image/picture_sina_style_new_3.jpg)|

| 全新风格 | 预览 | 多图裁剪 |
|:-----------:|:--------:|:---------:|
|![](image/picture_wechat_style_1.jpg) | ![](image/picture_wechat_style_2.jpg) | ![](image/picture_wechat_style_new_3.jpg)|

| 相册目录 | 单选模式 | 头像裁剪|
|:-----------:|:--------:|:--------:|
|![](image/picture_wechat_album_style.jpg) |![](image/picture_wechat_single_style_3.jpg) | ![](image/picture_circular_crop_new_style.jpg)|

| 白色风格 | 视频 | 音频 |
|:-----------:|:-----------:|:--------:|
|![](image/picture_white_style.jpeg) |![](image/picture_video.jpg) | ![](image/picture_audio.jpg)|

