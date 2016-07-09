package org.petanko.ottfoekst.boardsrch.main;

import java.io.File;
import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.indexer.BoardDataIndexer;

public class BoardDataIndexerMain {

	/** 棋譜ファイルが格納されたパスのデフォルト値 */
	public static final String KIFU_FILE_PATH_DEFAULT = "[KIFU_FILE_PATH]";

	/**
	 * 局面転置インデックスを生成するメインクラス。
	 * @param args[0] インデックスを格納するパス
	 * @param args[1] 棋譜ファイルが格納されたパス
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// 引数の変換
		Path indexDir = new File(args[0]).toPath();

		if(KIFU_FILE_PATH_DEFAULT.equals(args[1])) {
			throw new IllegalArgumentException("棋譜ファイルが格納されたフォルダパスを指定してください。 : " + KIFU_FILE_PATH_DEFAULT);
		}
		Path kifuDataPath = new File(args[1]).toPath();

		// 局面転置インデックスの生成
		BoardDataIndexer boardDataIndexer = new BoardDataIndexer(indexDir, kifuDataPath);
		boardDataIndexer.generateIndex();
	}
}
