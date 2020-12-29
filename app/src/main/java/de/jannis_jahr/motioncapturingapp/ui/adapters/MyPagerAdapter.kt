package de.jannis_jahr.motioncapturingapp.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.jannis_jahr.motioncapturingapp.ui.jobs.FailedJobsFragment
import de.jannis_jahr.motioncapturingapp.ui.jobs.FinishedJobsFragment
import de.jannis_jahr.motioncapturingapp.ui.jobs.JobsListFragment
import de.jannis_jahr.motioncapturingapp.ui.jobs.PendingJobsFragment

/**
 * @deprecated
 * This was used in an older version
 */
class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    val finished = FinishedJobsFragment()
    val pending = PendingJobsFragment()
    val failed = FailedJobsFragment()

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> finished
            1 -> pending
            else -> failed
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Finished"
            1 -> "Pending"
            else -> "Failed"
        }
    }


}