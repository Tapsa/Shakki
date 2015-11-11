package tapsa.shakki;

public enum Rank {
    R1, R2, R3, R4, R5, R6, R7, R8;

    public static Rank val(int i) {
        switch (i) {
            case 0:
                return R1;
            case 1:
                return R2;
            case 2:
                return R3;
            case 3:
                return R4;
            case 4:
                return R5;
            case 5:
                return R6;
            case 6:
                return R7;
            case 7:
                return R8;
            default:
                return R1;
        }
    }
}
