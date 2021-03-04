package com.bizzan.bitrade.constant;

public enum WalletType {
    SPOT(0, "币币钱包"),
    SWAP(1, "合约钱包");

    WalletType(int number, String description) {
        this.code = number;
        this.description = description;
    }
    private int code;
    private String description;
    public int getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }
}
