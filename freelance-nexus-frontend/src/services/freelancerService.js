import api from './api';

const freelancerService = {
  getFreelancerProfile: async (freelancerId) => {
    const response = await api.get(`/freelancers/${freelancerId}`);
    return response.data;
  },

  updateFreelancerProfile: async (freelancerId, profileData) => {
    const response = await api.put(`/freelancers/${freelancerId}`, profileData);
    return response.data;
  },

  getFreelancerSkills: async (freelancerId) => {
    const response = await api.get(`/freelancers/${freelancerId}/skills`);
    return response.data;
  },

  updateFreelancerSkills: async (freelancerId, skills) => {
    const response = await api.put(`/freelancers/${freelancerId}/skills`, skills);
    return response.data;
  },

  getFreelancerPortfolio: async (freelancerId) => {
    const response = await api.get(`/freelancers/${freelancerId}/portfolio`);
    return response.data;
  },

  addPortfolioItem: async (freelancerId, portfolioItem) => {
    const response = await api.post(`/freelancers/${freelancerId}/portfolio`, portfolioItem);
    return response.data;
  },

  deletePortfolioItem: async (freelancerId, itemId) => {
    const response = await api.delete(`/freelancers/${freelancerId}/portfolio/${itemId}`);
    return response.data;
  },

  getFreelancerStats: async (freelancerId) => {
    const response = await api.get(`/freelancers/${freelancerId}/stats`);
    return response.data;
  }
};

export default freelancerService;