// utils/constants.js
// src/utils/constants.js
export const API_GATEWAY_URL = process.env.REACT_APP_API_GATEWAY_URL || 'http://localhost:8765';

// For debugging - you can temporarily add this to see what's being loaded
console.log('API Gateway URL:', API_GATEWAY_URL);

export const ROLES = {
  ADMIN: 'ADMIN',
  CLIENT: 'CLIENT',
  FREELANCER: 'FREELANCER'
};

export const PROJECT_STATUS = {
  OPEN: 'OPEN',
  IN_PROGRESS: 'IN_PROGRESS',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED'
};

export const PROPOSAL_STATUS = {
  PENDING: 'PENDING',
  ACCEPTED: 'ACCEPTED',
  REJECTED: 'REJECTED',
  WITHDRAWN: 'WITHDRAWN'
};

export const PAYMENT_STATUS = {
  PENDING: 'PENDING',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
  REFUNDED: 'REFUNDED'
};