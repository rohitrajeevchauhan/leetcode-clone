
import { useCallback, useEffect, useMemo, useState } from 'react'
import axios from 'axios'
import {
    Bell,
    Check,
    CheckCheck,
    Trash2,
    Search,
    RefreshCw,
    AlertCircle,
    Loader2,
    MessageSquare,
    Trophy,
    Code2,
    Info,
    Circle,
    CalendarClock,
} from 'lucide-react'

const API_URL = 'http://localhost:8080/api/platform/notifications'

const getAuthConfig = () => {
    const token = localStorage.getItem('token')

    return {
        headers: token
            ? { Authorization: `Bearer ${token}` }
            : {},
    }
}

const notifyNavbar = () => {
    window.dispatchEvent(new Event('leetcode-notifications-updated'))
}

const getNotificationIcon = (type) => {
    const normalizedType = String(type || '').toLowerCase()

    if (
        normalizedType.includes('contest') ||
        normalizedType.includes('reward') ||
        normalizedType.includes('achievement')
    ) {
        return Trophy
    }

    if (
        normalizedType.includes('discussion') ||
        normalizedType.includes('comment') ||
        normalizedType.includes('message')
    ) {
        return MessageSquare
    }

    if (
        normalizedType.includes('submission') ||
        normalizedType.includes('problem') ||
        normalizedType.includes('code')
    ) {
        return Code2
    }

    if (
        normalizedType.includes('reminder') ||
        normalizedType.includes('daily')
    ) {
        return CalendarClock
    }

    if (normalizedType.includes('system')) {
        return Info
    }

    return Bell
}

const formatDate = (date) => {
    if (!date) return 'Date unavailable'

    const parsedDate = new Date(date)

    if (Number.isNaN(parsedDate.getTime())) {
        return 'Date unavailable'
    }

    return parsedDate.toLocaleString([], {
        dateStyle: 'medium',
        timeStyle: 'short',
    })
}

