package org.petanko.ottfoekst.boardsrch.searcher;

/**
 * 局面データ検索条件クラス。
 * @author ottfoekst
 *
 */
public class BoardSearchCondition {
	
	/** 盤面文字列(SFEN形式) */
	private String sfenBoardString;
	/** 完全一致検索かどうか */
	private boolean isPerfectMatch;
	/** 類似局面検索時のスコアの閾値 */
	private float similarSearchThreshold;
	
	/**
	 * コンストラクタ。
	 * 完全一致検索のとき(isPerfectMatch=true)、similarSearchThresholdは常に1.0が指定されます。
	 * @param sfenBoardString　盤面文字列(SFEN形式)
	 * @param isPerfectMatch　完全一致検索かどうか
	 * @param similarSearchThreshold　類似局面検索時のスコアの閾値
	 */
	public BoardSearchCondition(String sfenBoardString, boolean isPerfectMatch, float similarSearchThreshold) {
		this.sfenBoardString = sfenBoardString;
		this.isPerfectMatch = isPerfectMatch;
		if(isPerfectMatch) {
			this.similarSearchThreshold = 1.0f;
		}
		else {
			this.similarSearchThreshold = similarSearchThreshold;
		}
	}
	
	/**
	 * 盤面文字列(SFEN形式)を返します。
	 * @return 盤面文字列(SFEN形式)
	 */
	public String getSfenBoardString() {
		return sfenBoardString;
	}
	
	/**
	 * 完全一致検索かどうかを返します。
	 * @return　完全一致検索かどうか
	 */
	public boolean isPerfectMatch() {
		return isPerfectMatch;
	}
	
	/**
	 * 類似局面検索時のスコアの閾値を返します。
	 * @return 類似局面検索時のスコアの閾値
	 */
	public float getSimilarSearchThreshold() {
		return similarSearchThreshold;
	}
}
