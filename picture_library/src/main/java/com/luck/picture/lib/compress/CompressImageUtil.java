package com.luck.picture.lib.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.yalantis.ucrop.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * author：luck
 * project：PictureSelector
 * email：893855882@qq.com
 * data：16/12/31
 */

public class CompressImageUtil{
	private CompressConfig config;
	private Context context;
	Handler mhHandler = new Handler();
	public CompressImageUtil(Context context,CompressConfig config) {
		this.context=context;
		this.config=config==null?CompressConfig.ofDefaultConfig():config;
	}
	public void compress(String imagePath, CompressListener listener) {
		if (config.isEnablePixelCompress()){
			try {
				compressImageByPixel(imagePath,listener);
			} catch (FileNotFoundException e) {
				listener.onCompressFailed(imagePath,String.format("图片压缩失败,%s",e.toString()));
				e.printStackTrace();
			}
		}else {
			compressImageByQuality(BitmapFactory.decodeFile(imagePath),imagePath,listener);
		}
	}
	/**
	 * 多线程压缩图片的质量
	 * @author JPH
	 * @param bitmap 内存中的图片
	 * @param imgPath 图片的保存路径
	 * @date 2014-12-5下午11:30:43
	 */
	private void compressImageByQuality(final Bitmap bitmap, final String imgPath, final CompressListener listener){
		if(bitmap==null){
			sendMsg(false,imgPath,"像素压缩失败,bitmap is null",listener);
			return;
		}
		new Thread(new Runnable() {//开启多线程进行压缩处理
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int options = 100;
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
				while (baos.toByteArray().length >config.getMaxSize()) {//循环判断如果压缩后图片是否大于指定大小,大于继续压缩
					baos.reset();//重置baos即让下一次的写入覆盖之前的内容 
					options -= 5;//图片质量每次减少5
					if(options<=5)options=5;//如果图片质量小于5，为保证压缩后的图片质量，图片最底压缩质量为5
					bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
					if(options==5)break;//如果图片的质量已降到最低则，不再进行压缩
				}
//				if(bitmap!=null&&!bitmap.isRecycled()){
//					bitmap.recycle();//回收内存中的图片
//				}
				try {
					File thumbnailFile=getThumbnailFile(new File(imgPath));
					FileOutputStream fos = new FileOutputStream(thumbnailFile);//将压缩后的图片保存的本地上指定路径中
					fos.write(baos.toByteArray());
					fos.flush();
					fos.close();
					sendMsg(true, thumbnailFile.getPath(),null,listener);
				} catch (Exception e) {
					sendMsg(false,imgPath,"质量压缩失败",listener);
					e.printStackTrace();
				}
			}
		}).start();
	}
	/**
	 * 按比例缩小图片的像素以达到压缩的目的
	 * @author JPH
	 * @param imgPath
	 * @return 
	 * @date 2014-12-5下午11:30:59
	 */
	private void compressImageByPixel(String imgPath,CompressListener listener) throws FileNotFoundException {
		if(imgPath==null){
			sendMsg(false,imgPath,"要压缩的文件不存在",listener);
			return;
		}
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;//只读边,不读内容
		BitmapFactory.decodeFile(imgPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int width = newOpts.outWidth;
		int height = newOpts.outHeight;
		float maxSize =config.getMaxPixel();
		int be = 1;
		if (width >= height && width > maxSize) {//缩放比,用高或者宽其中较大的一个数据进行计算
			be = (int) (newOpts.outWidth / maxSize);
			be++;
		} else if (width < height && height > maxSize) {
			be = (int) (newOpts.outHeight / maxSize);
			be++;
		}
		newOpts.inSampleSize =be;//设置采样率
		newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
		if (config.isEnableQualityCompress()){
			compressImageByQuality(bitmap,imgPath,listener);//压缩好比例大小后再进行质量压缩
		}else {
			File thumbnailFile=getThumbnailFile(new File(imgPath));
			bitmap.compress(Bitmap.CompressFormat.JPEG,100,new FileOutputStream(thumbnailFile));

			listener.onCompressSuccess(thumbnailFile.getPath());
		}
	}
	/**
	 * 发送压缩结果的消息
	 * @param isSuccess 压缩是否成功
	 * @param imagePath
	 * @param message
	 */
	private void sendMsg(final boolean isSuccess, final String imagePath,final String message, final CompressListener listener){
		mhHandler.post(new Runnable() {
			@Override
			public void run() {
				if (isSuccess){
					listener.onCompressSuccess(imagePath);
				}else{
					listener.onCompressFailed(imagePath,message);
				}
			}
		});
	}
	private File getThumbnailFile(File file){
		if (file==null||!file.exists())return file;
		return FileUtils.getPhotoCacheDir(context,file);
	}
	/**
	 * 压缩结果监听器
	 */
	public interface CompressListener {
		/**
		 * 压缩成功
		 *
		 * @param imgPath 压缩图片的路径
		 */
		void onCompressSuccess(String imgPath);

		/**
		 * 压缩失败
		 * @param imgPath 压缩失败的图片
		 * @param msg 失败的原因
		 */
		void onCompressFailed(String imgPath, String msg);
	}
}
