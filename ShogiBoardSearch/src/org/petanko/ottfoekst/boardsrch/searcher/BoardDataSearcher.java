package org.petanko.ottfoekst.boardsrch.searcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.IntStream;

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
	 * @param piecePosition 局面
	 * @param isPerfectMatch 完全一致検索かどうか
	 * @param similarSearchThreshold 類似局面検索のスコア閾値
	 * @throws Exception
	 */
	public void searchBoardData(PiecePosition piecePosition, boolean isPerfectMatch, int similarSearchThreshold) throws Exception {
		// 完全一致検索のとき
		if(isPerfectMatch || (!isPerfectMatch && similarSearchThreshold == 100)) {
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

		// 検索結果の出力
		outputSearchResultOfPerfectMatch(boardDataIndexResult, piecePosition);
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

	private void outputSearchResultOfPerfectMatch(Map<Integer, List<Integer>> boardDataIndexResult, PiecePosition piecePosition) throws Exception {

		// 検索局面の出力
		piecePosition.outputBoard();
		System.out.println("");

		// 検索結果の出力
		for(Map.Entry<Integer, List<Integer>> entry : boardDataIndexResult.entrySet()) {
			// 棋譜ファイルパスの出力
			System.out.println("\"" + kifuIdIndex.getKifuFilePath(entry.getKey()) + "\"");
			// 手数の出力
			System.out.println("　(第　" + String.join(", ", entry.getValue().stream().map(String::valueOf).toArray(size -> new String[size])) + "　手)" + "\n");
		}
	}

	private void searchBoardBySimilarSearch(PiecePosition piecePosition, int similarSearchThreshold) throws Exception {
		// 局面トークンIDのリスト
		long[] boardTokenIdList = IntStream.range(0, 50).mapToLong(blockNo -> IndexerUtils.calculateBoardTokenId(piecePosition, blockNo)).toArray();

		Map<Integer, Map<Integer, Integer>> boardDataIndexResultAndScore = new TreeMap<>((left, right) -> Integer.compare(left, right));
		for(int blockNo = 0; blockNo < boardTokenIdList.length; blockNo++) {
			Map<Integer, List<Integer>> currentBoardDataIndex = boardDataIndex.getBoardDataIndex(blockNo, boardTokenIdList[blockNo]);
			mergeBoardDataIndexAndScore(boardDataIndexResultAndScore, currentBoardDataIndex, blockNo, similarSearchThreshold);
		}

		// scoreが最も高い結果に集約する
		Set<SimilarSearchResult> similarSearchResultSet = aggregateHighestScoreResult(boardDataIndexResultAndScore, similarSearchThreshold);

		int debug = 1;
		// TODO 検索結果の出力;
	}

	private void mergeBoardDataIndexAndScore(Map<Integer, Map<Integer, Integer>> boardDataIndexResultAndScore, Map<Integer, List<Integer>> currentBoardDataIndex, int blockNo, int similarSearchThreshold) {

		int[] onlyCurrentKifuIdArray = currentBoardDataIndex.keySet().stream()
				.mapToInt(integer -> integer.intValue()).filter(kifuId -> !boardDataIndexResultAndScore.keySet().contains(kifuId)).toArray();
		int[] andKifuIdArray = currentBoardDataIndex.keySet().stream()
				.mapToInt(integer -> integer.intValue()).filter(kifuId -> boardDataIndexResultAndScore.keySet().contains(kifuId)).toArray();

		if(onlyCurrentKifuIdArray.length > 0 && similarSearchThreshold < 2 * (50 - blockNo)) {
			// 初回はスコアを2として登録
			for(int index = 0; index < onlyCurrentKifuIdArray.length; index++) {
				int kifuId = onlyCurrentKifuIdArray[index];

				Map<Integer, Integer> tesuAndScoreMap = new TreeMap<>();
				currentBoardDataIndex.get(kifuId).stream().forEach(tesu -> tesuAndScoreMap.put(tesu, 2));

				boardDataIndexResultAndScore.put(kifuId, tesuAndScoreMap);
			}
		}

		if(andKifuIdArray.length > 0) {
			for(int index = 0; index < andKifuIdArray.length; index++) {
				int kifuId = andKifuIdArray[index];

				List<Integer> currentTesuList = currentBoardDataIndex.get(kifuId);
				Map<Integer, Integer> tesuAndScoreMap = boardDataIndexResultAndScore.get(kifuId);
				// 初出の手数ならばスコアを2、既出の手数ならスコアを2加算
				currentTesuList.stream().forEach(tesu -> tesuAndScoreMap.compute(tesu, (tesuKey, oldScore) -> oldScore == null ? 2 : oldScore + 2));
			}
		}

		// TODO 類似局面検索のスコア閾値を超えることができない結果を間引く
	}

	private Set<SimilarSearchResult> aggregateHighestScoreResult(Map<Integer, Map<Integer, Integer>> boardDataIndexResultAndScore, int similarSearchThreshold) {
		Set<SimilarSearchResult> similarSearchResultSet = new TreeSet<>();

		boardDataIndexResultAndScore.forEach((kifuId, tesuAndScoreMap) ->
			{
				int maxScore = tesuAndScoreMap.values().stream().max(Comparator.naturalOrder()).get();
				if(similarSearchThreshold <= maxScore) {
					int[] tesuOfMaxScore = new int[1];
					tesuAndScoreMap.forEach((tesu, score) ->
						{
							if(score == maxScore) {
								tesuOfMaxScore[0] = tesu;
							}
						});
					similarSearchResultSet.add(new SimilarSearchResult(kifuId, tesuOfMaxScore[0], maxScore));
					}
			});

		return similarSearchResultSet;
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

	/** 類似局面検索結果 */
	private class SimilarSearchResult implements Comparable<SimilarSearchResult> {
		private int kifuId;
		private int tesu;
		private int score;

		private SimilarSearchResult(int kifuId, int tesu, int score) {
			this.kifuId = kifuId;
			this.tesu = tesu;
			this.score = score;
		}

		@Override
		public int compareTo(SimilarSearchResult otherResult) {
			if(this.score != otherResult.score) {
				return - Integer.compare(this.score, otherResult.score);
			}
			else if(this.kifuId != otherResult.kifuId) {
				return Integer.compare(this.kifuId, otherResult.kifuId);
			}
			else {
				return Integer.compare(this.tesu, otherResult.tesu);
			}
		}

		@Override
		public String toString() {
			return "SimilarSearchResult [kifuId=" + kifuId + ", tesu=" + tesu + ", score=" + score + "]";
		}
	}
}
