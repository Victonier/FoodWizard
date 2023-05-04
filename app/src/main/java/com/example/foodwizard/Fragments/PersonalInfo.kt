package com.example.foodwizard.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodwizard.R
import com.example.foodwizard.databinding.FragmentNavigationBinding
import com.example.foodwizard.databinding.FragmentPersonalInfoBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val TAG = "PERSONALINFO"

/**
 * A simple [Fragment] subclass.
 * Use the [PersonalInfo.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalInfo : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("Personal INFO", "We in here");
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)

        binding.changeDietPlanButton.setOnClickListener{
            Log.d(tag, "Clicked the change Diet Plan")
            val fragmentManager = getParentFragmentManager()
            val newFragment = UpdateModal().apply{
                arguments = Bundle().apply {
                    putString("type", "dietPlan")
                }
            }
            newFragment.show(fragmentManager, "changeDiet")
        }
        binding.changePassword.setOnClickListener{
            Log.d(tag, "Clicked the change password")
            val fragmentManager = getParentFragmentManager()
            val newFragment = UpdateModal().apply{
                arguments = Bundle().apply {
                    putString("type", "password")
                }
            }
            newFragment.show(fragmentManager, "password")
        }
        binding.changeEmail.setOnClickListener{
            Log.d(tag, "Clicked the change email")
            val fragmentManager = getParentFragmentManager()
            val newFragment = UpdateModal().apply{
                arguments = Bundle().apply {
                    putString("type", "email")
                }
            }
            newFragment.show(fragmentManager, "email")
        }
        return binding.root
    }


}