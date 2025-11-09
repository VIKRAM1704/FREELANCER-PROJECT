import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import proposalService from '../../services/proposalService';
import projectService from '../../services/projectService';
import Loader from '../common/Loader';

const ViewProposals = () => {
  const { projectId } = useParams();
  const [project, setProject] = useState(null);
  const [proposals, setProposals] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProposals = async () => {
      try {
        const [projectData, proposalsData] = await Promise.all([
          projectService.getProjectById(projectId),
          proposalService.getProposalsByProject(projectId)
        ]);
        setProject(projectData);
        setProposals(proposalsData);
      } catch (error) {
        console.error('Error fetching proposals:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProposals();
  }, [projectId]);

  const handleAccept = async (proposalId) => {
    if (window.confirm('Accept this proposal? This will reject all other proposals.')) {
      try {
        await proposalService.acceptProposal(proposalId);
        window.location.reload();
      } catch (error) {
        alert('Failed to accept proposal');
      }
    }
  };

  const handleReject = async (proposalId) => {
    if (window.confirm('Are you sure you want to reject this proposal?')) {
      try {
        await proposalService.rejectProposal(proposalId);
        setProposals(proposals.map(p => 
          p.id === proposalId ? { ...p, status: 'REJECTED' } : p
        ));
      } catch (error) {
        alert('Failed to reject proposal');
      }
    }
  };

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>Proposals for: {project?.title}</h2>
          <p className="text-muted">Review and manage proposals from freelancers</p>
        </div>
        <div className="col-auto">
          <Link 
            to={`/client/ai-ranking/${projectId}`}
            className="btn btn-info"
          >
            <span className="ai-badge">AI</span> Rank Proposals
          </Link>
        </div>
      </div>

      <div className="card shadow-sm mb-4">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Project Details</h5>
        </div>
        <div className="card-body">
          <p>{project?.description}</p>
          <div className="row">
            <div className="col-md-4">
              <strong>Budget:</strong> ₹{project?.minBudget} - ₹{project?.maxBudget}
            </div>
            <div className="col-md-4">
              <strong>Duration:</strong> {project?.duration} days
            </div>
            <div className="col-md-4">
              <strong>Proposals:</strong> {proposals.length}
            </div>
          </div>
        </div>
      </div>

      {proposals.length === 0 ? (
        <div className="alert alert-info">
          <h5>No proposals yet</h5>
          <p className="mb-0">Freelancers haven't submitted any proposals yet.</p>
        </div>
      ) : (
        <div className="row g-4">
          {proposals.map((proposal) => (
            <div key={proposal.id} className="col-12">
              <div className="card shadow-sm proposal-card">
                <div className="card-body">
                  <div className="row">
                    <div className="col-md-8">
                      <div className="d-flex justify-content-between align-items-start mb-3">
                        <div>
                          <h5 className="mb-1">{proposal.freelancerName}</h5>
                          <small className="text-muted">
                            Submitted: {new Date(proposal.submittedAt).toLocaleDateString()}
                          </small>
                        </div>
                        <span className={`badge bg-${
                          proposal.status === 'ACCEPTED' ? 'success' :
                          proposal.status === 'REJECTED' ? 'danger' :
                          'warning'
                        }`}>
                          {proposal.status}
                        </span>
                      </div>

                      <div className="row mb-3">
                        <div className="col-sm-6">
                          <strong>Bid Amount:</strong> ₹{proposal.bidAmount}
                        </div>
                        <div className="col-sm-6">
                          <strong>Duration:</strong> {proposal.duration} days
                        </div>
                      </div>

                      <div className="mb-3">
                        <strong>Cover Letter:</strong>
                        <p className="mt-2">{proposal.coverLetter}</p>
                      </div>

                      <div className="mb-2">
                        <strong>Freelancer Skills:</strong>
                        <div className="mt-2">
                          {proposal.freelancerSkills?.map((skill, idx) => (
                            <span key={idx} className="badge bg-secondary me-1 mb-1">
                              {skill}
                            </span>
                          ))}
                        </div>
                      </div>
                    </div>

                    <div className="col-md-4 text-end">
                      {proposal.status === 'PENDING' && (
                        <div className="d-grid gap-2">
                          <button
                            className="btn btn-success"
                            onClick={() => handleAccept(proposal.id)}
                          >
                            Accept Proposal
                          </button>
                          <button
                            className="btn btn-outline-danger"
                            onClick={() => handleReject(proposal.id)}
                          >
                            Reject
                          </button>
                          <Link
                            to={`/freelancer/profile/${proposal.freelancerId}`}
                            className="btn btn-outline-primary"
                          >
                            View Profile
                          </Link>
                        </div>
                      )}

                      {proposal.status === 'ACCEPTED' && (
                        <div className="d-grid gap-2">
                          <Link
                            to={`/payment/${proposal.id}`}
                            className="btn btn-primary"
                          >
                            Make Payment
                          </Link>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ViewProposals;