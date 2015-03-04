package com.inveno.piflow.tools.bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import android.graphics.Bitmap;

import com.inveno.piflow.download.HttpDownload;
import com.inveno.piflow.tools.android.BitmapTools;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 具体实现下载图片的http协议的实现类
 * 
 * @author blueming.wu
 * 
 */
public class HttpDownloadImpl implements IDownloader {

	private static final int IO_BUFFER_SIZE = 8 * 1024; // 8k
	HttpURLConnection urlConnection = null;

	@Override
	public boolean downloadToLocalStreamByUrl(String urlString,
			OutputStream outputStream) {
		HttpDownload.disableConnectionReuseIfNecessary();
		BufferedOutputStream out = null;
		FlushedInputStream in = null;

		try {
			urlConnection = HttpDownload.downLoadData(urlString);
			in = new FlushedInputStream(new BufferedInputStream(
					urlConnection.getInputStream(), IO_BUFFER_SIZE));
			out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			return true;

		} catch (final IOException e) {
			Tools.showLogA("Error in downloadBitmap - " + urlString + " : " + e);
		} finally {
			try {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}

				if (out != null) {
					out.flush();
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {

			}
		}
		return false;
	}

	public class FlushedInputStream extends FilterInputStream {

		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int by_te = read();
					if (by_te < 0) {
						break; // we reached EOF
					} else {
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	@Override
	public void disConnection() {
		if (this.urlConnection != null)
			urlConnection.disconnect();
	}

	@Override
	public Bitmap downloadAndCompressBitmap(String urlString, float w, float h) {
		FlushedInputStream in = null;
		Bitmap bitmap = null;

		try {
			HttpURLConnection urlConnection = HttpDownload
					.downLoadData(urlString);
			in = new FlushedInputStream(new BufferedInputStream(
					urlConnection.getInputStream(), IO_BUFFER_SIZE));
			bitmap = BitmapTools.compress(in, w, h);
		} catch (IOException e) {
			Tools.showLogA("下载图片不保存IO异常");
			return null;
		} finally {
			try {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}

				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				return null;
			}
		}

		return bitmap;
	}

}
