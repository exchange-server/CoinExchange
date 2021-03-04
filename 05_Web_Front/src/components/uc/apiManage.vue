<template>
  <div class="nav-rights">
    <div class="main api-manage">
      <Card :bordered="false" class="content">
        <p slot="title">{{$t('uc.api.createApi')}}</p>
        <div class="content_header">
          <div class="add">
            <Form :model="formItem" label-position="top">
              <FormItem :label="$t('uc.api.mark')">
                <Input v-model="formItem.remark"></Input>
              </FormItem>
              <FormItem :label="$t('uc.api.bindAddress')">
                <Input v-model="formItem.bindIp"></Input>
              </FormItem>
            </Form>
            <Button type="primary" @click="codeVerify">{{$t('uc.api.create')}}</Button>
          </div>
          <div class="prompt">
            <p style="color:#7a8294; font-weight: 600;margin-bottom: 20px;">{{$t('uc.api.hint')}}</p>
            <ul style="text-align: left;">
              <li>
                {{$t('uc.api.part1')}}
                <router-link to>{{$t('uc.api.apiDoc')}}</router-link>
                {{$t('uc.api.howUse')}}
              </li>
              <li>{{$t('uc.api.part2')}}</li>
              <li>{{$t('uc.api.part3')}}</li>
            </ul>
          </div>
        </div>
      </Card>
      <Card :bordered="false" class="content">
        <p slot="title">{{$t('uc.api.myApiKey')}}</p>
        <Table :columns="myColumns" :data="tableData" :no-data-text="$t('common.nodata')"></Table>
      </Card>
    </div>
    <!--编辑API-->
    <Modal v-model="editor" :title="$t('uc.api.edit')">
      <Form :model="editorFormItem" label-position="top">
        <FormItem :label="$t('uc.api.mark')">
          <Input v-model="editorFormItem.remark"></Input>
        </FormItem>
        <FormItem :label="$t('uc.api.bindIpAddress')">
          <Input v-model="editorFormItem.bindIp"></Input>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="editor=false">{{$t('uc.api.cancel')}}</Button>
        <Button type="primary" size="large" @click="update">{{$t('uc.api.okText')}}</Button>
      </div>
    </Modal>
    <!--删除-->
    <Modal
      v-model="del"
      :title="$t('uc.api.delete')"
      @on-ok="onDel(id)"
      @on-cancel="del=false"
      :ok-text="$t('uc.api.okText')"
      :cancel-text="$t('uc.api.cancel')"
    >
      <p>{{$t('uc.api.sureDelete')}}</p>
    </Modal>
    <!--秘钥-->
    <Modal
      v-model="show"
      :title="$t('uc.api.access')"
      @on-ok="show=false"
      :mask-closable="false"
      :ok-text="$t('uc.api.okText')"
      :cancel-text="$t('uc.api.cancel')"
    >
      <p class="screat">{{$t('uc.api.tips')}}</p>
      <p class="screat">
        <!--{{screat}}-->
        <span>{{screat}}</span>
        <span
          v-clipboard:copy="screat"
          v-clipboard:success="onCopy"
          v-clipboard:error="onError"
          class="blue"
        >{{$t('uc.api.copy')}}</span>
      </p>
    </Modal>
    <!--添加短信校验-->
    <Modal v-model="verify" :title="$t('uc.api.edit')">
      <Form :model="formItem" label-position="top">
        <FormItem :label="$t('uc.safe.phonecode')" prop="vailCode3">
          <Input v-model="formItem.code" size="large">
            <div class="timebox" slot="append">
              <Button @click="sendCode(1)" :disabled="sendMsgDisabled">
                <span v-if="sendMsgDisabled">{{time+$t('uc.safe.second')}}</span>
                <span v-if="!sendMsgDisabled">{{$t('uc.safe.clickget')}}</span>
              </Button>
            </div>
          </Input>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" size="large" @click="verify=false">{{$t('uc.api.cancel')}}</Button>
        <Button type="primary" size="large" @click="make">{{$t('uc.api.okText')}}</Button>
      </div>
    </Modal>
    <!--编辑短信校验-->
    <div class="let-code">
      <Modal v-model="verifyEditor" :title="$t('uc.api.edit')">
        <Form :model="editorFormItem" label-position="top">
          <FormItem :label="$t('uc.safe.phonecode')" prop="vailCode3">
            <Input v-model="editorFormItem.code" size="large">
              <div class="timebox" slot="append">
                <Button @click="sendCode(2)" :disabled="sendMsgDisabled1">
                  <span v-if="sendMsgDisabled1">{{time1+$t('uc.safe.second')}}</span>
                  <span v-if="!sendMsgDisabled1">{{$t('uc.safe.clickget')}}</span>
                </Button>
              </div>
            </Input>
          </FormItem>
        </Form>
        <div slot="footer">
          <Button type="text" size="large" @click="verifyEditor=false">{{$t('uc.api.cancel')}}</Button>
          <Button type="primary" size="large" @click="okUpdate">{{$t('uc.api.okText')}}</Button>
        </div>
      </Modal>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      locale:'',
      del: false,
      editor: false,
      verify: false,
      loading: false,
      verifyEditor: false,
      show: false,
      screat: "",
      time: 60, // 发送验证码倒计时
      time1: 60, // 发送验证码倒计时
      sendMsgDisabled: false,
      sendMsgDisabled1: false,
      codeEditorFormItem: "",
      editorFormItem: {
        remark: "",
        bindIp: "",
        code: ""
      },
      formItem: {
        remark: "",
        bindIp: "",
        code: ""
      },
      tableData: [],
      id: ""
    };
  },
  //5.25加
  watch: {
        '$i18n.locale': {
            handler(newVal) {
                this.locale = newVal;
            },
            immediate: true,
        }
  },
  created() {
    this.getAllAPI();
  },
  methods: {
    sendCode(index) {
      let me = this;
      //获取手机code
      this.$http.post(this.host + "/uc/mobile/api/code").then(response => {
        let resp = response.body;
        if (resp.code == 0) {
          if (index == 1) {
            this.sendMsgDisabled = true;
            let interval = window.setInterval(function() {
              if (me.time-- <= 0) {
                me.time = 60;
                me.sendMsgDisabled = false;
                window.clearInterval(interval);
              }
            }, 1000);
          } else {
            this.sendMsgDisabled1 = true;
            let interval = window.setInterval(function() {
              if (me.time1-- <= 0) {
                me.time1 = 60;
                me.sendMsgDisabled1 = false;
                window.clearInterval(interval);
              }
            }, 1000);
          }
        } else {
          this.$Message.error(resp.message);
        }
      });
    },
    onCopy(e) {
      this.$Notice.success({
        title: this.$t("common.tip"),
        desc: "success"
      });
    },
    onError() {
      this.$Notice.error({
        title: this.$t("common.tip"),
        desc: "fail"
      });
    },
    getAllAPI() {
      return this.$http.get(this.host + `/uc/open/get_key`).then(res => {
        console.log(res);
        this.tableData = res.body.data;
        console.log(this.tableData);
      });
    },
    // 添加api校验
    codeVerify() {
      const IP_REG = /^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$/;
      if (this.formItem.remark == "") {
        const lang =
          this.$store.getters.lang == "English"
            ? "please scanner remark"
            : "请输入备注信息";
        this.$Message.error(lang);
        return;
      } else if (this.formItem.bindIp == "") {
        const lang =
          this.$store.getters.lang == "English"
            ? "please scanner IP Address"
            : "请输入IP地址";
        this.$Message.error(lang);
        return;
      } else if (!IP_REG.test(this.formItem.bindIp)) {
        const lang =
          this.$store.getters.lang == "English"
            ? "please scanner Correct IP Address"
            : "请输入正确IP地址";
        this.$Message.error(lang);
        return;
      } else {
        this.verify = true;
      }
    },
    // 确认添加api
    make() {
      this.$http
        .post(this.host + "/uc/open/api/save", this.formItem)
        .then(res => {
          if (!res.body.code) {
            this.$Message.success(res.body.message);
            this.formItem.remark = "";
            this.formItem.bindIp = "";
            this.formItem.code = "";
            this.getAllAPI();
            this.verify = false;
            this.screat = res.body.data;
            this.show = true;
          } else {
            this.$Message.error(res.body.message);
          }
        });
    },
    // 编辑验证
    okUpdate() {
      if (this.editorFormItem.code == undefined) {
        const lang =
          this.$store.getters.lang == "English"
            ? "please scanner Correct IP Address"
            : "请输入验证码";
        this.$Message.error(lang);
        return;
      } else {
        this.verifyEditor = false;
        this.editor = true;
      }
    },
    // 编辑api
    update() {
      const IP_REG = /^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$/;
      if (!this.editorFormItem.remark) {
        const lang =
          this.$store.getters.lang == "English"
            ? "please scanner remark"
            : "请输入备注信息";
        this.$Message.error(lang);
        return;
      }
      if (!this.editorFormItem.bindIp) {
        const lang =
          this.$store.getters.lang == "English"
            ? "please scanner IP Address"
            : "请输入IP地址";
        this.$Message.error(lang);
        return;
      }
      if (!IP_REG.test(this.editorFormItem.bindIp)) {
        const lang =
          this.$store.getters.lang == "English"
            ? "please scanner Correct IP Address"
            : "请输入正确IP地址";
        this.$Message.error(lang);
        return;
      }
      this.$http
        .post(this.host + "/uc/open/api/update", this.editorFormItem)
        .then(res => {
          if (!res.body.code) {
            this.getAllAPI();
            this.editor = false;
            this.$Message.success(res.body.message);
          } else {
            this.$Message.error(res.body.message);
          }
        });
    },
    // 删除api
    onDel(id) {
      this.$http.get(this.host + `/uc/open/api/del/${id}`).then(res => {
        if (!res.body.code) {
          this.$Message.success(res.body.message);
          this.del = false;
          this.getAllAPI();
        } else {
          this.$Message.error(res.body.message);
        }
      });
    }
  },
  computed: {
    myColumns() {
      const arr = [];
      arr.push({
        title: this.$t("uc.api.createTime"),
        // width: 100,
        width: this.locale == 'en' ? 110 : 100,
        key: "createTime"
      });
      arr.push({
        title: this.$t("uc.api.mark"),
        width: 90,
        key: "remark"
      });
      arr.push({
        title: "API Key",
        width: 150,
        key: "apiKey"
      });
      arr.push({
        title: this.$t("uc.api.accessKey"),
        width: this.locale == 'en' ? 130 : 100,
        render: (h, params) => {
          const text = "******";
          return h("span", {}, text);
        }
      });
      arr.push({
        title: this.$t("uc.api.bindIpAddress"),
        width: this.locale == 'en' ? 130 : '',
        render: (h, params) => {
          let txts = params.row.bindIp.split(",");
          txts = txts.length > 1 ? txts[0] + "  " + "..." : txts;
          return h("span", {}, txts);
        }
      });
      arr.push({
        title: this.$t("uc.api.ioDays"),
        width: this.locale == 'en' ? 150 : '',
        render: (h, params) => {
          if (!params.row.bindIp) {
            let residue = +new Date(params.row.expireTime) - +new Date();
            let lastTime = Math.floor(residue / 1000 / 60 / 60 / 24);
            lastTime = lastTime <= 0 ? 0 : lastTime;
            const timeDay = h("span", {}, lastTime);
            return [timeDay];
          }
        }
      });
      arr.push({
        title: this.$t("uc.api.operation"),
        align: "center",
        width: 180,
        render: (h, params) => {
          return [
            h(
              "Button",
              {
                props: {
                  type: "primary"
                },
                on: {
                  click: () => {
                    const { remark, bindIp, id, code } = params.row;
                    this.editorFormItem = { remark, bindIp, id, code };
                    this.verifyEditor = true;
                  }
                }
              },
              this.$t("uc.api.edit")
            ),
            h(
              "Button",
              {
                props: {
                  type: "error"
                },
                style: {
                  marginLeft: "10px"
                },
                on: {
                  click: () => {
                    this.del = true;
                    this.id = params.row.id;
                  }
                }
              },
              this.$t("uc.api.delete")
            )
          ];
        }
      });
      return arr;
    }
  }
};
</script>

<style lang="scss" scoped>
.blue {
  cursor: pointer;
  color: #fff;
  background: #3399ff;
  display: inline-block;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 3px;
}
.main {
  //   background-color: #eee;
  padding: 20px;
  .content {
    margin: 10px 0;
    .content_header {
      padding: 10px;
      display: flex;
      justify-content: space-between;
      .add {
        width: 39%;
      }
      .prompt {
        width: 49%;
        ul li {
          font-size: 10px;
          color: #7a8294;
          line-height: 25px;
          list-style: disc;
          span {
            color: #000;
          }
        }
      }
      .ivu-btn-primary {
        background-color: #f0ac19;
        border-color: #f0ac19;
      }
    }
  }
}
.screat {
  font-weight: 700;
  font-size: 16px;
  line-height: 26px;
}
.let-code {
}
.api-manage .ivu-card{
  background: transparent!important;
}
.api-manage .ivu-card-head p, .api-manage .ivu-card-head-inner{
  color: #EDEDED;
}

</style>
<style>
.api-manage .ivu-card .ivu-card-head{
  border-bottom: 1px solid #233346!important;
}
</style>



