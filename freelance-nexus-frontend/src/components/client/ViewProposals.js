import React, { useState, useEffect } from 'react';
import { Container, Card, Button, ListGroup, Badge } from 'react-bootstrap';
import { useParams } from 'react-router-dom';
import proposalService from '../../services/proposalService';
import Loader from '../common/Loader';
import { toast } from 'react-toastify';

const ViewProposals = () => {
  const { projectId } = useParams();
  const [proposals, setProposals] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProposals();
  }, [projectId]);

  const fetchProposals = async () => {
    try {
      const data = await proposalService.getProposalsByProject(projectId);
      setProposals(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleAccept = async (proposalId) => {
    try {
      await proposalService.acceptProposal(proposalId);
      toast.success('Proposal accepted!');
      fetchProposals();
    } catch (error) {
      toast.error('Failed to accept proposal');
    }
  };

  if (loading) return <Loader />;

  return (
    <Container className="mt-4">
      <h2>Project Proposals</h2>
      <Card className="mt-4">
        <ListGroup variant="flush">
          {proposals.map(p => (
            <ListGroup.Item key={p.id}>
              <div className="d-flex justify-content-between">
                <div>
                  <h5>{p.freelancerName}</h5>
                  <p>{p.coverLetter}</p>
                  <strong>Amount: ₹{p.proposedAmount}</strong>
                </div>
                <div>
                  <Badge bg={p.status === 'PENDING' ? 'warning' : 'success'}>{p.status}</Badge>
                  {p.status === 'PENDING' && (
                    <Button size="sm" onClick={() => handleAccept(p.id)} className="ms-2">
                      Accept
                    </Button>
                  )}
                </div>
              </div>
            </ListGroup.Item>
          ))}
        </ListGroup>
      </Card>
    </Container>
  );
};

export default ViewProposals;