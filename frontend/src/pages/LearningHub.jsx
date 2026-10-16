
import { useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import axios from 'axios'
import {
    Activity,
    Bookmark,
    CalendarDays,
    Check,
    CheckCircle2,
    ClipboardList,
    Code2,
    FileText,
    Flame,
    Plus,
    RefreshCw,
    Trash2
} from 'lucide-react'

const STORAGE = {
    bookmarks: 'leetcodeHubBookmarks',
    notes: 'leetcodeHubNotes',
    planner: 'leetcodeHubPlanner',
    completedDays: 'leetcodeHubCompletedDays',
    dailyTarget: 'leetcodeHubDailyTarget'
}

const todayKey = () => new Date().toLocaleDateString('en-CA')

function readStorage(key, fallback) {
    try {
        const value = localStorage.getItem(key)
        return value ? JSON.parse(value) : fallback
    } catch {
        return fallback
    }
}

function saveStorage(key, value) {
    localStorage.setItem(key, JSON.stringify(value))
}

function getProblemId(problem) {
    return String(problem.id ?? problem.problemId)
}

function getProblemTitle(problem) {
    return problem.title || problem.problemTitle || problem.name || 'Untitled problem'
}

function getDifficulty(problem) {
    const value = String(problem.difficulty || 'Unknown').toLowerCase()
    return value.charAt(0).toUpperCase() + value.slice(1)
}

function SectionButton({ active, onClick, icon: Icon, children }) {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`flex items-center gap-2 rounded-lg px-3 py-2.5 text-sm font-medium transition ${
                active
                    ? 'bg-orange-500 text-white'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
            }`}
        >
            <Icon size={17} />
            {children}
        </button>
    )
}

function Panel({ title, subtitle, children }) {
    return (
        <section className="rounded-2xl border border-slate-800 bg-[#151922] p-5 sm:p-6">
            <div className="mb-5">
                <h2 className="text-xl font-bold text-white">{title}</h2>
                {subtitle && (
                    <p className="mt-1 text-sm text-slate-400">{subtitle}</p>
                )}
            </div>
            {children}
        </section>
    )
}

function ProblemRow({ problem, actionLabel, onAction, actionIcon: ActionIcon }) {
    return (
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-800 p-4">
            <div className="min-w-0 flex-1">
                <Link
                    to={`/problems/${getProblemId(problem)}`}
                    className="font-semibold text-white hover:text-orange-400"
                >
                    {getProblemTitle(problem)}
                </Link>
                <p className={`mt-1 text-xs ${
                    getDifficulty(problem) === 'Easy'
                        ? 'text-green-400'
                        : getDifficulty(problem) === 'Medium'
                            ? 'text-yellow-400'
                            : getDifficulty(problem) === 'Hard'
                                ? 'text-red-400'
                                : 'text-slate-400'
                }`}>
                    {getDifficulty(problem)}
                </p>
            </div>

            <button
                type="button"
                onClick={() => onAction(problem)}
                className="inline-flex items-center gap-2 rounded-lg border border-slate-700 px-3 py-2 text-sm text-slate-300 hover:border-orange-500 hover:text-white"
            >
                {ActionIcon && <ActionIcon size={15} />}
                {actionLabel}
            </button>
        </div>
    )
}

