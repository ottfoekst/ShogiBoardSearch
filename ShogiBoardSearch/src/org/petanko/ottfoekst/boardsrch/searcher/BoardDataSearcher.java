package org.petanko.ottfoekst.boardsrch.searcher;

import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.index.BoardDataIndex;
import org.petanko.ottfoekst.boardsrch.index.KifuIdIndex;

/**
 * 局面検索器。
 * @author ottfoekst
 *
 */
public class BoardDataSearcher {

	/** インデックスが格納されたパス */
	private Path indexDir;
	
	/** 棋譜IDインデックス */
	private KifuIdIndex kifuIdIndex;
	/** 局面転置インデックス */
	private BoardDataIndex boardDataIndex;
	
	/**
	 * コンストラクタ。
	 * @param indexDir インデックスが格納されたパス
	 * @throws Exception
	 */
	public BoardDataSearcher(Path indexDir) throws Exception {
		this.indexDir = indexDir;
		// 棋譜IDインデックスのロード
		kifuIdIndex = new KifuIdIndex(indexDir);
		kifuIdIndex.loadIndex();
		// 局面転置インデックスのロード
		boardDataIndex = new BoardDataIndex(indexDir);
		boardDataIndex.loadIndex();
	}
	
	/**
	 * 局面検索を行います。
	 * @param searchCondition 局面データ検索条件
	 */
	public void searchBoardData(BoardSearchCondition searchCondition) {
		// 完全一致検索のとき
		if(searchCondition.isPerfectMatch()) {
			searchBoardByPerfectMatch(searchCondition);
		}
		// 類似局面検索のとき
		else {
			searchBoardBySimilarSearch(searchCondition, searchCondition.getSimilarSearchThreshold());
		}
	}
	
	private void searchBoardByPerfectMatch(BoardSearchCondition searchCondition) {
		// TODO 実装
	}
	
	private void searchBoardBySimilarSearch(BoardSearchCondition searchCondition, float similarSearchThreshold) {
		// TODO 実装
		
	}

	/**
	 *　局面検索器を終了します。
	 */
	public void shutDown() {
		kifuIdIndex.unloadIndex();
		kifuIdIndex = null;
		boardDataIndex.unloadIndex();
		boardDataIndex = null;
	}
}
