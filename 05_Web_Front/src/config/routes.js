export default [
    { path: '/', component: resolve=>(require(["../pages/index/index"],resolve)) },
    { path: '/index', component: resolve=>(require(["../pages/index/index"],resolve)) },
    { path: '/login', component: resolve=>(require(["../pages/uc/login"],resolve)) },
    { path: '/login/returnUrl/:returnUrl', component: resolve=>(require(["../pages/uc/login"],resolve)) },
    { path: '/register', component: resolve=>(require(["../pages/uc/register"],resolve)) },
    { path: '/reg', component: resolve=>(require(["../pages/uc/MobileRegister"],resolve)) },
    { path: '/app', component: resolve=>(require(["../pages/uc/AppDownload"],resolve)) },
    { path: '/findPwd', component: resolve=>(require(["../pages/uc/findpwd"],resolve)) },
    { path: '/exchange', component: resolve=>(require(["../pages/exchange/exchange"],resolve)) },
    { path: '/exchange/:pair', component: resolve=>(require(["../pages/exchange/exchange"],resolve)), name: "ExchangePair"},
    { path: '/help', component: resolve=>(require(["../pages/cms/help"],resolve)) },
    { path: '/helplist', component: resolve=>(require(["../pages/cms/HelpList"],resolve)) },
    { path: '/helpdetail', component: resolve=>(require(["../pages/cms/HelpDetail"],resolve)) },
    { path: '/notice', component: resolve=>(require(["../pages/cms/Notice"],resolve)) },
    { path: '/invite', component: resolve=>(require(["../pages/invite/Invite"],resolve)) },
    { path: '/lab', component: resolve=>(require(["../pages/activity/Activity"],resolve)) },
    { path: '/ctc', component: resolve=>(require(["../pages/ctc/Ctc"],resolve)) },
    { path: '/cexchange', component: resolve=>(require(["../pages/cexchange/cexchange"],resolve)) },
    { path: '/news', component: resolve=>(require(["../pages/news/News"],resolve)) },
    { path: '/lab/detail/:id', component: resolve=>(require(["../pages/activity/ActivityDetail"],resolve)) },
    { path: '/announcement/:id', component: resolve=>(require(["../pages/cms/NoticeItem"],resolve)), name: "NoticeDetail" },
    { path: '/partner', component: resolve=>(require(["../pages/activity/Partner"],resolve)) },
    { path: '/bzb', component: resolve=>(require(["../pages/activity/Bzb"],resolve)) },
    { path: '/whitepaper', component: resolve=>(require(["../pages/cms/WhitePaper"],resolve)) },
    { path: '*', component: resolve=>(require(["../pages/index/index"],resolve)) },
    { path: '/envelope/:eno', component: resolve=>(require(["../pages/envelope/Envelope"],resolve)) },
    {
        path: '/otc',
        component: resolve=>(require(["../pages/otc/Main"],resolve)),
        children: [{
                path: 'trade/*',
                component: resolve=>(require(["../pages/otc/Trade"],resolve))
            }
        ]
    },
    {
        path: '/uc',
        component: resolve=>(require(["../pages/uc/MemberCenter"],resolve)),
        children: [{
                path: '',
                component: resolve=>(require(["../components/uc/Safe"],resolve))
            },
            {
                path: 'safe',
                component: resolve=>(require(["../components/uc/Safe"],resolve))
            },
            {
                path: 'account',
                component: resolve=>(require(["../components/uc/Account"],resolve))
            },
            {
                path: 'money',
                component: resolve=>(require(["../components/uc/MoneyIndex"],resolve))
            },
            {
                path: 'record',
                component: resolve=>(require(["../components/uc/Record"],resolve))
            },
            {
                path: 'recharge',
                component: resolve=>(require(["../components/uc/Recharge"],resolve))
            },
            {
                path: 'withdraw',
                component: resolve=>(require(["../components/uc/Withdraw"],resolve))
            },
            {
                path: 'withdraw/address',
                component: resolve=>(require(["../components/uc/WithdrawAddress"],resolve))
            },
            {
                path: 'withdraw/code',
                component: resolve=>(require(["../components/uc/WithdrawCode"],resolve))
            },
            {
                path: 'ad',
                component: resolve=>(require(["../components/otc/MyAd"],resolve))
            },
            {
                path: 'ad/create',
                component: resolve=>(require(["../pages/otc/AdPublish"],resolve))
            },
            {
                path: 'ad/update',
                component: resolve=>(require(["../pages/otc/AdPublish"],resolve))
            },
            {
                path: 'order',
                component: resolve=>(require(["../components/uc/myorder"],resolve))
            },
            {
                path: 'entrust/current',
                component: resolve=>(require(["../components/uc/EntrustCurrent"],resolve))
            },
            {
                path: 'entrust/history',
                component: resolve=>(require(["../components/uc/EntrustHistory"],resolve))
            }, {
                path: 'trade',
                component: resolve=>(require(["../components/uc/MinTrade"],resolve))
            },
            {
                path: 'invitingmining',
                component: resolve=>(require(["../components/uc/InvitingMin"],resolve))
            },
            {
                path: 'paydividends',
                component: resolve=>(require(["../components/uc/PayDividends"],resolve))
            },
            {
                path: 'promotion/mycards',
                component: resolve=>(require(["../components/uc/PromotionMyCards"],resolve))
            },
            {
                path: 'promotion/mypromotion',
                component: resolve=>(require(["../components/uc/MyPromotion"],resolve))
            },
            {
                path: 'innovation/myorders',
                component: resolve=>(require(["../components/uc/InnovationOrders"],resolve))
            },
            {
                path: 'innovation/myminings',
                component: resolve=>(require(["../components/uc/InnovationMinings"],resolve))
            },
            {
                path: 'innovation/mylocked',
                component: resolve=>(require(["../components/uc/InnovationLocked"],resolve))
            },
            {
                path: 'quickExchange',
                component: resolve=>(require(["../components/uc/QuickExchange"],resolve))
            },
            {
                path: 'apiManage',
                component: resolve=>(require(["../components/uc/apiManage"],resolve))
            }
        ]
    },
    {
        name: 'tradeInfo',
        path: '/otc/tradeInfo',
        component: resolve=>(require(["../pages/otc/TradeInfo"],resolve))
    },
    {
        path: '/checkuser',
        component: resolve=>(require(["../pages/otc/CheckUser"],resolve))
    },
    {
        path: '/chat',
        component: resolve=>(require(["../pages/otc/Chat"],resolve))
    },
    {
        path: '/identbusiness',
        component: resolve=>(require(["../pages/uc/IdentBusiness"],resolve))
    },
    {
        path: '/about-us',
        component: resolve=>(require(["../pages/cms/AboutUs"],resolve))
    },

];
