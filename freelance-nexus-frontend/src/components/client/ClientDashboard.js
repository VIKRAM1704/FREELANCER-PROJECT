import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import projectService from '../../services/projectService';

const ClientDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalProjects: 0, activeProjects: 0, totalSpent: 0
  });

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const projects = await projectService.getProjectsByClient(user.id);
      setStats({
        totalProjects: projects.length,
        activeProjects: projects.filter(p => p.status === 'IN_PROGRESS').length,
        totalSpent: projects.reduce((sum, p) => sum + p.budget, 0)
      });
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Container className="mt-4">
      <h2>Client Dashboard</h2>
      <Row className="mt-4">
        <Col md={4}>
          <Card className="text-center shadow-sm">
            <Card.Body>
              <i className="bi bi-folder" style={{fontSize: '2.5rem'}}></i>
              <h3>{stats.totalProjects}</h3>
              <p>Total Projects</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center shadow-sm">
            <Card.Body>
              <i className="bi bi-briefcase" style={{fontSize: '2.5rem'}}></i>
              <h3>{stats.activeProjects}</h3>
              <p>Active Projects</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="text-center shadow-sm">
            <Card.Body>
              <i className="bi bi-currency-rupee" style={{fontSize: '2.5rem'}}></i>
              <h3>₹{stats.totalSpent}</h3>
              <p>Total Spent</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>
      <Row className="mt-4">
        <Col>
          <Link to="/client/post-project" className="btn btn-primary me-2">
            <i className="bi bi-plus"></i> Post New Project
          </Link>
          <Link to="/client/my-projects" className="btn btn-outline-secondary">
            View My Projects
          </Link>
        </Col>
      </Row>
    </Container>
  );
};

export default ClientDashboard;