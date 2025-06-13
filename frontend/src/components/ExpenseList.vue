<template>
  <div class="app-bg">
    <div class="container responsive-layout">
      <!-- Left: Add Expense Form -->
      <div class="left-pane">
        <h2>Add Expense</h2>
        <expense-form @expense-added="fetchExpenses" />
      </div>

      <!-- Right: Filter and Table -->
      <div class="right-pane">
        <!-- Header Tabs -->
        <div class="tabs">
          <button :class="{ active: activeTab === 'current' }" @click="activeTab = 'current'">Current Expenses</button>
          <button :class="{ active: activeTab === 'archived' }" @click="activeTab = 'archived'">Archived Expenses</button>
        </div>

        <!-- Filter Form (only for current tab) -->
        <form v-if="activeTab === 'current'" @submit.prevent="fetchExpenses" class="filter-form">
          <div>
            <label>Category:</label>
            <select v-model="filters.category">
              <option value="">All</option>
              <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
            </select>
          </div>
          <div class="slider-container">
            <label>Max Amount: ${{ filters.maxAmount }}</label>
            <input type="range" v-model.number="filters.maxAmount" :min="amountRange.min" :max="amountRange.max" step="1" />
          </div>
          <div>
            <label>Start Date:</label>
            <input v-model="filters.startDate" type="date" />
          </div>
          <div>
            <label>End Date:</label>
            <input v-model="filters.endDate" type="date" />
          </div>
          <div class="full-width">
            <button type="submit">Apply Filters</button>
            <button type="button" @click="resetFilters" class="reset-btn">Reset Filters</button>
          </div>
        </form>

        <!-- Expense Table -->
        <table v-if="expenses.length > 0">
          <thead>
            <tr>
              <th>Description</th>
              <th>Category</th>
              <th>Amount</th>
              <th>Date</th>
              <th v-if="activeTab === 'current'">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="expense in expenses" :key="expense.id">
              <td>{{ expense.description }}</td>
              <td>{{ expense.category }}</td>
              <td>${{ expense.amount.toFixed(2) }}</td>
              <td>{{ formatDate(expense.date) }}</td>
              <td v-if="activeTab === 'current'">
                <button @click="archiveExpense(expense.id)">Archive</button>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-else>No results found.</p>

        <!-- Pagination -->
        <div class="pagination" v-if="activeTab === 'current'">
          <button @click="prevPage" :disabled="page === 0">Previous</button>
          <span>Page {{ page + 1 }} of {{ totalPages }}</span>
          <button @click="nextPage" :disabled="page + 1 >= totalPages">Next</button>
        </div>

        <button @click="logout" class="logout-btn">Logout</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import ExpenseForm from './ExpenseForm.vue';

export default {
  components: { ExpenseForm },
  data() {
    return {
      activeTab: 'current',
      filters: {
        category: '',
        maxAmount: 1000,
        startDate: '',
        endDate: ''
      },
      amountRange: {
        min: 0,
        max: 1000
      },
      expenses: [],
      page: 0,
      size: 5,
      totalPages: 0,
      categories: ['Travel', 'Food', 'Entertainment', 'Transport', 'Others']
    };
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
    async fetchExpenses() {
      const auth = localStorage.getItem('auth');
      if (!auth) return this.$router.push('/');

      const params = new URLSearchParams({
        page: this.page,
        size: this.size,
        ...this.filters
      });
      try {
        const res = await axios.get(`http://localhost:8080/api/expenses?${params.toString()}`, {
          headers: { Authorization: `Basic ${auth}` }
        });
        this.expenses = res.data.expenses;
        this.totalPages = res.data.totalPages;
      } catch (err) {
        this.$router.push('/');
      }
    },
    async fetchArchived() {
      const auth = localStorage.getItem('auth');
      if (!auth) return this.$router.push('/');

      try {
        const res = await axios.get('http://localhost:8080/api/expenses/archived', {
          headers: { Authorization: `Basic ${auth}` }
        });
        this.expenses = res.data.expenses;
      } catch (err) {
        console.error('Error loading archived:', err);
      }
    },
    resetFilters() {
      this.filters = {
        category: '',
        maxAmount: 1000,
        startDate: '',
        endDate: ''
      };
      this.page = 0;
      this.fetchExpenses();
    },
    nextPage() {
      if (this.page + 1 < this.totalPages) {
        this.page++;
        this.fetchExpenses();
      }
    },
    prevPage() {
      if (this.page > 0) {
        this.page--;
        this.fetchExpenses();
      }
    },
    async archiveExpense(id) {
      const auth = localStorage.getItem('auth');
      try {
        await axios.delete(`http://localhost:8080/api/expenses/archive/${id}`, {
          headers: { Authorization: `Basic ${auth}` }
        });
        this.fetchExpenses();
      } catch (err) {
        console.error('Archive failed:', err);
      }
    },
    logout() {
      localStorage.removeItem('auth');
      this.$router.push('/');
    }
  },
  watch: {
    activeTab(newVal) {
      if (newVal === 'current') this.fetchExpenses();
      else if (newVal === 'archived') this.fetchArchived();
    }
  },
  mounted() {
    this.fetchExpenses();
  }
};
</script>

<style scoped>
.app-bg {
  background-color: #f2f2f2;
  min-height: 100vh;
  padding: 40px 0;
}
.container {
  max-width: 1200px;
  margin: auto;
  padding: 20px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.left-pane,
.right-pane {
  flex: 1;
  min-width: 300px;
  padding: 20px;
}
.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.tabs button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  background: #e0e0e0;
  cursor: pointer;
}
.tabs button.active {
  background: #007bff;
  color: white;
}
form {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}
form div {
  display: flex;
  flex-direction: column;
}
.full-width {
  grid-column: span 2;
  display: flex;
  gap: 10px;
}
label {
  margin-bottom: 6px;
  font-weight: bold;
}
input,
select {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
.slider-container input[type=range] {
  margin: 5px 0;
}
button {
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
}
button:disabled {
  background-color: #cccccc;
}
.reset-btn {
  background-color: #6c757d;
}
.logout-btn {
  margin-top: 20px;
  background-color: #dc3545;
}
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}
th, td {
  padding: 10px;
  border-bottom: 1px solid #ccc;
  text-align: left;
}
th {
  background-color: #f2f2f2;
}
table td button {
  background-color: #ffc107;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  color: #000;
  font-weight: bold;
  cursor: pointer;
}
@media (max-width: 768px) {
  .container {
    flex-direction: column;
  }
}
</style>