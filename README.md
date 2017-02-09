# PictureSelector  
最近项目中用到多图选择上传的需求，考虑到android机型众多问题就自己花时间写了一个，测试了大概60款机型，出现过一些问题也都一一修复了，基本上稳定了特分享出来，界面UI也是商用级的开发者不用在做太多修改了，界面高度自定义，可以设置符合你项目主色调的风格，集成完成后就可以拿来用。
顺便感谢一下，大家对我的支持~  

功能特点：  

1.适配android7.0系统    

2.解决部分机型裁剪闪退问题

3.解决图片过大oom闪退问题

4.动态获取系统权限，避免闪退  

5.支持相片or视频的单选和多选  

6.支持裁剪比例设置，如常用的  1:1、3：4、3:2、16:9 默认为图片大小

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

项目会一直维护，发现问题欢迎提出~  会第一时间修复哟~   联系方式893855882@qq.com  希望用得着的朋友点个start，你们的支持才是我继续下去的动力，在此先谢过~  

另附我的博客地址：http://blog.csdn.net/luck_mw

app-build 引入compile 'com.github.LuckSiege.PictureSelector:picture_library:v1.2.4'  注：之前引入如有报错，请引入最新版本、

项目根目录  

allprojects { 

    repositories { 
    
        jcenter() 
        
        maven { url 'https://jitpack.io' } 
        
    }  
    
}  


注：适配android7.0拍照问题，请在AndroidManifest.xml中添加标签  

 < provider 
 
            android:name="android.support.v4.content.FileProvider"
            
            android:authorities="${applicationId}.provider"
            
            android:exported="false"
            
            android:grantUriPermissions="true">
            
            < meta-data
            
                android:name="android.support.FILE_PROVIDER_PATHS"
                
                android:resource="@xml/file_paths" />
                
 </ provider> 
 
         
******相册参数构造******

FunctionConfig config = new FunctionConfig();  

config.setType(selectType); 1图片 or 2视频 LocalMediaLoader.TYPE_IMAGE,TYPE_VIDEO  

config.setCopyMode(copyMode); 裁剪比例 默认 1:1 3:4 3:2 16:9 可参考 Constants.COPY_MODEL_1_1  

config.setCompress(isCompress); 是否压缩  

config.setMaxSelectNum(maxSelectNum - images.size()); 最大可选数量  

config.setSelectMode(selectMode); 2单选 or 1多选 MODE_MULTIPLE MODE_SINGLE  

config.setShowCamera(isShow); 是否显示相机  

config.setEnablePreview(enablePreview); 是否预览  

config.setEnableCrop(enableCrop); 是否裁剪  

config.setPreviewVideo(isPreviewVideo); 是否预览视频(播放)  

config.setCropW(cropW); 裁剪宽  

config.setCropH(cropH); 裁剪高  

config.setRecordVideoDefinition(Constants.HIGH); // 视频清晰度 Constants.HIGH 清晰 Constants.ORDINARY 普通 低质量  

config.setRecordVideoSecond(60);// 视频秒数  

config.setCheckNumMode(isCheckNumMode); 是否显示QQ选择风格(带数字效果)  

config.setPreviewColor 预览文字颜色  

config.setCompleteColor 完成文字颜色 

config.setPreviewBottomBgColor 预览界面底部背景色  

config.setBottomBgColor 选择图片页面底部背景色  

config.options.setSelectMedia() 已选图片集合  

config.setCompressFlag(compressFlag); // 1是系统自带压缩 2是鲁班压缩

config.setCompressW(compressW); //Luban压缩宽

config.setCompressH(compressH); //Luban压缩高

// 先初始化参数配置，在启动相册  

PictureConfig.init(config);  

// 启动相册并设置回调函数  

PictureConfig.getPictureConfig.openPhoto(MainActivity.this, resultCallback); 

// 外部预览图片方法 (例如选完后要预览的可调用此方法)

PictureConfig.getPictureConfig.externalPicturePreview(this, position, selectMedia);

/**
  * 图片回调方法
 */

private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {  

    @Override
    
    public void onSelectSuccess(List< LocalMedia> resultList) {  
            if (media.isCompressed()){  
            // 注意：如果压缩过，在上传的时候，取 media.getCompressPath(); // 压缩图compressPath  
            } else {  
            // 注意：没有压缩过，在上传的时候，取 media.getPath(); // 原图path  
            } else{
            
            // 注意：如果media.getCatPath();不为空的话 就代表裁剪的图片，上传时可取，但是如果又压缩过，则取最终压缩过的compressPath  
            
            }
            
            selectMedia = resultList;  
            
            if (selectMedia != null) {  
            
                adapter.setList(selectMedia);  
                
                adapter.notifyDataSetChanged();  
                
            }  
        }  
  };  
  
  
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
