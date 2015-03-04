package com.inveno.piflow.tools.bitmap;

import java.io.OutputStream;

import android.graphics.Bitmap;

/**
 * 使用bitmap框架需传入此接口内的实现内，具体实现的下载方法由使用者定义
 * 
 * @author blueming.wu
 * 
 * @date 2013-3-27
 */
public interface IDownloader {

	/**
	 * 请求网络的inputStream写入outputStream
	 * 
	 * @param urlString
	 * @param outputStream
	 * @return
	 */
	public boolean downloadToLocalStreamByUrl(String urlString,
			OutputStream outputStream);

	public Bitmap downloadAndCompressBitmap(String urlString, float w, float h);

	/**
	 * 此方法需实现释放当前正在进行的网络连接
	 * 
	 * @return
	 */
	public void disConnection();
}