export default function LearningHub() {
    const [activeTab, setActiveTab] = useState('daily')
    const [problems, setProblems] = useState([])
    const [submissions, setSubmissions] = useState([])
    const [loading, setLoading] = useState(true)
    const [apiError, setApiError] = useState('')

    const [bookmarks, setBookmarks] = useState(
        () => readStorage(STORAGE.bookmarks, [])
    )
    const [notes, setNotes] = useState(
        () => readStorage(STORAGE.notes, {})
    )
    const [planner, setPlanner] = useState(
        () => readStorage(STORAGE.planner, [])
    )
    const [completedDays, setCompletedDays] = useState(
        () => readStorage(STORAGE.completedDays, [])
    )
    const [dailyTarget, setDailyTarget] = useState(
        () => readStorage(STORAGE.dailyTarget, 2)
    )

    const [selectedNoteId, setSelectedNoteId] = useState('')
    const [noteDraft, setNoteDraft] = useState('')
    const [plannerProblemId, setPlannerProblemId] = useState('')
    const [plannerDate, setPlannerDate] = useState(todayKey())
    const [plannerMessage, setPlannerMessage] = useState('')

    useEffect(() => {
        let cancelled = false

        async function loadData() {
            const token = localStorage.getItem('token')

            if (!token) {
                if (!cancelled) {
                    setApiError('Please log in to use your Learning Hub.')
                    setLoading(false)
                }
                return
            }

            const headers = { Authorization: `Bearer ${token}` }

            const [problemResult, submissionResult] = await Promise.allSettled([
                axios.get('/api/problems', { headers }),
                axios.get('/api/submissions', {
                    headers,
                    params: {
                        page: 0,
                        size: 100,
                        sort: 'createdAt,desc'
                    }
                })
            ])

            if (cancelled) return

            if (problemResult.status === 'fulfilled') {
                const data = problemResult.value.data
                setProblems(Array.isArray(data) ? data : data?.content || [])
            } else {
                setApiError('Could not load problems. Check your backend connection.')
            }

            if (submissionResult.status === 'fulfilled') {
                const data = submissionResult.value.data
                setSubmissions(Array.isArray(data) ? data : data?.content || [])
            }

            if (
                problemResult.status === 'rejected' &&
                (problemResult.reason?.response?.status === 401 ||
                    problemResult.reason?.response?.status === 403)
            ) {
                setApiError('Your session may have expired. Please log in again.')
            }

            if (!cancelled) setLoading(false)
        }

        loadData()

        return () => {
            cancelled = true
        }
    }, [])

    useEffect(() => saveStorage(STORAGE.bookmarks, bookmarks), [bookmarks])
    useEffect(() => saveStorage(STORAGE.notes, notes), [notes])
    useEffect(() => saveStorage(STORAGE.planner, planner), [planner])
    useEffect(() => saveStorage(STORAGE.completedDays, completedDays), [completedDays])
    useEffect(() => saveStorage(STORAGE.dailyTarget, dailyTarget), [dailyTarget])

    const problemMap = useMemo(
        () => new Map(problems.map(problem => [getProblemId(problem), problem])),
        [problems]
    )

    const bookmarkedProblems = bookmarks
        .map(id => problemMap.get(String(id)))
        .filter(Boolean)

    const dailyProblem = useMemo(() => {
        if (problems.length === 0) return null

        // A consistent daily choice based on the local date.
        const date = todayKey()
        let hash = 0

        for (const char of date) {
            hash = (hash * 31 + char.charCodeAt(0)) >>> 0
        }

        return problems[hash % problems.length]
    }, [problems])

    const todayCompleted = completedDays.includes(todayKey())

    const currentStreak = useMemo(() => {
        const dates = new Set(completedDays)
        const cursor = new Date()

        if (!dates.has(todayKey())) {
            cursor.setDate(cursor.getDate() - 1)
        }

        let count = 0

        while (dates.has(cursor.toLocaleDateString('en-CA'))) {
            count++
            cursor.setDate(cursor.getDate() - 1)
        }

        return count
    }, [completedDays])

    const longestStreak = useMemo(() => {
        const dates = [...new Set(completedDays)].sort()
        let best = 0
        let run = 0
        let previous = null

        for (const date of dates) {
            const current = new Date(`${date}T12:00:00`)
            const previousDate = previous ? new Date(`${previous}T12:00:00`) : null
            const difference = previousDate
                ? Math.round((current - previousDate) / 86400000)
                : 0

            run = previous && difference === 1 ? run + 1 : 1
            best = Math.max(best, run)
            previous = date
        }

        return best
    }, [completedDays])

    const todayPlannerTasks = planner.filter(task => task.date === todayKey())
    const todayTasksDone = todayPlannerTasks.filter(task => task.done).length

    function toggleBookmark(problem) {
        const id = getProblemId(problem)

        setBookmarks(current =>
            current.includes(id)
                ? current.filter(item => item !== id)
                : [...current, id]
        )
    }

    function openNote(problem) {
        const id = getProblemId(problem)
        setSelectedNoteId(id)
        setNoteDraft(notes[id] || '')
        setActiveTab('notes')
    }

    function saveNote() {
        if (!selectedNoteId) return

        setNotes(current => ({
            ...current,
            [selectedNoteId]: noteDraft
        }))
    }

    function addPlannerTask() {
        if (!plannerProblemId) {
            setPlannerMessage('Choose a problem first.')
            return
        }

        if (planner.some(task =>
            task.problemId === plannerProblemId && task.date === plannerDate
        )) {
            setPlannerMessage('This problem is already on that date.')
            return
        }

        setPlanner(current => [
            ...current,
            {
                id: `${Date.now()}-${plannerProblemId}`,
                problemId: plannerProblemId,
                date: plannerDate,
                done: false
            }
        ])

        setPlannerMessage('Problem added to your study plan.')
    }

    function togglePlannerTask(taskId) {
        setPlanner(current =>
            current.map(task =>
                task.id === taskId ? { ...task, done: !task.done } : task
            )
        )
    }

    function removePlannerTask(taskId) {
        setPlanner(current => current.filter(task => task.id !== taskId))
    }

    function markTodayComplete() {
        if (todayCompleted) return

        setCompletedDays(current => [...current, todayKey()])
    }

    function refreshPage() {
        window.location.reload()
    }

    const tabs = [
        { id: 'daily', label: 'Daily Challenge', icon: CalendarDays },
        { id: 'bookmarks', label: 'Bookmarks', icon: Bookmark },
        { id: 'notes', label: 'Problem Notes', icon: FileText },
        { id: 'planner', label: 'Study Planner', icon: ClipboardList },
        { id: 'streaks', label: 'Streaks', icon: Flame }
    ]

    if (loading) {
        return (
            <main className="min-h-screen bg-[#0b0f19] p-10 text-center text-white">
                <RefreshCw className="mx-auto mb-3 animate-spin text-orange-400" size={30} />
                Loading Learning Hub...
            </main>
        )
    }

    if (apiError && problems.length === 0) {
        return (
            <main className="min-h-screen bg-[#0b0f19] p-6 text-white">
                <div className="mx-auto max-w-2xl rounded-2xl border border-slate-800 bg-[#151922] p-6">
                    <h1 className="text-2xl font-bold">Learning Hub unavailable</h1>
                    <p className="mt-3 text-slate-400">{apiError}</p>
                    <button
                        type="button"
                        onClick={refreshPage}
                        className="mt-5 rounded-lg bg-orange-500 px-4 py-2 font-semibold"
                    >
                        Retry
                    </button>
                </div>
            </main>
        )
    }

    return (
        <main className="min-h-screen bg-[#0b0f19] px-4 py-8 text-white sm:px-6">
            <div className="mx-auto max-w-7xl">
                <div className="mb-8 flex flex-wrap items-center justify-between gap-4">
                    <div>
                        <p className="mb-2 text-sm font-semibold uppercase tracking-widest text-orange-400">
                            Learn consistently
                        </p>
                        <h1 className="text-3xl font-bold sm:text-4xl">Learning Hub</h1>
                        <p className="mt-2 text-slate-400">
                            Challenges, bookmarks, notes, plans, and coding streaks in one place.
                        </p>
                    </div>

                    <Link
                        to="/problems"
                        className="inline-flex items-center gap-2 rounded-lg bg-orange-500 px-4 py-3 font-semibold hover:bg-orange-600"
                    >
                        <Code2 size={18} />
                        Browse Problems
                    </Link>
                </div>

                {apiError && (
                    <div className="mb-5 rounded-xl border border-yellow-900 bg-yellow-950/30 p-4 text-sm text-yellow-200">
                        {apiError}
                    </div>
                )}

                <div className="mb-6 grid gap-4 sm:grid-cols-3">
                    <div className="rounded-xl border border-slate-800 bg-[#151922] p-4">
                        <div className="flex items-center gap-2 text-slate-400">
                            <Bookmark size={18} className="text-orange-400" />
                            Bookmarked
                        </div>
                        <p className="mt-3 text-3xl font-bold">{bookmarks.length}</p>
                    </div>

                    <div className="rounded-xl border border-slate-800 bg-[#151922] p-4">
                        <div className="flex items-center gap-2 text-slate-400">
                            <Flame size={18} className="text-orange-400" />
                            Current streak
                        </div>
                        <p className="mt-3 text-3xl font-bold">{currentStreak} days</p>
                    </div>

                    <div className="rounded-xl border border-slate-800 bg-[#151922] p-4">
                        <div className="flex items-center gap-2 text-slate-400">
                            <ClipboardList size={18} className="text-orange-400" />
                            Today's plan
                        </div>
                        <p className="mt-3 text-3xl font-bold">
                            {todayTasksDone}/{todayPlannerTasks.length}
                        </p>
                    </div>
                </div>

                <div className="mb-6 flex flex-wrap gap-2 rounded-xl border border-slate-800 bg-[#111620] p-2">
                    {tabs.map(tab => (
                        <SectionButton
                            key={tab.id}
                            active={activeTab === tab.id}
                            onClick={() => setActiveTab(tab.id)}
                            icon={tab.icon}
                        >
                            {tab.label}
                        </SectionButton>
                    ))}
                </div>

                {activeTab === 'daily' && (
                    <Panel
                        title="Daily Challenge"
                        subtitle="Your selected problem stays the same for the current date."
                    >
                        {dailyProblem ? (
                            <div className="rounded-xl border border-slate-800 bg-[#0b0f19] p-5">
                                <div className="flex flex-wrap items-start justify-between gap-4">
                                    <div>
                                        <span className="rounded-full bg-orange-500/10 px-3 py-1 text-xs font-semibold text-orange-400">
                                            Today's problem
                                        </span>
                                        <h3 className="mt-4 text-2xl font-bold">
                                            {getProblemTitle(dailyProblem)}
                                        </h3>
                                        <p className="mt-2 text-sm text-slate-400">
                                            Difficulty: {getDifficulty(dailyProblem)}
                                        </p>
                                    </div>
                                    <CalendarDays className="text-orange-400" size={30} />
                                </div>

                                <p className="mt-4 text-slate-400">
                                    {dailyProblem.description ||
                                        dailyProblem.problemStatement ||
                                        'Open the problem to review its statement, examples, and constraints.'}
                                </p>

                                <div className="mt-6 flex flex-wrap gap-3">
                                    <Link
                                        to={`/problems/${getProblemId(dailyProblem)}`}
                                        className="rounded-lg bg-orange-500 px-4 py-2 font-semibold hover:bg-orange-600"
                                    >
                                        Solve Challenge
                                    </Link>

                                    <button
                                        type="button"
                                        onClick={() => toggleBookmark(dailyProblem)}
                                        className="rounded-lg border border-slate-700 px-4 py-2 hover:border-orange-500"
                                    >
                                        {bookmarks.includes(getProblemId(dailyProblem))
                                            ? 'Remove Bookmark'
                                            : 'Bookmark Problem'}
                                    </button>

                                    <button
                                        type="button"
                                        onClick={markTodayComplete}
                                        disabled={todayCompleted}
                                        className="inline-flex items-center gap-2 rounded-lg border border-slate-700 px-4 py-2 disabled:cursor-not-allowed disabled:opacity-50"
                                    >
                                        <Check size={17} />
                                        {todayCompleted ? 'Day Completed' : 'Mark Day Complete'}
                                    </button>
                                </div>

                                <p className="mt-4 text-xs text-slate-500">
                                    Marking the day complete is a manual habit tracker; it does not verify a successful submission.
                                </p>
                            </div>
                        ) : (
                            <p className="text-slate-400">
                                No problems were returned by the API yet.
                            </p>
                        )}
                    </Panel>
                )}

                {activeTab === 'bookmarks' && (
                    <Panel
                        title="Saved Problems"
                        subtitle="Keep a list of problems you want to revisit."
                    >
                        {bookmarkedProblems.length === 0 ? (
                            <div className="rounded-xl border border-dashed border-slate-700 p-8 text-center">
                                <Bookmark className="mx-auto mb-3 text-slate-500" size={30} />
                                <p className="font-semibold">No bookmarks yet</p>
                                <p className="mt-2 text-sm text-slate-400">
                                    Save a problem from here or from the daily challenge.
                                </p>
                                <Link
                                    to="/problems"
                                    className="mt-4 inline-flex rounded-lg bg-orange-500 px-4 py-2 font-semibold"
                                >
                                    Browse Problems
                                </Link>
                            </div>
                        ) : (
                            <div className="space-y-3">
                                {bookmarkedProblems.map(problem => (
                                    <ProblemRow
                                        key={getProblemId(problem)}
                                        problem={problem}
                                        actionLabel="Remove"
                                        actionIcon={Trash2}
                                        onAction={toggleBookmark}
                                    />
                                ))}
                            </div>
                        )}
                    </Panel>
                )}

                {activeTab === 'notes' && (
                    <Panel
                        title="Problem Notes"
                        subtitle="Store your approach, algorithm, complexity, and revision reminders."
                    >
                        <label className="mb-2 block text-sm text-slate-300">
                            Choose a problem
                        </label>

                        <select
                            value={selectedNoteId}
                            onChange={event => {
                                const id = event.target.value
                                setSelectedNoteId(id)
                                setNoteDraft(notes[id] || '')
                            }}
                            className="mb-4 w-full rounded-lg border border-slate-700 bg-[#0b0f19] p-3 text-white"
                        >
                            <option value="">Select a problem...</option>
                            {problems.map(problem => (
                                <option
                                    key={getProblemId(problem)}
                                    value={getProblemId(problem)}
                                >
                                    {getProblemTitle(problem)}
                                </option>
                            ))}
                        </select>

                        <label className="mb-2 block text-sm text-slate-300">
                            Your notes
                        </label>

                        <textarea
                            value={noteDraft}
                            onChange={event => setNoteDraft(event.target.value)}
                            placeholder="Approach, pattern, edge cases, time complexity, space complexity..."
                            rows={8}
                            className="w-full resize-y rounded-xl border border-slate-700 bg-[#0b0f19] p-4 text-white outline-none focus:border-orange-500"
                        />

                        <div className="mt-4 flex flex-wrap items-center gap-3">
                            <button
                                type="button"
                                onClick={saveNote}
                                disabled={!selectedNoteId}
                                className="rounded-lg bg-orange-500 px-4 py-2 font-semibold disabled:opacity-50"
                            >
                                Save Notes
                            </button>

                            {selectedNoteId && (
                                <span className="text-sm text-slate-400">
                                    {notes[selectedNoteId]
                                        ? 'A saved note exists for this problem.'
                                        : 'No saved note yet.'}
                                </span>
                            )}
                        </div>
                    </Panel>
                )}

                {activeTab === 'planner' && (
                    <Panel
                        title="Study Planner"
                        subtitle="Choose problems and schedule them for a day."
                    >
                        <div className="grid gap-4 sm:grid-cols-3">
                            <div>
                                <label className="mb-2 block text-sm text-slate-300">
                                    Problem
                                </label>
                                <select
                                    value={plannerProblemId}
                                    onChange={event => setPlannerProblemId(event.target.value)}
                                    className="w-full rounded-lg border border-slate-700 bg-[#0b0f19] p-3 text-white"
                                >
                                    <option value="">Select problem...</option>
                                    {problems.map(problem => (
                                        <option
                                            key={getProblemId(problem)}
                                            value={getProblemId(problem)}
                                        >
                                            {getProblemTitle(problem)}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label className="mb-2 block text-sm text-slate-300">
                                    Planned date
                                </label>
                                <input
                                    type="date"
                                    value={plannerDate}
                                    onChange={event => setPlannerDate(event.target.value)}
                                    className="w-full rounded-lg border border-slate-700 bg-[#0b0f19] p-3 text-white"
                                />
                            </div>

                            <div className="flex items-end">
                                <button
                                    type="button"
                                    onClick={addPlannerTask}
                                    className="inline-flex w-full items-center justify-center gap-2 rounded-lg bg-orange-500 px-4 py-3 font-semibold hover:bg-orange-600"
                                >
                                    <Plus size={18} />
                                    Add to Plan
                                </button>
                            </div>
                        </div>

                        {plannerMessage && (
                            <p className="mt-3 text-sm text-slate-400">
                                {plannerMessage}
                            </p>
                        )}

                        <div className="mt-6 space-y-3">
                            {planner.length === 0 ? (
                                <p className="rounded-xl border border-dashed border-slate-700 p-6 text-center text-slate-400">
                                    Your study plan is empty. Add a problem above.
                                </p>
                            ) : (
                                [...planner]
                                    .sort((a, b) => a.date.localeCompare(b.date))
                                    .map(task => {
                                        const problem = problemMap.get(task.problemId)

                                        return (
                                            <div
                                                key={task.id}
                                                className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-800 p-4"
                                            >
                                                <label className="flex min-w-0 flex-1 items-center gap-3">
                                                    <input
                                                        type="checkbox"
                                                        checked={task.done}
                                                        onChange={() => togglePlannerTask(task.id)}
                                                        className="h-4 w-4 accent-orange-500"
                                                    />
                                                    <span className="min-w-0">
                                                        {problem ? (
                                                            <Link
                                                                to={`/problems/${task.problemId}`}
                                                                className={`font-medium hover:text-orange-400 ${
                                                                    task.done
                                                                        ? 'text-slate-500 line-through'
                                                                        : 'text-white'
                                                                }`}
                                                            >
                                                                {getProblemTitle(problem)}
                                                            </Link>
                                                        ) : (
                                                            <span className="text-slate-400">
                                                                Problem #{task.problemId}
                                                            </span>
                                                        )}
                                                        <span className="mt-1 block text-xs text-slate-500">
                                                            {task.date}
                                                        </span>
                                                    </span>
                                                </label>

                                                <button
                                                    type="button"
                                                    onClick={() => removePlannerTask(task.id)}
                                                    aria-label="Remove planned problem"
                                                    className="rounded-lg p-2 text-slate-400 hover:bg-red-500/10 hover:text-red-400"
                                                >
                                                    <Trash2 size={17} />
                                                </button>
                                            </div>
                                        )
                                    })
                            )}
                        </div>
                    </Panel>
                )}

                {activeTab === 'streaks' && (
                    <Panel
                        title="Coding Streaks"
                        subtitle="Build consistency by recording the days you practice."
                    >
                        <div className="grid gap-4 sm:grid-cols-3">
                            <div className="rounded-xl border border-slate-800 bg-[#0b0f19] p-5">
                                <Flame className="mb-3 text-orange-400" size={25} />
                                <p className="text-sm text-slate-400">Current streak</p>
                                <p className="mt-2 text-3xl font-bold">{currentStreak}</p>
                                <p className="mt-1 text-xs text-slate-500">consecutive days</p>
                            </div>

                            <div className="rounded-xl border border-slate-800 bg-[#0b0f19] p-5">
                                <TrophyIcon />
                                <p className="mt-3 text-sm text-slate-400">Longest streak</p>
                                <p className="mt-2 text-3xl font-bold">{longestStreak}</p>
                                <p className="mt-1 text-xs text-slate-500">consecutive days</p>
                            </div>

                            <div className="rounded-xl border border-slate-800 bg-[#0b0f19] p-5">
                                <CheckCircle2 className="mb-3 text-green-400" size={25} />
                                <p className="text-sm text-slate-400">Days recorded</p>
                                <p className="mt-2 text-3xl font-bold">
                                    {new Set(completedDays).size}
                                </p>
                                <p className="mt-1 text-xs text-slate-500">total practice days</p>
                            </div>
                        </div>

                        <div className="mt-6 rounded-xl border border-slate-800 p-5">
                            <div className="flex flex-wrap items-center justify-between gap-3">
                                <div>
                                    <h3 className="font-semibold">Record today's practice</h3>
                                    <p className="mt-1 text-sm text-slate-400">
                                        This manual tracker does not verify submissions.
                                    </p>
                                </div>

                                <button
                                    type="button"
                                    onClick={markTodayComplete}
                                    disabled={todayCompleted}
                                    className="inline-flex items-center gap-2 rounded-lg bg-orange-500 px-4 py-2 font-semibold disabled:cursor-not-allowed disabled:opacity-50"
                                >
                                    <Check size={17} />
                                    {todayCompleted ? 'Completed Today' : 'Complete Today'}
                                </button>
                            </div>
                        </div>

                        <h3 className="mb-3 mt-6 font-semibold">Recent practice days</h3>

                        <div className="grid grid-cols-7 gap-2 sm:grid-cols-10 md:grid-cols-14">
                            {Array.from({ length: 28 }, (_, index) => {
                                const date = new Date()
                                date.setDate(date.getDate() - (27 - index))
                                const key = date.toLocaleDateString('en-CA')
                                const completed = completedDays.includes(key)

                                return (
                                    <div
                                        key={key}
                                        title={`${key}${completed ? ' — completed' : ''}`}
                                        className={`flex aspect-square items-center justify-center rounded-md text-xs ${
                                            completed
                                                ? 'bg-green-500 text-white'
                                                : 'bg-slate-800 text-slate-500'
                                        }`}
                                    >
                                        {completed ? <Check size={14} /> : date.getDate()}
                                    </div>
                                )
                            })}
                        </div>
                    </Panel>
                )}
            </div>
        </main>
    )
}

function TrophyIcon() {
    return <Activity className="mb-3 text-yellow-400" size={25} />
}
