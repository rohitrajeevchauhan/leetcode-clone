
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import {
    Activity,
    CheckCircle2,
    Target,
    TrendingUp,
    Code2,
    ArrowRight,
    RefreshCw,
    AlertCircle,
    Trophy
} from 'lucide-react'

const PAGE_SIZE = 100

function normalizeStatus(status) {
    const value = String(status || '').toUpperCase()

    if (['ACCEPTED', 'AC', 'SUCCESS'].includes(value)) {
        return 'ACCEPTED'
    }

    if (['PENDING', 'RUNNING', 'IN_PROGRESS'].includes(value)) {
        return 'PENDING'
    }

    if (['WRONG_ANSWER', 'WA'].includes(value)) {
        return 'WRONG_ANSWER'
    }

    if (['TIME_LIMIT_EXCEEDED', 'TLE'].includes(value)) {
        return 'TIME_LIMIT_EXCEEDED'
    }

    if (['COMPILATION_ERROR', 'CE'].includes(value)) {
        return 'COMPILATION_ERROR'
    }

    return value || 'UNKNOWN'
}

function formatDate(dateValue) {
    if (!dateValue) return 'Date unavailable'

    const date = new Date(dateValue)

    if (Number.isNaN(date.getTime())) {
        return String(dateValue)
    }

    return date.toLocaleString()
}

function StatCard({ title, value, subtitle, icon: Icon }) {
    return (
        <div className="rounded-2xl border border-slate-800 bg-[#151922] p-5">
            <div className="flex items-center justify-between gap-3">
                <p className="text-sm text-slate-400">{title}</p>
                <Icon className="text-orange-400" size={23} />
            </div>

            <p className="mt-4 text-3xl font-bold text-white">
                {value}
            </p>

            <p className="mt-2 text-xs text-slate-500">
                {subtitle}
            </p>
        </div>
    )
}

