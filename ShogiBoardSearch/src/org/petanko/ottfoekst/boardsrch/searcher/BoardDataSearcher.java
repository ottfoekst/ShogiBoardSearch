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
	 * @param searchCondition �ǖʃf�[�^��������
	 * @throws Exception
	 */
	public void searchBoardData(PiecePosition piecePosition, boolean isPerfectMatch, float similarSearchThreshold) throws Exception {
		// ���S��v�����̂Ƃ�
		if(isPerfectMatch || (!isPerfectMatch && similarSearchThreshold == 1.0f)) {
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

	private void searchBoardBySimilarSearch(PiecePosition piecePosition, float similarSearchThreshold) {
		// TODO ����
		
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
}
