package org.petanko.ottfoekst.boardsrch.indexer;

import java.nio.file.Path;

public class IndexerUtils {
	
	/** 棋譜IDファイル名 */
	public static final String KIFUID_FILE_NAME = "bs.kifuid";
	/** 棋譜IDポインタファイル名 */
	public static final String KIFUID_PTR_FILE_NAME = "bs.kifuid.ptr";
	/** 局面転置インデックスファイル名 */
	public static final String BOARD_INV_FILE_NAME = "bs.board.inv";
	/** 局面転置インデックスポインタファイル名 */
	public static final String BOARD_INV_PTR_FILE_NAME = "bs.board.inv.ptr";

	/**
	 * 棋譜IDファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @return 棋譜IDファイルのパス
	 */
	public static Path getKifuIdFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_FILE_NAME);
	}
	
	/**
	 * 棋譜IDポインタファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @return 棋譜IDポインタファイルのパス
	 */
	public static Path getKifuIdPtrFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_PTR_FILE_NAME);
	}
	
	/**
	 * 局面転置インデックスファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @param blockNo 局面ブロック
	 * @return 局面転置インデックスファイルのパス
	 */
	public static Path getBoardInvFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_FILE_NAME + "." + blockNo);
	}
	
	/**
	 * 局面転置インデックスポインタファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @param blockNo 局面ブロック
	 * @return 局面転置インデックスポインタファイルのパス
	 */
	public static Path getBoardInvPtrFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_PTR_FILE_NAME + "." + blockNo);
	}
}
