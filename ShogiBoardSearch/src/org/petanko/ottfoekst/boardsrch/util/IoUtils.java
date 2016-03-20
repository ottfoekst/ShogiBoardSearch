package org.petanko.ottfoekst.boardsrch.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * File I/O�ŕ֗��ȃ��\�b�h���W�߂��N���X�B
 * @author ottfoekst
 *
 */
public class IoUtils {
	
	/**
	 * �w�肵���t�@�C���p�X����ǂݍ��݉\��DataInputStream��Ԃ��܂��B
	 * @param filePath �t�@�C���p�X
	 * @return filePath����ǂݍ��݉\��DataInputStream
	 * @throws Exception
	 */
	public static DataInputStream newDataInputStream(Path filePath) throws Exception {
		return new DataInputStream(new BufferedInputStream(new FileInputStream(filePath.toFile())));
	}
	
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
	 * �w�肵���t�@�C���p�X�ɏ������݉\��BufferedReader��Ԃ��܂��B
	 * @param filePath �t�@�C���p�X
	 * @return filePath�ɏ������݉\��BufferedReader
	 * @throws Exception
	 */
	public static BufferedReader newBufferedReader(Path filePath) throws Exception {
		return new BufferedReader(new FileReader(filePath.toFile()));
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
