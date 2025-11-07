import React, { createContext, useState, useEffect, useContext } from 'react';
import Keycloak from 'keycloak-js';
import { KEYCLOAK_URL, KEYCLOAK_REALM, KEYCLOAK_CLIENT_ID } from '../utils/constants';
import authService from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [keycloak, setKeycloak] = useState(null);
  const [authenticated, setAuthenticated] = useState(false);

  useEffect(() => {
    initKeycloak();
  }, []);

  const initKeycloak = async () => {
    try {
      const kc = new Keycloak({
        url: KEYCLOAK_URL,
        realm: KEYCLOAK_REALM,
        clientId: KEYCLOAK_CLIENT_ID
      });

      const auth = await kc.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256'
      });

      setKeycloak(kc);
      setAuthenticated(auth);

      if (auth) {
        localStorage.setItem('token', kc.token);
        const userProfile = await kc.loadUserProfile();
        const userData = {
          id: kc.tokenParsed.sub,
          username: kc.tokenParsed.preferred_username,
          email: userProfile.email,
          firstName: userProfile.firstName,
          lastName: userProfile.lastName,
          roles: kc.tokenParsed.realm_access?.roles || []
        };
        setUser(userData);
        localStorage.setItem('user', JSON.stringify(userData));
      }

      setLoading(false);

      // Token refresh
      setInterval(() => {
        kc.updateToken(70).then((refreshed) => {
          if (refreshed) {
            localStorage.setItem('token', kc.token);
          }
        }).catch(() => {
          console.error('Failed to refresh token');
        });
      }, 60000);
    } catch (error) {
      console.error('Keycloak initialization failed:', error);
      setLoading(false);
    }
  };

  const login = async () => {
    if (keycloak) {
      await keycloak.login();
    }
  };

  const logout = async () => {
    if (keycloak) {
      await keycloak.logout();
      setUser(null);
      setAuthenticated(false);
      localStorage.removeItem('token');
      localStorage.removeItem('user');
    }
  };

  const hasRole = (role) => {
    return user?.roles?.includes(role) || false;
  };

  const value = {
    user,
    loading,
    authenticated,
    keycloak,
    login,
    logout,
    hasRole
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};