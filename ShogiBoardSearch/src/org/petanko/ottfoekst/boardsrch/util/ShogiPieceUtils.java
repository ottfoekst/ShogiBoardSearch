package org.petanko.ottfoekst.boardsrch.util;

/**
 * 将棋駒に関する定数・メソッドを集めたクラス。
 * @author ottfoekst
 *
 */
public class ShogiPieceUtils {

	/** 駒がないマスを表す数値*/
	public static final int EMPTY = 0, EMP = 0;
	/**　成駒を表す数値 */
	public static final int PROMOTED = 8;
	/** 後手の駒を表す数値 */
	public static final int ENEMY = 16;

	/** 先手の歩 */
	public static final int SFU = 1;
	/** 先手の香 */
	public static final int SKY = 2;
	/** 先手の桂 */
	public static final int SKE = 3;
	/** 先手の銀 */
	public static final int SGI = 4;
	/** 先手の金 */
	public static final int SKI = 5;
	/** 先手の角 */
	public static final int SKA = 6;
	/** 先手の飛 */
	public static final int SHI = 7;
	/** 先手の玉 */
	public static final int SOU = 8;
	/** 先手のと */
	public static final int STO = SFU + PROMOTED;
	/** 先手の成香 */
	public static final int SNY = SKY + PROMOTED;
	/** 先手の成桂 */
	public static final int SNK = SKE + PROMOTED;
	/** 先手の成銀 */
	public static final int SNG = SGI + PROMOTED;
	/** 先手の馬 */
	public static final int SUM = SKA + PROMOTED;
	/** 先手の龍 */
	public static final int SRY = SHI + PROMOTED;
	
	/** 後手の歩 */
	public static final int EFU = SFU + ENEMY;
	/** 後手の香 */
	public static final int EKY = SKY + ENEMY;
	/** 後手の桂 */
	public static final int EKE = SKE + ENEMY;
	/** 後手の銀 */
	public static final int EGI = SGI + ENEMY;
	/** 後手の金 */
	public static final int EKI = SKI + ENEMY;
	/** 後手の角 */
	public static final int EKA = SKA + ENEMY;
	/** 後手の飛 */
	public static final int EHI = SHI + ENEMY;
	/** 後手の玉 */
	public static final int EOU = SOU + ENEMY;
	/** 後手のと */
	public static final int ETO = EFU + PROMOTED;
	/** 後手の成香 */
	public static final int ENY = EKY + PROMOTED;
	/** 後手の成桂 */
	public static final int ENK = EKE + PROMOTED;
	/** 後手の成銀 */
	public static final int ENG = EGI + PROMOTED;
	/** 後手の馬 */
	public static final int EUM = EKA + PROMOTED;
	/** 後手の龍 */
	public static final int ERY = EHI + PROMOTED;
	
}
