package org.petanko.ottfoekst.boardsrch.indexer;

import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.ENEMY;
import static org.petanko.ottfoekst.petankoshogi.board.ShogiPiece.SELF;

import java.io.DataOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.petanko.ottfoekst.boardsrch.util.IoUtils;
import org.petanko.ottfoekst.petankoshogi.board.PieceMove;
import org.petanko.ottfoekst.petankoshogi.board.PiecePosition;
import org.petanko.ottfoekst.petankoshogi.board.ShogiPiece;
import org.petanko.ottfoekst.petankoshogi.util.ShogiUtils;

/**
 * 局面転置インデックスを生成するクラス。
 * @author ottfoekst
 *
 */
public class BoardDataIndexer {

	/** インデックスを格納するパス */
	private Path indexDir;
	/** 棋譜ファイルが格納されたパス */
	private Path kifuDataPath;
	
	/** 棋譜ID */
	private int kifuId = 0;
	
	/** 棋譜IDファイル書き込み */
	private DataOutputStream kifuIdDos;
	/** 棋譜IDポインタファイル書き込み */
	private DataOutputStream kifuIdPtrDos;
	/** 棋譜IDファイルのポインタ */
	private long kifuIdFilePtr = 0;
	
	/** 1手前の移動先マス(to) */
	private int beforeTo = 0;
	
	/** 局面転置インデックスファイル書き込み */
	private DataOutputStream[] boardInvDosList;
	/** 局面転置インデックスポインタファイル書き込み */
	private DataOutputStream[] boardInvPtrDosList;
	
	/**　局面転置インデックスの書き込み */
	private BoardDataIndexWriter[] boardDataIndexWriterList;
	
	/** kifファイルの内部ユーティリティクラス */
	private KifFileUtils kifFileUtils = new KifFileUtils();
	
	/**
	 * コンストラクタ。
	 * @param indexDir インデックスを格納するパス
	 * @param kifuDataPath 棋譜ファイルが格納されたパス
	 */
	public BoardDataIndexer(Path indexDir, Path kifuDataPath) {
		this.indexDir = indexDir;
		this.kifuDataPath = kifuDataPath;
	}
	
	/**
	 * 局面データのインデックスを生成します。
	 * @throws Exception
	 */
	public void generateIndex() throws Exception {
				
		try {
			// 各種数値の初期化
			initializeAllNumList();
			// 各種DataOutputStreamやWriterの初期化
			initializeDosListAndWriterList();
			
			// メモリ上に転置インデックスを構築
			createInvIndexOnMemory(kifuDataPath.toFile());
			// 転置インデックスの書き出し
			for(int blockNo = 0; blockNo < 50; blockNo++ ) {
				boardDataIndexWriterList[blockNo].writeToIndexFile(boardInvDosList[blockNo], boardInvPtrDosList[blockNo]);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			// 各種DataOutputStreamを閉じる
			closeDosList();
		}
	}

	private void initializeAllNumList() {
		kifuId = 0;
		kifuIdFilePtr = 0;
	}

	private void initializeDosListAndWriterList() throws Exception {
		kifuIdDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdFilePath(indexDir));
		kifuIdPtrDos = IoUtils.newDataOutputStream(IndexerUtils.getKifuIdPtrFilePath(indexDir));
		
		boardInvDosList = new DataOutputStream[50];
		boardInvPtrDosList = new DataOutputStream[50];
		boardDataIndexWriterList = new BoardDataIndexWriter[50];
		for(int blockNo = 0; blockNo < boardInvDosList.length; blockNo++) {
			boardInvDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvFilePath(indexDir, blockNo));
			boardInvPtrDosList[blockNo] = IoUtils.newDataOutputStream(IndexerUtils.getBoardInvPtrFilePath(indexDir, blockNo));
			boardDataIndexWriterList[blockNo] = new BoardDataIndexWriter();
		}
	}

	private void createInvIndexOnMemory(File kifuDataFileOrFolder) throws Exception {
		// フォルダのとき
		if(kifuDataFileOrFolder.isDirectory()) {
			File[] files = kifuDataFileOrFolder.listFiles();
			for(File file : files) {
				createInvIndexOnMemory(file);
			}
		}
		// 棋譜ファイル(拡張子:kif)のとき
		else if(kifuDataFileOrFolder.toString().endsWith(".kif")) {
			// 棋譜IDポインタファイルへの書き込み
			kifuIdPtrDos.writeInt(kifuId); // 棋譜ID
			kifuIdPtrDos.writeLong(kifuIdFilePtr); // 棋譜IDファイルのポインタ
			kifuIdPtrDos.flush();
			// 棋譜IDファイルへの書き込み
			kifuIdFilePtr += writeKifuIdFile(kifuDataFileOrFolder);
			
			// 棋譜ファイルを読み込む
			String[] kifuDataList = Files.readAllLines(kifuDataFileOrFolder.toPath(), Charset.forName("MS932")).toArray(new String[0]);
			// 棋譜ファイルの内容に従って盤面を動かし、局面をインデックスに登録
			addBoardDataToInvIndex(kifuDataList);
			
			// 棋譜IDのカウントアップ
			kifuId++;
		}
	}

	private long writeKifuIdFile(File kifuDataFileOrFolder) throws Exception {
		// 棋譜ファイル名
		String kifuFileName = kifuDataFileOrFolder.getAbsolutePath();
		// 棋譜ファイル名を棋譜IDファイルに書き込む
		kifuIdDos.writeChars(kifuFileName);
		kifuIdDos.flush();
		
		// 棋譜ファイル名の長さを返す
		return kifuFileName.length();
	}

	private void addBoardDataToInvIndex(String[] kifuDataList) {
		// 指し手リスト
		PieceMove[] pieceMoveList = kifFileUtils.createPieceMoveListFromKifFile(kifuDataList);
		
		// 平手の初期局面
		PiecePosition piecePosition = ShogiUtils.getHiratePiecePosition();
		// 初期局面をインデックスに登録
		IntStream.range(0, 50).forEach(blockNo -> boardDataIndexWriterList[blockNo].add(kifuId, piecePosition.getTesu(), IndexerUtils.calculateBoardTokenId(piecePosition, blockNo)));
		
		for(PieceMove pieceMove : pieceMoveList) {
			// 指し手に従って局面を動かす
			piecePosition.movePiecePostion(pieceMove);
			// 各局面トークンをインデックスに登録
			IntStream.range(0, 50).forEach(blockNo -> boardDataIndexWriterList[blockNo].add(kifuId, piecePosition.getTesu(), IndexerUtils.calculateBoardTokenId(piecePosition, blockNo)));
		}
	}

	private void closeDosList() {
		// 全てのDataOutputStreamを連結
		DataOutputStream[] allDosList = Stream.concat(Arrays.stream(new DataOutputStream[]{kifuIdDos, kifuIdPtrDos}), 
				Stream.concat(Arrays.stream(boardInvDosList), Arrays.stream(boardInvPtrDosList))).toArray(size -> new DataOutputStream[size]);
		// Exceptionをスローせずに全て閉じる
		IoUtils.closeSilently(allDosList);
	}
		
	/** kifファイルの内部ユーティリティクラス */
	private class KifFileUtils {
		
		private PieceMove[] createPieceMoveListFromKifFile(String[] kifuDataList) {
			List<PieceMove> pieceMoveList = new ArrayList<PieceMove>();
			
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
}
