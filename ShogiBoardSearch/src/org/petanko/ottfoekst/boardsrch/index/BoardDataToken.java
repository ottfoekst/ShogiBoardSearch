package org.petanko.ottfoekst.boardsrch.index;

import java.util.Arrays;

/**
 * 局面データのトークン。
 * @author ottfoekst
 *
 */
public class BoardDataToken {
	
	/** 局面の完全一致検索でも使われるblockNoのリスト */
	private static final int[] BLOCKNO_LIST_USED_FOR_PERFECT_MATCH = new int[]{0, 3, 6, 21, 24, 27, 42, 45, 48, 50};

	/** 盤面データのどの部分を表すかを示す数値。0～48:盤面データ、49は先手の持ち駒データを表す。 */
	private byte blockNo;
	/** 局面トークンID */
	private long boardTokenId;
	
	/**
	 * コンストラクタ。
	 * @param blockNo 盤面データのどの部分を表すかを示す数値
	 * @param boardTokenId 局面トークンID
	 */
	public BoardDataToken(byte blockNo, long boardTokenId) {
		this.blockNo = blockNo;
		this.boardTokenId = boardTokenId;
	}
	
	/**
	 * 盤面データのどの部分を表すかを示す数値を返します。
	 * @return 盤面データのどの部分を表すかを示す数値
	 */
	public byte getBlockNo() {
		return blockNo;
	}
	
	/** 
	 * 類似局面検索でしか使われないトークンかどうかを返します。
	 * @return 類似局面検索でしか使われないトークンかどうか
	 */
	public boolean useOnlySimilarSearch() {
		return Arrays.binarySearch(BLOCKNO_LIST_USED_FOR_PERFECT_MATCH, blockNo) == -1;
	}
	
	/**
	 * 局面トークンIDを返します。
	 * @return　局面トークンID
	 */
	public long getBoardTokenId() {
		return boardTokenId;
	}
}
