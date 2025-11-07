import React, { useState, useEffect } from 'react';
import { Container, Card, Badge, ListGroup } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';
import Loader from '../common/Loader';

const MyProjects = () => {
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    try {
      const data = await projectService.getProjectsByClient(user.id);
      setProjects(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loader />;

  return (
    <Container className="mt-4">
      <h2>My Projects</h2>
      <Card className="mt-4">
        <ListGroup variant="flush">
          {projects.map(p => (
            <ListGroup.Item key={p.id}>
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h5>{p.title}</h5>
                  <p className="mb-0">Budget: ₹{p.budget}</p>
                </div>
                <div>
                  <Badge bg="primary" className="me-2">{p.status}</Badge>
                  <Link to={`/client/projects/${p.id}/proposals`} className="btn btn-sm btn-outline-primary">
                    View Proposals
                  </Link>
                </div>
              </div>
            </ListGroup.Item>
          ))}
        </ListGroup>
      </Card>
    </Container>
  );
};

export default MyProjects;