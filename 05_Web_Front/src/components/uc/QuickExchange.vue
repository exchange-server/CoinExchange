<style scoped>
.ivu-table td, .ivu-table th{
  height: 35px!important;
}
</style>
<template>
  <div class="nav-rights">
    <div class="nav-right">
      <div class="bill_flow_box">
        <div class="rightarea-con">
          <div class="order-table">
            <Table :no-data-text="$t('common.nodata')" :columns="tableColumnsRecord" :data="tableRecord" :disabled-hover="true" :loading="loading"></Table>
            <div style="margin: 10px;overflow: hidden">
              <div style="float: right;">
                <Page :total="total" :pageSize="pageSize" show-total :current="page" @on-change="changePage" id="record_pages"></Page>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  components: {},
  data() {
    return {
      loading: false,
      pageSize: 10,
      page: 1,
      total: 0,
      tableRecord: []
    };
  },
  created: function() {
    this.getList(this.page);
  },
  methods: {
    changePage(pageindex) {
      this.page=pageindex;
      this.getList(this.page);
    },
    dateform(time) {
      var date = new Date(time);
      var y = date.getFullYear();
      var m = date.getMonth() + 1;
      m = m < 10 ? "0" + m : m;
      var d = date.getDate();
      d = d < 10 ? "0" + d : d;
      var h = date.getHours();
      h = h < 10 ? "0" + h : h;
      var minute = date.getMinutes();
      var second = date.getSeconds();
      minute = minute < 10 ? "0" + minute : minute;
      second = second < 10 ? "0" + second : second;
      return y + "-" + m + "-" + d + " " + h + ":" + minute + ":" + second;
    },
    getList(pageNo) {
      let params = {};
      this.$http.post(this.host + "/uc/asset/wallet/quick-exchange-list", params).then(response => {
          var resp = response.body;
          if (resp.code == 0) {
            this.loading = false;
            if (resp.data) {
              this.tableRecord = resp.data;
            }
          } else {
            this.$Message.error(resp.message);
          }
          this.loading = false;
        });
    }
  },
  computed: {
    tableColumnsRecord() {
      let columns = [];
      var that = this;
      columns.push({
        title: this.$t("uc.finance.record.chargetime"),
        align: "center",
        width: 160,
        key:"createTime",
		render: function(h,params){
			return h("div", {}, that.dateFormatFromString(params.row.createTime));
		}
      });

      columns.push({
        title: this.$t("uc.finance.record.from"),
        align: "center",
        key:"fromUnit"
      });

      columns.push({
        title: this.$t("uc.finance.record.to"),
        align: "center",
        key:"toUnit"
      });

      columns.push({
        title: this.$t("uc.finance.record.qamount"),
        align: "center",
        key:"amount"
      });

      columns.push({
        title: this.$t("uc.finance.record.qrate"),
        align: "center",
        key:"rate"
      });

      columns.push({
        title: this.$t("uc.finance.record.qexamount"),
        align: "center",
        key:"exAmount"
      });

      return columns;
    }
  }
};
</script>
<style scoped lang="scss">
.nav-rights {
  .nav-right {
    height: auto;
    overflow: hidden;
    padding: 0 15px;
    .bill_flow_box .rightarea-con {
      .form-group {
        margin-bottom: 20px;
        text-align: left;
      }
    }
  }
}
</style>
