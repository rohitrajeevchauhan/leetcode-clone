import MainLayout from '../../layouts/MainLayout'
import {
  Code2,
  Trophy,
  Flame,
  Target,
  ArrowRight,
  CheckCircle2
} from 'lucide-react'

function Dashboard() {
  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">

        <div className="mx-auto max-w-7xl">

          {/* Header */}
          <div className="mb-8">
            <p className="mb-2 text-sm font-medium text-orange-500">
              LEETCLONE
            </p>

            <h1 className="text-3xl font-bold text-white">
              Welcome back 👋
            </h1>

            <p className="mt-2 text-gray-500">
              Keep solving problems and improve your coding skills.
            </p>
          </div>

          {/* Stats */}
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">

            <StatCard
              icon={Code2}
              title="Problems Solved"
              value="0"
              description="Start solving"
            />

            <StatCard
              icon={Trophy}
              title="Global Rank"
              value="#—"
              description="Not ranked yet"
            />

            <StatCard
              icon={Flame}
              title="Current Streak"
              value="0"
              description="Days"
            />

            <StatCard
              icon={Target}
              title="Accuracy"
              value="0%"
              description="Overall"
            />

          </div>

          {/* Main grid */}
          <div className="mt-6 grid gap-6 lg:grid-cols-3">

            {/* Daily challenge */}
            <div className="rounded-xl border border-gray-800 bg-[#181b24] p-6 lg:col-span-2">

              <div className="flex items-center justify-between">

                <div>
                  <p className="text-sm font-medium text-orange-500">
                    DAILY CHALLENGE
                  </p>

                  <h2 className="mt-2 text-2xl font-bold text-white">
                    Start your coding journey
                  </h2>
                </div>

                <div className="hidden rounded-xl bg-orange-500/10 p-4 sm:block">
                  <Code2
                    size={28}
                    className="text-orange-500"
                  />
                </div>

              </div>

              <p className="mt-4 max-w-xl text-gray-500">
                Solve a problem every day to build your streak,
                improve your skills, and prepare for interviews.
              </p>

              <button className="mt-6 flex items-center gap-2 rounded-lg bg-orange-500 px-5 py-3 font-semibold text-white transition hover:bg-orange-600">
                Start Challenge
                <ArrowRight size={18} />
              </button>

            </div>

            {/* Progress */}
            <div className="rounded-xl border border-gray-800 bg-[#181b24] p-6">

              <h2 className="text-lg font-semibold text-white">
                Your Progress
              </h2>

              <p className="mt-1 text-sm text-gray-500">
                Problems completed
              </p>

              <div className="mt-6">

                <div className="mb-2 flex justify-between text-sm">
                  <span className="text-gray-400">
                    Progress
                  </span>

                  <span className="text-white">
                    0 / 100
                  </span>
                </div>

                <div className="h-2 overflow-hidden rounded-full bg-gray-800">
                  <div className="h-full w-0 rounded-full bg-orange-500" />
                </div>

              </div>

              <div className="mt-6 space-y-3">

                <ProgressItem
                  label="Easy"
                  value="0"
                />

                <ProgressItem
                  label="Medium"
                  value="0"
                />

                <ProgressItem
                  label="Hard"
                  value="0"
                />

              </div>

            </div>

          </div>

          {/* Bottom section */}
          <div className="mt-6 grid gap-6 lg:grid-cols-2">

            {/* Recent submissions */}
            <div className="rounded-xl border border-gray-800 bg-[#181b24] p-6">

              <div className="flex items-center justify-between">

                <h2 className="text-lg font-semibold text-white">
                  Recent Submissions
                </h2>

                <span className="text-sm text-orange-500">
                  View all
                </span>

              </div>

              <div className="mt-5 flex min-h-32 flex-col items-center justify-center">

                <CheckCircle2
                  size={30}
                  className="text-gray-700"
                />

                <p className="mt-3 text-sm text-gray-500">
                  No submissions yet
                </p>

              </div>

            </div>

            {/* Activity */}
            <div className="rounded-xl border border-gray-800 bg-[#181b24] p-6">

              <h2 className="text-lg font-semibold text-white">
                Coding Activity
              </h2>

              <p className="mt-1 text-sm text-gray-500">
                Your activity will appear here.
              </p>

              <div className="mt-5 grid grid-cols-7 gap-2">

                {Array.from({ length: 35 }).map((_, index) => (
                  <div
                    key={index}
                    className="aspect-square rounded-sm bg-gray-800"
                  />
                ))}

              </div>

            </div>

          </div>

        </div>

      </div>
    </MainLayout>
  )
}

function StatCard({
  icon: Icon,
  title,
  value,
  description
}) {
  return (
    <div className="rounded-xl border border-gray-800 bg-[#181b24] p-5 transition hover:border-gray-700">

      <div className="flex items-center justify-between">

        <div className="rounded-lg bg-orange-500/10 p-2.5">
          <Icon
            size={20}
            className="text-orange-500"
          />
        </div>

      </div>

      <p className="mt-5 text-sm text-gray-500">
        {title}
      </p>

      <div className="mt-1 flex items-end gap-2">

        <span className="text-2xl font-bold text-white">
          {value}
        </span>

        <span className="mb-1 text-xs text-gray-600">
          {description}
        </span>

      </div>

    </div>
  )
}

function ProgressItem({ label, value }) {
  return (
    <div className="flex items-center justify-between">

      <span className="text-sm text-gray-400">
        {label}
      </span>

      <span className="text-sm font-medium text-white">
        {value}
      </span>

    </div>
  )
}

export default Dashboard