package org.petanko.ottfoekst.boardsrch.searcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.petanko.ottfoekst.boardsrch.index.BoardDataIndex;
import org.petanko.ottfoekst.boardsrch.index.BoardDataToken;
import org.petanko.ottfoekst.boardsrch.index.KifuIdIndex;
import org.petanko.ottfoekst.boardsrch.indexer.IndexerUtils;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;

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
	 * @throws Exception
	 */
	public void searchBoardData(PiecePosition piecePosition, boolean isPerfectMatch, float similarSearchThreshold) throws Exception {
		// 完全一致検索のとき
		if(isPerfectMatch || (!isPerfectMatch && similarSearchThreshold == 1.0f)) {
			searchBoardByPerfectMatch(piecePosition);
		}
		// 類似局面検索のとき
		else {
			searchBoardBySimilarSearch(piecePosition, similarSearchThreshold);
		}
	}
	
	private void searchBoardByPerfectMatch(PiecePosition piecePosition) throws Exception {
		// 完全一致検索で使用する局面トークンIDのリスト
		long[] boardTokenIdListForPerfectMatch = Arrays.stream(BoardDataToken.BLOCKNO_LIST_USED_FOR_PERFECT_MATCH)
				.mapToLong(blockNo -> IndexerUtils.calculateBoardTokenId(piecePosition, blockNo)).toArray();
		
		Map<Integer, List<Integer>> boardDataIndexResult = null;
		for(int index = 0; index < BoardDataToken.BLOCKNO_LIST_USED_FOR_PERFECT_MATCH.length; index++) {
			Map<Integer, List<Integer>> currentBoardDataIndex = boardDataIndex.getBoardDataIndex(BoardDataToken.BLOCKNO_LIST_USED_FOR_PERFECT_MATCH[index], boardTokenIdListForPerfectMatch[index]);
			
			if(boardDataIndexResult == null) {
				boardDataIndexResult = currentBoardDataIndex;
			}
			else {
				boardDataIndexResult = andBoardDataIndex(boardDataIndexResult, currentBoardDataIndex);
			}
		}
	}
	
	private Map<Integer, List<Integer>> andBoardDataIndex(Map<Integer, List<Integer>> boardDataIndexResult,	Map<Integer, List<Integer>> currentBoardDataIndex) {
		
		Map<Integer, List<Integer>> andBoardDataIndex = new TreeMap<>((left, right) -> Integer.compare(left, right));
		// 棋譜IDの共通部分
		int[] andKifuIdArray = currentBoardDataIndex.keySet().stream()
				.mapToInt(integer -> integer.intValue()).filter(kifuId -> boardDataIndexResult.keySet().contains(kifuId)).toArray();
		
		// 棋譜IDの共通部分がないとき
		if(andKifuIdArray.length == 0) {
			return andBoardDataIndex;
		}
		// 棋譜IDの共通部分があるとき
		else {
			for(int kifuId : andKifuIdArray) {				
				List<Integer> andTesuList = new ArrayList<>();
				boardDataIndexResult.get(kifuId).stream().filter(tesu -> currentBoardDataIndex.get(kifuId).contains(tesu)).forEach(andTesuList::add);
				// 手数リストの共通部分があるとき
				if(andTesuList.size() > 0) {
					andBoardDataIndex.put(kifuId, andTesuList);
				}
			}
		}
				
		return andBoardDataIndex;
	}

	private void searchBoardBySimilarSearch(PiecePosition piecePosition, float similarSearchThreshold) {
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
