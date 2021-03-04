<template>
  <div class="nav-rights">
    <div class="nav-right">
      <div class="bill_box_address">
        <section class="trade-group merchant-tops">
          <h1 class="tips-word1">{{$t('uc.finance.withdraw.withdrawcode')}}</h1>
        </section>
        <section>
          <div class="table-inner">
            <div class="action-inner">
              <div class="inner-left">
                <p class="describe">{{$t('uc.finance.withdraw.symbol')}}</p>
                <Select v-model="coinType" style="width:100px;margin-top: 10px;" size="large" :placeholder="$t('common.pleaseselect')">
                  <Option v-for="item in coinList" :value="item" :key="item">{{ item }}</Option>
                </Select>
              </div>
              <div class="inner-box deposit-address mt25">
                <p class="describe">{{$t('uc.finance.withdraw.num')}}</p>
                <div class="title">
                  <div class="input-group" style="margin-top:10px;position:relative;">
                    <InputNumber v-model="withdrawAmount" :placeholder="$t('uc.finance.withdraw.numtip1')" size="large" style="width:100%;"></InputNumber>
                    <span class="input-group-addon addon-tag uppercase">{{coinType}}</span>
                  </div>
                </div>
              </div>
              <div class="mt25">
                <p class="describe">&nbsp;</p>
                <div class="title">
                  <Button id="withdrawCodeSubmit" @click='generate' size="large" style="color:#fff;background:#f0a70a;border:1px solid #f0a70a;margin-top:10px;">{{$t('uc.finance.withdraw.generateCode')}}</Button>
                </div>
              </div>
            </div>
            <div class="action-content">
              <div class="action-body">
                <p class="acb-p1 describe">{{$t('uc.finance.withdraw.withdrawcodelist')}}</p>
                <div class="order-table">
                  <Table :columns="tableColumnsWithdrawRecord" :data="dataWithdrawCodeList" :disabled-hover="true"></Table>
                  <div style="margin: 10px;overflow: hidden">
                    <div style="float: right;">
                      <Page :total="dataCount" :current="1" @on-change="changePage" :loading="loading" class="recharge_btn"></Page>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
    <!-- model1 -->
    <Modal v-model="modal1" width="360">
      <p slot="header" style="color:#f60;text-align:center">
        <Icon type="ios-mail" size="20" color="#00b5f6;" />
        <span>{{$t('uc.finance.withdraw.generatecodesuccess')}}</span>
      </p>
      <div style="text-align:center">
        <p style="border-radius: 10px;padding: 20px 10px;background: #222f40;">{{ withdrawCode }}</p>
      </div>
    </Modal>
    <!-- model2 -->
    <Modal v-model="modal2" width="360">
      <p slot="header" style="color:#f60;text-align:center">
        <Icon type="ios-mail" size="20" color="#00b5f6;" />
        <span>{{$t('uc.finance.withdraw.safevalidate')}}</span>
      </p>
      <div style="text-align:center">
        <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="0">
          <FormItem>
            <Input type="password" v-model="formValidate.fundpwd" :placeholder="$t('otc.chat.msg7')"></Input>
          </FormItem>
        </Form>
      </div>
      <div slot="footer">
        <Button type="primary" size="large" long @click="submit">{{$t('uc.finance.withdraw.submit')}}</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
