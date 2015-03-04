package com.inveno.piflow.biz;

public abstract class BaseBiz {
	
	/**
	 * 单子类为单例时，退出主界面调用此方法主动释放对象中的资源
	 */
	public abstract void release();
}
