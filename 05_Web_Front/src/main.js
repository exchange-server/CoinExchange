// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
import VueClipboard from 'vue-clipboard2';
import routes from './config/routes.js';
import store from './config/store.js';
import VueRouter from 'vue-router';
import vueResource from 'vue-resource';
import VueI18n from 'vue-i18n';
import iView from 'iview';
import 'iview/dist/styles/iview.css';
import util from './assets/js/util.js';
import 'swiper/dist/css/swiper.css';
import './assets/icons/iconfont.css';
import App from './App.vue';
import Api from './config/api';
import $ from '@js/jquery.min.js';
var moment = require('moment');
var momentTimezone = require('moment-timezone');

import ViewUI from 'view-design';
import locale from 'view-design/dist/locale/zh-CN';
Vue.use(iView);
Vue.use(VueClipboard);
Vue.use(VueRouter);
Vue.use(vueResource);
Vue.use(VueI18n);
Vue.use(ViewUI, { locale });

Vue.prototype.rootHost = "https://www.bizzan.top"; //BIZZAN
Vue.prototype.host = "https://api.bizzan.pro"; //BIZZAN

Vue.prototype.api = Api;
Vue.http.options.credentials = true;
Vue.http.options.emulateJSON = true;
Vue.http.options.headers = {
    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
    'Content-Type': 'application/json;charset=utf-8'
};

const router = new VueRouter({
    mode: 'hash',
    routes
});

iView.LoadingBar.config({
    color: '#F90',
    failedColor: '#f0ad4e',
    height: 2
});

router.beforeEach((to, from, next) => {
    iView.LoadingBar.start();
    next();
});

router.afterEach((to,from,next) => {
    window.scrollTo(0,0);
    iView.LoadingBar.finish();
});

const i18n = new VueI18n({
    locale: 'zh_CN',
    messages: {
        'zh_CN': require('./assets/lang/cn.js'),
        'en_US': require('./assets/lang/en.js'),
		'zh_HK': require('./assets/lang/hk.js'),
		'ja_JP': require('./assets/lang/jp.js'),
		'ko_KR': require('./assets/lang/ko.js'),
		'de_DE': require('./assets/lang/de.js'),
		'fr_FR': require('./assets/lang/fr.js'),
		'it_IT': require('./assets/lang/it.js'),
		'es_ES': require('./assets/lang/es.js'),
    },
    silentTranslationWarn: true
});

Vue.http.interceptors.push((request, next) => {
    //登录成功后将后台返回的TOKEN在本地存下来,每次请求从sessionStorage中拿到存储的TOKEN值
    request.headers.set('x-auth-token', localStorage.getItem('TOKEN'));
	let lang = localStorage.getItem('LANGUAGE');
	if(lang!=null){
        lang = lang.substr(1);
        lang = lang.substr(0,lang.length-1);
    }
	request.headers.set('lang', lang);
	
    next((response) => {
        //登录极验证时需获取后台返回的TOKEN值
        var xAuthToken = response.headers.get('x-auth-token');
        if (xAuthToken != null && xAuthToken != '') {
            localStorage.setItem('TOKEN', xAuthToken);
        }

        if (response.data.code == '4000' || response.data.code == '3000') {
            store.commit('setMember', null);
            router.push('/login');
            return false;
        }
        return response;
    });
});

Vue.config.productionTip = false;

Vue.filter('timeFormat', function(tick) {
    return moment(tick).format("HH:mm:ss");
});

Vue.filter('dateFormat', function(tick) {
    return moment(tick).format("YYYY-MM-DD HH:mm:ss");
});

Vue.filter('toFixed', function(number, scale) {
    return new Number(number).toFixed(scale);
});

Vue.filter('toPercent', function(point) {
    var str = Number(point * 100).toFixed(1);
    str += "%";
    return str;
});

function toFloor(number, scale = 8) {
    if (new Number(number) == 0) {
        return 0;
    }
    var __str = number + "";
    if (__str.indexOf('e') > -1 || __str.indexOf('E') > -1) {
        var __num = new Number(number).toFixed(scale + 1),
            __str = __num + "";
        return __str.substring(0, __str.length - 1);
    } else if (__str.indexOf(".") > -1) {
        if (scale == 0) {
            return __str.substring(0, __str.indexOf("."));
        }
        return __str.substring(0, __str.indexOf(".") + scale + 1);
    } else {
        return __str;
    }
}
Vue.filter('toFloor', (number, scale) => {
    return toFloor(number, scale);
});
Vue.prototype.toFloor = toFloor;

Vue.prototype.getTimezone4K = function(){
		var curlang = this.$store.getters.lang;
		if(curlang=="en_US"){
			return "America/Los_Angeles";
		}
		if(curlang=="ja_JP"){
			return "Asia/Tokyo";
		}
		if(curlang=="ko_KR"){
			return "Asia/Seoul";
		}
		if(curlang=="de_DE"){
			return "Europe/Berlin";
		}
		if(curlang=="fr_FR"){
			return "Europe/Paris";
		}
		if(curlang=="it_IT"){
			return "Europe/Rome";
		}
		if(curlang=="es_ES"){
			return "Europe/Madrid";
		}
		if(curlang=="zh_HK"){
			return "Asia/Hong_Kong";
		}
		if(curlang=="zh_CN"){
			return "Asia/Shanghai";
		}
		return curlang;
};
Vue.prototype.getLang4K = function(){
		var curlang = this.$store.getters.lang;
		if(curlang=="en_US"){
			return "en";
		}
		if(curlang=="ja_JP"){
			return "ja";
		}
		if(curlang=="ko_KR"){
			return "ko";
		}
		if(curlang=="de_DE"){
			return "de_DE";
		}
		if(curlang=="fr_FR"){
			return "fr";
		}
		if(curlang=="it_IT"){
			return "it";
		}
		if(curlang=="es_ES"){
			return "es";
		}
		if(curlang=="zh_HK"){
			return "zh_TW";
		}
		if(curlang=="zh_CN"){
			return "zh";
		}
		return curlang;
};
Vue.prototype.timeFormat=function(tick) {
      return momentTimezone(tick).tz(this.getTimezone4K()).format("HH:mm:ss");
    };
Vue.prototype.dateFormat=function(tick) {
      return momentTimezone(tick).tz(this.getTimezone4K()).format("YYYY-MM-DD HH:mm:ss");
    };
Vue.prototype.dateFormatHM=function(tick) {
      return momentTimezone(tick).tz(this.getTimezone4K()).format("YYYY-MM-DD HH:mm");
    };
Vue.prototype.dateFormatFromString=function(tick){
	var timestamp = momentTimezone(tick).tz('Asia/Shanghai').valueOf();
	return momentTimezone(timestamp).tz(this.getTimezone4K()).format("YYYY-MM-DD HH:mm:ss");
};
new Vue({
    el: '#app',
    router,
    i18n,
    store,
    template: '<App/>',
    components: { App }
});
