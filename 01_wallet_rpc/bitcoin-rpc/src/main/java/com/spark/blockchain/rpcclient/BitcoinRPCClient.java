package com.spark.blockchain.rpcclient;

import com.alibaba.fastjson.JSONObject;
import com.spark.blockchain.rpcclient.Bitcoin.AddNoteCmd;
import com.spark.blockchain.rpcclient.Bitcoin.AddressValidationResult;
import com.spark.blockchain.rpcclient.Bitcoin.BasicTxInput;
import com.spark.blockchain.rpcclient.Bitcoin.Block;
import com.spark.blockchain.rpcclient.Bitcoin.Info;
import com.spark.blockchain.rpcclient.Bitcoin.MiningInfo;
import com.spark.blockchain.rpcclient.Bitcoin.PeerInfo;
import com.spark.blockchain.rpcclient.Bitcoin.RawTransaction;
import com.spark.blockchain.rpcclient.Bitcoin.ReceivedAddress;
import com.spark.blockchain.rpcclient.Bitcoin.Transaction;
import com.spark.blockchain.rpcclient.Bitcoin.TransactionsSinceBlock;
import com.spark.blockchain.rpcclient.Bitcoin.TxInput;
import com.spark.blockchain.rpcclient.Bitcoin.TxOutSetInfo;
import com.spark.blockchain.rpcclient.Bitcoin.TxOutput;
import com.spark.blockchain.rpcclient.Bitcoin.Unspent;
import com.spark.blockchain.rpcclient.Bitcoin.Work;
import com.spark.blockchain.rpcclient.Bitcoin.RawTransaction.In;
import com.spark.blockchain.rpcclient.Bitcoin.RawTransaction.Out;
import com.spark.blockchain.rpcclient.Bitcoin.RawTransaction.Out.ScriptPubKey;
import com.spark.blockchain.util.Base64Coder;
import com.spark.blockchain.util.JSON;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class BitcoinRPCClient implements Bitcoin {
    private static final Logger logger = Logger.getLogger(BitcoinRPCClient.class.getCanonicalName());
    public final URL rpcURL;
    private URL noAuthURL;
    private String authStr;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;
    private int connectTimeout;
    public static final Charset QUERY_CHARSET = Charset.forName("UTF-8");

    public BitcoinRPCClient(String rpcUrl) throws MalformedURLException {
        this(new URL(rpcUrl));
    }

    public BitcoinRPCClient(URL rpc) {
        this.hostnameVerifier = null;
        this.sslSocketFactory = null;
        this.connectTimeout = 0;
        this.rpcURL = rpc;

        try {
            this.noAuthURL = (new URI(rpc.getProtocol(), (String)null, rpc.getHost(), rpc.getPort(), rpc.getPath(), rpc.getQuery(), (String)null)).toURL();
        } catch (MalformedURLException var3) {
            throw new IllegalArgumentException(rpc.toString(), var3);
        } catch (URISyntaxException var4) {
            throw new IllegalArgumentException(rpc.toString(), var4);
        }

        this.authStr = rpc.getUserInfo() == null ? null : String.valueOf(Base64Coder.encode(rpc.getUserInfo().getBytes(Charset.forName("ISO8859-1"))));
    }

    public HostnameVerifier getHostnameVerifier() {
        return this.hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return this.sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public void setConnectTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout can not be negative");
        } else {
            this.connectTimeout = timeout;
        }
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public byte[] prepareRequest(final String method, final Object... params) {
        return JSON.stringify(new LinkedHashMap() {
            {
                this.put("method", method);
                this.put("params", params);
                this.put("id", "1");
            }
        }).getBytes(QUERY_CHARSET);
    }

    private static byte[] loadStream(InputStream in, boolean close) throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while(true) {
            int nr = in.read(buffer);
            if (nr == -1) {
                return o.toByteArray();
            }

            if (nr == 0) {
                throw new IOException("Read timed out");
            }

            o.write(buffer, 0, nr);
        }
    }

    public Object loadResponse(InputStream in, Object expectedID, boolean close) throws IOException, BitcoinException {
        Object var6;
        try {
            String r = new String(loadStream(in, close), QUERY_CHARSET);
            logger.log(Level.FINE, "Bitcoin JSON-RPC response:\n{0}", r);

            try {
                JSONObject response = com.alibaba.fastjson.JSON.parseObject(r);
                if (!expectedID.equals(response.get("id"))) {
                    throw new BitcoinRPCException("Wrong response ID (expected: " + String.valueOf(expectedID) + ", response: " + response.get("id") + ")");
                }

                if (response.get("error") != null) {
                    throw new BitcoinException(JSON.stringify(response.get("error")));
                }

                var6 = response.get("result");
            } catch (ClassCastException var10) {
                throw new BitcoinRPCException("Invalid server response format (data: \"" + r + "\")");
            }
        } finally {
            if (close) {
                in.close();
            }

        }

        return var6;
    }

    public Object query(String method, Object... o) throws BitcoinException {
        try {
            HttpURLConnection conn = (HttpURLConnection)this.noAuthURL.openConnection();
            if (this.connectTimeout != 0) {
                conn.setConnectTimeout(this.connectTimeout);
            }

            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (conn instanceof HttpsURLConnection) {
                if (this.hostnameVerifier != null) {
                    ((HttpsURLConnection)conn).setHostnameVerifier(this.hostnameVerifier);
                }

                if (this.sslSocketFactory != null) {
                    ((HttpsURLConnection)conn).setSSLSocketFactory(this.sslSocketFactory);
                }
            }

            conn.setRequestProperty("Authorization", "Basic " + this.authStr);
            byte[] r = this.prepareRequest(method, o);
            logger.log(Level.FINE, "Bitcoin JSON-RPC request:\n{0}", new String(r, QUERY_CHARSET));
            conn.getOutputStream().write(r);
            conn.getOutputStream().close();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new BitcoinRPCException("RPC Query Failed (method: " + method + ", params: " + Arrays.deepToString(o) + ", response header: " + responseCode + " " + conn.getResponseMessage() + ", response: " + new String(loadStream(conn.getErrorStream(), true)));
            } else {
                return this.loadResponse(conn.getInputStream(), "1", true);
            }
        } catch (IOException var6) {
            throw new BitcoinRPCException("RPC Query Failed (method: " + method + ", params: " + Arrays.deepToString(o) + ")", var6);
        }
    }

    public void addNode(String node, AddNoteCmd command) throws BitcoinException {
        this.query("addnode", node, command.toString());
    }

    public String createRawTransaction(List<TxInput> inputs, List<TxOutput> outputs) throws BitcoinException {
        List<Map> pInputs = new ArrayList();
        Iterator var4 = inputs.iterator();

        while(var4.hasNext()) {
            final TxInput txInput = (TxInput)var4.next();
            pInputs.add(new LinkedHashMap() {
                {
                    this.put("txid", txInput.txid());
                    this.put("vout", txInput.vout());
                }
            });
        }

        Map<String, BigDecimal> pOutputs = new LinkedHashMap();
        Iterator var6 = outputs.iterator();

        while(var6.hasNext()) {
            TxOutput txOutput = (TxOutput)var6.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal)pOutputs.put(txOutput.address(), txOutput.amount())) != null) {
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
            }
        }

        return (String)this.query("createrawtransaction", pInputs, pOutputs);
    }

    public RawTransaction decodeRawTransaction(String hex) throws BitcoinException {
        return new BitcoinRPCClient.RawTransactionImpl((Map)this.query("decoderawtransaction", hex));
    }

    public String dumpPrivKey(String address) throws BitcoinException {
        return (String)this.query("dumpprivkey", address);
    }

    public String getAccount(String address) throws BitcoinException {
        return (String)this.query("getaccount", address);
    }

    public String getAccountAddress(String account) throws BitcoinException {
        return (String)this.query("getaccountaddress", account);
    }

    public List<String> getAddressesByAccount(String account) throws BitcoinException {
        return (List)this.query("getaddressesbyaccount", account);
    }

    public double getBalance() throws BitcoinException {
        return ((Number)this.query("getbalance")).doubleValue();
    }

    public double getBalance(String account) throws BitcoinException {
        return ((Number)this.query("getbalance", account)).doubleValue();
    }

    public double getBalance(String account, int minConf) throws BitcoinException {
        return ((Number)this.query("getbalance", account, minConf)).doubleValue();
    }

    public Block getBlock(String blockHash) throws BitcoinException {
        return new BitcoinRPCClient.BlockMapWrapper((Map)this.query("getblock", blockHash));
    }

    public int getBlockCount() throws BitcoinException {
        return ((Number)this.query("getblockcount")).intValue();
    }

    public String getBlockHash(int blockId) throws BitcoinException {
        return (String)this.query("getblockhash", blockId);
    }

    public int getConnectionCount() throws BitcoinException {
        return ((Number)this.query("getconnectioncount")).intValue();
    }

    public double getDifficulty() throws BitcoinException {
        return ((Number)this.query("getdifficulty")).doubleValue();
    }

    public boolean getGenerate() throws BitcoinException {
        return (Boolean)this.query("getgenerate");
    }

    public double getHashesPerSec() throws BitcoinException {
        return ((Number)this.query("gethashespersec")).doubleValue();
    }

    public Info getInfo() throws BitcoinException {
        return new BitcoinRPCClient.InfoMapWrapper((Map)this.query("getinfo"));
    }

    public MiningInfo getMiningInfo() throws BitcoinException {
        return new BitcoinRPCClient.MiningInfoMapWrapper((Map)this.query("getmininginfo"));
    }

    public String getNewAddress() throws BitcoinException {
        return (String)this.query("getnewaddress");
    }

    public String getNewAddress(String account) throws BitcoinException {
        return (String)this.query("getnewaddress", account);
    }

    public PeerInfo getPeerInfo() throws BitcoinException {
        return new BitcoinRPCClient.PeerInfoMapWrapper((Map)this.query("getmininginfo"));
    }

    public String getRawTransactionHex(String txId) throws BitcoinException {
        return (String)this.query("getrawtransaction", txId);
    }

    public RawTransaction getRawTransaction(String txId) throws BitcoinException {
        return new BitcoinRPCClient.RawTransactionImpl((Map)this.query("getrawtransaction", txId, 1));
    }

    public double getReceivedByAccount(String account) throws BitcoinException {
        return ((Number)this.query("getreceivedbyaccount", account)).doubleValue();
    }

    public double getReceivedByAccount(String account, int minConf) throws BitcoinException {
        return ((Number)this.query("getreceivedbyaccount", account, minConf)).doubleValue();
    }

    public double getReceivedByAddress(String address) throws BitcoinException {
        return ((Number)this.query("getreceivedbyaddress", address)).doubleValue();
    }

    public double getReceivedByAddress(String address, int minConf) throws BitcoinException {
        return ((Number)this.query("getreceivedbyaddress", address, minConf)).doubleValue();
    }

    public RawTransaction getTransaction(String txId) throws BitcoinException {
        return new BitcoinRPCClient.RawTransactionImpl((Map)this.query("gettransaction", txId));
    }

    public TxOutSetInfo getTxOutSetInfo() throws BitcoinException {
        final Map txoutsetinfoResult = (Map)this.query("gettxoutsetinfo");
        return new TxOutSetInfo() {
            public int height() {
                return ((Number)txoutsetinfoResult.get("height")).intValue();
            }

            public String bestBlock() {
                return (String)txoutsetinfoResult.get("bestblock");
            }

            public int transactions() {
                return ((Number)txoutsetinfoResult.get("transactions")).intValue();
            }

            public int txOuts() {
                return ((Number)txoutsetinfoResult.get("txouts")).intValue();
            }

            public int bytesSerialized() {
                return ((Number)txoutsetinfoResult.get("bytes_serialized")).intValue();
            }

            public String hashSerialized() {
                return (String)txoutsetinfoResult.get("hash_serialized");
            }

            public double totalAmount() {
                return ((Number)txoutsetinfoResult.get("total_amount")).doubleValue();
            }

            public String toString() {
                return txoutsetinfoResult.toString();
            }
        };
    }

    public Work getWork() throws BitcoinException {
        final Map workResult = (Map)this.query("getwork");
        return new Work() {
            public String midstate() {
                return (String)workResult.get("midstate");
            }

            public String data() {
                return (String)workResult.get("data");
            }

            public String hash1() {
                return (String)workResult.get("hash1");
            }

            public String target() {
                return (String)workResult.get("target");
            }

            public String toString() {
                return workResult.toString();
            }
        };
    }

    public void importPrivKey(String bitcoinPrivKey) throws BitcoinException {
        this.query("importprivkey", bitcoinPrivKey);
    }

    public void importPrivKey(String bitcoinPrivKey, String label) throws BitcoinException {
        this.query("importprivkey", bitcoinPrivKey, label);
    }

    public void importPrivKey(String bitcoinPrivKey, String label, boolean rescan) throws BitcoinException {
        this.query("importprivkey", bitcoinPrivKey, label, rescan);
    }

    public Map<String, Number> listAccounts() throws BitcoinException {
        return (Map)this.query("listaccounts");
    }

    public Map<String, Number> listAccounts(int minConf) throws BitcoinException {
        return (Map)this.query("listaccounts", minConf);
    }

    public List<ReceivedAddress> listReceivedByAccount() throws BitcoinException {
        return new BitcoinRPCClient.ReceivedAddressListWrapper((List)this.query("listreceivedbyaccount"));
    }

    public List<ReceivedAddress> listReceivedByAccount(int minConf) throws BitcoinException {
        return new BitcoinRPCClient.ReceivedAddressListWrapper((List)this.query("listreceivedbyaccount", minConf));
    }

    public List<ReceivedAddress> listReceivedByAccount(int minConf, boolean includeEmpty) throws BitcoinException {
        return new BitcoinRPCClient.ReceivedAddressListWrapper((List)this.query("listreceivedbyaccount", minConf, includeEmpty));
    }

    public List<ReceivedAddress> listReceivedByAddress() throws BitcoinException {
        return new BitcoinRPCClient.ReceivedAddressListWrapper((List)this.query("listreceivedbyaddress"));
    }

    public List<ReceivedAddress> listReceivedByAddress(int minConf) throws BitcoinException {
        return new BitcoinRPCClient.ReceivedAddressListWrapper((List)this.query("listreceivedbyaddress", minConf));
    }

    public List<ReceivedAddress> listReceivedByAddress(int minConf, boolean includeEmpty) throws BitcoinException {
        return new BitcoinRPCClient.ReceivedAddressListWrapper((List)this.query("listreceivedbyaddress", minConf, includeEmpty));
    }

    public TransactionsSinceBlock listSinceBlock() throws BitcoinException {
        return new BitcoinRPCClient.TransactionsSinceBlockImpl((Map)this.query("listsinceblock"));
    }

    public TransactionsSinceBlock listSinceBlock(String blockHash) throws BitcoinException {
        return new BitcoinRPCClient.TransactionsSinceBlockImpl((Map)this.query("listsinceblock", blockHash));
    }

    public TransactionsSinceBlock listSinceBlock(String blockHash, int targetConfirmations) throws BitcoinException {
        return new BitcoinRPCClient.TransactionsSinceBlockImpl((Map)this.query("listsinceblock", blockHash, targetConfirmations));
    }

    public List<Transaction> listTransactions() throws BitcoinException {
        return new BitcoinRPCClient.TransactionListMapWrapper((List)this.query("listtransactions"));
    }

    public List<Transaction> listTransactions(String account) throws BitcoinException {
        return new BitcoinRPCClient.TransactionListMapWrapper((List)this.query("listtransactions", account));
    }

    public List<Transaction> listTransactions(String account, int count) throws BitcoinException {
        return new BitcoinRPCClient.TransactionListMapWrapper((List)this.query("listtransactions", account, count));
    }

    public List<Transaction> listTransactions(String account, int count, int from) throws BitcoinException {
        return new BitcoinRPCClient.TransactionListMapWrapper((List)this.query("listtransactions", account, count, from));
    }

    public List<Unspent> listUnspent() throws BitcoinException {
        return new BitcoinRPCClient.UnspentListWrapper((List)this.query("listunspent"));
    }

    public List<Unspent> listUnspent(int minConf) throws BitcoinException {
        return new BitcoinRPCClient.UnspentListWrapper((List)this.query("listunspent", minConf));
    }

    public List<Unspent> listUnspent(int minConf, int maxConf) throws BitcoinException {
        return new BitcoinRPCClient.UnspentListWrapper((List)this.query("listunspent", minConf, maxConf));
    }

    public List<Unspent> listUnspent(int minConf, int maxConf, String... addresses) throws BitcoinException {
        return new BitcoinRPCClient.UnspentListWrapper((List)this.query("listunspent", minConf, maxConf, addresses));
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount) throws BitcoinException {
        return (String)this.query("sendfrom", fromAccount, toBitcoinAddress, amount);
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf) throws BitcoinException {
        return (String)this.query("sendfrom", fromAccount, toBitcoinAddress, amount, minConf);
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf, String comment) throws BitcoinException {
        return (String)this.query("sendfrom", fromAccount, toBitcoinAddress, amount, minConf, comment);
    }

    public String sendFrom(String fromAccount, String toBitcoinAddress, double amount, int minConf, String comment, String commentTo) throws BitcoinException {
        return (String)this.query("sendfrom", fromAccount, toBitcoinAddress, amount, minConf, comment, commentTo);
    }

    public String sendMany(String fromAccount, List<TxOutput> outputs) throws BitcoinException {
        Map<String, BigDecimal> pOutputs = new LinkedHashMap();
        Iterator var5 = outputs.iterator();

        while(var5.hasNext()) {
            TxOutput txOutput = (TxOutput)var5.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal)pOutputs.put(txOutput.address(), txOutput.amount())) != null) {
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
            }
        }

        return (String)this.query("sendmany", fromAccount, pOutputs);
    }

    public String sendMany(String fromAccount, List<TxOutput> outputs, int minConf) throws BitcoinException {
        Map<String, BigDecimal> pOutputs = new LinkedHashMap();
        Iterator var6 = outputs.iterator();

        while(var6.hasNext()) {
            TxOutput txOutput = (TxOutput)var6.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal)pOutputs.put(txOutput.address(), txOutput.amount())) != null) {
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
            }
        }

        return (String)this.query("sendmany", fromAccount, pOutputs, minConf);
    }

    public String sendMany(String fromAccount, List<TxOutput> outputs, int minConf, String comment) throws BitcoinException {
        Map<String, BigDecimal> pOutputs = new LinkedHashMap();
        Iterator var7 = outputs.iterator();

        while(var7.hasNext()) {
            TxOutput txOutput = (TxOutput)var7.next();
            BigDecimal oldValue;
            if ((oldValue = (BigDecimal)pOutputs.put(txOutput.address(), txOutput.amount())) != null) {
                pOutputs.put(txOutput.address(), oldValue.add(txOutput.amount()));
            }
        }

        return (String)this.query("sendmany", fromAccount, pOutputs, minConf, comment);
    }

    public String sendRawTransaction(String hex) throws BitcoinException {
        return (String)this.query("sendrawtransaction", hex);
    }

    public String sendToAddress(String toAddress, double amount) throws BitcoinException {
        return (String)this.query("sendtoaddress", toAddress, amount);
    }

    public String sendToAddress(String toAddress, double amount, String comment) throws BitcoinException {
        return (String)this.query("sendtoaddress", toAddress, amount, comment);
    }

    public Boolean setTxFee(double amount) throws BitcoinException {
        return (Boolean)this.query("settxfee", amount);
    }

    public String sendToAddress(String toAddress, double amount, String comment, String commentTo) throws BitcoinException {
        return (String)this.query("sendtoaddress", toAddress, amount, comment, commentTo);
    }

    public String signMessage(String address, String message) throws BitcoinException {
        return (String)this.query("signmessage", address, message);
    }

    public String signRawTransaction(String hex) throws BitcoinException {
        Map result = (Map)this.query("signrawtransaction", hex);
        if ((Boolean)result.get("complete")) {
            return (String)result.get("hex");
        } else {
            throw new BitcoinException("Incomplete");
        }
    }

    public void stop() throws BitcoinException {
        this.query("stop");
    }

    public AddressValidationResult validateAddress(String address) throws BitcoinException {
        final Map validationResult = (Map)this.query("validateaddress", address);
        return new AddressValidationResult() {
            public boolean isValid() {
                return (Boolean)validationResult.get("isvalid");
            }

            public String address() {
                return (String)validationResult.get("address");
            }

            public boolean isMine() {
                return (Boolean)validationResult.get("ismine");
            }

            public boolean isScript() {
                return (Boolean)validationResult.get("isscript");
            }

            public String pubKey() {
                return (String)validationResult.get("pubkey");
            }

            public boolean isCompressed() {
                return (Boolean)validationResult.get("iscompressed");
            }

            public String account() {
                return (String)validationResult.get("account");
            }

            public String toString() {
                return validationResult.toString();
            }
        };
    }

    public boolean verifyMessage(String address, String signature, String message) throws BitcoinException {
        return (Boolean)this.query("verifymessage", address, signature, message);
    }

    private class UnspentListWrapper extends ListMapWrapper<Unspent> {
        public UnspentListWrapper(List<Map> list) {
            super(list);
        }

        protected Unspent wrap(final Map m) {
            return new Unspent() {
                public String txid() {
                    return MapWrapper.mapStr(m, "txid");
                }

                public int vout() {
                    return MapWrapper.mapInt(m, "vout");
                }

                public String address() {
                    return MapWrapper.mapStr(m, "address");
                }

                public String scriptPubKey() {
                    return MapWrapper.mapStr(m, "scriptPubKey");
                }

                public String account() {
                    return MapWrapper.mapStr(m, "account");
                }

                public BigDecimal amount() {
                    return MapWrapper.mapBigDecimal(m, "amount");
                }

                public int confirmations() {
                    return MapWrapper.mapInt(m, "confirmations");
                }
            };
        }
    }

    private class TransactionsSinceBlockImpl implements TransactionsSinceBlock {
        public final List<Transaction> transactions;
        public final String lastBlock;

        public TransactionsSinceBlockImpl(Map r) {
            this.transactions = BitcoinRPCClient.this.new TransactionListMapWrapper((List)r.get("transactions"));
            this.lastBlock = (String)r.get("lastblock");
        }

        public List<Transaction> transactions() {
            return this.transactions;
        }

        public String lastBlock() {
            return this.lastBlock;
        }
    }

    private class TransactionListMapWrapper extends ListMapWrapper<Transaction> {
        public TransactionListMapWrapper(List<Map> list) {
            super(list);
        }

        protected Transaction wrap(final Map m) {
            return new Transaction() {
                private RawTransaction raw = null;

                public String account() {
                    return MapWrapper.mapStr(m, "account");
                }

                public String address() {
                    return MapWrapper.mapStr(m, "address");
                }

                public String category() {
                    return MapWrapper.mapStr(m, "category");
                }

                public double amount() {
                    return MapWrapper.mapDouble(m, "amount");
                }

                public double fee() {
                    return MapWrapper.mapDouble(m, "fee");
                }

                public int confirmations() {
                    return MapWrapper.mapInt(m, "confirmations");
                }

                public String blockHash() {
                    return MapWrapper.mapStr(m, "blockhash");
                }

                public int blockIndex() {
                    return MapWrapper.mapInt(m, "blockindex");
                }

                public Date blockTime() {
                    return MapWrapper.mapCTime(m, "blocktime");
                }

                public String txId() {
                    return MapWrapper.mapStr(m, "txid");
                }

                public Date time() {
                    return MapWrapper.mapCTime(m, "time");
                }

                public Date timeReceived() {
                    return MapWrapper.mapCTime(m, "timereceived");
                }

                public String comment() {
                    return MapWrapper.mapStr(m, "comment");
                }

                public String commentTo() {
                    return MapWrapper.mapStr(m, "to");
                }

                public RawTransaction raw() {
                    if (this.raw == null) {
                        try {
                            this.raw = BitcoinRPCClient.this.getRawTransaction(this.txId());
                        } catch (BitcoinException var2) {
                            throw new RuntimeException(var2);
                        }
                    }

                    return this.raw;
                }

                public String toString() {
                    return m.toString();
                }
            };
        }
    }

    private static class ReceivedAddressListWrapper extends AbstractList<ReceivedAddress> {
        private final List<Map<String, Object>> wrappedList;

        public ReceivedAddressListWrapper(List<Map<String, Object>> wrappedList) {
            this.wrappedList = wrappedList;
        }

        public ReceivedAddress get(int index) {
            final Map<String, Object> e = (Map)this.wrappedList.get(index);
            return new ReceivedAddress() {
                public String address() {
                    return (String)e.get("address");
                }

                public String account() {
                    return (String)e.get("account");
                }

                public double amount() {
                    return ((Number)e.get("amount")).doubleValue();
                }

                public int confirmations() {
                    return ((Number)e.get("confirmations")).intValue();
                }

                public String toString() {
                    return e.toString();
                }
            };
        }

        public int size() {
            return this.wrappedList.size();
        }
    }

    private class RawTransactionImpl extends MapWrapper implements RawTransaction {
        public RawTransactionImpl(Map<String, Object> tx) {
            super(tx);
        }

        public String hex() {
            return this.mapStr("hex");
        }

        public String txId() {
            return this.mapStr("txid");
        }

        public int version() {
            return this.mapInt("version");
        }

        public long lockTime() {
            return this.mapLong("locktime");
        }

        public List<In> vIn() {
            final List<Map<String, Object>> vIn = (List)this.m.get("vin");
            return new AbstractList<In>() {
                public In get(int index) {
                    return RawTransactionImpl.this.new InImpl((Map)vIn.get(index));
                }

                public int size() {
                    return vIn.size();
                }
            };
        }

        public List<Out> vOut() {
            final List<Map<String, Object>> vOut = (List)this.m.get("vout");
            return new AbstractList<Out>() {
                public Out get(int index) {
                    return RawTransactionImpl.this.new OutImpl((Map)vOut.get(index));
                }

                public int size() {
                    return vOut.size();
                }
            };
        }

        public String blockHash() {
            return this.mapStr("blockhash");
        }

        public int confirmations() {
            return this.mapInt("confirmations");
        }

        public Date time() {
            return this.mapCTime("time");
        }

        public Date blocktime() {
            return this.mapCTime("blocktime");
        }

        private class OutImpl extends MapWrapper implements Out {
            public OutImpl(Map m) {
                super(m);
            }

            public double value() {
                return this.mapDouble("value");
            }

            public int n() {
                return this.mapInt("n");
            }

            public ScriptPubKey scriptPubKey() {
                return new BitcoinRPCClient.RawTransactionImpl.OutImpl.ScriptPubKeyImpl((Map)this.m.get("scriptPubKey"));
            }

            public TxInput toInput() {
                return new BasicTxInput(this.transaction().txId(), this.n());
            }

            public RawTransaction transaction() {
                return RawTransactionImpl.this;
            }

            private class ScriptPubKeyImpl extends MapWrapper implements ScriptPubKey {
                public ScriptPubKeyImpl(Map m) {
                    super(m);
                }

                public String asm() {
                    return this.mapStr("asm");
                }

                public String hex() {
                    return this.mapStr("hex");
                }

                public int reqSigs() {
                    return this.mapInt("reqSigs");
                }

                public String type() {
                    return this.mapStr(this.type());
                }

                public List<String> addresses() {
                    return (List)this.m.get("addresses");
                }
            }
        }

        private class InImpl extends MapWrapper implements In {
            public InImpl(Map m) {
                super(m);
            }

            public String txid() {
                return this.mapStr("txid");
            }

            public int vout() {
                return this.mapInt("vout");
            }

            public Map<String, Object> scriptSig() {
                return (Map)this.m.get("scriptSig");
            }

            public long sequence() {
                return this.mapLong("sequence");
            }

            public RawTransaction getTransaction() {
                try {
                    return BitcoinRPCClient.this.getRawTransaction(this.mapStr("txid"));
                } catch (BitcoinException var2) {
                    throw new RuntimeException(var2);
                }
            }

            public Out getTransactionOutput() {
                return (Out)this.getTransaction().vOut().get(this.mapInt("vout"));
            }
        }
    }

    private class PeerInfoMapWrapper extends MapWrapper implements PeerInfo {
        public PeerInfoMapWrapper(Map m) {
            super(m);
        }

        public String addr() {
            return this.mapStr("addr");
        }

        public String services() {
            return this.mapStr("services");
        }

        public int lastsend() {
            return this.mapInt("lastsend");
        }

        public int lastrecv() {
            return this.mapInt("lastrecv");
        }

        public int bytessent() {
            return this.mapInt("bytessent");
        }

        public int bytesrecv() {
            return this.mapInt("bytesrecv");
        }

        public int blocksrequested() {
            return this.mapInt("blocksrequested");
        }

        public Date conntime() {
            return this.mapCTime("conntime");
        }

        public int version() {
            return this.mapInt("version");
        }

        public String subver() {
            return this.mapStr("subver");
        }

        public boolean inbound() {
            return this.mapBool("inbound");
        }

        public int startingheight() {
            return this.mapInt("startingheight");
        }

        public int banscore() {
            return this.mapInt("banscore");
        }
    }

    private class MiningInfoMapWrapper extends MapWrapper implements MiningInfo {
        public MiningInfoMapWrapper(Map m) {
            super(m);
        }

        public int blocks() {
            return this.mapInt("blocks");
        }

        public int currentblocksize() {
            return this.mapInt("currentblocksize");
        }

        public int currentblocktx() {
            return this.mapInt("currentblocktx");
        }

        public double difficulty() {
            return this.mapDouble("difficulty");
        }

        public String errors() {
            return this.mapStr("errors");
        }

        public int genproclimit() {
            return this.mapInt("genproclimit");
        }

        public double networkhashps() {
            return this.mapDouble("networkhashps");
        }

        public int pooledtx() {
            return this.mapInt("pooledtx");
        }

        public boolean testnet() {
            return this.mapBool("testnet");
        }

        public String chain() {
            return this.mapStr("chain");
        }

        public boolean generate() {
            return this.mapBool("generate");
        }
    }

    private class InfoMapWrapper extends MapWrapper implements Info {
        public InfoMapWrapper(Map m) {
            super(m);
        }

        public int version() {
            return this.mapInt("version");
        }

        public int protocolversion() {
            return this.mapInt("protocolversion");
        }

        public int walletversion() {
            return this.mapInt("walletversion");
        }

        public double balance() {
            return this.mapDouble("balance");
        }

        public int blocks() {
            return this.mapInt("blocks");
        }

        public int timeoffset() {
            return this.mapInt("timeoffset");
        }

        public int connections() {
            return this.mapInt("connections");
        }

        public String proxy() {
            return this.mapStr("proxy");
        }

        public double difficulty() {
            return this.mapDouble("difficulty");
        }

        public boolean testnet() {
            return this.mapBool("testnet");
        }

        public int keypoololdest() {
            return this.mapInt("keypoololdest");
        }

        public int keypoolsize() {
            return this.mapInt("keypoolsize");
        }

        public int unlocked_until() {
            return this.mapInt("unlocked_until");
        }

        public double paytxfee() {
            return this.mapDouble("paytxfee");
        }

        public double relayfee() {
            return this.mapDouble("relayfee");
        }

        public String errors() {
            return this.mapStr("errors");
        }
    }

    private class BlockMapWrapper extends MapWrapper implements Block {
        public BlockMapWrapper(Map m) {
            super(m);
        }

        public String hash() {
            return this.mapStr("hash");
        }

        public int confirmations() {
            return this.mapInt("confirmations");
        }

        public int size() {
            return this.mapInt("size");
        }

        public int height() {
            return this.mapInt("height");
        }

        public int version() {
            return this.mapInt("version");
        }

        public String merkleRoot() {
            return this.mapStr("");
        }

        public List<String> tx() {
            return (List)this.m.get("tx");
        }

        public Date time() {
            return this.mapCTime("time");
        }

        public long nonce() {
            return this.mapLong("nonce");
        }

        public String bits() {
            return this.mapStr("bits");
        }

        public double difficulty() {
            return this.mapDouble("difficulty");
        }

        public String previousHash() {
            return this.mapStr("previousblockhash");
        }

        public String nextHash() {
            return this.mapStr("nextblockhash");
        }

        public Block previous() throws BitcoinException {
            return !this.m.containsKey("previousblockhash") ? null : BitcoinRPCClient.this.getBlock(this.previousHash());
        }

        public Block next() throws BitcoinException {
            return !this.m.containsKey("nextblockhash") ? null : BitcoinRPCClient.this.getBlock(this.nextHash());
        }
    }
}
