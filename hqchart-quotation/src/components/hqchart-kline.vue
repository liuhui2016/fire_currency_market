<template>
  <div id="app" style="display: flex;flex-direction: column;justify-content: center;align-items: center;">
    <div style="display: flex;flex-direction: column;width: 100%;margin-bottom: 80px;">
      <div style="display: flex;flex-direction: row;">
        <div style="width:20%;text-align: center;">24h最新价</div>
        <div style="width:20%;text-align: center;">24h开盘价</div>
        <div style="width:20%;text-align: center;">24h最高价</div>
        <div style="width:20%;text-align: center;">24h最低价</div>
        <div style="width:20%;text-align: center;">24h成交量</div>
        <div style="width:20%;text-align: center;">24小时成交笔数</div>
        <div style="width:20%;text-align: center;">24小时成交额</div>
      </div>
      <div style="display: flex;flex-direction: row;">
        <div style="width:20%;text-align: center;">{{close}}</div>
        <div style="width:20%;text-align: center;">{{open}}</div>
        <div style="width:20%;text-align: center;">{{high}}</div>
        <div style="width:20%;text-align: center;">{{low}}</div>
        <div style="width:20%;text-align: center;">{{amount}}</div>
        <div style="width:20%;text-align: center;">{{count}}</div>
        <div style="width:20%;text-align: center;">{{vol}}</div>
      </div>
    </div>
    <div class="kline-frame">
      <div style="width: 300px;">
        <div style="display: flex;flex-direction: row;width: 100%;">
          <div style="flex: 1;width:33%;text-align: center;">交易对</div>
          <div style="flex: 1;width:33%;text-align: center;">价格</div>
        </div>
        <div style="display: flex;flex-direction: row;width: 100%;margin-top: 20px;" v-for="item in symbols">
          <div style="flex: 1;text-align: center;cursor: pointer;color: aqua;" @click="changeSymbol(item)">{{item.symbolUp}}</div>
          <div style="flex: 1;width:33%;text-align: center;">{{item.market.close}}</div>
        </div>
      </div>
      <div style="kline-info">
        <div style="display: flex;flex-direction: row;width: 100%;">
          <div style="margin-right: 10px;cursor: pointer;" v-for="item in periodObject" @click="changeCycle(item)">{{item.hb}}</div>
        </div>
        <div id="minute" ref="minute"></div>
      </div>
      <div class="market-depth" style="width: 200px;">
        <div style="width: 100%;text-align: center;">盘口深度</div>
        <div>
          <div style="display: flex;flex-direction: row; margin-top: 20px;">
            <div style="flex:1;text-align: center;">价格</div>
            <div style="flex:1;text-align: center;">数量</div>
          </div>
          <div style="margin-top: 20px;min-height: 300px;">
            <div style="display: flex;flex-direction: row;" v-for="itemArr in asks">
              <div style="flex:1;text-align: center;">{{itemArr.tickArray[0]}}</div>
              <div style="flex:1;text-align: center;">{{itemArr.tickArray[0]}}</div>
            </div>
          </div>
          <div style="width: 100%;height: 20px;border-bottom: 1px solid black;border-top: 1px solid black;display: flex;align-items: center;justify-content: center;">
             <div>{{close}}</div>
          </div>
          <div style="min-height: 300px;">
            <div style="display: flex;flex-direction: row;" v-for="itemArr in bids">
              <div style="flex:1;text-align: center;">{{itemArr.tickArray[0]}}</div>
              <div style="flex:1;text-align: center;">{{itemArr.tickArray[0]}}</div>
            </div>
          </div>
        </div>
      </div>
      <div class="operation-window">
        <div style="width: 100%;text-align: center;">最新交易记录</div>
        <div style="height:600px;display: flex;flex-direction: column;width: 100%;overflow: hidden; margin-top: 20px;">
          <div style="display: flex;flex-direction: row;width: 100%;margin-bottom: 20px;">
            <div style="flex:1;text-align: center;margin: 0 10px;">价格</div>
            <div style="flex:1;text-align: center;margin: 0 10px;">数量</div>
            <div style="flex:2;text-align: center;margin: 0 10px;">时间</div>
          </div>
          <div style="display: flex;flex-direction: row;width: 100%;" v-for="item in record" :style="{color:(item.direction == 'buy'?'#41b37d':'#d74e5a')}">
            <div style="flex:1;margin: 0 10px;">{{item.price}}</div>
            <div style="flex:1;margin: 0 10px;">{{item.amount}}</div>
            <div style="flex:2;margin: 0 10px;">{{item.ts}}</div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
  import HQChart from 'hqchart'
  import SockJS from 'sockjs-client'
  import Stomp from 'stompjs'
  require('../../node_modules/hqchart/src/jscommon/umychart.es5/umychart.style.js')

  function DefaultData() {}

  DefaultData.GetMinuteOption = function() {
    var option = {
      Type: '历史K线图', //创建图形类型

      Windows: //窗口指标
        [{
            Index: "MA",
            Modify: false,
            Change: false
          },
          {
            Index: "VOL",
            Modify: false,
            Change: false
          }
        ],


      Symbol: 'BTCUSDT.BIT',
      //Symbol:'600000.sh',
      IsAutoUpdate: false, //是自动更新数据
      AutoUpdateFrequency: 5000, //数据更新频率
      IsApiPeriod: false,
      IsShowRightMenu: false, //右键菜单
      IsShowCorssCursorInfo: true, //是否显示十字光标的刻度信息
      //CorssCursorTouchEnd:true,
      DragDownload: {
        Minute: {
          Enable: true
        }
      }, //拖拽下载
      KLine: //K线设置
      {
        DragMode: 1, //拖拽模式 0 禁止拖拽 1 数据拖拽 2 区间选择
        Right: 0, //复权 0 不复权 1 前复权 2 后复权
        Period: 4, //周期 0 日线 1 周线 2 月线 3 年线
        MaxReqeustDataCount: 1000, //数据个数
        MaxRequestMinuteDayCount: 10, //分钟数据取5天
        PageSize: 50, //一屏显示多少数据
        // Info: ["互动易", "大宗交易", '龙虎榜', "调研", "业绩预告", "公告"], //信息地雷
        IsShowTooltip: true, //是否显示K线提示信息
      },

      KLineTitle: //标题设置
      {
        IsShowName: false, //不显示股票名称
        IsShowSettingInfo: false //不显示周期/复权
      },
      Border: //边框
      {
        Left: 5, //左边间距
        Right: 70, //右边间距
        Bottom: 25, //底部间距
        Top: 25 //顶部间距
      },

      Frame: //子框架设置
        [{
            SplitCount: 3,
            StringFormat: 0,
            IsShowLeftText: false
          },
          {
            SplitCount: 2,
            StringFormat: 0,
            IsShowLeftText: false
          },
          {
            SplitCount: 2,
            StringFormat: 0,
            IsShowLeftText: false
          }
        ],

      ExtendChart: //扩展图形
        [

        ],
    };

    return option;
  }


  export default {
    data() {
      var data = {
        Symbol: '600000.sh',

        Minute: {
          JSChart: null,
          Option: DefaultData.GetMinuteOption(),
        },
        stompClient: '',
        symbol: "btcusdt",
        symbolUp: "BTC/USDT",

        close: 0,
        open: 0,
        high: 0,
        low: 0,
        amount: 0,
        count: 0,
        vol: 0,


        asks: [],
        bids: [],
        depthSize: 20,

        record:[],

        symbols: [],
        type: "1min",
        periodObject: [{
            "hq": 0,
            "hb": "1day"
          },
          {
            "hq": 1,
            "hb": "1week"
          },
          {
            "hq": 2,
            "hb": "1mon"
          },
          {
            "hq": 3,
            "hb": "1year"
          },
          {
            "hq": 4,
            "hb": "1min"
          },
          {
            "hq": 5,
            "hb": "5min"
          },
          {
            "hq": 6,
            "hb": "15min"
          },
          {
            "hq": 7,
            "hb": "30min"
          },
          {
            "hq": 8,
            "hb": "60min"
          }
        ]
      };

      return data;

    },

    created() {

    },
    mounted() {

      this.OnSize();
      window.onresize = () => {
        this.OnSize();
      }

      this.initSymbols();
      this.CreateMinuteChart();
      this.initWebsocket();

      // 定时获取市场记录
      setInterval(()=>{
         this.timingGetRecord();
      },500);


    },

    methods: {
      OnSize() {
        var chartHeight = window.innerHeight - 330;
        var chartWidth = window.innerWidth - 830;


        var minute = this.$refs.minute;
        minute.style.width = chartWidth + 'px';
        minute.style.height = chartHeight + 'px';
      },
      initSymbols() {
        let This = this;
        this.$api.get('/v1/market/symbol', {}, response => {
          if (response.status == 200) {
            let symbols = response.data.data;
            for (let i = 0; i < symbols.length; i++) {
              symbols[i].market = {
                "amount": 0,
                "close": 0,
                "count": 0,
                "high": 0,
                "id": 0,
                "low": 0,
                "open": 0,
                "vol": 0
              }
            }
            This.symbols = symbols;
          }
        });
      },
      CreateMinuteChart() //创建日线图
      {
        if (this.Minute.JSChart) return;
        this.Minute.Option.Symbol = this.Symbol;
        this.Minute.Option.NetworkFilter = this.NetworkFilter;
        let chart = HQChart.Chart.JSChart.Init(this.$refs.minute);
        chart.SetOption(this.Minute.Option);
        this.Minute.JSChart = chart;

      },
      NetworkFilter(data, callback) {
        data.PreventDefault = true;
        let name = data.Name;
        // console.error(name);
        switch (name) {
          case "KLineChartContainer::RequestHistoryData":
            {
              console.log("日K数据");
              this.dayKlineData(callback);
              break;
            }
          case "KLineChartContainer::RequestOverlayHistoryData":
            {
              console.log("流通股本数据 (用于计算换手率)");
              break;
            }
          case "KLineChartContainer::RequestOverlayHistoryData":
            {
              console.log("叠加股票日K线数据");
              break;
            }
          case "KLineChartContainer::ReqeustHistoryMinuteData":
            {
              console.log("1分钟K线数据");
              this.minuteKlineData(callback);
              break;
            }
          case "KLineChartContainer::RequestMinuteRealtimeData":
            {
              console.log("当天1分钟K线数据");
              break;
            }
          case "KLineChartContainer::RequestRealtimeData":
            {
              console.log("当天最新日线数据");
              break;
            }
          case "KLineChartContainer::RequestDragMinuteData":
            {
              console.log("拖拽数据下载");
              break;
            }
        }
      },
      initWebsocket() {
        this.connection();

      },
      connection() {
        let socket = new SockJS("http://127.0.0.1:10010/quotation");
        this.stompClient = Stomp.over(socket);
        this.subscribe();
      },
      subscribe() {
        let This = this;
        this.stompClient.connect({}, function(frame) { //连接WebSocket服务端
          console.log('Connected:' + frame);
          This.stompClient.subscribe("/topic/kline/" + This.symbolUp + "/" + This.type, function(response) {
            let klineData = JSON.parse(response.body);
            let showData = This.buildKlineData([klineData.kline]);
            This.Minute.JSChart.JSChartContainer.RecvMinuteRealtimeDataV2(showData);
          });
          This.stompClient.subscribe("/topic/market", function(response) {
            let marketData = JSON.parse(response.body);
            let symbol = marketData.symbol;
            let data = marketData.market;
            This.updateMarketData(data, symbol);
          });
          This.stompClient.subscribe("/topic/depth/" + This.symbolUp, function(response) {
            let marketData = JSON.parse(response.body);
            This.asks = marketData.depth.asks.slice(0, This.depthSize);
            This.bids = marketData.depth.bids.slice(0, This.depthSize);
          });
        });
      },
      disconnect() {
        this.stompClient.disconnect(() => {
          console.log("关闭连接");
        });
      },
      changeSymbol(item) {
        this.close = 0;
        this.open = 0;
        this.high = 0;
        this.low = 0;
        this.amount = 0;
        this.count = 0;
        this.vol = 0;
        this.symbol = item.symbol;
        this.symbolUp = item.symbolUp;
        this.Minute.JSChart.ChangeSymbol(this.symbol);
        this.disconnect();
        this.connection();
      },
      changeCycle(item) {
        this.type = item.hb;
        this.Minute.JSChart.ChangePeriod(item.hq);
        this.disconnect();
        this.connection();
      },
      timingGetRecord(){
        let This = this;
        this.$api.get('/v1/market/latest/trade', {
          symbol: This.symbol
        }, response => {
          if (response.status == 200) {
            let showData = response.data.data.tick.data;
            for(let i=0;i<showData.length;i++){
              let tradeId = showData[i].tradeId;
              let flag = true;
              for(let j=0;j<This.record.length;j++){
                if(This.record[i].tradeId == tradeId){
                  flag = false;
                  break;
                }
              }
              if(flag){
                let d = new Date(showData[i].ts);
                showData[i].ts = d.getHours() + ":" + (d.getMinutes() > 10 ? d.getMinutes() : "0" + d.getMinutes())+ ":" + (d.getSeconds() > 10 ? d.getSeconds() : "0" + d.getSeconds());
                This.record.unshift(showData[i]);
              }
            }
          }
        });
      },
      minuteKlineData(callback) {
        let This = this;
        this.$api.get('/v1/market/', {
          period: This.type,
          size: 1000,
          symbol: This.symbol
        }, response => {
          if (response.status == 200) {
            let showData = This.buildKlineData(response.data.data.data);
            callback(showData);
          }
        });
      },
      updateMarketData(marketData, symbol) {
        for (let i = 0; i < this.symbols.length; i++) {
          if (symbol == this.symbols[i].symbolUp) {
            this.symbols[i].market = marketData;
          }
          if (this.symbolUp == symbol) {
            this.close = marketData.close
            this.open = marketData.open
            this.high = marketData.high
            this.low = marketData.low;
            this.amount = marketData.amount;
            this.count = marketData.count;
            this.vol = marketData.vol;
          }
        }
      },
      dayKlineData(callback) {
        let This = this;
        this.$api.get('/v1/market/', {
          period: "1day",
          size: 300,
          symbol: This.symbol
        }, response => {
          if (response.status == 200) {
            let showData = This.buildKlineData(response.data.data.data);
            callback(showData);
          }
        });
      },
      // 将kline数组转换成hqchart的固定格式
      buildKlineData(klineData) {
        console.log(klineData);
        let data = {
          code: 0,
          data: []
        };
        data.symbol = this.symbol + ".BIT";
        data.name = this.symbol;
        for (let i = klineData.length - 1; i >= 0; i--) {
          let amount = klineData[i].amount;
          let close = klineData[i].close;
          let count = klineData[i].count;
          let high = klineData[i].high;
          let low = klineData[i].low;
          let open = klineData[i].open;
          let vol = klineData[i].vol;
          let time = klineData[i].id * 1000;
          let d = new Date(time);
          let year = d.getFullYear() + "" + (d.getMonth() + 1 > 10 ? d.getMonth() + 1 : "0" + (d.getMonth() + 1)) + "" +
            (d.getDate() > 10 ? d.getDate() : "0" + d.getDate());
          let minute = d.getHours() + "" + (d.getMinutes() > 10 ? d.getMinutes() : "0" + d.getMinutes());
          let tempData = [parseInt(year), open, open, high, low, close, count, vol, parseInt(minute)];
          data.data.push(tempData);
        }
        return data;
      }
    }
  }
</script>

<style>
  .kline-frame {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: row;
  }

  .kline-info {
    display: flex;
    flex-direction: column;
  }

  .operation-window {
    width: 300px;
    display: flex;
    flex-direction: column;
  }

  .market-depth {
    display: flex;
    flex-direction: column;
  }
</style>
