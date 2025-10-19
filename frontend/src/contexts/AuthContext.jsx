/**
 * Authentication Context
 * Provides authentication state and methods throughout the application
 */

import { createContext, useContext, useReducer, useEffect } from 'react';
import { authService } from '../services/authService';

// Initial state
const initialState = {
  user: null,
  isAuthenticated: false,
  isLoading: true,
  error: null,
  requiresEmailVerification: false
};

// Action types
const AUTH_ACTIONS = {
  LOGIN_START: 'LOGIN_START',
  LOGIN_SUCCESS: 'LOGIN_SUCCESS',
  LOGIN_FAILURE: 'LOGIN_FAILURE',
  LOGOUT: 'LOGOUT',
  SET_LOADING: 'SET_LOADING',
  CLEAR_ERROR: 'CLEAR_ERROR',
  UPDATE_USER: 'UPDATE_USER'
};

// Reducer function
function authReducer(state, action) {
  switch (action.type) {
    case AUTH_ACTIONS.LOGIN_START:
      return {
        ...state,
        isLoading: true,
        error: null
      };
    
    case AUTH_ACTIONS.LOGIN_SUCCESS:
      return {
        ...state,
        user: action.payload.user,
        isAuthenticated: true,
        isLoading: false,
        error: null,
        requiresEmailVerification: action.payload.requiresEmailVerification || false
      };
    
    case AUTH_ACTIONS.LOGIN_FAILURE:
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: action.payload.error
      };
    
    case AUTH_ACTIONS.LOGOUT:
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isLoading: false,
        error: null,
        requiresEmailVerification: false
      };
    
    case AUTH_ACTIONS.SET_LOADING:
      return {
        ...state,
        isLoading: action.payload
      };
    
    case AUTH_ACTIONS.CLEAR_ERROR:
      return {
        ...state,
        error: null
      };
    
    case AUTH_ACTIONS.UPDATE_USER:
      return {
        ...state,
        user: { ...state.user, ...action.payload }
      };
    
    default:
      return state;
  }
}

// Create context
const AuthContext = createContext();

// Auth provider component
export function AuthProvider({ children }) {
  const [state, dispatch] = useReducer(authReducer, initialState);

  // Initialize auth state on mount
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        console.log('🔄 Initializing auth...');
        
        const existingToken = authService.getToken();
        const user = authService.getUser();
        
        if (existingToken && user) {
          console.log('✅ Found existing token and user, verifying...');
          
          // Verify existing token by fetching profile
          const profileResult = await authService.getProfile();
          if (profileResult.success) {
            console.log('✅ Existing token is valid, user authenticated');
            dispatch({
              type: AUTH_ACTIONS.LOGIN_SUCCESS,
              payload: { user: profileResult.data }
            });
            return;
          } else {
            console.log('❌ Existing token invalid, will try refresh...');
          }
        } else {
          console.log('❌ No existing token or user data found');
        }

        // Try refresh token only if:
        // 1. We had a token but it was invalid (existingToken exists but profile failed)
        // 2. We had user data but no token (user exists but no token - possible after reload)
        if (existingToken || user) {
          console.log('🔄 Attempting to refresh token using HTTP-Only cookie...');
          const refreshResult = await authService.refreshToken();
          
          if (refreshResult.success) {
            console.log('✅ Token refresh successful, fetching profile...');
            
            const profileResult = await authService.getProfile();
            console.log('👤 Profile result:', profileResult);
            
            if (profileResult.success) {
              console.log('✅ Profile fetched successfully, user authenticated');
              dispatch({
                type: AUTH_ACTIONS.LOGIN_SUCCESS,
                payload: { user: profileResult.data }
              });
            } else {
              console.log('❌ Profile fetch failed, clearing auth');
              authService.clearAuth();
              dispatch({ type: AUTH_ACTIONS.LOGOUT });
            }
          } else {
            console.log('❌ Token refresh failed, no valid session');
            authService.clearAuth();
            dispatch({ type: AUTH_ACTIONS.SET_LOADING, payload: false });
          }
        } else {
          console.log('❌ No existing session found, user not authenticated');
          dispatch({ type: AUTH_ACTIONS.SET_LOADING, payload: false });
        }
      } catch (error) {
        console.error('❌ Auth initialization error:', error);
        authService.clearAuth();
        dispatch({ type: AUTH_ACTIONS.SET_LOADING, payload: false });
      }
    };

    initializeAuth();
  }, []);

  // Login function
  const login = async (credentials) => {
    dispatch({ type: AUTH_ACTIONS.LOGIN_START });
    
    try {
      const result = await authService.login(credentials);
      
      if (result.success) {
        console.log('Login successful, user data:', result.data.user);
        dispatch({
          type: AUTH_ACTIONS.LOGIN_SUCCESS,
          payload: { 
            user: result.data.user,
            requiresEmailVerification: result.data.requiresEmailVerification
          }
        });
        return { 
          success: true,
          requiresEmailVerification: result.data.requiresEmailVerification
        };
      } else {
        dispatch({
          type: AUTH_ACTIONS.LOGIN_FAILURE,
          payload: { error: result.error }
        });
        return { success: false, error: result.error };
      }
    } catch (error) {
      const errorMessage = 'An unexpected error occurred during login';
      dispatch({
        type: AUTH_ACTIONS.LOGIN_FAILURE,
        payload: { error: errorMessage }
      });
      return { success: false, error: errorMessage };
    }
  };

  // Register function
  const register = async (userData) => {
    dispatch({ type: AUTH_ACTIONS.LOGIN_START });
    
    try {
      const result = await authService.register(userData);
      
      if (result.success) {
        // Auto-login after successful registration
        return await login({
          email: userData.email,
          password: userData.password
        });
      } else {
        dispatch({
          type: AUTH_ACTIONS.LOGIN_FAILURE,
          payload: { error: result.error }
        });
        return { success: false, error: result.error };
      }
    } catch (error) {
      const errorMessage = 'An unexpected error occurred during registration';
      dispatch({
        type: AUTH_ACTIONS.LOGIN_FAILURE,
        payload: { error: errorMessage }
      });
      return { success: false, error: errorMessage };
    }
  };

  // Logout function
  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      dispatch({ type: AUTH_ACTIONS.LOGOUT });
    }
  };

  // Update user profile
  const updateProfile = async (profileData) => {
    try {
      const result = await authService.updateProfile(profileData);
      
      if (result.success) {
        dispatch({
          type: AUTH_ACTIONS.UPDATE_USER,
          payload: result.data
        });
        return { success: true };
      } else {
        return { success: false, error: result.error };
      }
    } catch (error) {
      return { success: false, error: 'Failed to update profile' };
    }
  };

  // Clear error
  const clearError = () => {
    dispatch({ type: AUTH_ACTIONS.CLEAR_ERROR });
  };

  const value = {
    // State
    user: state.user,
    isAuthenticated: state.isAuthenticated,
    isLoading: state.isLoading,
    error: state.error,
    requiresEmailVerification: state.requiresEmailVerification,
    
    // Actions
    login,
    register,
    logout,
    updateProfile,
    clearError
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

// Custom hook to use auth context
export function useAuth() {
  const context = useContext(AuthContext);
  
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  
  return context;
}
