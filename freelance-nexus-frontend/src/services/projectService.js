import api from './api';

const projectService = {
  getAllProjects: async (filters = {}) => {
    const params = new URLSearchParams(filters);
    const response = await api.get(`/api/projects?${params}`);
    return response.data;
  },

  getProjectById: async (projectId) => {
    const response = await api.get(`/api/projects/${projectId}`);
    return response.data;
  },

  createProject: async (projectData) => {
    const response = await api.post('/api/projects', projectData);
    return response.data;
  },

  updateProject: async (projectId, projectData) => {
    const response = await api.put(`/api/projects/${projectId}`, projectData);
    return response.data;
  },

  deleteProject: async (projectId) => {
    const response = await api.delete(`/api/projects/${projectId}`);
    return response.data;
  },

  getClientProjects: async (clientId) => {
    const response = await api.get(`/api/projects/client/${clientId}`);
    return response.data;
  },

  getProjectsByStatus: async (status) => {
    const response = await api.get(`/api/projects/status/${status}`);
    return response.data;
  },

  searchProjects: async (query) => {
    const response = await api.get(`/api/projects/search?q=${query}`);
    return response.data;
  }
};

export default projectService;