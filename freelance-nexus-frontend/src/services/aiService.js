import api from './api';

const aiService = {
  getProjectRecommendations: async (freelancerId) => {
    const response = await api.get(`/ai/recommendations/freelancer/${freelancerId}`);
    return response.data;
  },

  rankProposals: async (projectId) => {
    const response = await api.get(`/ai/rank-proposals/${projectId}`);
    return response.data;
  },

  generateProjectSummary: async (projectId) => {
    const response = await api.get(`/ai/summarize-project/${projectId}`);
    return response.data;
  },

  analyzeFreelancerProfile: async (freelancerId) => {
    const response = await api.get(`/ai/analyze-profile/${freelancerId}`);
    return response.data;
  },

  matchFreelancersToProject: async (projectId) => {
    const response = await api.get(`/ai/match-freelancers/${projectId}`);
    return response.data;
  },

  predictProjectSuccess: async (projectId) => {
    const response = await api.get(`/ai/predict-success/${projectId}`);
    return response.data;
  }
};

export default aiService;