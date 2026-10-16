import { useCallback, useEffect, useState } from 'react'
import { Link, NavLink, useLocation, useNavigate } from 'react-router-dom'
import axios from 'axios'
import {
  Bell,
  Menu,
  X,
  LogOut,
  Code2,
  User,
} from 'lucide-react'

const API_URL = '/api/platform/notifications/unread/count'

export default function Navbar() {
  const navigate = useNavigate()
  const location = useLocation()

  const [username, setUsername] = useState(
      () => localStorage.getItem('username') || 'User'
  )
  const [unreadNotifications, setUnreadNotifications] = useState(0)
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const fetchUnreadCount = useCallback(async () => {
    const token = localStorage.getItem('token')

    if (!token) {
      setUnreadNotifications(0)
      return
    }

    try {
      const response = await axios.get(API_URL, {
        headers: {
          Authorization: 'Bearer ' + token,
        },
      })

      setUnreadNotifications(Number(response.data) || 0)
    } catch (error) {
      console.error(
          'Failed to fetch unread notifications:',
          error.response?.data || error.message
      )
    }
  }, [])

  useEffect(() => {
    const updateUsername = () => {
      setUsername(localStorage.getItem('username') || 'User')
    }

    updateUsername()
    fetchUnreadCount()

    const handleNotificationUpdate = () => {
      fetchUnreadCount()
    }

    const handleStorageChange = (event) => {
      if (
          !event ||
          event.key === 'token' ||
          event.key === 'username'
      ) {
        updateUsername()
        fetchUnreadCount()
      }
    }

    window.addEventListener(
        'leetcode-notifications-updated',
        handleNotificationUpdate
    )
    window.addEventListener('focus', handleNotificationUpdate)
    window.addEventListener('storage', handleStorageChange)

    return () => {
      window.removeEventListener(
          'leetcode-notifications-updated',
          handleNotificationUpdate
      )
      window.removeEventListener('focus', handleNotificationUpdate)
      window.removeEventListener('storage', handleStorageChange)
    }
  }, [fetchUnreadCount])

  useEffect(() => {
    setMobileMenuOpen(false)
  }, [location.pathname])

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('email')
    localStorage.removeItem('role')
    localStorage.removeItem('user')

    setUnreadNotifications(0)
    setMobileMenuOpen(false)

    navigate('/login')
  }

  const navLinks = [
    { name: 'Dashboard', path: '/dashboard' },
    { name: 'Problems', path: '/problems' },
    { name: 'My Submissions', path: '/submissions' },
    { name: 'Progress', path: '/progress' },
    { name: 'Learning Hub', path: '/learning-hub' },
    { name: 'Interview Prep', path: '/interview-prep' },
    { name: 'Analytics', path: '/analytics' },
    { name: 'My Profile', path: '/profile' },
    { name: 'Contests & Rewards', path: '/gamification' },
    { name: 'Community', path: '/community' },
  ]

  const closeMobileMenu = () => {
    setMobileMenuOpen(false)
  }

  const linkClass = ({ isActive }) => {
    return (
        'whitespace-nowrap rounded-lg px-3 py-2 text-sm font-medium transition-colors ' +
        (isActive
            ? 'bg-purple-500/15 text-cyan-300 ring-1 ring-purple-400/20'
            : 'text-slate-400 hover:bg-slate-800/80 hover:text-white')
    )
  }

  const NotificationButton = () => (
      <Link
          to="/notifications"
          aria-label={'Notifications: ' + unreadNotifications + ' unread'}
          title="Notifications"
          className="relative rounded-xl border border-slate-700/70 bg-slate-900 p-2.5 text-slate-300 transition hover:border-cyan-400/40 hover:text-cyan-300"
      >
        <Bell size={19} />

        {unreadNotifications > 0 && (
            <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-purple-500 px-1 text-[10px] font-bold text-white ring-2 ring-slate-950">
          {unreadNotifications > 99 ? '99+' : unreadNotifications}
        </span>
        )}
      </Link>
  )

  return (
      <nav className="sticky top-0 z-50 border-b border-slate-800/80 bg-slate-950/95 text-white shadow-xl shadow-black/20 backdrop-blur-xl">
        <div className="mx-auto max-w-[1600px] px-4 sm:px-6 lg:px-8">

          {/* First row: logo and account actions */}
          <div className="flex min-h-16 items-center justify-between gap-4">
            <Link
                to="/dashboard"
                onClick={closeMobileMenu}
                className="flex shrink-0 items-center gap-3"
            >
              <div className="rounded-xl bg-gradient-to-br from-purple-600 to-cyan-500 p-2.5 shadow-lg shadow-purple-950/40">
                <Code2 size={23} />
              </div>

              <div>
              <span className="text-xl font-bold tracking-tight">
                Leet<span className="text-cyan-400">Clone</span>
              </span>

                <p className="hidden text-[10px] tracking-[0.2em] text-slate-500 sm:block">
                  CODE. LEARN. GROW.
                </p>
              </div>
            </Link>

            {/* Desktop account actions */}
            <div className="hidden items-center gap-3 md:flex">
              <NotificationButton />

              <Link
                  to="/profile"
                  className="flex max-w-44 items-center gap-2 rounded-xl border border-slate-800 bg-slate-900/70 px-3 py-2 text-sm text-slate-300 transition hover:border-purple-500/40 hover:text-white"
              >
                <User size={17} className="shrink-0 text-cyan-400" />
                <span className="truncate">{username}</span>
              </Link>

              <button
                  type="button"
                  onClick={handleLogout}
                  className="flex items-center gap-2 rounded-xl border border-slate-700 px-3 py-2 text-sm text-slate-300 transition hover:border-red-500/50 hover:bg-red-500/10 hover:text-red-300"
              >
                <LogOut size={17} />
                Logout
              </button>
            </div>

            {/* Mobile actions */}
            <div className="flex shrink-0 items-center gap-2 md:hidden">
              <NotificationButton />

              <button
                  type="button"
                  onClick={() => setMobileMenuOpen((open) => !open)}
                  aria-label={mobileMenuOpen ? 'Close menu' : 'Open menu'}
                  aria-expanded={mobileMenuOpen}
                  className="rounded-xl border border-slate-700 bg-slate-900 p-2.5 text-slate-300 transition hover:text-cyan-300"
              >
                {mobileMenuOpen ? <X size={21} /> : <Menu size={21} />}
              </button>
            </div>
          </div>

          {/* Desktop navigation: separate row with wrapping links */}
          <div className="hidden border-t border-slate-800/70 py-3 md:block">
            <div className="flex flex-wrap items-center gap-2">
              {navLinks.map((link) => (
                  <NavLink
                      key={link.path}
                      to={link.path}
                      end={link.path === '/dashboard'}
                      className={linkClass}
                  >
                    {link.name}
                  </NavLink>
              ))}
            </div>
          </div>

          {/* Mobile navigation */}
          {mobileMenuOpen && (
              <div className="border-t border-slate-800/80 py-4 md:hidden">
                <div className="mb-4 flex items-center gap-3 rounded-xl border border-slate-800 bg-slate-900/80 p-3">
                  <div className="rounded-lg bg-purple-500/15 p-2 text-cyan-300">
                    <User size={19} />
                  </div>

                  <div className="min-w-0">
                    <p className="truncate text-sm font-semibold text-white">
                      {username}
                    </p>

                    <p className="text-xs text-slate-500">
                      {unreadNotifications} unread notifications
                    </p>
                  </div>
                </div>

                <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
                  {navLinks.map((link) => (
                      <NavLink
                          key={link.path}
                          to={link.path}
                          end={link.path === '/dashboard'}
                          onClick={closeMobileMenu}
                          className={linkClass}
                      >
                        {link.name}
                      </NavLink>
                  ))}

                  <Link
                      to="/notifications"
                      onClick={closeMobileMenu}
                      className="flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-400 transition hover:bg-slate-800 hover:text-white"
                  >
                    <Bell size={17} />
                    Notifications

                    {unreadNotifications > 0 && (
                        <span className="rounded-full bg-purple-500 px-2 py-0.5 text-xs font-bold text-white">
                    {unreadNotifications > 99
                        ? '99+'
                        : unreadNotifications}
                  </span>
                    )}
                  </Link>
                </div>

                <button
                    type="button"
                    onClick={handleLogout}
                    className="mt-4 flex w-full items-center justify-center gap-2 rounded-xl border border-red-500/30 px-3 py-3 text-sm text-red-300 transition hover:bg-red-500/10"
                >
                  <LogOut size={17} />
                  Logout
                </button>
              </div>
          )}
        </div>
      </nav>
  )
}