export default function NotificationsCenter() {
    const [notifications, setNotifications] = useState([])
    const [filter, setFilter] = useState('all')
    const [search, setSearch] = useState('')
    const [loading, setLoading] = useState(true)
    const [refreshing, setRefreshing] = useState(false)
    const [error, setError] = useState('')
    const [busyIds, setBusyIds] = useState([])
    const [actionLoading, setActionLoading] = useState(false)

    const unreadCount = useMemo(
        () => notifications.filter((notification) => !notification.read).length,
        [notifications]
    )

    const loadNotifications = useCallback(async (isRefresh = false) => {
        if (isRefresh) {
            setRefreshing(true)
        } else {
            setLoading(true)
        }

        setError('')

        try {
            const response = await axios.get(API_URL, getAuthConfig())

            const data = Array.isArray(response.data) ? response.data : []

            setNotifications(data)
            notifyNavbar()
        } catch (err) {
            console.error(
                'Failed to load notifications:',
                err.response?.data || err.message
            )

            if (err.response?.status === 401 || err.response?.status === 403) {
                setError(
                    'Authentication failed. Please log in again and check your session.'
                )
            } else {
                setError(
                    'Unable to load notifications. Check your backend connection and try again.'
                )
            }
        } finally {
            setLoading(false)
            setRefreshing(false)
        }
    }, [])

    useEffect(() => {
        loadNotifications()
    }, [loadNotifications])

    const markAsRead = async (notification) => {
        if (notification.read || busyIds.includes(notification.id)) {
            return
        }

        setBusyIds((current) => [...current, notification.id])
        setError('')

        try {
            const response = await axios.put(
                `${API_URL}/${notification.id}/read`,
                {},
                getAuthConfig()
            )

            setNotifications((current) =>
                current.map((item) =>
                    item.id === notification.id
                        ? { ...item, ...response.data, read: true }
                        : item
                )
            )

            notifyNavbar()
        } catch (err) {
            console.error(
                'Failed to mark notification as read:',
                err.response?.data || err.message
            )
            setError('Could not mark this notification as read. Please try again.')
        } finally {
            setBusyIds((current) =>
                current.filter((id) => id !== notification.id)
            )
        }
    }

    const markAllAsRead = async () => {
        if (unreadCount === 0 || actionLoading) return

        setActionLoading(true)
        setError('')

        try {
            await axios.put(`${API_URL}/read-all`, {}, getAuthConfig())

            setNotifications((current) =>
                current.map((notification) => ({
                    ...notification,
                    read: true,
                }))
            )

            notifyNavbar()
        } catch (err) {
            console.error(
                'Failed to mark all notifications as read:',
                err.response?.data || err.message
            )
            setError('Could not mark all notifications as read. Please try again.')
        } finally {
            setActionLoading(false)
        }
    }

    const deleteNotification = async (notificationId) => {
        if (busyIds.includes(notificationId)) return

        setBusyIds((current) => [...current, notificationId])
        setError('')

        try {
            await axios.delete(
                `${API_URL}/${notificationId}`,
                getAuthConfig()
            )

            setNotifications((current) =>
                current.filter((notification) => notification.id !== notificationId)
            )

            notifyNavbar()
        } catch (err) {
            console.error(
                'Failed to delete notification:',
                err.response?.data || err.message
            )
            setError('Could not delete this notification. Please try again.')
        } finally {
            setBusyIds((current) =>
                current.filter((id) => id !== notificationId)
            )
        }
    }

    const filteredNotifications = useMemo(() => {
        const query = search.trim().toLowerCase()

        return notifications.filter((notification) => {
            const matchesFilter =
                filter === 'all' ||
                (filter === 'unread' && !notification.read) ||
                (filter === 'read' && notification.read)

            const searchableText = [
                notification.title,
                notification.message,
                notification.type,
                notification.referenceType,
                notification.referenceId,
            ]
                .filter((value) => value !== null && value !== undefined)
                .join(' ')
                .toLowerCase()

            return matchesFilter && (!query || searchableText.includes(query))
        })
    }, [notifications, filter, search])

    const unreadVisibleCount = filteredNotifications.filter(
        (notification) => !notification.read
    ).length

    return (
        <div className="min-h-screen bg-slate-950 px-4 py-8 text-slate-100 sm:px-6 lg:px-8">
            <div className="mx-auto max-w-5xl">
                {/* Page header */}
                <div className="mb-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
                    <div>
                        <div className="mb-2 flex items-center gap-3">
                            <div className="rounded-xl bg-indigo-500/15 p-3 text-indigo-400">
                                <Bell size={25} />
                            </div>

                            <h1 className="text-3xl font-bold tracking-tight">
                                Notifications
                            </h1>
                        </div>

                        <p className="text-sm text-slate-400">
                            Stay up to date with your LeetClone account activity.
                        </p>

                        <div className="mt-3 flex flex-wrap items-center gap-2 text-sm">
              <span className="rounded-full border border-slate-700 bg-slate-900 px-3 py-1 text-slate-300">
                Total: {notifications.length}
              </span>

                            <span className="rounded-full border border-indigo-500/30 bg-indigo-500/10 px-3 py-1 text-indigo-300">
                Unread: {unreadCount}
              </span>
                        </div>
                    </div>

                    <div className="flex flex-wrap gap-2">
                        <button
                            type="button"
                            onClick={() => loadNotifications(true)}
                            disabled={refreshing}
                            className="flex items-center gap-2 rounded-lg border border-slate-700 px-4 py-2.5 text-sm transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {refreshing ? (
                                <Loader2 size={16} className="animate-spin" />
                            ) : (
                                <RefreshCw size={16} />
                            )}
                            Refresh
                        </button>

                        <button
                            type="button"
                            onClick={markAllAsRead}
                            disabled={unreadCount === 0 || actionLoading}
                            className="flex items-center gap-2 rounded-lg bg-indigo-600 px-4 py-2.5 text-sm font-medium transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {actionLoading ? (
                                <Loader2 size={16} className="animate-spin" />
                            ) : (
                                <CheckCheck size={16} />
                            )}
                            Mark all read
                        </button>
                    </div>
                </div>

                {/* Error message */}
                {error && (
                    <div
                        role="alert"
                        className="mb-5 flex items-start gap-3 rounded-xl border border-red-500/30 bg-red-500/10 p-4 text-sm text-red-300"
                    >
                        <AlertCircle size={19} className="mt-0.5 shrink-0" />

                        <p className="flex-1">{error}</p>

                        <button
                            type="button"
                            onClick={() => setError('')}
                            aria-label="Dismiss error"
                            className="text-red-300 hover:text-white"
                        >
                            ✕
                        </button>
                    </div>
                )}

                {/* Search and filters */}
                <div className="mb-5 rounded-2xl border border-slate-800 bg-slate-900 p-4">
                    <div className="flex flex-col gap-4 md:flex-row">
                        <div className="relative flex-1">
                            <Search
                                size={18}
                                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500"
                            />

                            <input
                                type="text"
                                value={search}
                                onChange={(event) => setSearch(event.target.value)}
                                placeholder="Search notifications..."
                                aria-label="Search notifications"
                                className="w-full rounded-lg border border-slate-700 bg-slate-950 py-2.5 pl-10 pr-3 text-sm text-white outline-none transition placeholder:text-slate-500 focus:border-indigo-500"
                            />
                        </div>

                        <div className="flex flex-wrap gap-2">
                            {[
                                { value: 'all', label: 'All' },
                                { value: 'unread', label: 'Unread' },
                                { value: 'read', label: 'Read' },
                            ].map((option) => (
                                <button
                                    type="button"
                                    key={option.value}
                                    onClick={() => setFilter(option.value)}
                                    aria-pressed={filter === option.value}
                                    className={`rounded-lg px-4 py-2 text-sm transition ${
                                        filter === option.value
                                            ? 'bg-indigo-600 text-white'
                                            : 'border border-slate-700 text-slate-300 hover:bg-slate-800'
                                    }`}
                                >
                                    {option.label}
                                </button>
                            ))}
                        </div>
                    </div>

                    <p className="mt-3 text-xs text-slate-500">
                        Showing {filteredNotifications.length} notifications
                        {filter !== 'all' ? ` · ${filter}` : ''}
                        {search.trim() ? ' · Search applied' : ''}
                    </p>
                </div>

                {/* Notification list */}
                <div className="overflow-hidden rounded-2xl border border-slate-800 bg-slate-900">
                    {loading ? (
                        <div className="flex flex-col items-center justify-center py-20 text-slate-400">
                            <Loader2
                                size={32}
                                className="mb-4 animate-spin text-indigo-400"
                            />
                            <p className="font-medium">Loading notifications...</p>
                            <p className="mt-1 text-sm">Connecting to your account</p>
                        </div>
                    ) : filteredNotifications.length === 0 ? (
                        <div className="flex flex-col items-center justify-center px-6 py-20 text-center">
                            <div className="mb-4 rounded-full bg-slate-800 p-4 text-slate-400">
                                {search || filter !== 'all' ? (
                                    <Search size={30} />
                                ) : (
                                    <Bell size={30} />
                                )}
                            </div>

                            <h2 className="text-lg font-semibold text-white">
                                {notifications.length === 0
                                    ? 'No notifications yet'
                                    : 'No matching notifications'}
                            </h2>

                            <p className="mt-2 max-w-sm text-sm leading-6 text-slate-400">
                                {notifications.length === 0
                                    ? 'When notifications are created for your account, they will appear here.'
                                    : 'Try a different search term or change the selected filter.'}
                            </p>

                            {(search || filter !== 'all') && (
                                <button
                                    type="button"
                                    onClick={() => {
                                        setSearch('')
                                        setFilter('all')
                                    }}
                                    className="mt-4 rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium hover:bg-indigo-500"
                                >
                                    Clear filters
                                </button>
                            )}

                            {error && notifications.length === 0 && (
                                <button
                                    type="button"
                                    onClick={() => loadNotifications(true)}
                                    className="mt-4 rounded-lg border border-slate-700 px-4 py-2 text-sm hover:bg-slate-800"
                                >
                                    Try again
                                </button>
                            )}
                        </div>
                    ) : (
                        <div className="divide-y divide-slate-800">
                            {filteredNotifications.map((notification) => {
                                const Icon = getNotificationIcon(notification.type)
                                const isBusy = busyIds.includes(notification.id)

                                return (
                                    <article
                                        key={notification.id}
                                        className={`flex gap-3 p-4 transition sm:gap-4 sm:p-5 ${
                                            notification.read
                                                ? 'bg-slate-900'
                                                : 'bg-indigo-500/[0.06]'
                                        }`}
                                    >
                                        {/* Type icon */}
                                        <div className="relative mt-1 h-fit shrink-0 rounded-xl bg-slate-800 p-3 text-indigo-400">
                                            <Icon size={21} />

                                            {!notification.read && (
                                                <span className="absolute -right-1 -top-1 h-3 w-3 rounded-full border-2 border-slate-900 bg-indigo-400" />
                                            )}
                                        </div>

                                        {/* Notification details */}
                                        <div className="min-w-0 flex-1">
                                            <div className="flex flex-wrap items-center gap-2">
                                                <h2 className="break-words font-semibold text-slate-100">
                                                    {notification.title || 'Notification'}
                                                </h2>

                                                {!notification.read && (
                                                    <span className="rounded-full bg-indigo-500/15 px-2 py-0.5 text-xs font-medium text-indigo-300">
                            Unread
                          </span>
                                                )}

                                                {notification.type && (
                                                    <span className="rounded-full border border-slate-700 px-2 py-0.5 text-xs text-slate-400">
                            {notification.type}
                          </span>
                                                )}
                                            </div>

                                            <p className="mt-2 whitespace-pre-wrap break-words text-sm leading-6 text-slate-300">
                                                {notification.message || 'No additional details.'}
                                            </p>

                                            <p className="mt-3 text-xs text-slate-500">
                                                {formatDate(notification.createdAt)}
                                            </p>

                                            {notification.referenceType && (
                                                <p className="mt-1 break-words text-xs text-slate-500">
                                                    Reference: {notification.referenceType}
                                                    {notification.referenceId != null
                                                        ? ` #${notification.referenceId}`
                                                        : ''}
                                                </p>
                                            )}
                                        </div>

                                        {/* Actions */}
                                        <div className="flex shrink-0 flex-col items-center gap-2">
                                            {!notification.read ? (
                                                <button
                                                    type="button"
                                                    onClick={() => markAsRead(notification)}
                                                    disabled={isBusy}
                                                    title="Mark as read"
                                                    aria-label={`Mark ${notification.title || 'notification'} as read`}
                                                    className="rounded-lg border border-slate-700 p-2 text-slate-400 transition hover:border-emerald-500 hover:bg-emerald-500/10 hover:text-emerald-400 disabled:cursor-not-allowed disabled:opacity-50"
                                                >
                                                    <Check size={17} />
                                                </button>
                                            ) : (
                                                <span
                                                    title="Already read"
                                                    className="rounded-lg p-2 text-emerald-400"
                                                >
                          <CheckCheck size={17} />
                        </span>
                                            )}

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    deleteNotification(notification.id)
                                                }
                                                disabled={isBusy}
                                                title="Delete notification"
                                                aria-label={`Delete ${notification.title || 'notification'}`}
                                                className="rounded-lg border border-slate-700 p-2 text-slate-400 transition hover:border-red-500 hover:bg-red-500/10 hover:text-red-400 disabled:cursor-not-allowed disabled:opacity-50"
                                            >
                                                <Trash2 size={17} />
                                            </button>

                                            {isBusy && (
                                                <Loader2
                                                    size={15}
                                                    className="animate-spin text-slate-500"
                                                />
                                            )}
                                        </div>
                                    </article>
                                )
                            })}
                        </div>
                    )}
                </div>

                {/* Footer */}
                {!loading && notifications.length > 0 && (
                    <div className="mt-4 flex flex-col gap-2 text-xs text-slate-500 sm:flex-row sm:items-center sm:justify-between">
                        <div className="flex items-center gap-2">
                            <Circle size={8} className="fill-indigo-400 text-indigo-400" />
                            Notifications are stored and managed by your backend.
                        </div>

                        <span>
              {unreadVisibleCount} unread in current view
            </span>
                    </div>
                )}
            </div>
        </div>
    )
}
