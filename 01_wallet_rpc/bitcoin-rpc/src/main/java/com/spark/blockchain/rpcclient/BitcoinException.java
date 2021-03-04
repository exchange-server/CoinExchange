package com.spark.blockchain.rpcclient;

public class BitcoinException extends Exception {
    public BitcoinException() {
    }

    public BitcoinException(String msg) {
        super(msg);
    }

    public BitcoinException(Throwable cause) {
        super(cause);
    }

    public BitcoinException(String message, Throwable cause) {
        super(message, cause);
    }
}
