import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';
import proposalService from '../../services/proposalService';
import Loader from '../common/Loader';

const SubmitProposal = () => {
  const { projectId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  
  const [project, setProject] = useState(null);
  const [proposal, setProposal] = useState({
    bidAmount: '',
    duration: '',
    coverLetter: '',
    attachments: []
  });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const fetchProject = useCallback(async () => {
    try {
      const data = await projectService.getProjectById(projectId);
      setProject(data);
    } catch (err) {
      setError('Failed to load project details');
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    fetchProject();
  }, [fetchProject]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      const proposalData = {
        ...proposal,
        projectId: parseInt(projectId),
        freelancerId: user.id,
        status: 'PENDING'
      };
      
      await proposalService.submitProposal(proposalData);
      navigate('/freelancer/proposals', { 
        state: { message: 'Proposal submitted successfully!' } 
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit proposal');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-lg-8 mx-auto">
          <h2 className="mb-4">Submit Proposal</h2>

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          {/* Project Details */}
          <div className="card shadow-sm mb-4">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">Project Details</h5>
            </div>
            <div className="card-body">
              <h4>{project?.title}</h4>
              <p className="text-muted">{project?.description}</p>
              
              <div className="row mt-3">
                <div className="col-md-6">
                  <strong>Budget:</strong> ₹{project?.minBudget} - ₹{project?.maxBudget}
                </div>
                <div className="col-md-6">
                  <strong>Duration:</strong> {project?.duration} days
                </div>
              </div>

              <div className="mt-3">
                <strong>Required Skills:</strong>
                <div className="mt-2">
                  {project?.requiredSkills?.map((skill, idx) => (
                    <span key={idx} className="badge bg-secondary me-1 mb-1">
                      {skill}
                    </span>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Proposal Form */}
          <form onSubmit={handleSubmit}>
            <div className="card shadow-sm mb-4">
              <div className="card-header bg-success text-white">
                <h5 className="mb-0">Your Proposal</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <label className="form-label">
                    Bid Amount (₹) <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    className="form-control"
                    value={proposal.bidAmount}
                    onChange={(e) => setProposal({ ...proposal, bidAmount: e.target.value })}
                    min={project?.minBudget}
                    max={project?.maxBudget}
                    required
                  />
                  <small className="text-muted">
                    Suggested range: ₹{project?.minBudget} - ₹{project?.maxBudget}
                  </small>
                </div>

                <div className="mb-3">
                  <label className="form-label">
                    Duration (days) <span className="text-danger">*</span>
                  </label>
                  <input
                    type="number"
                    className="form-control"
                    value={proposal.duration}
                    onChange={(e) => setProposal({ ...proposal, duration: e.target.value })}
                    min="1"
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">
                    Cover Letter <span className="text-danger">*</span>
                  </label>
                  <textarea
                    className="form-control"
                    rows="8"
                    value={proposal.coverLetter}
                    onChange={(e) => setProposal({ ...proposal, coverLetter: e.target.value })}
                    placeholder="Explain why you're the best fit for this project..."
                    required
                  />
                  <small className="text-muted">
                    Highlight your relevant experience and how you plan to complete the project
                  </small>
                </div>
              </div>
            </div>

            <div className="d-grid gap-2 d-md-flex justify-content-md-end">
              <button 
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navigate(-1)}
              >
                Cancel
              </button>
              <button 
                type="submit" 
                className="btn btn-primary"
                disabled={submitting}
              >
                {submitting ? 'Submitting...' : 'Submit Proposal'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SubmitProposal;
