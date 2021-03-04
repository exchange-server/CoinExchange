<template>
  <div class="cexchange">
    <div style="display:none;background-image: linear-gradient( 135deg, #FD6585 10%, #0D25B9 100%);text-align: center;height:30px;line-height:30px;letter-spacing: 1px;">本功能非商业基础版功能，需额外付费！</div>
    <img class="bannerimg" src="../../assets/images/ctc-bg.jpg">
    <div class="ctc_container">
      <h1>{{$t('cexchange.desc')}}</h1>
      <div class="main">
          <Tabs :animated="false" style="width:100%;">
            <TabPane :label="$t('cexchange.title')" name="all">
              <div class="ctc-container">
                <div class="trade_wrap">
                  <div class="trade_panel">
                    <div class="trade_bd_ctc">
                      <div class="panel panel_buy">
                        <div class="bd bd_limited">
                          <div style="min-height: 65px;width:100%;margin-bottom: 30px;padding: 10px 10px;border-radius: 5px;background: #0b1520;">
                            <div style="width:50%;float:left;">
                              <h3>{{totalSupply}} BZB</h3>
                              <p>{{$t('cexchange.restricted')}}</p>
                            </div>
                            <div style="width:50%;float:left;">
                              <h3>1 BZB ≈ {{currentPriceShow}} {{baseCoinUnit}}</h3>
                              <p>{{$t('cexchange.currentprice')}}</p>
                            </div>
                          </div>
                          <Form style="width: 60%;margin-left:20%;">
                            <FormItem class="buy-input">
                              <label class="before" style="width:180px;text-align:right;">{{$t('cexchange.quantity')}}：</label>
                              <div class="quantity-group">
                                <div :class="{ 'quantity-item': true, 'current': currentType == 1 }" @click="selectCount(1500, 1)"> 1500 BZB</div>
                                <div :class="{ 'quantity-item': true, 'current': currentType == 2 }" @click="selectCount(1000, 2)">1000 BZB</div>
                                <div :class="{ 'quantity-item': true, 'current': currentType == 3 }" @click="selectCount(500, 3)">500 BZB</div>
                                <div :class="{ 'quantity-item': true, 'current': currentType == 4 }" @click="selectCount(150, 4)">150 BZB</div>
                                <div @click="selectCount(0, 5)" style="float:left;margin-top: 30px;margin-left:15px;">
                                  <Input style="width:100%;" v-model="inputAmount"  size="large" :placeholder="$t('cexchange.inputtips1')"></Input>
                                </div>
                              </div>
                            </FormItem>
                            <FormItem class="trade-input">
                              <label class="before" style="width:180px;text-align:right;">{{$t('cexchange.choosecurrency')}}：</label>
                              <RadioGroup v-model="baseCoinUnit" style="text-align:left;margin-left:215px;float:left;">
                                  <div><Radio label="USDT" style="margin-bottom:10px;" >
                                    <span style="width:40px;display: inline-block;font-size: 14px;">USDT</span>
                                    <Input style="width:60%;" disabled :placeholder="$t('cexchange.assets') + assetUsdt"></Input>
                                    <Button style="width: 60px;margin-left:10px;" type="primary" @click="allBy(0)" size="small">{{$t('cexchange.all')}}</Button>
                                    <span style="margin-left: 20px;">1 USDT ≈ {{usdtRate}} BZB</span>
                                  </Radio><br/></div>
                                  <div><Radio label="BTC" style="margin-bottom:10px;">
                                    <span style="width:40px;display: inline-block;font-size: 14px;">BTC</span>
                                    <Input style="width:60%;" disabled :placeholder="$t('cexchange.assets') + assetBtc"></Input>
                                    <Button style="width: 60px;margin-left:10px;" type="primary" @click="allBy(1)" size="small">{{$t('cexchange.all')}}</Button>
                                    <span style="margin-left: 20px;">1 BTC ≈ {{btcRate}} BZB</span>
                                  </Radio><br/></div>
                                  <div><Radio label="ETH">
                                    <span style="width:40px;display: inline-block;font-size: 14px;">ETH</span>
                                    <Input style="width:60%;" disabled :placeholder="$t('cexchange.assets') + assetEth"></Input>
                                    <Button style="width: 60px;margin-left:10px;" type="primary" @click="allBy(2)" size="small">{{$t('cexchange.all')}}</Button>
                                    <span style="margin-left: 20px;">1 ETH ≈ {{ethRate}} BZB</span>
                                  </Radio><br/></div>
                              </RadioGroup>
                            </FormItem>
                            <FormItem>
                              <label class="before" style="width:180px;text-align:right;">{{$t('cexchange.totalpay')}}：</label>
                              <p style="text-align:left;margin-left:215px;float:left;font-size:26px;font-weight: bold;color:#dc8e00;">{{totalPaymentShow | fixed8}}</p>
                            </FormItem>
                            <FormItem style="margin-top: 40px;">
                              <label class="before"></label>
                              <Button v-if="isLogin" style="margin-left:140px;width: 400px;" size="large" type="primary" @click="confirm()">{{$t('cexchange.confirmpay')}}</Button>

                              <Button v-else style="margin-left:140px;width: 400px;" size="large" type="default">{{$t('cexchange.loginFirst')}}</Button>
                            </FormItem>
                          </Form>
                        </div>
                      </div>
                    </div>
                    <div></div>
                  </div>
                </div>
              </div>
            </TabPane>
          </Tabs>
      </div>
    </div>


    <Modal v-model="modal" width="450">
      <!-- <P style="color:red;font-weight: bold;">
        {{$t('uc.finance.withdraw.fundpwdtip')}}<br/>
        <Input type="password" v-model="fundpwd" :placeholder="$t('otc.chat.msg7')"></Input>
      </p> -->
      <p slot="header">
        {{$t("cexchange.confirm")}}
      </p>
      <p>{{$t("cexchange.confirmmsg")}}</p>
      <div slot="footer">
        <span style="margin-right:50px">{{$t("cexchange.cancel")}}</span>
        <span style="background:#f0ac19;color:#fff;width:80px;border-radius:30px;display:inline-block;text-align:center;height:30px;line-height: 30px;" @click="submit()">{{$t("cexchange.ok")}}</span>
      </div>
    </Modal>
  </div>
