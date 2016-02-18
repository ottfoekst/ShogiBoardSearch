package org.petanko.ottfoekst.boardsrch.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * File I/O�ŕ֗��ȃ��\�b�h���W�߂��N���X�B
 * @author ottfoekst
 *
 */
public class IoUtils {
	
	/**
	 * �w�肵���t�@�C���p�X�ɏ������݉\��DataOutputStream��Ԃ��܂��B
	 * @param filePath �t�@�C���p�X
	 * @return filePath�ɏ������݉\��DataOutputStream
	 * @throws Exception
	 */
	public static DataOutputStream newDataOutputStream(Path filePath) throws Exception {
		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filePath.toFile())));
	}

	/**
	 * Stream��Writer�����ׂĕ��܂��B
	 * IOException���N�����Ă��������Ĉ�ʂ�S�ĕ��܂��B
	 * @param closables Stream��Writer�̃��X�g
	 */
	public static void closeSilently(Closeable... closables) {
		for(Closeable closable : closables) {
			try {
				closable.close();
			}
			catch(IOException ignore) {
				// ��������
			}
		}
	}
}