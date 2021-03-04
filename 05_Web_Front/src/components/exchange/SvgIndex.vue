<template>
    <svg :style="{ height: opts.height + 'px', width: opts.width + 'px' }">
        <defs>
          <linearGradient id="orange_red" x1="0%" y1="100%" x2="0%" y2="0%">
               <stop offset="0%" stop-color="#172333"/>
               <stop offset="100%" stop-color="#1b304e"/>
          </linearGradient>
     </defs>
        <polygon :points="polygonPoints" style="fill:url(#orange_red)">
        </polygon>
       <polyline fill="none" :points="polylinePoints" :stroke="sColor" :stroke-width="opts.strokeWidth" stroke-linecap="square">
       </polyline>
    </svg>
</template>
<script>
export default {
    data:function(){
        return {
          coords:[],
          opts:{strokeWidth:2},
          pColor:'#c6d9fd',
          sColor:'#4d89f9'
        }
    },
    props:{
        values:{
            type:Array,
            required:true
        },
        width:{
            type:Number,
            required:false
        },
        height:{
            type:Number,
            required:false
        },
        rose:{
          type:String,
          required:false
        }
    },
    created:function(){
        this.opts.width = this.width || 120;
        this.opts.height = this.height || 50;
        this.opts.rose = this.rose || 0;
        this.pColor = "#172333";
        this.sColor = "rgb(41, 87, 142)";
        this.draw();
    },
    computed:{
        polylinePoints:function(){
          return this.coords.slice(2, this.coords.length - 2).join(' ');
        },
      polygonPoints:function(){
          return this.coords.join( );
      }
  },
  mounted:function(){

  },
  methods:{
      reload(newTrend, width, height){
        var opts = this.opts;
        this.opts.width = width;
        this.opts.height = height;
        var values = newTrend;
        if (values.length == 1) values.push(values[0])
        var max = Math.max.apply(Math, opts.max == undefined ? values : values.concat(opts.max))
            , min = Math.min.apply(Math, opts.min == undefined ? values : values.concat(opts.min))

        var strokeWidth = opts.strokeWidth
            , width = opts.width
            , height = opts.height - strokeWidth
            , diff = max - min

        var xScale = this.x = function(input) {
            return input * (width / (values.length - 1))
        }

        var yScale = this.y = function(input) {
            var y = height

            if (diff) {
                y -= ((input - min) / diff) * height
            }

            return y + strokeWidth / 2
        }

        var zero = yScale(Math.max(min, 0));
        this.coords = [];
        this.coords = [0, zero]

        for (var i = 0; i < values.length; i++) {
            this.coords.push(
            xScale(i),
            yScale(values[i])
            )
        }

        this.coords.push(width, zero);
      },
      draw(){
        var opts = this.opts;
        var values = this.values;
        if (values.length == 1) values.push(values[0])
        var max = Math.max.apply(Math, opts.max == undefined ? values : values.concat(opts.max))
            , min = Math.min.apply(Math, opts.min == undefined ? values : values.concat(opts.min))

        var strokeWidth = opts.strokeWidth
            , width = opts.width
            , height = opts.height - strokeWidth
            , diff = max - min

        var xScale = this.x = function(input) {
            return input * (width / (values.length - 1))
        }

        var yScale = this.y = function(input) {
            var y = height

            if (diff) {
                y -= ((input - min) / diff) * height
            }

            return y + strokeWidth / 2
        }

        var zero = yScale(Math.max(min, 0));
        this.coords = [];
        this.coords = [0, zero]

        for (var i = 0; i < values.length; i++) {
            this.coords.push(
            xScale(i),
            yScale(values[i])
            )
        }

        this.coords.push(width, zero);
      }
  }
}
</script>
