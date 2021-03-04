package com.spark.blockchain.rpcclient;

import com.spark.blockchain.rpcclient.Bitcoin.BasicTxInput;
import com.spark.blockchain.rpcclient.Bitcoin.Unspent;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class BitcoinUtil {
    public BitcoinUtil() {
    }

    public static double normalizeAmount(double amount) {
        return (double)((long)(0.5D + amount / 1.0E-8D)) * 1.0E-8D;
    }

    public static String sendTransaction(Bitcoin bitcoin, String targetAddress, BigDecimal amount, BigDecimal txFee) throws BitcoinException {
        List<Unspent> unspents = bitcoin.listUnspent(2);
        System.out.println("target=" + targetAddress + ",amount=" + amount.toPlainString() + ",+fee=" + txFee.toPlainString());
        BigDecimal moneySpent = BigDecimal.ZERO;
        BigDecimal moneyChange = BigDecimal.ZERO;
        if (unspents.size() == 0) {
            throw new BitcoinException("insufficient coin");
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String account = "acct-change-" + df.format(new Date());
            String changeAddress = bitcoin.getAccountAddress(account);
            if (changeAddress == null) {
                changeAddress = bitcoin.getNewAddress(account);
            }

            System.out.println("change address:" + changeAddress);
            BitcoinRawTxBuilder builder = new BitcoinRawTxBuilder(bitcoin);
            Iterator var11 = unspents.iterator();

            while(var11.hasNext()) {
                Unspent unspent = (Unspent)var11.next();
                moneySpent = moneySpent.add(unspent.amount());
                System.out.println("unspent=" + unspent.amount());
                builder.in(new BasicTxInput(unspent.txid(), unspent.vout()));
                if (moneySpent.compareTo(amount.add(txFee)) >= 0) {
                    break;
                }
            }

            if (moneySpent.compareTo(amount.add(txFee)) < 0) {
                throw new BitcoinException("insufficient coin");
            } else {
                moneyChange = moneySpent.subtract(amount.add(txFee));
                System.out.println("moneyChange:" + moneyChange.toPlainString());
                builder.out(targetAddress, amount);
                if (moneyChange.compareTo(BigDecimal.ZERO) > 0) {
                    builder.out(changeAddress, moneyChange);
                }

                return builder.send();
            }
        }
    }

    public static String sendTransaction(Bitcoin bitcoin, String fromAddress, String targetAddress, BigDecimal amount, BigDecimal txFee) throws BitcoinException {
        List<Unspent> unspents = bitcoin.listUnspent(1, 99999999, new String[]{fromAddress});
        System.out.println("target=" + targetAddress + ",amount=" + amount.toPlainString() + ",fee=" + txFee.toPlainString());
        BigDecimal moneySpent = BigDecimal.ZERO;
        BigDecimal moneyChange = BigDecimal.ZERO;
        if (unspents.size() == 0) {
            throw new BitcoinException("insufficient coin");
        } else {
            System.out.println("change address:" + fromAddress);
            BitcoinRawTxBuilder builder = new BitcoinRawTxBuilder(bitcoin);
            Iterator var10 = unspents.iterator();

            while(var10.hasNext()) {
                Unspent unspent = (Unspent)var10.next();
                moneySpent = moneySpent.add(unspent.amount());
                System.out.println("unspent=" + unspent.amount());
                builder.in(new BasicTxInput(unspent.txid(), unspent.vout()));
                if (moneySpent.compareTo(amount.add(txFee)) >= 0) {
                    break;
                }
            }

            if (moneySpent.compareTo(amount.add(txFee)) < 0) {
                throw new BitcoinException("insufficient coin");
            } else {
                moneyChange = moneySpent.subtract(amount.add(txFee));
                System.out.println("moneyChange:" + moneyChange.toPlainString());
                builder.out(targetAddress, amount);
                if (moneyChange.compareTo(BigDecimal.ZERO) > 0) {
                    builder.out(fromAddress, moneyChange);
                }

                return builder.send();
            }
        }
    }
}
