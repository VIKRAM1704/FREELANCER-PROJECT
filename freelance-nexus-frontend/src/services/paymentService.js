import api from './api';

const paymentService = {
  initiatePayment: async (paymentData) => {
    const response = await api.post('/payments', paymentData);
    return response.data;
  },

  processUPIPayment: async (paymentId, upiData) => {
    const response = await api.post(`/payments/${paymentId}/upi`, upiData);
    return response.data;
  },

  getPaymentById: async (paymentId) => {
    const response = await api.get(`/payments/${paymentId}`);
    return response.data;
  },

  getPaymentHistory: async (userId) => {
    const response = await api.get(`/payments/user/${userId}`);
    return response.data;
  },

  getPaymentByProposal: async (proposalId) => {
    const response = await api.get(`/payments/proposal/${proposalId}`);
    return response.data;
  },

  verifyPayment: async (paymentId, transactionId) => {
    const response = await api.post(`/payments/${paymentId}/verify`, { transactionId });
    return response.data;
  },

  requestRefund: async (paymentId, reason) => {
    const response = await api.post(`/payments/${paymentId}/refund`, { reason });
    return response.data;
  }
};

export default paymentService;
