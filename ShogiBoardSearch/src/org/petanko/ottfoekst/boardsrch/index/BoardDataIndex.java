package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.petanko.ottfoekst.boardsrch.indexer.IndexerUtils;
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
			boardTokenIdMatrix[blockNo] = createArrayWithLongMax(boardTokenIdList);
			boardInvPtrMatrix[blockNo] = createArrayWithLongMax(boardInvPtrList);
		}
	}
	
	private long[] createArrayWithLongMax(List<Long> longList) {
		
		long[] longArray = new long[longList.size() + 1];
		for(int index = 0; index < longList.size(); index++) {
			longArray[index] = longList.get(index);
		}
		longArray[longList.size()] = Long.MAX_VALUE; // 番人
		
		return longArray;
	}
	
	/**
	 * 局面転置インデックスをアンロードします。
	 */
	public void unloadIndex() {
		boardTokenIdMatrix = null;
		boardInvPtrMatrix = null;
	}
}
