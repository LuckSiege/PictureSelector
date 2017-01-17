

<provider android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                  android:name="android.support.FILE_PROVIDER_PATHS"
                        android:resource="@xml/file_paths" /> </provider>



项目根目录
allprojects {

    repositories {
    
    
        maven 
        
        { 
        
        url 
        
        'https://jitpack.io' 
        
        }
        
        jcenter()
        
    }
}


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

                    
// 先初始化参数配置，在启动相册

 
PictureConfig.init(config);

// 设置回调函数

PictureConfig.openPhoto(MainActivity.this, resultCallback);

 /**
  * 图片回调方法
 */

private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
    @Override
    public void onSelectSuccess(List<LocalMedia> resultList) {
    
    
            if (media.isCompressed()){
            
                    
            // 注意：如果压缩过，在上传的时候，取 media.getCompressPath(); // 压缩图compressPath
            
                        
            } else {
            
                    
            // 注意：没有压缩过，在上传的时候，取 media.getPath(); // 原图path
            
       
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
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/5F1513BFD9490AF153E3E30840964FB1.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/BA7C4A038613182020DA9CE0152DA5D4.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/0F918EB15954836F59A95A3F7E0D2012.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/2AEDE4E52CC095F5896E066C59DDDF85.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/36C818DEDF2A5AA745CD699FBBF67E7F.jpg)
![image](https://github.com/LuckSiege/PictureSelector/blob/master/image/9B433C9C47C3FCA7BC42D6E3B6F27698.jpg)