export default {
  components: {},

  data() {
    var that = this;
    return {
      interval: function() {},
      disbtn: false,
      dataCount: 10,
      loading: true,
      //else
      sendMsgDisabled1: false,
      sendMsgDisabled2: false,
      time1: 60, // 发送验证码倒计时
      time2: 60, // 发送验证码倒计时
      modal1: false,
      modal2: false,
      modal_loading: false,
      withdrawAddr: "",
      remark: "",
      coinType: "",
      withdrawAmount: 0,
      coinList: [],
      withdrawCode: "c10befbd3176ebf65653c85fd83cb7ca",
      tableColumnsWithdrawRecord: [
        {
          title: this.$t("uc.finance.withdraw.time"),
          width: 100,
          key: "createTime"
        },
        {
          title: this.$t("uc.finance.withdraw.symbol"),
          width: 80,
          key: "unit"
        },
        {
          title: this.$t("uc.finance.withdraw.num"),
          width: 100,
          key: "amount"
        },
        {
          title: this.$t("uc.finance.withdraw.withdrawcode"),
          key: "withdrawcode"
        },
        {
          title: this.$t("uc.finance.withdraw.withdrawcodestatus"),
          width: 100,
          key: "status"
        }
      ],
      dataWithdrawCodeList: [],
      formValidate: {
        fundpwd: ""
      },
      ruleValidate: {
        fundpwd: [
          {
            required: this.fundpwd,
            message: this.$t("uc.finance.withdraw.fundpwdtip"),
            trigger: "blur"
          }
        ]
      }
    };
  },
  created() {
    this.getMember();
    this.getList(0, 10);
    this.coinType = this.$route.query.name;
    this.getCoin();
  },
  methods: {
    refresh() {
      (this.coinType = null), (this.withdrawAddr = null), (this.remark = null);
      this.getList(0, 10);
    },
    getMember() {
      //获取个人安全信息
      this.$http
        .post(this.host + "/uc/approve/security/setting")
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            if (resp.data.mobilePhone) {
              // TODO
            } else {
              // TODO
            }
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    getCoin() {
      //币种
      this.$http
        .post(this.host + "/uc/withdraw/support/coin")
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            for (let i = 0; i < resp.data.length; i++) {
              this.coinList.push(resp.data[i]);
            }
          } else {
            this.$Message.error(resp.message);
          }
        });
    },
    getList(pageNo, pageSize) {
      //获取地址
      let params = {};
      params["page"] = pageNo;
      params["pageSize"] = pageSize;
      this.$http
        .post(this.host + "/uc/withdrawcode/record", params)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0 && resp.data.content) {
            this.dataWithdrawList = resp.data.content;
            this.dataCount = resp.data.totalElement;
          } else {
            this.$Message.error(resp.message);
          }
          this.loading = false;
        });
    },
    generate() {
      let interval = setInterval(() => {
        if (this.time2 <= 0) {
          this.sendMsgDisabled2 = false;
          window.clearInterval(interval);
          this.disbtn = false;
        }
      }, 1000);
      if (!this.coinType) {
        this.$Message.warning(this.$t("uc.finance.withdraw.symboltip"));
      } else if (!this.withdrawAmount) {
        this.$Message.warning(this.$t("uc.finance.withdraw.amounttip"));
      }else if (this.coinType && this.withdrawAmount) {
        this.modal2 = true;
      }
    },
    changePage(index) {
      this.getList(index, 10);
    },
    handleSubmit(name) {
      console.log("valid test...");
      this.$refs[name].validate(valid => {
        if (valid) {
          this.submit();
        } else {
          console.log("1valid test...");
          this.$Message.error(this.$t("uc.finance.withdraw.savemsg1"));
        }
      });
    },
    submit() {
      let param = {};
      param["unit"] = this.coinType;
      param["amount"] = this.withdrawAmount;
      param["jyPassword"] = this.formValidate.fundpwd;

      this.$http
        .post(this.host + "/uc/withdrawcode/apply/code", param)
        .then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.$Message.success(this.$t("uc.finance.withdraw.savemsg2"));

            this.modal2 = false;
            this.withdrawCode = resp.data.withdrawCode;
            this.modal1 = true;
          } else {
            this.$Message.error(resp.message);
          }
        });
    }
  },
  computed: {}
};
</script>

<style scoped lang="scss">
.nav-rights {
  .nav-right {
    .bill_box_address {
      section.trade-group.merchant-tops {
        .tips-word1 {
          margin-bottom: 20px;
          text-align: left;
          font-weight: normal;
          margin-left: 30px;
        }
      }
      .table-inner {
        .action-inner {
          display: table;
          padding: 0 30px;
          width: 100%;
          .inner-left {
            display: table-cell;
            width: 15%;
          }
        }
      }
    }
  }
}
.btnbox {
  text-align: right;
  padding: 25px 30px;
}

.deposit-address {
  width: 45% !important;
}

.mt25 {
  display: table-cell;
  width: 43%;
}

p.describe {
  font-size: 16px;
}

.action-content {
  padding: 20px 30px;
}
/* common */
.order-table {
  margin-top: 20px;
}

.content-wrap {
  // background: #f5f5f5;
  min-height: 750px;
}

.container {
  padding-top: 30px;
  margin: 0 auto;
}
span.addon-tag:last-child {
    padding: 0;
    border: none;
    background: none;
    cursor: default;
    position: absolute;
    right: 26px;
    top: 6px;
}
</style>
<style lang="scss">
.nav-rights {
  .nav-right {
    .bill_box_address {
      .table-inner {
        .action-inner {
          .inner-left {
            .ivu-select-dropdown .ivu-select-item {
              padding: 6px 16px;
            }
          }
        }
        .btnbox .ivu-btn {
          &:focus {
            -moz-box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
            -webkit-box-shadow: 2px 2px 5px transparent,
              -2px -2px 4px transparent;
            box-shadow: 2px 2px 5px transparent, -2px -2px 4px transparent;
          }
        }
      }
    }
  }
}
</style>


