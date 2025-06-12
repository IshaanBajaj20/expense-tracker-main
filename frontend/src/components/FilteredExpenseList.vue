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
      <button type="submit">Apply Filters</button>
    </form>

    <hr />

    <div v-if="expenses.length">
      <table>
        <thead>
          <tr>
            <th>Description</th>
            <th>Amount</th>
            <th>Date</th>
            <th>Category</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="expense in expenses" :key="expense.id">
            <td>{{ expense.description }}</td>
            <td>{{ expense.amount }}</td>
            <td>{{ expense.date }}</td>
            <td>{{ expense.category }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else>No results found.</div>

    <div class="pagination">
      <button @click="prevPage" :disabled="page === 0">Previous</button>
      <span>Page {{ page + 1 }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="page + 1 >= totalPages">Next</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'FilteredExpenseList',
  data() {
    return {
      filters: {
        category: '',
        minAmount: '',
        maxAmount: '',
        startDate: '',
        endDate: '',
      },
      expenses: [],
      page: 0,
      size: 5,
      totalPages: 0,
    };
  },
  methods: {
    fetchExpenses() {
      const params = {
        ...this.filters,
        page: this.page,
        size: this.size,
      };

      axios
        .get('http://localhost:8080/api/expenses', {
          params,
          auth: {
            username: 'admin',
            password: 'admin',
          },
        })
        .then((res) => {
          this.expenses = res.data.expenses;
          this.totalPages = res.data.totalPages;
        })
        .catch((err) => {
          console.error('Error fetching expenses:', err);
        });
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
  },
  mounted() {
    this.fetchExpenses();
  },
};
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: 30px auto;
  padding: 20px;
  font-family: 'Segoe UI', sans-serif;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

h2 {
  text-align: center;
  margin-bottom: 24px;
  font-size: 26px;
}

form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 20px;
}

form div {
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 6px;
  font-weight: 600;
  color: #333;
}

input[type="text"],
input[type="number"],
input[type="date"] {
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
  font-size: 14px;
}

button {
  grid-column: span 2;
  padding: 10px 16px;
  font-weight: 600;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s all ease;
}

button:hover {
  background-color: #0056b3;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 16px;
}

table th,
table td {
  text-align: left;
  padding: 12px;
  border-bottom: 1px solid #ddd;
}

table th {
  background-color: #f9f9f9;
  font-weight: 600;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 12px;
  border: 1px solid #ccc;
  background-color: #f5f5f5;
  cursor: pointer;
  border-radius: 6px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>

