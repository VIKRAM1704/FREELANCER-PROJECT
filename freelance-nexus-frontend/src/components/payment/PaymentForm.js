import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import proposalService from '../../services/proposalService';
import paymentService from '../../services/paymentService';
import Loader from '../common/Loader';

const PaymentForm = () => {
  const { proposalId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [proposal, setProposal] = useState(null);
  const [paymentData, setPaymentData] = useState({
    upiId: '',
    amount: '',
    description: ''
  });
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProposal = async () => {
      try {
        const data = await proposalService.getProposalById(proposalId);
        setProposal(data);
        setPaymentData(prev => ({
          ...prev,
          amount: data.bidAmount,
          description: `Payment for project: ${data.projectTitle}`
        }));
      } catch (error) {
        setError('Failed to load proposal details');
      } finally {
        setLoading(false);
      }
    };

    fetchProposal();
  }, [proposalId]);

  const handleChange = (e) => {
    setPaymentData({ ...paymentData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setProcessing(true);

    try {
      const payment = await paymentService.initiatePayment({
        proposalId: parseInt(proposalId),
        clientId: user.id,
        freelancerId: proposal.freelancerId,
        amount: parseFloat(paymentData.amount),
        paymentMethod: 'UPI',
        description: paymentData.description
      });

      // Process UPI payment
      await paymentService.processUPIPayment(payment.id, {
        upiId: paymentData.upiId,
        amount: paymentData.amount
      });

      navigate('/payment-history', { 
        state: { message: 'Payment initiated successfully!' } 
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Payment failed. Please try again.');
    } finally {
      setProcessing(false);
    }
  };

  if (loading) return <Loader />;

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-lg-8 mx-auto">
          <h2 className="mb-4">Make Payment</h2>

          {error && (
            <div className="alert alert-danger">{error}</div>
          )}

          {/* Proposal Details */}
          <div className="card shadow-sm mb-4">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">Payment Details</h5>
            </div>
            <div className="card-body">
              <div className="row mb-3">
                <div className="col-sm-6">
                  <strong>Project:</strong>
                  <div>{proposal?.projectTitle}</div>
                </div>
                <div className="col-sm-6">
                  <strong>Freelancer:</strong>
                  <div>{proposal?.freelancerName}</div>
                </div>
              </div>
              <div className="row">
                <div className="col-sm-6">
                  <strong>Agreed Amount:</strong>
                  <div className="text-success fs-4">₹{proposal?.bidAmount}</div>
                </div>
                <div className="col-sm-6">
                  <strong>Duration:</strong>
                  <div>{proposal?.duration} days</div>
                </div>
              </div>
            </div>
          </div>

          {/* Payment Form */}
          <form onSubmit={handleSubmit}>
            <div className="card shadow-sm mb-4">
              <div className="card-header bg-success text-white">
                <h5 className="mb-0">💳 UPI Payment</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <label className="form-label">UPI ID *</label>
                  <input
                    type="text"
                    className="form-control"
                    name="upiId"
                    value={paymentData.upiId}
                    onChange={handleChange}
                    placeholder="username@upi"
                    required
                  />
                  <small className="text-muted">
                    Enter your UPI ID (e.g., yourname@paytm, yourname@ybl)
                  </small>
                </div>

                <div className="mb-3">
                  <label className="form-label">Amount (₹) *</label>
                  <input
                    type="number"
                    className="form-control"
                    name="amount"
                    value={paymentData.amount}
                    onChange={handleChange}
                    readOnly
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Description</label>
                  <textarea
                    className="form-control"
                    name="description"
                    rows="2"
                    value={paymentData.description}
                    onChange={handleChange}
                  />
                </div>

                <div className="alert alert-info">
                  <small>
                    <strong>Note:</strong> You will be redirected to your UPI app to complete the payment. 
                    The amount will be held in escrow until the project is completed.
                  </small>
                </div>
              </div>
            </div>

            <div className="d-grid gap-2 d-md-flex justify-content-md-end">
              <button 
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navigate(-1)}
              >
                Cancel
              </button>
              <button 
                type="submit" 
                className="btn btn-success"
                disabled={processing}
              >
                {processing ? 'Processing...' : `Pay ₹${paymentData.amount}`}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default PaymentForm;