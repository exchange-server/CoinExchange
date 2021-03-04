package com.spark.blockchain.rpcclient;

import com.spark.blockchain.rpcclient.Bitcoin.Transaction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ConfirmedPaymentListener extends SimpleBitcoinPaymentListener {
    public int minConf;
    protected Set<String> processed;

    public ConfirmedPaymentListener(int minConf) {
        this.processed = Collections.synchronizedSet(new HashSet());
        this.minConf = minConf;
    }

    public ConfirmedPaymentListener() {
        this(6);
    }

    protected boolean markProcess(String txId) {
        return this.processed.add(txId);
    }

    public void transaction(Transaction transaction) {
        if (transaction.confirmations() >= this.minConf) {
            if (this.markProcess(transaction.txId() + "-" + transaction.address())) {
                this.confirmed(transaction);
            }
        }
    }

    public abstract void confirmed(Transaction var1);
}
