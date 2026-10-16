
import { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import {
    Code2,
    CheckCircle2,
    Target,
    TrendingUp,
    ArrowRight,
    Clock3,
    Trophy,
    Flame,
    RefreshCw,
    Zap,
    Terminal,
    ChevronRight,
    Sparkles,
    BookOpen,
    Activity,
    CircleDot,
} from 'lucide-react'

const API = {
    problems: '/api/problems',
    submissions: '/api/submissions',
}

const ACCEPTED_STATUSES = ['ACCEPTED', 'SUCCESS']

function getProblemId(item) {
    return item?.problemId ?? item?.problem?.id ?? item?.problem?.problemId ?? null
}

function getProblemTitle(item, problemMap) {
    const directTitle =
        item?.problemTitle ??
        item?.problem?.title ??
        item?.title

    if (directTitle) return directTitle

    const id = getProblemId(item)
    return id != null ? problemMap.get(String(id))?.title || `Problem #${id}` : 'Problem'
}

function getDifficulty(item, problemMap) {
    const id = getProblemId(item)
    const problem = id != null ? problemMap.get(String(id)) : null

    return String(
        item?.difficulty ??
        item?.problemDifficulty ??
        item?.problem?.difficulty ??
        problem?.difficulty ??
        ''
    ).toUpperCase()
}

function getDate(item) {
    if (!item?.createdAt) return '—'

    const date = new Date(item.createdAt)
    if (Number.isNaN(date.getTime())) return '—'

    return date.toLocaleDateString(undefined, {
        day: 'numeric',
        month: 'short',
        year: 'numeric',
    })
}

function Dashboard() {
    const [problems, setProblems] = useState([])
    const [submissions, setSubmissions] = useState([])
    const [loading, setLoading] = useState(true)
    const [refreshing, setRefreshing] = useState(false)
    const [error, setError] = useState('')

    const loadDashboard = useCallback(async (isRefresh = false) => {
        if (isRefresh) {
            setRefreshing(true)
        } else {
            setLoading(true)
        }

        setError('')

        const token = localStorage.getItem('token')
        const config = {
            headers: token ? { Authorization: `Bearer ${token}` } : {},
        }

        try {
            const [problemsResponse, submissionsResponse] = await Promise.all([
                axios.get(API.problems, config),
                axios.get(
                    `${API.submissions}?page=0&size=10&sort=createdAt,desc`,
                    config
                ),
            ])

            const problemData = problemsResponse.data
            const submissionData = submissionsResponse.data

            setProblems(
                Array.isArray(problemData)
                    ? problemData
                    : problemData?.content ?? problemData?.data ?? []
            )

            setSubmissions(
                Array.isArray(submissionData)
                    ? submissionData
                    : submissionData?.content ?? submissionData?.data ?? []
            )
        } catch (err) {
            console.error('Dashboard loading error:', err)
            setError(
                err.response?.data?.message ||
                'Could not load dashboard data. Please try again.'
            )
        } finally {
            setLoading(false)
            setRefreshing(false)
        }
    }, [])

    useEffect(() => {
        loadDashboard()
    }, [loadDashboard])

    const problemMap = useMemo(
        () => new Map(problems.map(problem => [String(problem.id), problem])),
        [problems]
    )

    const acceptedSubmissions = useMemo(
        () => submissions.filter(submission =>
            ACCEPTED_STATUSES.includes(String(submission.status || '').toUpperCase())
        ),
        [submissions]
    )

    const solvedProblemIds = useMemo(
        () => new Set(
            acceptedSubmissions
                .map(getProblemId)
                .filter(id => id != null)
                .map(String)
        ),
        [acceptedSubmissions]
    )

    const solvedCount = solvedProblemIds.size
    const totalProblems = problems.length
    const progress = totalProblems
        ? Math.min(100, Math.round((solvedCount / totalProblems) * 100))
        : 0

    const difficultyCounts = useMemo(() => {
        const solved = { EASY: new Set(), MEDIUM: new Set(), HARD: new Set() }

        acceptedSubmissions.forEach(submission => {
            const id = getProblemId(submission)
            const difficulty = getDifficulty(submission, problemMap)

            if (id != null && solved[difficulty]) {
                solved[difficulty].add(String(id))
            }
        })

        // Also count solved problems from the problem list if the submission
        // endpoint only returns problem IDs and statuses.
        solvedProblemIds.forEach(id => {
            const problem = problemMap.get(id)
            const difficulty = String(problem?.difficulty || '').toUpperCase()

            if (solved[difficulty]) {
                solved[difficulty].add(id)
            }
        })

        return {
            easy: solved.EASY.size,
            medium: solved.MEDIUM.size,
            hard: solved.HARD.size,
        }
    }, [acceptedSubmissions, problemMap, solvedProblemIds])

    const username =
        localStorage.getItem('username') ||
        (() => {
            try {
                return JSON.parse(localStorage.getItem('user') || '{}').username
            } catch {
                return ''
            }
        })() ||
        'Developer'

    const firstName = username.trim().split(/\s+/)[0]

    if (loading) {
        return (
            <main className="dashboard-shell flex min-h-[80vh] items-center justify-center px-4 text-slate-300">
                <div className="text-center">
                    <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl border border-violet-400/20 bg-violet-500/10">
                        <RefreshCw size={25} className="animate-spin text-cyan-300" />
                    </div>
                    <p className="font-medium text-white">Preparing your workspace</p>
                    <p className="mt-2 text-sm text-slate-500">
                        Loading your coding activity...
                    </p>
                </div>
            </main>
        )
    }

    return (
        <main className="dashboard-shell min-h-screen px-4 py-7 text-white sm:px-6 lg:px-8 lg:py-10">
            <div className="mx-auto max-w-7xl">
                {/* Welcome header */}
                <section className="dashboard-hero relative mb-8 overflow-hidden rounded-3xl border border-white/[0.08] p-6 sm:p-8 lg:p-10">
                    <div className="dashboard-hero-glow" />

                    <div className="relative z-10 flex flex-col justify-between gap-7 lg:flex-row lg:items-center">
                        <div>
                            <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-cyan-300/20 bg-cyan-300/[0.06] px-3 py-1.5 text-xs font-medium text-cyan-200">
                                <Sparkles size={14} />
                                YOUR CODING WORKSPACE
                            </div>

                            <h1 className="text-3xl font-bold tracking-tight sm:text-4xl lg:text-5xl">
                                Welcome back,{' '}
                                <span className="dashboard-gradient-text">
                                    {firstName}
                                </span>
                                <span className="ml-2 inline-block">✦</span>
                            </h1>

                            <p className="mt-4 max-w-xl text-sm leading-6 text-slate-400 sm:text-base">
                                Every problem you solve makes you stronger.
                                Keep building momentum, one submission at a time.
                            </p>

                            <div className="mt-6 flex flex-wrap gap-3">
                                <Link
                                    to="/problems"
                                    className="dashboard-primary-button inline-flex items-center justify-center gap-2 rounded-xl px-5 py-3 text-sm font-semibold text-white"
                                >
                                    <Code2 size={18} />
                                    Start practicing
                                    <ArrowRight size={16} />
                                </Link>

                                <Link
                                    to="/progress"
                                    className="inline-flex items-center justify-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-5 py-3 text-sm font-semibold text-slate-200 transition hover:border-violet-300/30 hover:bg-violet-400/[0.07]"
                                >
                                    <Activity size={17} />
                                    View progress
                                </Link>
                            </div>
                        </div>

                        <div className="hidden shrink-0 items-center justify-center lg:flex">
                            <div className="dashboard-code-art relative flex h-40 w-40 items-center justify-center rounded-[2rem] border border-violet-300/20 bg-violet-500/[0.08]">
                                <div className="absolute inset-3 rounded-[1.5rem] border border-cyan-300/10" />
                                <Terminal size={66} strokeWidth={1.2} className="text-cyan-200" />
                                <span className="absolute -right-2 -top-2 flex h-10 w-10 items-center justify-center rounded-xl border border-violet-300/20 bg-[#17152e] text-violet-200 shadow-xl">
                                    <Zap size={19} />
                                </span>
                            </div>
                        </div>
                    </div>
                </section>

                {error && (
                    <div
                        role="alert"
                        className="mb-7 flex flex-col gap-3 rounded-2xl border border-rose-400/20 bg-rose-400/[0.06] p-4 text-sm text-rose-200 sm:flex-row sm:items-center sm:justify-between"
                    >
                        <p>{error}</p>
                        <button
                            onClick={() => loadDashboard(true)}
                            disabled={refreshing}
                            className="inline-flex shrink-0 items-center justify-center gap-2 rounded-lg border border-rose-300/20 px-3 py-2 font-medium hover:bg-rose-300/10 disabled:opacity-60"
                        >
                            <RefreshCw size={15} className={refreshing ? 'animate-spin' : ''} />
                            Retry
                        </button>
                    </div>
                )}

                {/* Stat cards */}
                <section className="mb-8 grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4">
                    <StatCard
                        title="Available problems"
                        value={totalProblems}
                        description="Ready for your next attempt"
                        icon={<Code2 size={21} />}
                        accent="cyan"
                        footer="Problem library"
                    />
                    <StatCard
                        title="Problems solved"
                        value={solvedCount}
                        description="Unique accepted problems"
                        icon={<CheckCircle2 size={21} />}
                        accent="green"
                        footer={`${progress}% overall completion`}
                    />
                    <StatCard
                        title="Recent submissions"
                        value={submissions.length}
                        description="Records in the latest API response"
                        icon={<Target size={21} />}
                        accent="purple"
                        footer="Latest activity"
                    />
                    <StatCard
                        title="Completion rate"
                        value={`${progress}%`}
                        description="Solved compared with available"
                        icon={<TrendingUp size={21} />}
                        accent="blue"
                        footer={progress === 100 && totalProblems > 0 ? 'All problems solved!' : 'Keep your momentum'}
                    />
                </section>

                {/* Progress and difficulty */}
                <section className="mb-8 grid grid-cols-1 gap-5 lg:grid-cols-5">
                    <div className="dashboard-panel lg:col-span-3">
                        <PanelHeading
                            icon={<Trophy size={20} />}
                            title="Problem progress"
                            subtitle="Your progress across the problem library"
                            accent="text-violet-300"
                        />

                        <div className="mt-7 flex items-end justify-between gap-4">
                            <div>
                                <p className="text-4xl font-bold tracking-tight text-white">
                                    {solvedCount}
                                    <span className="ml-2 text-lg font-medium text-slate-500">
                                        / {totalProblems}
                                    </span>
                                </p>
                                <p className="mt-2 text-sm text-slate-400">
                                    Unique problems solved
                                </p>
                            </div>

                            <div className="rounded-2xl border border-violet-300/15 bg-violet-300/[0.06] px-4 py-3 text-right">
                                <p className="text-2xl font-bold text-violet-200">
                                    {progress}%
                                </p>
                                <p className="text-xs text-slate-500">complete</p>
                            </div>
                        </div>

                        <div className="mt-6 h-3 overflow-hidden rounded-full bg-[#080d18]">
                            <div
                                className="dashboard-progress-bar h-full rounded-full transition-all duration-700"
                                style={{ width: `${progress}%` }}
                            />
                        </div>

                        <div className="mt-3 flex items-center justify-between text-xs text-slate-500">
                            <span>Keep solving to level up</span>
                            <span>{Math.max(0, totalProblems - solvedCount)} remaining</span>
                        </div>

                        <div className="mt-7 grid grid-cols-3 gap-3">
                            <MiniMetric label="Easy" value={difficultyCounts.easy} color="text-emerald-300" />
                            <MiniMetric label="Medium" value={difficultyCounts.medium} color="text-amber-300" />
                            <MiniMetric label="Hard" value={difficultyCounts.hard} color="text-rose-300" />
                        </div>
                    </div>

                    <div className="dashboard-panel lg:col-span-2">
                        <PanelHeading
                            icon={<Flame size={20} />}
                            title="Difficulty breakdown"
                            subtitle="Solved problems by difficulty"
                            accent="text-cyan-300"
                        />

                        <div className="mt-7 space-y-6">
                            <DifficultyRow
                                label="Easy"
                                count={difficultyCounts.easy}
                                total={solvedCount}
                                color="bg-emerald-400"
                                textColor="text-emerald-300"
                            />
                            <DifficultyRow
                                label="Medium"
                                count={difficultyCounts.medium}
                                total={solvedCount}
                                color="bg-amber-400"
                                textColor="text-amber-300"
                            />
                            <DifficultyRow
                                label="Hard"
                                count={difficultyCounts.hard}
                                total={solvedCount}
                                color="bg-rose-400"
                                textColor="text-rose-300"
                            />
                        </div>

                        <div className="mt-7 rounded-xl border border-white/[0.06] bg-white/[0.025] p-3 text-xs leading-5 text-slate-500">
                            Difficulty totals are based on the problem or submission fields returned by your API. Missing difficulty data may not be counted.
                        </div>
                    </div>
                </section>

                {/* Recent submissions */}
                <section className="dashboard-panel mb-8">
                    <div className="mb-5 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                        <PanelHeading
                            icon={<Clock3 size={20} />}
                            title="Recent submissions"
                            subtitle="A quick look at your latest attempts"
                            accent="text-cyan-300"
                        />

                        <div className="flex flex-wrap items-center gap-2">
                            <button
                                onClick={() => loadDashboard(true)}
                                disabled={refreshing}
                                className="inline-flex items-center justify-center gap-2 rounded-xl border border-white/10 px-3 py-2 text-sm text-slate-300 transition hover:bg-white/[0.05] disabled:opacity-60"
                            >
                                <RefreshCw size={15} className={refreshing ? 'animate-spin' : ''} />
                                Refresh
                            </button>
                            <Link
                                to="/submissions"
                                className="inline-flex items-center gap-2 rounded-xl border border-violet-300/15 bg-violet-300/[0.05] px-3 py-2 text-sm font-medium text-violet-200 transition hover:bg-violet-300/10"
                            >
                                View all
                                <ArrowRight size={15} />
                            </Link>
                        </div>
                    </div>

                    {submissions.length === 0 ? (
                        <div className="rounded-2xl border border-dashed border-white/10 px-5 py-12 text-center">
                            <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-cyan-300/[0.07] text-cyan-200">
                                <Code2 size={27} />
                            </div>
                            <h3 className="font-semibold text-white">Your coding journey starts here</h3>
                            <p className="mx-auto mt-2 max-w-sm text-sm leading-6 text-slate-500">
                                Submit your first solution and it will appear here for easy tracking.
                            </p>
                            <Link
                                to="/problems"
                                className="mt-5 inline-flex items-center gap-2 text-sm font-semibold text-cyan-300 hover:text-violet-200"
                            >
                                Explore problems
                                <ArrowRight size={16} />
                            </Link>
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full min-w-[650px] text-left text-sm">
                                <thead>
                                <tr className="border-b border-white/[0.07] text-xs uppercase tracking-wider text-slate-500">
                                    <th className="px-3 py-4 font-medium">Problem</th>
                                    <th className="px-3 py-4 font-medium">Status</th>
                                    <th className="px-3 py-4 font-medium">Language</th>
                                    <th className="px-3 py-4 font-medium">Submitted</th>
                                    <th className="px-3 py-4 text-right font-medium">Details</th>
                                </tr>
                                </thead>
                                <tbody>
                                {submissions.slice(0, 5).map((submission, index) => {
                                    const difficulty = getDifficulty(submission, problemMap)

                                    return (
                                        <tr
                                            key={submission.id ?? index}
                                            className="border-b border-white/[0.045] transition last:border-0 hover:bg-white/[0.025]"
                                        >
                                            <td className="px-3 py-4">
                                                <div className="flex items-center gap-3">
                                                        <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border border-violet-300/10 bg-violet-300/[0.06] text-violet-200">
                                                            <Code2 size={17} />
                                                        </span>
                                                    <div>
                                                        <p className="font-medium text-slate-200">
                                                            {getProblemTitle(submission, problemMap)}
                                                        </p>
                                                        {difficulty && (
                                                            <p className={`mt-1 text-xs ${
                                                                difficulty === 'EASY'
                                                                    ? 'text-emerald-300'
                                                                    : difficulty === 'MEDIUM'
                                                                        ? 'text-amber-300'
                                                                        : difficulty === 'HARD'
                                                                            ? 'text-rose-300'
                                                                            : 'text-slate-500'
                                                            }`}>
                                                                {difficulty.charAt(0) + difficulty.slice(1).toLowerCase()}
                                                            </p>
                                                        )}
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-3 py-4">
                                                <StatusBadge status={submission.status} />
                                            </td>
                                            <td className="px-3 py-4">
                                                    <span className="rounded-lg border border-white/[0.06] bg-white/[0.025] px-2.5 py-1 font-mono text-xs text-slate-300">
                                                        {submission.language || '—'}
                                                    </span>
                                            </td>
                                            <td className="px-3 py-4 text-slate-400">
                                                {getDate(submission)}
                                            </td>
                                            <td className="px-3 py-4 text-right">
                                                {submission.id != null ? (
                                                    <Link
                                                        to={`/submissions/${submission.id}`}
                                                        className="inline-flex items-center gap-1 text-cyan-300 transition hover:text-violet-200"
                                                    >
                                                        View
                                                        <ChevronRight size={15} />
                                                    </Link>
                                                ) : '—'}
                                            </td>
                                        </tr>
                                    )
                                })}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>

                {/* Quick actions */}
                <section className="mb-4">
                    <div className="mb-4">
                        <h2 className="text-lg font-semibold text-white">Quick access</h2>
                        <p className="mt-1 text-sm text-slate-500">
                            Jump straight back into your workflow.
                        </p>
                    </div>

                    <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
                        <QuickAction
                            title="Practice problems"
                            description="Find your next coding challenge."
                            to="/problems"
                            icon={<Code2 size={21} />}
                            accent="cyan"
                        />
                        <QuickAction
                            title="Submission history"
                            description="Review past attempts and results."
                            to="/submissions"
                            icon={<BookOpen size={21} />}
                            accent="purple"
                        />
                        <QuickAction
                            title="Learning progress"
                            description="Explore your progress dashboard."
                            to="/progress"
                            icon={<TrendingUp size={21} />}
                            accent="blue"
                        />
                    </div>
                </section>

                <footer className="mt-8 flex flex-col items-center justify-between gap-2 border-t border-white/[0.06] pt-5 text-xs text-slate-600 sm:flex-row">
                    <span>LeetClone · Practice. Compete. Improve.</span>
                    <span className="inline-flex items-center gap-2">
                        <CircleDot size={12} className="text-cyan-400" />
                        Your progress, your pace.
                    </span>
                </footer>
            </div>
        </main>
    )
}

function PanelHeading({ icon, title, subtitle, accent }) {
    return (
        <div className="flex items-start gap-3">
            <span className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-white/[0.06] bg-white/[0.03] ${accent}`}>
                {icon}
            </span>
            <div>
                <h2 className="font-semibold text-white">{title}</h2>
                <p className="mt-1 text-sm text-slate-500">{subtitle}</p>
            </div>
        </div>
    )
}

function StatCard({ title, value, description, icon, accent, footer }) {
    const accents = {
        cyan: 'text-cyan-300 bg-cyan-300/[0.08] border-cyan-300/15',
        green: 'text-emerald-300 bg-emerald-300/[0.08] border-emerald-300/15',
        purple: 'text-violet-300 bg-violet-300/[0.08] border-violet-300/15',
        blue: 'text-blue-300 bg-blue-300/[0.08] border-blue-300/15',
    }

    return (
        <article className="dashboard-stat-card rounded-2xl border border-white/[0.07] bg-[#111827]/85 p-5">
            <div className="flex items-start justify-between gap-3">
                <p className="text-sm font-medium text-slate-400">{title}</p>
                <span className={`flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border ${accents[accent] || accents.cyan}`}>
                    {icon}
                </span>
            </div>
            <p className="mt-5 text-3xl font-bold tracking-tight text-white">{value}</p>
            <p className="mt-2 min-h-5 text-xs text-slate-500">{description}</p>
            <div className="mt-5 border-t border-white/[0.06] pt-3">
                <p className="text-xs text-slate-400">{footer}</p>
            </div>
        </article>
    )
}

function MiniMetric({ label, value, color }) {
    return (
        <div className="rounded-xl border border-white/[0.06] bg-[#090f1b]/80 p-3">
            <p className={`text-xl font-bold ${color}`}>{value}</p>
            <p className="mt-1 text-xs text-slate-500">{label}</p>
        </div>
    )
}

function DifficultyRow({ label, count, total, color, textColor }) {
    const percentage = total > 0 ? Math.min(100, Math.round((count / total) * 100)) : 0

    return (
        <div>
            <div className="mb-2 flex items-center justify-between gap-3 text-sm">
                <span className="text-slate-300">{label}</span>
                <span className={`font-semibold ${textColor}`}>
                    {count}
                    <span className="ml-1 font-normal text-slate-600">({percentage}%)</span>
                </span>
            </div>
            <div className="h-2 overflow-hidden rounded-full bg-[#080d18]">
                <div
                    className={`h-full rounded-full transition-all duration-500 ${color}`}
                    style={{ width: `${percentage}%` }}
                />
            </div>
        </div>
    )
}

function StatusBadge({ status }) {
    const normalized = String(status || 'UNKNOWN').toUpperCase()
    const accepted = ACCEPTED_STATUSES.includes(normalized)

    let style = 'border-slate-500/15 bg-slate-500/[0.08] text-slate-400'

    if (accepted) {
        style = 'border-emerald-300/15 bg-emerald-300/[0.08] text-emerald-300'
    } else if (['PENDING', 'RUNNING'].includes(normalized)) {
        style = 'border-amber-300/15 bg-amber-300/[0.08] text-amber-300'
    } else if (normalized !== 'UNKNOWN') {
        style = 'border-rose-300/15 bg-rose-300/[0.08] text-rose-300'
    }

    return (
        <span className={`inline-flex whitespace-nowrap rounded-full border px-2.5 py-1 text-xs font-medium ${style}`}>
            <span className="mr-1.5">●</span>
            {normalized.replaceAll('_', ' ')}
        </span>
    )
}

function QuickAction({ title, description, to, icon, accent }) {
    const iconStyles = {
        cyan: 'text-cyan-300 bg-cyan-300/[0.08]',
        purple: 'text-violet-300 bg-violet-300/[0.08]',
        blue: 'text-blue-300 bg-blue-300/[0.08]',
    }

    return (
        <Link
            to={to}
            className="dashboard-action group flex items-center justify-between gap-4 rounded-2xl border border-white/[0.07] bg-[#111827]/75 p-5"
        >
            <div className="flex items-start gap-4">
                <span className={`flex h-11 w-11 shrink-0 items-center justify-center rounded-xl ${iconStyles[accent] || iconStyles.cyan}`}>
                    {icon}
                </span>
                <div>
                    <h3 className="font-semibold text-slate-100 transition group-hover:text-cyan-200">
                        {title}
                    </h3>
                    <p className="mt-1.5 text-sm leading-5 text-slate-500">
                        {description}
                    </p>
                </div>
            </div>
            <ArrowRight size={17} className="shrink-0 text-slate-600 transition group-hover:translate-x-1 group-hover:text-cyan-300" />
        </Link>
    )
}

export default Dashboard
