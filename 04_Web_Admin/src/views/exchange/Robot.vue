<template>
  <div>
    <Card>
      <p slot="title">
      控盘机器人
      </p>
      <Row class="functionWrapper" style='padding:0px 8px 8px 8px'>
        <Col span="4">
          <div style="float: left" class="clearfix">
            <div class="poptip">选择控盘交易对：</div>
            <Select size="small" v-model="coinPair" style="width:120px" @on-change="selectCoinSymbol" >
                <Option v-for="item in coinPairList" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
          </div>
        </Col>

        <Col span="8">
          <div class="poptip">控盘策略：</div>
            <RadioGroup v-model="strategy" type="button" size="small">
                  <Radio label="1">跟随型趋势</Radio>
                  <Radio label="2">自定义趋势</Radio>
            </RadioGroup>
        </Col>

        <Col span="12" v-show="strategy == '2'">
          <div style="float: right" class="clearfix">
            <div class="poptip">控制粒度：</div>
            <RadioGroup v-model="period" type="button" size="small">
                  <Radio label="1">15分钟</Radio>
                  <Radio label="2">30分钟</Radio>
                  <Radio label="3">1小时</Radio>
                  <Radio label="4">2小时</Radio>
                  <Radio label="5">4小时</Radio>
            </RadioGroup>
          </div>
          <div style="float: right;margin-right: 20px;" class="clearfix">
            <div class="poptip">当日最高限价：</div>
            <Input v-model="maxPrice" style="width: 150px" size="small">
                <Button slot="append" type="primary" @click="setMaxPrice()">确定</Button>
            </Input>
          </div>
          <div style="float: right;margin-right: 20px;" class="clearfix">
            <div class="poptip">选择日期：</div>
            <DatePicker size="small" :value="robotDate" @on-change="selectDate" format="yyyy-MM-dd" type="date" placeholder="选择控盘日期" style="width: 120px"></DatePicker>
          </div>
        </Col>
      </Row>

      <div v-show="strategy == '1'" style="width: 100%;background: #F5F5F5;">
        <Row style='padding:20px 8px 10px 8px'>
          <Col span="24">
            <p style="font-size: 12px;color: red;padding-left: 30px;margin-bottom: 20px;">若选择【跟随型趋势】，请先确认控盘交易对是否已创建机器人。建议先创建好控盘机器人，并设置为停止状态，待设置好跟随趋势以后再启动机器人。
            </p>
            <Form :label-width="130">
              <FormItem label="跟随交易对：">
                <Select size="small" v-model="flowPair" style="width:120px" @on-change="selectFlowCoinSymbol" >
                    <Option v-for="item in flowPairList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select><br>选择后，行情将跟随所选交易对行情走势。目前仅支持BTC/USDT、ETH/USDT、LTC/USDT，如需更多其他交易对，需定制开发。
              </FormItem>

              <FormItem label="跟随价格比例：">
                  <Input v-model="flowPercent" style="width: 150px;">
                      <span slot="append">%</span>
                  </Input>请根据所选交易对当前行情最新价进行计算，例：如果设置该值为1，则该币种将以BTC/USDT行情价的1%作为锚定价，走势趋势将与BTC/USDT保持一致.
              </FormItem>

              <FormItem label="">
                  <Button type="success" @click="commitRobot2()" style="margin-top: 15px;">保存设置</Button>
              </FormItem>
            </Form>
            
          </Col>
        </Row>
      </div>

      <div  v-show="strategy == '2'">
        <div style="width: 100%;height:200px;" ref="dom" class="charts chart-bar"></div>

        <p style="padding: 10px 0 10px 0;"><Button size="small" type="primary" @click="generateKLine()">绘制预览K线图</Button><span style="float:right;font-size:12px;">以下生成的是模拟K线，仅供参考，实际运行结果与下图相似而不完全相同</span></p>

        <div style="width: 100%;height:200px;" ref="klineDom" class="charts chart-bar"></div>

        <Row class="functionWrapper" style='padding:0px 8px 0px 8px;margin-top: 20px;'>

          <Col span="18">
            <div style="float: left;" class="clearfix">
              <Form :label-width="130">
                <FormItem label="允许价格浮动比例：">
                    <Input v-model="pricePencent">
                        <span slot="append">%</span>
                    </Input>
                </FormItem>
              </Form>
            </div>
          </Col>
          <Col span="6">
            <div style="float: right" class="clearfix">
              <Button type="success" @click="commitRobot()">保存机器人趋势数据</Button>
            </div>
          </Col>
        </Row>
      </div>
    </Card>
    <Card style="margin-top: 10px;"  v-show="strategy == '2'">
      <p slot="title">
        BTC/USDT 交易对机器人趋势数据
      </p>
      <div>
        <Row>
          <Table
            :columns="columns"
            :data="klineList"
            :loading="ifLoading">
          </Table>
        </Row>
      </div>
    </Card>
  </div>
