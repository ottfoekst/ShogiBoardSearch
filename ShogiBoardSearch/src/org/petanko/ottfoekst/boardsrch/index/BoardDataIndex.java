package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.petanko.ottfoekst.boardsrch.indexer.IndexerUtils;
import org.petanko.ottfoekst.boardsrch.util.ArrayUtils;
import org.petanko.ottfoekst.boardsrch.util.IoUtils;

/**
 * 局面転置インデックス。
 * @author ottfoekst
 *
 */
public class BoardDataIndex {
	
	/** 各局面転置インデックスへのポインタから読み込む局面トークンID数。
	 *  この数値が大きいほどメモリ使用量は抑えらますが、局面データインデックスの読み込みに時間がかかる可能性があります。 */
	public static final int BOARDTOKEN_NUM_EACH_PTR = 50;

	/** インデックスを格納するパス */
	private Path indexDir;
	
	/** 局面転置インデックス(オンメモリ) */
	private long[][] boardTokenIdMatrix = new long[50][];
	private long[][] boardInvPtrMatrix = new long[50][];
	
	/**
	 * コンストラクタ。
	 * @param indexDir　インデックスを格納するパス
	 */
	public BoardDataIndex(Path indexDir) {
		this.indexDir = indexDir;
	}
	
	/**
	 * 局面転置インデックスをロードします。
	 */
	public void loadIndex() throws Exception {
		for(int blockNo = 0; blockNo < 50; blockNo++) {
			List<Long> boardTokenIdList = new ArrayList<>();
			List<Long> boardInvPtrList = new ArrayList<>();
			
			int skipCount = 0;
			try(DataInputStream boardInvPtrDis = IoUtils.newDataInputStream(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo))) {
				while(true) {
					// 局面トークンID と 局面転置インデックスのポインタをリストに登録
					boardTokenIdList.add(boardInvPtrDis.readLong());
					boardInvPtrList.add(boardInvPtrDis.readLong());
					
					while(true) {
						if(skipCount >= BOARDTOKEN_NUM_EACH_PTR - 1) {
							skipCount = 0;
							break;
						}
						
						boardInvPtrDis.readLong(); // 局面トークンIDを読み飛ばす
						boardInvPtrDis.readLong(); // 局面転置インデックスのポインタを読み飛ばす
						skipCount++;
					}
				}
			}
			catch(EOFException end) {
				// 読み込み終了
			}
			
			// 局面転置インデックス(オンメモリ)にセットする
			boardTokenIdMatrix[blockNo] = ArrayUtils.createArrayWithLongMax(boardTokenIdList);
			boardInvPtrMatrix[blockNo] = ArrayUtils.createArrayWithLongMax(boardInvPtrList);
		}
	}
	
	/**
	 * 引数のblockNo、boardTokenIdに該当する局面転置インデックスを返します。
	 * @param blockNo 盤面データのどの部分を表すかを示す数値{@see BoardDataToken}
	 * @param boardTokenId 局面トークンID
	 * @return 局面転置インデックス
	 * @throws Exception
	 */
	public Map<Integer, List<Integer>> getBoardDataIndex(int blockNo, long boardTokenId) throws Exception {
		Map<Integer, List<Integer>> boardDataIndexMap = new TreeMap<>((left, right) -> Integer.compare(left, right));
		
		long boardInvPtr = getBoardInvPtr(blockNo, boardTokenId);
		// ポインタが存在しないとき
		if(boardInvPtr == -1) {
			// 空の局面転置インデックスを返す
			return boardDataIndexMap;
		}
		
		// 局面転置インデックス読み込み
		try(RandomAccessFile boardInvRaf = new RandomAccessFile(IndexerUtils.getBoardInvFilePath(indexDir, blockNo).toFile(), "r")) {
			boardInvRaf.seek(boardInvPtr);
			boardInvRaf.readLong(); // 局面トークンIDを読み飛ばす
			
			int kifuCount = boardInvRaf.readInt();
			for(int kifuCountIndex = 0; kifuCountIndex < kifuCount; kifuCountIndex++) {
				int kifuId = boardInvRaf.readInt();
				int tesuNum = boardInvRaf.readInt();
				
				List<Integer> tesuList = new ArrayList<>();
				for(int tesuIndex = 0; tesuIndex < tesuNum; tesuIndex++) {
					tesuList.add(boardInvRaf.readInt());
				}
				
				boardDataIndexMap.put(kifuId, tesuList);
			}
		}
		
		return boardDataIndexMap;
	}

	private long getBoardInvPtr(int blockNo, long boardTokenId) throws Exception {
		
		/** 1. どの局面転置インデックス(オンメモリ)から読みこめばよいかを探索する */
		int boardTokenIdIndex = 0;
		while(boardTokenIdIndex < boardTokenIdMatrix[blockNo].length) {
			if(boardTokenId == boardTokenIdMatrix[blockNo][boardTokenIdIndex]) {
				return boardInvPtrMatrix[blockNo][boardTokenIdIndex];
			}
			else if(boardTokenId < boardTokenIdMatrix[blockNo][boardTokenIdIndex]) {
				// インデックスを1つ前に戻す
				boardTokenIdIndex = boardTokenIdIndex - 1;
				break;
			}
			boardTokenIdIndex++;
		}
		
		/** 2. boardTokenIdに該当する転置インデックスのポインタを探索する */
		long boardInvPtr = -1;
		try(RandomAccessFile boardInvPtrRaf = new RandomAccessFile(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo).toFile(), "r")) {
			boardInvPtrRaf.seek(8 * 2 * BOARDTOKEN_NUM_EACH_PTR * boardTokenIdIndex);
			// 局面転置インデックスのポインタを取得
			int readCount = 0;
			while(readCount < BOARDTOKEN_NUM_EACH_PTR) {
				long currentBoardTokenId = boardInvPtrRaf.readLong();
				long currentBoardInvPtr = boardInvPtrRaf.readLong();
				
				if(boardTokenId == currentBoardTokenId) {
					boardInvPtr = currentBoardInvPtr;
					break;
				}
				readCount++;
			}
		}
		catch(EOFException e) {
			// ファイル末尾に到達
		}
		
		return boardInvPtr;
	}
	
	/**
	 * 局面転置インデックスをアンロードします。
	 */
	public void unloadIndex() {
		boardTokenIdMatrix = null;
		boardInvPtrMatrix = null;
	}
}
