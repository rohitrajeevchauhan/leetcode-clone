
import { useEffect, useState } from 'react'
import {
    BookOpen, BriefcaseBusiness, CheckCircle2, ChevronRight,
    ClipboardList, RotateCcw, Target, Timer, Trash2
} from 'lucide-react'

const STORAGE = {
    roadmap: 'leetcodeInterviewRoadmap',
    cards: 'leetcodeInterviewFlashcards',
    applications: 'leetcodeInterviewApplications',
}

const roadmapData = [
    { id: 1, category: 'DSA', title: 'Arrays, strings and hashing', tasks: 20 },
    { id: 2, category: 'DSA', title: 'Two pointers and sliding window', tasks: 15 },
    { id: 3, category: 'DSA', title: 'Linked lists, stacks and queues', tasks: 15 },
    { id: 4, category: 'DSA', title: 'Trees, BST and heaps', tasks: 20 },
    { id: 5, category: 'DSA', title: 'Graphs, backtracking and DP', tasks: 25 },
    { id: 6, category: 'Java', title: 'OOP, collections and exceptions', tasks: 15 },
    { id: 7, category: 'SQL', title: 'Joins, grouping and subqueries', tasks: 15 },
    { id: 8, category: 'Backend', title: 'Spring Boot, REST and JPA', tasks: 15 },
    { id: 9, category: 'CS Fundamentals', title: 'DBMS, OS and computer networks', tasks: 20 },
    { id: 10, category: 'Projects', title: 'Explain architecture and trade-offs', tasks: 5 },
]

const questions = [
    { topic: 'Java', difficulty: 'Easy', question: 'What are the four pillars of OOP?', answer: 'Encapsulation, abstraction, inheritance and polymorphism.' },
    { topic: 'Java', difficulty: 'Medium', question: 'How does HashMap work internally?', answer: 'HashMap uses hash codes to locate buckets and uses equality checks to find keys. Collisions are handled within buckets; implementations can treeify heavily collided buckets.' },
    { topic: 'Java', difficulty: 'Medium', question: 'What is the difference between == and equals()?', answer: 'For objects, == compares references. equals() compares logical equality when implemented accordingly.' },
    { topic: 'SQL', difficulty: 'Easy', question: 'What is the difference between WHERE and HAVING?', answer: 'WHERE filters rows before grouping. HAVING filters groups after aggregation.' },
    { topic: 'SQL', difficulty: 'Medium', question: 'How do you find the second-highest salary?', answer: 'One approach: SELECT MAX(salary) FROM Employee WHERE salary < (SELECT MAX(salary) FROM Employee);' },
    { topic: 'Spring Boot', difficulty: 'Medium', question: 'What is dependency injection?', answer: 'A container provides an object with the dependencies it needs instead of the object constructing them itself.' },
    { topic: 'Spring Boot', difficulty: 'Medium', question: 'What is the difference between @Controller and @RestController?', answer: '@RestController combines @Controller and @ResponseBody, making handler return values the response body by default.' },
    { topic: 'DBMS', difficulty: 'Easy', question: 'What is database normalization?', answer: 'Structuring relational tables to reduce unnecessary duplication and avoid update, insertion and deletion anomalies.' },
    { topic: 'Operating Systems', difficulty: 'Medium', question: 'What is the difference between a process and a thread?', answer: 'A process has its own execution environment and resources; threads within a process share much of that process state.' },
    { topic: 'Computer Networks', difficulty: 'Easy', question: 'What is the difference between TCP and UDP?', answer: 'TCP provides reliable, ordered delivery. UDP is connectionless and does not guarantee delivery or ordering.' },
    { topic: 'DSA', difficulty: 'Medium', question: 'When would you use a sliding window?', answer: 'For suitable contiguous subarray or substring problems where a window can be expanded and contracted efficiently.' },
    { topic: 'DSA', difficulty: 'Medium', question: 'What is the difference between BFS and DFS?', answer: 'BFS explores level by level using a queue; DFS explores deeply using recursion or a stack.' },
]

