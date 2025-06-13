<template>
  <div class="expense-form">
    <form @submit.prevent="addExpense">
      <div class="form-group">
        <label>Description</label>
        <input v-model="description" placeholder="e.g. Car" required />
        <span class="error" v-if="errors.description">{{ errors.description }}</span>
      </div>

      <div class="form-group">
        <label>Amount</label>
        <input v-model.number="amount" type="number" placeholder="e.g. 25" required />
        <span class="error" v-if="errors.amount">{{ errors.amount }}</span>
      </div>

      <div class="form-group">
        <label>Date</label>
        <input v-model="date" type="date" required />
        <span class="error" v-if="errors.date">{{ errors.date }}</span>
      </div>

      <div class="form-group">
        <label>Category</label>
        <select v-model="category" required>
          <option disabled value="">Select Category</option>
          <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
        </select>
        <span class="error" v-if="errors.category">{{ errors.category }}</span>
      </div>

      <button type="submit">Add Expense</button>
    </form>

    <p class="success" v-if="successMessage">{{ successMessage }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      description: '',
      amount: 0,
      date: '',
      category: '',
      categories: ['Travel', 'Food', 'Entertainment', 'Transport', 'Others'],
      errors: {},
      successMessage: ''
    };
  },
  methods: {
    async addExpense() {
      this.errors = {};
      this.successMessage = '';
      const auth = localStorage.getItem('auth');

      try {
        await axios.post(
          'http://localhost:8080/api/expenses',
          {
            description: this.description,
            amount: this.amount,
            date: this.date,
            category: this.category
          },
          {
            headers: { Authorization: `Basic ${auth}` }
          }
        );

        this.successMessage = 'Expense added successfully!';
        this.description = '';
        this.amount = 0;
        this.date = '';
        this.category = '';
        this.$emit('expense-added');

      } catch (error) {
        if (error.response && error.response.status === 400) {
          this.errors = error.response.data;
        } else {
          console.error('Error submitting expense:', error);
        }
      }
    }
  }
};
</script>

<style scoped>
.expense-form {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 12px;
  font-family: Arial, sans-serif;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: bold;
  margin-bottom: 6px;
}

input,
select {
  padding: 10px;
  font-size: 1em;
  border-radius: 6px;
  border: 1px solid #ccc;
}

button {
  padding: 12px;
  font-size: 1em;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
}

button:hover {
  background-color: #218838;
}

.error {
  color: red;
  font-size: 0.85em;
  margin-top: 4px;
}

.success {
  color: green;
  font-weight: bold;
  margin-top: 12px;
}
</style>
