import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import paymentService from '../../services/paymentService';
import Loader from '../common/Loader';

const PaymentHistory = () => {
  const { user } = useAuth();
  const [payments, setPayments] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPayments = async () => {
      try {
        const data = await paymentService.getPaymentHistory(user.id);
        setPayments(data);
      } catch (error) {
        console.error('Error fetching payments:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPayments();
  }, [user.id]);

  const filteredPayments = payments.filter(p => 
    filter === 'ALL' || p.status === filter
  );

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row mb-4">
        <div className="col">
          <h2>Payment History</h2>
          <p className="text-muted">View all your transactions</p>
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
              All ({payments.length})
            </button>
            <button 
              className={`btn ${filter === 'COMPLETED' ? 'btn-success' : 'btn-outline-success'}`}
              onClick={() => setFilter('COMPLETED')}
            >
              Completed ({payments.filter(p => p.status === 'COMPLETED').length})
            </button>
            <button 
              className={`btn ${filter === 'PENDING' ? 'btn-warning' : 'btn-outline-warning'}`}
              onClick={() => setFilter('PENDING')}
            >
              Pending ({payments.filter(p => p.status === 'PENDING').length})
            </button>
            <button 
              className={`btn ${filter === 'FAILED' ? 'btn-danger' : 'btn-outline-danger'}`}
              onClick={() => setFilter('FAILED')}
            >
              Failed ({payments.filter(p => p.status === 'FAILED').length})
            </button>
          </div>
        </div>
      </div>

      {/* Payments List */}
      {filteredPayments.length === 0 ? (
        <div className="alert alert-info">
          <h5>No payment history</h5>
          <p className="mb-0">You haven't made any payments yet.</p>
        </div>
      ) : (
        <div className="card shadow-sm">
          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover mb-0">
                <thead className="table-light">
                  <tr>
                    <th>Date</th>
                    <th>Project</th>
                    <th>
                      {user?.roles?.includes('CLIENT') ? 'Freelancer' : 'Client'}
                    </th>
                    <th>Amount</th>
                    <th>Method</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredPayments.map((payment) => (
                    <tr key={payment.id}>
                      <td>
                        <small>{new Date(payment.createdAt).toLocaleDateString()}</small>
                      </td>
                      <td>
                        <strong>{payment.projectTitle}</strong>
                        <br />
                        <small className="text-muted">{payment.description}</small>
                      </td>
                      <td>
                        {user?.roles?.includes('CLIENT') 
                          ? payment.freelancerName 
                          : payment.clientName}
                      </td>
                      <td>
                        <strong className="text-success">₹{payment.amount}</strong>
                      </td>
                      <td>
                        <span className="badge bg-info">{payment.paymentMethod}</span>
                      </td>
                      <td>
                        <span className={`badge bg-${
                          payment.status === 'COMPLETED' ? 'success' :
                          payment.status === 'PENDING' ? 'warning' :
                          payment.status === 'FAILED' ? 'danger' :
                          'secondary'
                        }`}>
                          {payment.status}
                        </span>
                      </td>
                      <td>
                        {payment.status === 'PENDING' && (
                          <button className="btn btn-sm btn-outline-primary">
                            Verify
                          </button>
                        )}
                        {payment.status === 'COMPLETED' && (
                          <button className="btn btn-sm btn-outline-secondary">
                            Receipt
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* Summary */}
      <div className="row mt-4">
        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-body text-center">
              <h6 className="text-muted">Total Transactions</h6>
              <h3>{payments.length}</h3>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-body text-center">
              <h6 className="text-muted">Total Amount</h6>
              <h3 className="text-success">
                ₹{payments.reduce((sum, p) => sum + p.amount, 0)}
              </h3>
            </div>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card shadow-sm">
            <div className="card-body text-center">
              <h6 className="text-muted">Success Rate</h6>
              <h3 className="text-info">
                {payments.length > 0 
                  ? Math.round((payments.filter(p => p.status === 'COMPLETED').length / payments.length) * 100)
                  : 0}%
              </h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaymentHistory;