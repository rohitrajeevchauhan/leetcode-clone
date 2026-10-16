
import { useEffect, useMemo, useState } from 'react'
import axios from 'axios'
import {
    Activity, CheckCircle2, Code2, RefreshCw,
    Target, TrendingUp, XCircle, Lightbulb
} from 'lucide-react'

const API = 'http://localhost:8080'

function getItems(data) {
    if (Array.isArray(data)) return data
    if (Array.isArray(data?.content)) return data.content
    if (Array.isArray(data?.data)) return data.data
    if (Array.isArray(data?.problems)) return data.problems
    return []
}

function getProblemId(item) {
    return item?.problemId ?? item?.id ?? item?.problem?.id
}

function getStatus(item) {
    return String(item?.status ?? 'UNKNOWN').toUpperCase()
}

function isAccepted(item) {
    return ['ACCEPTED', 'AC', 'SUCCESS', 'PASSED'].includes(getStatus(item))
}

function getDate(item) {
    const value = item?.createdAt ?? item?.submittedAt ?? item?.updatedAt
    if (!value) return null
    const date = new Date(value)
    return Number.isNaN(date.getTime()) ? null : date
}

function formatDate(date) {
    return date.toLocaleDateString('en-IN', {
        day: 'numeric',
        month: 'short',
    })
}

function Metric({ label, value, detail, icon: Icon }) {
    return (
        <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
            <div className="flex items-center justify-between gap-3">
                <p className="text-sm text-slate-400">{label}</p>
                <Icon size={20} className="text-indigo-400" />
            </div>
            <p className="mt-4 text-3xl font-bold text-white">{value}</p>
            <p className="mt-2 text-xs text-slate-500">{detail}</p>
        </article>
    )
}

function Bar({ label, value, max, color = 'bg-indigo-500' }) {
    const width = max > 0 ? Math.max((value / max) * 100, value > 0 ? 3 : 0) : 0

    return (
        <div className="space-y-2">
            <div className="flex items-center justify-between gap-3 text-sm">
                <span className="text-slate-300">{label}</span>
                <span className="font-semibold text-white">{value}</span>
            </div>
            <div className="h-2.5 overflow-hidden rounded-full bg-slate-800">
                <div
                    className={`h-full rounded-full transition-all ${color}`}
                    style={{ width: `${width}%` }}
                />
            </div>
        </div>
    )
}

