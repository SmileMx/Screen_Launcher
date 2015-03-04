package com.inveno.piflow.entity.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.inveno.piflow.R;
import com.inveno.piflow.activity.PhotoShowActivity;
import com.inveno.piflow.activity.PhotoShowActivity.OnPageChange;
import com.inveno.piflow.entity.view.news.NewsDetailImageView;
import com.inveno.piflow.tools.bitmap.BitmapDisplayConfig;
import com.inveno.piflow.tools.bitmap.FlyshareBitmapManager;
import com.inveno.piflow.tools.bitmap.IBitmapDisplay;
import com.inveno.piflow.tools.commontools.DeviceConfig;
import com.inveno.piflow.tools.commontools.Tools;

/**
 * 看大图Fragment,具缩放功能
 * 
 * @author hongchang.liu
 * 
 */
public class PhotoShowFragment extends BaseFragment implements OnPageChange {
	private static final String IMAGE_DATA_EXTRA = "extra_image_data";
	private static final String IMAGE_MODE = "image_mode";
	private static final String IMAGE_FROM = "image_from";

	private static final String FRAGMENT_ID = "fragment_id";
	private static final String IMAGE_ID = "image_id";
	private static final String TYPECODE = "typeCode";

	private static final String IMG_WIDTH = "IMG_WIDTH";
	private static final String IMG_HEIGHT = "IMG_HEIGHT";

	private String mImageUrl;
	private int dayOrNight;
	private NewsDetailImageView mImageView;
	private FlyshareBitmapManager bitmapManager;
	private ViewPager mViewPager;

	private int fragmentId;
	private int from;

	private ImageView bg;

	private int WIDTH = 1900;
	private int HEIGHT = 1000;

	private int imgWidth;
	private int imgHeight;

	/**
	 * Factory method to generate a new instance of the fragment given an image
	 * number.
	 * 
	 * @param imageUrl
	 *            The image url to load
	 * @return A new instance of PhotoShowFragment with imageNum extras
	 */
	public static PhotoShowFragment newInstance(String imageUrl, int mode,
			int fromWhere, int imgWidth, int imgHeight) {
		final PhotoShowFragment f = new PhotoShowFragment();

		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, imageUrl);
		args.putInt(IMAGE_MODE, mode);
		args.putInt(IMAGE_FROM, fromWhere);
		args.putInt(IMG_WIDTH, imgWidth);
		args.putInt(IMG_HEIGHT, imgHeight);
		f.setArguments(args);

		return f;
	}

	public static PhotoShowFragment newInstance(String imageUrl, int mode,
			int fromWhere, int meituId, int fragmentId, int typeCode) {
		final PhotoShowFragment f = new PhotoShowFragment();

		final Bundle args = new Bundle();
		args.putString(IMAGE_DATA_EXTRA, imageUrl);
		args.putInt(IMAGE_MODE, mode);
		args.putInt(IMAGE_FROM, fromWhere);
		args.putInt(FRAGMENT_ID, fragmentId);
		args.putInt(IMAGE_ID, meituId);
		args.putInt(TYPECODE, typeCode);
		f.setArguments(args);

		return f;
	}

	/**
	 * Empty constructor as per the Fragment documentation
	 */
	public PhotoShowFragment() {
	}

	/**
	 * Populate image using a url from extras, use the convenience factory
	 * method {@link PhotoShowFragment#newInstance(String)} to create this
	 * fragment.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString(
				IMAGE_DATA_EXTRA) : null;
		dayOrNight = getArguments() != null ? getArguments().getInt(IMAGE_MODE)
				: 1;
		fragmentId = getArguments() != null ? getArguments()
				.getInt(FRAGMENT_ID) : 0;
		from = getArguments() != null ? getArguments().getInt(IMAGE_FROM) : -2;

		imgWidth = getArguments() != null ? getArguments().getInt(IMG_WIDTH)
				: 1;

		imgHeight = getArguments() != null ? getArguments().getInt(IMG_HEIGHT)
				: 1;

		// hasLoad=false;
		// 设置滑动监听
		((PhotoShowActivity) getActivity()).setOnPageChange(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate and locate the main ImageView
		final View v = inflater.inflate(R.layout.photo_show, container, false);
		mImageView = (NewsDetailImageView) v.findViewById(R.id.newsImageView);
		bg = (ImageView) v.findViewById(R.id.bg);
		// if (dayOrNight == 1)
		// v.setBackgroundColor(Color.WHITE);
		// else
		// v.setBackgroundColor(Color.BLACK);

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Use the parent activity to load the image asynchronously into the
		// ImageView (so a single
		// cache can be used over all pages in the ViewPager
		if (PhotoShowActivity.class.isInstance(getActivity())) {
			bitmapManager = ((PhotoShowActivity) getActivity())
					.getBitmapManager();
			bitmapManager.configIBitmapDisplay(new IBitmapDisplay() {

				@Override
				public void loadFailDisplay(ImageView imageView, int bitmapRes) {
					imageView.setScaleType(ScaleType.CENTER);
					imageView.setImageResource(bitmapRes);
					imageView.setTag(R.string.load_bitmap_key,
							FlyshareBitmapManager.LOAD_BMP_FAIL);
				}

				@Override
				public void loadCompletedisplay(ImageView imageView,
						Bitmap bitmap, BitmapDisplayConfig config) {
					imageView.setScaleType(ScaleType.CENTER_CROP);
					imageView.setImageDrawable(null);
					imageView.setImageBitmap(bitmap);
					imageView.setTag(R.string.load_bitmap_key,
							FlyshareBitmapManager.LOAD_BMP_OK);
				}
			});

			mViewPager = ((PhotoShowActivity) getActivity()).getmViewPager();
			mImageView.setParentView(mViewPager);

			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) bg
					.getLayoutParams();

			if (imgWidth * 1.0f / imgHeight >= WIDTH * 1.0f / HEIGHT) {

				lp.width = WIDTH;
				lp.height = (int) (WIDTH * imgHeight * 1.0f / imgWidth);

			} else {
				lp.height = HEIGHT;
				lp.width = (int) (HEIGHT * imgWidth * 1.0f / imgHeight);

			}

			Tools.showLog("datu", "imgWidth : " + imgWidth + " imgHeight:"
					+ imgHeight + " imgWidth / imgHeight " + imgWidth
					/ imgHeight + "  WIDTH / HEIGHT:" + WIDTH / HEIGHT);

			bg.setLayoutParams(lp);

			mImageView.setBg(bg);

			Tools.showLog("meitu", "看大图 url：" + mImageUrl);
			DeviceConfig deviceConfig = DeviceConfig.getInstance(getActivity());

			int width = deviceConfig.w;
			if (width > 540)
				width = 540;
			bitmapManager.displayForFlow(mImageView, mImageUrl,
					R.drawable.waterwall_list_default,
					R.drawable.waterwall_list_default, deviceConfig.w,
					deviceConfig.h, 150, false);

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mImageView != null) {
			FlyshareBitmapManager.cancelWork(mImageView);
			mImageView.setImageDrawable(null);
		}

	}

	@Override
	public void onPageChangeListener(int page) {
		// TODO Auto-generated method stub

	}

}