const initialCards = [
    { id: 1, front: 'Java: ArrayList vs LinkedList', back: 'ArrayList offers efficient indexed access. LinkedList has costly indexed access but efficient local insertions/removals when the position is already known.' },
    { id: 2, front: 'SQL: INNER JOIN vs LEFT JOIN', back: 'INNER JOIN returns matching rows. LEFT JOIN retains all left-side rows and fills unmatched right-side columns with NULL.' },
    { id: 3, front: 'Spring: What is IoC?', back: 'Inversion of Control means object creation and dependency management are delegated to a container or framework.' },
]

function load(key, fallback) {
    try {
        const value = localStorage.getItem(key)
        return value ? JSON.parse(value) : fallback
    } catch {
        return fallback
    }
}

function save(key, value) {
    try {
        localStorage.setItem(key, JSON.stringify(value))
    } catch {
        // Keep the page usable if browser storage is unavailable.
    }
}

const panelClass = 'rounded-2xl border border-slate-700 bg-slate-900 p-5'
const buttonClass = 'rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-40'
const inputClass = 'w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm text-white outline-none focus:border-indigo-500'

export default function InterviewPrep() {
    const [tab, setTab] = useState('roadmap')
    const [completed, setCompleted] = useState(() => load(STORAGE.roadmap, []))
    const [cards, setCards] = useState(() => load(STORAGE.cards, initialCards))
    const [applications, setApplications] = useState(() => load(STORAGE.applications, []))
    const [topic, setTopic] = useState('All')
    const [questionIndex, setQuestionIndex] = useState(0)
    const [revealed, setRevealed] = useState(false)
    const [flashIndex, setFlashIndex] = useState(0)
    const [flipped, setFlipped] = useState(false)
    const [front, setFront] = useState('')
    const [back, setBack] = useState('')
    const [company, setCompany] = useState('')
    const [role, setRole] = useState('Software Engineer Intern')
    const [status, setStatus] = useState('Applied')
    const [date, setDate] = useState('')

    useEffect(() => save(STORAGE.roadmap, completed), [completed])
    useEffect(() => save(STORAGE.cards, cards), [cards])
    useEffect(() => save(STORAGE.applications, applications), [applications])

    const topics = ['All', ...new Set(questions.map(q => q.topic))]
    const filteredQuestions = questions.filter(q => topic === 'All' || q.topic === topic)
    const currentQuestion = filteredQuestions[questionIndex % Math.max(filteredQuestions.length, 1)]
    const progress = Math.round((completed.length / roadmapData.length) * 100)
    const currentCard = cards.length ? cards[flashIndex % cards.length] : null

    function toggleTask(id) {
        setCompleted(old => old.includes(id) ? old.filter(item => item !== id) : [...old, id])
    }

    function addCard(event) {
        event.preventDefault()
        if (!front.trim() || !back.trim()) return
        setCards(old => [...old, { id: Date.now(), front: front.trim(), back: back.trim() }])
        setFront('')
        setBack('')
    }

    function addApplication(event) {
        event.preventDefault()
        if (!company.trim()) return
        setApplications(old => [{
            id: Date.now(),
            company: company.trim(),
            role: role.trim() || 'Software Engineer',
            status,
            date: date || new Date().toISOString().slice(0, 10),
        }, ...old])
        setCompany('')
        setRole('Software Engineer Intern')
        setStatus('Applied')
        setDate('')
    }

    const tabs = [
        { id: 'roadmap', label: 'Roadmap', icon: Target },
        { id: 'questions', label: 'Question Bank', icon: BookOpen },
        { id: 'mock', label: 'Mock Interview', icon: Timer },
        { id: 'flashcards', label: 'Flashcards', icon: RotateCcw },
        { id: 'applications', label: 'Applications', icon: BriefcaseBusiness },
    ]

    return (
        <main className="min-h-screen bg-slate-950 px-4 py-8 text-slate-100 sm:px-8">
            <div className="mx-auto max-w-7xl space-y-7">
                <header className="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
                    <div>
                        <p className="mb-2 text-sm font-semibold uppercase tracking-widest text-indigo-400">
                            LeetClone Career Toolkit
                        </p>
                        <h1 className="text-3xl font-bold sm:text-4xl">Interview Prep Center</h1>
                        <p className="mt-2 text-slate-400">
                            Prepare for coding rounds, technical interviews and job applications.
                        </p>
                    </div>
                    <div className={`${panelClass} min-w-44`}>
                        <div className="flex items-center justify-between text-sm text-slate-400">
                            <span>Roadmap progress</span>
                            <Target size={17} />
                        </div>
                        <p className="mt-2 text-3xl font-bold">{progress}%</p>
                        <div className="mt-3 h-2 overflow-hidden rounded-full bg-slate-700">
                            <div className="h-full rounded-full bg-indigo-500 transition-all" style={{ width: `${progress}%` }} />
                        </div>
                        <p className="mt-2 text-xs text-slate-400">
                            {completed.length} of {roadmapData.length} topics completed
                        </p>
                    </div>
                </header>

                <nav className="flex gap-2 overflow-x-auto pb-2">
                    {tabs.map(({ id, label, icon: Icon }) => (
                        <button key={id} onClick={() => setTab(id)}
                                className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-3 text-sm font-medium transition ${
                                    tab === id ? 'bg-indigo-600 text-white' : 'border border-slate-800 bg-slate-900 text-slate-300 hover:bg-slate-800'
                                }`}>
                            <Icon size={17} /> {label}
                        </button>
                    ))}
                </nav>

                {tab === 'roadmap' && (
                    <section className={panelClass}>
                        <h2 className="mb-1 text-xl font-semibold">Technical preparation roadmap</h2>
                        <p className="mb-5 text-sm text-slate-400">Check off each topic as you finish revising it.</p>
                        <div className="mb-4 grid gap-3 sm:grid-cols-3">
                            {['DSA', 'Java', 'SQL', 'Backend', 'CS Fundamentals', 'Projects'].map(category => {
                                const total = roadmapData.filter(t => t.category === category).length
                                const done = roadmapData.filter(t => t.category === category && completed.includes(t.id)).length
                                return (
                                    <div key={category} className="rounded-xl border border-slate-800 bg-slate-950 p-3">
                                        <p className="text-sm text-slate-400">{category}</p>
                                        <p className="mt-1 text-xl font-semibold">{done}/{total}</p>
                                    </div>
                                )
                            })}
                        </div>
                        <div className="space-y-3">
                            {roadmapData.map(item => (
                                <label key={item.id} className="flex cursor-pointer items-center gap-3 rounded-xl border border-slate-800 p-4 hover:bg-slate-800/50">
                                    <input type="checkbox" checked={completed.includes(item.id)} onChange={() => toggleTask(item.id)} className="h-4 w-4 accent-indigo-500" />
                                    <div className="min-w-0 flex-1">
                                        <p className={completed.includes(item.id) ? 'text-slate-500 line-through' : 'font-medium'}>{item.title}</p>
                                        <p className="mt-1 text-xs text-slate-400">{item.category} · {item.tasks} suggested practice tasks</p>
                                    </div>
                                    {completed.includes(item.id) && <CheckCircle2 className="text-emerald-400" size={19} />}
                                </label>
                            ))}
                        </div>
                    </section>
                )}

                {tab === 'questions' && (
                    <section className={panelClass}>
                        <h2 className="text-xl font-semibold">Technical question bank</h2>
                        <p className="mb-4 mt-1 text-sm text-slate-400">Practice common fresher interview questions.</p>
                        <div className="mb-5 flex flex-wrap gap-2">
                            {topics.map(item => (
                                <button key={item} onClick={() => { setTopic(item); setQuestionIndex(0); setRevealed(false) }}
                                        className={`rounded-full px-3 py-2 text-sm ${topic === item ? 'bg-indigo-600' : 'bg-slate-800 text-slate-300'}`}>
                                    {item}
                                </button>
                            ))}
                        </div>
                        {currentQuestion && (
                            <article className="rounded-xl border border-slate-700 bg-slate-950 p-5">
                                <div className="flex flex-wrap gap-2 text-xs">
                                    <span className="rounded-full bg-indigo-500/15 px-3 py-1 text-indigo-300">{currentQuestion.topic}</span>
                                    <span className="rounded-full bg-slate-800 px-3 py-1 text-slate-300">{currentQuestion.difficulty}</span>
                                    <span className="ml-auto text-slate-500">{(questionIndex % filteredQuestions.length) + 1} / {filteredQuestions.length}</span>
                                </div>
                                <h3 className="my-6 text-lg font-semibold">{currentQuestion.question}</h3>
                                {revealed && <p className="mb-5 rounded-lg bg-slate-900 p-4 leading-7 text-slate-300">{currentQuestion.answer}</p>}
                                <div className="flex flex-wrap gap-3">
                                    <button className={buttonClass} onClick={() => setRevealed(v => !v)}>
                                        {revealed ? 'Hide answer' : 'Reveal answer'}
                                    </button>
                                    <button className="rounded-lg border border-slate-700 px-4 py-2 text-sm hover:bg-slate-800"
                                            onClick={() => { setQuestionIndex(i => i + 1); setRevealed(false) }}>
                                        Next question <ChevronRight className="ml-1 inline" size={16} />
                                    </button>
                                </div>
                            </article>
                        )}
                    </section>
                )}

                {tab === 'mock' && (
                    <section className={panelClass}>
                        <h2 className="text-xl font-semibold">Mock interview practice</h2>
                        <p className="mb-5 mt-1 text-sm text-slate-400">Answer aloud or write your answer, then compare it with the reference answer.</p>
                        {currentQuestion && (
                            <div className="rounded-xl border border-slate-700 bg-slate-950 p-5">
                                <p className="text-sm text-indigo-400">{currentQuestion.topic} · {currentQuestion.difficulty}</p>
                                <h3 className="my-4 text-xl font-semibold">{currentQuestion.question}</h3>
                                <textarea className={`${inputClass} min-h-36 resize-y`} placeholder="Write your answer here..." />
                                {revealed && <div className="mt-4 rounded-lg border border-emerald-900 bg-emerald-950/30 p-4">
                                    <p className="mb-2 font-semibold text-emerald-300">Reference answer</p>
                                    <p className="text-sm leading-6 text-slate-300">{currentQuestion.answer}</p>
                                </div>}
                                <div className="mt-4 flex flex-wrap gap-3">
                                    <button className={buttonClass} onClick={() => setRevealed(v => !v)}>
                                        {revealed ? 'Hide reference' : 'Review answer'}
                                    </button>
                                    <button className="rounded-lg border border-slate-700 px-4 py-2 text-sm hover:bg-slate-800"
                                            onClick={() => { setQuestionIndex(i => i + 1); setRevealed(false) }}>
                                        Next question <ChevronRight className="ml-1 inline" size={16} />
                                    </button>
                                </div>
                            </div>
                        )}
                        <p className="mt-4 text-xs text-slate-500">This is self-practice, not an automatically scored or timed assessment.</p>
                    </section>
                )}

                {tab === 'flashcards' && (
                    <section className={panelClass}>
                        <h2 className="text-xl font-semibold">Revision flashcards</h2>
                        <p className="mb-5 mt-1 text-sm text-slate-400">Create concise notes and use them for quick revision.</p>
                        {currentCard ? (
                            <button onClick={() => setFlipped(v => !v)}
                                    className="flex min-h-52 w-full flex-col items-center justify-center rounded-2xl border border-indigo-500/30 bg-indigo-500/10 p-7 text-center transition hover:bg-indigo-500/15">
                                <span className="mb-4 text-xs uppercase tracking-widest text-indigo-300">{flipped ? 'Answer' : 'Question'} · Click to flip</span>
                                <span className="text-xl font-semibold">{flipped ? currentCard.back : currentCard.front}</span>
                            </button>
                        ) : <p className="text-slate-400">No flashcards yet. Add one below.</p>}
                        <div className="mt-4 flex flex-wrap gap-3">
                            <button className="rounded-lg border border-slate-700 px-4 py-2 text-sm" onClick={() => { setFlashIndex(i => (i - 1 + cards.length) % Math.max(cards.length, 1)); setFlipped(false) }} disabled={!cards.length}>Previous</button>
                            <button className={buttonClass} onClick={() => { setFlashIndex(i => (i + 1) % Math.max(cards.length, 1)); setFlipped(false) }} disabled={!cards.length}>Next card</button>
                            {currentCard && <button className="rounded-lg border border-red-900 px-4 py-2 text-sm text-red-300" onClick={() => { setCards(old => old.filter(c => c.id !== currentCard.id)); setFlashIndex(0); setFlipped(false) }}>Delete current</button>}
                        </div>
                        <form onSubmit={addCard} className="mt-7 grid gap-3 border-t border-slate-800 pt-5">
                            <h3 className="font-semibold">Add a flashcard</h3>
                            <input className={inputClass} value={front} onChange={e => setFront(e.target.value)} placeholder="Question or concept" required />
                            <textarea className={inputClass} value={back} onChange={e => setBack(e.target.value)} placeholder="Answer or explanation" required />
                            <button className={buttonClass} type="submit">Add flashcard</button>
                        </form>
                    </section>
                )}

                {tab === 'applications' && (
                    <section className={panelClass}>
                        <h2 className="text-xl font-semibold">Job application tracker</h2>
                        <p className="mb-5 mt-1 text-sm text-slate-400">Keep your internship and fresher applications organized.</p>
                        <form onSubmit={addApplication} className="grid gap-3 rounded-xl border border-slate-800 bg-slate-950 p-4 md:grid-cols-2">
                            <input className={inputClass} value={company} onChange={e => setCompany(e.target.value)} placeholder="Company name" required />
                            <input className={inputClass} value={role} onChange={e => setRole(e.target.value)} placeholder="Role title" />
                            <select className={inputClass} value={status} onChange={e => setStatus(e.target.value)}>
                                {['Interested', 'Applied', 'Assessment', 'Interview', 'Offer', 'Rejected'].map(s => <option key={s}>{s}</option>)}
                            </select>
                            <input className={inputClass} type="date" value={date} onChange={e => setDate(e.target.value)} aria-label="Application date" />
                            <button className={`${buttonClass} md:col-span-2`} type="submit">Add application</button>
                        </form>

                        <div className="my-5 grid grid-cols-2 gap-3 sm:grid-cols-3">
                            {['Applied', 'Assessment', 'Interview', 'Offer', 'Rejected', 'Interested'].map(s => (
                                <div key={s} className="rounded-xl border border-slate-800 bg-slate-950 p-3">
                                    <p className="text-xs text-slate-400">{s}</p>
                                    <p className="mt-1 text-2xl font-bold">{applications.filter(a => a.status === s).length}</p>
                                </div>
                            ))}
                        </div>

                        {applications.length === 0 ? (
                            <div className="py-10 text-center text-slate-400">
                                <BriefcaseBusiness className="mx-auto mb-3" size={30} />
                                No applications tracked yet.
                            </div>
                        ) : (
                            <div className="space-y-3">
                                {applications.map(app => (
                                    <article key={app.id} className="flex flex-wrap items-center gap-3 rounded-xl border border-slate-800 p-4">
                                        <div className="min-w-0 flex-1">
                                            <h3 className="font-semibold">{app.company}</h3>
                                            <p className="mt-1 text-sm text-slate-400">{app.role}</p>
                                            <p className="mt-1 text-xs text-slate-500">{app.date}</p>
                                        </div>
                                        <span className="rounded-full bg-indigo-500/15 px-3 py-1 text-xs text-indigo-300">{app.status}</span>
                                        <button className="rounded-lg p-2 text-red-300 hover:bg-red-950/40" aria-label={`Delete ${app.company}`} onClick={() => setApplications(old => old.filter(a => a.id !== app.id))}>
                                            <Trash2 size={17} />
                                        </button>
                                    </article>
                                ))}
                            </div>
                        )}
                    </section>
                )}

                <footer className="flex items-start gap-3 rounded-xl border border-slate-800 p-4 text-sm text-slate-400">
                    <ClipboardList className="mt-0.5 shrink-0 text-indigo-400" size={19} />
                    <p>Your progress is saved in this browser. This version does not yet sync to your backend or across devices. Keep applying consistently and review your weak topics every week.</p>
                </footer>
            </div>
        </main>
    )
}
