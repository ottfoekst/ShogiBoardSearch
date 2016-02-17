package org.petanko.ottfoekst.boardsrch.util;

/**
 * ������Ɋւ���萔�E���\�b�h���W�߂��N���X�B
 * @author ottfoekst
 *
 */
public class ShogiPieceUtils {

	/** ��Ȃ��}�X��\�����l*/
	public static final int EMPTY = 0, EMP = 0;
	/**�@�����\�����l */
	public static final int PROMOTED = 8;
	/** ���̋��\�����l */
	public static final int ENEMY = 16;

	/** ���̕� */
	public static final int SFU = 1;
	/** ���̍� */
	public static final int SKY = 2;
	/** ���̌j */
	public static final int SKE = 3;
	/** ���̋� */
	public static final int SGI = 4;
	/** ���̋� */
	public static final int SKI = 5;
	/** ���̊p */
	public static final int SKA = 6;
	/** ���̔� */
	public static final int SHI = 7;
	/** ���̋� */
	public static final int SOU = 8;
	/** ���̂� */
	public static final int STO = SFU + PROMOTED;
	/** ���̐��� */
	public static final int SNY = SKY + PROMOTED;
	/** ���̐��j */
	public static final int SNK = SKE + PROMOTED;
	/** ���̐��� */
	public static final int SNG = SGI + PROMOTED;
	/** ���̔n */
	public static final int SUM = SKA + PROMOTED;
	/** ���̗� */
	public static final int SRY = SHI + PROMOTED;
	
	/** ���̕� */
	public static final int EFU = SFU + ENEMY;
	/** ���̍� */
	public static final int EKY = SKY + ENEMY;
	/** ���̌j */
	public static final int EKE = SKE + ENEMY;
	/** ���̋� */
	public static final int EGI = SGI + ENEMY;
	/** ���̋� */
	public static final int EKI = SKI + ENEMY;
	/** ���̊p */
	public static final int EKA = SKA + ENEMY;
	/** ���̔� */
	public static final int EHI = SHI + ENEMY;
	/** ���̋� */
	public static final int EOU = SOU + ENEMY;
	/** ���̂� */
	public static final int ETO = EFU + PROMOTED;
	/** ���̐��� */
	public static final int ENY = EKY + PROMOTED;
	/** ���̐��j */
	public static final int ENK = EKE + PROMOTED;
	/** ���̐��� */
	public static final int ENG = EGI + PROMOTED;
	/** ���̔n */
	public static final int EUM = EKA + PROMOTED;
	/** ���̗� */
	public static final int ERY = EHI + PROMOTED;
	
}
