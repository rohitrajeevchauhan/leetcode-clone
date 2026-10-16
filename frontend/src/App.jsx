import {
    BrowserRouter,
    Routes,
    Route,
    Navigate,
} from 'react-router-dom'

import Login from './pages/Login.jsx'
import Dashboard from './pages/Dashboard.jsx'
import Problems from './pages/Problems.jsx'
import ProblemDetails from './pages/ProblemDetails.jsx'
import Submissions from './pages/Submissions.jsx'
import ProblemSubmissions from './pages/ProblemSubmissions.jsx'
import SubmissionDetails from './pages/SubmissionDetails.jsx'
import Profile from './pages/Profile.jsx'
import LearningHub from './pages/LearningHub.jsx'
import Navbar from './components/layout/Navbar.jsx'
import ProgressDashboard from './pages/ProgressDashboard.jsx'
import InterviewPrep from './pages/InterviewPrep.jsx'
import Analytics from './pages/Analytics.jsx'
import GamificationCenter from './pages/GamificationCenter.jsx'
import CommunityCenter from './pages/CommunityCenter.jsx'
import NotificationsCenter from './pages/NotificationsCenter.jsx'

function ProtectedRoute({ children }) {
    const token = localStorage.getItem('token')

    return token ? children : <Navigate to="/login" replace />
}

function App() {
    return (
        <BrowserRouter>
            <Navbar />

            <Routes>
                {/* Public routes */}
                <Route path="/login" element={<Login />} />

                {/* Dashboard */}
                <Route
                    path="/"
                    element={<Navigate to="/dashboard" replace />}
                />

                <Route
                    path="/dashboard"
                    element={
                        <ProtectedRoute>
                            <Dashboard />
                        </ProtectedRoute>
                    }
                />

                {/* Problems */}
                <Route
                    path="/problems"
                    element={
                        <ProtectedRoute>
                            <Problems />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/problems/:id"
                    element={
                        <ProtectedRoute>
                            <ProblemDetails />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/problems/:problemId/submissions"
                    element={
                        <ProtectedRoute>
                            <ProblemSubmissions />
                        </ProtectedRoute>
                    }
                />

                {/* Submissions */}
                <Route
                    path="/submissions"
                    element={
                        <ProtectedRoute>
                            <Submissions />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/submissions/:id"
                    element={
                        <ProtectedRoute>
                            <SubmissionDetails />
                        </ProtectedRoute>
                    }
                />

                {/* User profile */}
                <Route
                    path="/profile"
                    element={
                        <ProtectedRoute>
                            <Profile />
                        </ProtectedRoute>
                    }
                />

                {/* Learning and preparation */}
                <Route
                    path="/progress"
                    element={
                        <ProtectedRoute>
                            <ProgressDashboard />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/learning-hub"
                    element={
                        <ProtectedRoute>
                            <LearningHub />
                        </ProtectedRoute>
                    }
                />

                <Route
                    path="/interview-prep"
                    element={
                        <ProtectedRoute>
                            <InterviewPrep />
                        </ProtectedRoute>
                    }
                />

                {/* Analytics */}
                <Route
                    path="/analytics"
                    element={
                        <ProtectedRoute>
                            <Analytics />
                        </ProtectedRoute>
                    }
                />

                {/* Contests and rewards */}
                <Route
                    path="/gamification"
                    element={
                        <ProtectedRoute>
                            <GamificationCenter />
                        </ProtectedRoute>
                    }
                />

                {/* Community */}
                <Route
                    path="/community"
                    element={
                        <ProtectedRoute>
                            <CommunityCenter />
                        </ProtectedRoute>
                    }
                />

                {/* Notifications */}
                <Route
                    path="/notifications"
                    element={
                        <ProtectedRoute>
                            <NotificationsCenter />
                        </ProtectedRoute>
                    }
                />

                {/* Unknown routes */}
                <Route
                    path="*"
                    element={<Navigate to="/dashboard" replace />}
                />
            </Routes>
        </BrowserRouter>
    )
}

export default App