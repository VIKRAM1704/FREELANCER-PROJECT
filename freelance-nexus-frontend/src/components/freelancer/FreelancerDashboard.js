import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import freelancerService from '../../services/freelancerService';
import proposalService from '../../services/proposalService';
import Loader from '../common/Loader';

const FreelancerDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [recentProposals, setRecentProposals] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchDashboardData = useCallback(async () => {
    if (!user?.id) return; // ✅ guard if user isn't ready yet
    try {
      const [statsData, proposalsData] = await Promise.all([
        freelancerService.getFreelancerStats(user.id),
        proposalService.getFreelancerProposals(user.id),
      ]);
      setStats(statsData);
      setRecentProposals(proposalsData.slice(0, 5));
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  }, [user?.id]); // ✅ dependency properly declared

  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]); // ✅ ESLint clean, no warnings

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>Welcome back, {user?.firstName || user?.username}! 👋</h2>
          <p className="text-muted">Here's your freelance activity overview</p>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="row g-4 mb-4">
        <div className="col-md-3">
          <div className="card stat-card bg-primary text-white">
            <div className="card-body text-center">
              <h3>{stats?.totalProposals || 0}</h3>
              <p className="mb-0">Total Proposals</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card bg-success text-white">
            <div className="card-body text-center">
              <h3>{stats?.acceptedProposals || 0}</h3>
              <p className="mb-0">Accepted</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card bg-warning text-dark">
            <div className="card-body text-center">
              <h3>{stats?.pendingProposals || 0}</h3>
              <p className="mb-0">Pending</p>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card stat-card bg-info text-white">
            <div className="card-body text-center">
              <h3>₹{stats?.totalEarnings || 0}</h3>
              <p className="mb-0">Total Earnings</p>
            </div>
          </div>
        </div>
      </div>

      {/* AI Recommendations */}
      <div className="row mb-4">
        <div className="col">
          <div className="card shadow-sm ai-feature">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h5 className="mb-2">
                    <span className="ai-badge">AI</span> Project Recommendations
                  </h5>
                  <p className="mb-0">
                    We found projects matching your skills and experience
                  </p>
                </div>
                <Link to="/freelancer/recommendations" className="btn btn-light">
                  View Recommendations
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Proposals */}
      <div className="row">
        <div className="col-md-8">
          <div className="card shadow-sm">
            <div className="card-header bg-white">
              <h5 className="mb-0">Recent Proposals</h5>
            </div>
            <div className="card-body">
              {recentProposals.length === 0 ? (
                <p className="text-muted text-center py-3">No proposals yet</p>
              ) : (
                <div className="list-group list-group-flush">
                  {recentProposals.map((proposal) => (
                    <div key={proposal.id} className="list-group-item">
                      <div className="d-flex justify-content-between align-items-start">
                        <div>
                          <h6 className="mb-1">{proposal.projectTitle}</h6>
                          <p className="mb-1 text-muted small">
                            Bid: ₹{proposal.bidAmount} | Duration: {proposal.duration} days
                          </p>
                          <small className="text-muted">
                            Submitted: {new Date(proposal.submittedAt).toLocaleDateString()}
                          </small>
                        </div>
                        <span
                          className={`badge bg-${
                            proposal.status === 'ACCEPTED'
                              ? 'success'
                              : proposal.status === 'REJECTED'
                              ? 'danger'
                              : 'warning'
                          }`}
                        >
                          {proposal.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              <div className="text-center mt-3">
                <Link to="/freelancer/proposals" className="btn btn-outline-primary">
                  View All Proposals
                </Link>
              </div>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-header bg-white">
              <h5 className="mb-0">Quick Actions</h5>
            </div>
            <div className="card-body">
              <div className="d-grid gap-2">
                <Link to="/projects" className="btn btn-primary">
                  Browse Projects
                </Link>
                <Link to="/freelancer/profile" className="btn btn-outline-primary">
                  Edit Profile
                </Link>
                <Link to="/freelancer/proposals" className="btn btn-outline-secondary">
                  My Proposals
                </Link>
                <Link to="/payment-history" className="btn btn-outline-info">
                  Payment History
                </Link>
              </div>
            </div>
          </div>

          {/* Profile Completion */}
          <div className="card shadow-sm mt-3">
            <div className="card-body">
              <h6 className="mb-3">Profile Completion</h6>
              <div className="progress mb-2" style={{ height: '20px' }}>
                <div
                  className="progress-bar"
                  role="progressbar"
                  style={{ width: `${stats?.profileCompletion || 60}%` }}
                >
                  {stats?.profileCompletion || 60}%
                </div>
              </div>
              <small className="text-muted">
                Complete your profile to get better project matches
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FreelancerDashboard;
