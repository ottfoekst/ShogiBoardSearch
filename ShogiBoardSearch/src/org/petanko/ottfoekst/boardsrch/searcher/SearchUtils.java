package org.petanko.ottfoekst.boardsrch.searcher;

import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.util.KifFileUtils;
import org.petanko.ottfoekst.petankoshogi.board.PieceMove;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.util.ShogiUtils;

/**
 * 局面検索で利用するメソッドを集めたクラス。
 * @author ottfoekst
 */
public class SearchUtils {

	/**
	 * 棋譜ファイルのパスと手数から、該当する局面を返します。
	 * @param kifuFilePath 棋譜ファイルのパス
	 * @param tesu 手数
	 * @return 局面(PiecePosition)
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
