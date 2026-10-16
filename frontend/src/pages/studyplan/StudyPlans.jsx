import MainLayout from '../../layouts/MainLayout'
import {
  BookOpen,
  Clock,
  CheckCircle2,
  ArrowRight,
  Target
} from 'lucide-react'

function StudyPlans() {
  const plans = [
    {
      title: 'DSA Interview Preparation',
      description: 'Master arrays, linked lists, trees, graphs and dynamic programming.',
      duration: '12 weeks',
      problems: 150,
      progress: 0
    },
    {
      title: 'Java Programming',
      description: 'Strengthen your Java fundamentals and object-oriented programming.',
      duration: '8 weeks',
      problems: 80,
      progress: 0
    },
    {
      title: 'Competitive Programming',
      description: 'Improve algorithms, problem solving and contest performance.',
      duration: '16 weeks',
      problems: 200,
      progress: 0
    }
  ]

  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-7xl">

          <div className="mb-8">
            <p className="mb-2 text-sm font-medium text-orange-500">
              STUDY PLANS
            </p>

            <h1 className="text-3xl font-bold text-white">
              Study Plans
            </h1>

            <p className="mt-2 text-gray-500">
              Follow structured learning paths and prepare systematically.
            </p>
          </div>

          <div className="mb-6 rounded-xl border border-orange-500/20 bg-orange-500/5 p-6">
            <div className="flex flex-col gap-5 md:flex-row md:items-center md:justify-between">

              <div className="flex items-start gap-4">
                <div className="rounded-xl bg-orange-500/10 p-3">
                  <Target
                    size={24}
                    className="text-orange-500"
                  />
                </div>

                <div>
                  <h2 className="text-lg font-semibold text-white">
                    Build your coding roadmap
                  </h2>

                  <p className="mt-1 text-sm text-gray-500">
                    Choose a plan and work through problems step by step.
                  </p>
                </div>
              </div>

              <button className="rounded-lg bg-orange-500 px-5 py-3 text-sm font-semibold text-white hover:bg-orange-600">
                Explore Plans
              </button>

            </div>
          </div>

          <div className="grid gap-5 lg:grid-cols-3">

            {plans.map((plan) => (
              <div
                key={plan.title}
                className="rounded-xl border border-gray-800 bg-[#181b24] p-6 transition hover:border-gray-700"
              >

                <div className="flex items-start justify-between">

                  <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-orange-500/10">
                    <BookOpen
                      size={23}
                      className="text-orange-500"
                    />
                  </div>

                  <span className="rounded-full bg-gray-800 px-3 py-1 text-xs text-gray-400">
                    Beginner
                  </span>

                </div>

                <h2 className="mt-5 text-xl font-semibold text-white">
                  {plan.title}
                </h2>

                <p className="mt-3 min-h-12 text-sm leading-6 text-gray-500">
                  {plan.description}
                </p>

                <div className="mt-5 flex items-center gap-5 text-xs text-gray-500">

                  <span className="flex items-center gap-1.5">
                    <Clock size={15} />
                    {plan.duration}
                  </span>

                  <span className="flex items-center gap-1.5">
                    <CheckCircle2 size={15} />
                    {plan.problems} problems
                  </span>

                </div>

                <div className="mt-6">

                  <div className="mb-2 flex justify-between text-xs">
                    <span className="text-gray-500">
                      Progress
                    </span>

                    <span className="text-gray-400">
                      {plan.progress}%
                    </span>
                  </div>

                  <div className="h-2 overflow-hidden rounded-full bg-gray-800">
                    <div
                      className="h-full rounded-full bg-orange-500"
                      style={{
                        width: `${plan.progress}%`
                      }}
                    />
                  </div>

                </div>

                <button className="mt-6 flex w-full items-center justify-center gap-2 rounded-lg border border-gray-700 py-3 text-sm font-medium text-gray-300 hover:bg-gray-800 hover:text-white">
                  Start Plan
                  <ArrowRight size={16} />
                </button>

              </div>
            ))}

          </div>

        </div>
      </div>
    </MainLayout>
  )
}

export default StudyPlans