package org.petanko.ottfoekst.boardsrch.searcher;

import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.index.BoardDataIndex;
import org.petanko.ottfoekst.boardsrch.index.KifuIdIndex;

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
	 */
	public void searchBoardData(BoardSearchCondition searchCondition) {
		// ���S��v�����̂Ƃ�
		if(searchCondition.isPerfectMatch()) {
			searchBoardByPerfectMatch(searchCondition);
		}
		// �ގ��ǖʌ����̂Ƃ�
		else {
			searchBoardBySimilarSearch(searchCondition, searchCondition.getSimilarSearchThreshold());
		}
	}
	
	private void searchBoardByPerfectMatch(BoardSearchCondition searchCondition) {
		// TODO ����
	}
	
	private void searchBoardBySimilarSearch(BoardSearchCondition searchCondition, float similarSearchThreshold) {
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
