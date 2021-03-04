
<template>
  <div class="news">
    <div class="main">
      <div class="invite-content">
        <h2>{{$t('invite.ruledetail')}}</h2>
        <div class="content">
          <p style="margin-bottom:20px;border-left: 5px solid #e7e7e7;padding-left: 25px;">{{$t('invite.ruleprofile1')}}<b>{{$t('invite.ruleprofile2')}}</b><b>{{$t('invite.ruleprofile3')}}</b>{{$t('invite.ruleprofile4')}}</p>
          <p>{{$t('invite.ruletext1')}}</p>
          <p>{{$t('invite.ruletext2')}}</p>
          <p>{{$t('invite.ruletext3')}}</p>
          <p>{{$t('invite.ruletext4')}}</p>
          <p>{{$t('invite.ruletext5')}}</p>
          <p>{{$t('invite.ruletext6')}}</p>
          <p>{{$t('invite.ruletext7')}}</p>
          <p>{{$t('invite.ruletext1')}}</p>
          <p>{{$t('invite.ruletext2')}}</p>
          <p>{{$t('invite.ruletext3')}}</p>
          <p>{{$t('invite.ruletext4')}}</p>
          <p>{{$t('invite.ruletext5')}}</p>
          <p>{{$t('invite.ruletext6')}}</p>
          <p>{{$t('invite.ruletext7')}}</p>
          <p>{{$t('invite.ruletext1')}}</p>
          <p>{{$t('invite.ruletext2')}}</p>
          <p>{{$t('invite.ruletext3')}}</p>
          <p>{{$t('invite.ruletext4')}}</p>
          <p>{{$t('invite.ruletext5')}}</p>
          <p>{{$t('invite.ruletext6')}}</p>
          <p>{{$t('invite.ruletext7')}}</p>
          <p>{{$t('invite.ruletext1')}}</p>
          <p>{{$t('invite.ruletext2')}}</p>
          <p>{{$t('invite.ruletext3')}}</p>
          <p>{{$t('invite.ruletext4')}}</p>
          <p>{{$t('invite.ruletext5')}}</p>
          <p>{{$t('invite.ruletext6')}}</p>
          <p>{{$t('invite.ruletext7')}}</p>
        </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Vue from "vue";
import VueQriously from "vue-qriously";
import html2canvas from 'html2canvas';

Vue.use(VueQriously);

export default {
  data() {
    let self = this;
    return {
      
    };
  },
  created: function() {
    this.init();
  },
  computed: {
    lang() {
      this.updateLangData();
      return this.$store.state.lang;
    },
    langPram(){
      return this.$store.state.lang;
    },
    isLogin: function() {
      return this.$store.getters.isLogin;
    }
  },
  methods: {
    init() {
      this.$store.commit("navigate", "nav-news");
    }
  }
};
</script>
<style lang="scss" scoped>
.news {
  display:flex;
  flex-direction: column;
  background: linear-gradient(200deg, #00a1ff, #2b85e4, #003b6d);
  padding: 0 12%;
  padding-bottom: 100px;
  min-height: 400px;
  p.headertip{
    font-size:12px;text-align:right;color:rgba(0, 0, 0, 0.48);text-shadow: 0 0 25px #ffffff;
  }
  .main {
    min-height: 300px;
    width: 100%;
    margin: 0 auto;
    border-radius: 6px;
    margin-top: 80px;
    .invite-content{
      background: #FFF;
      padding: 30px 30px;
      margin-bottom: 30px;
      color: #6d6d6d;
      border-radius: 5px;
      box-shadow: 0px 0px 20px #909090;
      .content{
        margin-top: 20px;
        min-height: 100px;
        padding-left: 40px;
        line-height: 30px;
        letter-spacing: 1px;
        .percent-table{
          margin-bottom: 10px;
          background:#EDEDED;
          text-align:center;margin-left: 30px;font-size:12px;width:90%;margin-top: 10px;
          border-right: 1px solid #c8c8c8;
          border-bottom: 1px solid #c8c8c8;
          border-collapse:collapse;
          tr{
            background:#FFF;
            td{
              border-left: 1px solid #c8c8c8;
              border-top:1px solid #c8c8c8;
            }
          }
        }
        .rule-update{
          font-size: 12px;text-align:right;color:#AFAFAF;margin-top: 20px;padding-top: 20px;border-top: 1px solid #EDEDED;
        }
      }
      >h2{
        padding-bottom: 10px;
        border-bottom: 1px solid #EDEDED;
      }
    }
  }
}
</style>

<style scoped>
  .ivu-table-wrapper .ivu-table .ivu-table-row td{
    background-color: transparent;
    border: none;
    border-bottom: 1px solid #e3e3e3;
    color: #151515;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-header th{
    background-color: #F90;
    color: #000;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-row:hover{
    background: #f7f7f7!important;
  }
  .ivu-table-wrapper .ivu-table{
    background-color: #ffffff;
  }
  .ivu-table:after{
    width: 0;
  }
  .ivu-table-wrapper .ivu-table .ivu-table-row td{
    border-bottom: none;
  }
</style>
