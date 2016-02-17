package org.petanko.ottfoekst.boardsrch.indexer;

import java.nio.file.Path;

/**
 * 局面データのインデックスを生成するクラス。
 * @author ottfoekst
 *
 */
public class BoardDataIndexer {

	/** 棋譜データのファイルパスの区切り文字 */
	public static final char KIFUDATA_DELIM = 0x00;
	
	/** インデックスを格納するパス */
	private Path indexDir;
	/** 棋譜ファイルが格納されたディレクトリ */
	private Path kifuDataPath;
	
	/** 棋譜ID */
	private int kifuId = 0;
	
	/**
	 * コンストラクタ。
	 * @param indexDir インデックスを格納するパス
	 * @param kifuDataPath 棋譜ファイルが格納されたパス
	 */
	public BoardDataIndexer(Path indexDir, Path kifuDataPath) {
		this.indexDir = indexDir;
		this.kifuDataPath = kifuDataPath;
	}
	
	/**
	 * 局面データのインデックスを生成します。
	 * @throws Exception
	 */
	public void generateIndex() throws Exception {
		// TODO 実装
	}
}
