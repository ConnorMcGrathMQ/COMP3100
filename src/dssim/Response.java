package dssim;

import dssim.response.*;

public abstract class Response {
    public static Response createResponse(String input) {
        String[] p = input.split(" ");
        switch (p[0]) {
            case "DATA":
                return new ResponseDATA(
                        Integer.parseInt(p[1]),
                        Integer.parseInt(p[2])
                );
            case "JOBN":
                return new ResponseJOBN(
                        Integer.parseInt(p[1]),
                        Integer.parseInt(p[2]),
                        Integer.parseInt(p[3]),
                        Integer.parseInt(p[4]),
                        Integer.parseInt(p[5]),
                        Integer.parseInt(p[6])
                );
            case "JOBP":
                return new ResponseJOBP(
                        Integer.parseInt(p[1]),
                        Integer.parseInt(p[2]),
                        Integer.parseInt(p[3]),
                        Integer.parseInt(p[4]),
                        Integer.parseInt(p[5]),
                        Integer.parseInt(p[6])
                );
            case "JCPL":
                return new ResponseJCPL(
                        Integer.parseInt(p[1]),
                        Integer.parseInt(p[2]),
                        p[3],
                        Integer.parseInt(p[4])
                );
            case "RESF":
                return new ResponseRESF(
                        p[1],
                        Integer.parseInt(p[2]),
                        Integer.parseInt(p[3])
                );
            case "RESR":
                return new ResponseRESR(
                        p[1],
                        Integer.parseInt(p[2]),
                        Integer.parseInt(p[3])
                );
            case "NONE":
                return new ResponseNONE();
            case "OK":
                return new ResponseOK();
            case "ERR:":
                return new ResponseERR(
                        input.substring(5) // "ERR: " + "actual message" -> discard first 5 chars
                );
            case ".":
                return new ResponseDOT();
            default:
                return null;
        }
    }

    public enum ResponseType {
        DATA,
        JOBN,
        JOBP,
        JCPL,
        RESF,
        RESR,
        NONE,
        OK,
        ERR,
        DOT
    }

    public static ResponseType type(Response r) {
        if (r.getClass() == ResponseDATA.class) {
            return ResponseType.DATA;
        } else if (r.getClass() == ResponseJOBN.class) {
            return ResponseType.JOBN;
        } else if (r.getClass() == ResponseJOBP.class) {
            return ResponseType.JOBP;
        } else if (r.getClass() == ResponseJCPL.class) {
            return ResponseType.JCPL;
        } else if (r.getClass() == ResponseRESF.class) {
            return ResponseType.RESF;
        } else if (r.getClass() == ResponseRESR.class) {
            return ResponseType.RESR;
        } else if (r.getClass() == ResponseNONE.class) {
            return ResponseType.NONE;
        } else if (r.getClass() == ResponseOK.class) {
            return ResponseType.OK;
        } else if (r.getClass() == ResponseERR.class) {
            return ResponseType.ERR;
        } else if (r.getClass() == ResponseDOT.class) {
            return ResponseType.DOT;
        } else {
            return null;
        }
    }
}