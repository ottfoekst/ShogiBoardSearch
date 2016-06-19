package org.petanko.ottfoekst.boardsrch.main;

import java.io.File;
import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.searcher.BoardDataSearcher;
import org.petanko.ottfoekst.boardsrch.searcher.SearchUtils;

public class ShogiBoardSearch {

	/**
	 * ShogiBoardSearchのメインクラス。
	 * @param args[0] インデックスが格納されたパス
	 * @param args[1] 棋譜ファイルのフルパス
	 * @param args[2] 手数
	 * @param args[3] 検索方式(0:一致局面検索、1:類似局面検索)
	 * @param args[4] 類似局面検索のスコア閾値(0～100)
	 */
	public static void main(String[] args) {

		try {
			// 引数の変換
			Path indexDir = new File(args[0]).toPath();
			Path kifuFilePath = new File(args[1]).toPath();
			int tesu = Integer.parseInt(args[2]);
			int searchType = Integer.parseInt(args[3]);
			int similarSearchThreshold = Integer.parseInt(args[4]);

			// 検索準備
			BoardDataSearcher boardDataSearcher = new BoardDataSearcher(indexDir);
			// 検索
			boardDataSearcher.searchBoardData(SearchUtils.convertToPiecePosition(kifuFilePath, tesu), searchType != 1, similarSearchThreshold);

			// 検索終了
			boardDataSearcher.shutDown();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
