package org.petanko.ottfoekst.boardsrch.main;

import java.io.File;
import java.nio.file.Path;

import org.petanko.ottfoekst.boardsrch.searcher.BoardDataSearcher;
import org.petanko.ottfoekst.boardsrch.searcher.SearchUtils;

public class ShogiBoardSearch {

	/**
	 * ShogiBoardSearchÌCNXB
	 * @param args[0] CfbNXªi[³ê½pX
	 * @param args[1] ût@CÌtpX
	 * @param args[2] è
	 * @param args[3] õû®(0:êvÇÊõA1:ÞÇÊõ)
	 * @param args[4] ÞÇÊõÌXRAèl(0`100)
	 */
	public static void main(String[] args) {

		try {
			// øÌÏ·
			Path indexDir = new File(args[0]).toPath();
			Path kifuFilePath = new File(args[1]).toPath();
			int tesu = Integer.parseInt(args[2]);
			int searchType = Integer.parseInt(args[3]);
			int similarSearchThreshold = Integer.parseInt(args[4]);

			// õõ
			BoardDataSearcher boardDataSearcher = new BoardDataSearcher(indexDir);
			// õ
			boardDataSearcher.searchBoardData(SearchUtils.convertToPiecePosition(kifuFilePath, tesu), searchType != 1, similarSearchThreshold);

			// õI¹
			boardDataSearcher.shutDown();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
