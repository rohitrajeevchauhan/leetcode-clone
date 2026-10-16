
import { useEffect, useMemo, useState } from 'react'
import {
    MessageSquare, Users, Code2, Activity, Search, Plus,
    Heart, MessageCircle, Bookmark, Share2, Clock3, Hash,
    UserPlus, UserCheck, Trash2, X, Send, Filter, Copy,
    Braces, Sparkles
} from 'lucide-react'

const STORAGE = {
    posts: 'leetcodeCommunityPosts',
    snippets: 'leetcodeCommunitySnippets',
    follows: 'leetcodeCommunityFollows',
    saved: 'leetcodeCommunitySaved',
    comments: 'leetcodeCommunityComments',
    likes: 'leetcodeCommunityLikes',
}

const seedPosts = [
    {
        id: 'seed-1',
        author: 'CodeNinja',
        title: 'How should I approach Dynamic Programming?',
        body: 'I understand recursion and memoization, but I struggle to identify DP states in new problems. What checklist do you use before writing code?',
        topic: 'DSA',
        tags: ['DP', 'Interview Prep'],
        createdAt: '2026-10-15T10:30:00.000Z',
        likes: 8,
        comments: 2,
        demo: true,
    },
    {
        id: 'seed-2',
        author: 'JavaExplorer',
        title: 'Spring Boot: where should validation happen?',
        body: 'For a REST API, I use Bean Validation annotations on request DTOs and @Valid in controller methods. What other validation patterns have worked well for you?',
        topic: 'Spring Boot',
        tags: ['Java', 'Backend'],
        createdAt: '2026-10-14T12:00:00.000Z',
        likes: 5,
        comments: 3,
        demo: true,
    },
    {
        id: 'seed-3',
        author: 'QueryCraft',
        title: 'SQL question: find the second-highest salary',
        body: 'You can use a MAX subquery or DENSE_RANK() depending on whether the question asks for a distinct salary or employee rows. Remember to clarify duplicate handling.',
        topic: 'SQL',
        tags: ['SQL', 'Interview Prep'],
        createdAt: '2026-10-13T08:00:00.000Z',
        likes: 12,
        comments: 4,
        demo: true,
    },
]

const demoDevelopers = [
    { username: 'CodeNinja', role: 'DSA enthusiast', interests: ['DSA', 'C++'], solved: 320 },
    { username: 'JavaExplorer', role: 'Backend developer', interests: ['Java', 'Spring Boot'], solved: 210 },
    { username: 'QueryCraft', role: 'SQL enthusiast', interests: ['SQL', 'DBMS'], solved: 145 },
    { username: 'GraphGuru', role: 'Problem solver', interests: ['Graphs', 'DP'], solved: 275 },
]

const topics = ['All', 'DSA', 'Java', 'Spring Boot', 'SQL', 'Projects', 'Interview Prep']
const languages = ['Java', 'C++', 'Python', 'JavaScript', 'SQL']

const seedSnippets = [
    {
        id: 'snippet-1',
        title: 'Java frequency map',
        language: 'Java',
        topic: 'Hashing',
        code: 'Map<Integer, Integer> freq = new HashMap<>();\nfor (int value : nums) {\n    freq.put(value, freq.getOrDefault(value, 0) + 1);\n}',
        author: 'JavaExplorer',
        createdAt: '2026-10-14T09:00:00.000Z',
        demo: true,
    },
    {
        id: 'snippet-2',
        title: 'SQL second-highest salary',
        language: 'SQL',
        topic: 'SQL',
        code: 'SELECT MAX(salary)\nFROM Employee\nWHERE salary < (\n    SELECT MAX(salary) FROM Employee\n);',
        author: 'QueryCraft',
        createdAt: '2026-10-12T09:00:00.000Z',
        demo: true,
    },
    {
        id: 'snippet-3',
        title: 'Two-pointer template',
        language: 'Java',
        topic: 'Arrays',
        code: 'int left = 0, right = nums.length - 1;\nwhile (left < right) {\n    int sum = nums[left] + nums[right];\n    if (sum == target) break;\n    if (sum < target) left++;\n    else right--;\n}',
        author: 'CodeNinja',
        createdAt: '2026-10-11T09:00:00.000Z',
        demo: true,
    },
]

const panel = 'rounded-2xl border border-slate-800 bg-slate-900'
const inputClass = 'w-full rounded-xl border border-slate-700 bg-slate-950 px-3 py-2.5 text-sm text-white outline-none placeholder:text-slate-500 focus:border-indigo-500'
const primaryButton = 'inline-flex items-center justify-center gap-2 rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-50'
const secondaryButton = 'inline-flex items-center justify-center gap-2 rounded-xl border border-slate-700 px-3 py-2 text-sm text-slate-300 transition hover:bg-slate-800'