function StatusBadge({ status }) {
    const normalized = normalizeStatus(status)

    let styles = 'bg-slate-800 text-slate-300'

    if (normalized === 'ACCEPTED') {
        styles = 'bg-green-500/10 text-green-400'
    } else if (normalized === 'PENDING') {
        styles = 'bg-yellow-500/10 text-yellow-400'
    } else {
        styles = 'bg-red-500/10 text-red-400'
    }

    return (
        <span className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold ${styles}`}>
            {normalized.replaceAll('_', ' ')}
        </span>
    )
}

function ProgressDashboard() {
    const [submissions, setSubmissions] = useState([])
    const [problems, setProblems] = useState([])
    const [totalSubmissionCount, setTotalSubmissionCount] = useState(0)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        let cancelled = false

        async function loadDashboard() {
            const token = localStorage.getItem('token')

            if (!token) {
                if (!cancelled) {
                    setError('Your session is missing. Please log in again.')
                    setLoading(false)
                }
                return
            }

            try {
                const headers = {
                    Authorization: `Bearer ${token}`
                }

                const [submissionResponse, problemResponse] =
                    await Promise.all([
                        axios.get('/api/submissions', {
                            headers,
                            params: {
                                page: 0,
                                size: PAGE_SIZE,
                                sort: 'createdAt,desc'
                            }
                        }),
                        axios.get('/api/problems', { headers })
                    ])

                if (cancelled) return

                const submissionData = submissionResponse.data
                const submissionList = Array.isArray(submissionData)
                    ? submissionData
                    : submissionData?.content || []

                const problemData = problemResponse.data
                const problemList = Array.isArray(problemData)
                    ? problemData
                    : problemData?.content || []

                setSubmissions(submissionList)
                setProblems(problemList)

                setTotalSubmissionCount(
                    Number.isFinite(submissionData?.totalElements)
                        ? submissionData.totalElements
                        : submissionList.length
                )

                setError('')
            } catch (err) {
                if (!cancelled) {
                    if (
                        err.response?.status === 401 ||
                        err.response?.status === 403
                    ) {
                        setError(
                            'Your session has expired or access was denied. Please log in again.'
                        )
                    } else {
                        setError(
                            err.response?.data?.message ||
                            'Could not load dashboard data. Check that the backend is running.'
                        )
                    }
                }
            } finally {
                if (!cancelled) {
                    setLoading(false)
                }
            }
        }

        loadDashboard()

        return () => {
            cancelled = true
        }
    }, [])

    const problemById = new Map(
        problems.map(problem => [
            String(problem.id ?? problem.problemId),
            problem
        ])
    )

    const acceptedSubmissions = submissions.filter(
        submission => normalizeStatus(submission.status) === 'ACCEPTED'
    )

    const acceptedProblemIds = new Set(
        acceptedSubmissions
            .map(submission => submission.problemId)
            .filter(id => id !== null && id !== undefined)
            .map(String)
    )

    const solvedProblems = acceptedProblemIds.size

    const acceptanceRate = totalSubmissionCount > 0
        ? Math.round(
            (acceptedSubmissions.length / submissions.length) * 100
        )
        : 0

    const difficultyCounts = {
        Easy: 0,
        Medium: 0,
        Hard: 0
    }

    acceptedProblemIds.forEach(id => {
        const problem = problemById.get(id)
        const difficulty = String(problem?.difficulty || '').toLowerCase()

        if (difficulty === 'easy') difficultyCounts.Easy++
        if (difficulty === 'medium') difficultyCounts.Medium++
        if (difficulty === 'hard') difficultyCounts.Hard++
    })

    const maxDifficultyCount = Math.max(
        difficultyCounts.Easy,
        difficultyCounts.Medium,
        difficultyCounts.Hard,
        1
    )

    if (loading) {
        return (
            <main className="min-h-screen bg-[#0b0f19] px-6 py-20 text-white">
                <div className="mx-auto max-w-7xl text-center">
                    <RefreshCw
                        className="mx-auto mb-4 animate-spin text-orange-400"
                        size={32}
                    />
                    <p className="text-slate-400">
                        Loading your coding progress...
                    </p>
                </div>
            </main>
        )
    }

    if (error) {
        return (
            <main className="min-h-screen bg-[#0b0f19] px-6 py-16 text-white">
                <div className="mx-auto max-w-2xl rounded-2xl border border-red-900 bg-red-950/30 p-6">
                    <AlertCircle className="mb-3 text-red-400" size={28} />
                    <h1 className="text-xl font-bold">
                        Dashboard unavailable
                    </h1>
                    <p className="mt-3 text-slate-300">{error}</p>

                    <button
                        type="button"
                        onClick={() => window.location.reload()}
                        className="mt-5 rounded-lg bg-orange-500 px-4 py-2 font-semibold hover:bg-orange-600"
                    >
                        Retry
                    </button>
                </div>
            </main>
        )
    }

    return (
        <main className="min-h-screen bg-[#0b0f19] px-4 py-10 text-white sm:px-6">
            <div className="mx-auto max-w-7xl">

                <div className="mb-8 flex flex-wrap items-center justify-between gap-4">
                    <div>
                        <p className="mb-2 text-sm font-semibold uppercase tracking-widest text-orange-400">
                            Your learning journey
                        </p>

                        <h1 className="text-3xl font-bold sm:text-4xl">
                            Coding Progress
                        </h1>

                        <p className="mt-2 text-slate-400">
                            Review your submissions and track your progress.
                        </p>
                    </div>

                    <Link
                        to="/problems"
                        className="inline-flex items-center gap-2 rounded-lg bg-orange-500 px-5 py-3 font-semibold transition hover:bg-orange-600"
                    >
                        <Code2 size={18} />
                        Practice Problems
                    </Link>
                </div>

                <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                    <StatCard
                        title="Problems Solved"
                        value={solvedProblems}
                        subtitle="Distinct problems accepted in loaded history"
                        icon={CheckCircle2}
                    />

                    <StatCard
                        title="Total Submissions"
                        value={totalSubmissionCount}
                        subtitle="Total recorded by the submissions API"
                        icon={Activity}
                    />

                    <StatCard
                        title="Accepted Attempts"
                        value={acceptedSubmissions.length}
                        subtitle={`From the latest ${submissions.length} loaded submissions`}
                        icon={Trophy}
                    />

                    <StatCard
                        title="Acceptance Rate"
                        value={`${acceptanceRate}%`}
                        subtitle="Accepted attempts among loaded submissions"
                        icon={TrendingUp}
                    />
                </div>

                <div className="mt-8 grid gap-6 lg:grid-cols-2">

                    <section className="rounded-2xl border border-slate-800 bg-[#151922] p-6">
                        <div className="mb-6 flex items-center gap-3">
                            <Target className="text-orange-400" size={23} />
                            <div>
                                <h2 className="text-xl font-bold">
                                    Difficulty Breakdown
                                </h2>
                                <p className="mt-1 text-sm text-slate-400">
                                    Accepted problems grouped by difficulty
                                </p>
                            </div>
                        </div>

                        {['Easy', 'Medium', 'Hard'].map(level => {
                            const count = difficultyCounts[level]
                            const percentage =
                                (count / maxDifficultyCount) * 100

                            const textColor = {
                                Easy: 'text-green-400',
                                Medium: 'text-yellow-400',
                                Hard: 'text-red-400'
                            }[level]

                            const barColor = {
                                Easy: 'bg-green-500',
                                Medium: 'bg-yellow-500',
                                Hard: 'bg-red-500'
                            }[level]

                            return (
                                <div key={level} className="mb-6 last:mb-0">
                                    <div className="mb-2 flex justify-between">
                                        <span className={`font-medium ${textColor}`}>
                                            {level}
                                        </span>
                                        <span className="text-slate-300">
                                            {count}
                                        </span>
                                    </div>

                                    <div className="h-2 overflow-hidden rounded-full bg-slate-800">
                                        <div
                                            className={`h-full rounded-full transition-all ${barColor}`}
                                            style={{ width: `${percentage}%` }}
                                        />
                                    </div>
                                </div>
                            )
                        })}

                        <p className="mt-5 text-xs text-slate-500">
                            Difficulty counts require each problem to have a
                            matching ID and difficulty in the problems API.
                        </p>
                    </section>

                    <section className="rounded-2xl border border-slate-800 bg-[#151922] p-6">
                        <div className="mb-6 flex items-center gap-3">
                            <TrendingUp className="text-orange-400" size={23} />
                            <div>
                                <h2 className="text-xl font-bold">
                                    Keep Improving
                                </h2>
                                <p className="mt-1 text-sm text-slate-400">
                                    Your next steps
                                </p>
                            </div>
                        </div>

                        <div className="space-y-4">
                            <Link
                                to="/problems"
                                className="flex items-center justify-between rounded-xl border border-slate-700 p-4 transition hover:border-orange-500"
                            >
                                <div>
                                    <p className="font-semibold">
                                        Solve a new problem
                                    </p>
                                    <p className="mt-1 text-sm text-slate-400">
                                        Build your problem-solving skills.
                                    </p>
                                </div>
                                <ArrowRight className="text-orange-400" size={20} />
                            </Link>

                            <Link
                                to="/submissions"
                                className="flex items-center justify-between rounded-xl border border-slate-700 p-4 transition hover:border-orange-500"
                            >
                                <div>
                                    <p className="font-semibold">
                                        Review submissions
                                    </p>
                                    <p className="mt-1 text-sm text-slate-400">
                                        Inspect previous coding attempts.
                                    </p>
                                </div>
                                <ArrowRight className="text-orange-400" size={20} />
                            </Link>

                            <Link
                                to="/profile"
                                className="flex items-center justify-between rounded-xl border border-slate-700 p-4 transition hover:border-orange-500"
                            >
                                <div>
                                    <p className="font-semibold">
                                        View your profile
                                    </p>
                                    <p className="mt-1 text-sm text-slate-400">
                                        Check your account information.
                                    </p>
                                </div>
                                <ArrowRight className="text-orange-400" size={20} />
                            </Link>
                        </div>
                    </section>
                </div>

                <section className="mt-8 overflow-hidden rounded-2xl border border-slate-800 bg-[#151922]">
                    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 p-6">
                        <div>
                            <h2 className="text-xl font-bold">
                                Recent Submissions
                            </h2>
                            <p className="mt-1 text-sm text-slate-400">
                                Latest {submissions.length} loaded attempts
                            </p>
                        </div>

                        <Link
                            to="/submissions"
                            className="text-sm font-semibold text-orange-400 hover:text-orange-300"
                        >
                            View all
                        </Link>
                    </div>

                    {submissions.length === 0 ? (
                        <div className="p-10 text-center">
                            <Code2 className="mx-auto mb-3 text-slate-500" size={32} />
                            <p className="font-semibold">
                                No submissions yet
                            </p>
                            <p className="mt-2 text-sm text-slate-400">
                                Submit your first solution to start tracking progress.
                            </p>
                            <Link
                                to="/problems"
                                className="mt-5 inline-flex rounded-lg bg-orange-500 px-4 py-2 font-semibold hover:bg-orange-600"
                            >
                                Browse Problems
                            </Link>
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full min-w-[650px] text-left text-sm">
                                <thead className="bg-[#0f131d] text-slate-400">
                                <tr>
                                    <th className="px-6 py-4 font-medium">Problem</th>
                                    <th className="px-6 py-4 font-medium">Status</th>
                                    <th className="px-6 py-4 font-medium">Language</th>
                                    <th className="px-6 py-4 font-medium">Submitted</th>
                                </tr>
                                </thead>

                                <tbody>
                                {submissions.slice(0, 10).map((submission, index) => {
                                    const problemId = submission.problemId
                                    const problem = problemById.get(String(problemId))
                                    const title =
                                        submission.problemTitle ||
                                        problem?.title ||
                                        `Problem #${problemId ?? 'Unknown'}`

                                    return (
                                        <tr
                                            key={submission.id ?? `${problemId}-${index}`}
                                            className="border-t border-slate-800 transition hover:bg-slate-800/30"
                                        >
                                            <td className="px-6 py-4">
                                                {problemId != null ? (
                                                    <Link
                                                        to={`/problems/${problemId}`}
                                                        className="font-medium text-white hover:text-orange-400"
                                                    >
                                                        {title}
                                                    </Link>
                                                ) : (
                                                    <span>{title}</span>
                                                )}
                                            </td>

                                            <td className="px-6 py-4">
                                                <StatusBadge status={submission.status} />
                                            </td>

                                            <td className="px-6 py-4 text-slate-300">
                                                {submission.language || 'Unknown'}
                                            </td>

                                            <td className="px-6 py-4 text-slate-400">
                                                {formatDate(
                                                    submission.createdAt ||
                                                    submission.updatedAt
                                                )}
                                            </td>
                                        </tr>
                                    )
                                })}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>

                <p className="mt-5 text-xs text-slate-500">
                    Statistics are calculated from the latest {submissions.length}
                    loaded submissions, except total submissions when the API
                    supplies totalElements. Solved counts may be incomplete if
                    older submissions fall outside this page.
                </p>
            </div>
        </main>
    )
}

export default ProgressDashboard
