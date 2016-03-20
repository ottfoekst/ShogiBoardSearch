package org.petanko.ottfoekst.boardsrch.util;

import java.util.List;

/**
 * �z��Ɋւ���֗��ȃ��\�b�h���W�߂��N���X�B
 * @author ottfoekst
 *
 */
public class ArrayUtils {

	/**
	 * Integer�̃��X�g��int�z��ɕϊ����ĕԂ��܂��B���̍ۍŌ����int�̍ő�l�������܂��B
	 * @param intList Integer�̃��X�g
	 * @return int�̔z��(�Ō����{@link Integer.MAX_VALUE}��}��)
	 */
	public static int[] createArrayWithIntMax(List<Integer> intList) {
		
		int[] intArray = new int[intList.size() + 1];
		for(int index = 0; index < intList.size(); index++) {
			intArray[index] = intList.get(index);
		}
		intArray[intList.size()] = Integer.MAX_VALUE; // �Ԑl
		
		return intArray;
	}
	
	/**
	 * Long�̃��X�g��long�z��ɕϊ����ĕԂ��܂��B���̍ۍŌ����long�̍ő�l�������܂��B
	 * @param longList Long�̃��X�g
	 * @return long�̔z��(�Ō����{@link Long.MAX_VALUE}��}��)
	 */
	public static long[] createArrayWithLongMax(List<Long> longList) {
		
		long[] longArray = new long[longList.size() + 1];
		for(int index = 0; index < longList.size(); index++) {
			longArray[index] = longList.get(index);
		}
		longArray[longList.size()] = Long.MAX_VALUE; // �Ԑl
		
		return longArray;
	}
}
