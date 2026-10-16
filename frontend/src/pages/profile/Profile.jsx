import MainLayout from '../../layouts/MainLayout'
import {
  User,
  Mail,
  CalendarDays,
  Trophy,
  Code2,
  Flame,
  Target,
  Edit3
} from 'lucide-react'

function Profile() {
  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-7xl">

          <div className="mb-8">
            <p className="mb-2 text-sm font-medium text-orange-500">
              PROFILE
            </p>

            <h1 className="text-3xl font-bold text-white">
              My Profile
            </h1>

            <p className="mt-2 text-gray-500">
              Track your coding progress and achievements.
            </p>
          </div>

          <div className="grid gap-6 lg:grid-cols-3">

            {/* Profile Card */}
            <div className="rounded-xl border border-gray-800 bg-[#181b24] p-6">

              <div className="flex flex-col items-center text-center">

                <div className="flex h-24 w-24 items-center justify-center rounded-full bg-orange-500">
                  <User
                    size={42}
                    className="text-white"
                  />
                </div>

                <h2 className="mt-5 text-xl font-bold text-white">
                  Rohit
                </h2>

                <p className="mt-1 text-sm text-gray-500">
                  Developer
                </p>

                <button className="mt-5 flex items-center gap-2 rounded-lg border border-gray-700 px-4 py-2 text-sm text-gray-300 hover:bg-gray-800">
                  <Edit3 size={16} />
                  Edit Profile
                </button>

              </div>

              <div className="mt-7 space-y-4 border-t border-gray-800 pt-6">

                <div className="flex items-center gap-3 text-sm">
                  <Mail size={17} className="text-gray-500" />
                  <span className="text-gray-400">
                    Email not connected
                  </span>
                </div>

                <div className="flex items-center gap-3 text-sm">
                  <CalendarDays size={17} className="text-gray-500" />
                  <span className="text-gray-400">
                    Joined recently
                  </span>
                </div>

              </div>

            </div>

            {/* Statistics */}
            <div className="lg:col-span-2">

              <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">

                <StatCard
                  icon={Code2}
                  title="Problems Solved"
                  value="0"
                />

                <StatCard
                  icon={Trophy}
                  title="Global Rank"
                  value="#—"
                />

                <StatCard
                  icon={Flame}
                  title="Current Streak"
                  value="0 days"
                />

                <StatCard
                  icon={Target}
                  title="Accuracy"
                  value="0%"
                />

                <StatCard
                  icon={Trophy}
                  title="Contests"
                  value="0"
                />

                <StatCard
                  icon={Code2}
                  title="Submissions"
                  value="0"
                />

              </div>

              <div className="mt-6 rounded-xl border border-gray-800 bg-[#181b24] p-6">

                <h2 className="text-lg font-semibold text-white">
                  Problem Solving Progress
                </h2>

                <p className="mt-1 text-sm text-gray-500">
                  Your problems by difficulty.
                </p>

                <div className="mt-6 space-y-5">

                  <ProgressRow
                    label="Easy"
                    value="0"
                    total="100"
                    width="0%"
                  />

                  <ProgressRow
                    label="Medium"
                    value="0"
                    total="100"
                    width="0%"
                  />

                  <ProgressRow
                    label="Hard"
                    value="0"
                    total="100"
                    width="0%"
                  />

                </div>

              </div>

            </div>

          </div>

          <div className="mt-6 rounded-xl border border-gray-800 bg-[#181b24] p-6">

            <h2 className="text-lg font-semibold text-white">
              Recent Activity
            </h2>

            <div className="mt-6 flex min-h-32 items-center justify-center">

              <div className="text-center">
                <Code2
                  size={32}
                  className="mx-auto text-gray-700"
                />

                <p className="mt-3 text-sm text-gray-500">
                  Your coding activity will appear here.
                </p>
              </div>

            </div>

          </div>

        </div>
      </div>
    </MainLayout>
  )
}

function StatCard({ icon: Icon, title, value }) {
  return (
    <div className="rounded-xl border border-gray-800 bg-[#181b24] p-5">

      <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-orange-500/10">
        <Icon
          size={20}
          className="text-orange-500"
        />
      </div>

      <p className="mt-4 text-sm text-gray-500">
        {title}
      </p>

      <p className="mt-1 text-xl font-bold text-white">
        {value}
      </p>

    </div>
  )
}

function ProgressRow({ label, value, total, width }) {
  return (
    <div>

      <div className="mb-2 flex justify-between text-sm">
        <span className="text-gray-400">
          {label}
        </span>

        <span className="text-gray-500">
          {value} / {total}
        </span>
      </div>

      <div className="h-2 overflow-hidden rounded-full bg-gray-800">
        <div
          className="h-full rounded-full bg-orange-500"
          style={{ width }}
        />
      </div>

    </div>
  )
}

export default Profile