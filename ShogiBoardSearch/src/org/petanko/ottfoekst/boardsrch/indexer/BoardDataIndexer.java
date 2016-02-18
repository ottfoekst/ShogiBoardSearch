package org.petanko.ottfoekst.boardsrch.indexer;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import org.petanko.ottfoekst.boardsrch.util.IoUtils;

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
	/** 棋譜ファイルが格納したパス */
	private Path kifuDataPath;
	
	/** 棋譜ID */
	private int kifuId = 0;
	
	/** 棋譜IDファイル書き込み */
	private DataOutputStream kifuIdDos;
	/** 棋譜IDポインタファイル書き込み */
	private DataOutputStream kifuIdPtrDos;
	/** 局面転置インデックスファイル書き込み */
	private DataOutputStream[] boardInvDosList;
	/** 局面転置インデックスポインタファイル書き込み */
	private DataOutputStream[] boardInvPtrDosList;
	
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
				
		try {
			// 棋譜IDの初期化
			kifuId = 0;
			// 各種DataOutputStreamの初期化
			initializeDosList();
			
			// メモリ上に転置インデックスを構築
			createInvIndexOnMemory(kifuDataPath.toFile());
		}
		finally {
			// 各種DataOutputStreamを閉じる
			closeDosList();
		}
	}

	private void initializeDosList() throws Exception {
		kifuIdDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdFilePath(indexDir));
		kifuIdPtrDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdPtrFilePath(indexDir));
		
		boardInvDosList = new DataOutputStream[50];
		for(int blockNo = 0; blockNo < boardInvDosList.length; blockNo++) {
			boardInvDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvFilePath(indexDir, blockNo));
		}
		
		boardInvPtrDosList = new DataOutputStream[50];
		for(int blockNo = 0; blockNo < boardInvPtrDosList.length; blockNo++) {
			boardInvPtrDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo));
		}
	}

	private void createInvIndexOnMemory(File kifuDataFileOrFolder) {
		// フォルダのとき
		if(kifuDataFileOrFolder.isDirectory()) {
			File[] files = kifuDataFileOrFolder.listFiles();
			for(File file : files) {
				createInvIndexOnMemory(file);
			}
		}
		// 棋譜ファイル(拡張子:kif)のとき
		else if(kifuDataFileOrFolder.toString().endsWith(".kif")) {
			// TODO 実装
		}
	}

	private void closeDosList() {
		// 全てのDataOutputStreamを連結
		DataOutputStream[] allDosList = Stream.concat(Arrays.stream(new DataOutputStream[]{kifuIdDos, kifuIdPtrDos}), 
				Stream.concat(Arrays.stream(boardInvDosList), Arrays.stream(boardInvPtrDosList))).toArray(size -> new DataOutputStream[size]);
		// Exceptionをスローせずに全て閉じる
		IoUtils.closeSilently(allDosList);
	}
}
