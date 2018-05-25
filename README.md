# PictureSelector 2.0 
   一款针对android平台下的图片选择器，支持从相册或拍照选择图片或视频、音频，支持动态权限获取、裁剪(单图or多图裁剪)、压缩、主题自定义配置等功能、适配android 6.0+系统的开源图片选择框架。<br>  
  
  <br>项目会一直维护(有bug修复完成，一般周末会更新(不好意思，最近比较忙有时间会解决~有问题先提issue))，有bug请描述清楚，并请Issues会第一时间修复，个人QQ 893855882@qq.com  希望用得着的朋友点个star。 <br>
 Android开发交流 群一 619458861）(已满) <br> 
 Android开发交流 群二 679824206 <br> 
   
  [我的博客地址](http://blog.csdn.net/luck_mw) 
  
[![](https://jitpack.io/v/LuckSiege/PictureSelector.svg)](https://jitpack.io/#LuckSiege/PictureSelector)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/LuckSiege)
[![CSDN](https://img.shields.io/twitter/url/http/blog.csdn.net/luck_mw.svg?style=social)](http://blog.csdn.net/luck_mw)
[![I](https://img.shields.io/github/issues/LuckSiege/PictureSelector.svg)](https://github.com/LuckSiege/PictureSelector/issues)
[![Star](https://img.shields.io/github/stars/LuckSiege/PictureSelector.svg)](https://github.com/LuckSiege/PictureSelector)

## 目录
-[功能特点](#功能特点)<br>
-[集成方式](#集成方式)<br>
-[常见错误](#常见错误)<br>
-[功能配置](#功能配置)<br>
-[缓存清除](#缓存清除)<br>
-[主题配置](#主题配置)<br>
-[常用功能](#常用功能)<br>
-[结果回调](#结果回调)<br>
-[更新日志](#更新日志)<br>
-[混淆配置](#混淆配置)<br>
-[兼容性测试](#兼容性测试)<br>
-[演示效果](#演示效果)<br>
-[打赏](#打赏)<br>

# 功能特点

* 1.适配android6.0+系统
* 2.解决部分机型裁剪闪退问题
* 3.解决图片过大oom闪退问题
* 4.动态获取系统权限，避免闪退
* 5.支持相片or视频的单选和多选
* 6.支持裁剪比例设置，如常用的 1:1、3：4、3:2、16:9 默认为图片大小
* 7.支持视频预览
* 8.支持gif图片
* 9.支持.webp格式图片 
* 10.支持一些常用场景设置：如:是否裁剪、是否预览图片、是否显示相机等
* 11.新增自定义主题设置
* 12.新增图片勾选样式设置
* 13.新增图片裁剪宽高设置
* 14.新增图片压缩处理
* 15.新增录视频最大时间设置
* 16.新增视频清晰度设置
* 17.新增QQ选择风格，带数字效果 
* 18.新增自定义 文字颜色 背景色让风格和项目更搭配
* 19.新增多图裁剪功能
* 20.新增LuBan多图压缩
* 21.新增单独拍照功能
* 22.新增压缩大小设置
* 23.新增Luban压缩档次设置
* 24.新增圆形头像裁剪
* 25.新增音频功能查询


重要的事情说三遍记得添加权限

```
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.CAMERA" />
    
```


## 集成方式

方式一 compile引入

```
dependencies {
    implementation 'com.github.LuckSiege.PictureSelector:picture_library:v2.2.3'
}

```

项目根目录build.gradle加入

```
allprojects {
   repositories {
      jcenter()
      maven { url 'https://jitpack.io' }
   }
}
```

方式二 maven引入

step 1.
```
<repositories>
       <repository>
       <id>jitpack.io</id>
	<url>https://jitpack.io</url>
       </repository>
 </repositories>
```
step 2.
```

<dependency>
      <groupId>com.github.LuckSiege.PictureSelector</groupId>
      <artifactId>picture_library</artifactId>
      <version>v2.2.3</version> 
</dependency>

```

## 常见错误
```
 重要：PictureSelector.create()；调用此方法时，在activity中传activity.this，在fragment中请传fragment.this,
 影响回调到哪个地方的onActivityResult()。
 
 问题一：
 rxjava冲突：在app build.gradle下添加
 packagingOptions {
   exclude 'META-INF/rxjava.properties'
 }  
 
 问题二：
 java.lang.NullPointerException: 
 Attempt to invoke virtual method 'android.content.res.XmlResourceParser 
 android.content.pm.ProviderInfo.loadXmlMetaData(android.content.pm.PackageManager, java.lang.String)'
 on a null object reference
 
 * 注意 从v2.1.3版本中，将不需要配制以下内容
 
 application下添加如下节点:
 
 <provider
      android:name="android.support.v4.content.FileProvider"
      android:authorities="${applicationId}.provider"
      android:exported="false"
      android:grantUriPermissions="true">
       <meta-data
         android:name="android.support.FILE_PROVIDER_PATHS"
         android:resource="@xml/file_paths" />
</provider>

注意：如已添加其他sdk或项目中已使用过provider节点，
[请参考我的博客](http://blog.csdn.net/luck_mw/article/details/54970105)的解决方案

问题三：
经测试在小米部分低端机中，Fragment调用PictureSelector 2.0 拍照有时内存不足会暂时回收activity,
导致其fragment会重新创建 建议在fragment所依赖的activity加上如下代码:
if (savedInstanceState == null) {
      // 添加显示第一个fragment
      	fragment = new PhotoFragment();
      		getSupportFragmentManager().beginTransaction().add(R.id.tab_content, fragment,
                    PictureConfig.FC_TAG).show(fragment)
                    .commit();
     } else { 
      	fragment = (PhotoFragment) getSupportFragmentManager()
          .findFragmentByTag(PictureConfig.FC_TAG);
}
这里就是如果是被回收时，则不重新创建 通过tag取出fragment的实例。

问题四：
glide冲突
由于PictureSelector 2.0引入的是最新的glide 4.5.0,所以将项目中老版本的glide删除,并且将报错代码换成如下写法：
RequestOptions options = new RequestOptions();
options.placeholder(R.drawable.image);
Glide.with(context).load(url).apply(options).into(imageView);

```

## 功能配置
```
// 进入相册 以下是例子：用不到的api可以不写
 PictureSelector.create(MainActivity.this)
 	.openGallery()//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
 	.theme()//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
 	.maxSelectNum()// 最大图片选择数量 int
 	.minSelectNum()// 最小选择数量 int
	.imageSpanCount(4)// 每行显示个数 int
 	.selectionMode()// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
 	.previewImage()// 是否可预览图片 true or false
 	.previewVideo()// 是否可预览视频 true or false
	.enablePreviewAudio() // 是否可播放音频 true or false
 	.isCamera()// 是否显示拍照按钮 true or false
	.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
	.isZoomAnim(true)// 图片列表点击 缩放效果 默认true
	.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
	.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
 	.enableCrop()// 是否裁剪 true or false
 	.compress()// 是否压缩 true or false
 	.glideOverride()// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
 	.withAspectRatio()// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
 	.hideBottomControls()// 是否显示uCrop工具栏，默认不显示 true or false
 	.isGif()// 是否显示gif图片 true or false
	.compressSavePath(getPath())//压缩图片保存地址
 	.freeStyleCropEnabled()// 裁剪框是否可拖拽 true or false
 	.circleDimmedLayer()// 是否圆形裁剪 true or false
 	.showCropFrame()// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
 	.showCropGrid()// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
 	.openClickSound()// 是否开启点击声音 true or false
 	.selectionMedia()// 是否传入已选图片 List<LocalMedia> list
 	.previewEggs()// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
 	.cropCompressQuality()// 裁剪压缩质量 默认90 int
 	.minimumCompressSize(100)// 小于100kb的图片不压缩 
 	.synOrAsy(true)//同步true或异步false 压缩 默认同步
 	.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效 int 
 	.rotateEnabled() // 裁剪是否可旋转图片 true or false
 	.scaleEnabled()// 裁剪是否可放大缩小图片 true or false
 	.videoQuality()// 视频录制质量 0 or 1 int
	.videoMaxSecond(15)// 显示多少秒以内的视频or音频也可适用 int 
        .videoMinSecond(10)// 显示多少秒以内的视频or音频也可适用 int 
	.recordVideoSecond()//视频秒数录制 默认60s int
	.isDragFrame(false)// 是否可拖动裁剪框(固定)
 	.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code     
```

## 缓存清除
```
 //包括裁剪和压缩后的缓存，要在上传成功后调用，注意：需要系统sd卡权限 
 PictureFileUtils.deleteCacheDirFile(MainActivity.this);
 
```
## 主题配置

```
<!--默认样式 注意* 样式只可修改，不能删除任何一项 否则报错-->
    <style name="picture.default.style" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <!--标题栏背景色-->
        <item name="colorPrimary">@color/bar_grey</item>
        <!--状态栏背景色-->
        <item name="colorPrimaryDark">@color/bar_grey</item>
        <!--是否改变图片列表界面状态栏字体颜色为黑色-->
        <item name="picture.statusFontColor">false</item>
        <!--返回键图标-->
        <item name="picture.leftBack.icon">@drawable/picture_back</item>
        <!--标题下拉箭头-->
        <item name="picture.arrow_down.icon">@drawable/arrow_down</item>
        <!--标题上拉箭头-->
        <item name="picture.arrow_up.icon">@drawable/arrow_up</item>
        <!--标题文字颜色-->
        <item name="picture.title.textColor">@color/white</item>
        <!--标题栏右边文字-->
        <item name="picture.right.textColor">@color/white</item>
        <!--图片列表勾选样式-->
        <item name="picture.checked.style">@drawable/checkbox_selector</item>
        <!--开启图片列表勾选数字模式-->
        <item name="picture.style.checkNumMode">false</item>
        <!--选择图片样式0/9-->
        <item name="picture.style.numComplete">false</item>
        <!--图片列表底部背景色-->
        <item name="picture.bottom.bg">@color/color_fa</item>
        <!--图片列表预览文字颜色-->
        <item name="picture.preview.textColor">@color/tab_color_true</item>
        <!--图片列表已完成文字颜色-->
        <item name="picture.complete.textColor">@color/tab_color_true</item>
        <!--图片已选数量圆点背景色-->
        <item name="picture.num.style">@drawable/num_oval</item>
        <!--预览界面标题文字颜色-->
        <item name="picture.ac_preview.title.textColor">@color/white</item>
        <!--预览界面已完成文字颜色-->
        <item name="picture.ac_preview.complete.textColor">@color/tab_color_true</item>
        <!--预览界面标题栏背景色-->
        <item name="picture.ac_preview.title.bg">@color/bar_grey</item>
        <!--预览界面底部背景色-->
        <item name="picture.ac_preview.bottom.bg">@color/bar_grey_90</item>
        <!--预览界面返回箭头-->
        <item name="picture.preview.leftBack.icon">@drawable/picture_back</item>
        <!--是否改变预览界面状态栏字体颜色为黑色-->
        <item name="picture.preview.statusFontColor">false</item>
        <!--裁剪页面标题背景色-->
        <item name="picture.crop.toolbar.bg">@color/bar_grey</item>
        <!--裁剪页面状态栏颜色-->
        <item name="picture.crop.status.color">@color/bar_grey</item>
        <!--裁剪页面标题文字颜色-->
        <item name="picture.crop.title.color">@color/white</item>
        <!--相册文件夹列表选中图标-->
        <item name="picture.folder_checked_dot">@drawable/orange_oval</item>
    </style>

```

## 常用功能

******启动相册并拍照******       
```
 PictureSelector.create(MainActivity.this)
       .openGallery(PictureMimeType.ofImage())
       .forResult(PictureConfig.CHOOSE_REQUEST);
       
```
******单独启动拍照或视频 根据PictureMimeType自动识别******       
```
  PictureSelector.create(MainActivity.this)
       .openCamera(PictureMimeType.ofImage())
       .forResult(PictureConfig.CHOOSE_REQUEST);
```
******预览图片******       
```
// 预览图片 可自定长按保存路径
*注意 .themeStyle(themeId)；不可少，否则闪退...

PictureSelector.create(MainActivity.this).themeStyle(themeId).openExternalPreview(position, "/custom_file", selectList);
PictureSelector.create(MainActivity.this).themeStyle(themeId).openExternalPreview(position, selectList);

```
******预览视频****** 
```
PictureSelector.create(MainActivity.this).externalPictureVideo(video_path);

```
## 结果回调
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    
```


## 更新日志

# 当前版本：
* v2.2.3
* 1.修复沉浸式在部分机型标题栏遮挡情况

# 历史版本：
* v2.2.2
* 1.优化外部预览界面样式不同步问题
* 2.优化沉浸式方案，适配更多机型
* 3.新增isDragFrame(false) API  是否可拖动裁剪框(固定)
* 4.修复录音会生成重复文件问题

* v2.2.0
* 1.修复单独拍照+裁剪图片不返回问题

* v2.1.9
* 1.修改单选策略，也支持预览模式
* 2.修复8.0部分手机闪退问题
* 3.修复图片到了最大可选数量，在拍照返回图片不出现问题
* 4.修改单选模式在拍照返回图片不存在问题
* 5.升级glide为最新版本4.5.0
* 6.修复parUri() sdk判断错误问题
* 7.修复预览图片变形问题
* 8.修复Toast内存泄漏问题
* 9.修复若干已知bug

* v2.1.7
* 1.修复.bmp格式图片，同步压缩会出现闪退问题
* 2.修复部分机型在使用裁剪或压缩路径时报FileNotfoundException异常问题

* v2.1.6
* 1.增加拍照自定义相片后缀名(.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg)
* 2.修复设置数字选择风格，不显示数量问题
* 3.修复预览界面文字设置无效问题
* 4.修复已知bug

* v2.1.5
* 简化压缩代码，只保留luban最新版本压缩
* 增加压缩自定义保存路径
* 增加过滤多少kb范围内的图片不压缩处理
* 修复压缩透明图片出现黑色背景问题
* 修复开启点击音效第一次不响bug

* v2.1.3
* 支持长图预览功能
* 修复部分图片或视频查询不出来bug
* 去除使用者主动添加适配android 6.0以上系统拍照适配配置
* 升级PhotoView版本为最新版本
* 解决部分图片预览时没有填充满屏幕问题
* 优化相册启动时间，去除一些耗时操作
* 优化代码结构
* 修复已知问题

* v2.1.1
* 升级glide 4.0为正式版
* 修复7.1.1系统PopupWindow弹出位置错误bug

* v2.1.0
* 修复裁剪速度慢的问题

* v2.0.9
* 修复直接播放视频闪退bug
* 升级glide为4.0.0 rc1
* 新增图片列表点击缩放效果api

* v2.0.7
* 修复已知bug

* v2.0.6
* 新增自定拍照保存路径
* 修复录音不显示时长问题

# 项目使用第三方库：
* glide:4.5.0	
* rxjava:2.0.5
* rxandroid:2.0.1
* PhotoView:2.1.3
* luban
* 裁剪使用ucrop

## 混淆配置 
```
#PictureSelector 2.0
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
   
 #rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

```
## 打赏
# ~如果您觉得好，对你有帮助，可以给我一点打赏当做鼓励，蚊子再小也是肉呀(*^__^*) 嘻嘻…… 
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/apply.png)

## 兼容性测试
******腾讯优测-深度测试-通过率达到100%******

![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/test.png)

## 演示效果

![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/1.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/2.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/3.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/4.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/white.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/blue.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/11.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/5.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/6.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/7.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/8.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/audio.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/9.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/10.jpg)


