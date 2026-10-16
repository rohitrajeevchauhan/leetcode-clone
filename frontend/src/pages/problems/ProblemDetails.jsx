import MainLayout from '../../layouts/MainLayout'
import {
  ArrowLeft,
  CheckCircle2,
  Play,
  Send,
  Code2
} from 'lucide-react'
import { Link, useParams } from 'react-router-dom'

function ProblemDetails() {
  const { problemId } = useParams()

  return (
    <MainLayout>
      <div className="min-h-screen bg-[#0f1117] p-6">
        <div className="mx-auto max-w-7xl">

          <Link
            to="/problems"
            className="mb-6 flex w-fit items-center gap-2 text-sm text-gray-500 hover:text-white"
          >
            <ArrowLeft size={17} />
            Back to Problems
          </Link>

          <div className="grid gap-6 lg:grid-cols-2">

            {/* Problem */}
            <div className="rounded-xl border border-gray-800 bg-[#181b24]">

              <div className="border-b border-gray-800 p-6">
                <div className="flex items-start justify-between gap-4">

                  <div>
                    <p className="text-sm text-gray-500">
                      Problem #{problemId || '1'}
                    </p>

                    <h1 className="mt-2 text-2xl font-bold text-white">
                      Two Sum
                    </h1>

                    <div className="mt-3 flex flex-wrap gap-2">
                      <span className="rounded-full bg-green-500/10 px-3 py-1 text-xs font-medium text-green-400">
                        Easy
                      </span>

                      <span className="rounded-full bg-gray-800 px-3 py-1 text-xs text-gray-400">
                        Array
                      </span>

                      <span className="rounded-full bg-gray-800 px-3 py-1 text-xs text-gray-400">
                        Hash Map
                      </span>
                    </div>
                  </div>

                  <div className="rounded-lg bg-orange-500/10 p-3">
                    <Code2
                      size={24}
                      className="text-orange-500"
                    />
                  </div>

                </div>
              </div>

              <div className="p-6">

                <h2 className="mb-3 text-lg font-semibold text-white">
                  Description
                </h2>

                <p className="leading-7 text-gray-400">
                  Given an array of integers nums and an integer target,
                  return indices of the two numbers such that they add up
                  to target.
                </p>

                <p className="mt-4 leading-7 text-gray-400">
                  You may assume that each input would have exactly one
                  solution, and you may not use the same element twice.
                </p>

                <h2 className="mt-7 text-lg font-semibold text-white">
                  Example
                </h2>

                <div className="mt-3 rounded-lg border border-gray-800 bg-[#0f1117] p-4 font-mono text-sm">
                  <p className="text-gray-400">
                    Input:
                  </p>

                  <p className="mt-1 text-white">
                    nums = [2,7,11,15], target = 9
                  </p>

                  <p className="mt-4 text-gray-400">
                    Output:
                  </p>

                  <p className="mt-1 text-orange-400">
                    [0,1]
                  </p>
                </div>

                <h2 className="mt-7 text-lg font-semibold text-white">
                  Constraints
                </h2>

                <ul className="mt-3 list-disc space-y-2 pl-5 text-sm text-gray-400">
                  <li>2 ≤ nums.length ≤ 10⁴</li>
                  <li>-10⁹ ≤ nums[i] ≤ 10⁹</li>
                  <li>-10⁹ ≤ target ≤ 10⁹</li>
                </ul>

              </div>
            </div>

            {/* Editor */}
            <div className="flex flex-col rounded-xl border border-gray-800 bg-[#181b24]">

              <div className="flex items-center justify-between border-b border-gray-800 px-4 py-3">

                <select className="rounded-lg border border-gray-700 bg-[#0f1117] px-3 py-2 text-sm text-gray-300 outline-none">
                  <option>Java</option>
                  <option>Python</option>
                  <option>C++</option>
                  <option>JavaScript</option>
                </select>

                <div className="flex gap-2">

                  <button className="flex items-center gap-2 rounded-lg border border-gray-700 px-3 py-2 text-sm text-gray-300 hover:bg-gray-800">
                    <Play size={16} />
                    Run
                  </button>

                  <button className="flex items-center gap-2 rounded-lg bg-orange-500 px-3 py-2 text-sm font-semibold text-white hover:bg-orange-600">
                    <Send size={16} />
                    Submit
                  </button>

                </div>
              </div>

              <textarea
                defaultValue={`import java.util.*;

class Solution {
    public int[] twoSum(int[] nums, int target) {

        // Write your solution here

        return new int[]{};
    }
}`}
                spellCheck="false"
                className="min-h-[500px] flex-1 resize-none bg-[#0f1117] p-5 font-mono text-sm leading-6 text-gray-200 outline-none"
              />

              <div className="border-t border-gray-800 p-4">

                <div className="flex items-center gap-2 text-sm text-gray-500">
                  <CheckCircle2 size={16} />
                  Test cases
                </div>

                <div className="mt-3 rounded-lg border border-gray-800 bg-[#0f1117] p-3 font-mono text-xs text-gray-400">
                  [2,7,11,15]
                  <span className="mx-2 text-gray-600">→</span>
                  9
                </div>

              </div>

            </div>

          </div>

        </div>
      </div>
    </MainLayout>
  )
}

export default ProblemDetails