import Vue from 'vue'
import Router from 'vue-router'
import HqchartKline from '../components/hqchart-kline.vue'
import Test from '../components/test.vue'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/test',
      name: 'test',
      component: Test
    },
    {
      path: '/',
      name: 'hqchartKline',
      component: HqchartKline
    }
  ]
})