function readStorage(key, fallback) {
    try {
        const raw = localStorage.getItem(key)
        if (!raw) return fallback
        const parsed = JSON.parse(raw)
        return parsed ?? fallback
    } catch {
        return fallback
    }
}

function currentUsername() {
    return localStorage.getItem('username') || 'You'
}

function getInitials(name) {
    return String(name || '?')
        .split(/[\s_-]+/)
        .filter(Boolean)
        .slice(0, 2)
        .map(part => part[0].toUpperCase())
        .join('')
}

function timeAgo(value) {
    const date = new Date(value)
    if (Number.isNaN(date.getTime())) return 'Recently'

    const minutes = Math.max(0, Math.floor((Date.now() - date.getTime()) / 60000))
    if (minutes < 1) return 'Just now'
    if (minutes < 60) return `${minutes}m ago`

    const hours = Math.floor(minutes / 60)
    if (hours < 24) return `${hours}h ago`

    const days = Math.floor(hours / 24)
    if (days < 7) return `${days}d ago`
    return date.toLocaleDateString()
}

function Avatar({ name, small = false }) {
    return (
        <div className={`flex shrink-0 items-center justify-center rounded-full bg-indigo-500/15 font-semibold text-indigo-300 ${
            small ? 'h-9 w-9 text-xs' : 'h-11 w-11 text-sm'
        }`}>
            {getInitials(name)}
        </div>
    )
}

function SectionTitle({ icon: Icon, title, description, action }) {
    return (
        <div className="mb-5 flex flex-wrap items-start justify-between gap-3">
            <div className="flex items-start gap-3">
                <div className="rounded-xl bg-indigo-500/10 p-2.5">
                    <Icon size={21} className="text-indigo-400" />
                </div>
                <div>
                    <h2 className="text-lg font-semibold text-white">{title}</h2>
                    <p className="mt-1 text-sm text-slate-400">{description}</p>
                </div>
            </div>
            {action}
        </div>
    )
}

function Tag({ children, onClick, active = false }) {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`rounded-full border px-3 py-1 text-xs transition ${
                active
                    ? 'border-indigo-500 bg-indigo-500/15 text-indigo-300'
                    : 'border-slate-700 bg-slate-950 text-slate-400 hover:border-slate-500'
            }`}
        >
            {children}
        </button>
    )
}

