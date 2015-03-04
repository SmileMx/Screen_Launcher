package com.inveno.piflow.tools.commontools;

import java.util.Random;

/**
 * 算法工具类
 * 
 * @author blueming.wu
 * 
 * @date 2013-7-4
 */
public class MathUtils {

	/**
	 * 无重复随机数组产生算法
	 * 
	 * @param m
	 *            数组容量
	 * @param n
	 *            数组取值范围为0-（n-1）之间
	 * @return
	 */
	public static int[] RandomNum(int m, int n) {
		int[] v = new int[m];
		boolean[] b = new boolean[n];
		Random r = new Random();
		do {
			int x = r.nextInt(n);
			if (!b[x]) {
				v[--m] = x;
				b[x] = true;
			}
		} while (m > 0);
		return v;
	}
}
