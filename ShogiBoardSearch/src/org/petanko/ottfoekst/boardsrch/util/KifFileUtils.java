package org.petanko.ottfoekst.boardsrch.util;

import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.ENEMY;
import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.SELF;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.petanko.ottfoekst.petankoshogi.board.PieceMove;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.board.ShogiPiece;
import org.petanko.ottfoekst.petankoshogi.util.ShogiUtils;

/**
 * kifファイルのユーティリティクラス。
 * @author ottfoekst
 *
 */
public class KifFileUtils {
	
	/** 1手前の移動先マス(to) */
	private int beforeTo = 0;
	
	/**
	 * kifファイルを読み込み、指し手リストを返します。kifファイルの文字コードはShift_JIS固定です。
	 * @param kifuFilePath kifファイルのパス
	 * @return 指し手リスト(PieceMove[])
	 * @throws Exception
	 */
	public PieceMove[] createPieceMoveListFromKifFile(Path kifuFilePath) throws Exception {
		List<PieceMove> pieceMoveList = new ArrayList<PieceMove>();
		
		// 棋譜ファイルを読み込む
		String[] kifuDataList = Files.readAllLines(kifuFilePath, Charset.forName("MS932")).toArray(new String[0]);
		// 平手の初期局面
		PiecePosition piecePosition = ShogiUtils.getHiratePiecePosition();
		// 手数
		int tesu = 1;
		for(String kifuData : kifuDataList) {
			String[] kifuAndFrom;
			// 棋譜の書かれた行のとき
			if(kifuData.length() > 0 && kifuData.charAt(0) == (' ') && !kifuData.contains("投了") && !kifuData.contains("中断")) {
				// 棋譜を取得
				kifuAndFrom = 
						new String[]{kifuData.substring(5, kifuData.indexOf('(')).trim(), kifuData.substring(kifuData.indexOf("(") + 1, kifuData.indexOf(")"))};
				// PieceMoveに変換してリストに追加
				PieceMove pieceMove = convertToPieceMove(kifuAndFrom, piecePosition, tesu);
				pieceMoveList.add(pieceMove);
				// PieceMoveに従って局面をすすめる
				piecePosition.movePiecePostion(pieceMove);
				// 手数を1増やす
				tesu++;
			}
		}
		return pieceMoveList.toArray(new PieceMove[0]);
	}

	private PieceMove convertToPieceMove(String[] kifuAndFrom, PiecePosition piecePosition, int tesu) {
		// アラビア数字を使った棋譜に変換
		kifuAndFrom = convertToArabicNumKifu(kifuAndFrom);
		
		int from = kifuAndFrom[0].contains("打") ? 0 : Character.getNumericValue(kifuAndFrom[1].charAt(0)) * 0x10 + Character.getNumericValue(kifuAndFrom[1].charAt(1));
		int to = getToPosFromKifu(kifuAndFrom[0]);
		int piece = (from == 0) ? getDroppedPiece(kifuAndFrom[0], tesu) : piecePosition.getBoard()[from];
		int capturePiece = piecePosition.getBoard()[to];
		boolean isPromote = kifuAndFrom[0].endsWith("成");
		
		// beforeToの置き換え
		beforeTo = (to / 0x10) * 10 + to % 0x10;
		
		return new PieceMove(from, to, piece, capturePiece, isPromote);
	}

	private String[] convertToArabicNumKifu(String[] kifuAndFrom) {
		return new String[]{convertToArabicNumKifu(kifuAndFrom[0]), kifuAndFrom[1]};
	}
	
	private String convertToArabicNumKifu(String kifu) {
		StringBuilder buf = new StringBuilder();
		int arabicNum = -1;
		for(int charIndex = 0; charIndex < kifu.length(); charIndex++) {
			char c = kifu.charAt(charIndex);
			// 「同」のとき
			if(c == '同') {
				buf.append(beforeTo);
			}
			// 全角数字 or 漢数字のとき
			else if((arabicNum = ShogiUtils.getArabicNum(c)) != -1) {
				buf.append(arabicNum);
			}
			// スペースのとき
			else if(c == ' ' || c == '　') {
				continue;
			}
			// それ以外
			else {
				buf.append(c);
			}
		}
		return buf.toString();
	}
	
	private int getToPosFromKifu(String kifu) {
		return Character.getNumericValue(kifu.charAt(0)) * 0x10 + Character.getNumericValue(kifu.charAt(1));
	}

	private int getDroppedPiece(String kifu, int tesu) {
		int piece = 0;
		for(int index = 0; index < ShogiPiece.komaStrForKind.length; index++) {
			if(kifu.indexOf(ShogiPiece.komaStrForKind[index]) != -1) {
				piece = index;
				break;
			}
		}
		int turn = (tesu % 2 == 1) ? SELF : ENEMY;
		
		return (piece&~SELF&~ENEMY)|turn;
	}
}
