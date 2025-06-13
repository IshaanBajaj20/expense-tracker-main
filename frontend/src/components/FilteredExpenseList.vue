<template>
  <div class="container">
    <h2>Filtered Expenses</h2>

    <form @submit.prevent="fetchExpenses">
      <div>
        <label>Category:</label>
        <input v-model="filters.category" type="text" />
      </div>
      <div>
        <label>Min Amount:</label>
        <input v-model.number="filters.minAmount" type="number" />
      </div>
      <div>
        <label>Max Amount:</label>
        <input v-model.number="filters.maxAmount" type="number" />
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
      </div>
    </form>

    <table v-if="expenses.length > 0">
      <thead>
        <tr>
          <th>Description</th>
          <th>Category</th>
          <th>Amount</th>
          <th>Date</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="expense in expenses" :key="expense.id">
          <td>{{ expense.description }}</td>
          <td>{{ expense.category }}</td>
          <td>${{ expense.amount.toFixed(2) }}</td>
          <td>{{ formatDate(expense.date) }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else>No results found.</p>

    <div class="pagination">
      <button @click="prevPage" :disabled="page === 0">Previous</button>
      <span>Page {{ page + 1 }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="page + 1 >= totalPages">Next</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      filters: {
        category: '',
        minAmount: '',
        maxAmount: '',
        startDate: '',
        endDate: ''
      },
      expenses: [],
      page: 0,
      size: 5,
      totalPages: 0
    };
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
    async fetchExpenses() {
      const params = new URLSearchParams({
        page: this.page,
        size: this.size,
        ...this.filters
      });
      const response = await fetch(`http://localhost:8080/api/expenses?${params.toString()}`, {
        headers: {
          Authorization: 'Basic ' + btoa('admin:admin')
        }
      });
      const data = await response.json();
      this.expenses = data.expenses;
      this.totalPages = data.totalPages;
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
    }
  },
  mounted() {
    this.fetchExpenses();
  }
};
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: 20px auto;
  padding: 20px;
  font-family: Arial, sans-serif;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
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
}

label {
  margin-bottom: 6px;
  font-weight: bold;
}

input[type="text"],
input[type="number"],
input[type="date"] {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
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
</style>