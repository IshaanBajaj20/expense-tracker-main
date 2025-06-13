import { shallowMount } from '@vue/test-utils';
import ExpenseForm from '@/components/ExpenseForm.vue';
import axios from 'axios';

jest.mock('axios');

describe('ExpenseForm.vue', () => {
  beforeEach(() => {
    localStorage.setItem('auth', btoa('admin:admin'));
  });

  it('emits "expense-added" when form is submitted successfully', async () => {
    axios.post.mockResolvedValueOnce({ data: {} });

    const wrapper = shallowMount(ExpenseForm, {
      data() {
        return {
          description: 'Test Expense',
          amount: 50,
          date: '2025-06-12',
          category: 'Food',
          categories: ['Food', 'Travel'],
          errors: {},
          successMessage: ''
        };
      }
    });

    await wrapper.find('form').trigger('submit.prevent');

    await wrapper.vm.$nextTick();

    expect(wrapper.emitted('expense-added')).toBeTruthy();
  });
});
