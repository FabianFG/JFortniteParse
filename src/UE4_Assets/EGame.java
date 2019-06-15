/**
 * 
 */
package UE4_Assets;

/**
 * @author FunGames
 *
 */
public class EGame {
	
	public static int 
	
	GAME_UNKNOWN = 0,
	
	GAME_UE4_BASE = 0x1000000,
			// bytes: 01.00.0N.NX : 01=UE4, 00=masked by GAME_ENGINE, NN=UE4 subversion, X=game (4 bits, 0=base engine)
			// Add custom UE4 game engines here
			// 4.23
			GAME_Fortnite = GAME_UE4(23),
	
	GAME_ENGINE = 0xFFF0000; //mask for game engine
	
	
	public static final int LATEST_SUPPORTED_UE4_VERSION = 23; 
	
	
	public static int GAME_UE4(int x) {
		return (GAME_UE4_BASE + (x << 4));
	}
	public static int GAME_UE4_GET_MINOR(int x) {
		return ((x - GAME_UE4_BASE) >> 4);
	}
	
}
