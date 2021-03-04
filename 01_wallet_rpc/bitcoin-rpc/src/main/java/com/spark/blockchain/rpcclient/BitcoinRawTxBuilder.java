package com.spark.blockchain.rpcclient;

import com.spark.blockchain.rpcclient.Bitcoin.BasicTxInput;
import com.spark.blockchain.rpcclient.Bitcoin.BasicTxOutput;
import com.spark.blockchain.rpcclient.Bitcoin.RawTransaction;
import com.spark.blockchain.rpcclient.Bitcoin.TxInput;
import com.spark.blockchain.rpcclient.Bitcoin.TxOutput;
import com.spark.blockchain.rpcclient.Bitcoin.Unspent;
import com.spark.blockchain.rpcclient.Bitcoin.RawTransaction.Out;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class BitcoinRawTxBuilder {
    public final Bitcoin bitcoin;
    public LinkedHashSet<TxInput> inputs = new LinkedHashSet();
    public List<TxOutput> outputs = new ArrayList();
    private HashMap<String, RawTransaction> txCache = new HashMap();

    public BitcoinRawTxBuilder(Bitcoin bitcoin) {
        this.bitcoin = bitcoin;
    }

    public BitcoinRawTxBuilder in(TxInput in) {
        this.inputs.add(new BitcoinRawTxBuilder.Input(in.txid(), in.vout()));
        return this;
    }

    public BitcoinRawTxBuilder in(String txid, int vout) {
        this.in(new BasicTxInput(txid, vout));
        return this;
    }

    public BitcoinRawTxBuilder out(String address, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return this;
        } else {
            this.outputs.add(new BasicTxOutput(address, amount));
            System.out.println("outputs=" + address + "," + amount.toPlainString());
            return this;
        }
    }

    public BitcoinRawTxBuilder in(double value) throws BitcoinException {
        return this.in(value, 6);
    }

    public BitcoinRawTxBuilder in(double value, int minConf) throws BitcoinException {
        List<Unspent> unspent = this.bitcoin.listUnspent(minConf);
        double v = value;
        Iterator var7 = unspent.iterator();

        while(var7.hasNext()) {
            Unspent o = (Unspent)var7.next();
            if (!this.inputs.contains(new BitcoinRawTxBuilder.Input(o))) {
                this.in(o);
                v = BitcoinUtil.normalizeAmount(v - o.amount().doubleValue());
            }

            if (v < 0.0D) {
                break;
            }
        }

        if (v > 0.0D) {
            throw new BitcoinException("Not enough bitcoins (" + v + "/" + value + ")");
        } else {
            return this;
        }
    }

    private RawTransaction tx(String txId) throws BitcoinException {
        RawTransaction tx = (RawTransaction)this.txCache.get(txId);
        if (tx != null) {
            return tx;
        } else {
            tx = this.bitcoin.getRawTransaction(txId);
            this.txCache.put(txId, tx);
            return tx;
        }
    }

    public BitcoinRawTxBuilder outChange(String address) throws BitcoinException {
        return this.outChange(address, 0.0D);
    }

    public BitcoinRawTxBuilder outChange(String address, double fee) throws BitcoinException {
        double is = 0.0D;

        TxInput i;
        for(Iterator var6 = this.inputs.iterator(); var6.hasNext(); is = BitcoinUtil.normalizeAmount(is + ((Out)this.tx(i.txid()).vOut().get(i.vout())).value())) {
            i = (TxInput)var6.next();
        }

        double os = fee;

        TxOutput o;
        for(Iterator var8 = this.outputs.iterator(); var8.hasNext(); os = BitcoinUtil.normalizeAmount(os + o.amount().doubleValue())) {
            o = (TxOutput)var8.next();
        }

        if (os < is) {
            this.out(address, new BigDecimal(BitcoinUtil.normalizeAmount(is - os)));
        }

        return this;
    }

    public String create() throws BitcoinException {
        return this.bitcoin.createRawTransaction(new ArrayList(this.inputs), this.outputs);
    }

    public String sign() throws BitcoinException {
        return this.bitcoin.signRawTransaction(this.create());
    }

    public String send() throws BitcoinException {
        return this.bitcoin.sendRawTransaction(this.sign());
    }

    private class Input extends BasicTxInput {
        public Input(String txid, int vout) {
            super(txid, vout);
        }

        public Input(TxInput copy) {
            this(copy.txid(), copy.vout());
        }

        public int hashCode() {
            return this.txid.hashCode() + this.vout;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            } else if (!(obj instanceof TxInput)) {
                return false;
            } else {
                TxInput other = (TxInput)obj;
                return this.vout == other.vout() && this.txid.equals(other.txid());
            }
        }
    }
}
