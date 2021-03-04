package com.spark.blockchain.rpcclient;

import com.spark.blockchain.rpcclient.Bitcoin.Block;
import com.spark.blockchain.rpcclient.Bitcoin.Transaction;
import com.spark.blockchain.rpcclient.Bitcoin.TransactionsSinceBlock;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BitcoinAcceptor implements Runnable {
    private static final Logger logger = Logger.getLogger(BitcoinAcceptor.class.getCanonicalName());
    public final Bitcoin bitcoin;
    private String lastBlock;
    private String monitorBlock;
    int monitorDepth;
    private final LinkedHashSet<BitcoinPaymentListener> listeners;
    private HashSet<String> seen;
    private boolean stop;
    private long checkInterval;

    public BitcoinAcceptor(Bitcoin bitcoin, String lastBlock, int monitorDepth) {
        this.monitorBlock = null;
        this.listeners = new LinkedHashSet();
        this.seen = new HashSet();
        this.stop = false;
        this.checkInterval = 5000L;
        this.bitcoin = bitcoin;
        this.lastBlock = lastBlock;
        this.monitorDepth = monitorDepth;
    }

    public BitcoinAcceptor(Bitcoin bitcoin) {
        this(bitcoin, (String)null, 6);
    }

    public BitcoinAcceptor(Bitcoin bitcoin, String lastBlock, int monitorDepth, BitcoinPaymentListener listener) {
        this(bitcoin, lastBlock, monitorDepth);
        this.listeners.add(listener);
    }

    public BitcoinAcceptor(Bitcoin bitcoin, BitcoinPaymentListener listener) {
        this(bitcoin, (String)null, 12);
        this.listeners.add(listener);
    }

    public String getAccountAddress(String account) throws BitcoinException {
        List<String> a = this.bitcoin.getAddressesByAccount(account);
        return a.isEmpty() ? this.bitcoin.getNewAddress(account) : (String)a.get(0);
    }

    public synchronized String getLastBlock() {
        return this.lastBlock;
    }

    public synchronized void setLastBlock(String lastBlock) throws BitcoinException {
        if (this.lastBlock != null) {
            throw new IllegalStateException("lastBlock already set");
        } else {
            this.lastBlock = lastBlock;
            this.updateMonitorBlock();
        }
    }

    public synchronized BitcoinPaymentListener[] getListeners() {
        return (BitcoinPaymentListener[])this.listeners.toArray(new BitcoinPaymentListener[0]);
    }

    public synchronized void addListener(BitcoinPaymentListener listener) {
        this.listeners.add(listener);
    }

    public synchronized void removeListener(BitcoinPaymentListener listener) {
        this.listeners.remove(listener);
    }

    private void updateMonitorBlock() throws BitcoinException {
        this.monitorBlock = this.lastBlock;

        for(int i = 0; i < this.monitorDepth && this.monitorBlock != null; ++i) {
            Block b = this.bitcoin.getBlock(this.monitorBlock);
            this.monitorBlock = b == null ? null : b.previousHash();
        }

    }

    public synchronized void checkPayments() throws BitcoinException {
        TransactionsSinceBlock t = this.monitorBlock == null ? this.bitcoin.listSinceBlock() : this.bitcoin.listSinceBlock(this.monitorBlock);
        Iterator var2 = t.transactions().iterator();

        while(true) {
            Transaction transaction;
            do {
                if (!var2.hasNext()) {
                    if (!t.lastBlock().equals(this.lastBlock)) {
                        this.seen.clear();
                        this.lastBlock = t.lastBlock();
                        this.updateMonitorBlock();
                        var2 = this.listeners.iterator();

                        while(var2.hasNext()) {
                            BitcoinPaymentListener listener = (BitcoinPaymentListener)var2.next();

                            try {
                                listener.block(this.lastBlock);
                            } catch (Exception var7) {
                                logger.log(Level.SEVERE, (String)null, var7);
                            }
                        }
                    }

                    return;
                }

                transaction = (Transaction)var2.next();
            } while(!"receive".equals(transaction.category()));

            Iterator var4 = this.listeners.iterator();

            while(var4.hasNext()) {
                BitcoinPaymentListener listener = (BitcoinPaymentListener)var4.next();

                try {
                    listener.transaction(transaction);
                } catch (Exception var8) {
                    logger.log(Level.SEVERE, (String)null, var8);
                }
            }
        }
    }

    public void stopAccepting() {
        this.stop = true;
    }

    public long getCheckInterval() {
        return this.checkInterval;
    }

    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }

    public void run() {
        this.stop = false;
        long nextCheck = 0L;

        while(!Thread.interrupted() && !this.stop) {
            if (nextCheck <= System.currentTimeMillis()) {
                try {
                    nextCheck = System.currentTimeMillis() + this.checkInterval;
                    System.out.println("check...");
                    this.checkPayments();
                } catch (BitcoinException var5) {
                    Logger.getLogger(BitcoinAcceptor.class.getName()).log(Level.SEVERE, (String)null, var5);
                }
            } else {
                try {
                    Thread.sleep(Math.max(nextCheck - System.currentTimeMillis(), 100L));
                } catch (InterruptedException var4) {
                    Logger.getLogger(BitcoinAcceptor.class.getName()).log(Level.WARNING, (String)null, var4);
                }
            }
        }

    }
}
