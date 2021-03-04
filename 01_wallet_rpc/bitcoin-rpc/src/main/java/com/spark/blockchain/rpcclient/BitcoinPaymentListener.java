package com.spark.blockchain.rpcclient;

import com.spark.blockchain.rpcclient.Bitcoin.Transaction;

public interface BitcoinPaymentListener {
    void block(String var1);

    void transaction(Transaction var1);
}
