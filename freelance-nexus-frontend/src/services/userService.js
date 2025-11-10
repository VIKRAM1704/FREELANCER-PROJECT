import api from './api';

const userService = {
  getUserProfile: async (userId) => {
    const response = await api.get(`/users/${userId}`);
    return response.data;
  },

  updateUserProfile: async (userId, userData) => {
    const response = await api.put(`api/users/${userId}`, userData);
    return response.data;
  },

  getAllUsers: async () => {
    const response = await api.get('api/users');
    return response.data;
  },

  deleteUser: async (userId) => {
    const response = await api.delete(`api/users/${userId}`);
    return response.data;
  }
};

export default userService;
