import MainLayout from '../../layouts/MainLayout'
import {
  CalendarDays,
  Flame,
  Clock,
  Trophy,
  ArrowRight
} from 'lucide-react'

function DailyChallenge() {
  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-5xl">

          <div className="mb-8">
            <p className="mb-2 text-sm font-medium text-orange-500">
              DAILY CHALLENGE
            </p>

            <h1 className="text-3xl font-bold text-white">
              Daily Coding Challenge
            </h1>

            <p className="mt-2 text-gray-500">
              Solve one problem every day and build your coding streak.
            </p>
          </div>

          <div className="grid gap-6 lg:grid-cols-3">

            <div className="rounded-xl border border-gray-800 bg-[#181b24] p-6 lg:col-span-2">

              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-2 text-sm font-medium text-orange-500">
                    <CalendarDays size={17} />
                    TODAY'S CHALLENGE
                  </div>

                  <h2 className="mt-4 text-2xl font-bold text-white">
                    Two Sum
                  </h2>

                  <div className="mt-3 flex items-center gap-3">
                    <span className="rounded-full bg-green-500/10 px-3 py-1 text-xs font-medium text-green-400">
                      Easy
                    </span>

                    <span className="text-sm text-gray-500">
                      Arrays · Hash Map
                    </span>
                  </div>
                </div>

                <div className="hidden rounded-xl bg-orange-500/10 p-4 sm:block">
                  <Trophy
                    size={28}
                    className="text-orange-500"
                  />
                </div>
              </div>

              <p className="mt-6 leading-7 text-gray-400">
                Given an array of integers and a target value, find two
                numbers whose sum equals the target and return their indices.
              </p>

              <button className="mt-7 flex items-center gap-2 rounded-lg bg-orange-500 px-5 py-3 font-semibold text-white transition hover:bg-orange-600">
                Solve Challenge
                <ArrowRight size={18} />
              </button>

            </div>

            <div className="rounded-xl border border-gray-800 bg-[#181b24] p-6">

              <div className="flex items-center gap-3">
                <div className="rounded-lg bg-orange-500/10 p-3">
                  <Flame
                    size={22}
                    className="text-orange-500"
                  />
                </div>

                <div>
                  <p className="text-sm text-gray-500">
                    Current Streak
                  </p>

                  <p className="text-2xl font-bold text-white">
                    0 days
                  </p>
                </div>
              </div>

              <div className="mt-7 border-t border-gray-800 pt-5">
                <div className="flex items-center gap-2 text-sm text-gray-400">
                  <Clock size={16} />
                  Next challenge
                </div>

                <p className="mt-2 text-lg font-semibold text-white">
                  Tomorrow
                </p>
              </div>

            </div>

          </div>

          <div className="mt-6 rounded-xl border border-gray-800 bg-[#181b24] p-6">

            <h2 className="text-lg font-semibold text-white">
              Your Daily Progress
            </h2>

            <p className="mt-1 text-sm text-gray-500">
              Complete daily challenges to maintain your streak.
            </p>

            <div className="mt-6 grid grid-cols-7 gap-2">
              {Array.from({ length: 35 }).map((_, index) => (
                <div
                  key={index}
                  className="aspect-square rounded-md bg-gray-800"
                />
              ))}
            </div>

          </div>

        </div>
      </div>
    </MainLayout>
  )
}

export default DailyChallenge