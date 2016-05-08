package org.petanko.ottfoekst.boardsrch.index;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.RandomAccessFile;
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
	
	/**
	 * 棋譜ファイルパスの間のデリミタ。
	 */
	public static final char KIFU_FILEPATH_DELIM = 0x00;
	
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
	 * 指定した棋譜IDをもつ棋譜ファイルのパスを返します。
	 * @param kifuId 棋譜ID
	 * @return 棋譜ファイルのパス
	 * @throws Exception 
	 */
	public String getKifuFilePath(int kifuId) throws Exception {
		
		long kifuIdPtr = getKifuIdPtr(kifuId);
		// ポインタが存在しないとき
		if(kifuIdPtr == -1) {
			// 空文字を返す
			return "";
		}
		
		// 棋譜IDインデックス読み込み
		StringBuilder buf = new StringBuilder();
		try(RandomAccessFile kifuIdRaf = new RandomAccessFile(IndexerUtils.getKifuIdFilePath(indexDir).toFile(), "r")) {
			kifuIdRaf.seek(kifuIdPtr);
			
			char c;
			while((c = kifuIdRaf.readChar()) != KifuIdIndex.KIFU_FILEPATH_DELIM) {
				buf.append(c);
			}
		}
		
		return buf.toString();
	}
	
	private long getKifuIdPtr(int kifuId) throws Exception {
		
		/** 1. どの局面転置インデックス(オンメモリ)から読みこめばよいかを探索する */
		int kifuIdIndex = 0;
		while(kifuIdIndex < kifuIdArray.length) {
			if(kifuId == kifuIdArray[kifuIdIndex]) {
				return kifuIdPtrArray[kifuIdIndex];
			}
			else if(kifuId < kifuIdArray[kifuIdIndex]) {
				// インデックスを1つ前に戻す
				kifuIdIndex = kifuIdIndex - 1;
				break;
			}
			kifuIdIndex++;
		}
		
		/** 2. boardTokenIdに該当する転置インデックスのポインタを探索する */
		long kifuIdPtr = -1;
		try(RandomAccessFile kifuIdPtrRaf = new RandomAccessFile(IndexerUtils.getKifuIdPtrFilePath(indexDir).toFile(), "r")) {
			kifuIdPtrRaf.seek((4 + 8) * KIFUID_NUM_EACH_PTR * kifuIdIndex);
			// 局面転置インデックスのポインタを取得
			int readCount = 0;
			while(readCount < KIFUID_NUM_EACH_PTR) {
				long currentKifuId = kifuIdPtrRaf.readInt();
				long currentKifuIdPtr = kifuIdPtrRaf.readLong();
				
				if(kifuId == currentKifuId) {
					kifuIdPtr = currentKifuIdPtr;
					break;
				}
				readCount++;
			}
		}
		catch(EOFException e) {
			// ファイル末尾に到達
		}
		
		return kifuIdPtr;
	}

	/**
	 * 棋譜IDインデックスをアンロードします。
	 */
	public void unloadIndex() {
		kifuIdArray = null;
		kifuIdPtrArray = null;
	}
}
