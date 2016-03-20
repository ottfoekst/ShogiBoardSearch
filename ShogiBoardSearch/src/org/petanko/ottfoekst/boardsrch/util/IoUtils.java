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
 * File I/Oで便利なメソッドを集めたクラス。
 * @author ottfoekst
 *
 */
public class IoUtils {
	
	/**
	 * 指定したファイルパスから読み込み可能なDataInputStreamを返します。
	 * @param filePath ファイルパス
	 * @return filePathから読み込み可能なDataInputStream
	 * @throws Exception
	 */
	public static DataInputStream newDataInputStream(Path filePath) throws Exception {
		return new DataInputStream(new BufferedInputStream(new FileInputStream(filePath.toFile())));
	}
	
	/**
	 * 指定したファイルパスに書き込み可能なDataOutputStreamを返します。
	 * @param filePath ファイルパス
	 * @return filePathに書き込み可能なDataOutputStream
	 * @throws Exception
	 */
	public static DataOutputStream newDataOutputStream(Path filePath) throws Exception {
		return new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filePath.toFile())));
	}
	
	/**
	 * 指定したファイルパスに書き込み可能なBufferedReaderを返します。
	 * @param filePath ファイルパス
	 * @return filePathに書き込み可能なBufferedReader
	 * @throws Exception
	 */
	public static BufferedReader newBufferedReader(Path filePath) throws Exception {
		return new BufferedReader(new FileReader(filePath.toFile()));
	}

	/**
	 * StreamやWriterをすべて閉じます。
	 * IOExceptionが起こっても無視して一通り全て閉じます。
	 * @param closables StreamやWriterのリスト
	 */
	public static void closeSilently(Closeable... closables) {
		for(Closeable closable : closables) {
			try {
				closable.close();
			}
			catch(IOException ignore) {
				// 無視する
			}
		}
	}
}
