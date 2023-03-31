package uoc.ds.pr.util;

import uoc.ds.pr.SportEvents4Club;

public class LevelHelper {
    public static SportEvents4Club.Level getLevel(int numValoraciones) {
        if (numValoraciones < 2) {
            return SportEvents4Club.Level.ROOKIE;
        } else if (numValoraciones < 5) {
            return SportEvents4Club.Level.PRO;
        } else if (numValoraciones < 10) {
            return SportEvents4Club.Level.EXPERT;
        } else if (numValoraciones < 15) {
            return SportEvents4Club.Level.MASTER;
        } else {
            return SportEvents4Club.Level.LEGEND;
        }
    }
}