export default function CommunityCenter() {
    const username = currentUsername()

    const [tab, setTab] = useState('discussions')
    const [posts, setPosts] = useState(() => readStorage(STORAGE.posts, seedPosts))
    const [snippets, setSnippets] = useState(() => readStorage(STORAGE.snippets, seedSnippets))
    const [follows, setFollows] = useState(() => readStorage(STORAGE.follows, []))
    const [savedPosts, setSavedPosts] = useState(() => readStorage(STORAGE.saved, []))
    const [comments, setComments] = useState(() => readStorage(STORAGE.comments, {}))
    const [likedPosts, setLikedPosts] = useState(() => readStorage(STORAGE.likes, []))

    const [search, setSearch] = useState('')
    const [activeTopic, setActiveTopic] = useState('All')
    const [sortBy, setSortBy] = useState('recent')
    const [showPostForm, setShowPostForm] = useState(false)
    const [showSnippetForm, setShowSnippetForm] = useState(false)
    const [postTitle, setPostTitle] = useState('')
    const [postBody, setPostBody] = useState('')
    const [postTopic, setPostTopic] = useState('DSA')
    const [postTags, setPostTags] = useState('')
    const [snippetTitle, setSnippetTitle] = useState('')
    const [snippetLanguage, setSnippetLanguage] = useState('Java')
    const [snippetTopic, setSnippetTopic] = useState('')
    const [snippetCode, setSnippetCode] = useState('')
    const [snippetSearch, setSnippetSearch] = useState('')
    const [expandedComments, setExpandedComments] = useState([])
    const [commentDrafts, setCommentDrafts] = useState({})
    const [notice, setNotice] = useState('')

    useEffect(() => {
        try { localStorage.setItem(STORAGE.posts, JSON.stringify(posts)) } catch {}
    }, [posts])

    useEffect(() => {
        try { localStorage.setItem(STORAGE.snippets, JSON.stringify(snippets)) } catch {}
    }, [snippets])

    useEffect(() => {
        try { localStorage.setItem(STORAGE.follows, JSON.stringify(follows)) } catch {}
    }, [follows])

    useEffect(() => {
        try { localStorage.setItem(STORAGE.saved, JSON.stringify(savedPosts)) } catch {}
    }, [savedPosts])

    useEffect(() => {
        try { localStorage.setItem(STORAGE.comments, JSON.stringify(comments)) } catch {}
    }, [comments])

    useEffect(() => {
        try { localStorage.setItem(STORAGE.likes, JSON.stringify(likedPosts)) } catch {}
    }, [likedPosts])

    const filteredPosts = useMemo(() => {
        const term = search.trim().toLowerCase()

        return posts
            .filter(post => {
                const searchable = [
                    post.title,
                    post.body,
                    post.author,
                    post.topic,
                    ...(post.tags || []),
                ].join(' ').toLowerCase()

                const matchesSearch = !term || searchable.includes(term)
                const matchesTopic = activeTopic === 'All'
                    || post.topic === activeTopic
                    || (post.tags || []).includes(activeTopic)

                return matchesSearch && matchesTopic
            })
            .sort((a, b) => sortBy === 'popular'
                ? (b.likes || 0) - (a.likes || 0)
                : new Date(b.createdAt) - new Date(a.createdAt))
    }, [posts, search, activeTopic, sortBy])

    const filteredSnippets = useMemo(() => {
        const term = snippetSearch.trim().toLowerCase()

        return snippets.filter(snippet => {
            const searchable = [
                snippet.title,
                snippet.language,
                snippet.topic,
                snippet.author,
                snippet.code,
            ].join(' ').toLowerCase()

            const matchesSearch = !term || searchable.includes(term)
            const matchesTopic = activeTopic === 'All'
                || snippet.topic === activeTopic
                || snippet.language === activeTopic

            return matchesSearch && matchesTopic
        }).sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    }, [snippets, snippetSearch, activeTopic])

    const followingFeed = useMemo(
        () => posts.filter(post => follows.includes(post.author)),
        [posts, follows]
    )

    function createPost(event) {
        event.preventDefault()

        if (!postTitle.trim() || !postBody.trim()) return

        const tags = postTags
            .split(',')
            .map(tag => tag.trim())
            .filter(Boolean)
            .slice(0, 5)

        const newPost = {
            id: `post-${Date.now()}`,
            author: username,
            title: postTitle.trim(),
            body: postBody.trim(),
            topic: postTopic,
            tags,
            createdAt: new Date().toISOString(),
            likes: 0,
            comments: 0,
            demo: false,
        }

        setPosts(old => [newPost, ...old])
        setPostTitle('')
        setPostBody('')
        setPostTags('')
        setPostTopic('DSA')
        setShowPostForm(false)
        setTab('discussions')
        setNotice('Discussion created successfully.')
    }

    function createSnippet(event) {
        event.preventDefault()

        if (!snippetTitle.trim() || !snippetCode.trim()) return

        const newSnippet = {
            id: `snippet-${Date.now()}`,
            title: snippetTitle.trim(),
            language: snippetLanguage,
            topic: snippetTopic.trim() || snippetLanguage,
            code: snippetCode.trim(),
            author: username,
            createdAt: new Date().toISOString(),
            demo: false,
        }

        setSnippets(old => [newSnippet, ...old])
        setSnippetTitle('')
        setSnippetLanguage('Java')
        setSnippetTopic('')
        setSnippetCode('')
        setShowSnippetForm(false)
        setTab('snippets')
        setNotice('Code snippet added successfully.')
    }

    function toggleLike(postId) {
        const alreadyLiked = likedPosts.includes(postId)

        setLikedPosts(old => alreadyLiked
            ? old.filter(id => id !== postId)
            : [...old, postId])

        setPosts(old => old.map(post => {
            if (post.id !== postId) return post
            return {
                ...post,
                likes: Math.max(0, (post.likes || 0) + (alreadyLiked ? -1 : 1)),
            }
        }))
    }

    function toggleFollow(name) {
        if (name === username) return
        setFollows(old => old.includes(name)
            ? old.filter(item => item !== name)
            : [...old, name])
    }

    function toggleSave(postId) {
        setSavedPosts(old => old.includes(postId)
            ? old.filter(id => id !== postId)
            : [...old, postId])
    }

    function toggleComments(postId) {
        setExpandedComments(old => old.includes(postId)
            ? old.filter(id => id !== postId)
            : [...old, postId])
    }

    function addComment(event, postId) {
        event.preventDefault()
        const message = (commentDrafts[postId] || '').trim()
        if (!message) return

        const newComment = {
            id: `comment-${Date.now()}`,
            author: username,
            body: message,
            createdAt: new Date().toISOString(),
        }

        setComments(old => ({
            ...old,
            [postId]: [...(old[postId] || []), newComment],
        }))
        setPosts(old => old.map(post => post.id === postId
            ? { ...post, comments: (post.comments || 0) + 1 }
            : post))
        setCommentDrafts(old => ({ ...old, [postId]: '' }))
    }

    async function copySnippet(code) {
        try {
            await navigator.clipboard.writeText(code)
            setNotice('Code copied to clipboard.')
        } catch {
            setNotice('Clipboard access was unavailable. Select and copy the code manually.')
        }
    }

    function deleteOwnPost(post) {
        if (post.author !== username || post.demo) return
        if (!window.confirm('Delete this discussion?')) return
        setPosts(old => old.filter(item => item.id !== post.id))
        setSavedPosts(old => old.filter(id => id !== post.id))
        setLikedPosts(old => old.filter(id => id !== post.id))
    }

    function deleteOwnSnippet(snippet) {
        if (snippet.author !== username || snippet.demo) return
        if (!window.confirm('Delete this snippet?')) return
        setSnippets(old => old.filter(item => item.id !== snippet.id))
    }

    const tabs = [
        { id: 'discussions', label: 'Discussions', icon: MessageSquare },
        { id: 'developers', label: 'Developers', icon: Users },
        { id: 'snippets', label: 'Code Snippets', icon: Code2 },
        { id: 'feed', label: 'Activity Feed', icon: Activity },
        { id: 'saved', label: 'Saved Posts', icon: Bookmark },
    ]

    function renderPost(post) {
        const hasLiked = likedPosts.includes(post.id)
        const postComments = comments[post.id] || []
        const commentsOpen = expandedComments.includes(post.id)
        const isSaved = savedPosts.includes(post.id)

        return (
            <article key={post.id} className={`${panel} p-5`}>
                <div className="flex items-start gap-3">
                    <Avatar name={post.author} />
                    <div className="min-w-0 flex-1">
                        <div className="flex flex-wrap items-center gap-x-2 gap-y-1">
                            <span className="font-semibold text-white">{post.author}</span>
                            <span className="text-xs text-slate-500">· {timeAgo(post.createdAt)}</span>
                            {post.demo && <span className="rounded-full bg-slate-800 px-2 py-0.5 text-[10px] text-slate-400">SAMPLE</span>}
                        </div>
                        <div className="mt-2 flex flex-wrap gap-2">
                            <Tag active onClick={() => setActiveTopic(post.topic)}>{post.topic}</Tag>
                            {(post.tags || []).map(tag => (
                                <Tag key={tag} onClick={() => { setSearch(tag); setActiveTopic('All') }}>#{tag}</Tag>
                            ))}
                        </div>
                    </div>
                    {post.author === username && !post.demo && (
                        <button className="rounded-lg p-2 text-slate-500 hover:bg-slate-800 hover:text-red-300" aria-label="Delete discussion" onClick={() => deleteOwnPost(post)}>
                            <Trash2 size={16} />
                        </button>
                    )}
                </div>

                <h3 className="mt-4 text-lg font-semibold leading-7 text-white">{post.title}</h3>
                <p className="mt-2 whitespace-pre-wrap text-sm leading-7 text-slate-300">{post.body}</p>

                <div className="mt-5 flex flex-wrap items-center gap-2 border-t border-slate-800 pt-4">
                    <button onClick={() => toggleLike(post.id)} className={`${secondaryButton} ${hasLiked ? 'border-pink-500/40 text-pink-300' : ''}`}>
                        <Heart size={16} fill={hasLiked ? 'currentColor' : 'none'} /> {post.likes || 0}
                    </button>
                    <button onClick={() => toggleComments(post.id)} className={secondaryButton}>
                        <MessageCircle size={16} /> {post.comments || 0} comments
                    </button>
                    <button onClick={() => toggleSave(post.id)} className={`${secondaryButton} ${isSaved ? 'text-indigo-300' : ''}`}>
                        <Bookmark size={16} fill={isSaved ? 'currentColor' : 'none'} /> {isSaved ? 'Saved' : 'Save'}
                    </button>
                    <button onClick={() => {
                        setNotice('Share this discussion by copying its title: ' + post.title)
                        if (navigator.clipboard?.writeText) {
                            navigator.clipboard.writeText(post.title).catch(() => {})
                        }
                    }} className={secondaryButton}>
                        <Share2 size={16} /> Share
                    </button>
                </div>

                {commentsOpen && (
                    <div className="mt-4 space-y-3 border-t border-slate-800 pt-4">
                        {postComments.length === 0 && <p className="text-sm text-slate-500">No comments yet. Start the conversation.</p>}
                        {postComments.map(comment => (
                            <div key={comment.id} className="flex items-start gap-2 rounded-xl bg-slate-950 p-3">
                                <Avatar name={comment.author} small />
                                <div>
                                    <p className="text-sm font-semibold">{comment.author} <span className="ml-1 text-xs font-normal text-slate-500">{timeAgo(comment.createdAt)}</span></p>
                                    <p className="mt-1 whitespace-pre-wrap text-sm leading-6 text-slate-300">{comment.body}</p>
                                </div>
                            </div>
                        ))}
                        <form onSubmit={event => addComment(event, post.id)} className="flex gap-2">
                            <input
                                className={inputClass}
                                value={commentDrafts[post.id] || ''}
                                onChange={event => setCommentDrafts(old => ({ ...old, [post.id]: event.target.value }))}
                                placeholder="Write a comment..."
                                aria-label="Write a comment"
                            />
                            <button type="submit" className={primaryButton} aria-label="Send comment"><Send size={16} /></button>
                        </form>
                    </div>
                )}
            </article>
        )
    }

    return (
        <main className="min-h-screen bg-slate-950 px-4 py-8 text-slate-100 sm:px-8">
            <div className="mx-auto max-w-7xl space-y-7">
                <header className="flex flex-col justify-between gap-5 lg:flex-row lg:items-center">
                    <div>
                        <p className="mb-2 text-sm font-semibold uppercase tracking-widest text-indigo-400">LeetClone Community</p>
                        <h1 className="text-3xl font-bold sm:text-4xl">Community Center</h1>
                        <p className="mt-2 max-w-2xl text-slate-400">
                            Share what you learn, ask questions, connect with developers, and build your coding knowledge together.
                        </p>
                    </div>
                    <div className="flex flex-wrap gap-2">
                        <button className={secondaryButton} onClick={() => { setShowSnippetForm(true); setTab('snippets') }}>
                            <Code2 size={16} /> Share code
                        </button>
                        <button className={primaryButton} onClick={() => { setShowPostForm(true); setTab('discussions') }}>
                            <Plus size={17} /> New discussion
                        </button>
                    </div>
                </header>

                {notice && (
                    <div className="flex items-start justify-between gap-3 rounded-xl border border-indigo-500/30 bg-indigo-500/10 p-3 text-sm text-indigo-200">
                        <span>{notice}</span>
                        <button onClick={() => setNotice('')} aria-label="Dismiss message"><X size={16} /></button>
                    </div>
                )}

                <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                    <div className={`${panel} p-4`}>
                        <div className="flex items-center gap-3"><MessageSquare className="text-indigo-400" size={20} /><span className="text-sm text-slate-400">Discussions</span></div>
                        <p className="mt-3 text-2xl font-bold">{posts.length}</p>
                        <p className="mt-1 text-xs text-slate-500">Community topics</p>
                    </div>
                    <div className={`${panel} p-4`}>
                        <div className="flex items-center gap-3"><Code2 className="text-indigo-400" size={20} /><span className="text-sm text-slate-400">Code snippets</span></div>
                        <p className="mt-3 text-2xl font-bold">{snippets.length}</p>
                        <p className="mt-1 text-xs text-slate-500">Reusable examples</p>
                    </div>
                    <div className={`${panel} p-4`}>
                        <div className="flex items-center gap-3"><UserPlus className="text-indigo-400" size={20} /><span className="text-sm text-slate-400">Following</span></div>
                        <p className="mt-3 text-2xl font-bold">{follows.length}</p>
                        <p className="mt-1 text-xs text-slate-500">Developers followed</p>
                    </div>
                    <div className={`${panel} p-4`}>
                        <div className="flex items-center gap-3"><Bookmark className="text-indigo-400" size={20} /><span className="text-sm text-slate-400">Saved posts</span></div>
                        <p className="mt-3 text-2xl font-bold">{savedPosts.length}</p>
                        <p className="mt-1 text-xs text-slate-500">For later reading</p>
                    </div>
                </section>

                <nav className="flex gap-2 overflow-x-auto pb-2">
                    {tabs.map(({ id, label, icon: Icon }) => (
                        <button key={id} onClick={() => setTab(id)} className={`flex shrink-0 items-center gap-2 rounded-xl px-4 py-3 text-sm font-medium transition ${
                            tab === id ? 'bg-indigo-600 text-white' : 'border border-slate-800 bg-slate-900 text-slate-300 hover:bg-slate-800'
                        }`}>
                            <Icon size={17} /> {label}
                        </button>
                    ))}
                </nav>

                {showPostForm && (
                    <section className={`${panel} p-5`}>
                        <div className="mb-5 flex items-center justify-between">
                            <div>
                                <h2 className="text-lg font-semibold">Create a discussion</h2>
                                <p className="mt-1 text-sm text-slate-400">Ask a question or share something you learned.</p>
                            </div>
                            <button className={secondaryButton} onClick={() => setShowPostForm(false)}><X size={16} /> Close</button>
                        </div>
                        <form onSubmit={createPost} className="space-y-4">
                            <input className={inputClass} value={postTitle} onChange={event => setPostTitle(event.target.value)} placeholder="Discussion title" maxLength={140} required />
                            <textarea className={`${inputClass} min-h-36 resize-y`} value={postBody} onChange={event => setPostBody(event.target.value)} placeholder="Explain your question or idea..." maxLength={5000} required />
                            <div className="grid gap-3 sm:grid-cols-2">
                                <select className={inputClass} value={postTopic} onChange={event => setPostTopic(event.target.value)}>
                                    {topics.filter(topic => topic !== 'All').map(topic => <option key={topic}>{topic}</option>)}
                                </select>
                                <input className={inputClass} value={postTags} onChange={event => setPostTags(event.target.value)} placeholder="Tags, comma-separated (e.g. DP, Java)" />
                            </div>
                            <button className={primaryButton} type="submit"><Plus size={16} /> Publish discussion</button>
                        </form>
                    </section>
                )}

                {tab === 'discussions' && (
                    <section className="space-y-5">
                        <div className={`${panel} p-4`}>
                            <div className="flex flex-col gap-3 lg:flex-row">
                                <div className="relative flex-1">
                                    <Search size={17} className="absolute left-3 top-3 text-slate-500" />
                                    <input className={`${inputClass} pl-10`} value={search} onChange={event => setSearch(event.target.value)} placeholder="Search discussions, authors, tags..." />
                                </div>
                                <div className="flex flex-wrap gap-2">
                                    <select className={inputClass} value={activeTopic} onChange={event => setActiveTopic(event.target.value)} aria-label="Filter by topic">
                                        {topics.map(topic => <option key={topic}>{topic}</option>)}
                                    </select>
                                    <select className={inputClass} value={sortBy} onChange={event => setSortBy(event.target.value)} aria-label="Sort discussions">
                                        <option value="recent">Most recent</option>
                                        <option value="popular">Most liked</option>
                                    </select>
                                </div>
                            </div>
                            <div className="mt-4 flex flex-wrap gap-2">
                                {topics.map(topic => <Tag key={topic} active={activeTopic === topic} onClick={() => setActiveTopic(topic)}>{topic}</Tag>)}
                            </div>
                        </div>
                        {filteredPosts.length ? filteredPosts.map(renderPost) : (
                            <div className={`${panel} p-10 text-center`}>
                                <Search className="mx-auto mb-3 text-slate-500" size={28} />
                                <h3 className="font-semibold">No discussions found</h3>
                                <p className="mt-2 text-sm text-slate-400">Try another keyword or create a new discussion.</p>
                                <button className={`${primaryButton} mt-4`} onClick={() => setShowPostForm(true)}><Plus size={16} /> Start a discussion</button>
                            </div>
                        )}
                    </section>
                )}

                {tab === 'developers' && (
                    <section className="space-y-5">
                        <div className={`${panel} p-4`}>
                            <SectionTitle icon={Users} title="Developer directory" description="Discover sample community profiles by coding interests." />
                            <div className="relative">
                                <Search size={17} className="absolute left-3 top-3 text-slate-500" />
                                <input className={`${inputClass} pl-10`} value={search} onChange={event => setSearch(event.target.value)} placeholder="Search developers or interests..." />
                            </div>
                        </div>
                        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                            {demoDevelopers.filter(developer => [
                                developer.username, developer.role, ...developer.interests,
                            ].join(' ').toLowerCase().includes(search.toLowerCase())).map(developer => {
                                const isFollowing = follows.includes(developer.username)
                                return (
                                    <article key={developer.username} className={`${panel} p-5`}>
                                        <div className="flex items-center gap-3">
                                            <Avatar name={developer.username} />
                                            <div className="min-w-0 flex-1">
                                                <h3 className="truncate font-semibold">{developer.username}</h3>
                                                <p className="mt-1 text-sm text-slate-400">{developer.role}</p>
                                            </div>
                                        </div>
                                        <div className="my-4 flex flex-wrap gap-2">
                                            {developer.interests.map(interest => <Tag key={interest} onClick={() => { setActiveTopic(interest); setTab('discussions') }}>{interest}</Tag>)}
                                        </div>
                                        <div className="rounded-xl bg-slate-950 p-3">
                                            <p className="text-xl font-bold">{developer.solved}</p>
                                            <p className="text-xs text-slate-500">Sample solved-problem count</p>
                                        </div>
                                        <button className={`${isFollowing ? secondaryButton : primaryButton} mt-4 w-full`} onClick={() => toggleFollow(developer.username)}>
                                            {isFollowing ? <><UserCheck size={16} /> Following</> : <><UserPlus size={16} /> Follow developer</>}
                                        </button>
                                    </article>
                                )
                            })}
                        </div>
                        <p className="text-xs text-slate-500">These are demo profiles, not verified registered users. Your own username is read from localStorage.</p>
                    </section>
                )}

                {tab === 'snippets' && (
                    <section className="space-y-5">
                        {showSnippetForm && (
                            <div className={`${panel} p-5`}>
                                <div className="mb-5 flex items-center justify-between">
                                    <div>
                                        <h2 className="text-lg font-semibold">Share a code snippet</h2>
                                        <p className="mt-1 text-sm text-slate-400">Add a reusable code pattern for the community.</p>
                                    </div>
                                    <button className={secondaryButton} onClick={() => setShowSnippetForm(false)}><X size={16} /> Close</button>
                                </div>
                                <form onSubmit={createSnippet} className="space-y-4">
                                    <input className={inputClass} value={snippetTitle} onChange={event => setSnippetTitle(event.target.value)} placeholder="Snippet title" maxLength={120} required />
                                    <div className="grid gap-3 sm:grid-cols-2">
                                        <select className={inputClass} value={snippetLanguage} onChange={event => setSnippetLanguage(event.target.value)}>
                                            {languages.map(language => <option key={language}>{language}</option>)}
                                        </select>
                                        <input className={inputClass} value={snippetTopic} onChange={event => setSnippetTopic(event.target.value)} placeholder="Topic (e.g. Two Pointers)" />
                                    </div>
                                    <textarea className={`${inputClass} min-h-48 resize-y font-mono`} value={snippetCode} onChange={event => setSnippetCode(event.target.value)} placeholder="Paste your code here..." maxLength={15000} required />
                                    <button className={primaryButton} type="submit"><Code2 size={16} /> Publish snippet</button>
                                </form>
                            </div>
                        )}
                        <div className={`${panel} p-4`}>
                            <div className="flex flex-col gap-3 sm:flex-row">
                                <div className="relative flex-1">
                                    <Search size={17} className="absolute left-3 top-3 text-slate-500" />
                                    <input className={`${inputClass} pl-10`} value={snippetSearch} onChange={event => setSnippetSearch(event.target.value)} placeholder="Search snippets, language, or topic..." />
                                </div>
                                <button className={primaryButton} onClick={() => setShowSnippetForm(true)}><Plus size={16} /> Add snippet</button>
                            </div>
                            <div className="mt-3 flex flex-wrap gap-2">
                                {topics.map(topic => <Tag key={topic} active={activeTopic === topic} onClick={() => setActiveTopic(topic)}>{topic}</Tag>)}
                                <button className="text-xs text-slate-400 underline" onClick={() => setActiveTopic('All')}>Clear topic filter</button>
                            </div>
                        </div>
                        {filteredSnippets.length ? filteredSnippets.map(snippet => (
                            <article key={snippet.id} className={`${panel} overflow-hidden`}>
                                <div className="flex flex-wrap items-start justify-between gap-3 p-5">
                                    <div className="flex items-start gap-3">
                                        <div className="rounded-xl bg-indigo-500/10 p-2.5"><Braces size={21} className="text-indigo-400" /></div>
                                        <div>
                                            <h3 className="font-semibold">{snippet.title}</h3>
                                            <p className="mt-1 text-sm text-slate-400">By {snippet.author} · {timeAgo(snippet.createdAt)}</p>
                                            <div className="mt-2 flex flex-wrap gap-2">
                                                <Tag>{snippet.language}</Tag>
                                                <Tag>{snippet.topic}</Tag>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex gap-2">
                                        <button className={secondaryButton} onClick={() => copySnippet(snippet.code)}><Copy size={15} /> Copy</button>
                                        {snippet.author === username && !snippet.demo && (
                                            <button className={secondaryButton} onClick={() => deleteOwnSnippet(snippet)} aria-label="Delete snippet"><Trash2 size={15} /></button>
                                        )}
                                    </div>
                                </div>
                                <pre className="overflow-x-auto border-t border-slate-800 bg-slate-950 p-5 text-sm leading-6 text-emerald-200"><code>{snippet.code}</code></pre>
                            </article>
                        )) : (
                            <div className={`${panel} p-10 text-center text-slate-400`}>No matching snippets. Try another search or add a snippet.</div>
                        )}
                    </section>
                )}

                {tab === 'feed' && (
                    <section className="space-y-5">
                        <div className={`${panel} p-5`}>
                            <SectionTitle icon={Activity} title="Community activity" description="Browse recent discussions and activity from developers you follow." />
                            <div className="flex flex-wrap gap-2">
                                <button className={secondaryButton} onClick={() => setActiveTopic('All')}><Filter size={15} /> All topics</button>
                                <button className={secondaryButton} onClick={() => setSearch('')}>Clear search</button>
                            </div>
                        </div>
                        {followingFeed.length > 0 && (
                            <div className={`${panel} p-5`}>
                                <h3 className="mb-3 font-semibold">From developers you follow</h3>
                                <div className="space-y-3">
                                    {followingFeed.map(post => (
                                        <button key={post.id} onClick={() => setTab('discussions')} className="flex w-full items-start gap-3 rounded-xl bg-slate-950 p-3 text-left hover:bg-slate-800">
                                            <Avatar name={post.author} small />
                                            <div className="min-w-0 flex-1">
                                                <p className="font-medium">{post.title}</p>
                                                <p className="mt-1 text-xs text-slate-400">{post.author} · {timeAgo(post.createdAt)}</p>
                                            </div>
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}
                        <div className={`${panel} p-5`}>
                            <h3 className="mb-4 font-semibold">Recent activity</h3>
                            <div className="space-y-4">
                                {[...posts].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, 12).map(post => (
                                    <div key={post.id} className="flex items-start gap-3">
                                        <Avatar name={post.author} small />
                                        <div className="min-w-0 flex-1">
                                            <p className="text-sm leading-6">
                                                <span className="font-semibold">{post.author}</span>
                                                <span className="text-slate-400"> posted in </span>
                                                <button onClick={() => { setActiveTopic(post.topic); setTab('discussions') }} className="font-medium text-indigo-300 hover:underline">{post.topic}</button>
                                            </p>
                                            <button onClick={() => setTab('discussions')} className="mt-1 text-left font-medium text-slate-200 hover:text-indigo-300">{post.title}</button>
                                            <div className="mt-2 flex items-center gap-4 text-xs text-slate-500">
                                                <span className="flex items-center gap-1"><Heart size={13} /> {post.likes || 0}</span>
                                                <span className="flex items-center gap-1"><MessageCircle size={13} /> {post.comments || 0}</span>
                                                <span className="flex items-center gap-1"><Clock3 size={13} /> {timeAgo(post.createdAt)}</span>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </section>
                )}

                {tab === 'saved' && (
                    <section className="space-y-4">
                        <div className={`${panel} p-5`}>
                            <SectionTitle icon={Bookmark} title="Saved discussions" description="Keep useful discussions here for later revision." />
                        </div>
                        {posts.filter(post => savedPosts.includes(post.id)).length
                            ? posts.filter(post => savedPosts.includes(post.id)).map(renderPost)
                            : (
                                <div className={`${panel} p-10 text-center`}>
                                    <Bookmark size={28} className="mx-auto mb-3 text-slate-500" />
                                    <h3 className="font-semibold">Nothing saved yet</h3>
                                    <p className="mt-2 text-sm text-slate-400">Save a discussion to find it here later.</p>
                                    <button className={`${primaryButton} mt-4`} onClick={() => setTab('discussions')}>Browse discussions</button>
                                </div>
                            )}
                    </section>
                )}

                <footer className="flex items-start gap-3 rounded-xl border border-slate-800 p-4 text-sm text-slate-400">
                    <Sparkles size={18} className="mt-0.5 shrink-0 text-indigo-400" />
                    <p>
                        Frontend prototype: discussions, comments, likes, follows, saved posts, and snippets are stored in this browser. Sample profiles and posts are demonstration content. For a real community, connect these actions to authenticated backend APIs and add server-side validation and authorization.
                    </p>
                </footer>
            </div>
        </main>
    )
}
