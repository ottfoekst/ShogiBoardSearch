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
 * �ǖʌ�����B
 * @author ottfoekst
 *
 */
public class BoardDataSearcher {

	/** �C���f�b�N�X���i�[���ꂽ�p�X */
	private Path indexDir;

	/** ����ID�C���f�b�N�X */
	private KifuIdIndex kifuIdIndex;
	/** �ǖʓ]�u�C���f�b�N�X */
	private BoardDataIndex boardDataIndex;

	/**
	 * �R���X�g���N�^�B
	 * @param indexDir �C���f�b�N�X���i�[���ꂽ�p�X
	 * @throws Exception
	 */
	public BoardDataSearcher(Path indexDir) throws Exception {
		this.indexDir = indexDir;
		// ����ID�C���f�b�N�X�̃��[�h
		kifuIdIndex = new KifuIdIndex(indexDir);
		kifuIdIndex.loadIndex();
		// �ǖʓ]�u�C���f�b�N�X�̃��[�h
		boardDataIndex = new BoardDataIndex(indexDir);
		boardDataIndex.loadIndex();
	}

	/**
	 * �ǖʌ������s���܂��B
	 * @param piecePosition �ǖ�
	 * @param isPerfectMatch ���S��v�������ǂ���
	 * @param similarSearchThreshold �ގ��ǖʌ����̃X�R�A臒l
	 * @throws Exception
	 */
	public void searchBoardData(PiecePosition piecePosition, boolean isPerfectMatch, int similarSearchThreshold) throws Exception {
		// ���S��v�����̂Ƃ�
		if(isPerfectMatch || (!isPerfectMatch && similarSearchThreshold == 100)) {
			searchBoardByPerfectMatch(piecePosition);
		}
		// �ގ��ǖʌ����̂Ƃ�
		else {
			searchBoardBySimilarSearch(piecePosition, similarSearchThreshold);
		}
	}

	private void searchBoardByPerfectMatch(PiecePosition piecePosition) throws Exception {
		// ���S��v�����Ŏg�p����ǖʃg�[�N��ID�̃��X�g
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

		// �������ʂ̏o��
		outputSearchResultOfPerfectMatch(boardDataIndexResult, piecePosition);
	}

	private Map<Integer, List<Integer>> andBoardDataIndex(Map<Integer, List<Integer>> boardDataIndexResult,	Map<Integer, List<Integer>> currentBoardDataIndex) {

		Map<Integer, List<Integer>> andBoardDataIndex = new TreeMap<>((left, right) -> Integer.compare(left, right));
		// ����ID�̋��ʕ���
		int[] andKifuIdArray = currentBoardDataIndex.keySet().stream()
				.mapToInt(integer -> integer.intValue()).filter(kifuId -> boardDataIndexResult.keySet().contains(kifuId)).toArray();

		// ����ID�̋��ʕ������Ȃ��Ƃ�
		if(andKifuIdArray.length == 0) {
			return andBoardDataIndex;
		}
		// ����ID�̋��ʕ���������Ƃ�
		else {
			for(int kifuId : andKifuIdArray) {
				List<Integer> andTesuList = new ArrayList<>();
				boardDataIndexResult.get(kifuId).stream().filter(tesu -> currentBoardDataIndex.get(kifuId).contains(tesu)).forEach(andTesuList::add);
				// �萔���X�g�̋��ʕ���������Ƃ�
				if(andTesuList.size() > 0) {
					andBoardDataIndex.put(kifuId, andTesuList);
				}
			}
		}

		return andBoardDataIndex;
	}

	private void outputSearchResultOfPerfectMatch(Map<Integer, List<Integer>> boardDataIndexResult, PiecePosition piecePosition) throws Exception {

		// �����ǖʂ̏o��
		piecePosition.outputBoard();
		System.out.println("");

		// �������ʂ̏o��
		for(Map.Entry<Integer, List<Integer>> entry : boardDataIndexResult.entrySet()) {
			// �����t�@�C���p�X�̏o��
			System.out.println("\"" + kifuIdIndex.getKifuFilePath(entry.getKey()) + "\"");
			// �萔�̏o��
			System.out.println("�@(��@" + String.join(", ", entry.getValue().stream().map(String::valueOf).toArray(size -> new String[size])) + "�@��)" + "\n");
		}
	}

	private void searchBoardBySimilarSearch(PiecePosition piecePosition, int similarSearchThreshold) throws Exception {
		// �ǖʃg�[�N��ID�̃��X�g
		long[] boardTokenIdList = IntStream.range(0, 50).mapToLong(blockNo -> IndexerUtils.calculateBoardTokenId(piecePosition, blockNo)).toArray();

		Map<Integer, Map<Integer, Integer>> boardDataIndexResultAndScore = new TreeMap<>((left, right) -> Integer.compare(left, right));
		for(int blockNo = 0; blockNo < boardTokenIdList.length; blockNo++) {
			Map<Integer, List<Integer>> currentBoardDataIndex = boardDataIndex.getBoardDataIndex(blockNo, boardTokenIdList[blockNo]);
			mergeBoardDataIndexAndScore(boardDataIndexResultAndScore, currentBoardDataIndex, blockNo, similarSearchThreshold);
		}

		// score���ł��������ʂɏW�񂷂�
		Set<SimilarSearchResult> similarSearchResultSet = aggregateHighestScoreResult(boardDataIndexResultAndScore, similarSearchThreshold);

		int debug = 1;
		// TODO �������ʂ̏o��;
	}

	private void mergeBoardDataIndexAndScore(Map<Integer, Map<Integer, Integer>> boardDataIndexResultAndScore, Map<Integer, List<Integer>> currentBoardDataIndex, int blockNo, int similarSearchThreshold) {

		int[] onlyCurrentKifuIdArray = currentBoardDataIndex.keySet().stream()
				.mapToInt(integer -> integer.intValue()).filter(kifuId -> !boardDataIndexResultAndScore.keySet().contains(kifuId)).toArray();
		int[] andKifuIdArray = currentBoardDataIndex.keySet().stream()
				.mapToInt(integer -> integer.intValue()).filter(kifuId -> boardDataIndexResultAndScore.keySet().contains(kifuId)).toArray();

		if(onlyCurrentKifuIdArray.length > 0 && similarSearchThreshold < 2 * (50 - blockNo)) {
			// ����̓X�R�A��2�Ƃ��ēo�^
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
				// ���o�̎萔�Ȃ�΃X�R�A��2�A���o�̎萔�Ȃ�X�R�A��2���Z
				currentTesuList.stream().forEach(tesu -> tesuAndScoreMap.compute(tesu, (tesuKey, oldScore) -> oldScore == null ? 2 : oldScore + 2));
			}
		}

		// TODO �ގ��ǖʌ����̃X�R�A臒l�𒴂��邱�Ƃ��ł��Ȃ����ʂ��Ԉ���
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
	 *�@�ǖʌ�������I�����܂��B
	 */
	public void shutDown() {
		kifuIdIndex.unloadIndex();
		kifuIdIndex = null;
		boardDataIndex.unloadIndex();
		boardDataIndex = null;
	}

	/** �ގ��ǖʌ������� */
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
