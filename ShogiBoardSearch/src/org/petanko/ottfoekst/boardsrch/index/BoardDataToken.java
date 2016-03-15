package org.petanko.ottfoekst.boardsrch.index;

import java.util.Arrays;

/**
 * �ǖʃf�[�^�̃g�[�N���B
 * @author ottfoekst
 *
 */
public class BoardDataToken {
	
	/** �ǖʂ̊��S��v�����ł��g����blockNo�̃��X�g */
	private static final int[] BLOCKNO_LIST_USED_FOR_PERFECT_MATCH = new int[]{0, 3, 6, 21, 24, 27, 42, 45, 48, 50};

	/** �Ֆʃf�[�^�̂ǂ̕�����\�������������l�B0�`48:�Ֆʃf�[�^�A49�͐��̎�����f�[�^��\���B */
	private byte blockNo;
	/** �ǖʃg�[�N��ID */
	private long boardTokenId;
	
	/**
	 * �R���X�g���N�^�B
	 * @param blockNo �Ֆʃf�[�^�̂ǂ̕�����\�������������l
	 * @param boardTokenId �ǖʃg�[�N��ID
	 */
	public BoardDataToken(byte blockNo, long boardTokenId) {
		this.blockNo = blockNo;
		this.boardTokenId = boardTokenId;
	}
	
	/**
	 * �Ֆʃf�[�^�̂ǂ̕�����\�������������l��Ԃ��܂��B
	 * @return �Ֆʃf�[�^�̂ǂ̕�����\�������������l
	 */
	public byte getBlockNo() {
		return blockNo;
	}
	
	/** 
	 * �ގ��ǖʌ����ł����g���Ȃ��g�[�N�����ǂ�����Ԃ��܂��B
	 * @return �ގ��ǖʌ����ł����g���Ȃ��g�[�N�����ǂ���
	 */
	public boolean useOnlySimilarSearch() {
		return Arrays.binarySearch(BLOCKNO_LIST_USED_FOR_PERFECT_MATCH, blockNo) == -1;
	}
	
	/**
	 * �ǖʃg�[�N��ID��Ԃ��܂��B
	 * @return�@�ǖʃg�[�N��ID
	 */
	public long getBoardTokenId() {
		return boardTokenId;
	}
}