</template>

<script>
var moment = require("moment");
import expandRow from "@components/exchange/expand.vue";

export default {
  components: { expandRow },
  data() {
    const self = this;
    return {
      modal: false,
      currentType: 1,
      baseCoinUnit: "USDT",
      customAmount: 1500,
      inputAmount: "",
      tableMoney: [],
      assetUsdt: 0,
      assetBtc: 0,
      assetEth: 0,
      totalPayment: 0.00,
      rateData: {},
      usdtRate: 0,
      btcRate: 0,
      ethRate: 0,
      totalSupply: 0,
      nextIssuePrice: 0
    };
  },
  created: function() {
    this.init();
  },
  filters:{
    dateFormat: function(tick) {
      return moment(tick).format("YYYY-MM-DD HH:mm:ss");
    },
    fixedScale: function(value, scale) {
      return value.toFixed(scale);
    },
    fixed8: function(value){
      return value.toFixed(8);
    }
  },
  mounted () {

  },
  computed: {
    lang() {
      return this.$store.state.lang;
    },
    langPram(){
      return this.$store.state.lang;
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    },
    currentPriceShow: function(){
      if(this.baseCoinUnit == "USDT") {
        return this.usdtRate;
      }else if(this.baseCoinUnit == "BTC") {
        return this.btcRate;
      }else if(this.baseCoinUnit == "ETH") {
        return this.ethRate;
      }
    },
    nextPriceShow: function(){
      if(this.baseCoinUnit == "USDT") {
        return (1/this.rateData.nextUsdtRate).toFixed(2);
      }else if(this.baseCoinUnit == "BTC") {
        return (1/this.rateData.nextBtcRate).toFixed(8);
      }else if(this.baseCoinUnit == "ETH") {
        return (1/this.rateData.nextEthRate).toFixed(8);
      }
    },
    totalPaymentShow: function(){

      if(this.currentType != 5) {
        // customAmount 计算
        return this.customAmount * this.currentPriceShow;
      }else{
        // inputAmount 计算
        return this.inputAmount * this.currentPriceShow;
      }
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-cexchange");
      if(this.isLogin){
        this.getAssets();
      }
      this.getRate();
    },

    selectCount(count, typeId) {
      if(typeId != 5){
        this.customAmount = count;
        this.inputAmount = "";
      }
      this.currentType = typeId;
    },
    getRate(){
      this.$http.post(this.host + this.api.market.thumb, {}).then(response => {
        var resp = response.body;
        var btcPrice = 0;
        var ethPrice = 0;
        var kwcPrice = 0;
        for (var i = 0; i < resp.length; i++) {
          var coin = resp[i];
          if(coin.symbol == "BTC/USDT") {
            btcPrice = coin.close;
          }
          if(coin.symbol == "ETH/USDT") {
            ethPrice = coin.close;
          }
          if(coin.symbol == "BZB/USDT") {
            kwcPrice = coin.close;
          }
        }
        // 计算比例
        this.usdtRate = kwcPrice;
        this.btcRate = (kwcPrice / btcPrice ).toFixed(8);
        this.ethRate = (kwcPrice / ethPrice).toFixed(8);
      });
    },
    getAssets(){
      this.$http.post(this.host + "/uc/asset/wallet").then(response => {
        var resp = response.body;
        if (resp.code == 0) {
          this.tableMoney = resp.data;
          for (let i = 0; i < this.tableMoney.length; i++) {
            if(this.tableMoney[i].coin.unit == "USDT") {
              this.assetUsdt = this.tableMoney[i].balance;
            }
            if(this.tableMoney[i].coin.unit == "BTC") {
              this.assetBtc = this.tableMoney[i].balance;
            }
            if(this.tableMoney[i].coin.unit == "ETH") {
              this.assetEth = this.tableMoney[i].balance;
            }
          }
        } else {
          this.$Message.error(this.loginmsg);
        }
      });
    },
    allBy(type){
      if(!this.isLogin) {
        this.$Message.error("请先登录！");
      }
      if(type == 0) {
        // USDT
        this.baseCoinUnit = "USDT";
        this.currentType = 5;
        this.inputAmount = (this.usdtRate * this.assetUsdt).toFixed(2);
        this.totalPayment = this.assetUsdt;
      }else if(type == 1) {
        this.baseCoinUnit = "BTC";
        this.currentType = 5;
        this.inputAmount = (this.btcRate * this.assetBtc).toFixed(2);
        this.totalPayment = this.assetBtc;
      }else if(type == 2) {
        this.baseCoinUnit = "ETH";
        this.currentType = 5;
        this.inputAmount = (this.ethRate * this.assetEth).toFixed(2);
        this.totalPayment = this.assetEth;
      }
    },
    confirm(){
      var temAmount = 0;
      if(this.currentType != 5) {
        temAmount = this.customAmount;
      }else{
        temAmount = this.inputAmount;
      }
      if(temAmount <= 0) {
        this.$Message.error("The amount must > 0");
        return;
      }
      this.modal = true;
    },
    cancel(){
      this.modal = false;
    },
    submit(){
      this.modal = false;
      var that = this;
      let params = {};
      params.baseUnit = this.baseCoinUnit;
      if(this.currentType != 5) {
        params.amount = this.customAmount;
      }else{
        params.amount = this.inputAmount;
      }
      if(params.amount <= 0) {
        this.$Message.error("The amount must > 0");
        return;
      }

      var temPrice = 0;
      if(this.baseCoinUnit == "USDT") {
        temPrice =  this.usdtRate;
      }else if(this.baseCoinUnit == "BTC") {
        temPrice =  this.btcRate;
      }else if(this.baseCoinUnit == "ETH") {
        temPrice =  this.ethRate;
      }
      params.price = temPrice;

      this.$Spin.show();
      this.$http
        .post(this.host + "/uc/cexchange/do-exchange", params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Notice.success({
              title: that.$t("cexchange.success"),
              desc: resp.message
            });
          } else {
            this.$Notice.error({
              title: that.$t("cexchange.tip"),
              desc: resp.message
            });
          }
          this.$Spin.hide();
        });
    }
  }
};
</script>

