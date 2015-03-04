package com.inveno.piflow.tools.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;

import com.inveno.piflow.tools.commontools.Tools;

/**
 * 图片解析工具类
 * 
 * @author blueming.wu
 * 
 */
public class BitmapTools {

	/**
	 * 根据文件存储路径得到bitmap
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap getBitmap(String path) {
		return BitmapFactory.decodeFile(path);
	}

	/**
	 * 根据输入流得到bitmap
	 * 
	 * @param in
	 * @return
	 */
	public static Bitmap getBitmap(InputStream in) {
		return BitmapFactory.decodeStream(in);
	}

	/**
	 * 根据指定的宽高，保持纵横比，缩小读取到的图片信息
	 * 
	 * @param bytes
	 *            读取到的图片信息为字节数组
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(byte[] bytes, int width, int height) {
		Bitmap bitmap = null;
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		options.inJustDecodeBounds = false;
		int scaleX = options.outWidth / width;
		int scaleY = options.outHeight / height;
		int scale = scaleX > scaleY ? scaleX : scaleY;
		options.inSampleSize = scale;
		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		return bitmap;
	}

	/**
	 * 将字节数组转化成图片
	 * 
	 * @param bytes
	 * @return
	 */
	public static Bitmap getBitmap(byte[] bytes) {
		Bitmap bitmap = null;
		if (bytes.length > 0)
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

	/**
	 * 将图片转化成字节数组
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] BitmapToBytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 把bitmap存入指定的路径中
	 * 
	 * @param path
	 * @param bitmap
	 * @throws IOException
	 */
	public static void saveBitmap(String path, Bitmap bitmap)
			throws IOException {
		if (path != null && bitmap != null) {
			File file = new File(path);
			if (!file.exists()) {
				// 如果不存在，也有可能图片文件目录也不存在
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			OutputStream out = new FileOutputStream(file);
			String name = file.getName();
			String postfix = name.substring(name.lastIndexOf(".") + 1);
			if ("png".equals(postfix) || "+".equals(postfix))
				bitmap.compress(CompressFormat.PNG, 100, out);
			else
				bitmap.compress(CompressFormat.JPEG, 100, out);
		}
	}

	/**
	 * 把图片流转化成字节数组
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	private static Bitmap getWhiteLayout(int w, int h) {
		Bitmap bitLayout = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvasLayout = new Canvas(bitLayout);
		canvasLayout.drawColor(Color.WHITE);
		canvasLayout.save(Canvas.ALL_SAVE_FLAG);
		canvasLayout.restore();
		return bitLayout;
	}

	/**
	 * 在大图指定位置合成小图
	 * 
	 * @param bit_big
	 * @param bit_small
	 * @param pos_x
	 * @param pos_y
	 * @return
	 */
	public static Bitmap getNewBitmap(Bitmap bit_big, Bitmap bit_small,
			int pos_x, int pos_y) {
		Bitmap bit_new = null;
		bit_new = getWhiteLayout(bit_big.getWidth(), bit_big.getHeight());
		Canvas canvas = new Canvas(bit_new);
		Paint paint = new Paint();
		canvas.drawBitmap(bit_big, 0, 0, paint);
		canvas.drawBitmap(bit_small, pos_x, pos_y, paint);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return bit_new;
	}

	/**
	 * 根据输入流按纵横比缩放图片
	 * 
	 * @author hongchang.liu
	 * @date 2012-09-21
	 * @param in
	 * @param width
	 * @param height
	 * @return
	 */

	public static Bitmap getBitmapFromStream(InputStream in, int width,
			int height) {
		Bitmap bitmap = null;

		Tools.showLog("chl", "width:" + width);
		Tools.showLog("chl", "width:" + height);
		Options options = new Options();

		// 内部转换,将流变为字节数组
		byte[] bytes = BitmapTools.getByteFromByte(in);
		/**
		 * 意思就是说如果该值设为true那么将不返回实际的bitmap不给其分配内存空间而里面只包括一些解码边界信息即图片大小信息，
		 * 那么相应的方法也就出来了，通过设置inJustDecodeBounds为true，获取到outHeight(图片原始高度)和
		 * outWidth(图片的原始宽度)，
		 * 然后计算一个inSampleSize(缩放值)，然后就可以取图片了，这里要注意的是，inSampleSize 可能小于0，必须做判断。
		 * 
		 * */
		if (bytes != null) {
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
			options.inJustDecodeBounds = false;
			int scaleX = options.outWidth / width;
			int scaleY = options.outHeight / height;
			int scale = scaleX > scaleY ? scaleX : scaleY;
			// int scale=2;
			options.inSampleSize = scale;

			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
					options);
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return bitmap;

	}

	/**
	 * 图片缩放
	 * 
	 * @author mingsong.zhang
	 * @date 2012-10-19
	 * 
	 * @param bitmap
	 *            对象
	 * @param w
	 *            要缩放的宽度
	 * @param h
	 *            要缩放的高度
	 * @return newBmp 新 Bitmap对象
	 */
	public static Bitmap matrixBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}

	/**
	 * 图片压缩
	 * 
	 * @author mingsong.zhang
	 * @date 2012-10-19
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		if (bitmap != null) {

			Options options = new Options();

			byte[] bytes = BitmapToBytes(bitmap);

			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
			options.inJustDecodeBounds = false;
			int scaleX = options.outWidth / width;
			int scaleY = options.outHeight / height;
			int scale = scaleX > scaleY ? scaleX : scaleY;

			options.inSampleSize = scale;

			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
					options);
		}
		return bitmap;
	}

	/**
	 * @author hongchang.liu
	 * @date 2012-09-21
	 * @param is
	 *            图片的输入字节流
	 * 
	 *            android 开发中，经常会获取图片的操作，前段时间做联网的图片读取操作时，
	 *            偶然发现了个问题。经上网查阅资料，得知，这个android 的一个bug 。 在android 2.2 以下（包括2.2）
	 *            用 BitmapFactory.decodeStream() 这个方法， 会出现概率性的解析失败的异常。而在高版本中，eg
	 *            2.3 则不会出现这种异常。解决方法如下：getByte(InputStream is)
	 * 
	 * */
	private static byte[] getByteFromByte(InputStream is) {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		byte[] b = null;
		byte[] myByte = null;
		try {

			b = new byte[is.available()];
			int len = 0;
			while ((len = is.read(b, 0, b.length)) != -1) {

				arrayOutputStream.write(b, 0, len);
				arrayOutputStream.flush();

			}
			myByte = arrayOutputStream.toByteArray();
			arrayOutputStream.close();
			is.close();

		} catch (Exception e) {

			throw new RuntimeException();
		} finally {

			return myByte;

		}

	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @author hongchang.liu
	 * @date 2012-09-21
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		// 如果它被设置为true,那么产生的图将分配其像素,这样他们可以被净化,如果系统需要回收内存。
		opt.inPurgeable = true;
		// 与inPurgeable同时设置
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 从WEBVIEW缓存获取图片
	 * 
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Bitmap getPictureFromCache(File cacheDir)
			throws FileNotFoundException {
		Bitmap bitmap = null;
		File file = new File(cacheDir + "/webviewCacheChromium/f_000001");
		FileInputStream inStream = new FileInputStream(file);
		bitmap = BitmapFactory.decodeStream(inStream);
		return bitmap;
	}

	/**
	 * 获取可以使用的缓存目录
	 * 
	 * @param context
	 * @param uniqueName
	 *            目录名称
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取bitmap的字节大小
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int getBitmapSize(Bitmap bitmap) {
		// 每行字节大小乘以高度像素
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * 获取程序外部的缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getExternalCacheDir(Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/**
	 * 获取文件路径空间大小
	 * 
	 * @param path
	 * @return
	 */
	public static long getUsableSpace(File path) {

		long availableSize = 0;
		try {
			final StatFs stats = new StatFs(path.getPath());
			if (stats != null) {
				long blockSize = stats.getBlockSize();
				long alBlock = stats.getAvailableBlocks();
				availableSize = blockSize * alBlock;
			}
		} catch (Exception e) {
		}

		return availableSize;
	}

	/**
	 * @author blueming.wu
	 * 
	 *         取资源中的图片资源压缩成指定宽高
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeResource(res, resId, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * @author blueming.wu
	 * 
	 *         取文件路径下的图片压缩
	 * @param filename
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(filename, options);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	/**
	 * 图片压缩
	 * 
	 * @param fileDescriptor
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromDescriptor(
			FileDescriptor fileDescriptor, int reqWidth, int reqHeight)
			throws OutOfMemoryError {
		Bitmap bitmap = null;
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = true;
			options.inPurgeable = true;
			BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null,
					options);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			Tools.showLog("error", "处理图片内存溢出！！");
			return null;
		}

		return bitmap;
	}

	/**
	 * @author blueming.wu
	 * 
	 *         根据具体要压缩的宽高，算出压缩比率
	 * @param options
	 * @param reqWidth
	 *            需要压缩的宽
	 * @param reqHeight
	 *            需要压缩的高
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			// 如果原图宽大于高
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}

			final float totalPixels = width * height;

			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/**
	 * 图片按比例压缩
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compress(InputStream in, float ww, float hh) {

		Bitmap image = null;
		try {
			image = BitmapFactory.decodeStream(in);
		} catch (OutOfMemoryError e) {
			Tools.showLogA("图片流太大，解析报错，压缩后重新解析");
			BitmapFactory.Options opts = new Options();
			opts.inSampleSize = 3;
			image = BitmapFactory.decodeStream(in, null, opts);
		}

		if (image == null)
			return null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {
			Tools.showLogA("图片还是大于80，继续压缩");
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;// 每次都减少10
		}

		// ByteArrayInputStream isBm = new
		// ByteArrayInputStream(baos.toByteArray());
		// BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//
		// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		// newOpts.inJustDecodeBounds = true;
		// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		// newOpts.inJustDecodeBounds = false;
		// int w = newOpts.outWidth;
		// int h = newOpts.outHeight;
		//
		// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		// int be = 1;// be=1表示不缩放
		// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
		// be = (int) (newOpts.outWidth / ww);
		// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
		// be = (int) (newOpts.outHeight / hh);
		// }
		// if (be <= 0)
		// be = 1;
		// Tools.showLogA("压缩后的宽：" + w + " 压缩后的高：" + h + " 比率：" + be);
		// newOpts.inSampleSize = be;// 设置缩放比例
		// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		// isBm = new ByteArrayInputStream(baos.toByteArray());
		// bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

		return image;// 压缩好比例大小后再进行质量压缩
	}

	// /**
	// * 质量压缩方法
	// *
	// * @param image
	// * @return
	// */
	// private static Bitmap compressImage(Bitmap image) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//
	// // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	// image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	// int options = 100;
	// while (baos.toByteArray().length / 1024 > 50) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	// Tools.showLogA("图片还是大于50，继续压缩");
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
	// 这里压缩options%，把压缩后的数据存放到baos中
	// options -= 10;// 每次都减少10
	// }
	// ByteArrayInputStream isBm = new
	// ByteArrayInputStream(baos.toByteArray());//
	// 把压缩后的数据baos存放到ByteArrayInputStream中
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
	// 把ByteArrayInputStream数据生成图片
	// return bitmap;
	// }

	
	// 获取指定Activity的截屏原图
	public static Bitmap takeScreenShot(Activity activity){
		//View是你需要截图的View
		View view = activity.getWindow().getDecorView();
    	view.setDrawingCacheEnabled(true);
    	view.buildDrawingCache();
    	Bitmap b1 = view.getDrawingCache();
    	
    	//获取状态栏高度
    	Rect frame = new Rect();  
    	activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
    	int statusBarHeight = frame.top;  
    	System.out.println(statusBarHeight);
    	
    	//获取屏幕长和高
    	int dWidth = activity.getWindowManager().getDefaultDisplay().getWidth();  
        int dHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        Log.i("lhc", "状态栏高度："+statusBarHeight+"屏幕宽高："+dWidth+"x"+dHeight);
        //去掉标题栏
    	Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, dWidth, dHeight - statusBarHeight);

//    	
    	view.destroyDrawingCache();
    	return b;
	}
	
	public static void screenShotThumb(Bitmap b){
		
	}
	
	
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
	public static Bitmap extractThumbByte(final byte[] btyes, final int height, final int width, final boolean crop) {

		BitmapFactory.Options options = new BitmapFactory.Options();

		try {
			options.inJustDecodeBounds = true;
			Bitmap tmp = BitmapFactory.decodeByteArray(btyes, 0, btyes.length, options);
			if (tmp != null) {
				tmp.recycle();
				tmp = null;
			}

			Log.d("lhc", "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
			final double beY = options.outHeight * 1.0 / height;
			final double beX = options.outWidth * 1.0 / width;
			Log.d("lhc", "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
			options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
			if (options.inSampleSize <= 1) {
				options.inSampleSize = 1;
			}

			// NOTE: out of memory error
			while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
				options.inSampleSize++;
			}

			int newHeight = height;
			int newWidth = width;
			if (crop) {
				if (beY > beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			} else {
				if (beY < beX) {
					newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
				} else {
					newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
				}
			}

			options.inJustDecodeBounds = false;

			Log.i("lhc", "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
			Bitmap bm = BitmapFactory.decodeByteArray(btyes, 0, btyes.length, options);
			if (bm == null) {
				Log.e("lhc", "bitmap decode failed");
				return null;
			}

			Log.i("lhc", "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
			final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
			if (scale != null) {
				bm.recycle();
				bm = scale;
			}

			if (crop) {
				final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
				if (cropped == null) {
					return bm;
				}

				bm.recycle();
				bm = cropped;
				Log.i("lhc", "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
			}
			return bm;

		} catch (final OutOfMemoryError e) {
			Log.e("exception", "decode bitmap failed: " + e.getMessage());
			options = null;
		}

		return null;
	}

}
