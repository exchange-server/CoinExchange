package com.spark.blockchain.rpcclient;

import com.spark.blockchain.rpcclient.Bitcoin.Transaction;

public class SimpleBitcoinPaymentListener implements BitcoinPaymentListener {
    public SimpleBitcoinPaymentListener() {
    }

    public void block(String blockHash) {
    }

    public void transaction(Transaction transaction) {
    }
}
