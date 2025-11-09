import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';
import Loader from '../common/Loader';

const ProjectList = () => {
  const { user, authenticated } = useAuth();
  const [projects, setProjects] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const data = await projectService.getAllProjects({ status: 'OPEN' });
      setProjects(data);
    } catch (error) {
      console.error('Error fetching projects:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    if (searchTerm) {
      try {
        const data = await projectService.searchProjects(searchTerm);
        setProjects(data);
      } catch (error) {
        console.error('Search error:', error);
      }
    } else {
      fetchProjects();
    }
  };

  const filteredProjects = categoryFilter === 'ALL' 
    ? projects 
    : projects.filter(p => p.category === categoryFilter);

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>Browse Projects</h2>
          <p className="text-muted">Find your next opportunity</p>
        </div>
      </div>

      {/* Search and Filter */}
      <div className="row mb-4">
        <div className="col-md-8">
          <form onSubmit={handleSearch}>
            <div className="input-group">
              <input
                type="text"
                className="form-control"
                placeholder="Search projects..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              <button className="btn btn-primary" type="submit">
                Search
              </button>
            </div>
          </form>
        </div>
        <div className="col-md-4">
          <select
            className="form-select"
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
          >
            <option value="ALL">All Categories</option>
            <option value="WEB_DEVELOPMENT">Web Development</option>
            <option value="MOBILE_DEVELOPMENT">Mobile Development</option>
            <option value="DESIGN">Design</option>
            <option value="WRITING">Writing</option>
            <option value="MARKETING">Marketing</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
      </div>

      {/* Project Grid */}
      {filteredProjects.length === 0 ? (
        <div className="alert alert-info">
          <h5>No projects found</h5>
          <p className="mb-0">Try adjusting your search or filters</p>
        </div>
      ) : (
        <div className="row g-4">
          {filteredProjects.map((project) => (
            <div key={project.id} className="col-md-6 col-lg-4">
              <div className="card h-100 shadow-sm project-card">
                <div className="card-body">
                  <h5 className="card-title">{project.title}</h5>
                  <p className="card-text text-muted">
                    {project.description?.substring(0, 100)}...
                  </p>

                  <div className="mb-3">
                    <strong>Budget:</strong> ₹{project.minBudget} - ₹{project.maxBudget}
                  </div>

                  <div className="mb-3">
                    <strong>Duration:</strong> {project.duration} days
                  </div>

                  <div className="mb-3">
                    <strong>Skills:</strong>
                    <div className="mt-2">
                      {project.requiredSkills?.slice(0, 3).map((skill, idx) => (
                        <span key={idx} className="badge bg-secondary me-1 mb-1">
                          {skill}
                        </span>
                      ))}
                      {project.requiredSkills?.length > 3 && (
                        <span className="badge bg-secondary">
                          +{project.requiredSkills.length - 3}
                        </span>
                      )}
                    </div>
                  </div>

                  <div className="d-grid gap-2">
                    <Link 
                      to={`/projects/${project.id}`}
                      className="btn btn-outline-primary"
                    >
                      View Details
                    </Link>
                    {authenticated && user?.roles?.includes('FREELANCER') && (
                      <Link 
                        to={`/freelancer/submit-proposal/${project.id}`}
                        className="btn btn-primary"
                      >
                        Submit Proposal
                      </Link>
                    )}
                  </div>
                </div>
                <div className="card-footer bg-transparent">
                  <small className="text-muted">
                    Posted: {new Date(project.createdAt).toLocaleDateString()}
                  </small>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ProjectList;
