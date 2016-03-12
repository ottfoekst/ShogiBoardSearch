package org.petanko.ottfoekst.boardsrch.indexer;

import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.util.ShogiPieceUtils;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.board.ShogiPiece;

public class IndexerUtils {
	
	/** ����ID�t�@�C���� */
	public static final String KIFUID_FILE_NAME = "bs.kifuid";
	/** ����ID�|�C���^�t�@�C���� */
	public static final String KIFUID_PTR_FILE_NAME = "bs.kifuid.ptr";
	/** �ǖʓ]�u�C���f�b�N�X�t�@�C���� */
	public static final String BOARD_INV_FILE_NAME = "bs.board.inv";
	/** �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���� */
	public static final String BOARD_INV_PTR_FILE_NAME = "bs.board.inv.ptr";

	/**
	 * ����ID�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @return ����ID�t�@�C���̃p�X
	 */
	public static Path getKifuIdFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_FILE_NAME);
	}
	
	/**
	 * ����ID�|�C���^�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @return ����ID�|�C���^�t�@�C���̃p�X
	 */
	public static Path getKifuIdPtrFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_PTR_FILE_NAME);
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @param blockNo �ǖʃu���b�N�ԍ�
	 * @return �ǖʓ]�u�C���f�b�N�X�t�@�C���̃p�X
	 */
	public static Path getBoardInvFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_FILE_NAME + "." + blockNo);
	}
	
	/**
	 * �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���̃p�X��Ԃ��܂��B
	 * @param indexDir �C���f�b�N�X���i�[����p�X
	 * @param blockNo �ǖʃu���b�N�ԍ�
	 * @return �ǖʓ]�u�C���f�b�N�X�|�C���^�t�@�C���̃p�X
	 */
	public static Path getBoardInvPtrFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_PTR_FILE_NAME + "." + blockNo);
	}
	
	/**
	 * �ǖʃg�[�N��ID���v�Z���ĕԂ��܂��B
	 * @param piecePosition �ǖ�
	 * @param blockNo�@�Ֆʃf�[�^�̂ǂ̕�����\�������������l{@see BoardDataToken}
	 * @return�@�ǖʃg�[�N��ID
	 */
	public static long calculateBoardTokenId(PiecePosition piecePosition, int blockNo) {
		
		// �ǖʃg�[�N��ID
		long boardTokenId = 0;
		
		// �Ֆʂ̈ꕔ����\���Ƃ�
		if(blockNo != 49) {
			int[] board = piecePosition.getBoard();
			
			int startDan = blockNo / 7 + 1;
			int startSuji = 9 - blockNo % 7;
			for(int dan = startDan; dan < startDan + 3; dan++ ) {
				for(int suji = startSuji; suji > startSuji - 3; suji--) {
					boardTokenId += convertPieceFromShogiPieceToShogiPieceUtils(board[dan + suji * 0x10]);
					boardTokenId *= 33; // �ő���̗�(ShogiPieceUtils.ERY)��32�Ȃ̂�33�i���ŕ\��
				}
			}
		}
		// ���̎������\���Ƃ�
		else {
			int[] handPieces = piecePosition.getHandPieces();
			for(int piece = ShogiPiece.SHI; piece >= ShogiPiece.SFU; piece--) {
				boardTokenId += handPieces[piece];
				boardTokenId *= 19; // ���͍ő�18��������ɂ��̂�19�i���ŕ\��
			}
		}
		return boardTokenId;
	}

	private static long convertPieceFromShogiPieceToShogiPieceUtils(int pieceInShogiPiece) {
		switch(pieceInShogiPiece) {
		case ShogiPiece.EMP : return ShogiPieceUtils.EMP;
		case ShogiPiece.SFU : return ShogiPieceUtils.SFU;
		case ShogiPiece.STO : return ShogiPieceUtils.STO;
		case ShogiPiece.SKY : return ShogiPieceUtils.SKY;
		case ShogiPiece.SNY : return ShogiPieceUtils.SNY;
		case ShogiPiece.SKE : return ShogiPieceUtils.SKE;
		case ShogiPiece.SNK : return ShogiPieceUtils.SNK;
		case ShogiPiece.SGI : return ShogiPieceUtils.SGI;
		case ShogiPiece.SNG : return ShogiPieceUtils.SNG;
		case ShogiPiece.SKI : return ShogiPieceUtils.SKI;
		case ShogiPiece.SKA : return ShogiPieceUtils.SKA;
		case ShogiPiece.SUM : return ShogiPieceUtils.SUM;
		case ShogiPiece.SHI : return ShogiPieceUtils.SHI;
		case ShogiPiece.SRY : return ShogiPieceUtils.SRY;
		case ShogiPiece.SOU : return ShogiPieceUtils.SOU;
		case ShogiPiece.EFU : return ShogiPieceUtils.EFU;
		case ShogiPiece.ETO : return ShogiPieceUtils.ETO;
		case ShogiPiece.EKY : return ShogiPieceUtils.EKY;
		case ShogiPiece.ENY : return ShogiPieceUtils.ENY;
		case ShogiPiece.EKE : return ShogiPieceUtils.EKE;
		case ShogiPiece.ENK : return ShogiPieceUtils.ENK;
		case ShogiPiece.EGI : return ShogiPieceUtils.EGI;
		case ShogiPiece.ENG : return ShogiPieceUtils.ENG;
		case ShogiPiece.EKI : return ShogiPieceUtils.EKI;
		case ShogiPiece.EKA : return ShogiPieceUtils.EKA;
		case ShogiPiece.EUM : return ShogiPieceUtils.EUM;
		case ShogiPiece.EHI : return ShogiPieceUtils.EHI;
		case ShogiPiece.ERY : return ShogiPieceUtils.ERY;
		case ShogiPiece.EOU : return ShogiPieceUtils.EOU;		
		default : throw new IllegalArgumentException(); // �����~�X���������ɂ͓��B���Ȃ�
		}
	}
}
