package ng.inits.alphamessenger.ui.contact


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ng.inits.alphamessenger.R

/**
 * A simple [Fragment] subclass.
 *
 */
class ContactFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun toString(): String {
        return "Contacts"
    }

    companion object {
        fun newInstance() : ContactFragment {
            return ContactFragment()
        }
    }

}