<style>
.ctc .item-title{
  font-size: 20px;
  text-align: center;
  font-weight: bold;
  color: rgb(188, 188, 188);
}
.ctc .red{
  color: #f2334f;
}
.ctc .green{
  color: #45b854;
}
.ctc .item-title .unit{
  font-size: 14px;
}
.cexchange .item-desc{
  font-size: 12px;
  text-align: center;
  color: #7c7f82;
}
.cexchange .notice-bottom{
  margin-top: 5px;height: 55px;background-color:#192330;padding-top: 12px;color: rgb(42, 147, 255);
}
.cexchange .notice-btn-left{
  height: 30px;line-height: 30px;width: 42%;margin-left: 5%;float:left;border-radius:3px;border: 1px solid rgb(0, 116, 235);
}
.cexchange .notice-btn-left:hover{
  cursor: pointer;
}
.cexchange #sendCode {
  position: absolute;
  border: none;
  background: none;
  top: 6px;
  outline: none;
  right: 0;
  width: 30%;
  color: #f0ac19;
  cursor: pointer;
  height: 20px;
  line-height: 20px;
  border-left: 1px solid #dddee1;
}
.cexchange .notice-btn-right{
  height: 30px;line-height: 30px;width: 42%;margin-right: 5%;float:right;border-radius:3px;border: 1px solid rgb(0, 116, 235);
}
.cexchange .notice-btn-right:hover{
  cursor: pointer;
}
.cexchange .ivu-tabs-bar{
    border-bottom: 1px solid #323c53;
    font-size: 18px;
}
.cexchange .ivu-tabs-nav .ivu-tabs-tab:hover{
    color: #f0a70a;
}
.cexchange .ivu-tabs-nav .ivu-tabs-tab:hover, .cexchange .ivu-tabs-nav .ivu-tabs-tab-active{
    color: #f0a70a;
    font-size: 18px;
}
.cexchange .ivu-tabs-ink-bar{
    background-color: #f0a70a;
}
.cexchange .buy_total{
  border-top: 1px solid #323c53;
  padding-top: 30px;
  margin-bottom: 30px;
}
.cexchange .trade_bd_ctc{
  width: 100%;
}
.cexchange .trade_bd_ctc .panel {
    position: relative;
    z-index: 2;
    float: left;
    width: 100%;
    height: 620px;
    margin-top: 0;
    margin-right: 0;
    border: 0 solid transparent;
    padding-top: 35px;
}

