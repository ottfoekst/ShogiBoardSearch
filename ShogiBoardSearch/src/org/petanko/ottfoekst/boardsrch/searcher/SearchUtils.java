package org.petanko.ottfoekst.boardsrch.searcher;

import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.util.KifFileUtils;
import org.petanko.ottfoekst.petankoshogi.board.PieceMove;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.util.ShogiUtils;

/**
 * �ǖʌ����ŗ��p���郁�\�b�h���W�߂��N���X�B
 * @author ottfoekst
 */
public class SearchUtils {

	/**
	 * �����t�@�C���̃p�X�Ǝ萔����A�Y������ǖʂ�Ԃ��܂��B
	 * @param kifuFilePath �����t�@�C���̃p�X
	 * @param tesu �萔
	 * @return �ǖ�(PiecePosition)
	 * @throws Exception
	 */
	public static PiecePosition convertToPiecePosition(Path kifuFilePath, int tesu) throws Exception {

		PiecePosition piecePosition = ShogiUtils.getHiratePiecePosition();
		PieceMove[] pieceMoveList = new KifFileUtils().createPieceMoveListFromKifFile(kifuFilePath);

		for(int tesuIndex = 0; tesuIndex < tesu; tesuIndex++) {
			piecePosition.movePiecePostion(pieceMoveList[tesuIndex]);
		}

		return piecePosition;
	}
}
