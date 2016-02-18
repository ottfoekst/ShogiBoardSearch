package org.petanko.ottfoekst.boardsrch.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

/**
 * File I/Oで便利なメソッドを集めたクラス。
 * @author ottfoekst
 *
 */
public class IoUtils {
	
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
