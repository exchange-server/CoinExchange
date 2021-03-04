<template>
  <div>
    <Card>
      <p slot="title">
        手续费提取明细
        <Button type="primary" size="small" @click="refreshPageManual">
          <Icon type="refresh"></Icon>
          刷新
        </Button>
      </p>

     <Row class="functionWrapper">
        <div class="searchWrapper clearfix">
						<span>交易类型：</span>
						<Select v-model="tradeType" style="width: 200px">
							<Option v-for="(item, index) in tradeTypeArr"
										:value="index"
										:key="item">{{ item }}</Option>
						</Select>
				</div>
        <div class="searchWrapper clearfix" style="margin-left:20px;">
						<span>交易时间：</span>
						<DatePicker
							type="daterange"
							placement="bottom-end"
							@on-change="dateRange"
							placeholder="选择时间区间">
						</DatePicker>
				</div>
        <div class="searchWrapper clearfix">
          <Button type="primary" @click="searchByFilter">搜索</Button>
        </div>
      </Row>

      <Row class="tableWrapper">
        <Table :columns="columns_first"
							@on-sort-change="definedOrder"
							:data="userpage" border
							:loading="ifLoading">
				</Table>
      </Row>

      <Row class="pageWrapper" >
        <Page :total="pageNum" :current="currentPageIdx" @on-change="changePage" show-elevator></Page>
      </Row>

    </Card>
  </div>
</template>

<script>
import { withdrawFeeDetail  } from '@/service/getData';
  export default{
    data () {
      return {
      	tradeType: -1,
      	tradeTypeArr: ["--全部--", "币币交易", "永续合约", "期权合约"],
		filterSearch: {
			pageNo: 1,
			pageSize: 10,
			bizType: '',
			startTime: '',
			endTime: ''
		},
        currentPageIdx: 1,
        ifLoading: true,
        pageNum: null,
        userpage: [],
        columns_first: [
          {
            title: '业务类型',
            key: 'bizType',
            render(h, obj) {
            	if(obj.row.bizType=="echange") {
            		return h("span", {}, "币币交易");
            	} else if(obj.row.bizType=="swap") {
					return h("span", {}, "永续合约");
            	} else {
            		return h("span", {}, "期权合约");
            	}
          	}
          },
          {
            title: '手续费类型',
            key: 'feeType',
            render(h, obj) {
            	if(obj.row.bizType=="echange") {
            		return h("span", {}, obj.row.feeType=="left"? "买币手续费": "卖币手续费");
            	} else if(obj.row.bizType=="swap") {
					return h("span", {}, obj.row.feeType=="left"? "开仓手续费": "平仓手续费");
            	} else {
            		return h("span", {}, obj.row.feeType=="left"? "期权抽水": "期权手续费");
            	}
          	}
          },
          {
            title: '交易对',
            key: 'symbol'
          },
          {
            title: '提取数量',
            key: 'feeAmount',
            render(h, obj) {
            	return h("span", {}, obj.row.feeAmount+"("+obj.row.feeCoin+")");
          	}
          },
          {
            title: '提取时间',
            key: 'createTime'
          }
        ]
      }
    },
    watch: {
    	tradeType: function(news, olds) {
    		console.log(olds, news);
    		if(news==0) {
				this.filterSearch.bizType = "";
    		} else if(news==1) {
				this.filterSearch.bizType = "exchange";
    		} else if(news==2) {
				this.filterSearch.bizType = "swap";
    		} else if(news==3) {
    			this.filterSearch.bizType = "option";
    		}
    	}
    },
    methods: {
    	 dateRange(val) {
	        this.filterSearch.startTime = val[0];
			this.filterSearch.endTime = val[1];
	      },
		definedOrder(obj) {
			let direction = obj.order==='desc' ? 1 : 0;
			let propertyIndex = this.filterSearch.property.indexOf(obj.key);

			if(propertyIndex!==-1){
				this.filterSearch.direction[propertyIndex] = direction;
			}else{
				this.filterSearch.property.push(obj.key);
				this.filterSearch.direction.push(direction);
			}

			this.refreshPage(this.filterSearch);
		},
		searchByFilter() {
			this.currentPageIdx = 1;
			this.filterSearch.pageNo = 1;
			this.refreshPage(this.filterSearch);
		},
      	refreshPageManual() {
			this.currentPageIdx = 1;
			for(let key in this.filterSearch) {
				this.filterSearch[key] = '';
			}
			this.filterSearch.pageNo = 1;
			this.filterSearch.pageSize = 10;
			this.refreshPage(this.filterSearch);
      },
      changePage(pageIndex) {
			this.currentPageIdx = pageIndex;
			this.filterSearch.pageNo = pageIndex;
			this.refreshPage(this.filterSearch)
      },
      refreshPage(obj) {
		this.ifLoading = true;
        withdrawFeeDetail(obj).then( res => {
          if(!res.code) {
            this.ifLoading = false;
            this.pageNum = res.data.totalElements;
            this.userpage = res.data.content;
          }else {
          	this.$Message.error(res.message)
          }
		});
      }
    },
    created () {
      this.refreshPage();
    }
  }
</script>

<style>
/* .ivu-select-item-selected, .ivu-select-item-selected:hover{
	background: #fff;
	color: #000;
} */
</style>
