import { useCallback, useEffect, useMemo, useState } from "react";
import axios from "axios";
import {
    Trophy,
    Zap,
    Timer,
    Medal,
    CalendarCheck,
    CheckCircle2,
    LockKeyhole,
    Crown,
    Star,
    Flame,
    Target,
    Play,
    RotateCcw,
    ChevronRight,
    Award,
    Sparkles,
    RefreshCw,
    AlertCircle,
} from "lucide-react";

const API_URL = "http://localhost:8080/api/platform/gamification";

const api = axios.create({ baseURL: API_URL });

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

const quests = [
    {
        id: "q1",
        title: "Solve a coding problem",
        description: "Complete a problem on LeetClone.",
        xp: 30,
        icon: Target,
    },
    {
        id: "q2",
        title: "Revise a core concept",
        description: "Review one DSA or Java concept.",
        xp: 20,
        icon: Star,
    },
    {
        id: "q3",
        title: "Attempt a mini contest",
        description: "Finish the timed quiz.",
        xp: 50,
        icon: Timer,
    },
];

const contestQuestions = [
    {
        id: 1,
        topic: "Arrays",
        question: "What is the time complexity of binary search on a sorted array?",
        options: ["O(1)", "O(log n)", "O(n)", "O(n log n)"],
        answer: 1,
        explanation: "Binary search halves the remaining search range at each step.",
    },
    {
        id: 2,
        topic: "Hashing",
        question: "Which data structure checks efficiently whether an element has appeared before?",
        options: ["Stack only", "Queue only", "HashSet", "Binary tree traversal only"],
        answer: 2,
        explanation: "A HashSet supports efficient average-case membership checks.",
    },
    {
        id: 3,
        topic: "Linked Lists",
        question: "Which technique can detect a cycle in a linked list?",
        options: [
            "Two pointers moving at different speeds",
            "Binary search",
            "Prefix sum",
            "Topological sorting",
        ],
        answer: 0,
        explanation: "Floyd's cycle detection uses slow and fast pointers.",
    },
    {
        id: 4,
        topic: "Stacks",
        question: "Which data structure is most suitable for matching parentheses?",
        options: ["Queue", "Stack", "Heap", "Trie"],
        answer: 1,
        explanation: "A stack tracks the most recently opened bracket first.",
    },
    {
        id: 5,
        topic: "Graphs",
        question: "Which data structure is normally used for BFS?",
        options: [
            "Queue",
            "Stack only",
            "Priority queue in every case",
            "HashMap only",
        ],
        answer: 0,
        explanation: "BFS processes vertices in FIFO order using a queue.",
    },
];

const cardClass = "rounded-2xl border border-slate-800 bg-slate-900 p-5";
const primaryButton =
    "inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-40";
const secondaryButton =
    "inline-flex items-center justify-center gap-2 rounded-xl border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-200 transition hover:bg-slate-800 disabled:opacity-40";

function formatTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${String(minutes).padStart(2, "0")}:${String(
        remainingSeconds
    ).padStart(2, "0")}`;
}

function getLevel(xp) {
    return Math.floor(Math.max(0, xp) / 100) + 1;
}

function StatCard({ icon: Icon, label, value, detail }) {
    return (
        <div className={cardClass}>
            <div className="flex items-center justify-between gap-3">
                <span className="text-sm text-slate-400">{label}</span>
                <Icon size={20} className="text-indigo-400" />
            </div>
            <p className="mt-4 text-3xl font-bold">{value}</p>
            <p className="mt-1 text-xs text-slate-500">{detail}</p>
        </div>
    );
}

function SectionHeading({ icon: Icon, title, description }) {
    return (
        <div className="mb-5 flex items-start gap-3">
            <div className="rounded-xl bg-indigo-500/10 p-2.5">
                <Icon size={21} className="text-indigo-400" />
            </div>
            <div>
                <h2 className="text-lg font-semibold">{title}</h2>
                <p className="mt-1 text-sm text-slate-400">{description}</p>
            </div>
        </div>
    );
}

function getErrorMessage(error) {
    if (error.response?.status === 401 || error.response?.status === 403) {
        return "Your session may have expired. Please log in again.";
    }

    return (
        error.response?.data?.message ||
        "Unable to load gamification data. Check your backend and try again."
    );
}

export default function GamificationCenter() {
    const [dashboard, setDashboard] = useState(null);
    const [leaderboard, setLeaderboard] = useState([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);
    const [error, setError] = useState("");

    const [tab, setTab] = useState("overview");
    const [contestStatus, setContestStatus] = useState("idle");
    const [timeLeft, setTimeLeft] = useState(180);
    const [questionIndex, setQuestionIndex] = useState(0);
    const [answers, setAnswers] = useState({});
    const [contestReward, setContestReward] = useState(0);

    const loadGamification = useCallback(async (isRefresh = false) => {
        if (isRefresh) setRefreshing(true);
        else setLoading(true);

        setError("");

        try {
            const [dashboardResponse, leaderboardResponse] = await Promise.all([
                api.get("/dashboard"),
                api.get("/leaderboard"),
            ]);

            setDashboard(dashboardResponse.data);
            setLeaderboard(leaderboardResponse.data);
        } catch (err) {
            setError(getErrorMessage(err));
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    }, []);

    useEffect(() => {
        loadGamification();
    }, [loadGamification]);

    const xp = dashboard?.totalXp ?? 0;
    const level = dashboard?.level ?? getLevel(xp);
    const levelProgress = dashboard?.levelProgress ?? xp % 100;
    const solved = dashboard?.problemsSolved ?? 0;
    const completedContests = dashboard?.contestsCompleted ?? 0;
    const correctAnswers = dashboard?.correctQuizAnswers ?? 0;
    const achievements = dashboard?.achievements ?? [];

    const currentQuestion = contestQuestions[questionIndex];

    const correctCount = useMemo(
        () =>
            contestQuestions.filter((question) => {
                return answers[question.id] === question.answer;
            }).length,
        [answers]
    );

    const unlockedCodes = useMemo(
        () => new Set(achievements.map((item) => item.achievementCode)),
        [achievements]
    );

    const badges = useMemo(
        () => [
            {
                id: "FIRST_SOLVE",
                title: "First Blood",
                description: "Solve your first coding problem.",
                unlocked: unlockedCodes.has("FIRST_SOLVE"),
                icon: Target,
            },
            {
                id: "SOLVER_10",
                title: "Getting Started",
                description: "Solve 10 different coding problems.",
                unlocked: unlockedCodes.has("SOLVER_10"),
                icon: Crown,
            },
            {
                id: "SOLVER_50",
                title: "Problem Hunter",
                description: "Solve 50 different coding problems.",
                unlocked: unlockedCodes.has("SOLVER_50"),
                icon: Trophy,
            },
            {
                id: "CENTURY_CLUB",
                title: "Century Club",
                description: "Solve 100 different coding problems.",
                unlocked: unlockedCodes.has("CENTURY_CLUB"),
                icon: Medal,
            },
        ],
        [unlockedCodes]
    );

    // The quiz is currently practice-only. It does not claim backend XP.
    useEffect(() => {
        if (contestStatus !== "active") return undefined;

        const timer = window.setTimeout(() => {
            if (timeLeft <= 1) {
                finishContest();
            } else {
                setTimeLeft((value) => value - 1);
            }
        }, 1000);

        return () => window.clearTimeout(timer);
        // finishContest is intentionally implemented using current state below.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [contestStatus, timeLeft]);

    function startContest() {
        setAnswers({});
        setQuestionIndex(0);
        setTimeLeft(180);
        setContestReward(0);
        setContestStatus("active");
        setTab("contest");
    }

    function finishContest() {
        if (contestStatus !== "active") return;

        // Practice score only: persistent quiz rewards need a backend endpoint.
        const score = contestQuestions.filter(
            (question) => answers[question.id] === question.answer
        ).length;

        setContestReward(0);
        setContestStatus("completed");
        setTimeLeft(0);
    }

    function resetContest() {
        setContestStatus("idle");
        setTimeLeft(180);
        setAnswers({});
        setQuestionIndex(0);
        setContestReward(0);
    }

    const tabs = [
        { id: "overview", label: "Overview", icon: Sparkles },
        { id: "leaderboard", label: "Leaderboard", icon: Trophy },
        { id: "contest", label: "Timed Contest", icon: Timer },
        { id: "quests", label: "Daily Quests", icon: CalendarCheck },
        { id: "badges", label: "Achievements", icon: Medal },
    ];

    if (loading) {
        return (
            <main className="flex min-h-screen items-center justify-center bg-slate-950 text-slate-300">
                <RefreshCw size={22} className="mr-3 animate-spin" />
                Loading your gamification profile...
            </main>
        );
    }

    return (
        <main className="min-h-screen bg-slate-950 px-4 py-8 text-slate-100 sm:px-8">
            <div className="mx-auto max-w-7xl space-y-7">
                <header className="flex flex-col justify-between gap-5 lg:flex-row lg:items-center">
                    <div>
                        <p className="mb-2 text-sm font-semibold uppercase tracking-widest text-indigo-400">
                            LeetClone Rewards
                        </p>
                        <h1 className="text-3xl font-bold sm:text-4xl">
                            Contest & Gamification Center
                        </h1>
                        <p className="mt-2 max-w-2xl text-slate-400">
                            Practise consistently, take timed challenges, and track your
                            achievements.
                        </p>
                    </div>

                    <div className={`${cardClass} flex min-w-60 items-center gap-3`}>
                        <div className="rounded-xl bg-amber-500/10 p-3">
                            <Zap size={26} className="text-amber-400" />
                        </div>
                        <div>
                            <p className="text-sm text-slate-400">Backend experience</p>
                            <p className="text-2xl font-bold">{xp} XP</p>
                            <p className="text-xs text-slate-500">Level {level}</p>
                        </div>
                        <button
                            onClick={() => loadGamification(true)}
                            disabled={refreshing}
                            className="ml-auto rounded-lg p-2 hover:bg-slate-800 disabled:opacity-50"
                            aria-label="Refresh gamification data"
                            title="Refresh"
                        >
                            <RefreshCw
                                size={17}
                                className={refreshing ? "animate-spin" : ""}
                            />
                        </button>
                    </div>
                </header>

                {error && (
                    <div className="flex items-start gap-3 rounded-xl border border-red-900 bg-red-950/40 p-4 text-red-200">
                        <AlertCircle size={20} className="mt-0.5 shrink-0" />
                        <div className="flex-1">
                            <p className="font-semibold">Backend connection issue</p>
                            <p className="mt-1 text-sm">{error}</p>
                        </div>
                        <button
                            onClick={() => loadGamification(true)}
                            className="text-sm underline"
                        >
                            Retry
                        </button>
                    </div>
                )}

                <nav className="flex gap-2 overflow-x-auto pb-2">
                    {tabs.map(({ id, label, icon: Icon }) => (
                        <button
                            key={id}
                            onClick={() => setTab(id)}
                            className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-3 text-sm font-medium transition ${
                                tab === id
                                    ? "bg-indigo-600 text-white"
                                    : "border border-slate-800 bg-slate-900 text-slate-300 hover:bg-slate-800"
                            }`}
                        >
                            <Icon size={17} />
                            {label}
                        </button>
                    ))}
                </nav>

                {tab === "overview" && (
                    <>
                        <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                            <StatCard
                                icon={Zap}
                                label="Total XP"
                                value={xp}
                                detail="Persistent backend XP"
                            />
                            <StatCard
                                icon={Star}
                                label="Current level"
                                value={level}
                                detail={`${100 - levelProgress} XP to next level`}
                            />
                            <StatCard
                                icon={Target}
                                label="Problems solved"
                                value={solved}
                                detail="Unique accepted solves tracked by gamification"
                            />
                            <StatCard
                                icon={CheckCircle2}
                                label="Achievements"
                                value={achievements.length}
                                detail="Persisted unlocked achievements"
                            />
                        </section>

                        <section className="grid gap-5 lg:grid-cols-2">
                            <article className={cardClass}>
                                <SectionHeading
                                    icon={Zap}
                                    title={`Level ${level}`}
                                    description="Keep earning XP to level up."
                                />
                                <div className="mb-2 flex items-center justify-between text-sm">
                  <span className="text-slate-400">
                    Progress to level {level + 1}
                  </span>
                                    <span className="font-semibold">
                    {levelProgress}/100 XP
                  </span>
                                </div>
                                <div className="h-3 overflow-hidden rounded-full bg-slate-800">
                                    <div
                                        className="h-full rounded-full bg-gradient-to-r from-indigo-500 to-cyan-400 transition-all"
                                        style={{ width: `${levelProgress}%` }}
                                    />
                                </div>
                                <p className="mt-4 text-sm text-slate-400">
                                    XP and level progress are retrieved from the backend.
                                </p>
                            </article>

                            <article className={cardClass}>
                                <SectionHeading
                                    icon={Timer}
                                    title="Quick contest"
                                    description="Five multiple-choice DSA practice questions."
                                />
                                <div className="mb-4 flex flex-wrap gap-2">
                                    {["Arrays", "Hashing", "Linked Lists", "Stacks", "Graphs"].map(
                                        (topic) => (
                                            <span
                                                key={topic}
                                                className="rounded-full border border-slate-700 px-3 py-1 text-xs text-slate-300"
                                            >
                        {topic}
                      </span>
                                        )
                                    )}
                                </div>
                                <button className={primaryButton} onClick={startContest}>
                                    <Play size={16} />
                                    Start practice
                                </button>
                            </article>
                        </section>

                        <section className={cardClass}>
                            <SectionHeading
                                icon={Medal}
                                title="Achievements"
                                description={`${badges.filter((badge) => badge.unlocked).length} of ${badges.length} supported badges unlocked`}
                            />
                            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
                                {badges.map((badge) => {
                                    const Icon = badge.icon;
                                    return (
                                        <div
                                            key={badge.id}
                                            className={`rounded-xl border p-4 ${
                                                badge.unlocked
                                                    ? "border-amber-500/30 bg-amber-500/5"
                                                    : "border-slate-800 bg-slate-950 opacity-70"
                                            }`}
                                        >
                                            <div className="flex items-center gap-3">
                                                <Icon
                                                    size={24}
                                                    className={
                                                        badge.unlocked
                                                            ? "text-amber-300"
                                                            : "text-slate-600"
                                                    }
                                                />
                                                <div className="min-w-0">
                                                    <p className="font-semibold">{badge.title}</p>
                                                    <p className="mt-1 text-xs text-slate-400">
                                                        {badge.description}
                                                    </p>
                                                </div>
                                                {badge.unlocked ? (
                                                    <CheckCircle2
                                                        className="ml-auto shrink-0 text-emerald-400"
                                                        size={18}
                                                    />
                                                ) : (
                                                    <LockKeyhole
                                                        className="ml-auto shrink-0 text-slate-600"
                                                        size={17}
                                                    />
                                                )}
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        </section>
                    </>
                )}

                {tab === "leaderboard" && (
                    <section className={cardClass}>
                        <SectionHeading
                            icon={Trophy}
                            title="Leaderboard"
                            description="Real users ranked by their persisted XP."
                        />

                        {leaderboard.length === 0 ? (
                            <p className="rounded-xl border border-slate-800 bg-slate-950 p-6 text-center text-slate-400">
                                No leaderboard entries yet. Solve a problem to get started.
                            </p>
                        ) : (
                            <div className="space-y-3">
                                {leaderboard.map((player) => (
                                    <div
                                        key={player.userId}
                                        className="flex items-center gap-3 rounded-xl border border-slate-800 bg-slate-950 p-4"
                                    >
                                        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-slate-800 font-bold">
                                            {player.rank === 1 ? (
                                                <Crown size={20} className="text-amber-300" />
                                            ) : player.rank === 2 || player.rank === 3 ? (
                                                <Medal
                                                    size={20}
                                                    className={
                                                        player.rank === 2
                                                            ? "text-slate-300"
                                                            : "text-amber-600"
                                                    }
                                                />
                                            ) : (
                                                `#${player.rank}`
                                            )}
                                        </div>
                                        <div className="min-w-0 flex-1">
                                            <p className="truncate font-semibold">
                                                {player.username}
                                            </p>
                                            <p className="mt-1 text-xs text-slate-400">
                                                Level {player.level} · {player.problemsSolved} solved
                                            </p>
                                        </div>
                                        <div className="text-right">
                                            <p className="font-bold">{player.totalXp} XP</p>
                                            <p className="text-xs text-slate-500">Experience</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </section>
                )}

                {tab === "quests" && (
                    <section className={cardClass}>
                        <SectionHeading
                            icon={CalendarCheck}
                            title="Daily quest board"
                            description="Quest rewards are not enabled until the backend verifies and records quest completion."
                        />
                        <div className="space-y-3">
                            {quests.map((quest) => {
                                const Icon = quest.icon;
                                return (
                                    <article
                                        key={quest.id}
                                        className="flex flex-wrap items-center gap-4 rounded-xl border border-slate-800 bg-slate-950 p-4"
                                    >
                                        <div className="rounded-xl bg-indigo-500/10 p-3">
                                            <Icon className="text-indigo-400" size={21} />
                                        </div>
                                        <div className="min-w-0 flex-1">
                                            <h3 className="font-semibold">{quest.title}</h3>
                                            <p className="mt-1 text-sm text-slate-400">
                                                {quest.description}
                                            </p>
                                            <p className="mt-2 text-sm font-semibold text-amber-300">
                                                Planned reward: {quest.xp} XP
                                            </p>
                                        </div>
                                        <span className="rounded-full border border-slate-700 px-3 py-2 text-xs text-slate-400">
                      Not yet enabled
                    </span>
                                    </article>
                                );
                            })}
                        </div>
                    </section>
                )}

                {tab === "badges" && (
                    <section className={cardClass}>
                        <SectionHeading
                            icon={Award}
                            title="Achievement collection"
                            description="These achievements are loaded from the backend."
                        />
                        {achievements.length === 0 ? (
                            <div className="rounded-xl border border-slate-800 bg-slate-950 p-8 text-center">
                                <Award
                                    size={36}
                                    className="mx-auto mb-3 text-slate-500"
                                />
                                <h3 className="font-semibold">
                                    No achievements unlocked yet
                                </h3>
                                <p className="mt-2 text-sm text-slate-400">
                                    Get an accepted solution to your first problem to start
                                    earning achievements.
                                </p>
                            </div>
                        ) : (
                            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                                {achievements.map((achievement) => (
                                    <article
                                        key={achievement.achievementCode}
                                        className="rounded-2xl border border-amber-500/30 bg-amber-500/5 p-5"
                                    >
                                        <div className="flex items-center justify-between">
                                            <div className="rounded-xl bg-amber-500/10 p-3">
                                                <Medal size={27} className="text-amber-300" />
                                            </div>
                                            <CheckCircle2
                                                className="text-emerald-400"
                                                size={20}
                                            />
                                        </div>
                                        <h3 className="mt-5 font-semibold">
                                            {achievement.title}
                                        </h3>
                                        <p className="mt-2 text-sm leading-6 text-slate-400">
                                            {achievement.description}
                                        </p>
                                        <p className="mt-4 font-semibold text-amber-300">
                                            +{achievement.xpReward} XP
                                        </p>
                                        {achievement.unlockedAt && (
                                            <p className="mt-2 text-xs text-slate-500">
                                                Unlocked{" "}
                                                {new Date(
                                                    achievement.unlockedAt
                                                ).toLocaleDateString()}
                                            </p>
                                        )}
                                    </article>
                                ))}
                            </div>
                        )}
                    </section>
                )}

                {tab === "contest" && (
                    <section className={cardClass}>
                        {contestStatus === "idle" && (
                            <div className="py-8 text-center">
                                <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-indigo-500/10">
                                    <Timer size={32} className="text-indigo-400" />
                                </div>
                                <h2 className="text-2xl font-bold">
                                    Three-minute DSA practice
                                </h2>
                                <p className="mx-auto mt-3 max-w-lg text-slate-400">
                                    Answer five multiple-choice questions. This practice quiz
                                    does not award persistent XP yet.
                                </p>
                                <button
                                    className={`${primaryButton} mt-6`}
                                    onClick={startContest}
                                >
                                    <Play size={17} />
                                    Start practice
                                </button>
                            </div>
                        )}

                        {contestStatus === "active" && currentQuestion && (
                            <div>
                                <div className="mb-6 flex flex-wrap items-center justify-between gap-4">
                                    <div>
                                        <p className="text-sm text-indigo-400">
                                            Timed DSA practice
                                        </p>
                                        <h2 className="mt-1 text-xl font-bold">
                                            Question {questionIndex + 1} of {contestQuestions.length}
                                        </h2>
                                    </div>
                                    <div
                                        className={`flex items-center gap-2 rounded-xl border px-4 py-3 font-mono text-xl font-bold ${
                                            timeLeft <= 30
                                                ? "border-red-500/40 text-red-300"
                                                : "border-slate-700 text-white"
                                        }`}
                                    >
                                        <Timer size={20} />
                                        {formatTime(timeLeft)}
                                    </div>
                                </div>

                                <div className="mb-4 h-2 overflow-hidden rounded-full bg-slate-800">
                                    <div
                                        className="h-full rounded-full bg-indigo-500 transition-all"
                                        style={{
                                            width: `${
                                                ((questionIndex + 1) / contestQuestions.length) * 100
                                            }%`,
                                        }}
                                    />
                                </div>

                                <div className="rounded-xl border border-slate-800 bg-slate-950 p-5">
                                    <p className="text-xs font-semibold uppercase tracking-wider text-indigo-400">
                                        {currentQuestion.topic}
                                    </p>
                                    <h3 className="my-5 text-lg font-semibold leading-7">
                                        {currentQuestion.question}
                                    </h3>
                                    <div className="space-y-3">
                                        {currentQuestion.options.map((option, index) => {
                                            const selected =
                                                answers[currentQuestion.id] === index;

                                            return (
                                                <button
                                                    key={option}
                                                    onClick={() =>
                                                        setAnswers((old) => ({
                                                            ...old,
                                                            [currentQuestion.id]: index,
                                                        }))
                                                    }
                                                    className={`flex w-full items-center gap-3 rounded-xl border p-4 text-left text-sm transition ${
                                                        selected
                                                            ? "border-indigo-500 bg-indigo-500/10 text-white"
                                                            : "border-slate-800 hover:bg-slate-900"
                                                    }`}
                                                >
                          <span
                              className={`flex h-7 w-7 shrink-0 items-center justify-center rounded-lg ${
                                  selected ? "bg-indigo-600" : "bg-slate-800"
                              }`}
                          >
                            {String.fromCharCode(65 + index)}
                          </span>
                                                    {option}
                                                </button>
                                            );
                                        })}
                                    </div>
                                </div>

                                <div className="mt-5 flex flex-wrap items-center justify-between gap-3">
                                    <p className="text-sm text-slate-400">
                                        {Object.keys(answers).length} of {contestQuestions.length}{" "}
                                        answered
                                    </p>
                                    <div className="flex gap-2">
                                        <button
                                            className={secondaryButton}
                                            disabled={questionIndex === 0}
                                            onClick={() => setQuestionIndex((i) => i - 1)}
                                        >
                                            Previous
                                        </button>
                                        {questionIndex < contestQuestions.length - 1 ? (
                                            <button
                                                className={primaryButton}
                                                onClick={() => setQuestionIndex((i) => i + 1)}
                                            >
                                                Next <ChevronRight size={16} />
                                            </button>
                                        ) : (
                                            <button
                                                className={primaryButton}
                                                onClick={finishContest}
                                            >
                                                Finish practice
                                            </button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        )}

                        {contestStatus === "completed" && (
                            <div className="py-8 text-center">
                                <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-emerald-500/10">
                                    <Trophy size={32} className="text-emerald-400" />
                                </div>
                                <p className="text-sm font-semibold uppercase tracking-widest text-emerald-400">
                                    Practice completed
                                </p>
                                <h2 className="mt-3 text-3xl font-bold">
                                    {correctCount}/{contestQuestions.length} correct
                                </h2>
                                <p className="mt-3 text-slate-400">
                                    Practice score only. No XP was added to your account.
                                </p>

                                <div className="mx-auto mt-6 max-w-xl space-y-3 text-left">
                                    {contestQuestions.map((question, index) => (
                                        <div
                                            key={question.id}
                                            className="rounded-xl border border-slate-800 bg-slate-950 p-4"
                                        >
                                            <div className="flex items-start gap-3">
                                                {answers[question.id] === question.answer ? (
                                                    <CheckCircle2
                                                        size={19}
                                                        className="mt-0.5 shrink-0 text-emerald-400"
                                                    />
                                                ) : (
                                                    <Target
                                                        size={19}
                                                        className="mt-0.5 shrink-0 text-amber-400"
                                                    />
                                                )}
                                                <div>
                                                    <p className="font-medium">
                                                        {index + 1}. {question.question}
                                                    </p>
                                                    <p className="mt-2 text-sm text-slate-400">
                                                        Correct answer: {question.options[question.answer]}
                                                    </p>
                                                    <p className="mt-1 text-sm leading-6 text-slate-500">
                                                        {question.explanation}
                                                    </p>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                <button
                                    className={`${primaryButton} mt-6`}
                                    onClick={startContest}
                                >
                                    <RotateCcw size={16} />
                                    Try again
                                </button>
                                <button
                                    className={`${secondaryButton} ml-2 mt-6`}
                                    onClick={resetContest}
                                >
                                    Back to start
                                </button>
                            </div>
                        )}
                    </section>
                )}

                <footer className="flex items-start gap-3 rounded-xl border border-slate-800 p-4 text-sm text-slate-400">
                    <LockKeyhole
                        size={18}
                        className="mt-0.5 shrink-0 text-indigo-400"
                    />
                    <p>
                        XP, achievements, and leaderboard data come from your backend.
                        Daily quest rewards and timed quiz rewards are disabled until
                        server-side validation and persistence are implemented.
                    </p>
                </footer>
            </div>
        </main>
    );
}