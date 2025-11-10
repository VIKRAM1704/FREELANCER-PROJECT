import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../../context/AuthContext';
import freelancerService from '../../services/freelancerService';
import Loader from '../common/Loader';

const FreelancerProfile = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState({
    bio: '',
    hourlyRate: '',
    skills: [],
    portfolio: []
  });
  const [newSkill, setNewSkill] = useState('');
  const [newPortfolio, setNewPortfolio] = useState({
    title: '',
    description: '',
    url: ''
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  // ✅ Stable version of fetchProfile
  const fetchProfile = useCallback(async () => {
    try {
      const data = await freelancerService.getFreelancerProfile(user.id);
      setProfile(data);
    } catch (error) {
      console.error('Error fetching profile:', error);
    } finally {
      setLoading(false);
    }
  }, [user?.id]);

  useEffect(() => {
    if (user?.id) {
      fetchProfile();
    }
  }, [fetchProfile, user?.id]); // ✅ Dependencies fixed

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await freelancerService.updateFreelancerProfile(user.id, profile);
      setMessage({ type: 'success', text: 'Profile updated successfully!' });
    } catch (error) {
      setMessage({ type: 'danger', text: 'Failed to update profile' });
    } finally {
      setSaving(false);
    }
  };

  const addSkill = () => {
    if (newSkill && !profile.skills.includes(newSkill)) {
      setProfile({ ...profile, skills: [...profile.skills, newSkill] });
      setNewSkill('');
    }
  };

  const removeSkill = (skill) => {
    setProfile({
      ...profile,
      skills: profile.skills.filter((s) => s !== skill),
    });
  };

  const addPortfolioItem = () => {
    if (newPortfolio.title && newPortfolio.description) {
      setProfile({
        ...profile,
        portfolio: [...profile.portfolio, { ...newPortfolio, id: Date.now() }],
      });
      setNewPortfolio({ title: '', description: '', url: '' });
    }
  };

  const removePortfolioItem = (id) => {
    setProfile({
      ...profile,
      portfolio: profile.portfolio.filter((item) => item.id !== id),
    });
  };

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-lg-8 mx-auto">
          <h2 className="mb-4">My Profile</h2>

          {message.text && (
            <div className={`alert alert-${message.type}`} role="alert">
              {message.text}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {/* Basic Info */}
            <div className="card shadow-sm mb-4">
              <div className="card-header bg-primary text-white">
                <h5 className="mb-0">Basic Information</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <label className="form-label">Bio</label>
                  <textarea
                    className="form-control"
                    rows="4"
                    value={profile.bio}
                    onChange={(e) =>
                      setProfile({ ...profile, bio: e.target.value })
                    }
                    placeholder="Tell us about yourself and your expertise..."
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Hourly Rate (₹)</label>
                  <input
                    type="number"
                    className="form-control"
                    value={profile.hourlyRate}
                    onChange={(e) =>
                      setProfile({ ...profile, hourlyRate: e.target.value })
                    }
                    placeholder="Enter your hourly rate"
                  />
                </div>
              </div>
            </div>

            {/* Skills */}
            <div className="card shadow-sm mb-4">
              <div className="card-header bg-success text-white">
                <h5 className="mb-0">Skills</h5>
              </div>
              <div className="card-body">
                <div className="input-group mb-3">
                  <input
                    type="text"
                    className="form-control"
                    value={newSkill}
                    onChange={(e) => setNewSkill(e.target.value)}
                    placeholder="Add a skill"
                    onKeyPress={(e) =>
                      e.key === 'Enter' && (e.preventDefault(), addSkill())
                    }
                  />
                  <button
                    className="btn btn-success"
                    type="button"
                    onClick={addSkill}
                  >
                    Add
                  </button>
                </div>
                <div className="d-flex flex-wrap">
                  {profile.skills.map((skill, index) => (
                    <span key={index} className="skill-tag">
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

            {/* Portfolio */}
            <div className="card shadow-sm mb-4">
              <div className="card-header bg-info text-white">
                <h5 className="mb-0">Portfolio</h5>
              </div>
              <div className="card-body">
                <div className="row mb-3">
                  <div className="col-md-6">
                    <input
                      type="text"
                      className="form-control mb-2"
                      value={newPortfolio.title}
                      onChange={(e) =>
                        setNewPortfolio({
                          ...newPortfolio,
                          title: e.target.value,
                        })
                      }
                      placeholder="Project Title"
                    />
                  </div>
                  <div className="col-md-6">
                    <input
                      type="url"
                      className="form-control mb-2"
                      value={newPortfolio.url}
                      onChange={(e) =>
                        setNewPortfolio({
                          ...newPortfolio,
                          url: e.target.value,
                        })
                      }
                      placeholder="Project URL (optional)"
                    />
                  </div>
                  <div className="col-12">
                    <textarea
                      className="form-control mb-2"
                      rows="2"
                      value={newPortfolio.description}
                      onChange={(e) =>
                        setNewPortfolio({
                          ...newPortfolio,
                          description: e.target.value,
                        })
                      }
                      placeholder="Project Description"
                    />
                  </div>
                  <div className="col-12">
                    <button
                      type="button"
                      className="btn btn-info"
                      onClick={addPortfolioItem}
                    >
                      Add Portfolio Item
                    </button>
                  </div>
                </div>

                {profile.portfolio.map((item) => (
                  <div key={item.id} className="portfolio-item">
                    <div className="d-flex justify-content-between">
                      <div>
                        <h6>{item.title}</h6>
                        <p className="text-muted mb-1">{item.description}</p>
                        {item.url && (
                          <a
                            href={item.url}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="small"
                          >
                            View Project →
                          </a>
                        )}
                      </div>
                      <button
                        type="button"
                        className="btn btn-sm btn-outline-danger"
                        onClick={() => removePortfolioItem(item.id)}
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary w-100"
              disabled={saving}
            >
              {saving ? 'Saving...' : 'Save Profile'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default FreelancerProfile;
