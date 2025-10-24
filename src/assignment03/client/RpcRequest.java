package assignment03.client;

import java.io.Serializable;

public class RpcRequest implements Serializable {


    public static final String EMPTY_STRING = "";
    private final long serialVersionUID = 1L;

    private final String message;

    public RpcRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
