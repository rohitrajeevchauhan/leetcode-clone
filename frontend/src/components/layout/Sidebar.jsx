import {
  LayoutDashboard,
  Code2,
  Send,
  MessageSquare,
  Users,
  BookOpen,
  CalendarDays,
  User,
  Trophy,
  X
} from 'lucide-react'

import { NavLink } from 'react-router-dom'

function Sidebar({ mobileOpen, setMobileOpen }) {

  const menuItems = [
    {
      label: 'Dashboard',
      path: '/dashboard',
      icon: LayoutDashboard
    },
    {
      label: 'Problems',
      path: '/problems',
      icon: Code2
    },
    {
      label: 'Submissions',
      path: '/submissions',
      icon: Send
    },
    {
      label: 'Discussions',
      path: '/discussions',
      icon: MessageSquare
    },
    {
      label: 'Community',
      path: '/community',
      icon: Users
    },
    {
      label: 'Study Plans',
      path: '/study-plans',
      icon: BookOpen
    },
    {
      label: 'Daily Challenge',
      path: '/daily-challenge',
      icon: CalendarDays
    },
    {
      label: 'Profile',
      path: '/profile',
      icon: User
    }
  ]

  return (
    <>
      {mobileOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/60 lg:hidden"
          onClick={() => setMobileOpen(false)}
        />
      )}

      <aside
        className={`
          fixed left-0 top-16 z-40
          h-[calc(100vh-4rem)]
          w-64
          border-r border-gray-800
          bg-[#0f1117]
          transition-transform duration-300
          lg:translate-x-0
          ${mobileOpen ? 'translate-x-0' : '-translate-x-full'}
        `}
      >

        <div className="flex items-center justify-between border-b border-gray-800 px-4 py-4 lg:hidden">
          <span className="font-semibold text-white">
            Menu
          </span>

          <button
            onClick={() => setMobileOpen(false)}
            className="text-gray-400 hover:text-white"
          >
            <X size={20} />
          </button>
        </div>

        <nav className="space-y-1 p-3">

          {menuItems.map((item) => {
            const Icon = item.icon

            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={() => setMobileOpen(false)}
                className={({ isActive }) =>
                  `
                  flex items-center gap-3 rounded-lg px-3 py-3
                  text-sm font-medium transition
                  ${
                    isActive
                      ? 'bg-orange-500/10 text-orange-500'
                      : 'text-gray-400 hover:bg-gray-800 hover:text-white'
                  }
                  `
                }
              >
                <Icon size={19} />
                <span>{item.label}</span>
              </NavLink>
            )
          })}

        </nav>

        <div className="mx-3 mt-5 rounded-xl border border-gray-800 bg-[#181b24] p-4">

          <div className="mb-3 flex items-center gap-2">
            <Trophy size={17} className="text-orange-500" />

            <span className="text-sm font-semibold text-white">
              Your Progress
            </span>
          </div>

          <div className="mb-2 flex justify-between text-xs">
            <span className="text-gray-500">
              Problems solved
            </span>

            <span className="text-gray-300">
              0 / 100
            </span>
          </div>

          <div className="h-2 overflow-hidden rounded-full bg-gray-800">
            <div className="h-full w-0 rounded-full bg-orange-500" />
          </div>

          <p className="mt-3 text-xs text-gray-500">
            Start solving problems to track your progress.
          </p>

        </div>

      </aside>
    </>
  )
}

export default Sidebar