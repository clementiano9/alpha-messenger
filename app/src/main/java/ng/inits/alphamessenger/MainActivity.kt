package ng.inits.alphamessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import ng.inits.alphamessenger.ui.LoginActivity
import ng.inits.alphamessenger.ui.chat.ChatsFragment
import ng.inits.alphamessenger.ui.contact.ContactFragment
import ng.inits.alphamessenger.ui.search_contact.SearchContactActivity

class MainActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private val fragments = arrayListOf(ChatsFragment.newInstance(), ContactFragment.newInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the _adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, fragments)

        // Set up the ViewPager with the sections _adapter.
        container.adapter = mSectionsPagerAdapter
        tab_layout.setupWithViewPager(container)
        tab_layout.tabMode = TabLayout.MODE_FIXED

        add_contact_btn.setOnClickListener {
            startActivity(Intent(this, SearchContactActivity::class.java))
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager, private val fragments: List<Fragment>) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int): CharSequence = fragments[position].toString()
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            rootView.section_label.text = getString(R.string.section_format)
            return rootView
        }

        override fun toString(): String = "Placeholder"

        companion object {
            fun newInstance(): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                //val args = Bundle()
                //args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                //fragment.arguments = args
                return fragment
            }
        }
    }
}
