package dssim.response;

import dssim.Response;

public class ResponseERR extends Response {
    private String errorMessage;

    public ResponseERR(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
