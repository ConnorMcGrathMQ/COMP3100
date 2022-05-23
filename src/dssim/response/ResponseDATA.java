package dssim.response;

import dssim.Response;

public class ResponseDATA extends Response {
    private int nRecs, recLen;

    public ResponseDATA(int nRecs, int recLen) {
        this.nRecs = nRecs;
        this.recLen = recLen;
    }

    public int getnRecs() {
        return nRecs;
    }

    public int getRecLen() {
        return recLen;
    }
}
