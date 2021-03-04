package com.spark.blockchain.rpcclient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Bitcoin {
    void addNode(String var1, Bitcoin.AddNoteCmd var2) throws BitcoinException;

    String createRawTransaction(List<Bitcoin.TxInput> var1, List<Bitcoin.TxOutput> var2) throws BitcoinException;

    Bitcoin.RawTransaction decodeRawTransaction(String var1) throws BitcoinException;

    String dumpPrivKey(String var1) throws BitcoinException;

    String getAccount(String var1) throws BitcoinException;

    String getAccountAddress(String var1) throws BitcoinException;

    List<String> getAddressesByAccount(String var1) throws BitcoinException;

    double getBalance() throws BitcoinException;

    double getBalance(String var1) throws BitcoinException;

    double getBalance(String var1, int var2) throws BitcoinException;

    Bitcoin.Block getBlock(String var1) throws BitcoinException;

    int getBlockCount() throws BitcoinException;

    String getBlockHash(int var1) throws BitcoinException;

    int getConnectionCount() throws BitcoinException;

    double getDifficulty() throws BitcoinException;

    boolean getGenerate() throws BitcoinException;

    double getHashesPerSec() throws BitcoinException;

    Bitcoin.Info getInfo() throws BitcoinException;

    Bitcoin.MiningInfo getMiningInfo() throws BitcoinException;

    String getNewAddress() throws BitcoinException;

    String getNewAddress(String var1) throws BitcoinException;

    Bitcoin.PeerInfo getPeerInfo() throws BitcoinException;

    String getRawTransactionHex(String var1) throws BitcoinException;

    Bitcoin.RawTransaction getRawTransaction(String var1) throws BitcoinException;

    double getReceivedByAccount(String var1) throws BitcoinException;

    double getReceivedByAccount(String var1, int var2) throws BitcoinException;

    double getReceivedByAddress(String var1) throws BitcoinException;

    double getReceivedByAddress(String var1, int var2) throws BitcoinException;

    Bitcoin.RawTransaction getTransaction(String var1) throws BitcoinException;

    Bitcoin.TxOutSetInfo getTxOutSetInfo() throws BitcoinException;

    Bitcoin.Work getWork() throws BitcoinException;

    void importPrivKey(String var1) throws BitcoinException;

    void importPrivKey(String var1, String var2) throws BitcoinException;

    void importPrivKey(String var1, String var2, boolean var3) throws BitcoinException;

    Map<String, Number> listAccounts() throws BitcoinException;

    Map<String, Number> listAccounts(int var1) throws BitcoinException;

    List<Bitcoin.ReceivedAddress> listReceivedByAccount() throws BitcoinException;

    List<Bitcoin.ReceivedAddress> listReceivedByAccount(int var1) throws BitcoinException;

    List<Bitcoin.ReceivedAddress> listReceivedByAccount(int var1, boolean var2) throws BitcoinException;

    List<Bitcoin.ReceivedAddress> listReceivedByAddress() throws BitcoinException;

    List<Bitcoin.ReceivedAddress> listReceivedByAddress(int var1) throws BitcoinException;

    List<Bitcoin.ReceivedAddress> listReceivedByAddress(int var1, boolean var2) throws BitcoinException;

    Bitcoin.TransactionsSinceBlock listSinceBlock() throws BitcoinException;

    Bitcoin.TransactionsSinceBlock listSinceBlock(String var1) throws BitcoinException;

    Bitcoin.TransactionsSinceBlock listSinceBlock(String var1, int var2) throws BitcoinException;

    List<Bitcoin.Transaction> listTransactions() throws BitcoinException;

    List<Bitcoin.Transaction> listTransactions(String var1) throws BitcoinException;

    List<Bitcoin.Transaction> listTransactions(String var1, int var2) throws BitcoinException;

    List<Bitcoin.Transaction> listTransactions(String var1, int var2, int var3) throws BitcoinException;

    List<Bitcoin.Unspent> listUnspent() throws BitcoinException;

    List<Bitcoin.Unspent> listUnspent(int var1) throws BitcoinException;

    List<Bitcoin.Unspent> listUnspent(int var1, int var2) throws BitcoinException;

    List<Bitcoin.Unspent> listUnspent(int var1, int var2, String... var3) throws BitcoinException;

    String sendFrom(String var1, String var2, double var3) throws BitcoinException;

    String sendFrom(String var1, String var2, double var3, int var5) throws BitcoinException;

    String sendFrom(String var1, String var2, double var3, int var5, String var6) throws BitcoinException;

    String sendFrom(String var1, String var2, double var3, int var5, String var6, String var7) throws BitcoinException;

    String sendMany(String var1, List<Bitcoin.TxOutput> var2) throws BitcoinException;

    String sendMany(String var1, List<Bitcoin.TxOutput> var2, int var3) throws BitcoinException;

    String sendMany(String var1, List<Bitcoin.TxOutput> var2, int var3, String var4) throws BitcoinException;

    String sendRawTransaction(String var1) throws BitcoinException;

    String sendToAddress(String var1, double var2) throws BitcoinException;

    String sendToAddress(String var1, double var2, String var4) throws BitcoinException;

    Boolean setTxFee(double var1) throws BitcoinException;

    String sendToAddress(String var1, double var2, String var4, String var5) throws BitcoinException;

    String signMessage(String var1, String var2) throws BitcoinException;

    String signRawTransaction(String var1) throws BitcoinException;

    void stop() throws BitcoinException;

    Bitcoin.AddressValidationResult validateAddress(String var1) throws BitcoinException;

    boolean verifyMessage(String var1, String var2, String var3) throws BitcoinException;

    public interface AddressValidationResult {
        boolean isValid();

        String address();

        boolean isMine();

        boolean isScript();

        String pubKey();

        boolean isCompressed();

        String account();
    }

    public interface Unspent extends Bitcoin.TxInput, Bitcoin.TxOutput {
        String txid();

        int vout();

        String address();

        String account();

        String scriptPubKey();

        BigDecimal amount();

        int confirmations();
    }

    public interface TransactionsSinceBlock {
        List<Bitcoin.Transaction> transactions();

        String lastBlock();
    }

    public interface Transaction {
        String account();

        String address();

        String category();

        double amount();

        double fee();

        int confirmations();

        String blockHash();

        int blockIndex();

        Date blockTime();

        String txId();

        Date time();

        Date timeReceived();

        String comment();

        String commentTo();

        Bitcoin.RawTransaction raw();
    }

    public interface ReceivedAddress {
        String address();

        String account();

        double amount();

        int confirmations();
    }

    public interface Work {
        String midstate();

        String data();

        String hash1();

        String target();
    }

    public interface TxOutSetInfo {
        int height();

        String bestBlock();

        int transactions();

        int txOuts();

        int bytesSerialized();

        String hashSerialized();

        double totalAmount();
    }

    public interface RawTransaction {
        String hex();

        String txId();

        int version();

        long lockTime();

        List<Bitcoin.RawTransaction.In> vIn();

        List<Bitcoin.RawTransaction.Out> vOut();

        String blockHash();

        int confirmations();

        Date time();

        Date blocktime();

        public interface Out {
            double value();

            int n();

            Bitcoin.RawTransaction.Out.ScriptPubKey scriptPubKey();

            Bitcoin.TxInput toInput();

            Bitcoin.RawTransaction transaction();

            public interface ScriptPubKey {
                String asm();

                String hex();

                int reqSigs();

                String type();

                List<String> addresses();
            }
        }

        public interface In extends Bitcoin.TxInput {
            Map<String, Object> scriptSig();

            long sequence();

            Bitcoin.RawTransaction getTransaction();

            Bitcoin.RawTransaction.Out getTransactionOutput();
        }
    }

    public interface PeerInfo {
        String addr();

        String services();

        int lastsend();

        int lastrecv();

        int bytessent();

        int bytesrecv();

        int blocksrequested();

        Date conntime();

        int version();

        String subver();

        boolean inbound();

        int startingheight();

        int banscore();
    }

    public interface MiningInfo {
        int blocks();

        int currentblocksize();

        int currentblocktx();

        double difficulty();

        String errors();

        int genproclimit();

        double networkhashps();

        int pooledtx();

        boolean testnet();

        String chain();

        boolean generate();
    }

    public interface Info {
        int version();

        int protocolversion();

        int walletversion();

        double balance();

        int blocks();

        int timeoffset();

        int connections();

        String proxy();

        double difficulty();

        boolean testnet();

        int keypoololdest();

        int keypoolsize();

        int unlocked_until();

        double paytxfee();

        double relayfee();

        String errors();
    }

    public interface Block {
        String hash();

        int confirmations();

        int size();

        int height();

        int version();

        String merkleRoot();

        List<String> tx();

        Date time();

        long nonce();

        String bits();

        double difficulty();

        String previousHash();

        String nextHash();

        Bitcoin.Block previous() throws BitcoinException;

        Bitcoin.Block next() throws BitcoinException;
    }

    public static class BasicTxOutput implements Bitcoin.TxOutput {
        public String address;
        public BigDecimal amount;

        public BasicTxOutput(String address, BigDecimal amount) {
            this.address = address;
            this.amount = amount;
        }

        public String address() {
            return this.address;
        }

        public BigDecimal amount() {
            return this.amount;
        }
    }

    public interface TxOutput {
        String address();

        BigDecimal amount();
    }

    public static class BasicTxInput implements Bitcoin.TxInput {
        public String txid;
        public int vout;

        public BasicTxInput(String txid, int vout) {
            this.txid = txid;
            this.vout = vout;
        }

        public String txid() {
            return this.txid;
        }

        public int vout() {
            return this.vout;
        }
    }

    public interface TxInput {
        String txid();

        int vout();
    }

    public static enum AddNoteCmd {
        add,
        remove,
        onetry;

        private AddNoteCmd() {
        }
    }
}