export default function Analytics() {
    const [submissions, setSubmissions] = useState([])
    const [problems, setProblems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(1)

    async function loadAnalytics(nextPage = 0) {
        const token = localStorage.getItem('token')

        if (!token) {
            setError('Please log in to view your analytics.')
            setLoading(false)
            return
        }

        setLoading(true)
        setError('')

        const headers = { Authorization: `Bearer ${token}` }

        try {
            const [submissionResult, problemResult] = await Promise.all([
                axios.get(`${API}/api/submissions`, {
                    headers,
                    params: {
                        page: nextPage,
                        size: 100,
                        sort: 'createdAt,desc',
                    },
                }),
                axios.get(`${API}/api/problems`, { headers }),
            ])

            const submissionData = submissionResult.data
            const fetchedSubmissions = getItems(submissionData)

            setSubmissions(fetchedSubmissions)
            setTotalPages(
                typeof submissionData?.totalPages === 'number'
                    ? Math.max(submissionData.totalPages, 1)
                    : 1
            )
            setPage(nextPage)
            setProblems(getItems(problemResult.data))
        } catch (err) {
            setError(
                err.response?.status === 401
                    ? 'Your session may have expired. Please log in again.'
                    : err.response?.status === 403
                        ? 'You do not have permission to view this data.'
                        : 'Unable to load analytics. Check that your backend is running.'
            )
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        loadAnalytics(0)
    }, [])

    const stats = useMemo(() => {
        const accepted = submissions.filter(isAccepted)
        const acceptanceRate = submissions.length
            ? Math.round((accepted.length / submissions.length) * 100)
            : 0

        const languageCounts = {}
        submissions.forEach(item => {
            const language = String(item.language || 'Unknown').toUpperCase()
            languageCounts[language] = (languageCounts[language] || 0) + 1
        })

        const difficultyById = new Map(
            problems.map(problem => [
                String(problem.id ?? problem.problemId),
                String(problem.difficulty ?? 'Unknown').toUpperCase(),
            ])
        )

        const solvedIds = new Set(
            accepted
                .map(getProblemId)
                .filter(id => id != null)
                .map(String)
        )

        const difficultyCounts = { EASY: 0, MEDIUM: 0, HARD: 0, UNKNOWN: 0 }

        solvedIds.forEach(id => {
            const difficulty = difficultyById.get(id) || 'UNKNOWN'
            if (difficultyCounts[difficulty] !== undefined) {
                difficultyCounts[difficulty]++
            } else {
                difficultyCounts.UNKNOWN++
            }
        })

        const now = new Date()
        const activity = Array.from({ length: 7 }, (_, index) => {
            const day = new Date(now)
            day.setHours(0, 0, 0, 0)
            day.setDate(now.getDate() - (6 - index))

            const count = submissions.filter(item => {
                const date = getDate(item)
                return date && date.toDateString() === day.toDateString()
            }).length

            return {
                label: day.toLocaleDateString('en-IN', { weekday: 'short' }),
                date: formatDate(day),
                count,
            }
        })

        const languageEntries = Object.entries(languageCounts)
            .sort((a, b) => b[1] - a[1])

        const totalProblems = problems.length
        const knownSolved = solvedIds.size
        const recentFailures = submissions.filter(item =>
            !isAccepted(item) && getStatus(item) !== 'UNKNOWN'
        ).length

        return {
            acceptedCount: accepted.length,
            totalSubmissions: submissions.length,
            acceptanceRate,
            languageEntries,
            difficultyCounts,
            activity,
            totalProblems,
            knownSolved,
            recentFailures,
        }
    }, [submissions, problems])

    const maxActivity = Math.max(
        ...stats.activity.map(day => day.count),
        1
    )

    const suggestions = useMemo(() => {
        const result = []

        if (stats.totalSubmissions === 0) {
            result.push('Submit your first solution to start building your coding history.')
        } else {
            if (stats.acceptanceRate < 50) {
                result.push('Review failed submissions and test edge cases before submitting again.')
            } else {
                result.push('Keep your acceptance rate healthy by reviewing mistakes after each attempt.')
            }

            if (stats.difficultyCounts.EASY < 5) {
                result.push('Build consistency with Easy problems before increasing difficulty.')
            }

            if (stats.difficultyCounts.MEDIUM < 5) {
                result.push('Include Medium problems in your weekly practice to strengthen interview readiness.')
            }

            if (stats.languageEntries.length === 0) {
                result.push('Submit a solution to begin tracking your language usage.')
            } else {
                result.push('Review your core language fundamentals and practise explaining your solution complexity.')
            }
        }

        return result.slice(0, 4)
    }, [stats])

    return (
        <main className="min-h-screen bg-slate-950 px-4 py-8 text-slate-100 sm:px-8">
            <div className="mx-auto max-w-7xl space-y-7">
                <header className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                    <div>
                        <p className="mb-2 text-sm font-semibold uppercase tracking-widest text-indigo-400">
                            LeetClone Insights
                        </p>
                        <h1 className="text-3xl font-bold sm:text-4xl">Performance Analytics</h1>
                        <p className="mt-2 text-slate-400">
                            Understand your submissions, activity and coding habits.
                        </p>
                    </div>

                    <button
                        onClick={() => loadAnalytics(0)}
                        disabled={loading}
                        className="flex items-center justify-center gap-2 rounded-xl border border-slate-700 px-4 py-3 text-sm hover:bg-slate-800 disabled:opacity-50"
                    >
                        <RefreshCw size={16} className={loading ? 'animate-spin' : ''} />
                        Refresh data
                    </button>
                </header>

                {error && (
                    <div className="rounded-xl border border-red-900 bg-red-950/30 p-4 text-sm text-red-300">
                        {error}
                        <button
                            className="ml-3 underline"
                            onClick={() => loadAnalytics(0)}
                        >
                            Try again
                        </button>
                    </div>
                )}

                {loading ? (
                    <div className="rounded-2xl border border-slate-800 bg-slate-900 p-12 text-center text-slate-400">
                        Loading your coding statistics...
                    </div>
                ) : (
                    <>
                        <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                            <Metric
                                label="Submissions loaded"
                                value={stats.totalSubmissions}
                                detail="Records on the current page"
                                icon={Code2}
                            />
                            <Metric
                                label="Accepted submissions"
                                value={stats.acceptedCount}
                                detail="Successful attempts in loaded records"
                                icon={CheckCircle2}
                            />
                            <Metric
                                label="Acceptance rate"
                                value={`${stats.acceptanceRate}%`}
                                detail="Accepted ÷ loaded submissions"
                                icon={TrendingUp}
                            />
                            <Metric
                                label="Unique solved problems"
                                value={stats.knownSolved}
                                detail="Distinct problems with accepted submissions"
                                icon={Target}
                            />
                        </section>

                        <section className="grid gap-5 lg:grid-cols-2">
                            <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
                                <div className="mb-5 flex items-center gap-3">
                                    <Activity className="text-indigo-400" />
                                    <div>
                                        <h2 className="text-lg font-semibold">Last 7 days</h2>
                                        <p className="text-sm text-slate-400">Submission activity by day</p>
                                    </div>
                                </div>

                                <div className="flex h-48 items-end justify-between gap-2 border-b border-slate-700 pb-2">
                                    {stats.activity.map(day => (
                                        <div key={day.date} className="flex h-full flex-1 flex-col items-center justify-end gap-2">
                                            <span className="text-xs text-slate-400">{day.count}</span>
                                            <div
                                                title={`${day.date}: ${day.count} submissions`}
                                                className="w-full max-w-10 rounded-t-md bg-indigo-500 transition-all"
                                                style={{
                                                    height: `${Math.max((day.count / maxActivity) * 70, day.count ? 6 : 2)}%`,
                                                    opacity: day.count ? 1 : 0.25,
                                                }}
                                            />
                                            <span className="text-xs text-slate-400">{day.label}</span>
                                        </div>
                                    ))}
                                </div>
                                <p className="mt-4 text-xs text-slate-500">
                                    Activity is based on the submission records loaded from the backend.
                                </p>
                            </article>

                            <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
                                <div className="mb-5 flex items-center gap-3">
                                    <Code2 className="text-indigo-400" />
                                    <div>
                                        <h2 className="text-lg font-semibold">Language usage</h2>
                                        <p className="text-sm text-slate-400">Submissions grouped by language</p>
                                    </div>
                                </div>

                                {stats.languageEntries.length ? (
                                    <div className="space-y-5">
                                        {stats.languageEntries.map(([language, count]) => (
                                            <Bar
                                                key={language}
                                                label={language}
                                                value={count}
                                                max={stats.languageEntries[0][1]}
                                            />
                                        ))}
                                    </div>
                                ) : (
                                    <p className="py-10 text-center text-slate-500">
                                        Submit a solution to see language statistics.
                                    </p>
                                )}
                            </article>
                        </section>

                        <section className="grid gap-5 lg:grid-cols-2">
                            <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
                                <h2 className="mb-1 text-lg font-semibold">Difficulty breakdown</h2>
                                <p className="mb-5 text-sm text-slate-400">Unique accepted problems, grouped by difficulty</p>
                                <div className="space-y-5">
                                    <Bar label="Easy" value={stats.difficultyCounts.EASY} max={Math.max(...Object.values(stats.difficultyCounts), 1)} color="bg-emerald-500" />
                                    <Bar label="Medium" value={stats.difficultyCounts.MEDIUM} max={Math.max(...Object.values(stats.difficultyCounts), 1)} color="bg-amber-500" />
                                    <Bar label="Hard" value={stats.difficultyCounts.HARD} max={Math.max(...Object.values(stats.difficultyCounts), 1)} color="bg-red-500" />
                                    {stats.difficultyCounts.UNKNOWN > 0 && (
                                        <Bar label="Difficulty unavailable" value={stats.difficultyCounts.UNKNOWN} max={Math.max(...Object.values(stats.difficultyCounts), 1)} color="bg-slate-500" />
                                    )}
                                </div>
                                <p className="mt-4 text-xs text-slate-500">
                                    Only problems with matching difficulty information can be classified accurately.
                                </p>
                            </article>

                            <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
                                <div className="mb-4 flex items-center gap-3">
                                    <Lightbulb className="text-amber-400" />
                                    <h2 className="text-lg font-semibold">Your next steps</h2>
                                </div>
                                <div className="space-y-3">
                                    {suggestions.map((suggestion, index) => (
                                        <div key={index} className="flex items-start gap-3 rounded-xl border border-slate-800 bg-slate-950 p-4">
                                            <CheckCircle2 size={18} className="mt-0.5 shrink-0 text-indigo-400" />
                                            <p className="text-sm leading-6 text-slate-300">{suggestion}</p>
                                        </div>
                                    ))}
                                </div>
                                <div className="mt-5 rounded-xl bg-slate-950 p-4">
                                    <div className="flex items-center gap-2 text-sm text-slate-400">
                                        <XCircle size={16} />
                                        Non-accepted submissions
                                    </div>
                                    <p className="mt-2 text-2xl font-bold">{stats.recentFailures}</p>
                                    <p className="mt-1 text-xs text-slate-500">
                                        Review these attempts in your Submissions page to find mistakes.
                                    </p>
                                </div>
                            </article>
                        </section>

                        <section className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
                            <h2 className="text-lg font-semibold">Problem library overview</h2>
                            <p className="mt-1 text-sm text-slate-400">Problems available in your current problem catalogue</p>
                            <div className="mt-4 flex flex-wrap items-center gap-6">
                                <div>
                                    <p className="text-3xl font-bold">{stats.totalProblems}</p>
                                    <p className="text-sm text-slate-400">Available problems</p>
                                </div>
                                <div>
                                    <p className="text-3xl font-bold">{stats.knownSolved}</p>
                                    <p className="text-sm text-slate-400">Distinct accepted problems</p>
                                </div>
                                <div>
                                    <p className="text-3xl font-bold">
                                        {Math.max(stats.totalProblems - stats.knownSolved, 0)}
                                    </p>
                                    <p className="text-sm text-slate-400">Not yet matched to an accepted submission</p>
                                </div>
                            </div>
                        </section>

                        {totalPages > 1 && (
                            <div className="flex items-center justify-between rounded-xl border border-slate-800 p-4">
                <span className="text-sm text-slate-400">
                  Submission page {page + 1} of {totalPages}
                </span>
                                <div className="flex gap-2">
                                    <button
                                        disabled={page === 0 || loading}
                                        onClick={() => loadAnalytics(page - 1)}
                                        className="rounded-lg border border-slate-700 px-3 py-2 text-sm disabled:opacity-40"
                                    >
                                        Previous
                                    </button>
                                    <button
                                        disabled={page + 1 >= totalPages || loading}
                                        onClick={() => loadAnalytics(page + 1)}
                                        className="rounded-lg border border-slate-700 px-3 py-2 text-sm disabled:opacity-40"
                                    >
                                        Next
                                    </button>
                                </div>
                            </div>
                        )}
                    </>
                )}
            </div>
        </main>
    )
}
