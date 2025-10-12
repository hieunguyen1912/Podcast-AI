import { lazy } from 'react';

// Lazy load pages
const Home = lazy(() => import('../pages/HomePage'));
const Login = lazy(() => import('../pages/LoginPage'));
const Register = lazy(() => import('../pages/RegisterPage'));
const Dashboard = lazy(() => import('../pages/DashboardPage'));
const Podcast = lazy(() => import('../pages/PodcastPage'));
const NotFound = lazy(() => import('../pages/NotFoundPage'));

export const routes = [
  {
    path: '/',
    element: Home,
    public: true
  },
  {
    path: '/login',
    element: Login,
    public: true
  },
  {
    path: '/register',
    element: Register,
    public: true
  },
  {
    path: '/dashboard',
    element: Dashboard,
    protected: true
  },
  {
    path: '/me',
    element: Dashboard,
    protected: true
  },
  {
    path: '/podcast/:id',
    element: Podcast,
    protected: true
  },
  {
    path: '*',
    element: NotFound,
    public: true
  }
];