import api from './api';

const proposalService = {
  submitProposal: async (proposalData) => {
    const response = await api.post('/api/proposals', proposalData);
    return response.data;
  },

  getProposalById: async (proposalId) => {
    const response = await api.get(`/api/proposals/${proposalId}`);
    return response.data;
  },

  getProposalsByProject: async (projectId) => {
    const response = await api.get(`/api/proposals/project/${projectId}`);
    return response.data;
  },

  getFreelancerProposals: async (freelancerId) => {
    const response = await api.get(`/api/proposals/freelancer/${freelancerId}`);
    return response.data;
  },

  updateProposal: async (proposalId, proposalData) => {
    const response = await api.put(`/api/proposals/${proposalId}`, proposalData);
    return response.data;
  },

  deleteProposal: async (proposalId) => {
    const response = await api.delete(`/api/proposals/${proposalId}`);
    return response.data;
  },

  acceptProposal: async (proposalId) => {
    const response = await api.post(`/api/proposals/${proposalId}/accept`);
    return response.data;
  },

  rejectProposal: async (proposalId) => {
    const response = await api.post(`api/proposals/${proposalId}/reject`);
    return response.data;
  },

  withdrawProposal: async (proposalId) => {
    const response = await api.post(`/api/proposals/${proposalId}/withdraw`);
    return response.data;
  }
};

export default proposalService;