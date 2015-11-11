package tapsa.shakki;

public enum File {
    A, B, C, D, E, F, G, H;

    public static File val(int i) {
        switch (i) {
            case 0:
                return A;
            case 1:
                return B;
            case 2:
                return C;
            case 3:
                return D;
            case 4:
                return E;
            case 5:
                return F;
            case 6:
                return G;
            case 7:
                return H;
            default:
                return A;
        }
    }
}
