package com.example.wefly.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.wefly.R
import com.example.wefly.RegisterActivity

class FirstSlideFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_first_slide, container, false)
        val viewPager=activity?.findViewById<ViewPager2>(R.id.viewPager)
        val next =view.findViewById<TextView>(R.id.avanti)
        val skip =view.findViewById<TextView>(R.id.salta)
        next.setOnClickListener{
            viewPager?.currentItem=1
        }
        skip.setOnClickListener{
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        return view
    }

}