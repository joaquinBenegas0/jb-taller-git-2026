package py.edu.uc.lp3.jb_taller_git_2026.cs2;

/**
 * Modos de disparo del rifle. Cada modo sabe cuántas balas gasta por disparo,
 * así el rifle no necesita un if/switch por modo.
 */
public enum ModoDisparo {
    SEMI(1),
    RAFAGA(3),
    AUTOMATICO(5);

    private final int balasPorDisparo;

    ModoDisparo(int balasPorDisparo) {
        this.balasPorDisparo = balasPorDisparo;
    }

    public int balasPorDisparo() {
        return balasPorDisparo;
    }
}
