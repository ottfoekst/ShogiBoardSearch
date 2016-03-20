package org.petanko.ottfoekst.boardsrch.util;

import java.util.List;

/**
 * 配列に関する便利なメソッドを集めたクラス。
 * @author ottfoekst
 *
 */
public class ArrayUtils {

	/**
	 * Integerのリストをint配列に変換して返します。その際最後尾にintの最大値を加えます。
	 * @param intList Integerのリスト
	 * @return intの配列(最後尾に{@link Integer.MAX_VALUE}を挿入)
	 */
	public static int[] createArrayWithIntMax(List<Integer> intList) {
		
		int[] intArray = new int[intList.size() + 1];
		for(int index = 0; index < intList.size(); index++) {
			intArray[index] = intList.get(index);
		}
		intArray[intList.size()] = Integer.MAX_VALUE; // 番人
		
		return intArray;
	}
	
	/**
	 * Longのリストをlong配列に変換して返します。その際最後尾にlongの最大値を加えます。
	 * @param longList Longのリスト
	 * @return longの配列(最後尾に{@link Long.MAX_VALUE}を挿入)
	 */
	public static long[] createArrayWithLongMax(List<Long> longList) {
		
		long[] longArray = new long[longList.size() + 1];
		for(int index = 0; index < longList.size(); index++) {
			longArray[index] = longList.get(index);
		}
		longArray[longList.size()] = Long.MAX_VALUE; // 番人
		
		return longArray;
	}
}
