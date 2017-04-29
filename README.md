# PictureSelector  
   最近项目中用到多图选择上传的需求，考虑到android机型众多问题就自己花时间写了一个，测试了大概60款机型，出现过一些问题也都一一修复了，基本上稳定了特分享出来，界面UI也是商用级的开发者不用在做太多修改了，界面高度自定义，可以设置符合你项目主色调的风格，集成完成后就可以拿来用。
  
  项目会一直维护，发现问题欢迎提出会第一时间修复，QQ交流群 619458861，个人QQ 893855882@qq.com  希望用得着的朋友点个start。 
   
  [我的博客地址](http://blog.csdn.net/luck_mw)

[![](https://jitpack.io/v/LuckSiege/PictureSelector.svg)](https://jitpack.io/#LuckSiege/PictureSelector)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-brightgreen.svg)](https://github.com/LuckSiege)
[![CSDN](https://img.shields.io/twitter/url/http/blog.csdn.net/luck_mw.svg?style=social)](http://blog.csdn.net/luck_mw)
[![I](https://img.shields.io/github/issues/LuckSiege/PictureSelector.svg)](https://github.com/LuckSiege/PictureSelector/issues)
[![Star](https://img.shields.io/github/stars/LuckSiege/PictureSelector.svg)](https://github.com/LuckSiege/PictureSelector)


  
# 更新日志：
###### 版本 v1.3.8
###### 1.修复.webp格式图片压缩后后缀变为.jpg格式问题
###### 2.修复多图裁剪快速点击，结果返回为空问题
###### 3.修复快速点击启动相册重复问题
###### 4.将activity通信由广播改为EventBus3.0

# 项目使用第三方库：
###### 1.裁剪使用ucrop库
###### 2.eventbus:3.0.0'
###### 3.glide:3.7.0
###### 4.rxjava:2.0.5
###### 5.rxandroid:2.0.1
###### 6.okhttp:3.2.0
###### 7.PhotoView:1.2.4
###### 8.Luban

******功能特点：******  
```
  1.适配android6.0+系统
  2.解决部分机型裁剪闪退问题
  3.解决图片过大oom闪退问题
  4.动态获取系统权限，避免闪退
  5.支持相片or视频的单选和多选
  6.支持裁剪比例设置，如常用的 1:1、3：4、3:2、16:9 默认为图片大小
  7.支持视频预览
  8.支持gif图片
  9.支持.webp格式图片
  10.支持一些常用场景设置：如:是否裁剪、是否预览图片、是否显示相机等
  11.新增自定义主题设置
  12.新增图片勾选样式设置
  13.新增图片裁剪宽高设置
  14.新增图片压缩处理
  15.新增录视频最大时间设置
  16.新增视频清晰度设置
  17.新增QQ选择风格，带数字效果
  18.新增自定义 文字颜色 背景色让风格和项目更搭配
  19.新增多图裁剪功能
  20.新增LuBan多图压缩
  21.新增单独拍照功能
  22.新增压缩大小设置
  23.新增Luban压缩档次设置
    
```

******那些遇到拍照闪退问题的同学，请记得看清下面适配6.0的配置~******

重要的事情说三遍记得添加权限

```
  <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <uses-permission android:name="android.permission.CAMERA" />
    
```

******注：适配android6.0以上拍照问题，请在AndroidManifest.xml中添加标签******

```
<provider
   android:name="android.support.v4.content.FileProvider"
   android:authorities="${applicationId}.provider"
   android:exported="false"
   android:grantUriPermissions="true">
     <meta-data
         android:name="android.support.FILE_PROVIDER_PATHS"
         android:resource="@xml/file_paths" />
</provider>

```

******集成步骤******

compile引入

```
dependencies {
    compile 'com.github.LuckSiege.PictureSelector:picture_library:v1.3.8'
}

```

maven引入 
```
<dependency>
 <groupId>com.github.LuckSiege.PictureSelector</groupId>
 <artifactId>picture_library</artifactId>
 <version>v1.3.8</version>
</dependency>

```
项目根目录  

```
allprojects {
   repositories {
      jcenter()
      maven { url 'https://jitpack.io' }
   }
}
```

******常见错误*******
```
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
PhotoView 库冲突，可以删除自己项目中引用的，Picture_library中已经引用过，或引用com.commit451:PhotoView:1.2.4版本
 
```

******相册启动构造方法******
```
FunctionOptions options = new FunctionOptions.Builder()
        .setType(selectType) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
        .setCropMode(copyMode) // 裁剪模式 默认、1:1、3:4、3:2、16:9
        .setCompress(isCompress) //是否压缩
        .setEnablePixelCompress(true) //是否启用像素压缩
        .setEnableQualityCompress(true) //是否启质量压缩
        .setMaxSelectNum(maxSelectNum) // 可选择图片的数量
        .setSelectMode(selectMode) // 单选 or 多选
        .setShowCamera(isShow) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
        .setEnablePreview(enablePreview) // 是否打开预览选项
        .setEnableCrop(enableCrop) // 是否打开剪切选项
        .setPreviewVideo(isPreviewVideo) // 是否预览视频(播放) mode or 多选有效
        .setCheckedBoxDrawable(checkedBoxDrawable)
        .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
        .setRecordVideoSecond(60) // 视频秒数
        .setGif(false)// 是否显示gif图片，默认不显示
        .setCropW(cropW) // cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
        .setCropH(cropH) // cropH-->裁剪高度 值不能小于100 如果值大于图片原始宽高 将返回原图大小
        .setMaxB(maxB) // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb左右
        .setPreviewColor(previewColor) //预览字体颜色
        .setCompleteColor(completeColor) //已完成字体颜色
        .setPreviewBottomBgColor(previewBottomBgColor) //预览底部背景色
        .setBottomBgColor(bottomBgColor) //图片列表底部背景色
        .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
        .setCheckNumMode(isCheckNumMode)
        .setCompressQuality(100) // 图片裁剪质量,默认无损
        .setImageSpanCount(4) // 每行个数
        .setSelectMedia(selectMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
        .setCompressFlag(compressFlag) // 1 系统自带压缩 2 luban压缩
        .setCompressW(compressW) // 压缩宽 如果值大于图片原始宽高无效
        .setCompressH(compressH) // 压缩高 如果值大于图片原始宽高无效
        .setThemeStyle(themeStyle) // 设置主题样式
        .create();     
```

******启动相册并拍照******       
```
 PictureConfig.getPictureConfig().init(options).openPhoto(mContext, resultCallback);
 
 或默认配置
 PictureConfig.getPictureConfig().openPhoto(mContext, resultCallback);
```

******单独启动拍照或视频 根据type自动识别******       
```
 PictureConfig.getPictureConfig().init(options).startOpenCamera(mContext, resultCallback);
 
 或默认配置
 PictureConfig.getPictureConfig().startOpenCamera(mContext, resultCallback);
```
******预览图片******       
```
 PictureConfig.getPictureConfig().externalPicturePreview(mContext, position, selectMedia);
```
******预览视频****** 
```
PictureConfig.getPictureConfig().externalPictureVideo(mContext, selectMedia.get(position).getPath());
```
******图片回调完成结果返回******
```
  private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
            Log.i("callBack_result", selectMedia.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                String path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                String path = media.getCompressPath();
            } else {
                // 原图地址
                String path = media.getPath();
            }
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
            }
        }
    };
    
```

  
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/1.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/2.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/3.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/4.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/5.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/6.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/0F918EB15954836F59A95A3F7E0D2012.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/36C818DEDF2A5AA745CD699FBBF67E7F.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/9B433C9C47C3FCA7BC42D6E3B6F27698.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/BA7C4A038613182020DA9CE0152DA5D4.jpg)