</template>

<script>
import dtime from 'time-formater'
import Cookies from 'js-cookie'
import echarts from 'echarts'
import { getStore, setStore, removeStore } from '@/config/storage'
import { saveRobotKdata, getRobotKdataList, getCustomCoinPairList, saveRobotKdataFlow, getRobotConfig } from '@/service/getData'
export default {
   name: 'ChartLine',
   props: {
     text: String,
     subtext: String,
     yName: String
   },
   data () {
     return {
        dom: null,
        strategy: '2',
        klineDom: null,
        symbolSize: 5,
        maxPrice: 10,
        flowPercent: 1,
        pricePencent: 5,
        robotDate: '2020-12-13',
        period: '5', // 1:15分钟  2:30分钟  3:1小时  4:2小时 5:4小时
        // 通过拖动是可以实时改变这里的值的
        storeData: [], // 存储最后写入数据库的数据
        klineData: [], // 图标关联数据
        klineMockXData: [], // 模拟K线图X轴数据
        klineMockYData: [], // 模拟K线图Y轴数据
        flowPair: "BTC/USDT",
        flowPairList: [
          {value: "BTC/USDT", label: "BTC/USDT"},
          {value: "ETH/USDT", label: "ETH/USDT"},
          {value: "LTC/USDT", label: "LTC/USDT"}
        ],
        coinPair: "BTC/USDT",
        coinPairList: [
          {value: "BTC/USDT", label: "BTC/USDT"},
          {value: "ETH/USDT", label: "ETH/USDT"},
          {value: "LTC/USDT", label: "LTC/USDT"}
        ],
        robotParams: {},
        ifLoading: false,
        klineList: [],
        columns: [
          {
            title: "交易对",
            key: "coinName",
            width: 180
          },
          {
            title: "日期",
            key: "kdate",
          },
          {
            title: "价格浮动百分比",
            key: "pricePencent",
            render: (h, params) => {
              return h("span", {}, params.row.pricePencent + "%");
            }
          },
          {
            title: "操作",
            key: "xx",
            width: 250,
            fixed: 'right',
            render: (h, obj) => {
              return h("div", [
                h(
                  "Button",
                  {
                    props: {type: "info",size: "small"},
                    style: {marginRight: "5px"},
                    on: {
                      click: () => {
                        var row = obj.row;
                        this.pricePencent = row.pricePencent;
                        this.coinPair = row.coinName;
                        this.robotDate = row.kdate;
                        this.storeData = eval(row.kline);
                        for(var i = 0; i <= 96; i++) {
                          this.storeData[i][1] = parseFloat(this.storeData[i][1]);
                        }
                        this.generateKLine();
                        this.generateLine(this.period);
                      }
                    }
                  },
                  "查看/修改"
                )
              ]);
            }
          }
        ]
     }
   },
   watch: {
      period(val) {
        this.generateLine(val);
        this.setLineOption();
      }
   },
   methods: {
     resize () {
        this.dom.resize()
     },
     setMaxPrice(){
      this.dom.setOption({
        yAxis: {
          max: this.maxPrice
        }
      });
      this.setLineOption();
     },
     initStoreData(){
      this.storeData = new Array();
      for(var i = 0; i <= 96; i++) {
        this.storeData[i] = new Array();
        this.storeData[i][0] = i;
        this.storeData[i][1] = 1;
      }
     },
     commitRobot(){
        for(var i = 0; i <=96; i++){
          this.storeData[i][1] = this.storeData[i][1].toFixed(8);
        }
        let obj = {
          symbol: this.coinPair,
          kdate: this.robotDate,
          kline: this.arrayToJson(this.storeData),
          pricePencent: this.pricePencent
        };
        saveRobotKdata(obj).then( res => {
          if(!res.code) {
            this.$Notice.success({
                    title: "保存成功！",
                    desc: res.message,
                    duration: 10
                });
            this.getKlineList();
          }else{
            this.$Notice.error({
                    title: "保存失败",
                    desc: res.message,
                    duration: 10
                });
          }
        });
     },
     commitRobot2(){
        let obj = {
          symbol: this.coinPair,
          pair: this.flowPair,
          flowPercent: Number(this.flowPercent)
        };
        saveRobotKdataFlow(obj).then( res => {
          if(!res.code) {
            this.$Notice.success({
                    title: "保存成功！",
                    desc: res.message,
                    duration: 10
                });
          }else{
            this.$Notice.error({
                    title: "保存失败",
                    desc: res.message,
                    duration: 10
                });
          }
        });
     },
     getKlineList(){
        var nowDate = new Date();
        var nowDateStr = dtime(nowDate).format('YYYY-MM-DD HH:mm:ss');
        this.robotDate = nowDateStr;
        let obj = {
          symbol: this.coinPair,
          kdate: nowDateStr
        };
        getRobotKdataList(obj).then( res => {
          if(!res.code) {
            if(res.data.length > 0) {
              this.klineList = res.data;
              // 初始化第一条数据
              var row = this.klineList[0];
              this.pricePencent = row.pricePencent;
              this.robotDate = row.kdate;
              this.storeData = eval(row.kline);
              for(var i = 0; i <= 96; i++) {
                this.storeData[i][1] = parseFloat(this.storeData[i][1]);
              }
              this.generateKLine();
              this.generateLine(this.period);
            }else{
              this.$Notice.error({
                    title: "该交易对没有现在及未来的K线趋势数据",
                    desc: res.message,
                    duration: 10
                });
            }
          }else{
            this.$Notice.error({
                    title: "获取失败",
                    desc: res.message,
                    duration: 10
                });
          }
        });
     },
     getCoinPairList(){
      let obj = {

      };
      // 获取控盘交易对
      getCustomCoinPairList(obj).then( res => {
          if(!res.code) {
            if(res.data.length  == 0){
              this.$Notice.error({
                    title: "无法获取控盘交易对",
                    desc: res.message,
                    duration: 10
                });
            }else{
              this.coinPairList.length = 0;
              res.data.forEach(item => {
                var coinItem = {
                  label: item.symbol,
                  value: item.symbol
                }
                this.coinPairList.push(coinItem);
              });
              this.coinPair = this.coinPairList[0].value;

              // 获取该交易对的K线基础数据
              this.getKlineList();  
            }
          }else{
            this.$Notice.error({
                    title: "无法获取控盘交易对",
                    desc: res.message,
                    duration: 10
                });
          }
        });
     },
     getRobotParams(symbol){
        let obj = {
          symbol: symbol
        };
        getRobotConfig(obj).then( res => {
              if (!res.code) {
                  this.robotParams = res.data;
                  if(this.robotParams.strategyType == 1) {
                    this.strategy = "1";
                    this.flowPair = this.robotParams.flowPair;
                    this.flowPercent = this.robotParams.flowPercent;
                  }else{
                    this.strategy = "2";
                  }
              }else{
                  this.$Notice.info({
                        title: "机器人",
                        desc: "该交易对没有机器人，请先在【币币管理】中新建机器人",
                        duration: 10
                    });
              }
            });
     },
     selectCoinSymbol(symbol){
        this.getRobotParams(symbol);
        this.getKlineList();
     },
     selectFlowCoinSymbol(){

     },
     selectDate(newDate){
      if(newDate == this.robotDate) return;
      this.robotDate = newDate;
      var hasData = false;
      // 查询是否有数据，加载最新数据
      this.klineList.forEach(item => {
        if(item.kdate == newDate) {
          this.pricePencent = item.pricePencent;
          this.storeData = eval(item.kline);
          for(var i = 0; i <= 96; i++) {
            this.storeData[i][1] = parseFloat(this.storeData[i][1]);
          }
          this.generateKLine();
          this.generateLine(this.period);

          hasData = true;
        }
      });
      if (!hasData) {
        this.initStoreData();
        this.generateKLine();
        this.generateLine(this.period);
      } 
     },
     initKline(){
      this.klineDom = echarts.init(this.$refs.klineDom, '');
      var that = this;
      this.klineDom.setOption({
        backgroundColor: '#EDEDED',
        xAxis: {
          data: ['1', '2', '3', '4'],
          axisLabel: {
             formatter (value) {
                return value/4 + ':00' // 格式时间显示方式
             },
             fontSize: 8
          }
        },
        yAxis: {
          axisLabel: {
            fontSize: 8
          },
          splitLine: {
            show: true,
            lineStyle:{
               color: ['#CCC'],
               width: 1,
               type: 'dashed'
            }
      　　}
        },
        series: [{
            type: 'k',
            data: [],
            itemStyle: {
              normal:{
                color: '#ec0000',
                color0: '#00da3c',
                borderColor: '#ec0000',
                borderColor0: '#00da3c'
              }
            }
        }],
        grid: {
          left: 30,
          right:20,
          top: 10,
          bottom: 30
        },
      });
     },
     generateKLine(){
        this.klineMockXData = new Array();
        this.klineMockYData = new Array();
        for(var i = 0; i <= 96; i++) {
          this.klineMockXData[i] = i;
          this.klineMockYData[i] = new Array();
          // 随机生成开盘价、收盘价、最高价、最低价
          var rand = Math.ceil(Math.random()*10);
          var priceRandClose = Math.ceil(Math.random()*this.pricePencent); // 收盘价格
          var priceRandHight = Math.ceil(Math.random()*this.pricePencent);
          var priceRandLow = Math.ceil(Math.random()*this.pricePencent);
          this.klineMockYData[i][0] = i > 0 ? this.klineMockYData[i-1][1] : this.storeData[i][1]; // 取前一收盘价
          this.klineMockYData[i][1] = rand > 5 ? this.storeData[i][1] + this.storeData[i][1] * (priceRandClose/100) : this.storeData[i][1] - this.storeData[i][1] * (priceRandClose/100);
          if(this.klineMockYData[i][0] > this.klineMockYData[i][1]){ // 开盘价大于收盘价
            this.klineMockYData[i][2] = this.klineMockYData[i][0] + this.klineMockYData[i][0] * (priceRandHight/100);
            this.klineMockYData[i][3] = this.klineMockYData[i][1] - this.klineMockYData[i][1] * (priceRandLow/100);
          }else{ // 开盘价小于收盘价
            this.klineMockYData[i][2] = this.klineMockYData[i][1] + this.klineMockYData[i][1] * (priceRandHight/100);
            this.klineMockYData[i][3] = this.klineMockYData[i][0] - this.klineMockYData[i][0] * (priceRandLow/100);
          }
        }
        this.klineDom.setOption({
          xAxis: {
            data: this.klineMockXData
          },
          series: [{
              type: 'k',
              data:this.klineMockYData
          }]
        });
     },
     generateLine(period) {
        if(period == 1) {
          this.klineData = new Array();
          for(var i = 0; i <= 96; i++) {
            this.klineData[i] = new Array();
            this.klineData[i][0] = i;
            if(this.storeData[i][1] == 0){
              this.klineData[i][1] = this.maxPrice/2;
              this.storeData[i][1] = this.maxPrice/2;
            }else{
              this.klineData[i][1] = this.storeData[i][1];
            }
          }
        }else if(period == 2) {
          this.klineData = new Array();
          for(var i = 0; i <= 48; i++) {
            this.klineData[i] = new Array();
            this.klineData[i][0] = i*2;
            if(this.storeData[i*2][1] == 0){
              this.klineData[i][1] = this.maxPrice/2;
              this.storeData[i*2][1] = this.maxPrice/2;
            }else{
              this.klineData[i][1] = this.storeData[i*2][1];
            }
          }
        }else if(period == 3) {
          this.klineData = new Array();
          for(var i = 0; i <= 24; i++) {
            this.klineData[i] = new Array();
            this.klineData[i][0] = i*4;
            if(this.storeData[i*4][1] == 0){
              this.klineData[i][1] = this.maxPrice/2;
              this.storeData[i*4][1] = this.maxPrice/2;
            }else{
              this.klineData[i][1] = this.storeData[i*4][1];
            }
          }
        }else if(period == 4) {
          this.klineData = new Array();
          for(var i = 0; i <= 12; i++) {
            this.klineData[i] = new Array();
            this.klineData[i][0] = i*8;
            if(this.storeData[i*8][1] == 0){
              this.klineData[i][1] = this.maxPrice/2;
              this.storeData[i*8][1] = this.maxPrice/2;
            }else{
              this.klineData[i][1] = this.storeData[i*8][1];
            }
          }
        }else if(period == 5) {
          this.klineData = new Array();
          for(var i = 0; i <= 6; i++) {
            this.klineData[i] = new Array();
            this.klineData[i][0] = i*16;
            if(this.storeData[i*16][1] == 0){
              this.klineData[i][1] = this.maxPrice/2;
              this.storeData[i*16][1] = this.maxPrice/2;
              if(i < 6) {
                for(var j = i*16; j < (i+1) * 16; j++) {
                  if(this.storeData[j][1] == 0){
                    this.storeData[j][1] = this.maxPrice/2;
                  }
                }
              }
            }else{
              this.klineData[i][1] = this.storeData[i*16][1];
            }
          }
        }
        this.dom.setOption({
          series: [{
            id: 'a',
            data: this.klineData
          }]
         });

        this.generateKLine();
     },
     setLineOption(){
        this.dom.setOption({
          graphic: echarts.util.map(this.klineData, (dataItem, dataIndex) => {
            const that = this // 因为ondrag函数必须在回调内使用this.position获取实时坐标，此处必须用that替换this
            return {
               // 'circle' 表示这个 graphic element 的类型是圆点。
               type: 'circle',
               shape: {
               // 圆点的半径。
               r: this.symbolSize
             },
             // 用 transform 的方式对圆点进行定位。position: [x, y] 表示将圆点平移到 [x, y] 位置。
             // 这里使用了 convertToPixel 这个 API 来得到每个圆点的位置
             position: this.dom.convertToPixel('grid', dataItem),
             // 这个属性让圆点不可见（但是不影响他响应鼠标事件）。
             invisible: true,
             // 这个属性让圆点可以被拖拽。
             draggable: true,
             // 把 z 值设得比较大，表示这个圆点在最上方，能覆盖住已有的折线图的圆点。
             z: 100,
             ondrag: echarts.util.curry(function (dataIndex) { // 此处必须用匿名函数，不能用箭头函数，否则拿不到拖动的坐标
               let origin = that.dom.convertToPixel('grid', that.klineData[dataIndex]) // 得到每个圆点原始位置
               // let maxY = that.dom.convertToPixel('grid', [40, 36]) // 最高温度为36摄氏度，暂未做封装
               // 超过最高温度36将不能拖动,写死的最低点高度为240，最高点为40
               this.position[0] = origin[0] // 控制每个点位只能在y轴方向移动
               // this.position[1] = origin[1] // 控制每个点位只能在x轴方向移动
               // 实时获取拖动的点位信息并根据此信息重新画图
               that.klineData[dataIndex] = that.dom.convertFromPixel('grid', this.position);
               if(that.period == 1) {that.storeData[dataIndex][1] = that.klineData[dataIndex][1];}
               if(that.period == 2) {
                  for(var i = 0; i < 48; i++){
                    for(var j = i*2; j <= (i+1)*2; j++){
                      // 局部曲线变化
                      that.storeData[j][1] = that.klineData[i][1] - ((that.klineData[i][1] - that.klineData[i+1][1])/2)*(j-i*2)*Math.sin(Math.PI/2 * (j-i*2) / 2);
                    }
                  }
               }
               if(that.period == 3) {
                  for(var i = 0; i < 24; i++){
                    for(var j = i*4; j <= (i+1)*4; j++){
                      that.storeData[j][1] = that.klineData[i][1] - ((that.klineData[i][1] - that.klineData[i+1][1])/4)*(j-i*4)*Math.sin(Math.PI/2 * (j-i*4) / 4);
                    }
                  }
               }
               if(that.period == 4) {
                  for(var i = 0; i < 12; i++){
                    for(var j = i*8; j <= (i+1)*8; j++){
                      that.storeData[j][1] = that.klineData[i][1] - ((that.klineData[i][1] - that.klineData[i+1][1])/8)*(j-i*8)*Math.sin(Math.PI/2 * (j-i*8) / 8);
                    }
                  }
               }
               if(that.period == 5) {
                  for(var i = 0; i < 6; i++){
                    for(var j = i*16; j <= (i+1)*16; j++){
                      that.storeData[j][1] = that.klineData[i][1] - ((that.klineData[i][1] - that.klineData[i+1][1])/16)*(j-i*16)*Math.sin(Math.PI/2 * (j-i*16) / 16);
                    }
                  }
               }

               that.dom.setOption({
                series: [{
                  id: 'a',
                  data: that.klineData
                }]
               });
             }, dataIndex)
            }
          })
         });
     },
     arrayToJson(o) {
        var r = [];
        if (typeof o == "string") return "\"" + o.replace(/([\'\"\\])/g, "\\$1").replace(/(\n)/g, "\\n").replace(/(\r)/g, "\\r").replace(/(\t)/g, "\\t") + "\"";
        if (typeof o == "object") {
          if (!o.sort) {
            for (var i in o)
            r.push(i + ":" + this.arrayToJson(o[i]));
            if (!!document.all && !/^\n?function\s*toString\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)) {
            r.push("toString:" + o.toString.toString());
            }
            r = "{" + r.join() + "}";
          } else {
            for (var i = 0; i < o.length; i++) {
            r.push(this.arrayToJson(o[i]));
            }
            r = "[" + r.join() + "]";
          }
          return r;
        }
        return o.toString();
     }
   },
   mounted () {
     this.initKline();
     this.initStoreData();
     this.dom = echarts.init(this.$refs.dom, '');
     var that = this;
     this.dom.setOption({
      backgroundColor: '#FFE',
      title: {
        text: this.text,
        subtext: this.subtext,
        x: 'center'
      },
      grid: {
        left: 30,
        right:20,
        top: 10,
        bottom: 30
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                formatter: function (params) {
                    return params.value.toFixed(6);
                }
            }
        }
      },
      xAxis: {
        min: 0,
        max: 96,
        name: '',
        type: 'time',
        interval:4,
        axisLabel: {
           formatter (value) {
              return value/4 + ':00' // 格式时间显示方式
           },
           fontSize: 8
        },
        axisLine: { onZero: false },
        splitLine: {
          show: true,
          lineStyle:{
             color: ['#CCC'],
             width: 1,
             type: 'dashed'
          }
    　　},
        axisPointer: {
            label: {
                formatter: function (params) {
                    return '时间：' + that.robotDate + " " + parseInt(params.value/4) + ":" + ((params.value%4) != 0 ? (params.value%4)*15 : "00");
                }
            }
        }
      },
      yAxis: {
        min: 0,
        max: that.maxPrice,
        name: '价格',
        type: 'value',
        name: this.yName,
        axisLine: { onZero: false },
        axisLabel: {
          fontSize: 8
        },
        splitLine: {
          show: true,
          lineStyle:{
             color: ['#CCC'],
             width: 1,
             type: 'dashed'
          }
    　　}
      },
      series: [
        {
           id: 'a',
           type: 'line',
           smooth: true,
           symbolSize: this.symbolSize, // 为了方便拖拽，把 symbolSize 尺寸设大了。
           data: this.klineData
        }
      ]
     });

     this.generateLine(this.period);
     this.setLineOption();
     this.getCoinPairList();
    }
  }
</script>

<style lang="less" scoped>

</style>


