<template>
  <div class="login_form app-download">
    <div class="login_right">
      <div style="color: #000;margin-bottom: 60px;padding-top: 160px;width: 100%;text-align:center;z-index: 10;">
        <img src="../../assets/images/applogo.png" style="width: 22%;border-radius: 15px;"></img>
        <p style="font-size:18px;">BIZZAN</p>
        <p style="font-size:12px;margin-top: 10px;color:#888;">Latest Version：v{{version}}</p>
        <p style="font-size:10px;margin-top: 10px;color:#888;letter-spacing: 1px;">Publish Time：{{publishTime}}</p>
        <p style="font-size:18px;margin-top: 45px;">
            <span style="border: 1px solid #F90; padding: 8px 30px;border-radius: 5px;background-color:#F90;color:#FFF;" @click="downloadClick">
            <Icon custom="i-icon iconfont iconupload-demo" style="font-size: 24px;margin-right: 5px;" />Android</span>
			<span style="border: 1px solid #F90; padding: 8px 50px;border-radius: 5px;background-color:#F90;color:#FFF;" @click="downloadClick2">
			<Icon custom="i-icon iconfont iconupload-demo" style="font-size: 24px;margin-right: 5px;" />iOS</span>
        </p>
        <p style="font-size:12px;margin-top: 20px;color:#888;"></p>
      </div>
    </div>

    <div class="section" id="page4">
      <ul>
        <li>
          <div><img src="https://bizzanex.oss-cn-hangzhou.aliyuncs.com/2019/08/08/download1.png" alt=""></div>
          <p class="title">Extreme Experience</p>
          <p>Meticulously optimized interface display, experience smooth operation response</p>
        </li>
        <li>
          <div><img src="https://bizzanex.oss-cn-hangzhou.aliyuncs.com/2019/08/08/download2.png" alt=""></div>
          <p class="title">Currency Quotes</p>
          <p>Support MACD, KDJ, RSI, BOLL and other professional indicators</p>
        </li>
        <li>
          <div><img src="https://bizzanex.oss-cn-hangzhou.aliyuncs.com/2019/08/08/download5.png" alt=""></div>
          <p class="title">Currency Transaction</p>
          <p>Support two methods of limit order and market order</p>
        </li>
        <li>
          <div><img src="https://bizzanex.oss-cn-hangzhou.aliyuncs.com/2019/08/08/download3.png" alt=""></div>
          <p class="title">Fiat currency transaction</p>
          <p>Quality acceptors, to ensure the smooth flow of funds</p>
        </li>
        <li>
          <div><img src="https://bizzanex.oss-cn-hangzhou.aliyuncs.com/2019/08/08/download4.png" alt=""></div>
          <p class="title">Asset Center</p>
          <p>Focus on asset changes anytime, anywhere, fast recharge/withdraw</p>
        </li>
      </ul>
    </div>

    <div class="cover" id="cover" @click="coverClick"><img src="https://bizzanex.oss-cn-hangzhou.aliyuncs.com/2019/08/08/appdowncover.png"></img></div>
  </div>
</template>
<style scoped lang="scss">
.page-view2{
  .page-content{
    .layout{
      display: none;
    }
  }
}
#page4 {
  background: transparent;
  padding: 20px 0 80px 0;
  ul {
    width: 99%;
    margin: 0 auto;
    li {
      flex: 0 0  25%;
      display: inline-block;
      width: 100%;
      padding: 0 15px;
      div {
        border-radius: 50%;
        vertical-align: middle;
        text-align: center;
        margin: 0 auto;
        img {
          width: 80%;
          margin-top: 28px;
        }
      }
      p {
        font-size: 14px;
        margin: 10px 0;
        text-align: center;
        color: #828ea1;
      }
      p.title {
        color: #000;
        font-size: 18px;
        font-weight: 400;
      }
    }
  }
}
</style>
<script>
import $ from "jquery";
export default {
  data() {
    return {
      country: "中国",
      version: "1.0.0",
      publishTime: "2019/08/08/08/08 12:32:00"
    };
  },
  watch: {
    lang: function() {

    }
  },
  computed: {
    lang: function() {
      return this.$store.state.lang;
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    }
  },
  created: function() {
    window.scrollTo(0, 0);
    this.init();
  },
  methods: {
    init() {
      window.document.title = (this.lang == "zh_CN" ? "APP下载 - " : "APP Download - ") + "BIZZAN | Global digital currency trading platform";
      this.getVersion();
    },
    downloadClick(){
      if(this.isWeiXin()){
        document.getElementById("cover").style.display = "block";
      }else{
        location.href = "https://bizzanex.oss-cn-hangzhou.aliyuncs.com/appdownload/BIZZAN.apk";
      }
    },
    downloadClick2(){
      if(this.isWeiXin()){
        document.getElementById("cover").style.display = "block";
      }else{
        location.href = "https://testflight.apple.com/join/Exf8d0uH";
      }
    },
    coverClick(){
      document.getElementById("cover").style.display = "none";
    },
    getVersion(){
      let self = this;
      this.$http
        .post(this.host + "/uc/ancillary/system/app/version/0", null)
        .then(response => {
          var result = response.body;
          if (result.code == 0) {
            self.version = result.data.version;
            self.publishTime = result.data.publishTime;
          }
        });
    },
    isWeiXin(){
      var ua = window.navigator.userAgent.toLowerCase();
      if(ua.match(/MicroMessenger/i) == 'micromessenger'){
        return true;
      }else{
        return false;
      }
    }
  }
};
</script>
<style lang="scss">
.app-download{
  background:#f2f6fa!important;
}
.login_form {
  .cover{
    width: 100%;
    height: 100%;
    position: fixed;
    top: 0;
    right:0;
    background: rgba(0,0,0,0.8);
    z-index: 9999;
    display:none;
  }
  .cover img{
    width: 100%;
  }
  .login_right {
    form.ivu-form.ivu-form-label-right.ivu-form-inline {
      text-align:center;
      .ivu-form-item {
        .ivu-form-item-content {
          .ivu-input-wrapper.ivu-input-type {
            .ivu-input {
              border: none;
              border-bottom: 1px solid #27313e;
              font-size: 14px;
              background:transparent;
              border-radius:0;
              // color:#fff;
              &:focus {
                border: none;
                border-bottom: 1px solid #27313e;
                -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
                -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
                box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              }
            }
          }
        }
      }
      .check-agree {
        .ivu-checkbox-wrapper {
          .ivu-checkbox-input {
            &:focus {
              border: none;
              outline: none;
              -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              -webkit-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
              box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
            }
          }
        }
        .ivu-checkbox-wrapper.ivu-checkbox-wrapper-checked {
          .ivu-checkbox.ivu-checkbox-checked {
            .ivu-checkbox-inner {
              border: 1px solid #f0ac19;
              background-color: #f0ac19;
            }
          }

        }
        .ivu-checkbox-wrapper.ivu-checkbox-default{
          .ivu-checkbox{
            .ivu-checkbox-inner{
              background:transparent;
            }
          }
        }
      }
    }
  }
}
</style>