.cexchange .trade_panel{
  background: transparent!important;
}
.cexchange .trade_panel .panel .hd {
    line-height: 20px;
    height: 20px;
    border-bottom: 1px solid #1F2943;
    margin-bottom: 5px;
}

.cexchange .trade_panel .panel .hd span {
    padding-left: 0;
    font-size: 12px;
    margin: 0 3px;
    float:right;
}
.ctc-order-status{
  text-align:center;margin-bottom: 15px;background: #f0a70a;padding: 5px 0px;border-radius: 2px;color: #000000;
}
.cexchange .trade_panel .panel .hd b {
    padding-left: 0;
    font-size: 12px;
    color: #7A98F7;
    float:right;
}

.cexchange .trade_panel .panel .hd.hd_login a {
    float: right;
    text-decoration: none;
    font-size: 12px;
    margin-right: 10px;
}

.cexchange .trade_panel .panel.panel_buy {
    padding-right: 35px;
    padding-left: 35px;
    background: #192330;
}

.cexchange .trade_panel .panel.panel_sell {
    padding-right: 35px;
    padding-left: 35px;
    background: #192330;
    margin-left: 5px;
}
.cexchange .trade_wrap .buy-input .ivu-input {
  color: rgb(220, 142, 0);
  font-weight: bold;
  font-size: 20px;
  height: 35px;
}
.cexchange .trade_wrap .sell-input .ivu-input {
  color: #f2334f;
  font-weight: bold;
  font-size: 20px;
  height: 35px;
}
.cexchange .ivu-tabs{
  color: #a5a5a5;
}
.cexchange .trade_wrap .trade-input .ivu-input {
    border: 1px solid #27313e;
    color: #fff;
    height: 35px;
    border-radius: 0;
}

.cexchange .trade_wrap .ivu-input-wrapper {
    outline: none;
}

.cexchange .trade_wrap .ivu-input:focus,
.cexchange .trade_wrap .ivu-input:hover {
    box-shadow: none;
    outline: none;
}
.cexchange .trade_wrap .ivu-input-number-input:focus,
.cexchange .trade_wrap .ivu-input-number-input:hover {
    box-shadow: none;
    border-color: #41546d;
    outline: none;
}

