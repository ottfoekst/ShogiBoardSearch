package org.petanko.ottfoekst.boardsrch.indexer;

import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.util.ShogiPieceUtils;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.board.ShogiPiece;

public class IndexerUtils {
	
	/** 棋譜IDファイル名 */
	public static final String KIFUID_FILE_NAME = "bs.kifuid";
	/** 棋譜IDポインタファイル名 */
	public static final String KIFUID_PTR_FILE_NAME = "bs.kifuid.ptr";
	/** 局面転置インデックスファイル名 */
	public static final String BOARD_INV_FILE_NAME = "bs.board.inv";
	/** 局面転置インデックスポインタファイル名 */
	public static final String BOARD_INV_PTR_FILE_NAME = "bs.board.inv.ptr";

	/**
	 * 棋譜IDファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @return 棋譜IDファイルのパス
	 */
	public static Path getKifuIdFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_FILE_NAME);
	}
	
	/**
	 * 棋譜IDポインタファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @return 棋譜IDポインタファイルのパス
	 */
	public static Path getKifuIdPtrFilePath(Path indexDir) {
		return indexDir.resolve(KIFUID_PTR_FILE_NAME);
	}
	
	/**
	 * 局面転置インデックスファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @param blockNo 局面ブロック番号
	 * @return 局面転置インデックスファイルのパス
	 */
	public static Path getBoardInvFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_FILE_NAME + "." + blockNo);
	}
	
	/**
	 * 局面転置インデックスポインタファイルのパスを返します。
	 * @param indexDir インデックスを格納するパス
	 * @param blockNo 局面ブロック番号
	 * @return 局面転置インデックスポインタファイルのパス
	 */
	public static Path getBoardInvPtrFilePath(Path indexDir, int blockNo) {
		return indexDir.resolve(BOARD_INV_PTR_FILE_NAME + "." + blockNo);
	}
	
	/**
	 * 局面トークンIDを計算して返します。
	 * @param piecePosition 局面
	 * @param blockNo　盤面データのどの部分を表すかを示す数値{@see BoardDataToken}
	 * @return　局面トークンID
	 */
	public static long calculateBoardTokenId(PiecePosition piecePosition, int blockNo) {
		
		// 局面トークンID
		long boardTokenId = 0;
		
		// 盤面の一部分を表すとき
		if(blockNo != 49) {
			int[] board = piecePosition.getBoard();
			
			int startDan = blockNo / 7 + 1;
			int startSuji = 9 - blockNo % 7;
			for(int dan = startDan; dan < startDan + 3; dan++ ) {
				for(int suji = startSuji; suji > startSuji - 3; suji--) {
					boardTokenId += convertPieceFromShogiPieceToShogiPieceUtils(board[dan + suji * 0x10]);
					boardTokenId *= 33; // 最大後手の龍(ShogiPieceUtils.ERY)が32なので33進数で表す
				}
			}
		}
		// 先手の持ち駒を表すとき
		else {
			int[] handPieces = piecePosition.getHandPieces();
			for(int piece = ShogiPiece.SHI; piece >= ShogiPiece.SFU; piece--) {
				boardTokenId += handPieces[piece];
				boardTokenId *= 19; // 歩は最大18枚持ち駒にもつので19進数で表す
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
		default : throw new IllegalArgumentException(); // 実装ミスしかここには到達しない
		}
	}
}
