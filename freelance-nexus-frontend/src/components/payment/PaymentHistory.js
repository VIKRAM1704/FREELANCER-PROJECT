import React, { useState, useEffect } from 'react';
import { Container, Table, Badge } from 'react-bootstrap';
import { useAuth } from '../../context/AuthContext';
import paymentService from '../../services/paymentService';
import Loader from '../common/Loader';

const PaymentHistory = () => {
  const { user } = useAuth();
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPayments();
  }, []);

  const fetchPayments = async () => {
    try {
      const data = await paymentService.getPaymentsByUser(user.id);
      setPayments(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loader />;

  return (
    <Container className="mt-4">
      <h2>Payment History</h2>
      <Table striped bordered hover className="mt-4">
        <thead>
          <tr>
            <th>Date</th>
            <th>Project</th>
            <th>Amount</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {payments.map(p => (
            <tr key={p.id}>
              <td>{new Date(p.createdAt).toLocaleDateString()}</td>
              <td>{p.projectTitle}</td>
              <td>₹{p.amount}</td>
              <td>
                <Badge bg={p.status === 'COMPLETED' ? 'success' : 'warning'}>
                  {p.status}
                </Badge>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
};

export default PaymentHistory;