.cexchange .trade_wrap .ivu-input:hover {
    box-shadow: none;
    outline: none;
}
.cexchange .trade_wrap .ivu-input-number-input:hover {
    box-shadow: none;
    border-color: #41546d;
    outline: none;
}
.cexchange .trade_wrap .ivu-form-item-content input{
    padding-left: 5px;
    text-align:center;
    padding-right: 55px;
    font-size: 16px;
}
.cexchange .trade_wrap .ivu-form-item-content input::-webkit-input-placeholder {
    font-size: 14px;
    color: #515a6e;
    margin-bottom: 10px;
    text-align: left;
}
.cexchange .trade_wrap .trade-input input::-webkit-input-placeholder {

}
.cexchange .trade_wrap .ivu-form-item-content label.before {
    position: absolute;
    top: 4px;
    left: 10px;
    color: #7c7f82;
    z-index: 2;
    font-size: 14px;
}

.cexchange .trade_wrap .ivu-form-item-content label.after {
    position: absolute;
    top: 4px;
    right: 10px;
    color: #7c7f82;
    font-size: 14px;
}
.trade_bd_ctc Button {
    width: 100%;
    border: 0;
    color: #fff;
}

.trade_bd_ctc Button.bg-red {
    background-color: #f15057;
}
.trade_bd_ctc Button.bg-red:hover {
    background-color: #ff7278;
}

.trade_bd_ctc Button.bg-green {
    background-color: #00b275;
}
.trade_bd_ctc Button.bg-green:hover {
    background-color: #01ce88;
}

.trade_bd_ctc Button.bg-gray {
    background-color: #35475b;
    cursor: not-allowed;
    color: #9fabb5;
}
.trade_bd_ctc Button.bg-gray:hover{
    color: #9fabb5!important;
}
.trade_bd_ctc Button:hover {
    /* background: #54679F; */
}
.cexchange .trade_wrap .ivu-btn{
  color: #FFF!important;
}
.cexchange .total{
  min-height: 90px;
}
.trade-input .ivu-form-item-content .ivu-radio-group .ivu-radio-wrapper{
  cursor: auto!important;
}
.cexchange .trade_wrap .ivu-btn.ivu-btn-small{
  padding: 2px 5px!important;
}
</style>
<style lang="scss" scoped>
  .cexchange {
    height: 100%;
    background-size: cover;
    position: relative;
    overflow: hidden;
    padding-bottom: 50px;
    padding-top: 60px;
    color: #fff;
  }
  .cexchange .bannerimg {
    display: block;
    width: 100%;
  }
  .ctc_container {
    padding: 0 15%;
    text-align: center;
    height: 100%;
    > h1 {
      margin-top: -150px;
      font-size: 32px;
      line-height: 1;
      padding: 50px 0 20px 0;
      letter-spacing: 3px;
    }
  }
  .cexchange .main {
    margin-top: 55px;
    display: flex;
    justify-content: space-between;
    flex-direction: row;
    flex-wrap: wrap;
  }
  .ctc-container{
    min-height: 470px;
  }
  .bottom-panel{
      border-top: 1px solid rgb(237, 237, 237);margin-top: 15px;
      .bottom{
        display: flex;
        flex-direction: row;
        justify-content: space-between;
        span{
          font-size: 12px;
          color: #a7a7a7;
          margin-top:15px;
        }
        button, a{
          margin-top: 11px;
        }
        a.ivu-btn-primary{
          background:#0095ff;
        }
        a.ivu-btn-primary:hover{
          background: #2ba7ff;
        }
      }
  }
  .right{
    float: right;
  }
  .left{
    float: left;
  }
  .gray{
    color: #a7a7a7;
  }
  .quantity-group{
    margin-left:200px;
    .quantity-item{
      float:left;
      padding: 0px 15px;
      border-radius: 3px;
      border: 1px solid #515a6e;
      margin-left: 15px;
      &:hover{
        border: 1px solid #f0ac19;
        cursor:pointer;
      }
    }
    .current{
      border: 1px solid #f0ac19;
    }
  }
</style>
