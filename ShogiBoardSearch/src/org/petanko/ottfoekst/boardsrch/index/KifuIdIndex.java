package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.petanko.ottfoekst.boardsrch.indexer.IndexerUtils;
import org.petanko.ottfoekst.boardsrch.util.ArrayUtils;
import org.petanko.ottfoekst.boardsrch.util.IoUtils;

/**
 * 棋譜IDインデックス(棋譜IDと棋譜ファイルパスの紐付けを保持)。
 * @author ottfoekst
 *
 */
public class KifuIdIndex {
	
	/** 各棋譜IDファイルへのポインタから読み込む棋譜ID数。
	 *  この数値が大きいほどメモリ使用量は抑えらますが、棋譜IDファイルの読み込みに時間がかかる可能性があります。 */
	public static final int KIFUID_NUM_EACH_PTR = 50;
	
	/** インデックスを格納するパス */
	private Path indexDir;
	
	/** 棋譜IDインデックス(オンメモリ) */
	private int[] kifuIdArray;
	private long[] kifuIdPtrArray;

	/**
	 * コンストラクタ。
	 * @param indexDir インデックスを格納するパス
	 */
	public KifuIdIndex(Path indexDir) {
		this.indexDir = indexDir;
	}
	
	/**
	 * 棋譜IDインデックスをロードします。
	 */
	public void loadIndex() throws Exception {
		List<Integer> kifuIdList = new ArrayList<>();
		List<Long> kifuIdPtrList = new ArrayList<>();
		
		int skipCount = 0;
		try(DataInputStream kifuIdDis = IoUtils.newDataInputStream(IndexerUtils.getKifuIdPtrFilePath(indexDir))) {
			while(true) {
				// 棋譜ID と 棋譜IDファイルへのポインタをリストに登録
				kifuIdList.add(kifuIdDis.readInt());
				kifuIdPtrList.add(kifuIdDis.readLong());
				
				while(true) {
					if(skipCount >= KIFUID_NUM_EACH_PTR - 1){
						skipCount = 0;
						break;
					}
					
					kifuIdDis.readInt(); // 棋譜IDを読み飛ばす
					kifuIdDis.readLong(); // 棋譜IDファイルへのポインタを読み飛ばす
					skipCount++;
				}
			}
		}
		catch(EOFException end) {
			// 読み込み終了
		}
		
		// 棋譜IDインデックス(オンメモリ)にセットする。
		kifuIdArray = ArrayUtils.createArrayWithIntMax(kifuIdList);
		kifuIdPtrArray = ArrayUtils.createArrayWithLongMax(kifuIdPtrList);
	}
	
	/**
	 * 棋譜IDインデックスをアンロードします。
	 */
	public void unloadIndex() {
		kifuIdArray = null;
		kifuIdPtrArray = null;
	}
}
