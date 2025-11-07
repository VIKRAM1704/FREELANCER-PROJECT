import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import proposalService from '../../services/proposalService';
import Loader from '../common/Loader';

const MyProposals = () => {
  const { user } = useAuth();
  const [proposals, setProposals] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProposals();
  }, []);

  const fetchProposals = async () => {
    try {
      const data = await proposalService.getFreelancerProposals(user.id);
      setProposals(data);
    } catch (error) {
      console.error('Error fetching proposals:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleWithdraw = async (proposalId) => {
    if (window.confirm('Are you sure you want to withdraw this proposal?')) {
      try {
        await proposalService.withdrawProposal(proposalId);
        fetchProposals();
      } catch (error) {
        alert('Failed to withdraw proposal');
      }
    }
  };

  const filteredProposals = proposals.filter(p => 
    filter === 'ALL' || p.status === filter
  );

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>My Proposals</h2>
          <p className="text-muted">Track all your submitted proposals</p>
        </div>
      </div>

      {/* Filter */}
      <div className="row mb-4">
        <div className="col">
          <div className="btn-group" role="group">
            <button 
              className={`btn ${filter === 'ALL' ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setFilter('ALL')}
            >
              All ({proposals.length})
            </button>
            <button 
              className={`btn ${filter === 'PENDING' ? 'btn-warning' : 'btn-outline-warning'}`}
              onClick={() => setFilter('PENDING')}
            >
              Pending ({proposals.filter(p => p.status === 'PENDING').length})
            </button>
            <button 
              className={`btn ${filter === 'ACCEPTED' ? 'btn-success' : 'btn-outline-success'}`}
              onClick={() => setFilter('ACCEPTED')}
            >
              Accepted ({proposals.filter(p => p.status === 'ACCEPTED').length})
            </button>
            <button 
              className={`btn ${filter === 'REJECTED' ? 'btn-danger' : 'btn-outline-danger'}`}
              onClick={() => setFilter('REJECTED')}
            >
              Rejected ({proposals.filter(p => p.status === 'REJECTED').length})
            </button>
          </div>
        </div>
      </div>

      {/* Proposals List */}
      {filteredProposals.length === 0 ? (
        <div className="alert alert-info">
          <h5>No proposals found</h5>
          <p className="mb-0">
            Start browsing projects and submit proposals to get started!
          </p>
        </div>
      ) : (
        <div className="row g-4">
          {filteredProposals.map((proposal) => (
            <div key={proposal.id} className="col-12">
              <div className="card shadow-sm proposal-card">
                <div className="card-body">
                  <div className="row">
                    <div className="col-md-8">
                      <h5 className="card-title">{proposal.projectTitle}</h5>
                      <p className="text-muted mb-2">
                        {proposal.projectDescription?.substring(0, 150)}...
                      </p>
                      
                      <div className="row mb-2">
                        <div className="col-sm-6">
                          <strong>Your Bid:</strong> ₹{proposal.bidAmount}
                        </div>
                        <div className="col-sm-6">
                          <strong>Duration:</strong> {proposal.duration} days
                        </div>
                      </div>

                      <div className="mb-2">
                        <strong>Cover Letter:</strong>
                        <p className="text-muted small mt-1">
                          {proposal.coverLetter?.substring(0, 100)}...
                        </p>
                      </div>

                      <small className="text-muted">
                        Submitted: {new Date(proposal.submittedAt).toLocaleDateString()}
                      </small>
                    </div>

                    <div className="col-md-4 text-end">
                      <span className={`badge bg-${
                        proposal.status === 'ACCEPTED' ? 'success' :
                        proposal.status === 'REJECTED' ? 'danger' :
                        proposal.status === 'WITHDRAWN' ? 'secondary' :
                        'warning'
                      } mb-3`}>
                        {proposal.status}
                      </span>

                      <div className="d-grid gap-2">
                        <Link 
                          to={`/projects/${proposal.projectId}`}
                          className="btn btn-sm btn-outline-primary"
                        >
                          View Project
                        </Link>
                        
                        {proposal.status === 'PENDING' && (
                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleWithdraw(proposal.id)}
                          >
                            Withdraw
                          </button>
                        )}

                        {proposal.status === 'ACCEPTED' && (
                          <Link
                            to={`/payment/${proposal.id}`}
                            className="btn btn-sm btn-success"
                          >
                            View Payment
                          </Link>
                        )}
                      </div>
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

export default MyProposals;