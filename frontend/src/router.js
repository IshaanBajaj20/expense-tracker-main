import Vue from 'vue';
import Router from 'vue-router';
import UserLogin from './components/UserLogin.vue';
import ExpenseList from './components/ExpenseList.vue';
import FilteredExpenseList from './components/FilteredExpenseList.vue';

Vue.use(Router);

export default new Router({
  mode: 'history',
  routes: [
    { path: '/', name: 'login', component: UserLogin },
    { path: '/expenses', name: 'expenses', component: ExpenseList },
    { path: '/filtered', component: FilteredExpenseList }

  ]
});
