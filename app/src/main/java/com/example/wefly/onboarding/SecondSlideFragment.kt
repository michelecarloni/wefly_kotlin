package com.example.wefly.onboarding

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wefly.R
import com.example.wefly.activity.RegisterActivity

class SecondSlideFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_second_slide, container, false)
        val finish=view.findViewById<TextView>(R.id.finito)
        finish.setOnClickListener{
            val intent = Intent(requireActivity(), RegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            onBoardingFinished()
        }
        return view
    }

    private fun onBoardingFinished(){ //memorizza il fatto che abbiamo gia visto l'onboarding
        val sharedPref=requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor=sharedPref.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }

}