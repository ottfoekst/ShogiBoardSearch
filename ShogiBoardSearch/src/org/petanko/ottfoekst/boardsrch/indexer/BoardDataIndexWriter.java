package org.petanko.ottfoekst.boardsrch.indexer;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 局面転置インデックスの書き出しを行うクラス。
 * @author ottfoekst
 *
 */
public class BoardDataIndexWriter {
	
	/**　局面トークンIDのリスト　*/
	private List<Long> boardTokenIdList = new ArrayList<>();
	
	/**　各局面トークンIDのポスティングリストを保持するマップ　*/
	private Map<Long, List<String>> postingListMap = new HashMap<>();
	
	/**
	 * コンストラクタ。
	 */
	public BoardDataIndexWriter() {
		
	}
	
	/**
	 * ポスティングをメモリ上の転置インデックスに追加する。
	 * @param kifuId 棋譜ID
	 * @param tesu 手数
	 * @param boardTokenId　局面トークンID
	 */
	public void add(int kifuId, int tesu, long boardTokenId) {
		// 初めて登録する局面トークンIDのとき
		if(!postingListMap.containsKey(boardTokenId)) {
			List<String> postingList = new ArrayList<>();
			postingList.add(String.valueOf(kifuId) + "," + String.valueOf(tesu));
			// ポスティングリスト、局面トークンIDリストに新規登録
			postingListMap.put(boardTokenId, postingList);
			boardTokenIdList.add(boardTokenId);
		}
		// これまでに登録したことのある局面トークンIDのとき
		else {
			postingListMap.get(boardTokenId).add(String.valueOf(kifuId) + "," + String.valueOf(tesu));
		}
	}
	
	/**
	 * 局面転置インデックスを書き込む。
	 * @param boardInvDos 局面転置インデックス書き込み用DataOutputStream
	 * @param boardInvPtrDos 局面転置インデックスポインタファイル書き込み用DataOutputStream
	 */
	public void writeToIndexFile(DataOutputStream boardInvDos, DataOutputStream boardInvPtrDos) throws Exception {
		
		// 局面転置インデックスのポインタ
		long boardInvPtr = 0;
		
		// 局面トークンIDの昇順に並び替え、その順でインデックスファイルに書き込む
		Collections.sort(boardTokenIdList);
		for(long boardTokenId : boardTokenIdList) {
			// 局面転置インデックスポインタファイルへの書き込み
			boardInvPtrDos.writeLong(boardTokenId); // 局面トークンID
			boardInvPtrDos.writeLong(boardInvPtr); // 局面転置インデックスのポインタ
			boardInvPtrDos.flush();
			
			// 局面転置インデックスファイルへの書き込み
			boardInvPtr += writeBoardInvFile(boardInvDos, boardTokenId);
		}
		
		// 後処理
		boardTokenIdList.clear();
		boardTokenIdList = null;
		postingListMap.clear();
		postingListMap = null;
	}

	private long writeBoardInvFile(DataOutputStream boardInvDos, long boardTokenId) throws Exception {
		// 書き込みサイズ
		long writeSize = 0;
		
		// 局面トークンIDの書き込み
		boardInvDos.writeLong(boardTokenId);
		writeSize += 8;
		
		// 局面トークンIDのポスティングリストを取得
		List<String> postingList = postingListMap.get(boardTokenId);
		// 出現棋譜数の書き込み
		boardInvDos.writeInt(getKifuCount(postingList));
		writeSize += 4;
		
		// ポスティングリストの書き込み
		int postingIndex = 0;
		int beforeKifuId = -1;
		while(postingIndex < postingList.size()) {
			// 手数リストの生成
			List<Integer> tesuList = new ArrayList<>();
			int kifuId = 0;
			while(postingIndex < postingList.size()) {
				kifuId = Integer.parseInt(postingList.get(postingIndex).split(",")[0]);
				// 初回 or 1つ前と同じ棋譜IDのとき
				if(kifuId == -1 || kifuId == beforeKifuId) {
					// 手数リストに追加
					tesuList.add(Integer.parseInt(postingList.get(postingIndex).split(",")[1]));
					postingIndex++;
				}
				// 異なる棋譜IDが出てきたとき
				else {
					beforeKifuId = kifuId;
					// ループを抜ける
					break;
				}
			}
			
			// 棋譜IDの出力
			boardInvDos.writeInt(kifuId);
			writeSize += 4;
			// 出現手数の書き込み
			boardInvDos.writeInt(tesuList.size());
			writeSize += 4;
			// 手数リストの書き込み
			for(int tesu : tesuList) {
				boardInvDos.writeInt(tesu);
				writeSize += 4;
			}
			
			postingIndex++;
		}
		
		return writeSize;
	}

	// ちょっと効率悪いけど仕方ない
	private int getKifuCount(List<String> postingList) {
		// 出現棋譜数
		int kifuCount = 0;
		
		// 1つ前の出現棋譜ID
		int beforeKifuId = -1;
		for(String posting : postingList) {
			int kifuId = Integer.parseInt(posting.split(",")[0]);
			if(kifuId != beforeKifuId) {
				kifuCount++;
				beforeKifuId = kifuId;
			}
		}
		return kifuCount;
	}
}
