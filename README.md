# PictureSelector  
   最近项目中用到多图选择上传的需求，考虑到android机型众多问题就自己花时间写了一个，测试了大概60款机型，出现过一些问题也都一一修复了，基本上稳定了特分享出来，界面UI也是商用级的开发者不用在做太多修改了，界面高度自定义，可以设置符合你项目主色调的风格，集成完成后就可以拿来用。
  
  项目会一直维护，发现问题欢迎提出会第一时间修复，QQ交流群 619458861，个人QQ 893855882@qq.com  希望用得着的朋友点个start。 
   
  另附我的博客地址：http://blog.csdn.net/luck_mw

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
  9.支持一些常用场景设置：如:是否裁剪、是否预览图片、是否显示相机等
  10.新增自定义主题设置
  11.新增图片勾选样式设置
  12.新增图片裁剪宽高设置
  13.新增图片压缩处理
  14.新增录视频最大时间设置
  15.新增视频清晰度设置
  16.新增QQ选择风格，带数字效果
  17.新增自定义 文字颜色 背景色让风格和项目更搭配
  18.新增多图裁剪功能
  19.新增LuBan多图压缩
  20.新增单独拍照功能
  21.新增压缩大小设置
    
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
    compile 'com.github.LuckSiege.PictureSelector:picture_library:v1.2.9'
}

```
项目根目录  

```
allprojects {
   repositories {
      jcenter()
      maven { url 'https://jitpack.io' }
   }
}

       


/**
  * 图片回调方法
 */

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

  
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/A574F86A9A9F42A77D03B0ACC9E761C9.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/ABE302D298BD56DEC871F4464E64646F.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/3483AB11C78AF4C6DCC408504768A138.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/66C119A6BD918EAF9418324836C34BA6.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/new_image.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/5F1513BFD9490AF153E3E30840964FB1.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/BA7C4A038613182020DA9CE0152DA5D4.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/0F918EB15954836F59A95A3F7E0D2012.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/2AEDE4E52CC095F5896E066C59DDDF85.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/36C818DEDF2A5AA745CD699FBBF67E7F.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/9B433C9C47C3FCA7BC42D6E3B6F27698.jpg)
