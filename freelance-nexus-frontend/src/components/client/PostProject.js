import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';

const PostProject = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    minBudget: '',
    maxBudget: '',
    duration: '',
    requiredSkills: [],
    category: ''
  });
  const [newSkill, setNewSkill] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const addSkill = () => {
    if (newSkill && !formData.requiredSkills.includes(newSkill)) {
      setFormData({ 
        ...formData, 
        requiredSkills: [...formData.requiredSkills, newSkill] 
      });
      setNewSkill('');
    }
  };

  const removeSkill = (skill) => {
    setFormData({ 
      ...formData, 
      requiredSkills: formData.requiredSkills.filter(s => s !== skill) 
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      const projectData = {
        ...formData,
        clientId: user.id,
        status: 'OPEN'
      };
      await projectService.createProject(projectData);
      navigate('/client/projects');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to post project');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-lg-8 mx-auto">
          <h2 className="mb-4">Post a New Project</h2>

          {error && (
            <div className="alert alert-danger">{error}</div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="card shadow-sm mb-4">
              <div className="card-header bg-primary text-white">
                <h5 className="mb-0">Project Details</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <label className="form-label">Project Title *</label>
                  <input
                    type="text"
                    className="form-control"
                    name="title"
                    value={formData.title}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Description *</label>
                  <textarea
                    className="form-control"
                    name="description"
                    rows="6"
                    value={formData.description}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="row">
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Min Budget (₹) *</label>
                    <input
                      type="number"
                      className="form-control"
                      name="minBudget"
                      value={formData.minBudget}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div className="col-md-6 mb-3">
                    <label className="form-label">Max Budget (₹) *</label>
                    <input
                      type="number"
                      className="form-control"
                      name="maxBudget"
                      value={formData.maxBudget}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label">Duration (days) *</label>
                  <input
                    type="number"
                    className="form-control"
                    name="duration"
                    value={formData.duration}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Category *</label>
                  <select
                    className="form-select"
                    name="category"
                    value={formData.category}
                    onChange={handleChange}
                    required
                  >
                    <option value="">Select category</option>
                    <option value="WEB_DEVELOPMENT">Web Development</option>
                    <option value="MOBILE_DEVELOPMENT">Mobile Development</option>
                    <option value="DESIGN">Design</option>
                    <option value="WRITING">Writing</option>
                    <option value="MARKETING">Marketing</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>

                <div className="mb-3">
                  <label className="form-label">Required Skills *</label>
                  <div className="input-group mb-2">
                    <input
                      type="text"
                      className="form-control"
                      value={newSkill}
                      onChange={(e) => setNewSkill(e.target.value)}
                      placeholder="Add a skill"
                      onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addSkill())}
                    />
                    <button 
                      className="btn btn-primary" 
                      type="button"
                      onClick={addSkill}
                    >
                      Add
                    </button>
                  </div>
                  <div>
                    {formData.requiredSkills.map((skill, idx) => (
                      <span key={idx} className="skill-tag">
                        {skill}
                        <button
                          type="button"
                          className="btn-close btn-close-sm ms-2"
                          onClick={() => removeSkill(skill)}
                          style={{ fontSize: '0.7rem' }}
                        ></button>
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            <div className="d-grid gap-2 d-md-flex justify-content-md-end">
              <button 
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navigate('/client/dashboard')}
              >
                Cancel
              </button>
              <button 
                type="submit" 
                className="btn btn-primary"
                disabled={submitting}
              >
                {submitting ? 'Posting...' : 'Post Project'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PostProject;