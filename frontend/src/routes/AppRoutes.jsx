import { Routes, Route, Navigate } from 'react-router-dom'

import Login from '../pages/auth/Login'
import Register from '../pages/auth/Register'

import Dashboard from '../pages/dashboard/Dashboard'
import Problems from '../pages/problems/Problems'
import ProblemDetails from '../pages/problems/ProblemDetails'
import Submissions from '../pages/submissions/Submissions'
import Discussions from '../pages/discussion/Discussions'
import Community from '../pages/community/Community'
import StudyPlans from '../pages/studyplan/StudyPlans'
import DailyChallenge from '../pages/daily/DailyChallenge'
import Profile from '../pages/profile/Profile'

import ProtectedRoute from './ProtectedRoute'
import AdminRoute from './AdminRoute'

function AppRoutes() {

  return (
    <Routes>

      <Route
        path="/login"
        element={<Login />}
      />

      <Route
        path="/register"
        element={<Register />}
      />

      <Route
        path="/problems"
        element={<Problems />}
      />

      <Route
        path="/problems/:problemId"
        element={<ProblemDetails />}
      />

      <Route
        path="/daily-challenge"
        element={<DailyChallenge />}
      />

      <Route element={<ProtectedRoute />}>

        <Route
          path="/dashboard"
          element={<Dashboard />}
        />

        <Route
          path="/submissions"
          element={<Submissions />}
        />

        <Route
          path="/discussions"
          element={<Discussions />}
        />

        <Route
          path="/community"
          element={<Community />}
        />

        <Route
          path="/study-plans"
          element={<StudyPlans />}
        />

        <Route
          path="/profile"
          element={<Profile />}
        />

      </Route>

      <Route element={<AdminRoute />}>

        <Route
          path="/admin"
          element={<div>Admin Dashboard</div>}
        />

      </Route>

      <Route
        path="/"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />

      <Route
        path="*"
        element={
          <Navigate
            to="/dashboard"
            replace
          />
        }
      />

    </Routes>
  )
}

export default AppRoutes