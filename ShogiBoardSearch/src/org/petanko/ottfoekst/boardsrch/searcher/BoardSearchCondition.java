package org.petanko.ottfoekst.boardsrch.searcher;

/**
 * �ǖʃf�[�^���������N���X�B
 * @author ottfoekst
 *
 */
public class BoardSearchCondition {
	
	/** �Ֆʕ�����(SFEN�`��) */
	private String sfenBoardString;
	/** ���S��v�������ǂ��� */
	private boolean isPerfectMatch;
	/** �ގ��ǖʌ������̃X�R�A��臒l */
	private float similarSearchThreshold;
	
	/**
	 * �R���X�g���N�^�B
	 * ���S��v�����̂Ƃ�(isPerfectMatch=true)�AsimilarSearchThreshold�͏��1.0���w�肳��܂��B
	 * @param sfenBoardString�@�Ֆʕ�����(SFEN�`��)
	 * @param isPerfectMatch�@���S��v�������ǂ���
	 * @param similarSearchThreshold�@�ގ��ǖʌ������̃X�R�A��臒l
	 */
	public BoardSearchCondition(String sfenBoardString, boolean isPerfectMatch, float similarSearchThreshold) {
		this.sfenBoardString = sfenBoardString;
		this.isPerfectMatch = isPerfectMatch;
		if(isPerfectMatch) {
			this.similarSearchThreshold = 1.0f;
		}
		else {
			this.similarSearchThreshold = similarSearchThreshold;
		}
	}
	
	/**
	 * �Ֆʕ�����(SFEN�`��)��Ԃ��܂��B
	 * @return �Ֆʕ�����(SFEN�`��)
	 */
	public String getSfenBoardString() {
		return sfenBoardString;
	}
	
	/**
	 * ���S��v�������ǂ�����Ԃ��܂��B
	 * @return�@���S��v�������ǂ���
	 */
	public boolean isPerfectMatch() {
		return isPerfectMatch;
	}
	
	/**
	 * �ގ��ǖʌ������̃X�R�A��臒l��Ԃ��܂��B
	 * @return �ގ��ǖʌ������̃X�R�A��臒l
	 */
	public float getSimilarSearchThreshold() {
		return similarSearchThreshold;
	}
}
